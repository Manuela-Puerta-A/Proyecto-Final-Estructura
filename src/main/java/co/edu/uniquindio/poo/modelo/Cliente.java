package co.edu.uniquindio.poo.modelo;

import co.edu.uniquindio.poo.estructura.Cola;
import co.edu.uniquindio.poo.estructura.ColaLlena;
import co.edu.uniquindio.poo.estructura.ListaCircular;
import co.edu.uniquindio.poo.estructura.ListaEnlazada;
import co.edu.uniquindio.poo.estructura.Pila;

public class Cliente {

    private String id;
    private String nombre;
    private double saldo;
    private double ahorros;
    private int puntos;
    private int puntosCanjeados;
    private ListaEnlazada<Transaccion> historial;
    private Pila<Transaccion> reversibles;
    private ListaCircular<String> notificaciones;
    private Cola<String> noLeidas;
    private ListaEnlazada<MovimientoPuntos> historialPuntos;
    private String ultimaCategoria;

    private int transferenciasConDescuento;
    private int retirosGratisHastaDia;

    public Cliente(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = 0;
        this.ahorros = 0;
        this.puntos = 0;
        this.puntosCanjeados = 0;
        this.historial = new ListaEnlazada<>();
        this.reversibles = new Pila<>();
        this.notificaciones = new ListaCircular<>(5);
        this.noLeidas = new Cola<>(10);
        this.historialPuntos = new ListaEnlazada<>();
        this.ultimaCategoria = null;
        this.transferenciasConDescuento = 0;
        this.retirosGratisHastaDia = 0;
    }

    public String getRango() {
        return calcularRango(puntos);
    }

    public static String calcularRango(int puntos) {
        if (puntos > 5000) {
            return "Platino";
        } else if (puntos > 1000) {
            return "Oro";
        } else if (puntos > 500) {
            return "Plata";
        }
        return "Bronce";
    }

    public int getPuntosDisponibles() {
        return puntos - puntosCanjeados;
    }

    public void notificar(int dia, String mensaje) {
        String texto = "Dia " + dia + ": " + mensaje;
        notificaciones.agregar(texto);
        try {
            noLeidas.encolar(texto);
        } catch (ColaLlena e) {
            noLeidas.desencolar();
            noLeidas.encolar(texto);
        }
    }

    public ListaEnlazada<String> leerNotificaciones() {
        ListaEnlazada<String> leidas = new ListaEnlazada<>();
        while (!noLeidas.isEmpty()) {
            leidas.agregarFinal(noLeidas.desencolar());
        }
        return leidas;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getAhorros() {
        return ahorros;
    }

    public void setAhorros(double ahorros) {
        this.ahorros = ahorros;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntosCanjeados() {
        return puntosCanjeados;
    }

    public void setPuntosCanjeados(int puntosCanjeados) {
        this.puntosCanjeados = puntosCanjeados;
    }

    public ListaEnlazada<Transaccion> getHistorial() {
        return historial;
    }

    public Pila<Transaccion> getReversibles() {
        return reversibles;
    }

    public ListaCircular<String> getNotificaciones() {
        return notificaciones;
    }

    public int getTransferenciasConDescuento() {
        return transferenciasConDescuento;
    }

    public void setTransferenciasConDescuento(int transferenciasConDescuento) {
        this.transferenciasConDescuento = transferenciasConDescuento;
    }

    public int getRetirosGratisHastaDia() {
        return retirosGratisHastaDia;
    }

    public void setRetirosGratisHastaDia(int retirosGratisHastaDia) {
        this.retirosGratisHastaDia = retirosGratisHastaDia;
    }

    public Cola<String> getNoLeidas() {
        return noLeidas;
    }

    public ListaEnlazada<MovimientoPuntos> getHistorialPuntos() {
        return historialPuntos;
    }

    public String getUltimaCategoria() {
        return ultimaCategoria;
    }

    public void setUltimaCategoria(String ultimaCategoria) {
        this.ultimaCategoria = ultimaCategoria;
    }
}
