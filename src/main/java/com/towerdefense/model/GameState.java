package com.towerdefense.model;

import com.towerdefense.eventbus.EventBus;
import com.towerdefense.eventbus.EventType;

public class GameState {

    private final int initialLives;
    private final int initialGold;

    private int lives;
    private int score;
    private int wave;
    private int gold;

    public GameState(int lives, int gold) {
        this.initialLives = lives;
        this.initialGold  = gold;
        this.lives = lives;
        this.score = 0;
        this.wave  = 0;
        this.gold  = gold;
    }

    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getWave()  { return wave;  }
    public int getGold()  { return gold;  }

    public void loseLife() {
        lives--;
        EventBus.getInstance().postEvent(EventType.LIVES_CHANGED, lives);
    }

    public void addScore(int score) {
        this.score += score;
        EventBus.getInstance().postEvent(EventType.SCORE_CHANGED, this.score);
    }

    public void nextWave() {
        wave++;
        EventBus.getInstance().postEvent(EventType.WAVE_CHANGED, wave);
    }

    public void addGold(int gold) {
        this.gold += gold;
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED, this.gold);
    }

    public void spendGold(int gold) {
        this.gold -= gold;
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED, this.gold);
    }

    public boolean isGameOver() { return lives <= 0; }

    public void resetToInitial() {
        this.lives = initialLives;
        this.score = 0;
        this.wave  = 0;
        this.gold  = initialGold;
        publishAll();
    }

    public void restoreSnapshot(int lives, int score, int wave, int gold) {
        this.lives = lives;
        this.score = score;
        this.wave  = wave;
        this.gold  = gold;
        publishAll();
    }

    private void publishAll() {
        EventBus.getInstance().postEvent(EventType.LIVES_CHANGED, lives);
        EventBus.getInstance().postEvent(EventType.SCORE_CHANGED, score);
        EventBus.getInstance().postEvent(EventType.WAVE_CHANGED,  wave);
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED,  gold);
    }
}
