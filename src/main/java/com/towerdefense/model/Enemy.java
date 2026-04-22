package com.towerdefense.model;

public interface Enemy {
    void move();
    void takeDamage(int amount);
    boolean isAlive();
    Cell getPosition();
    int getPathIndex();
    int getHealth();
    int getMaxHealth();
    int getReward();
}
