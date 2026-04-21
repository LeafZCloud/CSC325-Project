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
}
