package com.towerdefense.model.enemy;

import com.towerdefense.model.Tile;
import java.util.List;

public abstract class EnemyFactory {

    public abstract IEnemy create(List<Tile> path, int wave);

    public static class Basic extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new BasicEnemy(path, wave); }
    }

    public static class Fast extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new FastEnemy(path, wave); }
    }

    public static class Armored extends EnemyFactory {
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new ArmoredEnemy(path, wave); }
    }
}
