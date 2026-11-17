package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.NodoArbol;

public class ArbolBinario<T extends Comparable<T>> {

    private NodoArbol<T> raiz;
    private int peso;

    public ArbolBinario() {
        raiz = null;
        peso = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getPeso() {
        return peso;
    }

    public void insertar(T elemento) {
        raiz = insertar(raiz, elemento);
    }

    private NodoArbol<T> insertar(NodoArbol<T> raizAux, T elemento) {
        if (raizAux == null) {
            peso++;
            return new NodoArbol<>(elemento);
        }
        int cmp = raizAux.getElemento().compareTo(elemento);
        if (cmp == 0) {
            throw new RuntimeException("El nodo ya existe");
        }
        if (cmp > 0) {
            raizAux.setHijoIzquierdo(insertar(raizAux.getHijoIzquierdo(), elemento));
        } else {
            raizAux.setHijoDerecho(insertar(raizAux.getHijoDerecho(), elemento));
        }
        return raizAux;
    }

    public void eliminar(T elemento) {
        raiz = eliminar(raiz, elemento);
    }

    private NodoArbol<T> eliminar(NodoArbol<T> raizAux, T elemento) {
        if (raizAux == null) {
            return null;
        }
        int cmp = raizAux.getElemento().compareTo(elemento);
        if (cmp > 0) {
            raizAux.setHijoIzquierdo(eliminar(raizAux.getHijoIzquierdo(), elemento));
        } else if (cmp < 0) {
            raizAux.setHijoDerecho(eliminar(raizAux.getHijoDerecho(), elemento));
        } else {

            if (raizAux.getHijoIzquierdo() == null) {
                peso--;
                return raizAux.getHijoDerecho();
            }
            if (raizAux.getHijoDerecho() == null) {
                peso--;
                return raizAux.getHijoIzquierdo();
            }

            NodoArbol<T> mayor = buscarMayor(raizAux.getHijoIzquierdo());
            raizAux.setElemento(mayor.getElemento());
            raizAux.setHijoIzquierdo(eliminar(raizAux.getHijoIzquierdo(), mayor.getElemento()));
        }
        return raizAux;
    }

    private NodoArbol<T> buscarMayor(NodoArbol<T> raizAux) {
        if (raizAux.getHijoDerecho() == null) {
            return raizAux;
        }
        return buscarMayor(raizAux.getHijoDerecho());
    }

    public ListaEnlazada<T> inordenInverso() {
        ListaEnlazada<T> lista = new ListaEnlazada<>();
        inordenInverso(raiz, lista);
        return lista;
    }

    private void inordenInverso(NodoArbol<T> raizAux, ListaEnlazada<T> lista) {
        if (raizAux == null) {
            return;
        }
        inordenInverso(raizAux.getHijoDerecho(), lista);
        lista.agregarFinal(raizAux.getElemento());
        inordenInverso(raizAux.getHijoIzquierdo(), lista);
    }

    public int calcularAltura() {
        return calcularAltura(raiz);
    }

    private int calcularAltura(NodoArbol<T> raizAux) {
        if (raizAux == null) {
            return 0;
        }
        int alturaIzquierda = 1 + calcularAltura(raizAux.getHijoIzquierdo());
        int alturaDerecha = 1 + calcularAltura(raizAux.getHijoDerecho());
        if (alturaIzquierda > alturaDerecha) {
            return alturaIzquierda;
        }
        return alturaDerecha;
    }

    public boolean esValido() {
        return esValido(raiz, null, null);
    }

    private boolean esValido(NodoArbol<T> raizAux, T minimo, T maximo) {
        if (raizAux == null) {
            return true;
        }
        T elemento = raizAux.getElemento();
        if (minimo != null && elemento.compareTo(minimo) <= 0) {
            return false;
        }
        if (maximo != null && elemento.compareTo(maximo) >= 0) {
            return false;
        }
        return esValido(raizAux.getHijoIzquierdo(), minimo, elemento)
                && esValido(raizAux.getHijoDerecho(), elemento, maximo);
    }
}
