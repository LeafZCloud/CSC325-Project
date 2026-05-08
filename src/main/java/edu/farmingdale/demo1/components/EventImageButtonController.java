package edu.farmingdale.demo1.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EventImageButtonController {

    @FXML
    private Button triggerButton;

    @FXML
    private ImageView buttonImage;

    public void setOnAction(EventHandler<ActionEvent> handler) {
        triggerButton.setOnAction(handler);
    }

    public void setImage(String imageUrl) {
        buttonImage.setImage(new Image(imageUrl));
    }

    public void setFitHeight(double fitHeight) {
        buttonImage.setFitHeight(fitHeight);
    }

    public void setCoolingDown(boolean coolingDown) {
        triggerButton.setDisable(coolingDown);

        if (coolingDown) {
            triggerButton.setStyle("""
                -fx-background-color: rgba(239, 68, 68, 0.82);
                -fx-background-radius: 10;
                -fx-border-color: #fecaca;
                -fx-border-radius: 10;
                -fx-border-width: 2;
                -fx-padding: 0;
                -fx-opacity: 1;
            """);
            buttonImage.setOpacity(0.45);
            return;
        }

        triggerButton.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        buttonImage.setOpacity(1);
    }
}
