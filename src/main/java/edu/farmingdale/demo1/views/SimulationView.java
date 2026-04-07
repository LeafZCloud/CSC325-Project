package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.components.EventCard;
import edu.farmingdale.demo1.components.StatBar;
import edu.farmingdale.demo1.components.WorldMapView;
import edu.farmingdale.demo1.components.RegionCard;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.GameEventDef;
import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;

import edu.farmingdale.demo1.simulation.SimulationModel;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SimulationView extends BorderPane {

    private GameState state;

    private Runnable onSimulationEnd;

    public SimulationView(PlanetConfig config) {

        state = SimulationModel.buildInitialState(config);

        buildUI();
    }

    public void setOnSimulationEnd(Runnable r) {
        this.onSimulationEnd = r;
    }

    public GameState getState() {
        return state;
    }

    private void buildUI() {

        // Planet map
        WorldMapView map = new WorldMapView(
                state.regions,
                state.planet,
                state.flashingRegions,
                state.lastEventId
        );

        setCenter(map);

        // Right panel: Global overview + Regions list
        VBox right = new VBox(14);

        Label globalTitle = new Label("GLOBAL OVERVIEW");
        globalTitle.setStyle("-fx-text-fill:#cbd5e1; -fx-font-weight:bold;");

        VBox stats = new VBox(10);
        stats.getChildren().addAll(
                new StatBar("Population", state.globalStats.population),
                new StatBar("Stress", state.globalStats.stress),
                new StatBar("Economic Health", state.globalStats.economicHealth),
                new StatBar("Exposure to Events", state.globalStats.exposure)
        );

        Label regionsTitle = new Label("REGIONS");
        regionsTitle.setStyle("-fx-text-fill:#cbd5e1; -fx-font-weight:bold; -fx-padding: 10 0 0 0;");

        VBox regionList = new VBox(10);
        for (GameTypes.Region r : state.regions) {
            regionList.getChildren().add(new RegionCard(r));
        }

        right.getChildren().addAll(globalTitle, stats, regionsTitle, regionList);

        setRight(right);

        // Events
        HBox events = new HBox(8);

        for (GameEventDef event : GameTypes.GAME_EVENTS) {

            EventCard card = new EventCard(event);

            card.setOnAction(e -> triggerEvent(event));

            events.getChildren().add(card);
        }

        setBottom(events);

        // End button
        Button end = new Button("End Simulation");

        end.setOnAction(e -> {
            if (onSimulationEnd != null) {
                onSimulationEnd.run();
            }
        });

        setTop(end);
    }

    private void triggerEvent(GameEventDef event) {

        state = SimulationModel.applyEvent(state, event);

        buildUI();
    }

}
