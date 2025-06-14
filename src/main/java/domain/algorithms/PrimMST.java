package domain.algorithms;

import domain.GraphException;
import java.util.*;

public class PrimMST {

    // Hacer la clase Edge pública y estática
    public static class Edge {
        public Object source;
        public Object destination;
        public double weight;

        public Edge(Object source, Object destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return source + " -> " + destination + " (" + weight + ")";
        }
    }

    private static class PrimEdge implements Comparable<PrimEdge> {
        public Object vertex;
        public double weight;
        public Object parent;

        public PrimEdge(Object vertex, double weight, Object parent) {
            this.vertex = vertex;
            this.weight = weight;
            this.parent = parent;
        }

        @Override
        public int compareTo(PrimEdge other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    public static List<Edge> findMST(List<Object> vertices, Map<Object, Map<Object, Double>> adjacencyMap) throws GraphException {
        if (vertices.isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        List<Edge> mst = new ArrayList<>();
        Set<Object> visited = new HashSet<>();
        PriorityQueue<PrimEdge> minHeap = new PriorityQueue<>();

        // Comenzar con el primer vértice
        Object startVertex = vertices.get(0);
        visited.add(startVertex);

        // Agregar todas las aristas del vértice inicial al heap
        if (adjacencyMap.containsKey(startVertex)) {
            for (Map.Entry<Object, Double> neighbor : adjacencyMap.get(startVertex).entrySet()) {
                minHeap.offer(new PrimEdge(neighbor.getKey(), neighbor.getValue(), startVertex));
            }
        }

        // Algoritmo de Prim
        while (!minHeap.isEmpty() && mst.size() < vertices.size() - 1) {
            PrimEdge current = minHeap.poll();

            if (visited.contains(current.vertex)) {
                continue; // Ya visitado
            }

            // Agregar vértice al MST
            visited.add(current.vertex);
            mst.add(new Edge(current.parent, current.vertex, current.weight));

            // Agregar nuevas aristas al heap
            if (adjacencyMap.containsKey(current.vertex)) {
                for (Map.Entry<Object, Double> neighbor : adjacencyMap.get(current.vertex).entrySet()) {
                    if (!visited.contains(neighbor.getKey())) {
                        minHeap.offer(new PrimEdge(neighbor.getKey(), neighbor.getValue(), current.vertex));
                    }
                }
            }
        }

        return mst;
    }

    public static double calculateTotalWeight(List<Edge> mst) {
        return mst.stream().mapToDouble(edge -> edge.weight).sum();
    }
}