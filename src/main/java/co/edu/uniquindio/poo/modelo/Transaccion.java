package co.edu.uniquindio.poo.modelo;

public class Transaccion {

    private static int contador = 0;

    private int id;
    private String tipo;
    private String origen;
    private String destino;
    private double monto;
    private double comision;
    private int puntos;
    private int dia;
    private boolean revertida;

    public Transaccion(String tipo, String origen, String destino, double monto, double comision, int puntos, int dia) {
        contador++;
        this.id = contador;
        this.tipo = tipo;
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
        this.comision = comision;
        this.puntos = puntos;
        this.dia = dia;
        this.revertida = false;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getMonto() {
        return monto;
    }

    public double getComision() {
        return comision;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getDia() {
        return dia;
    }

    public boolean isRevertida() {
        return revertida;
    }

    public void setRevertida(boolean revertida) {
        this.revertida = revertida;
    }
}
