package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.Nodo;

public class ListaCircular<T> {

    private Nodo<T> nodoUltimo;
    private int size;
    private int capacidad;

    public ListaCircular(int capacidad) {
        this.nodoUltimo = null;
        this.size = 0;
        this.capacidad = capacidad;
    }

    public void agregar(T dato) {
        if (size == 0) {
            Nodo<T> nuevoNodo = new Nodo<>(dato);
            nuevoNodo.setSiguiente(nuevoNodo);
            nodoUltimo = nuevoNodo;
            size++;
        } else if (size < capacidad) {
            Nodo<T> nuevoNodo = new Nodo<>(dato);
            nuevoNodo.setSiguiente(nodoUltimo.getSiguiente());
            nodoUltimo.setSiguiente(nuevoNodo);
            nodoUltimo = nuevoNodo;
            size++;
        } else {
            nodoUltimo = nodoUltimo.getSiguiente();
            nodoUltimo.setDato(dato);
        }
    }

    public ListaEnlazada<T> listar() {
        ListaEnlazada<T> lista = new ListaEnlazada<>();
        if (size == 0) {
            return lista;
        }
        Nodo<T> aux = nodoUltimo.getSiguiente();
        for (int i = 0; i < size; i++) {
            lista.agregarFinal(aux.getDato());
            aux = aux.getSiguiente();
        }
        return lista;
    }
}
