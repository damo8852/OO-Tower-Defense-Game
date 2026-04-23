package com.towerdefense.model.enemy;

import com.towerdefense.model.Cell;

public interface IEnemy {
    void move();
    void takeDamage(int damage);
    boolean isAlive();
    boolean hasReachedEnd();
    Cell getPosition();
    int getPathIndex();
    int getHealth();
    int getMaxHealth();
    int getReward();
    String getTypeName();
}
