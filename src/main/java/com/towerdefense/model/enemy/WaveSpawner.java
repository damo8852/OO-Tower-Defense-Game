package com.towerdefense.model.enemy;

import java.util.ArrayList;
import java.util.List;

import com.towerdefense.model.Tile;

public class WaveSpawner {

    private static final int DEFAULT_ENEMY_COUNT = 5;
    private static final int ENEMIES_PER_WAVE_INCREMENT = 2;

    private final List<EnemyFactory> factories;

    public WaveSpawner(List<EnemyFactory> factories) {
        this.factories = factories;
    }

    // generate enemy list for the wave, count scales with wave number
    public List<IEnemy> spawnWave(int waveNumber, List<Tile> path) {
        List<IEnemy> enemies = new ArrayList<>();
        int count = DEFAULT_ENEMY_COUNT + (waveNumber - 1) * ENEMIES_PER_WAVE_INCREMENT;
        for (int i = 0; i < count; i++) {
            enemies.add(pickFactory(waveNumber, i).create(path, waveNumber));
        }
        return enemies;
    }

    // pick factory by cycling through available types
    private EnemyFactory pickFactory(int waveNumber, int enemyIndex) {
        int availableTypes = Math.min(waveNumber, factories.size());
        return factories.get(enemyIndex % availableTypes);
    }
}
