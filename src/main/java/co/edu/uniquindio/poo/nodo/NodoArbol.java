package co.edu.uniquindio.poo.nodo;

public class NodoArbol<T> {

    private T elemento;
    private NodoArbol<T> hijoIzquierdo;
    private NodoArbol<T> hijoDerecho;
    private int altura; 

    public NodoArbol(T elemento) {
        this.elemento = elemento;
        this.altura = 1;
    }

    public T getElemento() {
        return elemento;
    }

    public void setElemento(T elemento) {
        this.elemento = elemento;
    }

    public NodoArbol<T> getHijoIzquierdo() {
        return hijoIzquierdo;
    }

    public void setHijoIzquierdo(NodoArbol<T> hijoIzquierdo) {
        this.hijoIzquierdo = hijoIzquierdo;
    }

    public NodoArbol<T> getHijoDerecho() {
        return hijoDerecho;
    }

    public void setHijoDerecho(NodoArbol<T> hijoDerecho) {
        this.hijoDerecho = hijoDerecho;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }
}
