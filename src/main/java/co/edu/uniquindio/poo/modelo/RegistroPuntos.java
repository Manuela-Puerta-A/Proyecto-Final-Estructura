package co.edu.uniquindio.poo.modelo;

public class RegistroPuntos implements Comparable<RegistroPuntos> {

    private String clienteId;
    private String nombre;
    private int puntos;

    public RegistroPuntos(String clienteId, String nombre, int puntos) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.puntos = puntos;
    }

    @Override
    public int compareTo(RegistroPuntos otro) {
        if (this.puntos != otro.puntos) {
            return Integer.compare(this.puntos, otro.puntos);
        }
        return this.clienteId.compareTo(otro.clienteId);
    }

    public String getRango() {
        return Cliente.calcularRango(puntos);
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }
}
