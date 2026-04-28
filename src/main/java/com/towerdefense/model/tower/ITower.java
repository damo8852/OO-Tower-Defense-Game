package com.towerdefense.model.tower;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;

import java.util.List;

public interface ITower {
    // attack enemies
    void attack(List<IEnemy> enemies);
    // return tower type
    TowerType getTowerType();
    // return position
    Tile getPosition();
    // return attack range
    int getRange();
    // return attack damage
    int getDamage();
    // return placement cost
    int getCost();
    // return sell value
    int getSellValue();
    // boost damage and range
    void upgrade(int damageIncrease, int rangeIncrease);
    // reduce damage and range
    void downgrade(int damageDecrease, int rangeDecrease);
}
