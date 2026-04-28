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

    // place tower and spend gold
    @Override
    public void execute() {
        engine.addTower(tower);
        gameState.spendGold(tower.getCost());
    }

    // remove tower and refund cost
    @Override
    public void undo() {
        engine.removeTower(tower);
        gameState.addGold(tower.getCost());
    }

    // return command record for save/load
    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_PLACE, tower.getTowerType().name(),
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
