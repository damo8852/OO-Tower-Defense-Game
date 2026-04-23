package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;
import java.util.List;

public class BasicEnemy extends AbstractEnemy {

    private static final int INITIAL_HEALTH = 100;
    private static final int MOVE_SPEED     = 1;
    private static final int KILL_REWARD    = 10;

    public BasicEnemy(List<Cell> path) {
        super(path, INITIAL_HEALTH, MOVE_SPEED, KILL_REWARD);
    }

    @Override
    public String getTypeName() { return "BASIC"; }
}
