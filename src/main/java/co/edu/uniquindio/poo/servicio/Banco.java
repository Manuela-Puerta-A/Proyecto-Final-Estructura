package co.edu.uniquindio.poo.servicio;

import co.edu.uniquindio.poo.estructura.ArbolAVL;
import co.edu.uniquindio.poo.estructura.ArbolBinario;
import co.edu.uniquindio.poo.estructura.ColaPrioridad;
import co.edu.uniquindio.poo.estructura.Grafo;
import co.edu.uniquindio.poo.estructura.ListaEnlazada;
import co.edu.uniquindio.poo.modelo.Cliente;
import co.edu.uniquindio.poo.modelo.MovimientoPuntos;
import co.edu.uniquindio.poo.modelo.Programada;
import co.edu.uniquindio.poo.modelo.RegistroPuntos;
import co.edu.uniquindio.poo.modelo.Transaccion;
import co.edu.uniquindio.poo.nodo.Nodo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service
public class Banco {

    public static final String[] CATEGORIAS = { "Comida", "Transporte", "Servicios", "Entretenimiento", "Otros" };

    private HashMap<String, Cliente> clientes;
    private HashMap<String, Integer> tablaPuntos;
    private ArbolBinario<RegistroPuntos> arbolPuntos;
    private ArbolAVL<RegistroPuntos> arbolRangos;
    private ColaPrioridad<Programada> programadas;
    private Grafo grafoTransferencias;
    private Grafo grafoGastos;
    private Grafo grafoMonederos;
    private int dia;

    public Banco() {
        clientes = new HashMap<>();
        tablaPuntos = new HashMap<>();
        arbolPuntos = new ArbolBinario<>();
        arbolRangos = new ArbolAVL<>();
        programadas = new ColaPrioridad<>();
        grafoTransferencias = new Grafo();
        grafoGastos = new Grafo();
        grafoMonederos = new Grafo();
        dia = 1;
        cargarDatosDePrueba();
    }

    public void registrarCliente(String id, String nombre) {
        if (id.isBlank() || nombre.isBlank()) {
            throw new RuntimeException("El id y el nombre son obligatorios");
        }
        if (clientes.containsKey(id)) {
            throw new RuntimeException("Ya existe un cliente con id " + id);
        }
        Cliente cliente = new Cliente(id, nombre);
        clientes.put(id, cliente);
        tablaPuntos.put(id, 0);
        RegistroPuntos registro = new RegistroPuntos(id, nombre, 0);
        arbolPuntos.insertar(registro);
        arbolRangos.insertar(registro);
        grafoTransferencias.agregarVertice(id);
        grafoGastos.agregarVertice(id);
        grafoMonederos.agregarVertice(id + "-Principal");
        grafoMonederos.agregarVertice(id + "-Ahorros");
    }

    public Cliente buscarCliente(String id) {
        Cliente cliente = clientes.get(id);
        if (cliente == null) {
            throw new RuntimeException("No existe el cliente " + id);
        }
        return cliente;
    }

    public Collection<Cliente> getClientes() {
        return clientes.values();
    }

    public int getDia() {
        return dia;
    }

    public String[] getCategorias() {
        return CATEGORIAS;
    }

    public void depositar(String id, double monto) {
        validarMonto(monto);
        Cliente cliente = buscarCliente(id);
        cliente.setSaldo(cliente.getSaldo() + monto);
        int puntos = calcularPuntos("DEPOSITO", monto);
        registrar(cliente, new Transaccion("DEPOSITO", id, null, monto, 0, puntos, dia));
    }

    public void retirar(String id, double monto) {
        validarMonto(monto);
        Cliente cliente = buscarCliente(id);
        double cargo = monto * 0.004;
        cargo = Math.round(cargo * 100) / 100.0;
        if (dia <= cliente.getRetirosGratisHastaDia()) {
            cargo = 0;
        }
        if (cliente.getSaldo() < monto + cargo) {
            throw new RuntimeException("Saldo insuficiente. Tienes $" + dinero(cliente.getSaldo())
                    + " y necesitas $" + dinero(monto + cargo));
        }
        cliente.setSaldo(cliente.getSaldo() - monto - cargo);
        int puntos = calcularPuntos("RETIRO", monto);
        registrar(cliente, new Transaccion("RETIRO", id, null, monto, cargo, puntos, dia));
        alertaSaldoBajo(cliente);
    }

    public void transferir(String idOrigen, String idDestino, double monto) {
        validarMonto(monto);
        if (idOrigen.equals(idDestino)) {
            throw new RuntimeException("No te puedes transferir a ti mismo");
        }
        Cliente origen = buscarCliente(idOrigen);
        Cliente destino = buscarCliente(idDestino);
        double comision = calcularComision(origen, monto);
        if (origen.getSaldo() < monto + comision) {
            throw new RuntimeException(
                    "Saldo insuficiente para transferir $" + dinero(monto) + " + comision $" + dinero(comision));
        }
        if (origen.getTransferenciasConDescuento() > 0) {
            origen.setTransferenciasConDescuento(origen.getTransferenciasConDescuento() - 1);
        }
        origen.setSaldo(origen.getSaldo() - monto - comision);
        destino.setSaldo(destino.getSaldo() + monto);
        int puntos = calcularPuntos("TRANSFERENCIA", monto);
        Transaccion t = new Transaccion("TRANSFERENCIA", idOrigen, idDestino, monto, comision, puntos, dia);
        registrar(origen, t);
        destino.getHistorial().agregarFinal(t);
        destino.notificar(dia, "Recibiste $" + dinero(monto) + " de " + origen.getNombre());
        grafoTransferencias.agregarArista(idOrigen, idDestino, monto);
        grafoMonederos.agregarArista(idOrigen + "-Principal", idDestino + "-Principal", monto);
        alertaSaldoBajo(origen);
    }

    public void gastar(String id, String categoria, double monto) {
        validarMonto(monto);
        Cliente cliente = buscarCliente(id);
        if (!esCategoria(categoria)) {
            throw new RuntimeException("Categoria no valida");
        }
        if (cliente.getSaldo() < monto) {
            throw new RuntimeException("Saldo insuficiente. Tienes $" + dinero(cliente.getSaldo()));
        }
        cliente.setSaldo(cliente.getSaldo() - monto);
        registrar(cliente, new Transaccion("GASTO", id, categoria, monto, 0, 0, dia));

        grafoGastos.agregarArista(id, categoria, monto);
        if (cliente.getUltimaCategoria() != null) {
            grafoGastos.agregarArista(cliente.getUltimaCategoria(), categoria, 1);
        }
        cliente.setUltimaCategoria(categoria);
        alertaSaldoBajo(cliente);
    }

    public void moverDinero(String id, String hacia, double monto) {
        validarMonto(monto);
        Cliente cliente = buscarCliente(id);
        moverSinRegistrar(cliente, hacia, monto, "No tienes ese dinero en el monedero de origen");
        registrar(cliente, new Transaccion("MOVIMIENTO", id, hacia, monto, 0, 0, dia));
    }

    private void moverSinRegistrar(Cliente cliente, String hacia, double monto, String error) {
        String id = cliente.getId();
        if (hacia.equals("Ahorros")) {
            if (cliente.getSaldo() < monto) {
                throw new RuntimeException(error);
            }
            cliente.setSaldo(cliente.getSaldo() - monto);
            cliente.setAhorros(cliente.getAhorros() + monto);
            grafoMonederos.agregarArista(id + "-Principal", id + "-Ahorros", monto);
        } else {
            if (cliente.getAhorros() < monto) {
                throw new RuntimeException(error);
            }
            cliente.setAhorros(cliente.getAhorros() - monto);
            cliente.setSaldo(cliente.getSaldo() + monto);
            grafoMonederos.agregarArista(id + "-Ahorros", id + "-Principal", monto);
        }
    }

    public int calcularPuntos(String tipo, double monto) {
        int cientos = (int) (monto / 100);
        if (tipo.equals("DEPOSITO")) {
            return cientos * 1;
        } else if (tipo.equals("RETIRO")) {
            return cientos * 2;
        } else if (tipo.equals("TRANSFERENCIA")) {
            return cientos * 3;
        }
        return 0;
    }

    public double calcularComision(Cliente cliente, double monto) {
        double comision = monto * 0.01;
        String rango = cliente.getRango();
        if (rango.equals("Plata")) {
            comision = comision * 0.90;
        } else if (rango.equals("Oro")) {
            comision = comision * 0.75;
        } else if (rango.equals("Platino")) {
            comision = comision * 0.50;
        }
        if (cliente.getTransferenciasConDescuento() > 0) {
            comision = comision * 0.90;
        }
        return Math.round(comision * 100) / 100.0;
    }

    private void cambiarPuntos(Cliente cliente, int cantidad, String descripcion) {
        if (cantidad == 0) {
            return;
        }
        String rangoAntes = cliente.getRango();
        RegistroPuntos viejo = new RegistroPuntos(cliente.getId(), cliente.getNombre(), cliente.getPuntos());
        arbolPuntos.eliminar(viejo);
        arbolRangos.eliminar(viejo);

        cliente.setPuntos(cliente.getPuntos() + cantidad);

        RegistroPuntos nuevo = new RegistroPuntos(cliente.getId(), cliente.getNombre(), cliente.getPuntos());
        arbolPuntos.insertar(nuevo);
        arbolRangos.insertar(nuevo);
        tablaPuntos.put(cliente.getId(), cliente.getPuntos());
        cliente.getHistorialPuntos().agregarFinal(new MovimientoPuntos(dia, descripcion, cantidad));

        if (!rangoAntes.equals(cliente.getRango())) {
            cliente.notificar(dia, "Tu rango cambio: ahora eres " + cliente.getRango());
        }
    }

    public int consultarPuntos(String id) {
        Integer puntos = tablaPuntos.get(id);
        if (puntos == null) {
            throw new RuntimeException("No existe el cliente " + id);
        }
        return puntos;
    }

    public void canjear(String id, int costo) {
        Cliente cliente = buscarCliente(id);
        if (cliente.getPuntosDisponibles() < costo) {
            throw new RuntimeException("No tienes puntos suficientes. Tienes " + cliente.getPuntosDisponibles());
        }
        String beneficio;
        if (costo == 100) {
            cliente.setTransferenciasConDescuento(cliente.getTransferenciasConDescuento() + 5);
            beneficio = "10% menos de comision en tus proximas 5 transferencias";
        } else if (costo == 500) {
            cliente.setRetirosGratisHastaDia(dia + 30);
            beneficio = "retiros sin cargo hasta el dia " + (dia + 30);
        } else if (costo == 1000) {
            cliente.setSaldo(cliente.getSaldo() + 50);
            cliente.getHistorial().agregarFinal(new Transaccion("BONO", id, null, 50, 0, 0, dia));
            beneficio = "bono de $50 en el monedero principal";
        } else {
            throw new RuntimeException("Beneficio no valido");
        }

        cliente.setPuntosCanjeados(cliente.getPuntosCanjeados() + costo);
        cliente.getHistorialPuntos().agregarFinal(new MovimientoPuntos(dia, "Canje: " + beneficio, -costo));
        cliente.notificar(dia, "Canjeaste " + costo + " puntos: " + beneficio);
    }

    public ListaEnlazada<RegistroPuntos> ranking() {
        return arbolPuntos.inordenInverso();
    }

    public ListaEnlazada<RegistroPuntos> clientesDelRango(String rango) {
        int desde = 0;
        int hasta = 501;
        if (rango.equals("Plata")) {
            desde = 501;
            hasta = 1001;
        } else if (rango.equals("Oro")) {
            desde = 1001;
            hasta = 5001;
        } else if (rango.equals("Platino")) {
            desde = 5001;
            hasta = Integer.MAX_VALUE;
        }

        return arbolRangos.buscarEntre(new RegistroPuntos("", "", desde), new RegistroPuntos("", "", hasta));
    }

    public int getAlturaABB() {
        return arbolPuntos.calcularAltura();
    }

    public int getAlturaAVL() {
        return arbolRangos.calcularAltura();
    }

    public void revertir(String id) {
        Cliente cliente = buscarCliente(id);
        if (cliente.getReversibles().isEmpty()) {
            throw new RuntimeException("No hay transacciones para revertir");
        }
        Transaccion t = cliente.getReversibles().peek(); // primero se mira, se saca solo si se puede
        if (cliente.getPuntosDisponibles() < t.getPuntos()) {
            throw new RuntimeException("No se puede revertir: ya canjeaste esos puntos");
        }

        if (t.getTipo().equals("DEPOSITO")) {
            if (cliente.getSaldo() < t.getMonto()) {
                throw new RuntimeException("No se puede revertir: ya gastaste ese dinero");
            }
            cliente.setSaldo(cliente.getSaldo() - t.getMonto());
        } else if (t.getTipo().equals("RETIRO")) {
            cliente.setSaldo(cliente.getSaldo() + t.getMonto() + t.getComision());
        } else if (t.getTipo().equals("TRANSFERENCIA")) {
            Cliente destino = buscarCliente(t.getDestino());
            if (destino.getSaldo() < t.getMonto()) {
                throw new RuntimeException("No se puede revertir: el destino ya gasto ese dinero");
            }
            destino.setSaldo(destino.getSaldo() - t.getMonto());
            cliente.setSaldo(cliente.getSaldo() + t.getMonto() + t.getComision());
            grafoTransferencias.restarArista(id, t.getDestino(), t.getMonto());
            grafoMonederos.restarArista(id + "-Principal", t.getDestino() + "-Principal", t.getMonto());
            destino.notificar(dia, cliente.getNombre() + " revirtio una transferencia de $" + dinero(t.getMonto()));
        } else if (t.getTipo().equals("GASTO")) {
            cliente.setSaldo(cliente.getSaldo() + t.getMonto());
            grafoGastos.restarArista(id, t.getDestino(), t.getMonto());
        } else if (t.getTipo().equals("MOVIMIENTO")) {

            String deVuelta = t.getDestino().equals("Ahorros") ? "Principal" : "Ahorros";
            moverSinRegistrar(cliente, deVuelta, t.getMonto(), "No se puede revertir: ya usaste ese dinero");
            String desde = t.getDestino().equals("Ahorros") ? "-Principal" : "-Ahorros";
            String hacia = t.getDestino().equals("Ahorros") ? "-Ahorros" : "-Principal";
            grafoMonederos.restarArista(id + desde, id + hacia, t.getMonto());
            grafoMonederos.restarArista(id + hacia, id + desde, t.getMonto());
        }

        cliente.getReversibles().pop();
        t.setRevertida(true);

        cambiarPuntos(cliente, -t.getPuntos(), "Se revirtio la transaccion #" + t.getId());
        cliente.notificar(dia, "Se revirtio la transaccion #" + t.getId() + " (" + t.getTipo() + ")");
    }

    public void programar(String tipo, String id, String destino, double monto, int enDias, int cadaDias) {
        validarMonto(monto);
        buscarCliente(id);
        if (tipo.equals("TRANSFERENCIA")) {
            buscarCliente(destino);
        }
        if (enDias < 1) {
            throw new RuntimeException("Debe ser para dentro de 1 dia o mas");
        }
        programadas.encolar(new Programada(tipo, id, destino, monto, dia + enDias, cadaDias));
    }

    public ListaEnlazada<Programada> getProgramadas() {
        return programadas.listar();
    }

    public void avanzarDia() {
        dia++;
        while (!programadas.isEmpty() && programadas.peek().getDia() <= dia) {
            Programada p = programadas.desencolar();
            Cliente cliente = buscarCliente(p.getClienteId());
            try {
                if (p.getTipo().equals("DEPOSITO")) {
                    depositar(p.getClienteId(), p.getMonto());
                } else if (p.getTipo().equals("RETIRO")) {
                    retirar(p.getClienteId(), p.getMonto());
                } else {
                    transferir(p.getClienteId(), p.getDestinoId(), p.getMonto());
                }
                cliente.notificar(dia, "Se ejecuto tu " + p.getTipo() + " programado de $" + dinero(p.getMonto()));
            } catch (RuntimeException e) {
                cliente.notificar(dia, "No se pudo ejecutar tu " + p.getTipo() + " programado: " + e.getMessage());
            }
            if (p.getCadaDias() > 0) {
                p.setDia(dia + p.getCadaDias());
                programadas.encolar(p);
            }
        }
        for (Programada p : programadas.listar()) {
            if (p.getDia() == dia + 1) {
                buscarCliente(p.getClienteId()).notificar(dia, "Recordatorio: manana se ejecuta tu "
                        + p.getTipo() + " de $" + dinero(p.getMonto()));
            }
        }
    }

    public ListaEnlazada<String> leerNotificaciones(String id) {
        return buscarCliente(id).leerNotificaciones();
    }

    public HashMap<String, Double> transferenciasDe(String id) {
        return grafoTransferencias.getVecinos(id);
    }

    public ListaEnlazada<String> redDeTransferencias(String id) {
        return grafoTransferencias.recorridoAnchura(id);
    }

    public HashMap<String, Double> gastosPorCategoria(String id) {
        return grafoGastos.getVecinos(id);
    }

    public String categoriaPrincipal(String id) {
        String mayor = "Ninguna";
        double montoMayor = 0;
        for (String categoria : gastosPorCategoria(id).keySet()) {
            double monto = gastosPorCategoria(id).get(categoria);
            if (monto > montoMayor) {
                montoMayor = monto;
                mayor = categoria;
            }
        }
        return mayor;
    }

    public HashMap<String, Double> gastoTotalPorCategoria() {
        HashMap<String, Double> totales = new HashMap<>();
        for (String id : clientes.keySet()) {
            HashMap<String, Double> gastos = gastosPorCategoria(id);
            for (String categoria : gastos.keySet()) {
                totales.put(categoria, totales.getOrDefault(categoria, 0.0) + gastos.get(categoria));
            }
        }
        return totales;
    }

    public ListaEnlazada<String> patronesDeGasto() {
        ListaEnlazada<String> patrones = new ListaEnlazada<>();
        for (String categoria : CATEGORIAS) {
            HashMap<String, Double> siguientes = grafoGastos.getVecinos(categoria);
            for (String siguiente : siguientes.keySet()) {
                int veces = (int) Math.round(siguientes.get(siguiente));
                patrones.agregarFinal("Despues de " + categoria + " gastan en " + siguiente + " (" + veces + " veces)");
            }
        }
        return patrones;
    }

    public ListaEnlazada<String> movimientosDeMonederos(String id) {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        String[] monederos = { id + "-Principal", id + "-Ahorros" };
        for (String monedero : monederos) {
            HashMap<String, Double> destinos = grafoMonederos.getVecinos(monedero);
            for (String destino : destinos.keySet()) {
                lista.agregarFinal(monedero + " -> " + destino + ": $" + dinero(destinos.get(destino)));
            }
        }
        return lista;
    }

    public ListaEnlazada<String> verificarIntegridad(String id) {
        ListaEnlazada<String> resultado = new ListaEnlazada<>();
        Cliente cliente = buscarCliente(id);

        double saldoCalculado = sumarSaldo(cliente.getHistorial().getPrimero(), id);
        double saldoReal = cliente.getSaldo() + cliente.getAhorros();
        if (Math.abs(saldoCalculado - saldoReal) > 0.01) {
            resultado.agregarFinal("ERROR: el historial da $" + dinero(saldoCalculado) + " pero el cliente tiene $"
                    + dinero(saldoReal));
        } else {
            resultado.agregarFinal("OK: el saldo total ($" + dinero(saldoReal) + ") coincide con el historial");
        }

        int puntosCalculados = sumarPuntos(cliente.getHistorial().getPrimero(), id);
        if (puntosCalculados != cliente.getPuntos() || puntosCalculados != consultarPuntos(id)) {
            resultado.agregarFinal(
                    "ERROR: el historial da " + puntosCalculados + " puntos pero hay " + cliente.getPuntos());
        } else {
            resultado.agregarFinal(
                    "OK: los puntos (" + puntosCalculados + ") coinciden con el historial y la tabla hash");
        }

        int disponibles = sumarMovimientos(cliente.getHistorialPuntos().getPrimero());
        if (disponibles != cliente.getPuntosDisponibles()) {
            resultado.agregarFinal("ERROR: el historial de puntos da " + disponibles + " disponibles");
        } else {
            resultado.agregarFinal("OK: el historial de puntos da los " + disponibles + " puntos disponibles");
        }

        if (arbolPuntos.esValido() && arbolRangos.estaBalanceado()) {
            resultado.agregarFinal("OK: el ABB esta ordenado y el AVL esta balanceado");
        } else {
            resultado.agregarFinal("ERROR: los arboles de puntos estan mal");
        }
        return resultado;
    }

    private double sumarSaldo(Nodo<Transaccion> nodo, String id) {
        if (nodo == null) {
            return 0;
        }
        Transaccion t = nodo.getDato();
        double efecto = 0;
        if (!t.isRevertida()) {
            if (t.getTipo().equals("DEPOSITO") || t.getTipo().equals("BONO")) {
                efecto = t.getMonto();
            } else if (t.getTipo().equals("RETIRO") || t.getTipo().equals("GASTO")) {
                efecto = -t.getMonto() - t.getComision();
            } else if (t.getTipo().equals("TRANSFERENCIA")) {
                if (t.getOrigen().equals(id)) {
                    efecto = -t.getMonto() - t.getComision();
                } else {
                    efecto = t.getMonto();
                }
            }

        }
        return efecto + sumarSaldo(nodo.getSiguiente(), id);
    }

    private int sumarPuntos(Nodo<Transaccion> nodo, String id) {
        if (nodo == null) {
            return 0;
        }
        Transaccion t = nodo.getDato();
        int puntos = 0;
        if (!t.isRevertida() && t.getOrigen().equals(id)) {
            puntos = t.getPuntos();
        }
        return puntos + sumarPuntos(nodo.getSiguiente(), id);
    }

    private int sumarMovimientos(Nodo<MovimientoPuntos> nodo) {
        if (nodo == null) {
            return 0;
        }
        return nodo.getDato().getCantidad() + sumarMovimientos(nodo.getSiguiente());
    }

    private void registrar(Cliente cliente, Transaccion t) {
        cliente.getHistorial().agregarFinal(t);
        cliente.getReversibles().push(t);
        cambiarPuntos(cliente, t.getPuntos(), t.getTipo() + " de $" + dinero(t.getMonto()) + " (#" + t.getId() + ")");
    }

    private void alertaSaldoBajo(Cliente cliente) {
        if (cliente.getSaldo() < 50) {
            cliente.notificar(dia, "ALERTA: tu saldo esta bajo ($" + dinero(cliente.getSaldo()) + ")");
        }
    }

    private void validarMonto(double monto) {
        if (monto <= 0) {
            throw new RuntimeException("El monto debe ser mayor que 0");
        }
    }

    private boolean esCategoria(String categoria) {
        for (String c : CATEGORIAS) {
            if (c.equals(categoria)) {
                return true;
            }
        }
        return false;
    }

    private void cargarDatosDePrueba() {
        registrarCliente("C1", "Ana Gomez");
        registrarCliente("C2", "Luis Perez");
        registrarCliente("C3", "Sara Rios");
        registrarCliente("C4", "Juan Diaz");
        depositar("C1", 50000);
        depositar("C2", 20000);
        depositar("C3", 8000);
        depositar("C4", 3000);
        retirar("C1", 1200);
        transferir("C1", "C2", 5000);
        transferir("C2", "C3", 3000);
        transferir("C3", "C4", 500);
        gastar("C1", "Comida", 300);
        gastar("C1", "Transporte", 80);
        gastar("C1", "Comida", 250);
        gastar("C2", "Comida", 120);
        gastar("C2", "Entretenimiento", 400);
        gastar("C3", "Servicios", 900);
        moverDinero("C1", "Ahorros", 10000);
        programar("DEPOSITO", "C2", null, 1000, 1, 7);
        programar("TRANSFERENCIA", "C1", "C3", 200, 2, 0);
    }

    private String dinero(double valor) {
        return String.format("%.2f", valor);
    }
}
