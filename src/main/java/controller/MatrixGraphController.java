package controller;

import domain.AdjacencyMatrixGraph;
import domain.GraphException;
import domain.Vertex;
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

public class MatrixGraphController {

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

    public void initialize() {
        graph = new AdjacencyMatrixGraph(15);
        infoLabel.setText("Clickee en una arista para ver su peso");
        TA_Info.setWrapText(true);
        TA_Info.setEditable(false);

        // Cargar grafo inicial
        loadInitialGraph();
    }

    private void loadInitialGraph() {
        try {
            // Generar 8 números aleatorios únicos
            Set<Integer> usedNumbers = new HashSet<>();
            for (int i = 0; i < 8; i++) {
                int randomNum;
                do {
                    randomNum = util.Utility.random(0, 99);
                } while (usedNumbers.contains(randomNum));

                usedNumbers.add(randomNum);
                graph.addVertex(randomNum);
            }

            // Agregar aristas aleatorias
            List<Integer> vertices = new ArrayList<>(usedNumbers);
            int numEdges = random.nextInt(8) + 6;

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertices.get(random.nextInt(vertices.size()));
                Integer target = vertices.get(random.nextInt(vertices.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = util.Utility.random(1, 50);
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeGraph();
            updateTextArea("Grafo inicial cargado con " + usedNumbers.size() + " vértices.");

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
            // Generar 10 números aleatorios únicos
            Set<Integer> numbers = new HashSet<>();
            while (numbers.size() < 10) {
                numbers.add(util.Utility.random(0, 99));
            }

            // Agregar vértices
            for (Integer number : numbers) {
                graph.addVertex(number);
            }

            // Agregar aristas aleatorias
            List<Integer> vertexList = new ArrayList<>(numbers);
            int numEdges = random.nextInt(10) + 8;

            for (int i = 0; i < numEdges; i++) {
                Integer source = vertexList.get(random.nextInt(vertexList.size()));
                Integer target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = util.Utility.random(1, 50);
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeGraph();
            updateTextArea("Grafo aleatorizado con " + numbers.size() + " números (0-99).");

        } catch (GraphException | ListException e) {
            updateTextArea("Error al aleatorizar: " + e.getMessage());
        }
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

            List<Integer> vertices = getVerticesList();
            Integer vertex1 = vertices.get(random.nextInt(vertices.size()));
            Integer vertex2 = vertices.get(random.nextInt(vertices.size()));

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

            List<Integer> vertices = getVerticesList();
            Integer vertex = vertices.get(random.nextInt(vertices.size()));

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

    @javafx.fxml.FXML
    public void pressedRandom(Event event) {
        infoLabel.setText("Ejecutando operación...");
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

    private void highlightNode(StackPane node, Color color) {
        Circle circle = (Circle) node.getChildren().get(0);
        FillTransition ft = new FillTransition(Duration.millis(500), circle);
        ft.setToValue(color);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    private void animateTraversal(String traversalResult, Color color) {
        String[] vertices = traversalResult.replace(" ", "").split(",");
        SequentialTransition animation = new SequentialTransition();

        for (String vertexStr : vertices) {
            if (!vertexStr.trim().isEmpty()) {
                try {
                    Integer vertex = Integer.parseInt(vertexStr.trim());
                    StackPane node = visualNodes.get(vertex);
                    if (node != null) {
                        Circle circle = (Circle) node.getChildren().get(0);
                        FillTransition ft = new FillTransition(Duration.millis(800), circle);
                        ft.setToValue(color);
                        animation.getChildren().add(ft);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si no es un número válido
                }
            }
        }

        animation.play();
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