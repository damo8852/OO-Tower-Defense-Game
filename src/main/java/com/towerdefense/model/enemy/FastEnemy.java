package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;
import java.util.List;

public class FastEnemy extends Enemy {

    private static final int DEFAULT_HEALTH = 60;
    private static final int MOVE_SPEED     = 2;
    private static final int KILL_REWARD    = 15;

    public FastEnemy(List<Cell> path) {
        super(path, DEFAULT_HEALTH, MOVE_SPEED, KILL_REWARD);
    }

    @Override
    public String getTypeName() { return "FAST"; }
}
