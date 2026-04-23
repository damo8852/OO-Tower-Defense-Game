package com.towerdefense.pattern.factory;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public abstract class EnemyFactory {
    public abstract IEnemy create(List<Cell> path);
}
