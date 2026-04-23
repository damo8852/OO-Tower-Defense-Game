package com.towerdefense.app;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.pattern.command.CommandHistory;
import com.towerdefense.pattern.command.UpgradeTowerCommand;
import com.towerdefense.pattern.factory.ArmoredEnemyFactory;
import com.towerdefense.pattern.factory.BasicEnemyFactory;
import com.towerdefense.pattern.factory.FastEnemyFactory;
import com.towerdefense.pattern.factory.WaveSpawner;
import com.towerdefense.persistence.GameLoadService;
import com.towerdefense.persistence.GameSaveService;
import com.towerdefense.ui.HudView;
import com.towerdefense.ui.MapGridView;
import com.towerdefense.ui.TowerSelectionPanel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class TowerDefenseApp extends Application {

    private static final int    MAP_ROWS             = 10;
    private static final int    MAP_COLS             = 10;
    private static final int    INITIAL_LIVES        = 10;
    private static final int    INITIAL_GOLD         = 500;
    private static final long   TICK_INTERVAL_NS     = 300_000_000L;
    private static final String SAVE_FILE            = "save.json";
    private static final String WINDOW_TITLE         = "Tower Defense";
    private static final int    RIGHT_PANEL_SPACING  = 8;
    private static final int    RIGHT_PANEL_PADDING  = 10;
    private static final int    RIGHT_PANEL_WIDTH    = 270;
    private static final String INSTRUCTION_STYLE    = "-fx-font-size: 11;";
    private static final String INSTRUCTION_TEXT     =
            "Left-click empty cell  → place tower\n" +
            "Left-click tower cell  → upgrade (" + UpgradeTowerCommand.UPGRADE_COST + "g)\n" +
            "Right-click tower cell → sell";

    private GameEngine     engine;
    private GameState      gameState;
    private CommandHistory commandHistory;
    private MapGridView    mapGridView;
    private Label          statusLabel;
    private boolean        waveRunning = false;

    @Override
    public void start(Stage stage) {
        buildDomain();
        HudView hudView = buildHud();
        TowerSelectionPanel selectionPanel = new TowerSelectionPanel();
        mapGridView = new MapGridView(engine, gameState, commandHistory, selectionPanel);
        buildStage(stage, hudView, selectionPanel);
        startGameLoop();
    }

    private void buildDomain() {
        GameMap map = new GameMap(MAP_ROWS, MAP_COLS);
        gameState = new GameState(INITIAL_LIVES, INITIAL_GOLD);
        WaveSpawner waveSpawner = new WaveSpawner(List.of(
                new BasicEnemyFactory(), new FastEnemyFactory(), new ArmoredEnemyFactory()));
        engine = new GameEngine(map, gameState, waveSpawner);
        commandHistory = new CommandHistory();
    }

    private HudView buildHud() {
        HudView hud = new HudView(INITIAL_LIVES, 0, 0, INITIAL_GOLD);
        gameState.addObserver(hud);
        return hud;
    }

    private void buildStage(Stage stage, HudView hudView, TowerSelectionPanel selectionPanel) {
        statusLabel = new Label("Press Start Wave to begin.");
        VBox rightPanel = buildRightPanel(hudView, selectionPanel);
        HBox root = new HBox(mapGridView, rightPanel);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private VBox buildRightPanel(HudView hudView, TowerSelectionPanel selectionPanel) {
        Button startBtn = new Button("Start Wave");
        Button undoBtn  = new Button("Undo");
        Button saveBtn  = new Button("Save");
        Button loadBtn  = new Button("Load");

        for (Button b : List.of(startBtn, undoBtn, saveBtn, loadBtn)) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        startBtn.setOnAction(e -> startWave());
        undoBtn.setOnAction(e  -> undoLastCommand());
        saveBtn.setOnAction(e  -> saveGame());
        loadBtn.setOnAction(e  -> loadGame());

        Label instructions = new Label(INSTRUCTION_TEXT);
        instructions.setStyle(INSTRUCTION_STYLE);

        VBox panel = new VBox(RIGHT_PANEL_SPACING,
                statusLabel, new Separator(),
                hudView, new Separator(),
                selectionPanel, new Separator(),
                startBtn, undoBtn, saveBtn, loadBtn,
                new Separator(), instructions);
        panel.setPadding(new Insets(RIGHT_PANEL_PADDING));
        panel.setPrefWidth(RIGHT_PANEL_WIDTH);
        return panel;
    }

    private void startGameLoop() {
        long[] lastTick = {0};
        new AnimationTimer() {
            @Override public void handle(long now) {
                if (waveRunning && now - lastTick[0] >= TICK_INTERVAL_NS) {
                    engine.tick();
                    lastTick[0] = now;
                    checkWaveEnd();
                }
                mapGridView.refresh();
            }
        }.start();
    }

    private void checkWaveEnd() {
        if (gameState.isGameOver()) {
            waveRunning = false;
            statusLabel.setText("GAME OVER — final score: " + gameState.getScore());
        } else if (engine.isWaveComplete()) {
            waveRunning = false;
            statusLabel.setText("Wave " + gameState.getWave() + " complete! Press Start Wave.");
        }
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
