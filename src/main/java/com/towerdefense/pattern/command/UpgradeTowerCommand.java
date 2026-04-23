package com.towerdefense.pattern.command;

import com.towerdefense.model.GameState;
import com.towerdefense.model.Tower;

public class UpgradeTowerCommand implements GameCommand {

    private static final int DAMAGE_INCREASE  = 10;
    private static final int RANGE_INCREASE   = 1;
    public  static final int UPGRADE_COST     = 75;

    private final Tower tower;
    private final GameState gameState;

    public UpgradeTowerCommand(Tower tower, GameState gameState) {
        this.tower = tower;
        this.gameState = gameState;
    }

    @Override
    public void execute() {
        tower.upgrade(DAMAGE_INCREASE, RANGE_INCREASE);
        gameState.spendGold(UPGRADE_COST);
    }

    @Override
    public void undo() {
        tower.downgrade(DAMAGE_INCREASE, RANGE_INCREASE);
        gameState.addGold(UPGRADE_COST);
    }

    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_UPGRADE, null,
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
