package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;
import java.util.List;

public class FastEnemy extends AbstractEnemy {

    private static final int INITIAL_HEALTH = 60;
    private static final int MOVE_SPEED     = 2;
    private static final int KILL_REWARD    = 15;

    public FastEnemy(List<Cell> path) {
        super(path, INITIAL_HEALTH, MOVE_SPEED, KILL_REWARD);
    }

    @Override
    public String getTypeName() { return "FAST"; }
}
