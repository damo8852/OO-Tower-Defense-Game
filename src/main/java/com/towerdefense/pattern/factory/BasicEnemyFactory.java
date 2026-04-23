package com.towerdefense.pattern.factory;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.BasicEnemy;
import com.towerdefense.model.enemy.IEnemy;

public class BasicEnemyFactory extends EnemyFactory {

    @Override
    public IEnemy create(List<Cell> path) {
        return new BasicEnemy(path);
    }
}
