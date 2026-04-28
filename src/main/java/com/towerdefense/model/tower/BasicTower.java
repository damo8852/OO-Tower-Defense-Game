package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerShot;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.NearestEnemyStrategy;

public class BasicTower extends Tower {

    private static final int ATTACK_RANGE = 3;
    private static final int ATTACK_DAMAGE = 20;

    public BasicTower(Tile position) {
        super(position, new NearestEnemyStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.BASIC.getCost());
    }

    // attack nearest enemy in range
    @Override
    public void attack(List<IEnemy> enemies) {
        attackSingleTarget(enemies);
    }

    // deferred attack on nearest enemy
    @Override
    public Runnable planAttack(List<IEnemy> enemies) {
        return planSingleTargetAttack(enemies);
    }

    // return shot for nearest enemy
    @Override
    public List<TowerShot> collectShots(List<IEnemy> enemies) {
        return singleShot(enemies);
    }

    // return tower type
    @Override
    public TowerType getTowerType() { return TowerType.BASIC; }
}
