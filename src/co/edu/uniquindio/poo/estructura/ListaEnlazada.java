package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.Nodo;

import java.util.Iterator;

public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> nodoPrimero;
    private Nodo<T> nodoUltimo;
    private int size;

    public ListaEnlazada() {
        nodoPrimero = null;
        nodoUltimo = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public Nodo<T> getPrimero() {
        return nodoPrimero;
    }

    public void agregarFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (isEmpty()) {
            nodoPrimero = nuevoNodo;
            nodoUltimo = nuevoNodo;
        } else {
            nodoUltimo.setSiguiente(nuevoNodo);
            nodoUltimo = nuevoNodo;
        }
        size++;
    }

    public boolean contiene(T dato) {
        Nodo<T> aux = nodoPrimero;
        while (aux != null) {
            if (aux.getDato().equals(dato)) {
                return true;
            }
            aux = aux.getSiguiente();
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> actual = nodoPrimero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}