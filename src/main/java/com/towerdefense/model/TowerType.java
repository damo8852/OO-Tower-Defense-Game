package com.towerdefense.model;

public enum TowerType {
    BASIC(100), SNIPER(200), SPLASH(150);

    private final int cost;

    TowerType(int cost) { this.cost = cost; }

    // return placement cost
    public int getCost() { return cost; }
}
