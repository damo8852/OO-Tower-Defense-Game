package com.towerdefense.app;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.eventbus.EventBus;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.command.CommandHistory;
import com.towerdefense.command.UpgradeTowerCommand;
import com.towerdefense.model.enemy.EnemyFactory;
import com.towerdefense.model.enemy.WaveSpawner;
import com.towerdefense.persistence.GameLoadService;
import com.towerdefense.persistence.GameSaveService;
import com.towerdefense.ui.HudView;
import com.towerdefense.ui.MapGridView;
import com.towerdefense.ui.TowerSelectionPanel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class TowerDefenseApp extends Application {

    private static final int    MAP_ROWS              = 10;
    private static final int    MAP_COLS              = 10;
    private static final int    DEFAULT_LIVES         = 10;
    private static final int    DEFAULT_GOLD          = 500;
    private static final long   TICK_INTERVAL_NS      = 600_000_000L;
    private static final String SAVE_FILE             = "save.json";
    private static final String WINDOW_TITLE          = "Tower Defense";
    private static final int    RIGHT_PANEL_SPACING   = 8;
    private static final int    RIGHT_PANEL_PADDING   = 10;
    private static final int    RIGHT_PANEL_WIDTH     = 270;
    private static final int    DEFAULT_WINDOW_WIDTH  = 830;
    private static final int    DEFAULT_WINDOW_HEIGHT = 620;
    private static final int    MIN_WINDOW_WIDTH      = 500;
    private static final int    MIN_WINDOW_HEIGHT     = 400;
    private static final String INSTRUCTION_STYLE     = "-fx-font-size: 11;";
    private static final String INSTRUCTION_TEXT      =
            "Left-click empty cell  → place tower\n" +
            "Left-click tower cell  → upgrade (" + UpgradeTowerCommand.UPGRADE_COST + "g)\n" +
            "Right-click tower cell → sell";

    private static final String DARK_BG     = "#1a2e1a";
    private static final String BTN_BASE    = "-fx-font-size: 18; -fx-min-width: 220; -fx-min-height: 50; " +
                                              "-fx-background-color: #2d5a2d; -fx-text-fill: white; " +
                                              "-fx-border-color: #4a8a4a; -fx-border-width: 2; " +
                                              "-fx-border-radius: 4; -fx-background-radius: 4;";
    private static final String BTN_HOVER   = "-fx-font-size: 18; -fx-min-width: 220; -fx-min-height: 50; " +
                                              "-fx-background-color: #3d7a3d; -fx-text-fill: white; " +
                                              "-fx-border-color: #4a8a4a; -fx-border-width: 2; " +
                                              "-fx-border-radius: 4; -fx-background-radius: 4;";

    private Stage          primaryStage;
    private AnimationTimer gameLoop;
    private HudView        hudView;

    private GameEngine     engine;
    private GameState      gameState;
    private CommandHistory commandHistory;
    private MapGridView    mapGridView;
    private Label          statusLabel;
    private Label          gameOverScoreLabel;
    private VBox           gameOverOverlay;
    private boolean        waveRunning = false;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(true);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setWidth(DEFAULT_WINDOW_WIDTH);
        stage.setHeight(DEFAULT_WINDOW_HEIGHT);
        stage.setScene(buildStartScene());
        stage.show();
    }

    // ── Start screen ──────────────────────────────────────────────

    private Scene buildStartScene() {
        Label title = new Label("TOWER DEFENSE");
        title.setStyle("-fx-font-size: 56; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Defend your base from waves of enemies");
        subtitle.setStyle("-fx-font-size: 16; -fx-text-fill: white;");

        Button newGameBtn  = new Button("New Game");
        Button loadGameBtn = new Button("Load Game");

        for (Button b : List.of(newGameBtn, loadGameBtn)) {
            b.setStyle(BTN_BASE);
            b.setOnMouseEntered(e -> b.setStyle(BTN_HOVER));
            b.setOnMouseExited(e  -> b.setStyle(BTN_BASE));
        }

        newGameBtn.setOnAction(e  -> startNewGame());
        loadGameBtn.setOnAction(e -> startLoadGame());

        VBox box = new VBox(24, title, subtitle, new Separator(), newGameBtn, loadGameBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + DARK_BG + ";");

        Scene scene = new Scene(box);
        scene.setFill(Color.web(DARK_BG));
        return scene;
    }

    private void startNewGame() {
        detachHudView();
        buildDomain();
        launchGame();
    }

    private void startLoadGame() {
        detachHudView();
        buildDomain();
        launchGame();
        loadGame();
    }

    private void detachHudView() {
        if (hudView != null) {
            EventBus.getInstance().detach(hudView);
            hudView = null;
        }
    }

    // ── Game screen ───────────────────────────────────────────────

    private void buildDomain() {
        GameMap map = GameMap.Builder.sShapeMap();
        gameState = new GameState(DEFAULT_LIVES, DEFAULT_GOLD);
        WaveSpawner waveSpawner = new WaveSpawner(List.of(
                new EnemyFactory.Basic(), new EnemyFactory.Fast(), new EnemyFactory.Armored()));
        engine = new GameEngine(map, gameState, waveSpawner);
        commandHistory = new CommandHistory();
    }

    private void launchGame() {
        if (gameLoop != null) gameLoop.stop();
        waveRunning = false;

        hudView = new HudView(DEFAULT_LIVES, 0, 0, DEFAULT_GOLD);
        TowerSelectionPanel selectionPanel = new TowerSelectionPanel();
        mapGridView = new MapGridView(engine, gameState, commandHistory, selectionPanel);

        statusLabel    = new Label("Press Start Wave to begin.");
        statusLabel.setStyle("-fx-text-fill: white;");
        gameOverOverlay = buildGameOverOverlay();

        VBox rightPanel = buildRightPanel(hudView, selectionPanel);
        rightPanel.setPrefWidth(RIGHT_PANEL_WIDTH);
        rightPanel.setMinWidth(RIGHT_PANEL_WIDTH);
        rightPanel.setMaxWidth(RIGHT_PANEL_WIDTH);

        // mapContainer fills all space left of the right panel and centres the canvas
        StackPane mapContainer = new StackPane(mapGridView);
        mapContainer.setStyle("-fx-background-color: " + DARK_BG + ";");

        BorderPane layout = new BorderPane();
        layout.setCenter(mapContainer);
        layout.setRight(rightPanel);

        StackPane root = new StackPane(layout, gameOverOverlay);
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        Scene gameScene = new Scene(root);
        gameScene.setFill(Color.web(DARK_BG));

        // Resize canvas whenever the container changes size
        mapContainer.widthProperty().addListener((obs, old, w) ->
                mapGridView.resize(w.doubleValue(), mapContainer.getHeight()));
        mapContainer.heightProperty().addListener((obs, old, h) ->
                mapGridView.resize(mapContainer.getWidth(), h.doubleValue()));

        primaryStage.setScene(gameScene);
        startGameLoop();
    }

    private VBox buildGameOverOverlay() {
        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 52; -fx-font-weight: bold; -fx-text-fill: white;");

        gameOverScoreLabel = new Label();
        gameOverScoreLabel.setStyle("-fx-font-size: 26; -fx-text-fill: white;");

        Button menuBtn = new Button("Main Menu");
        menuBtn.setStyle("-fx-font-size: 16;");
        menuBtn.setOnAction(e -> goToMainMenu());

        VBox overlay = new VBox(20, title, gameOverScoreLabel, menuBtn);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.72);");
        overlay.setVisible(false);
        return overlay;
    }

    private void goToMainMenu() {
        if (gameLoop != null) gameLoop.stop();
        waveRunning = false;
        detachHudView();
        primaryStage.setScene(buildStartScene());
    }

    private VBox buildRightPanel(HudView hud, TowerSelectionPanel selectionPanel) {
        Button startBtn = new Button("Start Wave");
        Button undoBtn  = new Button("Undo");
        Button saveBtn  = new Button("Save");
        Button loadBtn  = new Button("Load");
        Button menuBtn  = new Button("Main Menu");

        for (Button b : List.of(startBtn, undoBtn, saveBtn, loadBtn, menuBtn)) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        startBtn.setOnAction(e -> startWave());
        undoBtn.setOnAction(e  -> undoLastCommand());
        saveBtn.setOnAction(e  -> saveGame());
        loadBtn.setOnAction(e  -> loadGame());
        menuBtn.setOnAction(e  -> goToMainMenu());

        Label instructions = new Label(INSTRUCTION_TEXT);
        instructions.setStyle(INSTRUCTION_STYLE + "-fx-text-fill: white;");

        VBox panel = new VBox(RIGHT_PANEL_SPACING,
                statusLabel, new Separator(),
                hud, new Separator(),
                selectionPanel, new Separator(),
                startBtn, undoBtn, saveBtn, loadBtn, menuBtn,
                new Separator(), instructions);
        panel.setPadding(new Insets(RIGHT_PANEL_PADDING));
        return panel;
    }

    // ── Game loop ─────────────────────────────────────────────────

    private void startGameLoop() {
        long[] lastTick = {0};
        gameLoop = new AnimationTimer() {
            @Override public void handle(long now) {
                if (waveRunning && now - lastTick[0] >= TICK_INTERVAL_NS) {
                    engine.tick();
                    lastTick[0] = now;
                    checkWaveEnd();
                }
                mapGridView.refresh();
            }
        };
        gameLoop.start();
    }

    // ── Wave / game control ───────────────────────────────────────

    private void checkWaveEnd() {
        if (gameState.isGameOver()) {
            showGameOver();
        } else if (engine.isWaveComplete()) {
            waveRunning = false;
            statusLabel.setText("Wave " + gameState.getWave() + " complete! Press Start Wave.");
        }
    }

    private void showGameOver() {
        waveRunning = false;
        statusLabel.setText("GAME OVER — final score: " + gameState.getScore());
        gameOverScoreLabel.setText("Final Score: " + gameState.getScore());
        gameOverOverlay.setVisible(true);
    }

    private void startWave() {
        if (waveRunning || gameState.isGameOver()) return;
        engine.startNextWave();
        waveRunning = true;
        statusLabel.setText("Wave " + gameState.getWave() + " in progress...");
    }

    private void undoLastCommand() {
        commandHistory.undo();
        mapGridView.refresh();
    }

    private void saveGame() {
        try {
            new GameSaveService().save(commandHistory.getRecords(), SAVE_FILE, gameState);
            statusLabel.setText("Saved to " + SAVE_FILE);
        } catch (IOException ex) {
            showAlert("Save failed: " + ex.getMessage());
        }
    }

    private void loadGame() {
        try {
            waveRunning = false;
            commandHistory.clear();
            engine.clearTowers();
            gameState.resetToInitial();
            new GameLoadService().load(SAVE_FILE, engine, gameState, commandHistory);
            mapGridView.refresh();
            statusLabel.setText("Loaded. Press Start Wave to continue.");
        } catch (IOException ex) {
            showAlert("Load failed: " + ex.getMessage());
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
