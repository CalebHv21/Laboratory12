package controller;

import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

public class MatrixOperationsController {
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Label infoLabel;
    @javafx.fxml.FXML
    private Pane buttonPane11;
    @javafx.fxml.FXML
    private TextArea TA_Info;
    @javafx.fxml.FXML
    private Pane graphPane;

    private AdjacencyMatrixGraph graph;
    private Map<Integer, StackPane> visualNodes = new HashMap<>();
    private List<AristaVisual> visualEdges = new ArrayList<>();
    private final double RADIUS = 120.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 18.0;
    private Random random = new Random();

    @javafx.fxml.FXML
    public void initialize() {
        graph = new AdjacencyMatrixGraph(25);
        infoLabel.setText("Matriz de Adyacencia - Operaciones automáticas");
        TA_Info.setWrapText(true);
        TA_Info.setEditable(false);
    }

    @javafx.fxml.FXML
    public void addEdgesNWeightsOnAction(ActionEvent actionEvent) {
        if (graph.counter < 2) {
            updateTextArea("Necesitas al menos 2 vértices para agregar aristas.");
            return;
        }

        try {
            // Obtener vértices existentes
            List<Integer> existingVertices = new ArrayList<>();
            for (int i = 0; i < graph.counter; i++) {
                existingVertices.add((Integer) graph.vertexList[i].data);
            }

            // Agregar 3-6 aristas aleatorias
            int edgesToAdd = random.nextInt(4) + 3;
            int actualAdded = 0;

            for (int i = 0; i < edgesToAdd; i++) {
                Integer source = existingVertices.get(random.nextInt(existingVertices.size()));
                Integer target = existingVertices.get(random.nextInt(existingVertices.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = random.nextInt(50) + 1;
                    graph.addEdgeWeight(source, target, weight);
                    actualAdded++;
                }
            }

            updateTextArea("Se agregaron " + actualAdded + " aristas con pesos aleatorios (1-50).");
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al agregar aristas: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {
        graph.clear();
        clearVisualElements();
        updateTextArea("Grafo completamente limpiado.");
    }

    @javafx.fxml.FXML
    public void removeEdgesNWeightsOnAction(ActionEvent actionEvent) {
        if (graph.counter < 2) {
            updateTextArea("No hay suficientes vértices para remover aristas.");
            return;
        }

        try {
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < graph.counter; i++) {
                vertices.add((Integer) graph.vertexList[i].data);
            }

            int edgesRemoved = 0;
            int attempts = 5;

            for (int i = 0; i < attempts; i++) {
                Integer source = vertices.get(random.nextInt(vertices.size()));
                Integer target = vertices.get(random.nextInt(vertices.size()));

                if (!source.equals(target) && graph.containsEdge(source, target)) {
                    graph.removeEdge(source, target);
                    edgesRemoved++;
                }
            }

            updateTextArea("Se removieron " + edgesRemoved + " aristas del grafo.");
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al remover aristas: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
            Integer newVertex;
            do {
                newVertex = random.nextInt(100);
            } while (graph.containsVertex(newVertex));

            graph.addVertex(newVertex);
            updateTextArea("Vértice agregado automáticamente: " + newVertex);
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al agregar vértice: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
                return;
            }

            List<Integer> vertices = getVerticesList();
            Integer vertexToRemove = vertices.get(random.nextInt(vertices.size()));

            graph.removeVertex(vertexToRemove);
            updateTextArea("Vértice removido automáticamente: " + vertexToRemove);
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al remover vértice: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        graph.clear();
        clearVisualElements();

        try {
            // Generar 8-12 números aleatorios únicos
            Set<Integer> numbers = new HashSet<>();
            while (numbers.size() < 10) {
                numbers.add(random.nextInt(100));
            }

            // Agregar vértices
            for (Integer number : numbers) {
                graph.addVertex(number);
            }

            // Agregar aristas aleatorias
            List<Integer> vertexList = new ArrayList<>(numbers);
            int numEdges = random.nextInt(12) + 8;

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = random.nextInt(50) + 1;
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            updateTextArea("Grafo aleatorizado con " + numbers.size() + " números y conexiones aleatorias.");
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al aleatorizar: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void pressedRandom(Event event) {
        infoLabel.setText("Generando operación aleatoria...");
    }

    private void visualizeGraph() {
        clearVisualElements();

        try {
            if (graph.isEmpty()) return;

            List<Integer> vertices = getVerticesList();

            // Crear nodos visuales en disposición circular
            for (int i = 0; i < vertices.size(); i++) {
                Integer vertex = vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y);
                visualNodes.put(vertex, node);
                graphPane.getChildren().add(node);
            }

            // Para las aristas, extraemos la información del toString() del grafo
            String graphString = graph.toString();
            String[] lines = graphString.split("\n");

            for (String line : lines) {
                if (line.contains("There is edge between the vertexes:") && line.contains("WEIGHT:")) {
                    try {
                        // Formato: "There is edge between the vertexes: 12....34_____WEIGHT: 25"
                        String[] parts = line.split("There is edge between the vertexes:")[1].split("_____WEIGHT:");
                        if (parts.length == 2) {
                            String vertexPart = parts[0].trim();
                            String weightStr = parts[1].trim();

                            // Extraer source y target de "12....34"
                            String[] vertexParts = vertexPart.split("\\.\\.\\.");
                            if (vertexParts.length == 2) {
                                try {
                                    Integer source = Integer.parseInt(vertexParts[0].trim());
                                    Integer target = Integer.parseInt(vertexParts[1].trim());

                                    StackPane sourceNode = visualNodes.get(source);
                                    StackPane targetNode = visualNodes.get(target);

                                    if (sourceNode != null && targetNode != null && !source.equals(target)) {
                                        createVisualEdge(sourceNode, targetNode, weightStr);
                                    }
                                } catch (NumberFormatException e) {
                                    // Ignorar errores de parsing
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar errores de parsing individuales
                    }
                }
            }

            infoLabel.setText("Grafo visualizado con " + vertices.size() + " vértices.");

        } catch (ListException e) {
            updateTextArea("Error visualizando grafo: " + e.getMessage());
        }
    }

    private StackPane createVisualNode(String text, double x, double y) {
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.DARKBLUE);
        circle.setStrokeWidth(2);

        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setTextFill(Color.BLACK);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, label);
        stack.setLayoutX(x - NODE_RADIUS);
        stack.setLayoutY(y - NODE_RADIUS);
        stack.setAlignment(Pos.CENTER);

        // Efectos de hover
        stack.setOnMouseEntered(e -> {
            circle.setStrokeWidth(3);
            stack.setScaleX(1.1);
            stack.setScaleY(1.1);
        });

        stack.setOnMouseExited(e -> {
            circle.setStrokeWidth(2);
            stack.setScaleX(1.0);
            stack.setScaleY(1.0);
        });

        return stack;
    }

    private void createVisualEdge(StackPane source, StackPane target, Object weight) {
        double startX = source.getLayoutX() + NODE_RADIUS;
        double startY = source.getLayoutY() + NODE_RADIUS;
        double endX = target.getLayoutX() + NODE_RADIUS;
        double endY = target.getLayoutY() + NODE_RADIUS;

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);

        String weightText = weight.toString();

        line.setOnMouseClicked(e -> {
            infoLabel.setText("Peso de la arista: " + weightText);
            line.setStroke(Color.RED);
        });

        line.setOnMouseEntered(e -> line.setStrokeWidth(4));
        line.setOnMouseExited(e -> line.setStrokeWidth(2));

        AristaVisual arista = new AristaVisual(line, Integer.parseInt(weightText), source, target);
        visualEdges.add(arista);
        graphPane.getChildren().add(0, line);
    }

    private void clearVisualElements() {
        graphPane.getChildren().removeAll(visualNodes.values());
        for (AristaVisual edge : visualEdges) {
            graphPane.getChildren().remove(edge.getLinea());
        }
        visualNodes.clear();
        visualEdges.clear();
    }

    private void updateTextArea(String message) {
        TA_Info.appendText(new Date() + ": " + message + "\n");
    }

    private List<Integer> getVerticesList() throws ListException {
        List<Integer> vertices = new ArrayList<>();

        // Usamos toString() del grafo para extraer los vértices
        String graphString = graph.toString();
        String[] lines = graphString.split("\n");

        for (String line : lines) {
            if (line.contains("The vextex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    String vertexStr = parts[1].trim();
                    try {
                        Integer vertex = Integer.parseInt(vertexStr);
                        vertices.add(vertex);
                    } catch (NumberFormatException e) {
                        // Ignorar si no es un número válido
                    }
                }
            }
        }

        return vertices;
    }
}