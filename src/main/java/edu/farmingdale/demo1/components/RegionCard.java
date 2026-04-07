package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.Region;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

 /*region card showing region name, stability label,
  a health bar, and compact stats line. */
public class RegionCard extends VBox {

    private static String stabilityFor(Region r) {
        boolean critical = r.economicHealth < 35 || r.stress > 70 || r.exposure > 70;
        boolean warning = r.economicHealth < 50 || r.stress > 55 || r.exposure > 55;
        if (critical) return "Critical";
        if (warning) return "Strained";
        return "Stable";
    }

    public RegionCard(Region r) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("""
            -fx-background-color:#0b1220;
            -fx-background-radius:12;
            -fx-border-color:#1f2a37;
            -fx-border-radius:12;
        """);

        // Header line: name + status
        HBox header = new HBox();
        Label name = new Label(r.name);
        name.setStyle("-fx-text-fill:#e2e8f0; -fx-font-size:14px; -fx-font-weight:bold;");

        String statusText = stabilityFor(r);
        Label status = new Label(statusText);
        String statusColor = "#22c55e"; // Stable
        if ("Strained".equals(statusText)) statusColor = "#f59e0b";
        if ("Critical".equals(statusText)) statusColor = "#ef4444";
        status.setStyle("-fx-text-fill:" + statusColor + "; -fx-font-size:12px; -fx-font-weight:bold;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(name, spacer, status);

        // Bar: represent stress level, color-coded (red/yellow)
        Rectangle background = new Rectangle(260, 8);
        background.setArcHeight(6);
        background.setArcWidth(6);
        background.setFill(Color.web("#1e293b"));

        double pct = Math.max(0, Math.min(100, r.stress));
        double width = pct * 2.6; // 260px max
        Rectangle fill = new Rectangle(width, 8);
        fill.setArcHeight(6);
        fill.setArcWidth(6);
        String barColor = pct > 75 ? "#ef4444" : (pct > 45 ? "#f59e0b" : "#22c55e");
        fill.setFill(Color.web(barColor));

        javafx.scene.layout.StackPane bar = new javafx.scene.layout.StackPane(background, fill);

        // Footer compact stats
        String popPct = String.valueOf(Math.round(r.populationShare * 100)) + "%";
        Label small = new Label("Pop " + popPct + "    Stress " + r.stress + "%    Econ " + r.economicHealth + "%");
        small.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");

        getChildren().addAll(header, bar, small);
    }
}
