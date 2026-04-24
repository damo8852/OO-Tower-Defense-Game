package com.towerdefense.strategy;


import java.util.Comparator;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public class FirstInPathStrategy implements TargetingStrategy {

    @Override
    public IEnemy select(List<IEnemy> inRange, Tile towerPosition) {
        return inRange.stream()
                .filter(IEnemy::isAlive)
                .max(Comparator.comparingInt(IEnemy::getPathIndex))
                .orElse(null);
    }
}
