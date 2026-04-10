module demo1 {
    requires com.google.gson;
    requires firebase.admin;
    requires google.cloud.firestore;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires okhttp3;
    requires java.desktop;

    exports edu.farmingdale.demo1;

    opens edu.farmingdale.demo1 to javafx.fxml;
}