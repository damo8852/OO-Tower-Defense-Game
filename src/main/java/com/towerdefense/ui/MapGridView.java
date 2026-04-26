package com.towerdefense.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerShot;
import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.Tower;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.command.CommandHistory;
import com.towerdefense.command.PlaceTowerCommand;
import com.towerdefense.command.SellTowerCommand;
import com.towerdefense.command.UpgradeTowerCommand;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MapGridView extends Canvas {

    private static final int    DEFAULT_TILE_SIZE    = 50;
    private static final int    MIN_TILE_SIZE        = 20;
    private static final String FONT_NAME            = "Arial";
    private static final double GRID_STROKE_ALPHA    = 0.35;
    private static final long   PROJECTILE_TRAVEL_NS = 280_000_000L;

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

    // All pixel values scale proportionally with tileSize
    private int tileSize = DEFAULT_TILE_SIZE;

    private int    labelSize()   { return tileSize * 16 / DEFAULT_TILE_SIZE; }
    private int    statsSize()   { return tileSize * 9  / DEFAULT_TILE_SIZE; }
    private int    labelXOff()   { return tileSize * 17 / DEFAULT_TILE_SIZE; }
    private int    labelYOff()   { return tileSize * 28 / DEFAULT_TILE_SIZE; }
    private int    statsXOff()   { return tileSize * 4  / DEFAULT_TILE_SIZE; }
    private int    statsYOff()   { return tileSize * 44 / DEFAULT_TILE_SIZE; }
    private int    enemyInset()  { return tileSize * 8  / DEFAULT_TILE_SIZE; }
    private int    enemyYOff()   { return tileSize * 11 / DEFAULT_TILE_SIZE; }
    private int    hpBarInset()  { return Math.max(1, tileSize * 2 / DEFAULT_TILE_SIZE); }
    private int    hpBarHeight() { return Math.max(2, tileSize * 5 / DEFAULT_TILE_SIZE); }
    private int    stackXOff()   { return Math.max(1, tileSize * 8 / DEFAULT_TILE_SIZE); }
    private double projRadius()  { return tileSize * 5.0 / DEFAULT_TILE_SIZE; }

    private final GameEngine engine;
    private final GameState gameState;
    private final CommandHistory commandHistory;
    private final TowerSelectionPanel selectionPanel;
    private final List<Projectile> projectiles = new ArrayList<>();
    private long lastRefreshNanos = -1;

    public MapGridView(GameEngine engine, GameState gameState,
                       CommandHistory commandHistory, TowerSelectionPanel selectionPanel) {
        super(engine.getMap().getCols() * DEFAULT_TILE_SIZE,
              engine.getMap().getRows() * DEFAULT_TILE_SIZE);
        this.engine = engine;
        this.gameState = gameState;
        this.commandHistory = commandHistory;
        this.selectionPanel = selectionPanel;
        setOnMouseClicked(this::handleClick);
        refresh();
    }

    /** Called by the app whenever the available space changes (window resize / fullscreen). */
    public void resize(double availW, double availH) {
        int cols = engine.getMap().getCols();
        int rows = engine.getMap().getRows();
        int newSize = Math.max(MIN_TILE_SIZE, (int) Math.min(availW / cols, availH / rows));
        if (newSize == tileSize) return;
        tileSize = newSize;
        setWidth(cols * tileSize);
        setHeight(rows * tileSize);
        refresh();
    }

    public void refresh() {
        long now = System.nanoTime();

        engine.drainShots().forEach(shot -> projectiles.add(toProjectile(shot)));

        if (lastRefreshNanos > 0) {
            double fraction = (now - lastRefreshNanos) / (double) PROJECTILE_TRAVEL_NS;
            projectiles.forEach(p -> p.advance(fraction));
        }
        lastRefreshNanos = now;
        projectiles.removeIf(Projectile::isDone);

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGrid(gc);
        drawProjectiles(gc);
        drawEnemies(gc);
    }

    private Projectile toProjectile(TowerShot shot) {
        Color color = TOWER_COLORS.getOrDefault(shot.towerType(), Color.WHITE);
        return new Projectile(
                tileCenterX(shot.origin()), tileCenterY(shot.origin()),
                tileCenterX(shot.target()), tileCenterY(shot.target()),
                color);
    }

    private double tileCenterX(Tile t) { return t.getCol() * tileSize + tileSize / 2.0; }
    private double tileCenterY(Tile t) { return t.getRow() * tileSize + tileSize / 2.0; }

    // --- grid ---

    private void drawGrid(GraphicsContext gc) {
        GameMap map = engine.getMap();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Tile cell = map.getCell(r, c);
                Tower tower = cell.getType() == Tile.Type.TOWER
                        ? engine.getTowerAt(r, c).orElse(null)
                        : null;
                drawCell(gc, r, c, cell, tower);
            }
        }
    }

    private void drawCell(GraphicsContext gc, int r, int c, Tile cell, Tower tower) {
        double x = (double) c * tileSize;
        double y = (double) r * tileSize;
        gc.setFill(cellColor(cell.getType(), tower));
        gc.fillRect(x, y, tileSize, tileSize);
        gc.setStroke(Color.color(0, 0, 0, GRID_STROKE_ALPHA));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, tileSize, tileSize);
        if (tower != null) drawTower(gc, x, y, tower);
    }

    private Color cellColor(Tile.Type type, Tower tower) {
        return switch (type) {
            case PATH  -> COLOR_PATH;
            case EMPTY -> COLOR_EMPTY;
            case TOWER -> TOWER_COLORS.getOrDefault(
                    tower != null ? tower.getTowerType() : TowerType.BASIC, Color.GRAY);
        };
    }

    private void drawTower(GraphicsContext gc, double x, double y, Tower tower) {
        gc.setFill(COLOR_LABEL);
        gc.setFont(Font.font(FONT_NAME, FontWeight.BOLD, labelSize()));
        gc.fillText(TOWER_LABELS.getOrDefault(tower.getTowerType(), "?"), x + labelXOff(), y + labelYOff());
        gc.setFont(Font.font(FONT_NAME, statsSize()));
        gc.fillText("D:" + tower.getDamage() + " R:" + tower.getRange(), x + statsXOff(), y + statsYOff());
    }

    // --- projectiles ---

    private void drawProjectiles(GraphicsContext gc) {
        double r = projRadius();
        for (Projectile p : projectiles) {
            gc.setFill(p.color);
            gc.fillOval(p.x() - r, p.y() - r, r * 2, r * 2);
        }
    }

    // --- enemies ---

    private void drawEnemies(GraphicsContext gc) {
        Map<Tile, Integer> countByTile = new HashMap<>();
        for (IEnemy e : engine.getActiveEnemies()) {
            int stackIdx = countByTile.merge(e.getPosition(), 1, Integer::sum) - 1;
            drawEnemy(gc, e, stackIdx * stackXOff());
        }
    }

    /** Darkens a base colour by 15 % per wave beyond wave 1, bottoming out at 30 % brightness. */
    private static Color waveColor(Color base, int wave) {
        double factor = Math.max(0.30, 1.0 - (wave - 1) * 0.15);
        return new Color(base.getRed() * factor, base.getGreen() * factor, base.getBlue() * factor, 1.0);
    }

    private void drawEnemy(GraphicsContext gc, IEnemy e, double xShift) {
        Tile pos = e.getPosition();
        double x = pos.getCol() * tileSize + xShift;
        double y = pos.getRow() * tileSize;

        double hpRatio = (double) e.getHealth() / e.getMaxHealth();
        int hpI = hpBarInset();
        int hpH = hpBarHeight();
        gc.setFill(COLOR_HP_BG);
        gc.fillRect(x + hpI, y + hpI, tileSize - hpI * 2, hpH);
        gc.setFill(COLOR_HP_FG);
        gc.fillRect(x + hpI, y + hpI, (tileSize - hpI * 2) * hpRatio, hpH);

        int ei = enemyInset();
        gc.setFill(waveColor(ENEMY_COLORS.getOrDefault(e.getTypeName(), Color.RED), e.getWave()));
        gc.fillOval(x + ei, y + enemyYOff(), tileSize - ei * 2, tileSize - ei * 2);
    }

    // --- clicks ---

    private void handleClick(MouseEvent event) {
        int col = (int) (event.getX() / tileSize);
        int row = (int) (event.getY() / tileSize);
        GameMap map = engine.getMap();
        if (row < 0 || row >= map.getRows() || col < 0 || col >= map.getCols()) return;

        Tile cell = map.getCell(row, col);
        if (event.getButton() == MouseButton.PRIMARY) {
            handlePrimaryClick(cell, row, col);
        } else if (event.getButton() == MouseButton.SECONDARY) {
            if (cell.getType() == Tile.Type.TOWER) sellTower(row, col);
        }
    }

    private void handlePrimaryClick(Tile cell, int row, int col) {
        switch (cell.getType()) {
            case EMPTY -> placeTower(cell);
            case TOWER -> upgradeTower(row, col);
            default    -> {}
        }
    }

    private void placeTower(Tile cell) {
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
