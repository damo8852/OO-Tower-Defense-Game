package com.towerdefense.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.TowerShot;
import com.towerdefense.model.tower.Tower;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.model.enemy.WaveSpawner;

public class GameEngine {

    private final GameMap map;
    private final GameState gameState;
    private final WaveSpawner waveSpawner;
    private final List<Tower> towers;
    private List<IEnemy> activeEnemies;
    private final Deque<IEnemy> spawnQueue = new ArrayDeque<>();
    private List<TowerShot> pendingShots = List.of();

    public GameEngine(GameMap map, GameState gameState, WaveSpawner waveSpawner) {
        this.map = map;
        this.gameState = gameState;
        this.waveSpawner = waveSpawner;
        this.towers = new ArrayList<>();
        this.activeEnemies = new ArrayList<>();
    }

    public void startNextWave() {
        gameState.nextWave();
        activeEnemies = new ArrayList<>();
        spawnQueue.clear();
        spawnQueue.addAll(waveSpawner.spawnWave(gameState.getWave(), map.getPath()));
    }

    public void tick() {
        if (!spawnQueue.isEmpty()) activeEnemies.add(spawnQueue.poll());
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

        // Record shots (for UI projectile animation) before any damage changes state.
        pendingShots = towers.stream()
                .flatMap(t -> t.collectShots(targetable).stream())
                .toList();

        // Phase 1: all towers select targets based on the same alive-at-tick-start snapshot.
        // Phase 2: all damage applied at once, so no tower can "steal" a target from another.
        towers.stream()
                .map(t -> t.planAttack(targetable))
                .filter(Objects::nonNull)
                .toList()
                .forEach(Runnable::run);
        rewardKills(targetable);
    }

    public List<TowerShot> drainShots() {
        List<TowerShot> shots = pendingShots;
        pendingShots = List.of();
        return shots;
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
        return spawnQueue.isEmpty() && activeEnemies.isEmpty();
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
