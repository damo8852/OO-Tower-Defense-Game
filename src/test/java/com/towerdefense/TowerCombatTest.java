package com.towerdefense;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.eventbus.EventBus;
import com.towerdefense.model.GameMap;
import com.towerdefense.model.GameState;
import com.towerdefense.model.enemy.BasicEnemy;
import com.towerdefense.model.enemy.EnemyFactory;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.model.enemy.WaveSpawner;
import com.towerdefense.model.tower.BasicTower;
import com.towerdefense.model.tower.SplashTower;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TowerCombatTest {

    private GameMap map;

    @Before
    public void setUp() {
        EventBus.resetForTesting();
        map = new GameMap.Builder().size(5, 5).withUPath().build();
    }

    @Test
    public void basicTower_damagesEnemyInRange() {
        BasicEnemy enemy = new BasicEnemy(map.getPath(), 1);
        BasicTower tower = new BasicTower(map.getCell(1, 0));

        tower.attack(List.of(enemy));

        assertEquals(80, enemy.getHealth());
    }

    @Test
    public void basicTower_doesNotDamageEnemyOutOfRange() {
        BasicEnemy enemy = new BasicEnemy(map.getPath(), 1);
        BasicTower tower = new BasicTower(map.getCell(4, 0));

        tower.attack(List.of(enemy));

        assertEquals(100, enemy.getHealth());
    }

    @Test
    public void splashTower_damagesAllEnemiesInRange() {
        BasicEnemy e1 = new BasicEnemy(map.getPath(), 1);
        BasicEnemy e2 = new BasicEnemy(map.getPath(), 1);
        e2.move();
        SplashTower splash = new SplashTower(map.getCell(1, 0));

        splash.attack(List.of(e1, e2));

        assertEquals(90, e1.getHealth());
        assertEquals(90, e2.getHealth());
    }

    @Test
    public void engine_awardsScoreAndGoldOnEnemyKill() {
        GameState state = new GameState(20, 500);
        GameEngine engine = new GameEngine(map, state,
                new WaveSpawner(List.of(new EnemyFactory.Basic())));
        // Tower at (1,2): after tick 1 the first enemy reaches (0,2) and becomes
        // the nearest target in tick 2, letting the tower land the killing blow.
        engine.addTower(new BasicTower(map.getCell(1, 2)));

        engine.startNextWave();
        engine.tick();                                           // enemy1 enters, moves to (0,1), takes 20 dmg → 80 HP
        engine.getActiveEnemies().forEach(e -> e.takeDamage(61)); // bring to 19 HP
        engine.tick();                                           // enemy1 moves to (0,2), nearest to (1,2), tower kills it

        assertTrue(state.getScore() >= 10);
        assertTrue(state.getGold()  >  500);
    }

    @Test
    public void engine_removesKilledEnemyFromActiveList() {
        GameState state = new GameState(20, 500);
        GameEngine engine = new GameEngine(map, state,
                new WaveSpawner(List.of(new EnemyFactory.Basic())));
        engine.addTower(new BasicTower(map.getCell(1, 0)));

        engine.startNextWave();
        engine.tick();                                            // enemy1 enters active list
        engine.getActiveEnemies().forEach(e -> e.takeDamage(999)); // kill all active enemies
        engine.tick();                                            // cleanupDefeatedEnemies removes them

        assertTrue("Dead enemies must not remain in the active list",
                engine.getActiveEnemies().stream().allMatch(IEnemy::isAlive));
    }
}
