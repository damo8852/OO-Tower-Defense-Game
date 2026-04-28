package com.towerdefense.model.enemy;

import com.towerdefense.model.Tile;
import java.util.List;

public abstract class EnemyFactory {

    // create enemy for this path and wave
    public abstract IEnemy create(List<Tile> path, int wave);

    public static class Basic extends EnemyFactory {
        // create a basic enemy
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new BasicEnemy(path, wave); }
    }

    public static class Fast extends EnemyFactory {
        // create a fast enemy
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new FastEnemy(path, wave); }
    }

    public static class Armored extends EnemyFactory {
        // create an armored enemy
        @Override
        public IEnemy create(List<Tile> path, int wave) { return new ArmoredEnemy(path, wave); }
    }
}
