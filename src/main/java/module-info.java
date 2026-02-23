module edu.farmingdale.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.farmingdale.demo1 to javafx.fxml;
    exports edu.farmingdale.demo1;
}