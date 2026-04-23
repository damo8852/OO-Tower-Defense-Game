package com.towerdefense.pattern.factory;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import java.util.ArrayList;
import java.util.List;

public class WaveSpawner {

    private static final int BASE_ENEMY_COUNT          = 5;
    private static final int ENEMIES_PER_WAVE_INCREMENT = 2;

    private final List<EnemyFactory> factories;

    public WaveSpawner(List<EnemyFactory> factories) {
        this.factories = factories;
    }

    public List<Enemy> spawnWave(int waveNumber, List<Cell> path) {
        List<Enemy> enemies = new ArrayList<>();
        int count = BASE_ENEMY_COUNT + (waveNumber - 1) * ENEMIES_PER_WAVE_INCREMENT;

        for (int i = 0; i < count; i++) {
            enemies.add(pickFactory(waveNumber, i).create(path));
        }
        return enemies;
    }

    // Wave 1 → only basic; wave 2 → basic+fast; wave 3+ → all types
    private EnemyFactory pickFactory(int waveNumber, int enemyIndex) {
        int availableTypes = Math.min(waveNumber, factories.size());
        return factories.get(enemyIndex % availableTypes);
    }
}
