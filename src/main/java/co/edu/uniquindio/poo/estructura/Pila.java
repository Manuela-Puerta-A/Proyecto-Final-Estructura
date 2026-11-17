package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.Nodo;

public class Pila<T> {

    private Nodo<T> cima;
    private int size;

    public Pila() {
        cima = null;
        size = 0;
    }

    public boolean isEmpty() {
        return cima == null;
    }

    public int getSize() {
        return size;
    }

    public void push(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(cima);
        cima = nuevoNodo;
        size++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("No hay elementos en la pila");
        }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        size--;
        return dato;
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("No hay elementos en la pila");
        }
        return cima.getDato();
    }
}