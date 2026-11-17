package co.edu.uniquindio.poo.estructura;

import java.util.HashMap;

public class Grafo {

    private HashMap<String, HashMap<String, Double>> adyacencia;

    public Grafo() {
        adyacencia = new HashMap<>();
    }

    public void agregarVertice(String vertice) {
        if (!adyacencia.containsKey(vertice)) {
            adyacencia.put(vertice, new HashMap<>());
        }
    }

    public void agregarArista(String origen, String destino, double monto) {
        agregarVertice(origen);
        agregarVertice(destino);
        HashMap<String, Double> vecinos = adyacencia.get(origen);
        vecinos.put(destino, vecinos.getOrDefault(destino, 0.0) + monto);
    }

    public void restarArista(String origen, String destino, double monto) {
        HashMap<String, Double> vecinos = adyacencia.get(origen);
        if (vecinos == null || !vecinos.containsKey(destino)) {
            return;
        }
        double nuevo = vecinos.get(destino) - monto;
        if (nuevo <= 0.001) {
            vecinos.remove(destino);
        } else {
            vecinos.put(destino, nuevo);
        }
    }

    public HashMap<String, Double> getVecinos(String vertice) {
        agregarVertice(vertice);
        return adyacencia.get(vertice);
    }

    public ListaEnlazada<String> recorridoAnchura(String inicio) {
        ListaEnlazada<String> visitados = new ListaEnlazada<>();
        Cola<String> cola = new Cola<>(100);
        cola.encolar(inicio);
        visitados.agregarFinal(inicio);
        while (!cola.isEmpty()) {
            String actual = cola.desencolar();
            for (String vecino : getVecinos(actual).keySet()) {
                if (!visitados.contiene(vecino)) {
                    visitados.agregarFinal(vecino);
                    cola.encolar(vecino);
                }
            }
        }
        return visitados;
    }
}
