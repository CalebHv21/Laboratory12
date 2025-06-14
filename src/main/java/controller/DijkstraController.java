package controller;

import domain.*;
import domain.algorithms.DijkstraAlgorithm;
import domain.list.ListException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.AristaVisual;

import java.util.*;

public class DijkstraController {
    @javafx.fxml.FXML
    private RadioButton linkedlistRB;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Label infoLabel;
    @javafx.fxml.FXML
    private Pane buttonPane11;
    @javafx.fxml.FXML
    private TableColumn<DijkstraTableData, Integer> tcPos;
    @javafx.fxml.FXML
    private TableColumn<DijkstraTableData, String> tcVertex;
    @javafx.fxml.FXML
    private RadioButton listRB;
    @javafx.fxml.FXML
    private RadioButton matrixRB;
    @javafx.fxml.FXML
    private TableColumn<DijkstraTableData, String> tcDistance;
    @javafx.fxml.FXML
    private Pane graphPane;
    @javafx.fxml.FXML
    private TableView<DijkstraTableData> tvGraph;

    // Grafos dirigidos
    private DirectedAdjacencyMatrixGraph matrixGraph = new DirectedAdjacencyMatrixGraph(15);
    private DirectedAdjacencyListGraph adlistGraph = new DirectedAdjacencyListGraph(15);
    private DirectedSinglyLinkedListGraph linkedListGraph = new DirectedSinglyLinkedListGraph();

    private Map<Integer, StackPane> visualNodes = new HashMap<>();
    private List<AristaVisual> visualEdges = new ArrayList<>();

    private final double RADIUS = 130.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 25.0;
    private Random random = new Random();

    @javafx.fxml.FXML
    public void initialize() {
        infoLabel.setText("Seleccione tipo de grafo y genere uno aleatorio");

        // Configurar columnas de la tabla
        tcPos.setCellValueFactory(new PropertyValueFactory<>("position"));
        tcVertex.setCellValueFactory(new PropertyValueFactory<>("vertex"));
        tcDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));

        matrixGraph = new DirectedAdjacencyMatrixGraph(15);
        adlistGraph = new DirectedAdjacencyListGraph(15);
        linkedListGraph = new DirectedSinglyLinkedListGraph();
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
    public void adjacencyListOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        matrixRB.setSelected(false);
        linkedlistRB.setSelected(false);
    }

    @javafx.fxml.FXML
    public void linkedListOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        matrixRB.setSelected(false);
        listRB.setSelected(false);
    }

    @javafx.fxml.FXML
    public void adjacencyMatrixOnAction(ActionEvent actionEvent) {
        clearVisualElements();
        listRB.setSelected(false);
        linkedlistRB.setSelected(false);
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

            // Agregar aristas dirigidas aleatorias
            List<Integer> vertexList = new ArrayList<>(numbers);
            int numEdges = random.nextInt(vertexList.size() * 2) + vertexList.size();

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !matrixGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    matrixGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeGraph();
            applyDijkstraToMatrix();
            infoLabel.setText("Grafo matriz generado y Dijkstra aplicado - " + numbers.size() + " vértices");

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

            List<Integer> vertexList = new ArrayList<>(numbers);
            int numEdges = random.nextInt(vertexList.size() * 2) + vertexList.size();

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !adlistGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    adlistGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeAdjacencyListGraph();
            applyDijkstraToAdjacencyList();
            infoLabel.setText("Grafo lista adyacencia generado y Dijkstra aplicado - " + numbers.size() + " vértices");

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

            List<Integer> vertexList = new ArrayList<>(numbers);
            int numEdges = random.nextInt(vertexList.size() * 2) + vertexList.size();

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !linkedListGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(10, 100);
                    linkedListGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeLinkedListGraph();
            applyDijkstraToLinkedList();
            infoLabel.setText("Grafo lista enlazada generado y Dijkstra aplicado - " + numbers.size() + " vértices");

        } catch (GraphException | ListException e) {
            util.FXUtility.showErrorAlert("Error", "Error generando grafo lista enlazada: " + e.getMessage());
        }
    }

    private void applyDijkstraToMatrix() throws GraphException, ListException {
        List<Object> vertices = new ArrayList<>();
        Map<Object, Map<Object, Integer>> adjacencyMap = new HashMap<>();

        // Extraer vértices usando toString y parseando
        String graphInfo = matrixGraph.toString();
        vertices = extractVerticesFromString(graphInfo);

        // Crear mapa de adyacencia usando toString del grafo
        adjacencyMap = extractAdjacencyMapFromMatrix(graphInfo);

        if (!vertices.isEmpty()) {
            Object startVertex = vertices.get(0);
            List<DijkstraAlgorithm.DijkstraResult> results =
                    DijkstraAlgorithm.findShortestPaths(vertices, adjacencyMap, startVertex);

            updateTable(results);
        }
    }

    private void applyDijkstraToAdjacencyList() throws GraphException, ListException {
        List<Object> vertices = new ArrayList<>();
        Map<Object, Map<Object, Integer>> adjacencyMap = new HashMap<>();

        // Extraer información usando toString del grafo
        String graphInfo = adlistGraph.toString();
        vertices = extractVerticesFromString(graphInfo);
        adjacencyMap = extractAdjacencyMapFromList(graphInfo);

        if (!vertices.isEmpty()) {
            Object startVertex = vertices.get(0);
            List<DijkstraAlgorithm.DijkstraResult> results =
                    DijkstraAlgorithm.findShortestPaths(vertices, adjacencyMap, startVertex);

            updateTable(results);
        }
    }

    private void applyDijkstraToLinkedList() throws GraphException, ListException {
        List<Object> vertices = new ArrayList<>();
        Map<Object, Map<Object, Integer>> adjacencyMap = new HashMap<>();

        // Extraer información usando toString del grafo
        String graphInfo = linkedListGraph.toString();
        vertices = extractVerticesFromString(graphInfo);
        adjacencyMap = extractAdjacencyMapFromList(graphInfo);

        if (!vertices.isEmpty()) {
            Object startVertex = vertices.get(0);
            List<DijkstraAlgorithm.DijkstraResult> results =
                    DijkstraAlgorithm.findShortestPaths(vertices, adjacencyMap, startVertex);

            updateTable(results);
        }
    }

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

    private Map<Object, Map<Object, Integer>> extractAdjacencyMapFromMatrix(String graphInfo) {
        Map<Object, Map<Object, Integer>> adjacencyMap = new HashMap<>();
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
                            Integer weight = Integer.parseInt(edgeParts[1].trim());

                            adjacencyMap.computeIfAbsent(source, k -> new HashMap<>()).put(target, weight);
                        }
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return adjacencyMap;
    }

    private Map<Object, Map<Object, Integer>> extractAdjacencyMapFromList(String graphInfo) {
        Map<Object, Map<Object, Integer>> adjacencyMap = new HashMap<>();
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
                        Integer weight = Integer.parseInt(edgeParts[1].trim());

                        adjacencyMap.computeIfAbsent(currentVertex, k -> new HashMap<>()).put(target, weight);
                    }
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return adjacencyMap;
    }

    private void visualizeGraph() {
        graphPane.getChildren().clear();
        visualNodes.clear();
        visualEdges.clear();

        try {
            String graphInfo = matrixGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            Map<Object, Map<Object, Integer>> adjacencyMap = extractAdjacencyMapFromMatrix(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y);
                visualNodes.put(vertex, node);
                graphPane.getChildren().add(node);
            }

            // Crear aristas visuales dirigidas
            for (Object source : adjacencyMap.keySet()) {
                for (Object target : adjacencyMap.get(source).keySet()) {
                    Integer weight = adjacencyMap.get(source).get(target);

                    StackPane sourceNode = visualNodes.get(source);
                    StackPane targetNode = visualNodes.get(target);

                    if (sourceNode != null && targetNode != null) {
                        createDirectedVisualEdge(sourceNode, targetNode, weight.toString());
                    }
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private void visualizeAdjacencyListGraph() {
        graphPane.getChildren().clear();
        visualNodes.clear();
        visualEdges.clear();

        try {
            String graphInfo = adlistGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            Map<Object, Map<Object, Integer>> adjacencyMap = extractAdjacencyMapFromList(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y);
                visualNodes.put(vertex, node);
                graphPane.getChildren().add(node);
            }

            // Crear aristas visuales dirigidas
            for (Object source : adjacencyMap.keySet()) {
                for (Object target : adjacencyMap.get(source).keySet()) {
                    Integer weight = adjacencyMap.get(source).get(target);

                    StackPane sourceNode = visualNodes.get(source);
                    StackPane targetNode = visualNodes.get(target);

                    if (sourceNode != null && targetNode != null) {
                        createDirectedVisualEdge(sourceNode, targetNode, weight.toString());
                    }
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private void visualizeLinkedListGraph() {
        graphPane.getChildren().clear();
        visualNodes.clear();
        visualEdges.clear();

        try {
            String graphInfo = linkedListGraph.toString();
            List<Object> vertices = extractVerticesFromString(graphInfo);
            Map<Object, Map<Object, Integer>> adjacencyMap = extractAdjacencyMapFromList(graphInfo);

            // Crear nodos visuales
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = (Integer) vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y);
                visualNodes.put(vertex, node);
                graphPane.getChildren().add(node);
            }

            // Crear aristas visuales dirigidas
            for (Object source : adjacencyMap.keySet()) {
                for (Object target : adjacencyMap.get(source).keySet()) {
                    Integer weight = adjacencyMap.get(source).get(target);

                    StackPane sourceNode = visualNodes.get(source);
                    StackPane targetNode = visualNodes.get(target);

                    if (sourceNode != null && targetNode != null) {
                        createDirectedVisualEdge(sourceNode, targetNode, weight.toString());
                    }
                }
            }
        } catch (Exception e) {
            util.FXUtility.showErrorAlert("Error", "Error visualizando grafo: " + e.getMessage());
        }
    }

    private StackPane createVisualNode(String text, double x, double y) {
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(Color.LIGHTCYAN);
        circle.setStroke(Color.DARKBLUE);
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

    private void createDirectedVisualEdge(StackPane source, StackPane target, String weight) {
        double startX = source.getLayoutX() + NODE_RADIUS;
        double startY = source.getLayoutY() + NODE_RADIUS;
        double endX = target.getLayoutX() + NODE_RADIUS;
        double endY = target.getLayoutY() + NODE_RADIUS;

        // Calcular punto de conexión en el borde del círculo
        double dx = endX - startX;
        double dy = endY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double unitX = dx / distance;
        double unitY = dy / distance;

        double adjustedStartX = startX + unitX * NODE_RADIUS;
        double adjustedStartY = startY + unitY * NODE_RADIUS;
        double adjustedEndX = endX - unitX * NODE_RADIUS;
        double adjustedEndY = endY - unitY * NODE_RADIUS;

        Line line = new Line(adjustedStartX, adjustedStartY, adjustedEndX, adjustedEndY);
        line.setStroke(Color.DARKBLUE);
        line.setStrokeWidth(2);

        // Crear flecha para indicar dirección
        createArrowHead(line, adjustedEndX, adjustedEndY, unitX, unitY);

        // Etiqueta de peso
        double midX = (adjustedStartX + adjustedEndX) / 2;
        double midY = (adjustedStartY + adjustedEndY) / 2;

        Label weightLabel = new Label(weight);
        weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        weightLabel.setStyle("-fx-background-color: white; -fx-padding: 1px;");
        weightLabel.setLayoutX(midX - 8);
        weightLabel.setLayoutY(midY - 8);

        AristaVisual arista = new AristaVisual(line, Integer.parseInt(weight), source, target);
        visualEdges.add(arista);

        graphPane.getChildren().addAll(line, weightLabel);
    }

    private void createArrowHead(Line line, double endX, double endY, double unitX, double unitY) {
        double arrowLength = 10;
        double arrowAngle = Math.PI / 6;

        double arrowX1 = endX - arrowLength * Math.cos(Math.atan2(unitY, unitX) - arrowAngle);
        double arrowY1 = endY - arrowLength * Math.sin(Math.atan2(unitY, unitX) - arrowAngle);
        double arrowX2 = endX - arrowLength * Math.cos(Math.atan2(unitY, unitX) + arrowAngle);
        double arrowY2 = endY - arrowLength * Math.sin(Math.atan2(unitY, unitX) + arrowAngle);

        Line arrow1 = new Line(endX, endY, arrowX1, arrowY1);
        Line arrow2 = new Line(endX, endY, arrowX2, arrowY2);

        arrow1.setStroke(Color.DARKBLUE);
        arrow2.setStroke(Color.DARKBLUE);
        arrow1.setStrokeWidth(2);
        arrow2.setStrokeWidth(2);

        graphPane.getChildren().addAll(arrow1, arrow2);
    }

    private void updateTable(List<DijkstraAlgorithm.DijkstraResult> results) {
        ObservableList<DijkstraTableData> data = FXCollections.observableArrayList();

        for (int i = 0; i < results.size(); i++) {
            DijkstraAlgorithm.DijkstraResult result = results.get(i);
            String distanceStr = result.distance == Integer.MAX_VALUE ? "∞" : String.valueOf(result.distance);
            data.add(new DijkstraTableData(i, result.vertex.toString(), distanceStr));
        }

        tvGraph.setItems(data);
    }

    private void clearVisualElements() {
        graphPane.getChildren().clear();
        visualNodes.clear();
        visualEdges.clear();

        matrixGraph.clear();
        adlistGraph.clear();
        linkedListGraph.clear();

        if (tvGraph != null) {
            tvGraph.getItems().clear();
        }

        infoLabel.setText("Elementos limpiados");
    }

    // Clase para los datos de la tabla
    public static class DijkstraTableData {
        private int position;
        private String vertex;
        private String distance;

        public DijkstraTableData(int position, String vertex, String distance) {
            this.position = position;
            this.vertex = vertex;
            this.distance = distance;
        }

        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }

        public String getVertex() { return vertex; }
        public void setVertex(String vertex) { this.vertex = vertex; }

        public String getDistance() { return distance; }
        public void setDistance(String distance) { this.distance = distance; }
    }
}