package com.towerdefense.pattern.factory;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import java.util.List;

public abstract class EnemyFactory {
    public abstract Enemy create(List<Cell> path);
}
