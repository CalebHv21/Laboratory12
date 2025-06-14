package domain.algorithms;

import domain.GraphException;
import java.util.*;

public class DijkstraAlgorithm {

    public static class DijkstraResult {
        public Object vertex;
        public int distance;
        public Object predecessor;

        public DijkstraResult(Object vertex, int distance, Object predecessor) {
            this.vertex = vertex;
            this.distance = distance;
            this.predecessor = predecessor;
        }

        @Override
        public String toString() {
            return "Vertex: " + vertex + ", Distance: " + distance + ", Previous: " + predecessor;
        }
    }

    public static List<DijkstraResult> findShortestPaths(List<Object> vertices,
                                                         Map<Object, Map<Object, Integer>> adjacencyMap,
                                                         Object startVertex) throws GraphException {

        if (vertices.isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        if (!vertices.contains(startVertex)) {
            throw new GraphException("El vértice de inicio no existe en el grafo");
        }

        // Inicializar distancias y predecesores
        Map<Object, Integer> distances = new HashMap<>();
        Map<Object, Object> predecessors = new HashMap<>();
        Set<Object> visited = new HashSet<>();
        PriorityQueue<VertexDistance> priorityQueue = new PriorityQueue<>();

        // Inicializar todas las distancias como infinito
        for (Object vertex : vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
            predecessors.put(vertex, null);
        }

        // La distancia al vértice inicial es 0
        distances.put(startVertex, 0);
        priorityQueue.offer(new VertexDistance(startVertex, 0));

        // Algoritmo de Dijkstra
        while (!priorityQueue.isEmpty()) {
            VertexDistance current = priorityQueue.poll();
            Object currentVertex = current.vertex;

            if (visited.contains(currentVertex)) {
                continue;
            }

            visited.add(currentVertex);

            // Examinar todos los vecinos del vértice actual
            if (adjacencyMap.containsKey(currentVertex)) {
                for (Map.Entry<Object, Integer> neighbor : adjacencyMap.get(currentVertex).entrySet()) {
                    Object neighborVertex = neighbor.getKey();
                    int edgeWeight = neighbor.getValue();

                    if (!visited.contains(neighborVertex)) {
                        int newDistance = distances.get(currentVertex) + edgeWeight;

                        if (newDistance < distances.get(neighborVertex)) {
                            distances.put(neighborVertex, newDistance);
                            predecessors.put(neighborVertex, currentVertex);
                            priorityQueue.offer(new VertexDistance(neighborVertex, newDistance));
                        }
                    }
                }
            }
        }

        // Crear lista de resultados
        List<DijkstraResult> results = new ArrayList<>();
        for (Object vertex : vertices) {
            int distance = distances.get(vertex);
            Object predecessor = predecessors.get(vertex);
            results.add(new DijkstraResult(vertex, distance, predecessor));
        }

        // Ordenar por vértice
        results.sort((a, b) -> {
            if (a.vertex instanceof Integer && b.vertex instanceof Integer) {
                return Integer.compare((Integer) a.vertex, (Integer) b.vertex);
            }
            return a.vertex.toString().compareTo(b.vertex.toString());
        });

        return results;
    }

    private static class VertexDistance implements Comparable<VertexDistance> {
        Object vertex;
        int distance;

        public VertexDistance(Object vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(VertexDistance other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static List<Object> getShortestPath(List<DijkstraResult> results, Object targetVertex) {
        List<Object> path = new ArrayList<>();
        Object current = targetVertex;

        // Encontrar el resultado para el vértice objetivo
        Map<Object, Object> predecessors = new HashMap<>();
        for (DijkstraResult result : results) {
            predecessors.put(result.vertex, result.predecessor);
        }

        // Construir el camino hacia atrás
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        return path;
    }
}