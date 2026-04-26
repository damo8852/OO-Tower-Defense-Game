package com.towerdefense.model.enemy;

import java.util.List;

import com.towerdefense.model.Tile;

public class ArmoredEnemy extends Enemy {

    private static final int BASE_HEALTH = 200;
    private static final int HEALTH_PER_WAVE = 40;
    private static final int MOVE_SPEED = 1;
    private static final int KILL_REWARD = 25;
    private static final int ARMOR_DIVISOR = 2;

    public ArmoredEnemy(List<Tile> path, int wave) {
        super(path, BASE_HEALTH + (wave - 1) * HEALTH_PER_WAVE, MOVE_SPEED, KILL_REWARD, wave);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage / ARMOR_DIVISOR);
    }

    @Override
    public String getTypeName() { return "ARMORED"; }
}
