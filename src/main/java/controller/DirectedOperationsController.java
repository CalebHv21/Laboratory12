package controller;

import domain.*;
import domain.list.ListException;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
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

public class DirectedOperationsController {


    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private RadioButton linkedlistRB;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Label infoLabel;
    @javafx.fxml.FXML
    private Pane buttonPane11;
    @javafx.fxml.FXML
    private TextArea TA_Info;
    @javafx.fxml.FXML
    private RadioButton listRB;
    @javafx.fxml.FXML
    private RadioButton matrixRB;
    @javafx.fxml.FXML
    private Pane graphPane;

    //Grafos
    private DirectedAdjacencyMatrixGraph matrixGraph = new DirectedAdjacencyMatrixGraph(10);
    private DirectedAdjacencyListGraph adlistGraph = new DirectedAdjacencyListGraph(10);
    private DirectedSinglyLinkedListGraph linkedListGraph = new DirectedSinglyLinkedListGraph();

    private ToggleGroup graphGroup;
    private Graph graphSelected;

    //Monumentos históricos:
    private final String[] monumentos = {
            "La Gran Muralla China", "La Torre Eiffel", "El Coliseo Romano ",
            "La Estatua de la Libertad ", "La Alhambra ", "Stonehenge", "Machu Picchu",
            "La Pirámide de Giza ", "El Taj Mahal", "El Partenón", "Petra ",
            "Angkor Wat ", "El Kremlin y la Plaza Roja", "La Sagrada Familia", "Chichén Itzá",
            "Moáis de la Isla de Pascua ", "Castillo de Edimburgo ", "Castillo de Neuschwanstein ",
            "Monte Rushmore", "El Palacio de Versalles", "La Catedral de San Basilio", "La Mezquita Azul ",
            "La Torre de Pisa", "El Obelisco de Buenos Aires", "El Arco del Triunfo"
    };


    private Map<String, StackPane> visualNodes = new HashMap<>();
    private Map<Character, StackPane> visualNodes2 = new HashMap<>();
    private List<AristaVisual> visualEdges = new ArrayList<>();
    private final double RADIUS = 130.0;
    private final double CENTER_X = 250.0;
    private final double CENTER_Y = 280.0;
    private final double NODE_RADIUS = 25.0;
    private Random random = new Random();


    @javafx.fxml.FXML
    public void initialize() {


        infoLabel.setText("Seleccione una opción de grafo");
        TA_Info.setWrapText(true);
        TA_Info.setEditable(false);

        matrixGraph = new DirectedAdjacencyMatrixGraph(10);
        adlistGraph = new DirectedAdjacencyListGraph(10);
        linkedListGraph = new DirectedSinglyLinkedListGraph();


    }



    @javafx.fxml.FXML
    public void addEdgesNWeightsOnAction(ActionEvent actionEvent) {

        if(linkedlistRB.isSelected()){

            try {
                if (linkedListGraph.size() < 2) {
                    updateTextArea("Necesitas al menos 2 vértices para agregar aristas.");
                    return;
                }

                List<String> vertices = getVerticesLinkedList();
                int edgesToAdd = random.nextInt(3) + 2;
                int actualAdded = 0;

                for (int i = 0; i < edgesToAdd; i++) {
                    String source = vertices.get(random.nextInt(vertices.size()));
                    String target = vertices.get(random.nextInt(vertices.size()));

                    if (!source.equals(target) && !linkedListGraph.containsEdge(source, target)) {
                        int weight = util.Utility.random(101,150); //peso 101-150
                        linkedListGraph.addEdgeWeight(source, target, weight);
                        actualAdded++;
                    }
                }

                updateTextArea("Se agregaron " + actualAdded + " aristas con pesos (101-150).");
                visualizeLinkedListGraphGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar aristas: " + e.getMessage());
            }

        }else if(listRB.isSelected()){

            try {
                if (adlistGraph.size() < 2) {
                    updateTextArea("Necesitas al menos 2 vértices para agregar aristas.");
                    return;
                }

                List<Character> existingVertices = getVerticesADList();

                // Agregar 3-5 aristas aleatorias
                int edgesToAdd = random.nextInt(3) + 3;
                int actualAdded = 0;

                for (int i = 0; i < edgesToAdd; i++) {
                    Character source = existingVertices.get(random.nextInt(existingVertices.size()));
                    Character target = existingVertices.get(random.nextInt(existingVertices.size()));

                    if (!source.equals(target) && !adlistGraph.containsEdge(source, target)) {
                        int weight = util.Utility.random(51,100);
                        adlistGraph.addEdgeWeight(source, target, weight);
                        actualAdded++;
                    }
                }

                updateTextArea("Se agregaron " + actualAdded + " aristas con pesos aleatorios (51-100).");
                visualizeADListGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar aristas: " + e.getMessage());
            }


        }else if(matrixRB.isSelected()){

            if (matrixGraph.counter < 2) {
                updateTextArea("Necesitas al menos 2 vértices para agregar aristas.");
                return;
            }

            try {
                // Obtener vértices existentes
                List<Integer> existingVertices = new ArrayList<>();
                for (int i = 0; i < matrixGraph.counter; i++) {
                    existingVertices.add((Integer) matrixGraph.vertexList[i].data);
                }

                // Agregar 3-6 aristas aleatorias
                int edgesToAdd = random.nextInt(4) + 3;
                int actualAdded = 0;

                for (int i = 0; i < edgesToAdd; i++) {
                    Integer source = existingVertices.get(random.nextInt(existingVertices.size()));
                    Integer target = existingVertices.get(random.nextInt(existingVertices.size()));

                    if (!source.equals(target) && !matrixGraph.containsEdge(source, target)) {
                        int weight = random.nextInt(50) + 1;
                        matrixGraph.addEdgeWeight(source, target, weight);
                        actualAdded++;
                    }
                }

                updateTextArea("Se agregaron " + actualAdded + " aristas con pesos aleatorios (1-50).");
                visualizeMatrixGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar aristas: " + e.getMessage());
            }


        }else{
            util.FXUtility.showErrorAlert("Error", "Seleccione una opción de grafo");
        }



    }

    @javafx.fxml.FXML
    public void clearOnAction(ActionEvent actionEvent) {

        linkedlistRB.setSelected(false);
        listRB.setSelected(false);
        matrixRB.setSelected(false);

        matrixGraph.clear();
        adlistGraph.clear();
        linkedListGraph.clear();
        clearVisualADListElements();
        clearVisualMatrixElements();
        clearVisualLinkedElements();
        updateTextArea("Grafo de personajes completamente limpiado.");
    }

    @javafx.fxml.FXML
    public void removeEdgesNWeightsOnAction(ActionEvent actionEvent) {

        if(linkedlistRB.isSelected()){

            try {
                if (linkedListGraph.size() < 2) {
                    updateTextArea("No hay suficientes vértices para remover aristas.");
                    return;
                }

                List<String> vertices = getVerticesLinkedList();
                int edgesRemoved = 0;
                int attempts = 4;

                for (int i = 0; i < attempts; i++) {
                    String source = vertices.get(random.nextInt(vertices.size()));
                    String target = vertices.get(random.nextInt(vertices.size()));

                    if (!source.equals(target) && linkedListGraph.containsEdge(source, target)) {
                        linkedListGraph.removeEdge(source, target);
                        edgesRemoved++;
                    }
                }

                updateTextArea("Se removieron " + edgesRemoved + " aristas del grafo.");
                visualizeLinkedListGraphGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover aristas: " + e.getMessage());
            }


        }else if(listRB.isSelected()){


            try {
                if (adlistGraph.size() < 2) {
                    updateTextArea("No hay suficientes vértices para remover aristas.");
                    return;
                }

                List<Character> vertices = getVerticesADList();

                int edgesRemoved = 0;
                int attempts = 5;

                for (int i = 0; i < attempts; i++) {
                    Character source = vertices.get(random.nextInt(vertices.size()));
                    Character target = vertices.get(random.nextInt(vertices.size()));

                    if (!source.equals(target) && adlistGraph.containsEdge(source, target)) {
                        adlistGraph.removeEdge(source, target);
                        edgesRemoved++;
                    }
                }

                updateTextArea("Se removieron " + edgesRemoved + " aristas del grafo.");
                visualizeADListGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover aristas: " + e.getMessage());
            }





        }else if(matrixRB.isSelected()){


            if (matrixGraph.counter < 2) {
                updateTextArea("No hay suficientes vértices para remover aristas.");
                return;
            }

            try {
                List<Integer> vertices = new ArrayList<>();
                for (int i = 0; i < matrixGraph.counter; i++) {
                    vertices.add((Integer) matrixGraph.vertexList[i].data);
                }

                int edgesRemoved = 0;
                int attempts = 5;

                for (int i = 0; i < attempts; i++) {
                    Integer source = vertices.get(random.nextInt(vertices.size()));
                    Integer target = vertices.get(random.nextInt(vertices.size()));

                    if (!source.equals(target) && matrixGraph.containsEdge(source, target)) {
                        matrixGraph.removeEdge(source, target);
                        edgesRemoved++;
                    }
                }

                updateTextArea("Se removieron " + edgesRemoved + " aristas del grafo.");
                visualizeMatrixGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover aristas: " + e.getMessage());
            }

        }else{
            util.FXUtility.showErrorAlert("Error", "Seleccione una opción de grafo");
        }

    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {

        if(linkedlistRB.isSelected()){

            try {
                String newName;
                do {
                    newName = monumentos[random.nextInt(monumentos.length)];
                } while (linkedListGraph.containsVertex(newName));

                linkedListGraph.addVertex(newName);
                updateTextArea("Monumento histórico agregado: " + newName);
                visualizeLinkedListGraphGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar personaje: " + e.getMessage());
            }


        }else if(listRB.isSelected()){

            try {
                Character newVertex;
                do {
                    newVertex = (char) ('A' + random.nextInt(26));
                } while (adlistGraph.containsVertex(newVertex));

                adlistGraph.addVertex(newVertex);
                updateTextArea("Vértice agregado automáticamente: " + newVertex);
                visualizeADListGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar vértice: " + e.getMessage());
            }


        }else if(matrixRB.isSelected()){

            try {
                Integer newVertex;
                do {
                    newVertex = random.nextInt(100);
                } while (matrixGraph.containsVertex(newVertex));

                matrixGraph.addVertex(newVertex);
                updateTextArea("Vértice agregado automáticamente: " + newVertex);
                visualizeMatrixGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al agregar vértice: " + e.getMessage());
            }


        }else{
            util.FXUtility.showErrorAlert("Error", "Seleccione una opción de grafo");
        }





    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {

        if(linkedlistRB.isSelected()){
            try {
                if (linkedListGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<String> vertices = getVerticesLinkedList();
                String vertexToRemove = vertices.get(random.nextInt(vertices.size()));

                linkedListGraph.removeVertex(vertexToRemove);
                updateTextArea("Monumento histórico removido: " + vertexToRemove);
                visualizeLinkedListGraphGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover monumento: " + e.getMessage());
            }


        }else if(listRB.isSelected()){

            try {
                if (adlistGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<Character> vertices = getVerticesADList();
                Character vertexToRemove = vertices.get(random.nextInt(vertices.size()));

                adlistGraph.removeVertex(vertexToRemove);
                updateTextArea("Vértice removido automáticamente: " + vertexToRemove);
                visualizeADListGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover vértice: " + e.getMessage());
            }

        }else if(matrixRB.isSelected()){

            try {
                if (matrixGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<Integer> vertices = getVerticesList();
                Integer vertexToRemove = vertices.get(random.nextInt(vertices.size()));

                matrixGraph.removeVertex(vertexToRemove);
                updateTextArea("Vértice removido automáticamente: " + vertexToRemove);
                visualizeMatrixGraph();

            } catch (GraphException | ListException e) {
                updateTextArea("Error al remover vértice: " + e.getMessage());
            }



        }else{
            util.FXUtility.showErrorAlert("Error", "Seleccione una opción de grafo");
        }






    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {

        matrixGraph = new DirectedAdjacencyMatrixGraph(10);
        adlistGraph = new DirectedAdjacencyListGraph(10);
        linkedListGraph = new DirectedSinglyLinkedListGraph();

        clearVisualADListElements();
        clearVisualMatrixElements();
        clearVisualLinkedElements();

        if (listRB.isSelected()) {
            loadADListGraph();
        } else if (matrixRB.isSelected()) {
            loadMatrixGraph();
        } else if (linkedlistRB.isSelected()){
            loadLinkedListGraph();
        }else{
            util.FXUtility.showErrorAlert("Error", "Seleccione una opción");
        }


    }

    @javafx.fxml.FXML
    public void pressedRandom(Event event) {
    }

    @javafx.fxml.FXML
    public void adjacencyListOnAction(ActionEvent actionEvent) {

        initialize();
        matrixRB.setSelected(false);
        linkedlistRB.setSelected(false);

        if (listRB.isSelected()) {
            clearVisualADListElements();
            clearVisualMatrixElements();
            clearVisualLinkedElements();
            loadADListGraph();
        } else {
            clearVisualADListElements();
        }

    }

    @javafx.fxml.FXML
    public void linkedListOnAction(ActionEvent actionEvent) {

        initialize();
        matrixRB.setSelected(false);
        listRB.setSelected(false);

        if (linkedlistRB.isSelected()) {
            clearVisualADListElements();
            clearVisualMatrixElements();
            clearVisualLinkedElements();
            loadLinkedListGraph();
        } else {
            clearVisualLinkedElements();
        }
    }

    @javafx.fxml.FXML
    public void adjacencyMatrixOnAction(ActionEvent actionEvent) {

        initialize();
        listRB.setSelected(false);
        linkedlistRB.setSelected(false);

        if (matrixRB.isSelected()) {
            clearVisualADListElements();
            clearVisualMatrixElements();
            clearVisualLinkedElements();
            loadMatrixGraph();
        } else {
            clearVisualMatrixElements();
        }



    }


    /// /////////////////////////////////////////////////////////////////////////////////////////////

    private void loadMatrixGraph() {

        try {
            // Generar 10 personajes históricos únicos
            Set<Integer> numbers = new HashSet<>();
            while (numbers.size() < 10) {
                numbers.add(random.nextInt(100));  // 0 a 99
            }

            // Agregar vértices
            for (int num : numbers) {
                matrixGraph.addVertex(num);
            }

            // Convertir Set a List para obtener índices aleatorios
            List<Integer> numList = new ArrayList<>(numbers);

            // Número aleatorio de aristas entre 4 y 9
            int numEdges = random.nextInt(6) + 4;

            for (int i = 0; i < numEdges; i++) {
                int source = numList.get(random.nextInt(numList.size()));
                int target = numList.get(random.nextInt(numList.size()));

                // Verificar que no sean el mismo vértice y que no exista la arista
                if (source != target && !matrixGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(1, 50); // pesos entre 1 y 50
                    matrixGraph.addEdgeWeight(source, target, weight);
                } else {
                    i--;
                }
            }

            visualizeMatrixGraph();
            updateTextArea("Grafo Matriz Dirigido cargando...");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo matriz: " + e.getMessage());
        }
    }


    private void loadLinkedListGraph() {
        try {

            Set<String> names = new HashSet<>();
            while (names.size() < 10) {
                names.add(monumentos[random.nextInt(monumentos.length)]);
            }

            // Agregar vértices
            for (String name : names) {
                linkedListGraph.addVertex(name);
            }

            // Agregar aristas aleatorias con pesos altos
            List<String> namesList = new ArrayList<>(names);
            int numEdges = random.nextInt(6) + 4;

            for (int i = 0; i < numEdges; i++) {
                String source = namesList.get(random.nextInt(namesList.size()));
                String target = namesList.get(random.nextInt(namesList.size()));

                if (!source.equals(target) && !linkedListGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(51,100);
                    linkedListGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeLinkedListGraphGraph();
            updateTextArea("Grafo inicial cargado con monumentos históricos.");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo inicial: " + e.getMessage());
        }
    }

    private void loadADListGraph() {
        try {
            // Generar 10 letras aleatorias únicas
            Set<Character> letters = new HashSet<>();
            while (letters.size() < 10) {
                letters.add((char) ('A' + random.nextInt(26)));
            }

            // Agregar vértices
            for (Character letter : letters) {
                adlistGraph.addVertex(letter);
            }

            // Agregar aristas aleatorias
            List<Character> vertexList = new ArrayList<>(letters);
            int numEdges = random.nextInt(8) + 6;

            for (int i = 0; i < numEdges; i++) {
                Character source = vertexList.get(random.nextInt(vertexList.size()));
                Character target = vertexList.get(random.nextInt(vertexList.size()));

                if (!source.equals(target) && !adlistGraph.containsEdge(source, target)) {
                    int weight = util.Utility.random(101,150);
                    adlistGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeADListGraph();
            updateTextArea("Grafo inicial cargado con letras mayúsculas.");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo inicial: " + e.getMessage());
        }
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////

    private void visualizeADListGraph() {
        clearVisualADListElements();

        try {
            if (adlistGraph.isEmpty()) return;

            List<Character> vertices = getVerticesADList();

            // Crear nodos visuales en disposición circular
            for (int i = 0; i < vertices.size(); i++) {
                Character vertex = vertices.get(i);
                double angle = 2 * Math.PI * i / vertices.size();
                double x = CENTER_X + RADIUS * Math.cos(angle);
                double y = CENTER_Y + RADIUS * Math.sin(angle);

                StackPane node = createVisualNode(vertex.toString(), x, y);
                visualNodes2.put(vertex, node);
                graphPane.getChildren().add(node);
            }

            // Para las aristas, extraemos la información del toString() del grafo
            String graphString = adlistGraph.toString();
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
                                Character source = findSourceVertexADList(lines, line);

                                if (source != null && !source.equals(target)) {
                                    StackPane sourceNode = visualNodes2.get(source);
                                    StackPane targetNode = visualNodes2.get(target);

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


    private void visualizeLinkedListGraphGraph() {
        clearVisualLinkedElements();

        try {
            if (linkedListGraph.isEmpty()) return;

            List<String> vertices = getVerticesLinkedList();

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
            String graphString = linkedListGraph.toString();
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

            infoLabel.setText("Grafo visualizado con " + vertices.size() + " monumentos.");

        } catch (ListException e) {
            updateTextArea("Error visualizando grafo: " + e.getMessage());
        }
    }

        private void visualizeMatrixGraph() {
            clearVisualMatrixElements();

            if (matrixGraph.isEmpty()) return;

            List<String> vertices = getVerticesMatrixList();

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

            // Dibujar aristas directamente desde la matriz de adyacencia

            for (int i = 0; i < matrixGraph.counter; i++) {
                for (int j = 0; j < matrixGraph.counter; j++) {
                    Object objWeight = matrixGraph.adjacencyMatrix[i][j];
                    if (objWeight != null) {
                        int weight = 0;
                        if (objWeight instanceof Integer) {
                            weight = (Integer) objWeight;
                        } else {
                            try {
                                weight = Integer.parseInt(objWeight.toString());
                            } catch (NumberFormatException e) {
                                continue; //Pone peso 0
                            }
                        }
                        if (weight > 0) {
                            String source = String.valueOf(matrixGraph.vertexList[i].data);
                            String target = String.valueOf(matrixGraph.vertexList[j].data);

                            StackPane sourceNode = visualNodes.get(source);
                            StackPane targetNode = visualNodes.get(target);

                            if (sourceNode != null && targetNode != null) {
                                createVisualEdge(sourceNode, targetNode, weight);
                            }
                        }
                    }
                }
            }

            infoLabel.setText("Grafo visualizado con " + vertices.size() + " números");

        }


        /// //////////////////////////////////////////////////////////////////////////////////////////////////////////


        private void clearVisualLinkedElements() {
            graphPane.getChildren().removeAll(visualNodes.values());
            for (AristaVisual edge : visualEdges) {
                graphPane.getChildren().remove(edge.getLinea());
            }
            visualNodes.clear();
            visualEdges.clear();
        }

        private List<String> getVerticesLinkedList() throws ListException {
            List<String> vertices = new ArrayList<>();

            // Usamos toString() del grafo para extraer los vértices
            String graphString = linkedListGraph.toString();
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

    private Character findSourceVertexADList(String[] lines, String targetLine) {
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


    private List<Integer> getVerticesList() throws ListException {
        List<Integer> vertices = new ArrayList<>();

        // Usamos toString() del grafo para extraer los vértices
        String graphString = matrixGraph.toString();
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

    private List<String> getVerticesMatrixList() {
        List<String> vertices = new ArrayList<>();
        for (int i = 0; i < matrixGraph.counter; i++) {
            vertices.add(String.valueOf(matrixGraph.vertexList[i].data));
        }
        return vertices;
    }

    private List<Character> getVerticesADList() throws ListException {
        List<Character> vertices = new ArrayList<>();

        // Usamos toString() del grafo para extraer los vértices
        String graphString = adlistGraph.toString();
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
            infoLabel.setText("Arista: " + text);
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

    private void clearVisualMatrixElements() {
        graphPane.getChildren().removeAll(visualNodes.values());
        for (AristaVisual edge : visualEdges) {
            graphPane.getChildren().remove(edge.getLinea());
        }
        visualNodes.clear();
        visualEdges.clear();
    }

    private void clearVisualADListElements() {
        graphPane.getChildren().removeAll(visualNodes2.values());
        for (AristaVisual edge : visualEdges) {
            graphPane.getChildren().remove(edge.getLinea());
        }
        visualNodes2.clear();
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
            StackPane node = visualNodes.get(vertexName);
            if (node != null) {
                Circle circle = (Circle) node.getChildren().get(0);
                FillTransition ft = new FillTransition(Duration.millis(800), circle);
                ft.setToValue(color);
                animation.getChildren().add(ft);
            }
        }

        animation.play();
    }

    private void updateTextArea(String message) {
        TA_Info.appendText(new Date() + ": " + message + "\n");
    }


    /// ////////////////////////////////////////////////////////////////////


    }//END CLASS
