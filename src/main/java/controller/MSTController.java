package controller;

import domain.*;
import domain.algorithms.KruskalMST;
import domain.algorithms.PrimMST;
import domain.list.ListException;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import util.AristaVisual;

import java.util.*;

public class MSTController {
    @javafx.fxml.FXML
    private Text txtMessage11;
    @javafx.fxml.FXML
    private Pane graphPane2;
    @javafx.fxml.FXML
    private Text txtMessage1;
    @javafx.fxml.FXML
    private Pane graphPane1;
    @javafx.fxml.FXML
    private RadioButton listRB;
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private RadioButton linkedlistRB;
    @javafx.fxml.FXML
    private RadioButton mstRBKruskal;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Label infoLabel;
    @javafx.fxml.FXML
    private Pane buttonPane11;
    @javafx.fxml.FXML
    private RadioButton mstRBPrim;
    @javafx.fxml.FXML
    private RadioButton matrixRB;

    // Grafos no dirigidos
    private AdjacencyMatrixGraph matrixGraph = new AdjacencyMatrixGraph(15);
    private AdjacencyListGraph adlistGraph = new AdjacencyListGraph(15);
    private SinglyLinkedListGraph linkedListGraph = new SinglyLinkedListGraph();

    private Map<Integer, StackPane> visualNodes1 = new HashMap<>();
    private Map<Integer, StackPane> visualNodes2 = new HashMap<>();
    private List<AristaVisual> visualEdges1 = new ArrayList<>();
    private List<AristaVisual> visualEdges2 = new ArrayList<>();

    private final double RADIUS = 120.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 25.0;
    private Random random = new Random();

    @javafx.fxml.FXML
    public void initialize() {
        infoLabel.setText("Seleccione tipo de grafo y algoritmo MST");
        matrixGraph = new AdjacencyMatrixGraph(15);
        adlistGraph = new AdjacencyListGraph(15);
        linkedListGraph = new SinglyLinkedListGraph();
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        clearVisualElements();

        if (matrixRB.isSelected()) {
            loadMatrixGraph();
        } else if (listRB.isSelected()) {
            loadAdjacencyListGraph();
        } else if (linkedlistRB.isSelected()) {
            loadLinkedListGraph();
        } else {
            util.FXUtility.showErrorAlert("Error", "Seleccione un tipo de grafo");
        }
    }

    @javafx.fxml.FXML
    public void kruskalOnAction(ActionEvent actionEvent) {
        if (!hasGraphLoaded()) {
            util.FXUtility.showErrorAlert("Error", "Primero genere un grafo aleatorio");
            return;
        }

        try {
            if (matrixRB.isSelected()) {
                applyKruskalToMatrix();
            } else if (listRB.isSelected()) {
                applyKruskalToAdjacencyList();
            } else if (linkedlistRB.isSelected()) {
                applyKruskalToLinkedList();
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error aplicando Kruskal: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void primOnAction(ActionEvent actionEvent) {
        if (!hasGraphLoaded()) {
            util.FXUtility.showErrorAlert("Error", "Primero genere un grafo aleatorio");
            return;
        }

        try {
            if (matrixRB.isSelected()) {
                applyPrimToMatrix();
            } else if (listRB.isSelected()) {
                applyPrimToAdjacencyList();
            } else if (linkedlistRB.isSelected()) {
                applyPrimToLinkedList();
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error aplicando Prim: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void adjacencyListOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        matrixRB.setSelected(false);
        linkedlistRB.setSelected(false);
        mstRBKruskal.setSelected(false);
        mstRBPrim.setSelected(false);
    }

    @javafx.fxml.FXML
    public void linkedListOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        matrixRB.setSelected(false);
        listRB.setSelected(false);
        mstRBKruskal.setSelected(false);
        mstRBPrim.setSelected(false);
    }

    @javafx.fxml.FXML
    public void adjacencyMatrixOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        listRB.setSelected(false);
        linkedlistRB.setSelected(false);
        mstRBKruskal.setSelected(false);
        mstRBPrim.setSelected(false);
    }

    private void loadMatrixGraph() {
        try {
            matrixGraph.clear();

            // Generar 8-10 números únicos entre 0 y 99
            Set<Integer> numbers = new HashSet<>();
            int numVertices = random.nextInt(3) + 8; // 8-10 vértices
            while (numbers.size() < numVertices) {
                numbers.add(random.nextInt(100));
            }

            // Agregar vértices
            for (int num : numbers) {
                matrixGraph.addVertex(num);
            }

            // Agregar aristas para asegurar conectividad
            List<Integer> vertexList = new ArrayList<>(numbers);

            // Conectar cada vértice con al menos otro para asegurar conectividad
            for (int i = 0; i < vertexList.size() - 1; i++) {
                int weight = util.Utility.random(10, 100);
                matrixGraph.addEdgeWeight(vertexList.get(i), vertexList.get(i + 1), weight);
            }

            // Agregar algunas aristas adicionales aleatorias
            int additionalEdges = random.nextInt(5) + 3;

            for (int i = 0; i < additionalEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !matrixGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    matrixGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeOriginalGraph();
            infoLabel.setText("Grafo matriz generado - " + numbers.size() + " vértices");

        } catch (GraphException | ListException e) {
            util.FXUtility.showErrorAlert("Error", "Error generando grafo matriz: " + e.getMessage());
        }
    }

    private void loadAdjacencyListGraph() {
        try {
            adlistGraph.clear();

            Set<Integer> numbers = new HashSet<>();
            int numVertices = random.nextInt(3) + 8;
            while (numbers.size() < numVertices) {
                numbers.add(random.nextInt(100));
            }

            for (int num : numbers) {
                adlistGraph.addVertex(num);
            }

            // Usar el método público connectEvenAndOddVertices()
            adlistGraph.connectEvenAndOddVertices();

            List<Integer> vertexList = new ArrayList<>(numbers);
            int additionalEdges = random.nextInt(5) + 3;

            for (int i = 0; i < additionalEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !adlistGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    adlistGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeOriginalAdjacencyListGraph();
            infoLabel.setText("Grafo lista adyacencia generado - " + numbers.size() + " vértices");

        } catch (GraphException | ListException e) {
            util.FXUtility.showErrorAlert("Error", "Error generando grafo lista: " + e.getMessage());
        }
    }

    private void loadLinkedListGraph() {
        try {
            linkedListGraph.clear();

            Set<Integer> numbers = new HashSet<>();
            int numVertices = random.nextInt(3) + 8;
            while (numbers.size() < numVertices) {
                numbers.add(random.nextInt(100));
            }

            for (int num : numbers) {
                linkedListGraph.addVertex(num);
            }

            // Agregar aristas aleatorias para conectividad
            List<Integer> vertexList = new ArrayList<>(numbers);

            // Asegurar conectividad básica - conectar cada vértice con al menos otro
            for (int i = 0; i < vertexList.size() - 1; i++) {
                int weight = util.Utility.random(10, 100);
                linkedListGraph.addEdgeWeight(vertexList.get(i), vertexList.get(i + 1), weight);
            }

            // Agregar aristas adicionales
            int additionalEdges = random.nextInt(vertexList.size()) + 2;
            for (int i = 0; i < additionalEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !linkedListGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    linkedListGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeOriginalLinkedListGraph();
            infoLabel.setText("Grafo lista enlazada generado - " + numbers.size() + " vértices");

        } catch (GraphException | ListException e) {
            util.FXUtility.showErrorAlert("Error", "Error generando grafo lista enlazada: " + e.getMessage());
        }
    }

    private void applyKruskalToMatrix() throws GraphException, ListException {
        String graphInfo = matrixGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        List<KruskalMST.Edge> edges = extractEdgesFromMatrix(graphInfo);

        List<KruskalMST.Edge> mst = KruskalMST.findMST(vertices, edges);
        double totalWeight = KruskalMST.calculateTotalWeight(mst);

        visualizeMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Kruskal MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    private void applyKruskalToAdjacencyList() throws GraphException, ListException {
        String graphInfo = adlistGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        List<KruskalMST.Edge> edges = extractEdgesFromAdjacencyList(graphInfo);

        List<KruskalMST.Edge> mst = KruskalMST.findMST(vertices, edges);
        double totalWeight = KruskalMST.calculateTotalWeight(mst);

        visualizeMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Kruskal MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    private void applyKruskalToLinkedList() throws GraphException, ListException {
        String graphInfo = linkedListGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        List<KruskalMST.Edge> edges = extractEdgesFromLinkedList(graphInfo);

        List<KruskalMST.Edge> mst = KruskalMST.findMST(vertices, edges);
        double totalWeight = KruskalMST.calculateTotalWeight(mst);

        visualizeMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Kruskal MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    private void applyPrimToMatrix() throws GraphException, ListException {
        String graphInfo = matrixGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        Map<Object, Map<Object, Double>> adjacencyMap = extractAdjacencyMapFromMatrix(graphInfo);

        List<PrimMST.Edge> mst = PrimMST.findMST(vertices, adjacencyMap);
        double totalWeight = PrimMST.calculateTotalWeight(mst);

        visualizePrimMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Prim MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    private void applyPrimToAdjacencyList() throws GraphException, ListException {
        String graphInfo = adlistGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        Map<Object, Map<Object, Double>> adjacencyMap = extractAdjacencyMapFromAdjacencyList(graphInfo);

        List<PrimMST.Edge> mst = PrimMST.findMST(vertices, adjacencyMap);
        double totalWeight = PrimMST.calculateTotalWeight(mst);

        visualizePrimMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Prim MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    private void applyPrimToLinkedList() throws GraphException, ListException {
        String graphInfo = linkedListGraph.toString();
        List<Object> vertices = extractVerticesFromString(graphInfo);
        Map<Object, Map<Object, Double>> adjacencyMap = extractAdjacencyMapFromLinkedList(graphInfo);

        List<PrimMST.Edge> mst = PrimMST.findMST(vertices, adjacencyMap);
        double totalWeight = PrimMST.calculateTotalWeight(mst);

        visualizePrimMST(mst, graphPane2, visualNodes2, visualEdges2);
        infoLabel.setText("Prim MST aplicado - Peso total: " + String.format("%.0f", totalWeight));
    }

    // Métodos de extracción de información del toString()
    private List<Object> extractVerticesFromString(String graphInfo) {
        List<Object> vertices = new ArrayList<>();
        String[] lines = graphInfo.split("\n");

        for (String line : lines) {
            if (line.contains("The vextex in the position") && line.contains("is:") ||
                    line.contains("The vertex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    String vertexStr = parts[1].trim();
                    try {
                        Integer vertex = Integer.parseInt(vertexStr);
                        if (!vertices.contains(vertex)) {
                            vertices.add(vertex);
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar si no es un número válido
                    }
                }
            }
        }

        return vertices;
    }

    private List<KruskalMST.Edge> extractEdgesFromMatrix(String graphInfo) {
        List<KruskalMST.Edge> edges = new ArrayList<>();
        String[] lines = graphInfo.split("\n");
        Set<String> processedEdges = new HashSet<>();

        for (String line : lines) {
            if (line.contains("There is edge between the vertexes:") && line.contains("WEIGHT:")) {
                try {
                    String[] edgeParts = line.split("There is edge between the vertexes:")[1].split("_____WEIGHT:");
                    if (edgeParts.length == 2) {
                        String[] vertices = edgeParts[0].split("\\.\\.\\.\\.");
                        if (vertices.length == 2) {
                            Integer source = Integer.parseInt(vertices[0].trim());
                            Integer target = Integer.parseInt(vertices[1].trim());
                            Integer weight = Integer.parseInt(edgeParts[1].trim());

                            String edgeKey = createEdgeKey(source, target);
                            if (!processedEdges.contains(edgeKey)) {
                                edges.add(new KruskalMST.Edge(source, target, weight));
                                processedEdges.add(edgeKey);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return edges;
    }

    private List<KruskalMST.Edge> extractEdgesFromAdjacencyList(String graphInfo) {
        return extractEdgesFromList(graphInfo);
    }

    private List<KruskalMST.Edge> extractEdgesFromLinkedList(String graphInfo) {
        return extractEdgesFromList(graphInfo);
    }

    private List<KruskalMST.Edge> extractEdgesFromList(String graphInfo) {
        List<KruskalMST.Edge> edges = new ArrayList<>();
        String[] lines = graphInfo.split("\n");
        Object currentVertex = null;
        Set<String> processedEdges = new HashSet<>();

        for (String line : lines) {
            if (line.contains("The vextex in the position") && line.contains("is:") ||
                    line.contains("The vertex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    try {
                        currentVertex = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            } else if (line.contains("Edge=") && line.contains("Weight=") && currentVertex != null) {
                try {
                    String[] edgeParts = line.split("Edge=")[1].split("\\. Weight=");
                    if (edgeParts.length == 2) {
                        Integer target = Integer.parseInt(edgeParts[0].trim());
                        Integer weight = Integer.parseInt(edgeParts[1].trim());

                        String edgeKey = createEdgeKey(currentVertex, target);
                        if (!processedEdges.contains(edgeKey)) {
                            edges.add(new KruskalMST.Edge(currentVertex, target, weight));
                            processedEdges.add(edgeKey);
                        }
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return edges;
    }

    private Map<Object, Map<Object, Double>> extractAdjacencyMapFromMatrix(String graphInfo) {
        Map<Object, Map<Object, Double>> adjacencyMap = new HashMap<>();
        String[] lines = graphInfo.split("\n");

        for (String line : lines) {
            if (line.contains("There is edge between the vertexes:") && line.contains("WEIGHT:")) {
                try {
                    String[] edgeParts = line.split("There is edge between the vertexes:")[1].split("_____WEIGHT:");
                    if (edgeParts.length == 2) {
                        String[] vertices = edgeParts[0].split("\\.\\.\\.\\.");
                        if (vertices.length == 2) {
                            Integer source = Integer.parseInt(vertices[0].trim());
                            Integer target = Integer.parseInt(vertices[1].trim());
                            Double weight = Double.parseDouble(edgeParts[1].trim());

                            adjacencyMap.computeIfAbsent(source, k -> new HashMap<>()).put(target, weight);
                            adjacencyMap.computeIfAbsent(target, k -> new HashMap<>()).put(source, weight);
                        }
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return adjacencyMap;
    }

    private Map<Object, Map<Object, Double>> extractAdjacencyMapFromAdjacencyList(String graphInfo) {
        return extractAdjacencyMapFromList(graphInfo);
    }

    private Map<Object, Map<Object, Double>> extractAdjacencyMapFromLinkedList(String graphInfo) {
        return extractAdjacencyMapFromList(graphInfo);
    }

    private Map<Object, Map<Object, Double>> extractAdjacencyMapFromList(String graphInfo) {
        Map<Object, Map<Object, Double>> adjacencyMap = new HashMap<>();
        String[] lines = graphInfo.split("\n");
        Object currentVertex = null;

        for (String line : lines) {
            if (line.contains("The vextex in the position") && line.contains("is:") ||
                    line.contains("The vertex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    try {
                        currentVertex = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            } else if (line.contains("Edge=") && line.contains("Weight=") && currentVertex != null) {
                try {
                    String[] edgeParts = line.split("Edge=")[1].split("\\. Weight=");
                    if (edgeParts.length == 2) {
                        Integer target = Integer.parseInt(edgeParts[0].trim());
                        Double weight = Double.parseDouble(edgeParts[1].trim());

                        adjacencyMap.computeIfAbsent(currentVertex, k -> new HashMap<>()).put(target, weight);
                        adjacencyMap.computeIfAbsent(target, k -> new HashMap<>()).put(currentVertex, weight);
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return adjacencyMap;
    }

    private String createEdgeKey(Object source, Object target) {
        String s1 = source.toString();
        String s2 = target.toString();
        return s1.compareTo(s2) < 0 ? s1 + "-" + s2 : s2 + "-" + s1;
    }

    private void visualizeOriginalGraph() {
        graphPane1.getChildren().clear();
        visualNodes1.clear();
        visualEdges1.clear();

        try {
            String graphInfo = matrixGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            List<KruskalMST.Edge> edges = extractEdgesFromMatrix(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y, Color.LIGHTBLUE);
                visualNodes1.put(vertex, node);
                graphPane1.getChildren().add(node);
            }

            // Crear aristas visuales
            for (KruskalMST.Edge edge : edges) {
                Integer source = (Integer) edge.source;
                Integer target = (Integer) edge.destination;

                StackPane sourceNode = visualNodes1.get(source);
                StackPane targetNode = visualNodes1.get(target);

                if (sourceNode != null && targetNode != null) {
                    createVisualEdge(sourceNode, targetNode, String.valueOf((int) edge.weight),
                            Color.DARKBLUE, 2, graphPane1, visualEdges1);
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private void visualizeOriginalAdjacencyListGraph() {
        graphPane1.getChildren().clear();
        visualNodes1.clear();
        visualEdges1.clear();

        try {
            String graphInfo = adlistGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            List<KruskalMST.Edge> edges = extractEdgesFromAdjacencyList(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y, Color.LIGHTBLUE);
                visualNodes1.put(vertex, node);
                graphPane1.getChildren().add(node);
            }

            // Crear aristas visuales
            for (KruskalMST.Edge edge : edges) {
                Integer source = (Integer) edge.source;
                Integer target = (Integer) edge.destination;

                StackPane sourceNode = visualNodes1.get(source);
                StackPane targetNode = visualNodes1.get(target);

                if (sourceNode != null && targetNode != null) {
                    createVisualEdge(sourceNode, targetNode, String.valueOf((int) edge.weight),
                            Color.DARKBLUE, 2, graphPane1, visualEdges1);
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private void visualizeOriginalLinkedListGraph() {
        graphPane1.getChildren().clear();
        visualNodes1.clear();
        visualEdges1.clear();

        try {
            String graphInfo = linkedListGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            List<KruskalMST.Edge> edges = extractEdgesFromLinkedList(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y, Color.LIGHTBLUE);
                visualNodes1.put(vertex, node);
                graphPane1.getChildren().add(node);
            }

            // Crear aristas visuales
            for (KruskalMST.Edge edge : edges) {
                Integer source = (Integer) edge.source;
                Integer target = (Integer) edge.destination;

                StackPane sourceNode = visualNodes1.get(source);
                StackPane targetNode = visualNodes1.get(target);

                if (sourceNode != null && targetNode != null) {
                    createVisualEdge(sourceNode, targetNode, String.valueOf((int) edge.weight),
                            Color.DARKBLUE, 2, graphPane1, visualEdges1);
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private void visualizeMST(List<KruskalMST.Edge> mst, Pane pane,
                              Map<Integer, StackPane> nodes, List<AristaVisual> edges) {
        pane.getChildren().clear();
        nodes.clear();
        edges.clear();

        // Obtener todos los vértices del MST
        Set<Integer> vertices = new HashSet<>();
        for (KruskalMST.Edge edge : mst) {
            vertices.add((Integer) edge.source);
            vertices.add((Integer) edge.destination);
        }

        List<Integer> vertexList = new ArrayList<>(vertices);
        Collections.sort(vertexList);

        // Crear nodos visuales
        for (int i = 0; i < vertexList.size(); i++) {
            Integer vertex = vertexList.get(i);
            double angle = 2 * Math.PI * i / vertexList.size();
            double x = CENTER_X + RADIUS * Math.cos(angle);
            double y = CENTER_Y + RADIUS * Math.sin(angle);

            StackPane node = createVisualNode(vertex.toString(), x, y, Color.LIGHTGREEN);
            nodes.put(vertex, node);
            pane.getChildren().add(node);
        }

        // Crear aristas del MST
        for (KruskalMST.Edge edge : mst) {
            Integer source = (Integer) edge.source;
            Integer target = (Integer) edge.destination;

            StackPane sourceNode = nodes.get(source);
            StackPane targetNode = nodes.get(target);

            if (sourceNode != null && targetNode != null) {
                createVisualEdge(sourceNode, targetNode, String.valueOf((int) edge.weight),
                        Color.RED, 3, pane, edges);
            }
        }
    }

    private void visualizePrimMST(List<PrimMST.Edge> mst, Pane pane,
                                  Map<Integer, StackPane> nodes, List<AristaVisual> edges) {
        pane.getChildren().clear();
        nodes.clear();
        edges.clear();

        // Obtener todos los vértices del MST
        Set<Integer> vertices = new HashSet<>();
        for (PrimMST.Edge edge : mst) {
            vertices.add((Integer) edge.source);
            vertices.add((Integer) edge.destination);
        }

        List<Integer> vertexList = new ArrayList<>(vertices);
        Collections.sort(vertexList);

        // Crear nodos visuales
        for (int i = 0; i < vertexList.size(); i++) {
            Integer vertex = vertexList.get(i);
            double angle = 2 * Math.PI * i / vertexList.size();
            double x = CENTER_X + RADIUS * Math.cos(angle);
            double y = CENTER_Y + RADIUS * Math.sin(angle);

            StackPane node = createVisualNode(vertex.toString(), x, y, Color.LIGHTGREEN);
            nodes.put(vertex, node);
            pane.getChildren().add(node);
        }

        // Crear aristas del MST
        for (PrimMST.Edge edge : mst) {
            Integer source = (Integer) edge.source;
            Integer target = (Integer) edge.destination;

            StackPane sourceNode = nodes.get(source);
            StackPane targetNode = nodes.get(target);

            if (sourceNode != null && targetNode != null) {
                createVisualEdge(sourceNode, targetNode, String.valueOf((int) edge.weight),
                        Color.RED, 3, pane, edges);
            }
        }
    }

    private StackPane createVisualNode(String text, double x, double y, Color fillColor) {
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(fillColor);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, label);
        stack.setLayoutX(x - NODE_RADIUS);
        stack.setLayoutY(y - NODE_RADIUS);
        stack.setAlignment(Pos.CENTER);

        return stack;
    }

    private void createVisualEdge(StackPane source, StackPane target, String weight,
                                  Color color, double strokeWidth, Pane pane, List<AristaVisual> edges) {
        double startX = source.getLayoutX() + NODE_RADIUS;
        double startY = source.getLayoutY() + NODE_RADIUS;
        double endX = target.getLayoutX() + NODE_RADIUS;
        double endY = target.getLayoutY() + NODE_RADIUS;

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);

        // Agregar etiqueta de peso en el medio de la línea
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;

        Label weightLabel = new Label(weight);
        weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        weightLabel.setStyle("-fx-background-color: white; -fx-padding: 2px;");
        weightLabel.setLayoutX(midX - 10);
        weightLabel.setLayoutY(midY - 10);

        AristaVisual arista = new AristaVisual(line, Integer.parseInt(weight), source, target);
        edges.add(arista);

        pane.getChildren().addAll(line, weightLabel);
    }

    private boolean hasGraphLoaded() {
        if (matrixRB.isSelected()) {
            return !matrixGraph.isEmpty();
        } else if (listRB.isSelected()) {
            return !adlistGraph.isEmpty();
        } else if (linkedlistRB.isSelected()) {
            return !linkedListGraph.isEmpty();
        }
        return false;
    }

    private void clearVisualElements() {
        graphPane1.getChildren().clear();
        graphPane2.getChildren().clear();
        visualNodes1.clear();
        visualNodes2.clear();
        visualEdges1.clear();
        visualEdges2.clear();

        matrixGraph.clear();
        adlistGraph.clear();
        linkedListGraph.clear();

        infoLabel.setText("Elementos limpiados");
    }
}