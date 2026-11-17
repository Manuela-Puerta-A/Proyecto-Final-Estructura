package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.Nodo;

public class ColaPrioridad<T extends Comparable<T>> {

    private Nodo<T> nodoPrimero;
    private int size;

    public ColaPrioridad() {
        nodoPrimero = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void encolar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (isEmpty() || dato.compareTo(nodoPrimero.getDato()) < 0) {
            nuevoNodo.setSiguiente(nodoPrimero);
            nodoPrimero = nuevoNodo;
        } else {
            Nodo<T> aux = nodoPrimero;
            while (aux.getSiguiente() != null && aux.getSiguiente().getDato().compareTo(dato) <= 0) {
                aux = aux.getSiguiente();
            }
            nuevoNodo.setSiguiente(aux.getSiguiente());
            aux.setSiguiente(nuevoNodo);
        }
        size++;
    }

    public T desencolar() {
        if (isEmpty()) {
            throw new RuntimeException("No hay elementos en la cola");
        }
        T dato = nodoPrimero.getDato();
        nodoPrimero = nodoPrimero.getSiguiente();
        size--;
        return dato;
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("No hay elementos en la cola");
        }
        return nodoPrimero.getDato();
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
