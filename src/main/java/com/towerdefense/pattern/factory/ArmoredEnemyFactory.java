package com.towerdefense.pattern.factory;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.ArmoredEnemy;
import com.towerdefense.model.enemy.IEnemy;

public class ArmoredEnemyFactory extends EnemyFactory {

    @Override
    public IEnemy create(List<Cell> path) {
        return new ArmoredEnemy(path);
    }
}
