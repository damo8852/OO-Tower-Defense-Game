package com.towerdefense;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.BasicTower;
import com.towerdefense.model.tower.SniperTower;
import com.towerdefense.model.tower.SplashTower;
import com.towerdefense.model.enemy.EnemyFactory;
import com.towerdefense.model.enemy.WaveSpawner;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameEngineTest {

    private GameEngine engine;
    private GameMap map;
    private GameState state;

    @Before
    public void setUp() {
        map = new GameMap(5, 5);
        state = new GameState(20, 200);

        WaveSpawner spawner = new WaveSpawner(List.of(
                new EnemyFactory.Basic(),
                new EnemyFactory.Fast(),
                new EnemyFactory.Armored()
        ));

        engine = new GameEngine(map, state, spawner);

        engine.addTower(new BasicTower(map.getCell(1, 0)));
        engine.addTower(new SniperTower(map.getCell(2, 0)));
        engine.addTower(new SplashTower(map.getCell(1, 1)));
    }

    @Test
    public void wave1_allEnemiesRemovedWithinMaxTicks() {
        engine.startNextWave();
        assertEquals(1, state.getWave());
        assertFalse(engine.isWaveComplete());

        runWaveToCompletion(200);

        assertTrue("Wave 1 should complete within 200 ticks", engine.isWaveComplete());
    }

    @Test
    public void wave1_scoreIncreasesWhenEnemiesKilled() {
        engine.startNextWave();
        runWaveToCompletion(200);

        assertTrue("Score should increase after enemies are killed or escaped",
                state.getScore() > 0 || state.getLives() < 20);
    }

    @Test
    public void threeWaves_gameProgressesCorrectly() {
        for (int wave = 1; wave <= 3; wave++) {
            engine.startNextWave();
            assertEquals(wave, state.getWave());
            runWaveToCompletion(500);
            assertTrue("Wave " + wave + " should complete", engine.isWaveComplete());
        }
        assertEquals(3, state.getWave());
    }

    @Test
    public void livesDeductedWhenEnemyReachesEnd() {
        GameMap emptyMap = new GameMap(3, 3);
        GameState emptyState = new GameState(20, 0);
        WaveSpawner spawner = new WaveSpawner(List.of(new EnemyFactory.Basic()));
        GameEngine emptyEngine = new GameEngine(emptyMap, emptyState, spawner);

        emptyEngine.startNextWave();
        runEngineToCompletion(emptyEngine, 100);

        assertTrue("Lives should decrease when enemies escape", emptyState.getLives() < 20);
    }

    @Test
    public void armoredEnemyTakesHalfDamage() {
        com.towerdefense.model.enemy.ArmoredEnemy armored =
                new com.towerdefense.model.enemy.ArmoredEnemy(map.getPath(), 1);
        armored.takeDamage(100);
        assertEquals(150, armored.getHealth());
    }

    @Test
    public void fastEnemyMovesAtDoubleSpeed() {
        com.towerdefense.model.enemy.FastEnemy fast =
                new com.towerdefense.model.enemy.FastEnemy(map.getPath(), 1);
        com.towerdefense.model.enemy.BasicEnemy basic =
                new com.towerdefense.model.enemy.BasicEnemy(map.getPath(), 1);

        fast.move();
        basic.move();

        assertEquals(2, fast.getPathIndex());
        assertEquals(1, basic.getPathIndex());
    }

    private void runWaveToCompletion(int maxTicks) {
        runEngineToCompletion(engine, maxTicks);
    }

    private void runEngineToCompletion(GameEngine e, int maxTicks) {
        int ticks = 0;
        while (!e.isWaveComplete() && ticks < maxTicks) {
            e.tick();
            ticks++;
        }
    }
}
