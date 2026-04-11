package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes.FeedPost;
import edu.farmingdale.demo1.simulation.SocialFeedModel;
import edu.farmingdale.demo1.simulation.SocialFeedModel.SentimentStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class SocialFeedView extends VBox {

    public SocialFeedView(List<FeedPost> posts) {
        setSpacing(10);

        if (!SocialFeedModel.hasPosts(posts)) {
            Label empty = new Label("No one is posting yet.");
            empty.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            getChildren().add(empty);
            return;
        }

        for (FeedPost post : posts) {
            getChildren().add(createPostCard(post));
        }
    }

    private VBox createPostCard(FeedPost post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle("""
            -fx-background-color:#0b1220;
            -fx-background-radius:12;
            -fx-border-color:#1f2a37;
            -fx-border-radius:12;
        """);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(post.avatarInitial);
        avatar.setMinSize(34, 34);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("""
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-background-radius:999;
            -fx-font-size:13px;
            -fx-background-color:%s;
        """.formatted(post.avatarColor));

        VBox userMeta = new VBox(2);
        Label username = new Label(post.username);
        username.setStyle("-fx-text-fill:#e2e8f0; -fx-font-size:13px; -fx-font-weight:bold;");

        Label handle = new Label(post.handle + " · " + post.timeAgo);
        handle.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");
        userMeta.getChildren().addAll(username, handle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SentimentStyle badge = SocialFeedModel.SENTIMENT_BADGES.getOrDefault(
                post.sentiment,
                new SentimentStyle("Neutral", "#64748b")
        );

        Label sentiment = new Label(badge.label);
        sentiment.setStyle("""
            -fx-text-fill:white;
            -fx-font-size:10px;
            -fx-font-weight:bold;
            -fx-padding:4 8 4 8;
            -fx-background-radius:999;
            -fx-background-color:%s;
        """.formatted(badge.color));

        header.getChildren().addAll(avatar, userMeta, spacer, sentiment);

        Label body = new Label(post.content);
        body.setWrapText(true);
        body.setStyle("-fx-text-fill:#dbe4f0; -fx-font-size:13px;");

        HBox metrics = new HBox(16);
        Label likes = new Label("Likes " + SocialFeedModel.formatLikes(post.likes));
        Label reposts = new Label("Reposts " + SocialFeedModel.formatReposts(post.reposts));
        likes.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");
        reposts.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");
        metrics.getChildren().addAll(likes, reposts);

        card.getChildren().addAll(header, body, metrics);
        return card;
    }
}
