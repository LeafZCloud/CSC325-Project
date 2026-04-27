package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.Region;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
        setMaxWidth(Double.MAX_VALUE);
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

        double pct = Math.max(0, Math.min(100, r.stress));
        String barColor = pct > 75 ? "#ef4444" : (pct > 45 ? "#f59e0b" : "#22c55e");
        ProgressBar bar = new ProgressBar(pct / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);
        bar.setStyle("""
            -fx-accent:%s;
            -fx-control-inner-background:#1e293b;
            -fx-background-insets:0;
            -fx-padding:0;
        """.formatted(barColor));

        // Footer compact stats
        String popFormatted;
        if (r.population >= 1.0) {
            popFormatted = String.format("%.2fB", r.population);
        } else {
            popFormatted = String.format("%.0fM", r.population * 1000);
        }
        Label small = new Label(String.format("Pop %s    Stress %d%%    Econ %d%%", popFormatted, r.stress, r.economicHealth));
        small.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");

        getChildren().addAll(header, bar, small);
    }
}
