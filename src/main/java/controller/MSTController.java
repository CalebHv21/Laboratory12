package controller;

import domain.*;
import domain.algorithms.KruskalMST;
import domain.algorithms.PrimMST;
import domain.list.ListException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class MSTController implements Initializable {

    @FXML private RadioButton rbAdjacencyMatrix;
    @FXML private RadioButton rbAdjacencyList;
    @FXML private RadioButton rbLinkedList;
    @FXML private RadioButton rbKruskal;
    @FXML private RadioButton rbPrim;
    @FXML private Button btnRandomize;
    @FXML private Button btnApplyMST;
    @FXML private Pane graphPane;
    @FXML private TextArea txtResult;
    @FXML private Label lblTotalWeight;

    private ToggleGroup graphTypeGroup;
    private ToggleGroup algorithmGroup;
    private Graph currentGraph;
    private Random random = new Random();

    // Para visualización
    private Map<Object, Circle> vertexCircles = new HashMap<>();
    private List<Line> edgeLines = new ArrayList<>();
    private List<Text> weightLabels = new ArrayList<>();

    // Datos del grafo actual
    private List<Object> currentVertices = new ArrayList<>();
    private List<KruskalMST.Edge> currentEdges = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupToggleGroups();
        setupEventHandlers();
        generateRandomGraph();
    }

    private void setupToggleGroups() {
        graphTypeGroup = new ToggleGroup();
        rbAdjacencyMatrix.setToggleGroup(graphTypeGroup);
        rbAdjacencyList.setToggleGroup(graphTypeGroup);
        rbLinkedList.setToggleGroup(graphTypeGroup);
        rbAdjacencyMatrix.setSelected(true);

        algorithmGroup = new ToggleGroup();
        rbKruskal.setToggleGroup(algorithmGroup);
        rbPrim.setToggleGroup(algorithmGroup);
        rbKruskal.setSelected(true);
    }

    private void setupEventHandlers() {
        btnRandomize.setOnAction(e -> generateRandomGraph());
        btnApplyMST.setOnAction(e -> applyMSTAlgorithm());

        graphTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                generateRandomGraph();
            }
        });
    }

    @FXML
    private void generateRandomGraph() {
        try {
            // Crear grafo según el tipo seleccionado
            createGraphByType();

            // Generar vértices aleatorios (5-8 vértices)
            int numVertices = 5 + random.nextInt(4);
            Set<Integer> vertices = new HashSet<>();

            while (vertices.size() < numVertices) {
                vertices.add(random.nextInt(100)); // 0-99
            }

            // Limpiar datos anteriores
            currentVertices.clear();
            currentEdges.clear();

            // Agregar vértices al grafo
            currentGraph.clear();
            for (Integer vertex : vertices) {
                currentGraph.addVertex(vertex);
                currentVertices.add(vertex);
            }

            // Generar aristas aleatorias con pesos
            List<Integer> vertexList = new ArrayList<>(vertices);
            int numEdges = numVertices + random.nextInt(numVertices); // Suficientes para MST

            for (int i = 0; i < numEdges; i++) {
                int source = vertexList.get(random.nextInt(vertexList.size()));
                int dest = vertexList.get(random.nextInt(vertexList.size()));

                if (source != dest && !currentGraph.containsEdge(source, dest)) {
                    double weight = 10 + random.nextDouble() * 90; // 10-100
                    currentGraph.addEdgeWeight(source, dest, weight);

                    // Guardar arista para algoritmos MST
                    currentEdges.add(new KruskalMST.Edge(source, dest, weight));
                }
            }

            visualizeGraph();
            txtResult.clear();
            lblTotalWeight.setText("Peso Total: -");

        } catch (Exception e) {
            showAlert("Error", "Error al generar grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createGraphByType() {
        RadioButton selected = (RadioButton) graphTypeGroup.getSelectedToggle();
        String type = selected.getText();

        switch (type) {
            case "Adjacency Matrix":
                currentGraph = new AdjacencyMatrixGraph(20); // tamaño máximo
                break;
            case "Adjacency List":
                currentGraph = new AdjacencyListGraph(20);
                break;
            case "Linked List":
                currentGraph = new SinglyLinkedListGraph();
                break;
            default:
                currentGraph = new AdjacencyMatrixGraph(20);
        }
    }

    @FXML
    private void applyMSTAlgorithm() {
        try {
            RadioButton selectedAlgorithm = (RadioButton) algorithmGroup.getSelectedToggle();
            String algorithm = selectedAlgorithm.getText();

            if ("Kruskal".equals(algorithm)) {
                applyKruskal();
            } else {
                applyPrim();
            }

        } catch (Exception e) {
            showAlert("Error", "Error al aplicar algoritmo MST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyKruskal() throws GraphException, ListException {
        // Aplicar Kruskal
        List<KruskalMST.Edge> mst = KruskalMST.findMST(currentVertices, currentEdges);
        double totalWeight = KruskalMST.calculateTotalWeight(mst);

        // Mostrar resultado
        StringBuilder result = new StringBuilder("=== ALGORITMO DE KRUSKAL ===\n\n");
        result.append("Aristas del MST:\n");
        for (KruskalMST.Edge edge : mst) {
            result.append(String.format("%s -> %s (%.2f)\n",
                    edge.source, edge.destination, edge.weight));
        }

        txtResult.setText(result.toString());
        lblTotalWeight.setText(String.format("Peso Total: %.2f", totalWeight));

        visualizeMSTKruskal(mst);
    }

    private void applyPrim() throws GraphException, ListException {
        // Crear mapa de adyacencia para Prim
        Map<Object, Map<Object, Double>> adjacencyMap = createAdjacencyMap();

        // Aplicar Prim
        List<PrimMST.Edge> mst = PrimMST.findMST(currentVertices, adjacencyMap);
        double totalWeight = PrimMST.calculateTotalWeight(mst);

        // Mostrar resultado
        StringBuilder result = new StringBuilder("=== ALGORITMO DE PRIM ===\n\n");
        result.append("Aristas del MST:\n");
        for (PrimMST.Edge edge : mst) {
            result.append(String.format("%s -> %s (%.2f)\n",
                    edge.source, edge.destination, edge.weight));
        }

        txtResult.setText(result.toString());
        lblTotalWeight.setText(String.format("Peso Total: %.2f", totalWeight));

        visualizeMSTPrim(mst);
    }

    private Map<Object, Map<Object, Double>> createAdjacencyMap() {
        Map<Object, Map<Object, Double>> adjacencyMap = new HashMap<>();

        // Inicializar mapa
        for (Object vertex : currentVertices) {
            adjacencyMap.put(vertex, new HashMap<>());
        }

        // Agregar aristas
        for (KruskalMST.Edge edge : currentEdges) {
            adjacencyMap.get(edge.source).put(edge.destination, edge.weight);
            adjacencyMap.get(edge.destination).put(edge.source, edge.weight); // grafo no dirigido
        }

        return adjacencyMap;
    }

    private void visualizeGraph() {
        graphPane.getChildren().clear();
        vertexCircles.clear();
        edgeLines.clear();
        weightLabels.clear();

        if (currentVertices.isEmpty()) return;

        // Calcular posiciones de vértices en círculo
        double centerX = graphPane.getPrefWidth() / 2;
        double centerY = graphPane.getPrefHeight() / 2;
        double radius = Math.min(centerX, centerY) * 0.7;

        // Dibujar vértices
        for (int i = 0; i < currentVertices.size(); i++) {
            Object vertex = currentVertices.get(i);
            double angle = 2 * Math.PI * i / currentVertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle circle = new Circle(x, y, 20);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);

            Text label = new Text(x - 10, y + 5, vertex.toString());

            vertexCircles.put(vertex, circle);
            graphPane.getChildren().addAll(circle, label);
        }

        // Dibujar aristas
        for (KruskalMST.Edge edge : currentEdges) {
            Circle sourceCircle = vertexCircles.get(edge.source);
            Circle destCircle = vertexCircles.get(edge.destination);

            if (sourceCircle != null && destCircle != null) {
                Line line = new Line(
                        sourceCircle.getCenterX(), sourceCircle.getCenterY(),
                        destCircle.getCenterX(), destCircle.getCenterY()
                );
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1);

                // Etiqueta de peso
                double midX = (sourceCircle.getCenterX() + destCircle.getCenterX()) / 2;
                double midY = (sourceCircle.getCenterY() + destCircle.getCenterY()) / 2;
                Text weightLabel = new Text(midX, midY, String.format("%.1f", edge.weight));
                weightLabel.setFill(Color.RED);

                edgeLines.add(line);
                weightLabels.add(weightLabel);
                graphPane.getChildren().addAll(line, weightLabel);
            }
        }
    }

    private void visualizeMSTKruskal(List<KruskalMST.Edge> mst) {
        // Resaltar aristas del MST
        for (Line line : edgeLines) {
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(1);
        }

        // Resaltar aristas del MST
        for (KruskalMST.Edge mstEdge : mst) {
            for (int i = 0; i < currentEdges.size(); i++) {
                KruskalMST.Edge edge = currentEdges.get(i);
                if ((edge.source.equals(mstEdge.source) && edge.destination.equals(mstEdge.destination)) ||
                        (edge.source.equals(mstEdge.destination) && edge.destination.equals(mstEdge.source))) {
                    edgeLines.get(i).setStroke(Color.BLUE);
                    edgeLines.get(i).setStrokeWidth(3);
                    break;
                }
            }
        }
    }

    private void visualizeMSTPrim(List<PrimMST.Edge> mst) {
        // Similar a Kruskal
        for (Line line : edgeLines) {
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(1);
        }

        for (PrimMST.Edge mstEdge : mst) {
            for (int i = 0; i < currentEdges.size(); i++) {
                KruskalMST.Edge edge = currentEdges.get(i);
                if ((edge.source.equals(mstEdge.source) && edge.destination.equals(mstEdge.destination)) ||
                        (edge.source.equals(mstEdge.destination) && edge.destination.equals(mstEdge.source))) {
                    edgeLines.get(i).setStroke(Color.GREEN);
                    edgeLines.get(i).setStrokeWidth(3);
                    break;
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}