package com.towerdefense.pattern.factory;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.model.enemy.FastEnemy;

public class FastEnemyFactory extends EnemyFactory {

    @Override
    public IEnemy create(List<Cell> path) {
        return new FastEnemy(path);
    }
}
