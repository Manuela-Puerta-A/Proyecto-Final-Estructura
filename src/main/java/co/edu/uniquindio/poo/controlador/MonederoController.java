package co.edu.uniquindio.poo.controlador;

import co.edu.uniquindio.poo.servicio.Banco;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class MonederoController {

    private Banco banco;

    public MonederoController(Banco banco) {
        this.banco = banco;
    }

   
    @GetMapping("/")
    public String inicio(@RequestParam(defaultValue = "C1") String id, Model model) {
        model.addAttribute("cliente", banco.buscarCliente(id));
        model.addAttribute("clientes", banco.getClientes());
        model.addAttribute("categorias", banco.getCategorias());
        model.addAttribute("dia", banco.getDia());
        return "index";
    }

   
    @GetMapping("/puntos")
    public String puntos(@RequestParam(defaultValue = "C1") String id,
                         @RequestParam(defaultValue = "Bronce") String rango, Model model) {
        model.addAttribute("cliente", banco.buscarCliente(id));
        model.addAttribute("clientes", banco.getClientes());
        model.addAttribute("dia", banco.getDia());
        model.addAttribute("puntosTabla", banco.consultarPuntos(id));
        model.addAttribute("ranking", banco.ranking());
        model.addAttribute("rango", rango);
        model.addAttribute("clientesRango", banco.clientesDelRango(rango));
        model.addAttribute("alturaABB", banco.getAlturaABB());
        model.addAttribute("alturaAVL", banco.getAlturaAVL());
        return "puntos";
    }

   
    @GetMapping("/reportes")
    public String reportes(@RequestParam(defaultValue = "C1") String id, Model model) {
        model.addAttribute("cliente", banco.buscarCliente(id));
        model.addAttribute("clientes", banco.getClientes());
        model.addAttribute("dia", banco.getDia());
        model.addAttribute("programadas", banco.getProgramadas());
        model.addAttribute("gastos", banco.gastosPorCategoria(id));
        model.addAttribute("categoriaPrincipal", banco.categoriaPrincipal(id));
        model.addAttribute("gastoTotal", banco.gastoTotalPorCategoria());
        model.addAttribute("patrones", banco.patronesDeGasto());
        model.addAttribute("transferencias", banco.transferenciasDe(id));
        model.addAttribute("red", banco.redDeTransferencias(id));
        model.addAttribute("monederos", banco.movimientosDeMonederos(id));
        model.addAttribute("integridad", banco.verificarIntegridad(id));
        return "reportes";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam String nuevoId, @RequestParam String nombre, RedirectAttributes attrs) {
        try {
            banco.registrarCliente(nuevoId, nombre);
            attrs.addFlashAttribute("mensaje", "Cliente registrado");
            return "redirect:/?id=" + nuevoId;
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/depositar")
    public String depositar(@RequestParam String id, @RequestParam double monto, RedirectAttributes attrs) {
        try {
            banco.depositar(id, monto);
            attrs.addFlashAttribute("mensaje", "Deposito realizado");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/retirar")
    public String retirar(@RequestParam String id, @RequestParam double monto, RedirectAttributes attrs) {
        try {
            banco.retirar(id, monto);
            attrs.addFlashAttribute("mensaje", "Retiro realizado");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/transferir")
    public String transferir(@RequestParam String id, @RequestParam String destino, @RequestParam double monto,
                             RedirectAttributes attrs) {
        try {
            banco.transferir(id, destino, monto);
            attrs.addFlashAttribute("mensaje", "Transferencia realizada");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/gastar")
    public String gastar(@RequestParam String id, @RequestParam String categoria, @RequestParam double monto,
                         RedirectAttributes attrs) {
        try {
            banco.gastar(id, categoria, monto);
            attrs.addFlashAttribute("mensaje", "Gasto registrado en " + categoria);
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/leer")
    public String leer(@RequestParam String id, RedirectAttributes attrs) {
        int cantidad = banco.leerNotificaciones(id).getSize();
        attrs.addFlashAttribute("mensaje", "Leiste " + cantidad + " notificaciones");
        return "redirect:/?id=" + id;
    }

    @PostMapping("/mover")
    public String mover(@RequestParam String id, @RequestParam String hacia, @RequestParam double monto,
                        RedirectAttributes attrs) {
        try {
            banco.moverDinero(id, hacia, monto);
            attrs.addFlashAttribute("mensaje", "Dinero movido a " + hacia);
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/revertir")
    public String revertir(@RequestParam String id, RedirectAttributes attrs) {
        try {
            banco.revertir(id);
            attrs.addFlashAttribute("mensaje", "Se revirtio la ultima transaccion");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/?id=" + id;
    }

    @PostMapping("/canjear")
    public String canjear(@RequestParam String id, @RequestParam int costo, RedirectAttributes attrs) {
        try {
            banco.canjear(id, costo);
            attrs.addFlashAttribute("mensaje", "Canjeaste " + costo + " puntos");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/puntos?id=" + id;
    }

    @PostMapping("/programar")
    public String programar(@RequestParam String id, @RequestParam String tipo, @RequestParam String destino,
                            @RequestParam double monto, @RequestParam int enDias, @RequestParam int cadaDias,
                            RedirectAttributes attrs) {
        try {
            banco.programar(tipo, id, destino, monto, enDias, cadaDias);
            attrs.addFlashAttribute("mensaje", "Transaccion programada");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reportes?id=" + id;
    }

    @PostMapping("/avanzar")
    public String avanzar(@RequestParam String id, RedirectAttributes attrs) {
        banco.avanzarDia();
        attrs.addFlashAttribute("mensaje", "Paso un dia. Hoy es el dia " + banco.getDia());
        return "redirect:/reportes?id=" + id;
    }
}