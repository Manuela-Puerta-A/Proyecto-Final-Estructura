package co.edu.uniquindio.poo.estructura;

import java.util.NoSuchElementException;

import co.edu.uniquindio.poo.nodo.Nodo;

public class Cola<T> {

    private Nodo<T> nodoPrimero;
    private Nodo<T> nodoUltimo;
    private int limite;
    private int size;

    public Cola(int limite) {
        this.size = 0;
        this.nodoPrimero = null;
        this.nodoUltimo = null;
        this.limite = limite;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void encolar(T dato) {
        if (size == limite) {
            System.out.println("La cola esta llena");
        }

        Nodo<T> nuevoNodo = new Nodo<T>(dato);
        if (isEmpty()) {
            nodoPrimero = nuevoNodo;
            nodoUltimo = nuevoNodo;
        } else {
            nodoUltimo.setSiguiente(nuevoNodo);
            nodoUltimo = nuevoNodo;
        }
        size++;
    }

    public T desencolar() {
        if (isEmpty()) {
            throw new NoSuchElementException("No hay elementos en la cola");
        } else {
            T dato = nodoPrimero.getDato();
            nodoPrimero = nodoPrimero.getSiguiente();
            if (nodoPrimero == null) {
                nodoUltimo = null;
            }
            size--;
            return dato;
        }
    }

    public void imprimir() {
        Nodo<T> auxiliar = nodoPrimero;
        while (auxiliar != null) {
            System.out.print(auxiliar.getDato() + " -> ");
            auxiliar = auxiliar.getSiguiente();
        }
    }

    public ListaEnlazada<T> listar() {
        ListaEnlazada<T> lista = new ListaEnlazada<>();
        Nodo<T> aux = nodoPrimero;
        while (aux != null) {
            lista.agregarFinal(aux.getDato());
            aux = aux.getSiguiente();
        }
        return lista;
    }
}
