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
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.model.enemy.WaveSpawner;
import com.towerdefense.model.tower.Tower;

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

    // increment wave and queue enemies to spawn
    public void startNextWave() {
        gameState.nextWave();
        activeEnemies = new ArrayList<>();
        spawnQueue.clear();
        spawnQueue.addAll(waveSpawner.spawnWave(gameState.getWave(), map.getPath()));
    }

    // one game step - spawn, move, handle escapes and combat
    public void tick() {
        if (!spawnQueue.isEmpty()) activeEnemies.add(spawnQueue.poll());
        List<IEnemy> inPlay = getEnemiesInPlay();
        moveEnemies(inPlay);
        processEscapes(inPlay);
        processCombat(inPlay);
        cleanupDefeatedEnemies();
    }

    // get alive enemies that havent reached the end
    private List<IEnemy> getEnemiesInPlay() {
        return activeEnemies.stream()
                .filter(e -> e.isAlive() && !e.hasReachedEnd())
                .toList();
    }

    // move all enemies one step
    private void moveEnemies(List<IEnemy> enemies) {
        enemies.forEach(IEnemy::move);
    }

    // lose a life for each enemy that escaped
    private void processEscapes(List<IEnemy> enemies) {
        enemies.stream()
                .filter(IEnemy::hasReachedEnd)
                .forEach(e -> gameState.loseLife());
    }

    // collect shots, run tower attacks, reward kills
    private void processCombat(List<IEnemy> inPlay) {
        List<IEnemy> targetable = inPlay.stream()
                .filter(e -> !e.hasReachedEnd())
                .toList();
        pendingShots = towers.stream().flatMap(t -> t.collectShots(targetable).stream()).toList();
        towers.stream()
                .map(t -> t.planAttack(targetable))
                .filter(Objects::nonNull)
                .toList()
                .forEach(Runnable::run);
        rewardKills(targetable);
    }

    // return and clear pending shots for the renderer
    public List<TowerShot> drainShots() {
        List<TowerShot> shots = pendingShots;
        pendingShots = List.of();
        return shots;
    }

    // add score and gold for each kill
    private void rewardKills(List<IEnemy> targetable) {
        targetable.stream()
                .filter(e -> !e.isAlive())
                .forEach(e -> {
                    gameState.addScore(e.getReward());
                    gameState.addGold(e.getReward());
                });
    }

    // remove dead or escaped enemies
    private void cleanupDefeatedEnemies() {
        activeEnemies.removeIf(e -> !e.isAlive() || e.hasReachedEnd());
    }

    // add tower and mark cell as occupied
    public void addTower(Tower tower) {
        towers.add(tower);
        map.placeTower(tower.getPosition().getRow(), tower.getPosition().getCol());
    }

    // remove tower and free the cell
    public void removeTower(Tower tower) {
        towers.remove(tower);
        map.removeTower(tower.getPosition().getRow(), tower.getPosition().getCol());
    }

    // remove all towers, used when loading
    public void clearTowers() {
        List.copyOf(towers).forEach(this::removeTower);
    }

    // true when spawn queue and active enemies are empty
    public boolean isWaveComplete() {
        return spawnQueue.isEmpty() && activeEnemies.isEmpty();
    }

    // find tower at given row, col
    public Optional<Tower> getTowerAt(int row, int col) {
        return towers.stream()
                .filter(t -> t.getPosition().getRow() == row && t.getPosition().getCol() == col)
                .findFirst();
    }

    // return active enemies
    public List<IEnemy> getActiveEnemies() { return List.copyOf(activeEnemies); }
    // return all placed towers
    public List<Tower> getTowers()        { return List.copyOf(towers); }
    // return game state
    public GameState getGameState()       { return gameState; }
    // return the map
    public GameMap getMap()               { return map; }
}
