package com.towerdefense.pattern.observer;

public interface GameObserver {
    void onLivesChanged(int newLives);
    void onScoreChanged(int newScore);
    void onWaveChanged(int newWave);
    void onGoldChanged(int newGold);
}
