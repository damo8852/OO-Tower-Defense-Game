package com.towerdefense.model;

import com.towerdefense.model.TowerType;

public record TowerShot(Tile origin, Tile target, TowerType towerType) {}
