package com.towerdefense.model.enemy;

import com.towerdefense.model.Tile;

public interface IEnemy {
    // move enemy one step
    void move();
    // apply damage
    void takeDamage(int damage);
    // true if alive
    boolean isAlive();
    // true if reached end
    boolean hasReachedEnd();
    // return current tile
    Tile getPosition();
    // return path index
    int getPathIndex();
    // return current health
    int getHealth();
    // return max health
    int getMaxHealth();
    // return kill reward
    int getReward();
    // return wave number
    int getWave();
    // return type name string
    String getTypeName();
}
