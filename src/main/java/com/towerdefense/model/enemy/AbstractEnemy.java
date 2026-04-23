package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import java.util.List;

public abstract class AbstractEnemy implements Enemy {

    private int health;
    private final int maxHealth;
    private final int reward;
    private final int speed;
    private int pathIndex;
    private final List<Cell> path;

    protected AbstractEnemy(List<Cell> path, int health, int speed, int reward) {
        this.path = path;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.reward = reward;
        this.pathIndex = 0;
    }

    @Override
    public void move() {
        pathIndex = Math.min(pathIndex + speed, path.size() - 1);
    }

    @Override
    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
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
    public Cell getPosition() {
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
}
