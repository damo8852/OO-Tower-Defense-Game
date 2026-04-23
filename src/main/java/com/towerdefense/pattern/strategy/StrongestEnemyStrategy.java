package com.towerdefense.pattern.strategy;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import java.util.Comparator;
import java.util.List;

public class StrongestEnemyStrategy implements TargetingStrategy {

    @Override
    public Enemy select(List<Enemy> inRange, Cell towerPosition) {
        return inRange.stream()
                .filter(Enemy::isAlive)
                .max(Comparator.comparingInt(Enemy::getHealth))
                .orElse(null);
    }
}
