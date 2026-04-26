package com.towerdefense;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.Tower;
import com.towerdefense.model.tower.BasicTower;
import com.towerdefense.model.tower.SniperTower;
import com.towerdefense.command.*;
import com.towerdefense.eventbus.EventBus;
import com.towerdefense.eventbus.EventType;
import com.towerdefense.eventbus.ITowerDefenseObserver;
import com.towerdefense.model.enemy.EnemyFactory;
import com.towerdefense.model.enemy.WaveSpawner;
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
        EventBus.resetForTesting();
        map = new GameMap.Builder().size(5, 5).withUPath().build();
        state = new GameState(20, 500);
        WaveSpawner spawner = new WaveSpawner(List.of(new EnemyFactory.Basic()));
        engine = new GameEngine(map, state, spawner);
        history = new CommandHistory();
    }

    @Test
    public void observer_notifiedOnLivesChange() {
        List<Integer> received = new ArrayList<>();
        EventBus.getInstance().attach(eventObserver(EventType.LIVES_CHANGED, received));

        state.loseLife();

        assertEquals(1, received.size());
        assertEquals(19, (int) received.get(0));
    }

    @Test
    public void observer_notifiedOnScoreChange() {
        List<Integer> received = new ArrayList<>();
        EventBus.getInstance().attach(eventObserver(EventType.SCORE_CHANGED, received));

        state.addScore(50);

        assertEquals(1, received.size());
        assertEquals(50, (int) received.get(0));
    }

    @Test
    public void observer_notifiedOnWaveChange() {
        List<Integer> received = new ArrayList<>();
        EventBus.getInstance().attach(eventObserver(EventType.WAVE_CHANGED, received));

        state.nextWave();
        state.nextWave();

        assertEquals(2, received.size());
        assertEquals(2, (int) received.get(1));
    }

    @Test
    public void observer_notifiedOnGoldChange() {
        List<Integer> received = new ArrayList<>();
        EventBus.getInstance().attach(eventObserver(EventType.GOLD_CHANGED, received));

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
        EventBus.getInstance().attach(eventObserver(EventType.GOLD_CHANGED, a));
        EventBus.getInstance().attach(eventObserver(EventType.GOLD_CHANGED, b));

        state.addGold(10);

        assertEquals(1, a.size());
        assertEquals(1, b.size());
    }

    @Test
    public void placeTower_execute_addsTowerAndDeductsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        history.execute(new PlaceTowerCommand(engine, state, tower));

        assertEquals(1, engine.getTowers().size());
        assertEquals(400, state.getGold());
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
        state.spendGold(tower.getCost());

        history.execute(new SellTowerCommand(engine, state, tower));

        assertEquals(0, engine.getTowers().size());
        assertEquals(450, state.getGold());
    }

    @Test
    public void sellTower_undo_restoresTowerAndDeductsGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        engine.addTower(tower);
        state.spendGold(tower.getCost());

        history.execute(new SellTowerCommand(engine, state, tower));
        history.undo();

        assertEquals(1, engine.getTowers().size());
        assertEquals(400, state.getGold());
    }

    @Test
    public void upgradeTower_execute_boostsDamageAndRange() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        int originalDamage = tower.getDamage();
        int originalRange  = tower.getRange();

        history.execute(new UpgradeTowerCommand(tower, state));

        assertEquals(originalDamage + 10, tower.getDamage());
        assertEquals(originalRange  + 1,  tower.getRange());
        assertEquals(425, state.getGold());
    }

    @Test
    public void upgradeTower_undo_restoresStatsAndGold() {
        Tower tower = new BasicTower(map.getCell(1, 0));
        int originalDamage = tower.getDamage();
        int originalRange  = tower.getRange();

        history.execute(new UpgradeTowerCommand(tower, state));
        history.undo();

        assertEquals(originalDamage, tower.getDamage());
        assertEquals(originalRange,  tower.getRange());
        assertEquals(500, state.getGold());
    }

    @Test
    public void commandHistory_multipleUndos_revertInOrder() {
        Tower t1 = new BasicTower(map.getCell(1, 0));
        Tower t2 = new SniperTower(map.getCell(2, 0));

        history.execute(new PlaceTowerCommand(engine, state, t1));
        history.execute(new PlaceTowerCommand(engine, state, t2));
        assertEquals(2, engine.getTowers().size());

        history.undo();
        assertEquals(1, engine.getTowers().size());
        assertTrue(engine.getTowers().contains(t1));

        history.undo();
        assertEquals(0, engine.getTowers().size());
    }

    @Test
    public void commandHistory_undoWhenEmpty_doesNothing() {
        history.undo();
        assertFalse(history.canUndo());
    }

    private ITowerDefenseObserver eventObserver(EventType targetType, List<Integer> received) {
        return (eventType, eventObject) -> {
            if (eventType == targetType) received.add((Integer) eventObject);
        };
    }
}
