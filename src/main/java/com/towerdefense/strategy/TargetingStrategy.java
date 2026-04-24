package com.towerdefense.strategy;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public interface TargetingStrategy {
    IEnemy select(List<IEnemy> inRange, Tile towerPosition);
}
