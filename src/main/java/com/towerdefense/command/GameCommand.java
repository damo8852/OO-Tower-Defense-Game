package com.towerdefense.command;

public interface GameCommand {
    void execute();
    void undo();
    CommandRecord toRecord();
}
