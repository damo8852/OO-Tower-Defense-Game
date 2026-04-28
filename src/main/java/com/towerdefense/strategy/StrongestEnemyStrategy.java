package com.towerdefense.strategy;


import java.util.Comparator;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public class StrongestEnemyStrategy implements TargetingStrategy {

    // pick alive enemy with most health
    @Override
    public IEnemy select(List<IEnemy> inRange, Tile towerPosition) {
        return inRange.stream()
                .filter(IEnemy::isAlive)
                .max(Comparator.comparingInt(IEnemy::getHealth))
                .orElse(null);
    }
}
