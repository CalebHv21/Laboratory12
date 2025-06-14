package domain.algorithms;

import domain.GraphException;
import java.util.*;

public class KruskalMST {

    // Hacer la clase Edge pública y estática
    public static class Edge implements Comparable<Edge> {
        public Object source;
        public Object destination;
        public double weight;

        public Edge(Object source, Object destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return source + " -> " + destination + " (" + weight + ")";
        }
    }

    // Union-Find para detectar ciclos
    private static class UnionFind {
        private Map<Object, Object> parent;
        private Map<Object, Integer> rank;

        public UnionFind() {
            parent = new HashMap<>();
            rank = new HashMap<>();
        }

        public void makeSet(Object vertex) {
            parent.put(vertex, vertex);
            rank.put(vertex, 0);
        }

        public Object find(Object vertex) {
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex)));
            }
            return parent.get(vertex);
        }

        public boolean union(Object vertex1, Object vertex2) {
            Object root1 = find(vertex1);
            Object root2 = find(vertex2);

            if (root1.equals(root2)) {
                return false; // Ya están en el mismo conjunto (ciclo)
            }

            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 < rank2) {
                parent.put(root1, root2);
            } else if (rank1 > rank2) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }

            return true;
        }
    }

    public static List<Edge> findMST(List<Object> vertices, List<Edge> edges) throws GraphException {
        if (vertices.isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        List<Edge> mst = new ArrayList<>();
        UnionFind uf = new UnionFind();

        // Inicializar Union-Find
        for (Object vertex : vertices) {
            uf.makeSet(vertex);
        }

        // Ordenar aristas por peso
        Collections.sort(edges);

        // Algoritmo de Kruskal
        for (Edge edge : edges) {
            if (uf.union(edge.source, edge.destination)) {
                mst.add(edge);
                if (mst.size() == vertices.size() - 1) {
                    break; // MST completo
                }
            }
        }

        return mst;
    }

    public static double calculateTotalWeight(List<Edge> mst) {
        return mst.stream().mapToDouble(edge -> edge.weight).sum();
    }
}