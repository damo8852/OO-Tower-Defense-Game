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

    @Override
    public void move() {
        pathIndex = Math.min(pathIndex + speed, path.size() - 1);
    }

    @Override
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public boolean hasReachedEnd() {
        return pathIndex >= path.size() - 1;
    }

    @Override
    public Tile getPosition() {
        return path.get(pathIndex);
    }

    @Override
    public int getPathIndex() { return pathIndex; }

    @Override
    public int getHealth() { return health; }

    @Override
    public int getMaxHealth() { return maxHealth; }

    @Override
    public int getReward() { return reward; }

    @Override
    public int getWave() { return wave; }
}
