package com.towerdefense.ui;

import com.towerdefense.eventbus.EventBus;
import com.towerdefense.eventbus.EventType;
import com.towerdefense.eventbus.ITowerDefenseObserver;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HudView extends VBox implements ITowerDefenseObserver {

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
        for (Label l : new Label[]{livesLabel, scoreLabel, waveLabel, goldLabel})
            l.setStyle("-fx-text-fill: white;");
        getChildren().addAll(livesLabel, scoreLabel, waveLabel, goldLabel);
    }

    public void initialize() {
        EventBus.getInstance().attach(this);
    }

    // receive event and update the matching hud label
    @Override
    public void update(EventType eventType, Object eventObject) {
        switch (eventType) {
            case LIVES_CHANGED -> livesLabel.setText("Lives: " + eventObject);
            case SCORE_CHANGED -> scoreLabel.setText("Score: " + eventObject);
            case WAVE_CHANGED  -> waveLabel.setText("Wave:  "  + eventObject);
            case GOLD_CHANGED  -> goldLabel.setText("Gold:  "  + eventObject);
        }
    }
}
