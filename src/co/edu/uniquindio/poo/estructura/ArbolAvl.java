package co.edu.uniquindio.poo.estructura;

import co.edu.uniquindio.poo.nodo.NodoArbol;


public class ArbolAVL<T extends Comparable<T>> {

    private NodoArbol<T> raiz;
    private int peso;

    public ArbolAVL() {
        raiz = null;
        peso = 0;
    }

    public int getPeso() {
        return peso;
    }

    public int calcularAltura() {
        return altura(raiz);
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
        return balancear(raizAux);
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
        return balancear(raizAux);
    }

    private NodoArbol<T> buscarMayor(NodoArbol<T> raizAux) {
        if (raizAux.getHijoDerecho() == null) {
            return raizAux;
        }
        return buscarMayor(raizAux.getHijoDerecho());
    }


    private int altura(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return nodo.getAltura();
    }

    private void actualizarAltura(NodoArbol<T> nodo) {
        nodo.setAltura(1 + Math.max(altura(nodo.getHijoIzquierdo()), altura(nodo.getHijoDerecho())));
    }

    
    private int fe(NodoArbol<T> nodo) {
        return altura(nodo.getHijoIzquierdo()) - altura(nodo.getHijoDerecho());
    }

    private NodoArbol<T> balancear(NodoArbol<T> nodo) {
        actualizarAltura(nodo);
        int fe = fe(nodo);
        if (fe > 1) { 
            if (fe(nodo.getHijoIzquierdo()) < 0) {
                nodo.setHijoIzquierdo(rotarIzquierda(nodo.getHijoIzquierdo())); 
            }
            return rotarDerecha(nodo);
        }
        if (fe < -1) { 
            if (fe(nodo.getHijoDerecho()) > 0) {
                nodo.setHijoDerecho(rotarDerecha(nodo.getHijoDerecho())); 
            }
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    private NodoArbol<T> rotarDerecha(NodoArbol<T> nodo) {
        NodoArbol<T> nuevaRaiz = nodo.getHijoIzquierdo();
        nodo.setHijoIzquierdo(nuevaRaiz.getHijoDerecho());
        nuevaRaiz.setHijoDerecho(nodo);
        actualizarAltura(nodo);
        actualizarAltura(nuevaRaiz);
        return nuevaRaiz;
    }

    private NodoArbol<T> rotarIzquierda(NodoArbol<T> nodo) {
        NodoArbol<T> nuevaRaiz = nodo.getHijoDerecho();
        nodo.setHijoDerecho(nuevaRaiz.getHijoIzquierdo());
        nuevaRaiz.setHijoIzquierdo(nodo);
        actualizarAltura(nodo);
        actualizarAltura(nuevaRaiz);
        return nuevaRaiz;
    }

    public ListaEnlazada<T> buscarEntre(T desde, T hasta) {
        ListaEnlazada<T> lista = new ListaEnlazada<>();
        buscarEntre(raiz, desde, hasta, lista);
        return lista;
    }

    private void buscarEntre(NodoArbol<T> raizAux, T desde, T hasta, ListaEnlazada<T> lista) {
        if (raizAux == null) {
            return;
        }
        T elemento = raizAux.getElemento();
        if (elemento.compareTo(desde) > 0) {
            buscarEntre(raizAux.getHijoIzquierdo(), desde, hasta, lista);
        }
        if (elemento.compareTo(desde) >= 0 && elemento.compareTo(hasta) < 0) {
            lista.agregarFinal(elemento);
        }
        if (elemento.compareTo(hasta) < 0) {
            buscarEntre(raizAux.getHijoDerecho(), desde, hasta, lista);
        }
    }

    public boolean estaBalanceado() {
        return estaBalanceado(raiz);
    }

    private boolean estaBalanceado(NodoArbol<T> raizAux) {
        if (raizAux == null) {
            return true;
        }
        int fe = fe(raizAux);
        if (fe > 1 || fe < -1) {
            return false;
        }
        return estaBalanceado(raizAux.getHijoIzquierdo()) && estaBalanceado(raizAux.getHijoDerecho());
    }
}

