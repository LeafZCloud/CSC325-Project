module edu.farmingdale.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires firebase.admin;
    requires com.google.gson;
    requires com.google.common;
    requires jdk.httpserver;
    requires proto.google.common.protos;


    opens edu.farmingdale.demo1 to javafx.fxml;
    exports edu.farmingdale.demo1;
}