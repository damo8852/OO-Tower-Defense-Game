package com.towerdefense.app;

import java.io.IOException;
import java.util.List;

import com.towerdefense.command.CommandHistory;
import com.towerdefense.command.UpgradeTowerCommand;
import com.towerdefense.engine.GameEngine;
import com.towerdefense.eventbus.EventBus;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.MapType;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TowerDefenseApp extends Application {
    private static final int DEFAULT_LIVES = 10;
    private static final int DEFAULT_GOLD = 500;
    private static final long TICK_INTERVAL_NS = 600_000_000L;
    private static final String SAVE_FILE = "save.json";
    private static final String WINDOW_TITLE = "Tower Defense";
    private static final int RIGHT_PANEL_SPACING = 8;
    private static final int RIGHT_PANEL_PADDING = 10;
    private static final int RIGHT_PANEL_WIDTH = 270;
    private static final int DEFAULT_WINDOW_WIDTH = 830;
    private static final int DEFAULT_WINDOW_HEIGHT = 620;
    private static final int MIN_WINDOW_WIDTH = 500;
    private static final int MIN_WINDOW_HEIGHT = 400;
    private static final String INSTRUCTION_STYLE = "-fx-font-size: 11;";
    private static final String INSTRUCTION_TEXT = """
            Left-click empty cell  → place tower
            Left-click tower cell  → upgrade (%dg)
            Right-click tower cell → sell""".formatted(UpgradeTowerCommand.UPGRADE_COST);

    private static final String DARK_BG = "#1a2e1a";
    private static final String BTN_BASE = "-fx-font-size: 18; -fx-min-width: 220; -fx-min-height: 50; " +
        "-fx-background-color: #2d5a2d; -fx-text-fill: white; " +
        "-fx-border-color: #4a8a4a; -fx-border-width: 2; " +
        "-fx-border-radius: 4; -fx-background-radius: 4;";
    private static final String BTN_HOVER = "-fx-font-size: 18; -fx-min-width: 220; -fx-min-height: 50; " +
        "-fx-background-color: #3d7a3d; -fx-text-fill: white; " +
        "-fx-border-color: #4a8a4a; -fx-border-width: 2; " +
        "-fx-border-radius: 4; -fx-background-radius: 4;";

    private Stage primaryStage;
    private AnimationTimer gameLoop;
    private HudView hudView;

    private GameEngine engine;
    private GameState gameState;
    private CommandHistory commandHistory;
    private MapGridView mapGridView;
    private Label statusLabel;
    private Label gameOverScoreLabel;
    private VBox gameOverOverlay;
    private boolean waveRunning  = false;
    private MapType currentMapType = MapType.U_SHAPE;

    // shows the start screen, javafx entry
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

    // builds the main menu 
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

    // inits domain and launches the game
    private void startNewGame() {
        detachHudView();
        buildDomain();
        launchGame();
    }

    // inits domain objects, launches the game, loads last save
    private void startLoadGame() {
        detachHudView();
        buildDomain();
        launchGame();
        loadGame();
    }

    // removes hud from the event bus - prevents stale listeners when restarting
    private void detachHudView() {
        if (hudView != null) {
            EventBus.getInstance().detach(hudView);
            hudView = null;
        }
    }

    // create the core objects: map, game state, wave spawner, engine, and command history
    private void buildDomain() {
        GameMap map = new GameMap.Builder().size(10, 10).shape(currentMapType).build();
        gameState = new GameState(DEFAULT_LIVES, DEFAULT_GOLD);
        WaveSpawner waveSpawner = new WaveSpawner(List.of(
                new EnemyFactory.Basic(), new EnemyFactory.Fast(), new EnemyFactory.Armored()));
        engine = new GameEngine(map, gameState, waveSpawner);
        commandHistory = new CommandHistory();
    }

    // constructs and displays the full game and the game loop
    private void launchGame() {
        if (gameLoop != null) gameLoop.stop();
        waveRunning = false;

        hudView = new HudView(DEFAULT_LIVES, 0, 0, DEFAULT_GOLD);
        hudView.initialize();
        TowerSelectionPanel selectionPanel = new TowerSelectionPanel();
        mapGridView = new MapGridView(engine, gameState, commandHistory, selectionPanel);

        statusLabel = new Label("Press Start Wave to begin.");
        statusLabel.setStyle("-fx-text-fill: white;");
        gameOverOverlay = buildGameOverOverlay();

        VBox rightPanel = buildRightPanel(hudView, selectionPanel);
        rightPanel.setPrefWidth(RIGHT_PANEL_WIDTH);
        rightPanel.setMinWidth(RIGHT_PANEL_WIDTH);
        rightPanel.setMaxWidth(RIGHT_PANEL_WIDTH);

        // fills space and centers map grid
        StackPane mapContainer = new StackPane(mapGridView);
        mapContainer.setStyle("-fx-background-color: " + DARK_BG + ";");

        BorderPane layout = new BorderPane();
        layout.setCenter(mapContainer);
        layout.setRight(rightPanel);

        StackPane root = new StackPane(layout, gameOverOverlay);
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        Scene gameScene = new Scene(root);
        gameScene.setFill(Color.web(DARK_BG));

        // resize when window size changes
        mapContainer.widthProperty().addListener((obs, old, w) ->
                mapGridView.resize(w.doubleValue(), mapContainer.getHeight()));
        mapContainer.heightProperty().addListener((obs, old, h) ->
                mapGridView.resize(mapContainer.getWidth(), h.doubleValue()));

        primaryStage.setScene(gameScene);
        startGameLoop();
    }

    // game over overlay
    private VBox buildGameOverOverlay() {
        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 52; -fx-font-weight: bold; -fx-text-fill: white;");

        gameOverScoreLabel = new Label();
        gameOverScoreLabel.setStyle("-fx-font-size: 26; -fx-text-fill: white;");

        RadioButton uShapeRb = new RadioButton("U-Shape");
        RadioButton sShapeRb = new RadioButton("S-Shape");
        uShapeRb.setStyle("-fx-text-fill: white;");
        sShapeRb.setStyle("-fx-text-fill: white;");
        ToggleGroup mapToggle = new ToggleGroup();
        uShapeRb.setToggleGroup(mapToggle);
        sShapeRb.setToggleGroup(mapToggle);
        if (currentMapType == MapType.U_SHAPE) {
            uShapeRb.setSelected(true);
        } else {
            sShapeRb.setSelected(true);
        }

        Label selectMapLabel = new Label("Select Map:");
        selectMapLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Button playAgainBtn = new Button("Play Again");
        Button menuBtn      = new Button("Main Menu");
        playAgainBtn.setStyle("-fx-font-size: 16; -fx-min-width: 180;");
        menuBtn.setStyle("-fx-font-size: 16; -fx-min-width: 180;");

        playAgainBtn.setOnAction(e -> {
            if (uShapeRb.isSelected()) {
                currentMapType = MapType.U_SHAPE;
            } else {
                currentMapType = MapType.S_SHAPE;
            }
            restartGame();
        });
        menuBtn.setOnAction(e -> goToMainMenu());

        VBox overlay = new VBox(16, title, gameOverScoreLabel, selectMapLabel, uShapeRb, sShapeRb, playAgainBtn, menuBtn);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.72);");
        overlay.setVisible(false);
        return overlay;
    }

    // removes the current game session and starts a new one
    private void restartGame() {
        detachHudView();
        buildDomain();
        launchGame();
    }

    // interrupts game loop and returns to the main menu screen
    private void goToMainMenu() {
        if (gameLoop != null) gameLoop.stop();
        waveRunning = false;
        detachHudView();
        primaryStage.setScene(buildStartScene());
    }

    // right side hud with status, tower selection, and controls
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

    // start animationtimer that drives game ticks at the set interval
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

    // after each tick check if wave complete 
    private void checkWaveEnd() {
        if (gameState.isGameOver()) {
            showGameOver();
        } else if (engine.isWaveComplete()) {
            waveRunning = false;
            statusLabel.setText("Wave " + gameState.getWave() + " complete! Press Start Wave.");
        }
    }

    // shows game over overlay
    private void showGameOver() {
        waveRunning = false;
        statusLabel.setText("GAME OVER — final score: " + gameState.getScore());
        gameOverScoreLabel.setText("Final Score: " + gameState.getScore());
        gameOverOverlay.setVisible(true);
    }

    // start next wave when not already running and game not over
    private void startWave() {
        if (waveRunning || gameState.isGameOver()) return;
        engine.startNextWave();
        waveRunning = true;
        statusLabel.setText("Wave " + gameState.getWave() + " in progress...");
    }

    // pops and undoes the most recent command, then refreshes the map grid to update visuals
    private void undoLastCommand() {
        commandHistory.undo();
        mapGridView.refresh();
    }

    // serializes command history to JSON and saves
    private void saveGame() {
        try {
            new GameSaveService().save(commandHistory.getRecords(), SAVE_FILE, gameState);
            statusLabel.setText("Saved to " + SAVE_FILE);
        } catch (IOException ex) {
            showAlert("Save failed: " + ex.getMessage());
        }
    }

    // resets state and reads command history from JSON to restore game state
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

    // displays an alert
    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    // app entry point
    public static void main(String[] args) {
        launch(args);
    }
}
