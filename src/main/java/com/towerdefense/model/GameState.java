package com.towerdefense.model;

public class GameState {

    private int lives;
    private int score;
    private int wave;
    private int gold;

    public GameState(int lives, int gold) {
        this.lives = lives;
        this.score = 0;
        this.wave = 0;
        this.gold = gold;
    }

    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getWave() { return wave; }
    public int getGold() { return gold; }

    public void loseLife() { lives--; }
    public void addScore(int points) { score += points; }
    public void nextWave() { wave++; }
    public void addGold(int amount) { gold += amount; }
    public void spendGold(int amount) { gold -= amount; }

    public boolean isGameOver() { return lives <= 0; }
}
