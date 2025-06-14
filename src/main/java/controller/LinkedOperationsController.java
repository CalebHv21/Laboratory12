package controller;

import java.util.regex.Pattern;
import domain.SinglyLinkedListGraph;
import domain.GraphException;
import domain.Vertex;
import domain.EdgeWeight;
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

public class LinkedOperationsController {
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

    private SinglyLinkedListGraph graph;
    private Map<String, StackPane> visualNodes = new HashMap<>();
    private List<AristaVisual> visualEdges = new ArrayList<>();
    private final double RADIUS = 130.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 25.0;
    private Random random = new Random();

    private final String[] HISTORICAL_NAMES = {
            "Napoleon", "Caesar", "Cleopatra", "Alexander", "Gandhi", "Lincoln",
            "Churchill", "Washington", "Jefferson", "Franklin", "Mozart", "Beethoven",
            "Shakespeare", "Leonardo", "Michelangelo", "Galileo", "Newton", "Einstein",
            "Columbus", "Marco Polo", "Hannibal", "Spartacus", "Plato", "Aristotle"
    };

    @javafx.fxml.FXML
    public void initialize() {
        graph = new SinglyLinkedListGraph();
        infoLabel.setText("Lista Enlazada - Operaciones con personajes históricos");
        TA_Info.setWrapText(true);
        TA_Info.setEditable(false);
    }

    @javafx.fxml.FXML
    public void addEdgesNWeightsOnAction(ActionEvent actionEvent) {
        try {
            if (graph.size() < 2) {
                updateTextArea("Necesitas al menos 2 vértices para agregar aristas.");
                return;
            }

            List<String> vertices = getVerticesList();
            int edgesToAdd = random.nextInt(3) + 2;
            int actualAdded = 0;

            for (int i = 0; i < edgesToAdd; i++) {
                String source = vertices.get(random.nextInt(vertices.size()));
                String target = vertices.get(random.nextInt(vertices.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = random.nextInt(1001) + 1000; // 1000-2000
                    graph.addEdgeWeight(source, target, weight);
                    actualAdded++;
                }
            }

            updateTextArea("Se agregaron " + actualAdded + " aristas con pesos (1000-2000).");
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al agregar aristas: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {
        graph.clear();
        clearVisualElements();
        updateTextArea("Grafo de personajes históricos completamente limpiado.");
    }

    @javafx.fxml.FXML
    public void removeEdgesNWeightsOnAction(ActionEvent actionEvent) {
        try {
            if (graph.size() < 2) {
                updateTextArea("No hay suficientes vértices para remover aristas.");
                return;
            }

            List<String> vertices = getVerticesList();
            int edgesRemoved = 0;
            int attempts = 4;

            for (int i = 0; i < attempts; i++) {
                String source = vertices.get(random.nextInt(vertices.size()));
                String target = vertices.get(random.nextInt(vertices.size()));

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
            String newName;
            do {
                newName = HISTORICAL_NAMES[random.nextInt(HISTORICAL_NAMES.length)];
            } while (graph.containsVertex(newName));

            graph.addVertex(newName);
            updateTextArea("Personaje histórico agregado: " + newName);
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al agregar personaje: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
                return;
            }

            List<String> vertices = getVerticesList();
            String vertexToRemove = vertices.get(random.nextInt(vertices.size()));

            graph.removeVertex(vertexToRemove);
            updateTextArea("Personaje histórico removido: " + vertexToRemove);
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al remover personaje: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        graph.clear();
        clearVisualElements();

        try {
            // Generar 8-10 personajes históricos únicos
            Set<String> names = new HashSet<>();
            while (names.size() < 10) {
                names.add(HISTORICAL_NAMES[random.nextInt(HISTORICAL_NAMES.length)]);
            }

            // Agregar vértices
            for (String name : names) {
                graph.addVertex(name);
            }

            // Agregar aristas aleatorias con pesos altos
            List<String> namesList = new ArrayList<>(names);
            int numEdges = random.nextInt(8) + 6;

            for (int i = 0; i < numEdges; i++) {
                String source = namesList.get(random.nextInt(namesList.size()));
                String target = namesList.get(random.nextInt(namesList.size()));

                if (!source.equals(target) && !graph.containsEdge(source, target)) {
                    int weight = random.nextInt(1001) + 1000;
                    graph.addEdgeWeight(source, target, weight);
                }
            }

            updateTextArea("Grafo aleatorizado con " + names.size() + " personajes históricos.");
            visualizeGraph();

        } catch (GraphException | ListException e) {
            updateTextArea("Error al aleatorizar: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void pressedRandom(Event event) {
        infoLabel.setText("Ejecutando operación automática...");
    }

    private List<String> getVerticesList() throws ListException {
        List<String> vertices = new ArrayList<>();

        // Usamos toString() del grafo para extraer los vértices
        String graphString = graph.toString();
        String[] lines = graphString.split("\n");

        for (String line : lines) {
            if (line.contains("The vertex in the position") && line.contains("is:")) {
                String[] parts = line.split("is:");
                if (parts.length > 1) {
                    String vertexName = parts[1].trim();
                    if (!vertexName.isEmpty()) {
                        vertices.add(vertexName);
                    }
                }
            }
        }

        return vertices;
    }

    private void visualizeGraph() {
        clearVisualElements();

        try {
            if (graph.isEmpty()) return;

            List<String> vertices = getVerticesList();

            // Crear nodos visuales en disposición circular
            for (int i = 0; i < vertices.size(); i++) {
                String vertex = vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex, x, y);
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
                            String target = parts[0].trim();
                            String weightStr = parts[1].trim();

                            // Encontrar el vértice fuente buscando en las líneas anteriores
                            String source = findSourceVertex(lines, line);

                            if (source != null && !source.equals(target)) {
                                StackPane sourceNode = visualNodes.get(source);
                                StackPane targetNode = visualNodes.get(target);

                                if (sourceNode != null && targetNode != null) {
                                    createVisualEdge(sourceNode, targetNode, weightStr);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar errores de parsing individuales
                    }
                }
            }

        } catch (ListException e) {
            updateTextArea("Error visualizando grafo: " + e.getMessage());
        }
    }

    private String findSourceVertex(String[] lines, String targetLine) {
        // Buscar hacia atrás para encontrar el vértice fuente
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals(targetLine)) {
                // Buscar hacia atrás la línea que contiene "The vertex in the position"
                for (int j = i - 1; j >= 0; j--) {
                    if (lines[j].contains("The vertex in the position") && lines[j].contains("is:")) {
                        String[] parts = lines[j].split("is:");
                        if (parts.length > 1) {
                            return parts[1].trim();
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
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.DARKGREEN);
        circle.setStrokeWidth(2);

        // Truncar nombres largos para visualización
        String displayText = text.length() > 8 ? text.substring(0, 8) : text;
        Label label = new Label(displayText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
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
            infoLabel.setText("Personaje: " + text);
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
        line.setStroke(Color.DARKGREEN);
        line.setStrokeWidth(2);

        String weightText = weight != null ? weight.toString() : "1000";

        line.setOnMouseClicked(e -> {
            infoLabel.setText("Peso de la conexión: " + weightText);
            line.setStroke(Color.GOLD);
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
}