package edu.farmingdale.demo1.simulation;

import edu.farmingdale.demo1.simulation.GameTypes.FeedPost;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SocialFeedModel {

    public static class SentimentStyle {
        public String label;
        public String color;

        public SentimentStyle(String label, String color) {
            this.label = label;
            this.color = color;
        }
    }

    public static final Map<String, String> SENTIMENT_STYLES = new HashMap<>();
    public static final Map<String, SentimentStyle> SENTIMENT_BADGES = new HashMap<>();

    static {

        SENTIMENT_STYLES.put("positive", "#22c55e");
        SENTIMENT_STYLES.put("negative", "#f59e0b");
        SENTIMENT_STYLES.put("neutral", "#64748b");
        SENTIMENT_STYLES.put("panic", "#ef4444");

        SENTIMENT_BADGES.put(
                "positive",
                new SentimentStyle("😊 Hopeful", "#22c55e")
        );

        SENTIMENT_BADGES.put(
                "negative",
                new SentimentStyle("😟 Concerned", "#f59e0b")
        );

        SENTIMENT_BADGES.put(
                "neutral",
                new SentimentStyle("😐 Neutral", "#64748b")
        );

        SENTIMENT_BADGES.put(
                "panic",
                new SentimentStyle("😱 Panic", "#ef4444")
        );
    }

    public static String formatLikes(int likes) {
        return String.format("%,d", likes);
    }

    public static String formatReposts(int reposts) {
        return String.format("%,d", reposts);
    }

    public static boolean hasPosts(List<FeedPost> posts) {
        return posts != null && !posts.isEmpty();
    }
}
