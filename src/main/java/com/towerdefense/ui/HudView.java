package com.towerdefense.ui;

import com.towerdefense.pattern.observer.GameObserver;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HudView extends VBox implements GameObserver {

    private static final int SPACING = 4;

    private final Label livesLabel;
    private final Label scoreLabel;
    private final Label waveLabel;
    private final Label goldLabel;

    public HudView(int lives, int score, int wave, int gold) {
        super(SPACING);
        setPadding(new Insets(0, 0, 4, 0));
        livesLabel = new Label("Lives: " + lives);
        scoreLabel = new Label("Score: " + score);
        waveLabel  = new Label("Wave:  " + wave);
        goldLabel  = new Label("Gold:  " + gold);
        getChildren().addAll(livesLabel, scoreLabel, waveLabel, goldLabel);
    }

    @Override public void onLivesChanged(int v) { livesLabel.setText("Lives: " + v); }
    @Override public void onScoreChanged(int v) { scoreLabel.setText("Score: " + v); }
    @Override public void onWaveChanged(int v)  { waveLabel.setText("Wave:  " + v); }
    @Override public void onGoldChanged(int v)  { goldLabel.setText("Gold:  " + v); }
}
