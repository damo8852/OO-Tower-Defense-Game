package com.towerdefense.model;

import java.util.List;

import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.TargetingStrategy;

public abstract class Tower {

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

    public abstract void attack(List<IEnemy> enemies);
    public abstract TowerType getTowerType();

    protected void attackSingleTarget(List<IEnemy> enemies) {
        IEnemy target = selectTarget(enemies);
        if (target != null) {
            target.takeDamage(damage);
        }
    }

    private IEnemy selectTarget(List<IEnemy> enemies) {
        return targetingStrategy.select(enemiesInRange(enemies), position);
    }

    protected List<IEnemy> enemiesInRange(List<IEnemy> enemies) {
        return enemies.stream()
                .filter(e -> e.isAlive() && !e.hasReachedEnd() && position.distanceTo(e.getPosition()) <= range)
                .toList();
    }

    public void upgrade(int damageIncrease, int rangeIncrease) {
        damage += damageIncrease;
        range += rangeIncrease;
    }

    public void downgrade(int damageDecrease, int rangeDecrease) {
        damage -= damageDecrease;
        range -= rangeDecrease;
    }

    public Tile getPosition() { return position; }
    public int getRange()     { return range; }
    public int getDamage()    { return damage; }
    public int getCost()      { return cost; }
    public int getSellValue() { return cost / SELL_VALUE_DIVISOR; }
}
