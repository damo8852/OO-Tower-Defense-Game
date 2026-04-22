package com.towerdefense.model;

import com.towerdefense.pattern.strategy.TargetingStrategy;
import java.util.List;

public abstract class Tower {

    protected final Cell position;
    protected final TargetingStrategy targetingStrategy;
    protected int range;
    protected int damage;
    protected final int cost;

    protected Tower(Cell position, TargetingStrategy targetingStrategy, int range, int damage, int cost) {
        this.position = position;
        this.targetingStrategy = targetingStrategy;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
    }

    public abstract void attack(List<Enemy> enemies);

    protected List<Enemy> enemiesInRange(List<Enemy> enemies) {
        return enemies.stream()
                .filter(e -> e.isAlive() && distanceTo(e) <= range)
                .toList();
    }

    private double distanceTo(Enemy enemy) {
        int dr = enemy.getPosition().getRow() - position.getRow();
        int dc = enemy.getPosition().getCol() - position.getCol();
        return Math.sqrt(dr * dr + dc * dc);
    }

    public Cell getPosition() { return position; }
    public int getRange() { return range; }
    public int getDamage() { return damage; }
    public int getCost() { return cost; }
    public int getSellValue() { return cost / 2; }
}
