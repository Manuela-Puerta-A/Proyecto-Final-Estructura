package co.edu.uniquindio.poo.modelo;

public class MovimientoPuntos {

    private int dia;
    private String descripcion;
    private int cantidad;

    public MovimientoPuntos(int dia, String descripcion, int cantidad) {
        this.dia = dia;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public int getDia() {
        return dia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }
}