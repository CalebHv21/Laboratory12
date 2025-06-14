package controller;

import domain.*;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
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
import javafx.util.Duration;
import util.AristaVisual;

import java.util.*;

public class DirectedGraphController {
    @javafx.fxml.FXML
    private RadioButton linkedlistRB;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Label infoLabel;
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
/*
    private void verifySelection(){

        groupGraphs();

        if (graphGroup.getSelectedToggle() == matrixRB) {
           graphSelected = matrixGraph;
        } else if (graphGroup.getSelectedToggle() == listRB) {
            graphSelected = adlistGraph;
        } else if (graphGroup.getSelectedToggle() == linkedlistRB) {
            graphSelected = linkedListGraph;
        } else {
            util.FXUtility.showAlert("Alerta", "Seleccione un tipo de grafo");
        }
    }

    private void groupGraphs(){

        //Agrupar las opciones
        graphGroup = new ToggleGroup();
        matrixRB.setToggleGroup(graphGroup);
        listRB.setToggleGroup(graphGroup);
        linkedlistRB.setToggleGroup(graphGroup);

    }
*/

    @javafx.fxml.FXML
    public void toStringOnAction(ActionEvent actionEvent) {
        TA_Info.setText("");
        if (matrixRB.isSelected()) {
            if (matrixGraph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
            } else {
                updateTextArea("=== INFORMACIÓN DEL GRAFO ===\n" + matrixGraph.toString());
            }
        } else if (listRB.isSelected()) {
            if (adlistGraph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
            } else {
                updateTextArea("=== INFORMACIÓN DEL GRAFO ===\n" + adlistGraph.toString());
            }
        } else if (linkedlistRB.isSelected()) {
            if (linkedListGraph.isEmpty()) {
                updateTextArea("El grafo está vacío.");
            } else {
                updateTextArea("=== INFORMACIÓN DEL GRAFO ===\n" + linkedListGraph.toString());
            }
        }
    }

    @javafx.fxml.FXML
    public void dfsTourOnAction(ActionEvent actionEvent) {

        try {

            if(matrixRB.isSelected()){

                if (matrixGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar DFS.");
                    return;
                }


                String dfsResult = matrixGraph.dfs();
                updateTextArea("DFS Tour: " + dfsResult);
                animateTraversal(dfsResult, Color.LIGHTYELLOW);

            }else if(listRB.isSelected()){

                if (adlistGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar DFS.");
                    return;
                }


                String dfsResult = adlistGraph.dfs();
                updateTextArea("DFS Tour: " + dfsResult);
                animateTraversal(dfsResult, Color.LIGHTYELLOW);

            }else if(linkedlistRB.isSelected()){

                if (linkedListGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar DFS.");
                    return;
                }


                String dfsResult = linkedListGraph.dfs();
                updateTextArea("DFS Tour: " + dfsResult);
                animateTraversal(dfsResult, Color.LIGHTYELLOW);


            }


        } catch (GraphException | ListException e) {
            updateTextArea("Error en DFS: " + e.getMessage());
        } catch (StackException e) {
            throw new RuntimeException(e);
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
    public void bfsTourOnAction(ActionEvent actionEvent) {

        try {

            if(matrixRB.isSelected()){

                if (matrixGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar BFS.");
                    return;
                }


                String bfsResult = matrixGraph.bfs();
                updateTextArea("BFS Tour: " + bfsResult);
                animateTraversal(bfsResult, Color.LIGHTYELLOW);

            }else if(listRB.isSelected()){

                if (adlistGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar BFS.");
                    return;
                }


                String bfsResult = adlistGraph.bfs();
                updateTextArea("BFS Tour: " + bfsResult);
                animateTraversal(bfsResult, Color.LIGHTYELLOW);

            }else if(linkedlistRB.isSelected()){

                if (linkedListGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío. No se puede realizar BFS.");
                    return;
                }


                String bfsResult = linkedListGraph.bfs();
                updateTextArea("BFS Tour: " + bfsResult);
                animateTraversal(bfsResult, Color.LIGHTYELLOW);


            }


        } catch (GraphException | QueueException | ListException e) {
            updateTextArea("Error en BFS: " + e.getMessage());
        }
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
    public void containsEdgeOnAction(ActionEvent actionEvent) {

        try {

            if (matrixRB.isSelected()) { //PARA MATRIZ

                if (matrixGraph.size() < 2) {
                    updateTextArea("Necesitas al menos 2 vértices para verificar aristas.");
                    return;
                }

                List<Integer> vertices = getVerticesList();
                Integer vertex1 = vertices.get(random.nextInt(vertices.size()));
                Integer vertex2 = vertices.get(random.nextInt(vertices.size()));

                boolean hasEdge = matrixGraph.containsEdge(vertex1, vertex2);
                updateTextArea("¿Existe arista entre " + vertex1 + " y " + vertex2 + "? " + hasEdge);

            } else if (listRB.isSelected()) {

                if (adlistGraph.size() < 2) {
                    updateTextArea("Necesitas al menos 2 vértices para verificar aristas.");
                    return;
                }

                List<Character> vertices = getVerticesADList();
                Character vertex1 = vertices.get(random.nextInt(vertices.size()));
                Character vertex2 = vertices.get(random.nextInt(vertices.size()));

                boolean hasEdge = adlistGraph.containsEdge(vertex1, vertex2);
                updateTextArea("¿Existe arista entre " + vertex1 + " y " + vertex2 + "? " + hasEdge);


            } else if (linkedlistRB.isSelected()) {

                if (linkedListGraph.size() < 2) {
                    updateTextArea("Necesitas al menos 2 vértices para verificar aristas.");
                    return;
                }

                List<String> vertices = getVerticesLinkedList();
                String vertex1 = vertices.get(random.nextInt(vertices.size()));
                String vertex2 = vertices.get(random.nextInt(vertices.size()));

                boolean hasEdge = linkedListGraph.containsEdge(vertex1, vertex2);
                updateTextArea("¿Existe arista entre " + vertex1 + " y " + vertex2 + "? " + hasEdge);

            }//END IF


        } catch (GraphException | ListException e) {
            updateTextArea("Error verificando arista: " + e.getMessage());
        }


    }

    @javafx.fxml.FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {


        try {

            if (matrixRB.isSelected()) { //PARA MATRIZ
                if (matrixGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<Integer> vertices = getVerticesList();
                Integer vertex = vertices.get(random.nextInt(vertices.size()));

                boolean hasVertex = matrixGraph.containsVertex(vertex);
                updateTextArea("¿Contiene el vértice " + vertex + "? " + hasVertex);

                // Destacar el vértice verificado
                if (hasVertex && visualNodes.containsKey(vertex)) {
                    highlightNode(visualNodes.get(vertex), Color.YELLOW);
                }


            }else if(listRB.isSelected()){

                if (adlistGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<Character> vertices = getVerticesADList();
                Character vertex = vertices.get(random.nextInt(vertices.size()));

                boolean hasVertex = adlistGraph.containsVertex(vertex);
                updateTextArea("¿Contiene el vértice " + vertex + "? " + hasVertex);

                // Destacar el vértice verificado
                if (hasVertex && visualNodes2.containsKey(vertex)) {
                    highlightNode(visualNodes2.get(vertex), Color.YELLOW);
                }

            }else if(linkedlistRB.isSelected()){

                if (linkedListGraph.isEmpty()) {
                    updateTextArea("El grafo está vacío.");
                    return;
                }

                List<String> vertices = getVerticesLinkedList();
                String vertex = vertices.get(random.nextInt(vertices.size()));

                boolean hasVertex = linkedListGraph.containsVertex(vertex);
                updateTextArea("¿Contiene el vértice " + vertex + "? " + hasVertex);

                // Destacar el vértice verificado
                if (hasVertex && visualNodes.containsKey(vertex)) {
                    highlightNode(visualNodes.get(vertex), Color.YELLOW);
                }

            }//END IF


        } catch (GraphException | ListException e) {
            updateTextArea("Error verificando vértice: " + e.getMessage());
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


    //Visualización de grafos:

    //PARA MATRIZ
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

    //PARA LINKED LIST:

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
                    int weight = util.Utility.random(101,150);
                    linkedListGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeLinkedListGraphGraph();
            updateTextArea("Grafo inicial cargado con monumentos históricos.");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo inicial: " + e.getMessage());
        }
    }


    private void clearVisualLinkedElements() {
        graphPane.getChildren().removeAll(visualNodes.values());
        for (AristaVisual edge : visualEdges) {
            graphPane.getChildren().remove(edge.getLinea());
        }
        visualNodes.clear();
        visualEdges.clear();
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


    //PARA ADJACENCY LIST:

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
                    int weight = util.Utility.random(51,100);
                    adlistGraph.addEdgeWeight(source, target, weight);
                }
            }

            visualizeADListGraph();
            updateTextArea("Grafo inicial cargado con letras mayúsculas.");

        } catch (GraphException | ListException e) {
            updateTextArea("Error cargando grafo inicial: " + e.getMessage());
        }
    }


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

}
