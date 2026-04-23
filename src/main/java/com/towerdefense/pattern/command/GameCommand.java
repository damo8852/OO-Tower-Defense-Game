package com.towerdefense.pattern.command;

public interface GameCommand {
    void execute();
    void undo();
    CommandRecord toRecord();
}
