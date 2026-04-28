package com.towerdefense.model.enemy;

import java.util.List;

import com.towerdefense.model.Tile;

public class FastEnemy extends Enemy {

    private static final int BASE_HEALTH = 60;
    private static final int HEALTH_PER_WAVE = 12;
    private static final int MOVE_SPEED = 2;
    private static final int KILL_REWARD = 15;

    public FastEnemy(List<Tile> path, int wave) {
        super(path, BASE_HEALTH + (wave - 1) * HEALTH_PER_WAVE, MOVE_SPEED, KILL_REWARD, wave);
    }

    // return type name
    @Override
    public String getTypeName() { return "FAST"; }
}
