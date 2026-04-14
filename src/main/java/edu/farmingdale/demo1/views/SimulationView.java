package edu.farmingdale.demo1.views;

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
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private GameState state;
    private String activeEventTab = "all";
    private String activeSidebarTab = "stats";
    private String selectedEventId;
    private final Timeline yearTimeline;

    private Runnable onSimulationEnd;

    public SimulationView(PlanetConfig config) {
        state = SimulationModel.buildInitialState(config);
        yearTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> advanceYear()));
        yearTimeline.setCycleCount(Animation.INDEFINITE);
        yearTimeline.play();
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
        setCenter(map);

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
        end.setOnAction(e -> {
            yearTimeline.stop();
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
        sidebar.setPrefWidth(380);
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
        contentScroller.setStyle("-fx-background:#020617; -fx-background-color:transparent;");

        VBox.setVgrow(contentScroller, Priority.ALWAYS);
        sidebar.getChildren().addAll(title, tabs, contentScroller);
        return sidebar;
    }

    private Button createSidebarTab(String tabId, String label) {
        Button tab = new Button(label);
        tab.setStyle(sidebarTabStyle(tabId.equals(activeSidebarTab)));
        tab.setOnAction(e -> {
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

        Label summary = new Label("Global systems status across population, pressure, economy, and exposure.");
        summary.setWrapText(true);
        summary.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");

        VBox stats = new VBox(10);
        stats.getChildren().addAll(
                new StatBar("Population", state.globalStats.population),
                new StatBar("Stress", state.globalStats.stress),
                new StatBar("Economic Health", state.globalStats.economicHealth),
                new StatBar("Exposure to Events", state.globalStats.exposure)
        );

        Label regionsTitle = sectionLabel("Regions");
        VBox regionList = new VBox(10);
        for (GameTypes.Region region : state.regions) {
            regionList.getChildren().add(new RegionCard(region));
        }

        content.getChildren().addAll(summary, stats, regionsTitle, regionList);
        return content;
    }

    private VBox buildEventsSidebar() {
        VBox content = new VBox(12);

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
        for (EventLogEntry entry : state.eventLog) {
            Button item = new Button(entry.eventName + " · Year " + entry.year);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle(eventLogItemStyle(entry.id.equals(selectedEventId)));
            item.setOnAction(e -> {
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
            tabButton.setOnAction(e -> {
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
            EventCard card = new EventCard(event);
            card.setOnAction(e -> triggerEvent(event));
            eventCards.getChildren().add(card);
        }

        if (filteredEvents.isEmpty()) {
            Label emptyState = new Label("No events available in this category yet.");
            emptyState.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            eventCards.getChildren().add(emptyState);
        }

        ScrollPane eventScroller = new ScrollPane(eventCards);
        eventScroller.setFitToHeight(true);
        eventScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        eventScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        eventScroller.setPannable(true);
        eventScroller.setStyle("-fx-background:#0f172a; -fx-background-color:transparent;");

        Label sectionTitle = new Label(EVENT_TAB_LABELS.get(activeEventTab));
        sectionTitle.setStyle("-fx-text-fill:white; -fx-font-size:16px; -fx-font-weight:bold;");

        eventBrowser.getChildren().addAll(sectionTitle, tabBar, eventScroller);
        return eventBrowser;
    }

    private void triggerEvent(GameEventDef event) {
        state = SimulationModel.applyEvent(state, event);
        activeSidebarTab = "events";
        if (!state.eventLog.isEmpty()) {
            selectedEventId = state.eventLog.get(0).id;
        }
        buildUI();
    }

    private void ensureSelectedEvent() {
        if (state.eventLog.isEmpty()) {
            selectedEventId = null;
            return;
        }

        if (selectedEventId == null || findSelectedEventEntry() == null) {
            selectedEventId = state.eventLog.get(0).id;
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
        for (GameEventDef event : GameTypes.GAME_EVENTS) {
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
        return String.format("%+.0f%%", value);
    }

    private void advanceYear() {
        state.year += 1;
        buildUI();
    }
}
