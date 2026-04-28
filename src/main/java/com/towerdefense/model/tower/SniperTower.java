package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerShot;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.StrongestEnemyStrategy;

public class SniperTower extends Tower {

    private static final int ATTACK_RANGE = 6;
    private static final int ATTACK_DAMAGE = 50;

    public SniperTower(Tile position) {
        super(position, new StrongestEnemyStrategy(), ATTACK_RANGE, ATTACK_DAMAGE, TowerType.SNIPER.getCost());
    }

    // attack strongest enemy in range
    @Override
    public void attack(List<IEnemy> enemies) {
        attackSingleTarget(enemies);
    }

    // deferred attack on strongest enemy
    @Override
    public Runnable planAttack(List<IEnemy> enemies) {
        return planSingleTargetAttack(enemies);
    }

    // return shot for strongest enemy
    @Override
    public List<TowerShot> collectShots(List<IEnemy> enemies) {
        return singleShot(enemies);
    }

    // return tower type
    @Override
    public TowerType getTowerType() { return TowerType.SNIPER; }
}
