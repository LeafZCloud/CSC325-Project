package edu.farmingdale.demo1.components;

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

    private static double stabilityPercentFor(Region r) {
        double populationPct = Math.clamp(r.population * 10, 0, 100);
        double economyPct = Math.clamp(r.economicHealth, 0, 100);
        double stressPct = Math.clamp(r.stress, 0, 100);
        double exposurePct = Math.clamp(r.exposure, 0, 100);

        // Population is contextual (low-weight). Region health is mainly economy/stress/exposure.
        double pct = (populationPct * 0.10)
                + (economyPct * 0.30)
                + ((100 - stressPct) * 0.30)
                + ((100 - exposurePct) * 0.30);

        if (economyPct >= 100 && populationPct >= 40 && stressPct <= 0 && exposurePct <= 0) {
            pct = 100;
        }

        return pct;
    }

    private static String stabilityFor(Region r) {
        double pct = stabilityPercentFor(r);

        if (pct < (100.0 / 3.0)) return "Critical";
        if (pct < (200.0 / 3.0)) return "Strained";
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

        double pct = stabilityPercentFor(r);
        String barColor;
        if (pct < (100.0 / 3.0)) {
            barColor = "#ef4444";
        } else if (pct < (200.0 / 3.0)) {
            barColor = "#f59e0b";
        } else {
            barColor = "#22c55e";
        }
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
        Label small = new Label(String.format("Pop %s    Stress %d%%    Econ %d%%    Exposure %d%%", popFormatted, r.stress, r.economicHealth, r.exposure));
        small.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");

        getChildren().addAll(header, bar, small);
    }
}
