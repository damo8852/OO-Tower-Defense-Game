package com.towerdefense.command;

import com.towerdefense.model.GameState;
import com.towerdefense.model.tower.Tower;

public class UpgradeTowerCommand implements GameCommand {

    private static final int DAMAGE_INCREASE = 10;
    private static final int RANGE_INCREASE = 1;
    public  static final int UPGRADE_COST = 75;

    private final Tower tower;
    private final GameState gameState;

    public UpgradeTowerCommand(Tower tower, GameState gameState) {
        this.tower = tower;
        this.gameState = gameState;
    }

    // boost tower stats and spend upgrade cost
    @Override
    public void execute() {
        tower.upgrade(DAMAGE_INCREASE, RANGE_INCREASE);
        gameState.spendGold(UPGRADE_COST);
    }

    // reverse stat boost and refund upgrade cost
    @Override
    public void undo() {
        tower.downgrade(DAMAGE_INCREASE, RANGE_INCREASE);
        gameState.addGold(UPGRADE_COST);
    }

    // return command record for save/load
    @Override
    public CommandRecord toRecord() {
        return new CommandRecord(CommandRecord.TYPE_UPGRADE, null,
                tower.getPosition().getRow(), tower.getPosition().getCol());
    }
}
