package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.FirstInPathStrategy;

public class SplashTower extends Tower {

    private static final int ATTACK_RANGE  = 2;
    private static final int ATTACK_DAMAGE = 10;

    public SplashTower(Tile position) {
        super(position, new FirstInPathStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.SPLASH.getCost());
    }

    @Override
    public void attack(List<IEnemy> enemies) {
        enemiesInRange(enemies).forEach(e -> e.takeDamage(getDamage()));
    }

    @Override
    public TowerType getTowerType() { return TowerType.SPLASH; }
}
