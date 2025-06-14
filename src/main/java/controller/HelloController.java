package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.HelloApplication;

import java.io.IOException;

public class HelloController {
    @FXML
    private Text txtMessage;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;

    @FXML
    public void initialize() {
        txtMessage.setText("Laboratory No. 12");
    }

    private void load(String form) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            showCompactError("Error", "No se pudo cargar la vista: " + form +
                    "\nError: " + e.getMessage());
        }
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
        this.txtMessage.setText("Laboratory No. 12");
    }


    @FXML
    public void operations(ActionEvent actionEvent) {
        try {
            load("operations-view.fxml");
            txtMessage.setText("Operations - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Operations: " + e.getMessage());
        }
    }

    @FXML
    public void kruskalAndPrim(ActionEvent actionEvent) {
        try {
            load("kruskalPrim-view.fxml");
            txtMessage.setText("Kruskal / Prim - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Kruskal / Prim: " + e.getMessage());
        }
    }

    @FXML
    public void directedGraph(ActionEvent actionEvent) {
        try {
            load("directedGraph-view.fxml");
            txtMessage.setText("Directed Graph - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Directed Graph: " + e.getMessage());
        }
    }

    @FXML
    public void dijkstraOnAction(ActionEvent actionEvent) {
        try {
            load("dijkstra-view.fxml");
            txtMessage.setText("Dijkstra- Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Dijkstra: " + e.getMessage());
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void showCompactError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefSize(400, 250);
        alert.getDialogPane().setMaxWidth(400);
        alert.getDialogPane().setMaxHeight(250);
        alert.showAndWait();
    }


}