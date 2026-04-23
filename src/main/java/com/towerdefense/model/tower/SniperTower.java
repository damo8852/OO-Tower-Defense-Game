package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Tower;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.pattern.strategy.StrongestEnemyStrategy;

public class SniperTower extends Tower {

    private static final int ATTACK_RANGE  = 6;
    private static final int ATTACK_DAMAGE = 50;

    public SniperTower(Cell position) {
        super(position, new StrongestEnemyStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.SNIPER.getCost());
    }

    @Override
    public void attack(List<IEnemy> enemies) {
        attackSingleTarget(enemies);
    }

    @Override
    public TowerType getTowerType() { return TowerType.SNIPER; }
}
