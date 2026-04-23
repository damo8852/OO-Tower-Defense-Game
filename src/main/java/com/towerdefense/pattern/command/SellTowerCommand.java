package com.towerdefense.pattern.command;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.GameState;
import com.towerdefense.model.Tower;

public class SellTowerCommand implements GameCommand {

    private final GameEngine engine;
    private final GameState gameState;
    private final Tower tower;

    public SellTowerCommand(GameEngine engine, GameState gameState, Tower tower) {
        this.engine = engine;
        this.gameState = gameState;
        this.tower = tower;
    }

    @Override
    public void execute() {
        engine.removeTower(tower);
        gameState.addGold(tower.getSellValue());
    }

    @Override
    public void undo() {
        engine.addTower(tower);
        gameState.spendGold(tower.getSellValue());
    }

    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_SELL, null,
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
