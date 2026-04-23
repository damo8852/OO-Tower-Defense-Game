package com.towerdefense.pattern.strategy;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import java.util.List;

public interface TargetingStrategy {
    Enemy select(List<Enemy> inRange, Cell towerPosition);
}
