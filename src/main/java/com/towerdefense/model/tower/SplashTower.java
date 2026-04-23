package com.towerdefense.model.tower;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Enemy;
import com.towerdefense.model.Tower;
import com.towerdefense.model.TowerType;
import com.towerdefense.pattern.strategy.FirstInPathStrategy;
import java.util.List;

public class SplashTower extends Tower {

    private static final int ATTACK_RANGE  = 2;
    private static final int ATTACK_DAMAGE = 10;

    public SplashTower(Cell position) {
        super(position, new FirstInPathStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.SPLASH.getCost());
    }

    @Override
    public void attack(List<Enemy> enemies) {
        enemiesInRange(enemies).forEach(e -> e.takeDamage(getDamage()));
    }

    @Override
    public TowerType getTowerType() { return TowerType.SPLASH; }
}
