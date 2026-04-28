package com.towerdefense.strategy;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;

public interface TargetingStrategy {
    // pick best target from in-range enemies
    IEnemy select(List<IEnemy> inRange, Tile towerPosition);
}
