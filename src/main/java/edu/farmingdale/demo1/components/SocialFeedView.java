package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes.FeedPost;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SocialFeedView extends VBox {

    public SocialFeedView(List<FeedPost> posts) {

        setSpacing(5);

        for (FeedPost post : posts) {

            HBox row = new HBox(6);

            Label user = new Label(post.username);
            Label text = new Label(post.content);

            row.getChildren().addAll(user, text);

            getChildren().add(row);
        }
    }
}
