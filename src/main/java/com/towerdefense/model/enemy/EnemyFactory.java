package com.towerdefense.model.enemy;

import com.towerdefense.model.Tile;
import java.util.List;

public abstract class EnemyFactory {

    public abstract IEnemy create(List<Tile> path);

    public static class Basic extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path) { return new BasicEnemy(path); }
    }

    public static class Fast extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path) { return new FastEnemy(path); }
    }

    public static class Armored extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path) { return new ArmoredEnemy(path); }
    }
}
