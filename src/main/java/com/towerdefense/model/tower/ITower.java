package com.towerdefense.model.tower;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;

import java.util.List;

public interface ITower {
    void attack(List<IEnemy> enemies);
    TowerType getTowerType();
    Tile getPosition();
    int getRange();
    int getDamage();
    int getCost();
    int getSellValue();
    void upgrade(int damageIncrease, int rangeIncrease);
    void downgrade(int damageDecrease, int rangeDecrease);
}
