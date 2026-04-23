package com.towerdefense.pattern.strategy;


import java.util.Comparator;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public class StrongestEnemyStrategy implements TargetingStrategy {

    @Override
    public IEnemy select(List<IEnemy> inRange, Cell towerPosition) {
        return inRange.stream()
                .filter(IEnemy::isAlive)
                .max(Comparator.comparingInt(IEnemy::getHealth))
                .orElse(null);
    }
}
