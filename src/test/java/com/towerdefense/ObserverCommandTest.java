package com.towerdefense;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.Tower;
import com.towerdefense.model.tower.BasicTower;
import com.towerdefense.model.tower.SniperTower;
import com.towerdefense.pattern.command.*;
import com.towerdefense.pattern.factory.BasicEnemyFactory;
import com.towerdefense.pattern.factory.WaveSpawner;
import com.towerdefense.pattern.observer.GameObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ObserverCommandTest {

    private GameMap map;
    private GameState state;
    private GameEngine engine;
    private CommandHistory history;

    @Before
    public void setUp() {
        map = new GameMap(5, 5);
        state = new GameState(20, 500);
        WaveSpawner spawner = new WaveSpawner(List.of(new BasicEnemyFactory()));
        engine = new GameEngine(map, state, spawner);
        history = new CommandHistory();
    }

    // --- Observer tests ---

    @Test
    public void observer_notifiedOnLivesChange() {
        List<Integer> received = new ArrayList<>();
        state.addObserver(observerWith(lives -> received.add(lives), s -> {}, w -> {}, g -> {}));

        state.loseLife();

        assertEquals(1, received.size());
        assertEquals(19, (int) received.get(0));
    }

    @Test
    public void observer_notifiedOnScoreChange() {
        List<Integer> received = new ArrayList<>();
        state.addObserver(observerWith(l -> {}, score -> received.add(score), w -> {}, g -> {}));

        state.addScore(50);

        assertEquals(1, received.size());
        assertEquals(50, (int) received.get(0));
    }

    @Test
    public void observer_notifiedOnWaveChange() {
        List<Integer> received = new ArrayList<>();
        state.addObserver(observerWith(l -> {}, s -> {}, wave -> received.add(wave), g -> {}));

        state.nextWave();
        state.nextWave();

        assertEquals(2, received.size());
        assertEquals(2, (int) received.get(1));
    }

    @Test
    public void observer_notifiedOnGoldChange() {
        List<Integer> received = new ArrayList<>();
        state.addObserver(observerWith(l -> {}, s -> {}, w -> {}, gold -> received.add(gold)));

        state.spendGold(100);
        state.addGold(50);

        assertEquals(2, received.size());
        assertEquals(400, (int) received.get(0));
        assertEquals(450, (int) received.get(1));
    }

    @Test
    public void multipleObservers_allNotified() {
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        state.addObserver(observerWith(l -> {}, s -> {}, w -> {}, gold -> a.add(gold)));
        state.addObserver(observerWith(l -> {}, s -> {}, w -> {}, gold -> b.add(gold)));

        state.addGold(10);

        assertEquals(1, a.size());
        assertEquals(1, b.size());
    }

    // --- Command tests ---

    @Test
    public void placeTower_execute_addsTowerAndDeductsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        history.execute(new PlaceTowerCommand(engine, state, tower));

        assertEquals(1, engine.getTowers().size());
        assertEquals(400, state.getGold()); // 500 - 100
    }

    @Test
    public void placeTower_undo_removesTowerAndRefundsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        history.execute(new PlaceTowerCommand(engine, state, tower));
        history.undo();

        assertEquals(0, engine.getTowers().size());
        assertEquals(500, state.getGold());
    }

    @Test
    public void sellTower_execute_removesTowerAndAddsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        engine.addTower(tower);
        state.spendGold(tower.getCost()); // simulate prior purchase

        history.execute(new SellTowerCommand(engine, state, tower));

        assertEquals(0, engine.getTowers().size());
        assertEquals(450, state.getGold()); // (500 - 100) + 50 sell value
    }

    @Test
    public void sellTower_undo_restoresTowerAndDeductsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        engine.addTower(tower);
        state.spendGold(tower.getCost());

        history.execute(new SellTowerCommand(engine, state, tower));
        history.undo();

        assertEquals(1, engine.getTowers().size());
        assertEquals(400, state.getGold()); // back to post-purchase amount
    }

    @Test
    public void upgradeTower_execute_boostsDamageAndRange() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        int originalDamage = tower.getDamage();
        int originalRange = tower.getRange();

        history.execute(new UpgradeTowerCommand(tower, state));

        assertEquals(originalDamage + 10, tower.getDamage());
        assertEquals(originalRange + 1, tower.getRange());
        assertEquals(425, state.getGold()); // 500 - 75
    }

    @Test
    public void upgradeTower_undo_restoresStatsAndGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        int originalDamage = tower.getDamage();
        int originalRange = tower.getRange();

        history.execute(new UpgradeTowerCommand(tower, state));
        history.undo();

        assertEquals(originalDamage, tower.getDamage());
        assertEquals(originalRange, tower.getRange());
        assertEquals(500, state.getGold());
    }

    @Test
    public void commandHistory_multipleUndos_revertInOrder() {
        Tower t1 = new BasicTower(map.getCell(1, 0));
        Tower t2 = new SniperTower(map.getCell(2, 0));

        history.execute(new PlaceTowerCommand(engine, state, t1));
        history.execute(new PlaceTowerCommand(engine, state, t2));
        assertEquals(2, engine.getTowers().size());

        history.undo(); // undo sniper
        assertEquals(1, engine.getTowers().size());
        assertTrue(engine.getTowers().contains(t1));

        history.undo(); // undo basic
        assertEquals(0, engine.getTowers().size());
    }

    @Test
    public void commandHistory_undoWhenEmpty_doesNothing() {
        history.undo(); // should not throw
        assertFalse(history.canUndo());
    }

    // Helper to build a GameObserver from four lambdas without an anonymous class
    private GameObserver observerWith(
            java.util.function.IntConsumer onLives,
            java.util.function.IntConsumer onScore,
            java.util.function.IntConsumer onWave,
            java.util.function.IntConsumer onGold) {
        return new GameObserver() {
            public void onLivesChanged(int v) { onLives.accept(v); }
            public void onScoreChanged(int v) { onScore.accept(v); }
            public void onWaveChanged(int v)  { onWave.accept(v); }
            public void onGoldChanged(int v)  { onGold.accept(v); }
        };
    }
}
