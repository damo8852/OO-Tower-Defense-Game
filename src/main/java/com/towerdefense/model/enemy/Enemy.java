package com.towerdefense.model.enemy;

import java.util.List;

import com.towerdefense.model.Tile;

public abstract class Enemy implements IEnemy {

    private int health;
    private final int maxHealth;
    private final int reward;
    private final int speed;
    private final int wave;
    private int pathIndex;
    private final List<Tile> path;

    protected Enemy(List<Tile> path, int health, int speed, int reward, int wave) {
        this.path = path;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.reward = reward;
        this.wave = wave;
        this.pathIndex = 0;
    }

    // move enemy forward by speed, capped at end
    @Override
    public void move() {
        pathIndex = Math.min(pathIndex + speed, path.size() - 1);
    }

    // take damage, floor at zero
    @Override
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    // true if still has health
    @Override
    public boolean isAlive() {
        return health > 0;
    }

    // true if at last path tile
    @Override
    public boolean hasReachedEnd() {
        return pathIndex >= path.size() - 1;
    }

    // return current tile
    @Override
    public Tile getPosition() {
        return path.get(pathIndex);
    }

    // return path position index
    @Override
    public int getPathIndex() { return pathIndex; }

    // return current health
    @Override
    public int getHealth() { return health; }

    // return starting health
    @Override
    public int getMaxHealth() { return maxHealth; }

    // return gold reward on kill
    @Override
    public int getReward() { return reward; }

    // return wave spawned in
    @Override
    public int getWave() { return wave; }
}
