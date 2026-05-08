package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.Database.DatabaseController;
import edu.farmingdale.demo1.Database.FirebaseAuthService;
import edu.farmingdale.demo1.components.EventImageButtonController;
import edu.farmingdale.demo1.components.EventCard;
import edu.farmingdale.demo1.components.RegionCard;
import edu.farmingdale.demo1.components.SocialFeedView;
import edu.farmingdale.demo1.components.StatBar;
import edu.farmingdale.demo1.components.WorldMapView;
import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.EventLogEntry;
import edu.farmingdale.demo1.simulation.GameTypes.GameEventDef;
import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.SimulationModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationView extends BorderPane {

    private static final List<String> EVENT_TABS = List.of("all", "disaster", "conflict", "technology", "society");
    private static final Map<String, String> EVENT_TAB_LABELS = Map.of(
            "all", "All Events",
            "disaster", "Disasters",
            "conflict", "Conflicts",
            "technology", "Technology",
            "society", "Society"
    );
    private static final Map<String, String> EVENT_BUTTON_IMAGES = Map.ofEntries(
            Map.entry("meteor", "/images/commandsAndEvents/AsteroidButton.png"),
            Map.entry("earthquakes", "/images/commandsAndEvents/EarthquakeButton.png"),
            Map.entry("ice_age", "/images/commandsAndEvents/BlizzardButton.png"),
            Map.entry("volcanic_eruptions", "/images/commandsAndEvents/EruptionButton.png"),
            Map.entry("drought", "/images/commandsAndEvents/DroughtButton.png"),
            Map.entry("plague", "/images/commandsAndEvents/PlagueButton.png"),
            Map.entry("nuke", "/images/commandsAndEvents/Nuke.png"),
            Map.entry("world_war", "/images/commandsAndEvents/WorldWarButton.png"),
            Map.entry("industrial_revolution", "/images/commandsAndEvents/IndustrializeButton.png"),
            Map.entry("medical_breakthrough", "/images/commandsAndEvents/MedicalBreakThrough.png"),
            Map.entry("golden_age", "/images/commandsAndEvents/GoldenAgeButton.png")
    );
    private static final Map<String, String> TRIGGERED_EVENT_IMAGES = Map.ofEntries(
            Map.entry("tsunami", "/images/commandsAndEvents/TsunamiEvent.png"),
            Map.entry("virus", "/images/commandsAndEvents/VirusEvent.png"),
            Map.entry("depression", "/images/commandsAndEvents/DepressionEvent.png"),
            Map.entry("ice_age", "/images/commandsAndEvents/IceAgeEvent.png"),
            Map.entry("famine", "/images/commandsAndEvents/FamineEvent.png"),
            Map.entry("rebellion", "/images/commandsAndEvents/RebellionEvent.png"),
            Map.entry("economic_boom", "/images/commandsAndEvents/EcoBoomEvent.png")
    );


    private GameState state;
    private String activeEventTab = "all";
    private String activeSidebarTab = "stats";
    private double sidebarScrollPosition = 0.0;
    private String selectedEventId;
    private final Timeline yearTimeline;
    private final PauseTransition popupTimer;
    private String activePopupEventId;

    private final FirebaseAuthService authService;
    private final DatabaseController databaseController;
    private Runnable onSimulationEnd;

    public SimulationView(PlanetConfig config, FirebaseAuthService authService, DatabaseController databaseController) {
        this.authService = authService;
        this.databaseController = databaseController;
        state = SimulationModel.buildInitialState(config);
        yearTimeline = new Timeline(new KeyFrame(Duration.seconds(10), ignored -> advanceYear()));
        yearTimeline.setCycleCount(Animation.INDEFINITE);
        yearTimeline.play();
        popupTimer = new PauseTransition(Duration.seconds(5));
        popupTimer.setOnFinished(ignored -> clearPopup());
        buildUI();
    }

    public void setOnSimulationEnd(Runnable r) {
        this.onSimulationEnd = r;
    }

    public GameState getState() {
        return state;
    }

    private void buildUI() {
        WorldMapView map = new WorldMapView(
                state.regions,
                state.planet,
                state.flashingRegions,
                state.lastEventId
        );
        StackPane centerLayer = new StackPane(map);
        centerLayer.setPickOnBounds(false);

        if (activePopupEventId != null) {
            Node popup = buildTriggeredEventPopup(activePopupEventId);
            centerLayer.getChildren().add(popup);
            StackPane.setAlignment(popup, Pos.TOP_CENTER);
            popup.setTranslateY(-215);
            popup.setTranslateX(160);
        }

        setCenter(centerLayer);

        setRight(buildSidebar());
        setBottom(buildEventBrowser());
        setTop(buildTopBar());
    }

    private HBox buildTopBar() {
        Button end = new Button("End Simulation");
        end.setStyle("""
            -fx-background-color:#ef4444;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-background-radius:10;
            -fx-padding:10 18 10 18;
        """);
        end.setOnAction(ignored -> {
            yearTimeline.stop();
            System.out.println("IDToken: " + authService.getSaveIdToken());
            System.out.println("LocalId: " + authService.getSaveLocalIdToken());
            databaseController.saveGameState(state, authService.getSaveIdToken(), authService.getSaveLocalIdToken(), 1);
            if (onSimulationEnd != null) {
                onSimulationEnd.run();
            }
        });

        Label year = new Label("Year " + state.year);
        year.setStyle("-fx-text-fill:#e2e8f0; -fx-font-size:18px; -fx-font-weight:bold;");

        Label world = new Label(state.planet.name);
        world.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");

        VBox titleBlock = new VBox(2, year, world);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(12, titleBlock, spacer, end);
        topBar.setPadding(new Insets(14, 18, 8, 18));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color:rgba(2,6,23,0.72);");
        return topBar;
    }

    private VBox buildSidebar() {
        ensureSelectedEvent();

        VBox sidebar = new VBox(12);
        sidebar.setMinWidth(300);
        sidebar.setPrefWidth(360);
        sidebar.setMaxWidth(420);
        sidebar.setPadding(new Insets(14));
        sidebar.setStyle("""
            -fx-background-color:#020617;
            -fx-border-color:#1e293b;
            -fx-border-width:0 0 0 1;
        """);

        Label title = new Label("WORLD CONSOLE");
        title.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold;");

        HBox tabs = new HBox(8);
        tabs.getChildren().addAll(
                createSidebarTab("stats", "Stats"),
                createSidebarTab("events", "Events"),
                createSidebarTab("feed", "Feed")
        );

        ScrollPane contentScroller = new ScrollPane(buildSidebarContent());
        contentScroller.setFitToWidth(true);
        contentScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroller.setMaxWidth(Double.MAX_VALUE);
        contentScroller.setStyle("-fx-background:#020617; -fx-background-color:transparent;");
        contentScroller.getStyleClass().add("sidebar-scroller");
        contentScroller.setVvalue(sidebarScrollPosition);
        contentScroller.vvalueProperty().addListener(observable ->
                sidebarScrollPosition = ((javafx.beans.value.ObservableDoubleValue) observable).get());

        VBox.setVgrow(contentScroller, Priority.ALWAYS);
        sidebar.getChildren().addAll(title, tabs, contentScroller);
        return sidebar;
    }

    private Button createSidebarTab(String tabId, String label) {
        Button tab = new Button(label);
        tab.setStyle(sidebarTabStyle(tabId.equals(activeSidebarTab)));
        tab.setOnAction(ignored -> {
            activeSidebarTab = tabId;
            buildUI();
        });
        return tab;
    }

    private VBox buildSidebarContent() {
        return switch (activeSidebarTab) {
            case "events" -> buildEventsSidebar();
            case "feed" -> buildFeedSidebar();
            default -> buildStatsSidebar();
        };
    }

    private VBox buildStatsSidebar() {
        VBox content = new VBox(14);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setPadding(new Insets(0, 10, 0, 0));

        Label summary = new Label("Global systems status across population, pressure, economy, and exposure.");
        summary.setWrapText(true);
        summary.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");

        VBox stats = new VBox(10);
        stats.setMaxWidth(Double.MAX_VALUE);
        stats.getChildren().addAll(
                new StatBar("Population", state.globalStats.population),
                new StatBar("Stress", state.globalStats.stress),
                new StatBar("Economic Health", state.globalStats.economicHealth),
                new StatBar("Exposure to Events", state.globalStats.exposure)
        );

        Label regionsTitle = sectionLabel("Regions");
        VBox regionList = new VBox(10);
        regionList.setMaxWidth(Double.MAX_VALUE);
        for (GameTypes.Region region : state.regions) {
            RegionCard card = new RegionCard(region);
            card.setMaxWidth(Double.MAX_VALUE);
            regionList.getChildren().add(card);
        }

        content.getChildren().addAll(summary, stats, regionsTitle, regionList);
        return content;
    }

    private VBox buildEventsSidebar() {
        VBox content = new VBox(12);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setPadding(new Insets(0, 10, 0, 0));

        if (state.eventLog.isEmpty()) {
            Label empty = new Label("Trigger an event to see the event log and detailed breakdown here.");
            empty.setWrapText(true);
            empty.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            content.getChildren().add(empty);
            return content;
        }

        EventLogEntry selected = findSelectedEventEntry();
        if (selected != null) {
            GameEventDef definition = findEventDef(selected);
            VBox detail = new VBox(8);
            detail.setMaxWidth(Double.MAX_VALUE);
            detail.setPadding(new Insets(12));
            detail.setStyle("""
                -fx-background-color:#0b1220;
                -fx-background-radius:12;
                -fx-border-color:#1f2a37;
                -fx-border-radius:12;
            """);

            Label title = new Label(selected.emoji + " " + selected.eventName);
            title.setWrapText(true);
            title.setStyle("-fx-text-fill:#f8fafc; -fx-font-size:16px; -fx-font-weight:bold;");

            Label meta = new Label("Category: " + capitalize(selected.category) + " · Year " + selected.year);
            meta.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px;");

            Label description = new Label(definition != null ? definition.description : "No description available.");
            description.setWrapText(true);
            description.setStyle("-fx-text-fill:#dbe4f0; -fx-font-size:13px;");

            Label flavor = new Label(definition != null ? "\"" + definition.flavour + "\"" : "");
            flavor.setWrapText(true);
            flavor.setStyle("-fx-text-fill:#7dd3fc; -fx-font-size:12px;");

            Label affected = new Label("Affected Regions: " + describeAffectedRegions(selected));
            affected.setWrapText(true);
            affected.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:12px;");

            Label effects = new Label(
                    "Impact: Population " + formatPopulationDelta(selected.effects.population)
                            + " · Stress " + signedPercent(selected.effects.stress)
                            + " · Economy " + signedPercent(selected.effects.economicHealth)
                            + " · Exposure " + signedPercent(selected.effects.exposure)
            );
            effects.setWrapText(true);
            effects.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:12px;");

            detail.getChildren().addAll(title, meta, description);
            if (definition != null) {
                detail.getChildren().add(flavor);
            }
            detail.getChildren().addAll(affected, effects);

            content.getChildren().addAll(sectionLabel("Selected Event"), detail);
        }

        Label logTitle = sectionLabel("Event Log");
        VBox logList = new VBox(8);
        logList.setMaxWidth(Double.MAX_VALUE);
        for (EventLogEntry entry : state.eventLog) {
            Button item = new Button(entry.eventName + " · Year " + entry.year);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle(eventLogItemStyle(entry.id.equals(selectedEventId)));
            item.setOnAction(ignored -> {
                selectedEventId = entry.id;
                buildUI();
            });
            logList.getChildren().add(item);
        }

        content.getChildren().addAll(logTitle, logList);

        return content;
    }

    private VBox buildFeedSidebar() {
        VBox content = new VBox(12);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setPadding(new Insets(0, 10, 0, 0));
        Label title = sectionLabel("World Feed");
        Label subtitle = new Label("Live reactions from citizens, analysts, and reporters across the planet.");
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");

        SocialFeedView feedView = new SocialFeedView(state.feedPosts);
        content.getChildren().addAll(title, subtitle, feedView);
        return content;
    }

    private VBox buildEventBrowser() {
        VBox eventBrowser = new VBox(12);
        eventBrowser.setPadding(new Insets(14, 16, 16, 16));
        eventBrowser.setStyle("-fx-background-color:#0f172a; -fx-border-color:#1e293b; -fx-border-width:1 0 0 0;");

        HBox tabBar = new HBox(8);
        tabBar.setAlignment(Pos.CENTER_LEFT);
        for (String tab : EVENT_TABS) {
            Button tabButton = new Button(EVENT_TAB_LABELS.get(tab));
            tabButton.setStyle(tabButtonStyle(tab.equals(activeEventTab)));
            tabButton.setOnAction(ignored -> {
                activeEventTab = tab;
                buildUI();
            });
            tabBar.getChildren().add(tabButton);
        }

        HBox eventCards = new HBox(10);
        eventCards.setAlignment(Pos.CENTER_LEFT);

        List<GameEventDef> filteredEvents = new ArrayList<>();
        for (GameEventDef event : GameTypes.GAME_EVENTS) {
            if ("all".equals(activeEventTab) || activeEventTab.equals(event.category)) {
                filteredEvents.add(event);
            }
        }

        for (GameEventDef event : filteredEvents) {
            eventCards.getChildren().add(createEventTrigger(event));
        }

        if (filteredEvents.isEmpty()) {
            Label emptyState = new Label("No events available in this category yet.");
            emptyState.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            eventCards.getChildren().add(emptyState);
        }

        ScrollPane eventScroller = new ScrollPane(eventCards);
        eventScroller.setFitToHeight(true);
        eventScroller.setPrefViewportHeight(120);
        eventScroller.setMinViewportHeight(120);
        eventScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        eventScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        eventScroller.setPannable(true);
        eventScroller.getStyleClass().add("event-browser-scroller");

        Label sectionTitle = new Label(EVENT_TAB_LABELS.get(activeEventTab));
        sectionTitle.setStyle("-fx-text-fill:white; -fx-font-size:16px; -fx-font-weight:bold;");

        eventBrowser.getChildren().addAll(sectionTitle, tabBar, eventScroller);
        return eventBrowser;
    }

    private void triggerEvent(GameEventDef event) {
        if (!SimulationModel.isEventAvailableForPlayer(state, event.id)) {
            return;
        }

        state = SimulationModel.applyPlayerCommand(state, event);
        showPopup(state.pendingTriggeredEventId);
        activeSidebarTab = "events";
        if (!state.eventLog.isEmpty()) {
            selectedEventId = state.eventLog.getFirst().id;
        }
        buildUI();
    }

    private Node createEventTrigger(GameEventDef event) {
        boolean available = SimulationModel.isEventAvailableForPlayer(state, event.id);
        String imagePath = EVENT_BUTTON_IMAGES.get(event.id);
        if (imagePath != null) {
            try {
                URL imageResource = getClass().getResource(imagePath);
                if (imageResource == null) {
                    return createFallbackEventCard(event);
                }
                String imageUrl = imageResource.toExternalForm();
                Image image = new Image(imageUrl);
                if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
                    return createFallbackEventCard(event);
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/event-image-button.fxml"));
                Node imageButton = loader.load();
                EventImageButtonController controller = loader.getController();
                controller.setImage(imageUrl);
                // controller.setFitHeight(EVENT_BUTTON_HEIGHTS.getOrDefault(event.id, 84.0));
                controller.setOnAction(ignored -> triggerEvent(event));
                imageButton.setDisable(!available);
                imageButton.setOpacity(available ? 1.0 : 0.45);
                return imageButton;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load event image button for " + event.id + ".", e);
            }
        }

        return createFallbackEventCard(event);
    }

    private EventCard createFallbackEventCard(GameEventDef event) {
        EventCard card = new EventCard(event);
        card.setOnAction(ignored -> triggerEvent(event));
        card.setDisable(!SimulationModel.isEventAvailableForPlayer(state, event.id));
        return card;
    }

    private void ensureSelectedEvent() {
        if (state.eventLog.isEmpty()) {
            selectedEventId = null;
            return;
        }

        if (selectedEventId == null || findSelectedEventEntry() == null) {
            selectedEventId = state.eventLog.getFirst().id;
        }
    }

    private EventLogEntry findSelectedEventEntry() {
        if (selectedEventId == null) {
            return null;
        }

        for (EventLogEntry entry : state.eventLog) {
            if (selectedEventId.equals(entry.id)) {
                return entry;
            }
        }

        return null;
    }

    private GameEventDef findEventDef(EventLogEntry entry) {
        for (GameEventDef event : GameTypes.allEvents()) {
            if (event.id.equals(entry.id) || event.name.equals(entry.eventName)) {
                return event;
            }
        }
        return null;
    }

    private String describeAffectedRegions(EventLogEntry entry) {
        List<String> names = new ArrayList<>();
        for (String regionId : entry.affectedRegions) {
            for (GameTypes.Region region : state.regions) {
                if (region.id.equals(regionId)) {
                    names.add(region.name);
                    break;
                }
            }
        }

        return names.isEmpty() ? "Planet-wide" : String.join(", ", names);
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill:#cbd5e1; -fx-font-weight:bold; -fx-font-size:13px;");
        return label;
    }

    private String sidebarTabStyle(boolean active) {
        if (active) {
            return """
                -fx-background-color:#38bdf8;
                -fx-text-fill:#020617;
                -fx-font-weight:bold;
                -fx-background-radius:999;
                -fx-padding:8 14 8 14;
            """;
        }

        return """
            -fx-background-color:#1e293b;
            -fx-text-fill:#cbd5e1;
            -fx-border-color:#334155;
            -fx-border-radius:999;
            -fx-background-radius:999;
            -fx-padding:8 14 8 14;
        """;
    }

    private String tabButtonStyle(boolean active) {
        if (active) {
            return """
                -fx-background-color:#38bdf8;
                -fx-text-fill:#0f172a;
                -fx-font-weight:bold;
                -fx-background-radius:999;
                -fx-padding:8 16 8 16;
            """;
        }

        return """
            -fx-background-color:#1e293b;
            -fx-text-fill:#cbd5e1;
            -fx-border-color:#334155;
            -fx-border-radius:999;
            -fx-background-radius:999;
            -fx-padding:8 16 8 16;
        """;
    }

    private String eventLogItemStyle(boolean active) {
        if (active) {
            return """
                -fx-background-color:#172554;
                -fx-text-fill:#e0f2fe;
                -fx-font-weight:bold;
                -fx-background-radius:10;
                -fx-border-color:#38bdf8;
                -fx-border-radius:10;
                -fx-padding:10 12 10 12;
            """;
        }

        return """
            -fx-background-color:#0b1220;
            -fx-text-fill:#cbd5e1;
            -fx-background-radius:10;
            -fx-border-color:#1f2a37;
            -fx-border-radius:10;
            -fx-padding:10 12 10 12;
        """;
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private String signedPercent(int value) {
        return (value >= 0 ? "+" : "") + value + "%";
    }

    private String formatPopulationDelta(double value) {
        return String.format("%+.1fB", value);
    }

    private void advanceYear() {
        state.year += 1;
        buildUI();
    }

    private void showPopup(String eventId) {
        popupTimer.stop();
        activePopupEventId = eventId;

        if (eventId != null) {
            popupTimer.playFromStart();
        }
    }

    private void clearPopup() {
        activePopupEventId = null;
        buildUI();
    }

    private Node buildTriggeredEventPopup(String eventId) {
        VBox popupBox = new VBox();
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setMouseTransparent(true);
        popupBox.setMaxWidth(280);
        popupBox.setPadding(new Insets(0));

        String imagePath = TRIGGERED_EVENT_IMAGES.get(eventId);
        URL imageResource = imagePath != null ? getClass().getResource(imagePath) : null;
        if (imageResource != null) {
            ImageView imageView = new ImageView(new Image(imageResource.toExternalForm()));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(250);
            popupBox.getChildren().add(imageView);
            return popupBox;
        }

        GameEventDef event = GameTypes.findEventById(eventId);
        Label fallback = new Label(event != null ? event.name : "Event Triggered");
        fallback.setStyle("""
            -fx-background-color:#7f1d1d;
            -fx-text-fill:white;
            -fx-font-size:14px;
            -fx-font-weight:bold;
            -fx-background-radius:12;
            -fx-padding:10 16 10 16;
        """);
        popupBox.getChildren().add(fallback);
        return popupBox;
    }
}
