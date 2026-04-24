package com.towerdefense.model.tower;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerType;

public abstract class TowerFactory {

    public abstract Tower create(Tile position);

    public static TowerFactory forType(TowerType type) {
        return switch (type) {
            case BASIC  -> new Basic();
            case SNIPER -> new Sniper();
            case SPLASH -> new Splash();
        };
    }

    public static class Basic extends TowerFactory {
        @Override public Tower create(Tile position) { return new BasicTower(position); }
    }

    public static class Sniper extends TowerFactory {
        @Override public Tower create(Tile position) { return new SniperTower(position); }
    }

    public static class Splash extends TowerFactory {
        @Override public Tower create(Tile position) { return new SplashTower(position); }
    }
}
