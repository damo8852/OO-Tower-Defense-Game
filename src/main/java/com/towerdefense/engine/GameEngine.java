package com.towerdefense.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.Tower;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.model.enemy.WaveSpawner;

public class GameEngine {

    private final GameMap map;
    private final GameState gameState;
    private final WaveSpawner waveSpawner;
    private final List<Tower> towers;
    private List<IEnemy> activeEnemies;

    public GameEngine(GameMap map, GameState gameState, WaveSpawner waveSpawner) {
        this.map = map;
        this.gameState = gameState;
        this.waveSpawner = waveSpawner;
        this.towers = new ArrayList<>();
        this.activeEnemies = new ArrayList<>();
    }

    public void startNextWave() {
        gameState.nextWave();
        activeEnemies = waveSpawner.spawnWave(gameState.getWave(), map.getPath());
    }

    public void tick() {
        List<IEnemy> inPlay = getEnemiesInPlay();
        moveEnemies(inPlay);
        processEscapes(inPlay);
        processCombat(inPlay);
        cleanupDefeatedEnemies();
    }

    private List<IEnemy> getEnemiesInPlay() {
        return activeEnemies.stream()
                .filter(e -> e.isAlive() && !e.hasReachedEnd())
                .toList();
    }

    private void moveEnemies(List<IEnemy> enemies) {
        enemies.forEach(IEnemy::move);
    }

    private void processEscapes(List<IEnemy> enemies) {
        enemies.stream()
                .filter(IEnemy::hasReachedEnd)
                .forEach(e -> gameState.loseLife());
    }

    private void processCombat(List<IEnemy> inPlay) {
        List<IEnemy> targetable = inPlay.stream()
                .filter(e -> !e.hasReachedEnd())
                .toList();
        towers.forEach(t -> t.attack(targetable));
        rewardKills(targetable);
    }

    private void rewardKills(List<IEnemy> targetable) {
        targetable.stream()
                .filter(e -> !e.isAlive())
                .forEach(e -> {
                    gameState.addScore(e.getReward());
                    gameState.addGold(e.getReward());
                });
    }

    private void cleanupDefeatedEnemies() {
        activeEnemies.removeIf(e -> !e.isAlive() || e.hasReachedEnd());
    }

    public void addTower(Tower tower) {
        towers.add(tower);
        map.placeTower(tower.getPosition().getRow(), tower.getPosition().getCol());
    }

    public void removeTower(Tower tower) {
        towers.remove(tower);
        map.removeTower(tower.getPosition().getRow(), tower.getPosition().getCol());
    }

    public void clearTowers() {
        List.copyOf(towers).forEach(this::removeTower);
    }

    public boolean isWaveComplete() {
        return activeEnemies.isEmpty();
    }

    public Optional<Tower> getTowerAt(int row, int col) {
        return towers.stream()
                .filter(t -> t.getPosition().getRow() == row && t.getPosition().getCol() == col)
                .findFirst();
    }

    public List<IEnemy> getActiveEnemies() { return List.copyOf(activeEnemies); }
    public List<Tower> getTowers()        { return List.copyOf(towers); }
    public GameState getGameState()       { return gameState; }
    public GameMap getMap()               { return map; }
}
