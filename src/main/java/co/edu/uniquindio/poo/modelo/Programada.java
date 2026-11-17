package co.edu.uniquindio.poo.modelo;

public class Programada implements Comparable<Programada> {

    private String tipo;
    private String clienteId;
    private String destinoId;
    private double monto;
    private int dia;
    private int cadaDias;

    public Programada(String tipo, String clienteId, String destinoId, double monto, int dia, int cadaDias) {
        this.tipo = tipo;
        this.clienteId = clienteId;
        this.destinoId = destinoId;
        this.monto = monto;
        this.dia = dia;
        this.cadaDias = cadaDias;
    }

    @Override
    public int compareTo(Programada otra) {
        return this.dia - otra.dia;
    }

    public String getTipo() {
        return tipo;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public double getMonto() {
        return monto;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getCadaDias() {
        return cadaDias;
    }
}
