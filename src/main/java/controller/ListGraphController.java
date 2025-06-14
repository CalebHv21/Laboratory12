package controller;

import domain.AdjacencyListGraph;
import domain.GraphException;
import domain.Vertex;
import domain.EdgeWeight;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
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
import javafx.util.Duration;
import util.AristaVisual;

import java.util.*;

public class ListGraphController {
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

    private AdjacencyListGraph graph;
    private Map<Character, StackPane> visualNodes = new HashMap<>();
    private List<AristaVisual> visualEdges = new ArrayList<>();
    private final double RADIUS = 120.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 20.0;
    private Random random = new Random();

    @javafx.fxml.FXML
    public void initialize() {
        graph = new AdjacencyListGraph(30);
        infoLabel.setText("Lista de Adyacencia - Letras mayúsculas");
        TA_Info.setWrapText(true);
        TA_Info.setEditable(false);

        // Cargar grafo inicial
        loadInitialGraph();
    }

    private void loadInitialGraph() {
        try {
            // Generar 10 letras aleatorias únicas
            Set<Character> letters = new HashSet<>();
            while (letters.size() < 8) {
                letters.add((char) ('A' + random.nextInt(26)));
            }

            // Agregar vértices
            for (Character letter : letters) {
                graph.addVertex(letter);
            }

            // Agregar aristas aleatorias
            List<Character> vertexList = new ArrayList<>(letters);
            int numEdges = random.nextInt(8) + 6;

            for (int i = 0; i < numEdges; i++) {
                Character source = vertexList.get(random.nextInt(vertexList.size()));
                Character target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = util.Utility.random(40) + 1;
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeGraph();
            updateTextArea("Grafo inicial cargado con letras mayúsculas.");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo inicial: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void toStringOnAction(ActionEvent actionEvent) {
        if (graph.isEmpty()) {
            updateTextArea("El grafo está vacío.");
        } else {
            updateTextArea("=== INFORMACIÓN DEL GRAFO ===\n" + graph.toString());
        }
    }

    @javafx.fxml.FXML
    public void dfsTourOnAction(ActionEvent actionEvent) {
        if (graph.isEmpty()) {
            updateTextArea("El grafo está vacío. No se puede realizar DFS.");
            return;
        }

        try {
            String dfsResult = graph.dfs();
            updateTextArea("DFS Tour: " + dfsResult);
            animateTraversal(dfsResult, Color.LIGHTGREEN);
        } catch (GraphException | StackException | ListException e) {
            updateTextArea("Error en DFS: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        graph.clear();
        clearVisualElements();

        try {
            // Generar 10 letras aleatorias únicas
            Set<Character> letters = new HashSet<>();
            while (letters.size() < 10) {
                letters.add((char) ('A' + random.nextInt(26)));
            }

            // Agregar vértices
            for (Character letter : letters) {
                graph.addVertex(letter);
            }

            // Agregar aristas aleatorias
            List<Character> vertexList = new ArrayList<>(letters);
            int numEdges = random.nextInt(10) + 8;

            for (int i = 0; i < numEdges; i++) {
                Character source = vertexList.get(random.nextInt(vertexList.size()));
                Character target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = util.Utility.random(40) + 1;
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeGraph();
            updateTextArea("Grafo aleatorizado con letras mayúsculas (A-Z).");

        } catch (GraphException | ListException e) {
            updateTextArea("Error al aleatorizar: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void pressedRandom(Event event) {
        infoLabel.setText("Ejecutando operación...");
    }

    @javafx.fxml.FXML
    public void bfsTourOnAction(ActionEvent actionEvent) {
        if (graph.isEmpty()) {
            updateTextArea("El grafo está vacío. No se puede realizar BFS.");
            return;
        }

        try {
            String bfsResult = graph.bfs();
            updateTextArea("BFS Tour: " + bfsResult);
            animateTraversal(bfsResult, Color.LIGHTYELLOW);
        } catch (GraphException | QueueException | ListException e) {
            updateTextArea("Error en BFS: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void containsEdgeOnAction(ActionEvent actionEvent) {
        try {
            if (graph.size() < 2) {
                updateTextArea("Necesitas al menos 2 vértices para verificar aristas.");
                return;
            }

            List<Character> vertices = getVerticesList();
            Character vertex1 = vertices.get(random.nextInt(vertices.size()));
            Character vertex2 = vertices.get(random.nextInt(vertices.size()));

            boolean hasEdge = graph.containsEdge(vertex1, vertex2);
            updateTextArea("¿Existe arista entre " + vertex1 + " y " + vertex2 + "? " + hasEdge);

        } catch (GraphException | ListException e) {
            updateTextArea("Error verificando arista: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
                return;
            }

            List<Character> vertices = getVerticesList();
            Character vertex = vertices.get(random.nextInt(vertices.size()));

            boolean hasVertex = graph.containsVertex(vertex);
            updateTextArea("¿Contiene el vértice " + vertex + "? " + hasVertex);

            // Destacar el vértice verificado
            if (hasVertex && visualNodes.containsKey(vertex)) {
                highlightNode(visualNodes.get(vertex), Color.YELLOW);
            }

        } catch (GraphException | ListException e) {
            updateTextArea("Error verificando vértice: " + e.getMessage());
        }
    }

    private void visualizeGraph() {
        clearVisualElements();

        try {
            if (graph.isEmpty()) return;

            List<Character> vertices = getVerticesList();

            // Crear nodos visuales en disposición circular
            for (int i = 0; i < vertices.size(); i++) {
                Character vertex = vertices.get(i);
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
                if (line.contains("Edge=") && line.contains("Weight=")) {
                    try {
                        // Extraer información de arista del formato "Edge=target. Weight=peso"
                        String[] parts = line.split("Edge=")[1].split("\\. Weight=");
                        if (parts.length == 2) {
                            String targetStr = parts[0].trim();
                            String weightStr = parts[1].trim();

                            if (targetStr.length() == 1) {
                                Character target = targetStr.charAt(0);

                                // Encontrar el vértice fuente buscando en las líneas anteriores
                                Character source = findSourceVertex(lines, line);

                                if (source != null && !source.equals(target)) {
                                    StackPane sourceNode = visualNodes.get(source);
                                    StackPane targetNode = visualNodes.get(target);

                                    if (sourceNode != null && targetNode != null) {
                                        createVisualEdge(sourceNode, targetNode, weightStr);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar errores de parsing individuales
                    }
                }
            }

            infoLabel.setText("Grafo visualizado con " + vertices.size() + " letras.");

        } catch (ListException e) {
            updateTextArea("Error visualizando grafo: " + e.getMessage());
        }
    }

    private Character findSourceVertex(String[] lines, String targetLine) {
        // Buscar hacia atrás para encontrar el vértice fuente
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals(targetLine)) {
                // Buscar hacia atrás la línea que contiene "The vextex in the position"
                for (int j = i - 1; j >= 0; j--) {
                    if (lines[j].contains("The vextex in the position") && lines[j].contains("is:")) {
                        String[] parts = lines[j].split("is:");
                        if (parts.length > 1) {
                            String vertexStr = parts[1].trim();
                            if (vertexStr.length() == 1) {
                                return vertexStr.charAt(0);
                            }
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    private StackPane createVisualNode(String text, double x, double y) {
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(Color.LIGHTCORAL);
        circle.setStroke(Color.DARKRED);
        circle.setStrokeWidth(2);

        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);

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
        line.setStroke(Color.DARKRED);
        line.setStrokeWidth(2);

        String weightText = weight != null ? weight.toString() : "1";

        line.setOnMouseClicked(e -> {
            infoLabel.setText("Peso de la arista: " + weightText);
            line.setStroke(Color.ORANGE);
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

    private void highlightNode(StackPane node, Color color) {
        Circle circle = (Circle) node.getChildren().get(0);
        FillTransition ft = new FillTransition(Duration.millis(500), circle);
        ft.setToValue(color);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    private void animateTraversal(String traversalResult, Color color) {
        String[] parts = traversalResult.split(",");
        SequentialTransition animation = new SequentialTransition();

        for (String part : parts) {
            String vertexName = part.trim();
            Character vertex = vertexName.length() > 0 ? vertexName.charAt(0) : null;
            if (vertex != null) {
                StackPane node = visualNodes.get(vertex);
                if (node != null) {
                    Circle circle = (Circle) node.getChildren().get(0);
                    FillTransition ft = new FillTransition(Duration.millis(800), circle);
                    ft.setToValue(color);
                    animation.getChildren().add(ft);
                }
            }
        }

        animation.play();
    }

    private void updateTextArea(String message) {
        TA_Info.appendText(new Date() + ": " + message + "\n");
    }

    private List<Character> getVerticesList() throws ListException {
        List<Character> vertices = new ArrayList<>();

        // Usamos toString() del grafo para extraer los vértices
        String graphString = graph.toString();
        String[] lines = graphString.split("\n");

        for (String line : lines) {
            if (line.contains("The vextex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    String vertexStr = parts[1].trim();
                    if (!vertexStr.isEmpty() && vertexStr.length() == 1) {
                        vertices.add(vertexStr.charAt(0));
                    }
                }
            }
        }

        return vertices;
    }
}