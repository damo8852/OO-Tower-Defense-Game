package com.towerdefense.pattern.factory;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import com.towerdefense.model.enemy.ArmoredEnemy;
import java.util.List;

public class ArmoredEnemyFactory extends EnemyFactory {

    @Override
    public Enemy create(List<Cell> path) {
        return new ArmoredEnemy(path);
    }
}
