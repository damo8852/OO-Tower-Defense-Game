package com.towerdefense.model;

public interface Projectile {
    void update();
    boolean hasHit();
    Enemy getTarget();
}
