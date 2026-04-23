package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;
import java.util.List;

public class ArmoredEnemy extends AbstractEnemy {

    private static final int INITIAL_HEALTH = 200;
    private static final int MOVE_SPEED     = 1;
    private static final int KILL_REWARD    = 25;
    private static final int ARMOR_DIVISOR  = 2;

    public ArmoredEnemy(List<Cell> path) {
        super(path, INITIAL_HEALTH, MOVE_SPEED, KILL_REWARD);
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount / ARMOR_DIVISOR);
    }

    @Override
    public String getTypeName() { return "ARMORED"; }
}
