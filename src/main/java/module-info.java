module ucr.lab {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens ucr.lab to javafx.fxml;
    exports ucr.lab;
    exports controller;
    opens controller to javafx.fxml;
}