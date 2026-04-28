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
        this.initialGold = gold;
        this.lives = lives;
        this.score = 0;
        this.wave = 0;
        this.gold = gold;
    }

    // return current lives
    public int getLives() { return lives; }
    // return current score
    public int getScore() { return score; }
    // return current wave
    public int getWave()  { return wave;  }
    // return current gold
    public int getGold()  { return gold;  }

    // lose a life and fire lives changed event
    public void loseLife() {
        lives--;
        EventBus.getInstance().postEvent(EventType.LIVES_CHANGED, lives);
    }

    // add to score and fire score changed event
    public void addScore(int score) {
        this.score += score;
        EventBus.getInstance().postEvent(EventType.SCORE_CHANGED, this.score);
    }

    // increment wave and fire wave changed event
    public void nextWave() {
        wave++;
        EventBus.getInstance().postEvent(EventType.WAVE_CHANGED, wave);
    }

    // add gold and fire gold changed event
    public void addGold(int gold) {
        this.gold += gold;
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED, this.gold);
    }

    // spend gold and fire gold changed event
    public void spendGold(int gold) {
        this.gold -= gold;
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED, this.gold);
    }

    // true when no lives remain
    public boolean isGameOver() { return lives <= 0; }

    // reset all state to starting values
    public void resetToInitial() {
        this.lives = initialLives;
        this.score = 0;
        this.wave  = 0;
        this.gold  = initialGold;
        publishAll();
    }

    // restore saved state snapshot
    public void restoreSnapshot(int lives, int score, int wave, int gold) {
        this.lives = lives;
        this.score = score;
        this.wave  = wave;
        this.gold  = gold;
        publishAll();
    }

    // fire all events to sync observers
    private void publishAll() {
        EventBus.getInstance().postEvent(EventType.LIVES_CHANGED, lives);
        EventBus.getInstance().postEvent(EventType.SCORE_CHANGED, score);
        EventBus.getInstance().postEvent(EventType.WAVE_CHANGED,  wave);
        EventBus.getInstance().postEvent(EventType.GOLD_CHANGED,  gold);
    }
}
