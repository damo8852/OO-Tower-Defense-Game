package com.towerdefense.command;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.Tower;

public class PlaceTowerCommand implements GameCommand {

    private final GameEngine engine;
    private final GameState gameState;
    private final Tower tower;

    public PlaceTowerCommand(GameEngine engine, GameState gameState, Tower tower) {
        this.engine = engine;
        this.gameState = gameState;
        this.tower = tower;
    }

    @Override
    public void execute() {
        engine.addTower(tower);
        gameState.spendGold(tower.getCost());
    }

    @Override
    public void undo() {
        engine.removeTower(tower);
        gameState.addGold(tower.getCost());
    }

    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_PLACE, tower.getTowerType().name(),
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
