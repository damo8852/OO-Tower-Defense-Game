package com.towerdefense.command;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.Tower;

public class SellTowerCommand implements GameCommand {

    private final GameEngine engine;
    private final GameState gameState;
    private final Tower tower;

    public SellTowerCommand(GameEngine engine, GameState gameState, Tower tower) {
        this.engine = engine;
        this.gameState = gameState;
        this.tower = tower;
    }

    // remove tower and add sell value to gold
    @Override
    public void execute() {
        engine.removeTower(tower);
        gameState.addGold(tower.getSellValue());
    }

    // re-place tower and take back sell value
    @Override
    public void undo() {
        engine.addTower(tower);
        gameState.spendGold(tower.getSellValue());
    }

    // return command record for save/load
    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_SELL, null,
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
