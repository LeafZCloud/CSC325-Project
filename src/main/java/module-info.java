module edu.farmingdale.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires firebase.admin;


    opens edu.farmingdale.demo1 to javafx.fxml;
    exports edu.farmingdale.demo1;
}