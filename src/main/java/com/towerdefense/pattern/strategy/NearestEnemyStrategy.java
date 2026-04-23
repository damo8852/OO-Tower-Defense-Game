package com.towerdefense.pattern.strategy;


import java.util.Comparator;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public class NearestEnemyStrategy implements TargetingStrategy {

    @Override
    public IEnemy select(List<IEnemy> inRange, Cell towerPosition) {
        return inRange.stream()
                .filter(IEnemy::isAlive)
                .min(Comparator.comparingDouble(e -> towerPosition.distanceTo(e.getPosition())))
                .orElse(null);
    }
}
