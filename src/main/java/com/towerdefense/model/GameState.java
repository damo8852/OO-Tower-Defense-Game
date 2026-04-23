package com.towerdefense.model;

import com.towerdefense.pattern.observer.GameObserver;
import java.util.ArrayList;
import java.util.List;

public class GameState {

    private final int initialLives;
    private final int initialGold;

    private int lives;
    private int score;
    private int wave;
    private int gold;
    private final List<GameObserver> observers = new ArrayList<>();

    public GameState(int lives, int gold) {
        this.initialLives = lives;
        this.initialGold  = gold;
        this.lives = lives;
        this.score = 0;
        this.wave  = 0;
        this.gold  = gold;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getWave()  { return wave;  }
    public int getGold()  { return gold;  }

    public void loseLife() {
        lives--;
        observers.forEach(o -> o.onLivesChanged(lives));
    }

    public void addScore(int score) {
        this.score += score;
        observers.forEach(o -> o.onScoreChanged(this.score));
    }

    public void nextWave() {
        wave++;
        observers.forEach(o -> o.onWaveChanged(wave));
    }

    public void addGold(int gold) {
        this.gold += gold;
        observers.forEach(o -> o.onGoldChanged(this.gold));
    }

    public void spendGold(int gold) {
        this.gold -= gold;
        observers.forEach(o -> o.onGoldChanged(this.gold));
    }

    public boolean isGameOver() { return lives <= 0; }

    public void resetToInitial() {
        this.lives = initialLives;
        this.score = 0;
        this.wave  = 0;
        this.gold  = initialGold;
        observers.forEach(o -> o.onLivesChanged(lives));
        observers.forEach(o -> o.onScoreChanged(score));
        observers.forEach(o -> o.onWaveChanged(wave));
        observers.forEach(o -> o.onGoldChanged(gold));
    }

    public void restoreSnapshot(int lives, int score, int wave, int gold) {
        this.lives = lives;
        this.score = score;
        this.wave  = wave;
        this.gold  = gold;
        observers.forEach(o -> o.onLivesChanged(lives));
        observers.forEach(o -> o.onScoreChanged(score));
        observers.forEach(o -> o.onWaveChanged(wave));
        observers.forEach(o -> o.onGoldChanged(gold));
    }
}
