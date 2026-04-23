package com.towerdefense.ui;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.Tower;
import com.towerdefense.model.TowerType;
import com.towerdefense.pattern.command.CommandHistory;
import com.towerdefense.pattern.command.PlaceTowerCommand;
import com.towerdefense.pattern.command.SellTowerCommand;
import com.towerdefense.pattern.command.UpgradeTowerCommand;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Map;

public class MapGridView extends Canvas {

    private static final int    TILE_SIZE         = 50;
    private static final String FONT_NAME         = "Arial";
    private static final int    FONT_LABEL_SIZE   = 16;
    private static final int    FONT_STATS_SIZE   = 9;
    private static final int    LABEL_X_OFFSET    = 17;
    private static final int    LABEL_Y_OFFSET    = 28;
    private static final int    STATS_X_OFFSET    = 4;
    private static final int    STATS_Y_OFFSET    = 44;
    private static final int    ENEMY_INSET       = 8;
    private static final int    ENEMY_Y_OFFSET    = 11;
    private static final int    HP_BAR_INSET      = 2;
    private static final int    HP_BAR_HEIGHT     = 5;
    private static final double GRID_STROKE_ALPHA = 0.35;

    private static final Color COLOR_PATH  = Color.SANDYBROWN;
    private static final Color COLOR_EMPTY = Color.color(0.18, 0.38, 0.18);
    private static final Color COLOR_HP_BG = Color.DARKRED;
    private static final Color COLOR_HP_FG = Color.LIMEGREEN;
    private static final Color COLOR_LABEL = Color.WHITE;

    private static final Map<TowerType, Color> TOWER_COLORS = Map.of(
        TowerType.BASIC,  Color.STEELBLUE,
        TowerType.SNIPER, Color.MEDIUMPURPLE,
        TowerType.SPLASH, Color.DARKORANGE
    );

    private static final Map<TowerType, String> TOWER_LABELS = Map.of(
        TowerType.BASIC,  "B",
        TowerType.SNIPER, "S",
        TowerType.SPLASH, "X"
    );

    private static final Map<String, Color> ENEMY_COLORS = Map.of(
        "BASIC",   Color.TOMATO,
        "FAST",    Color.GOLD,
        "ARMORED", Color.SILVER
    );

    private final GameEngine engine;
    private final GameState gameState;
    private final CommandHistory commandHistory;
    private final TowerSelectionPanel selectionPanel;

    public MapGridView(GameEngine engine, GameState gameState,
                       CommandHistory commandHistory, TowerSelectionPanel selectionPanel) {
        super(engine.getMap().getCols() * TILE_SIZE, engine.getMap().getRows() * TILE_SIZE);
        this.engine = engine;
        this.gameState = gameState;
        this.commandHistory = commandHistory;
        this.selectionPanel = selectionPanel;
        setOnMouseClicked(this::handleClick);
        refresh();
    }

    public void refresh() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGrid(gc);
        drawEnemies(gc);
    }

    // --- grid drawing ---

    private void drawGrid(GraphicsContext gc) {
        GameMap map = engine.getMap();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Cell cell = map.getCell(r, c);
                Tower tower = cell.getType() == Cell.Type.TOWER
                        ? engine.getTowerAt(r, c).orElse(null)
                        : null;
                drawCell(gc, r, c, cell, tower);
            }
        }
    }

    private void drawCell(GraphicsContext gc, int r, int c, Cell cell, Tower tower) {
        double x = c * TILE_SIZE;
        double y = r * TILE_SIZE;

        gc.setFill(cellColor(cell.getType(), tower));
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        gc.setStroke(Color.color(0, 0, 0, GRID_STROKE_ALPHA));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);

        if (tower != null) drawTower(gc, x, y, tower);
    }

    private Color cellColor(Cell.Type type, Tower tower) {
        return switch (type) {
            case PATH  -> COLOR_PATH;
            case EMPTY -> COLOR_EMPTY;
            case TOWER -> TOWER_COLORS.getOrDefault(
                    tower != null ? tower.getTowerType() : TowerType.BASIC, Color.GRAY);
        };
    }

    private void drawTower(GraphicsContext gc, double x, double y, Tower tower) {
        gc.setFill(COLOR_LABEL);
        gc.setFont(Font.font(FONT_NAME, FontWeight.BOLD, FONT_LABEL_SIZE));
        gc.fillText(TOWER_LABELS.getOrDefault(tower.getTowerType(), "?"), x + LABEL_X_OFFSET, y + LABEL_Y_OFFSET);
        gc.setFont(Font.font(FONT_NAME, FONT_STATS_SIZE));
        gc.fillText("D:" + tower.getDamage() + " R:" + tower.getRange(), x + STATS_X_OFFSET, y + STATS_Y_OFFSET);
    }

    // --- enemy drawing ---

    private void drawEnemies(GraphicsContext gc) {
        engine.getActiveEnemies().forEach(e -> drawEnemy(gc, e));
    }

    private void drawEnemy(GraphicsContext gc, Enemy e) {
        Cell pos = e.getPosition();
        double x = pos.getCol() * TILE_SIZE;
        double y = pos.getRow() * TILE_SIZE;

        double hpRatio = (double) e.getHealth() / e.getMaxHealth();
        gc.setFill(COLOR_HP_BG);
        gc.fillRect(x + HP_BAR_INSET, y + HP_BAR_INSET,
                TILE_SIZE - HP_BAR_INSET * 2, HP_BAR_HEIGHT);
        gc.setFill(COLOR_HP_FG);
        gc.fillRect(x + HP_BAR_INSET, y + HP_BAR_INSET,
                (TILE_SIZE - HP_BAR_INSET * 2) * hpRatio, HP_BAR_HEIGHT);

        gc.setFill(ENEMY_COLORS.getOrDefault(e.getTypeName(), Color.RED));
        gc.fillOval(x + ENEMY_INSET, y + ENEMY_Y_OFFSET,
                TILE_SIZE - ENEMY_INSET * 2, TILE_SIZE - ENEMY_INSET * 2);
    }

    // --- click handling ---

    private void handleClick(MouseEvent event) {
        int col = (int) (event.getX() / TILE_SIZE);
        int row = (int) (event.getY() / TILE_SIZE);
        GameMap map = engine.getMap();
        if (row < 0 || row >= map.getRows() || col < 0 || col >= map.getCols()) return;

        Cell cell = map.getCell(row, col);
        if (event.getButton() == MouseButton.PRIMARY) {
            handlePrimaryClick(cell, row, col);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            if (cell.getType() == Cell.Type.TOWER) sellTower(row, col);
        }
    }

    private void handlePrimaryClick(Cell cell, int row, int col) {
        switch (cell.getType()) {
            case EMPTY -> placeTower(cell);
            case TOWER -> upgradeTower(row, col);
            default    -> {}
        }
    }

    private void placeTower(Cell cell) {
        Tower tower = selectionPanel.createTower(cell);
        if (tower == null || gameState.getGold() < tower.getCost()) return;
        commandHistory.execute(new PlaceTowerCommand(engine, gameState, tower));
        refresh();
    }

    private void upgradeTower(int row, int col) {
        if (gameState.getGold() < UpgradeTowerCommand.UPGRADE_COST) return;
        engine.getTowerAt(row, col).ifPresent(t -> {
            commandHistory.execute(new UpgradeTowerCommand(t, gameState));
            refresh();
        });
    }

    private void sellTower(int row, int col) {
        engine.getTowerAt(row, col).ifPresent(t -> {
            commandHistory.execute(new SellTowerCommand(engine, gameState, t));
            refresh();
        });
    }
}
