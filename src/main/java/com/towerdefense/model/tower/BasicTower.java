package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Cell;
import com.towerdefense.model.Tower;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.pattern.strategy.NearestEnemyStrategy;

public class BasicTower extends Tower {

    private static final int ATTACK_RANGE  = 3;
    private static final int ATTACK_DAMAGE = 20;

    public BasicTower(Cell position) {
        super(position, new NearestEnemyStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.BASIC.getCost());
    }

    @Override
    public void attack(List<IEnemy> enemies) {
        attackSingleTarget(enemies);
    }

    @Override
    public TowerType getTowerType() { return TowerType.BASIC; }
}
