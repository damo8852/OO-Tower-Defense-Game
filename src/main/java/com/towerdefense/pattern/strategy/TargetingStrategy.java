package com.towerdefense.pattern.strategy;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.enemy.IEnemy;
import java.util.List;

public interface TargetingStrategy {
    IEnemy select(List<IEnemy> inRange, Cell towerPosition);
}
