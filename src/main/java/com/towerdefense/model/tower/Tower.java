package com.towerdefense.model.tower;

import java.util.List;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerShot;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.TargetingStrategy;

public abstract class Tower implements ITower {

    private static final int SELL_VALUE_DIVISOR = 2;

    private final Tile position;
    private final TargetingStrategy targetingStrategy;
    private int range;
    private int damage;
    private final int cost;

    protected Tower(Tile position, TargetingStrategy targetingStrategy, int range, int damage, int cost) {
        this.position = position;
        this.targetingStrategy = targetingStrategy;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
    }

    // pick and damage one target using targeting strategy
    protected void attackSingleTarget(List<IEnemy> enemies) {
        IEnemy target = targetingStrategy.select(enemiesInRange(enemies), position);
        if (target != null) {
            target.takeDamage(damage);
        }
    }

    // return deferred attack on one target, null if no target in range
    protected Runnable planSingleTargetAttack(List<IEnemy> enemies) {
        IEnemy target = targetingStrategy.select(enemiesInRange(enemies), position);
        return target == null ? null : () -> target.takeDamage(damage);
    }

    // return deferred attack for this tick
    public abstract Runnable planAttack(List<IEnemy> enemies);

    // return shot records for the renderer
    public abstract List<TowerShot> collectShots(List<IEnemy> enemies);

    // return one shot for the selected target
    protected List<TowerShot> singleShot(List<IEnemy> enemies) {
        IEnemy target = targetingStrategy.select(enemiesInRange(enemies), position);
        return target == null ? List.of()
                : List.of(new TowerShot(position, target.getPosition(), getTowerType()));
    }

    // filter enemies to those in range
    protected List<IEnemy> enemiesInRange(List<IEnemy> enemies) {
        return enemies.stream()
                .filter(e -> e.isAlive() && !e.hasReachedEnd() && position.distanceTo(e.getPosition()) <= range)
                .toList();
    }

    // boost damage and range
    @Override
    public void upgrade(int damageIncrease, int rangeIncrease) {
        damage += damageIncrease;
        range += rangeIncrease;
    }

    // reduce damage and range - undo upgrade
    @Override
    public void downgrade(int damageDecrease, int rangeDecrease) {
        damage -= damageDecrease;
        range -= rangeDecrease;
    }

    // return position tile
    @Override public Tile getPosition() { return position; }
    // return attack range
    @Override public int getRange() { return range; }
    // return attack damage
    @Override public int getDamage() { return damage; }
    // return placement cost
    @Override public int getCost() { return cost; }
    // return sell value, half of cost
    @Override public int getSellValue() { return cost / SELL_VALUE_DIVISOR; }
}
