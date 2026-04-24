package com.towerdefense.model.tower;

import com.towerdefense.model.Tile;
import com.towerdefense.model.enemy.IEnemy;
import com.towerdefense.strategy.TargetingStrategy;

import java.util.List;

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

    protected void attackSingleTarget(List<IEnemy> enemies) {
        IEnemy target = targetingStrategy.select(enemiesInRange(enemies), position);
        if (target != null) {
            target.takeDamage(damage);
        }
    }

    protected List<IEnemy> enemiesInRange(List<IEnemy> enemies) {
        return enemies.stream()
                .filter(e -> e.isAlive() && !e.hasReachedEnd() && position.distanceTo(e.getPosition()) <= range)
                .toList();
    }

    @Override
    public void upgrade(int damageIncrease, int rangeIncrease) {
        damage += damageIncrease;
        range += rangeIncrease;
    }

    @Override
    public void downgrade(int damageDecrease, int rangeDecrease) {
        damage -= damageDecrease;
        range -= rangeDecrease;
    }

    @Override public Tile getPosition() { return position; }
    @Override public int getRange()     { return range; }
    @Override public int getDamage()    { return damage; }
    @Override public int getCost()      { return cost; }
    @Override public int getSellValue() { return cost / SELL_VALUE_DIVISOR; }
}
