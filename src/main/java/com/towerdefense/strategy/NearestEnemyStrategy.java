package com.towerdefense.strategy;


import java.util.Comparator;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public class NearestEnemyStrategy implements TargetingStrategy {

    // pick alive enemy closest to the tower
    @Override
    public IEnemy select(List<IEnemy> inRange, Tile towerPosition) {
        return inRange.stream()
                .filter(IEnemy::isAlive)
                .min(Comparator.comparingDouble(e -> towerPosition.distanceTo(e.getPosition())))
                .orElse(null);
    }
}
