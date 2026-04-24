package com.towerdefense.model.enemy;

import com.towerdefense.model.Tile;
import java.util.List;

public class ArmoredEnemy extends Enemy {

    private static final int DEFAULT_HEALTH = 200;
    private static final int MOVE_SPEED     = 1;
    private static final int KILL_REWARD    = 25;
    private static final int ARMOR_DIVISOR  = 2;

    public ArmoredEnemy(List<Tile> path) {
        super(path, DEFAULT_HEALTH, MOVE_SPEED, KILL_REWARD);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage / ARMOR_DIVISOR);
    }

    @Override
    public String getTypeName() { return "ARMORED"; }
}
