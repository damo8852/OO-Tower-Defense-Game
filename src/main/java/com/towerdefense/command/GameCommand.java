package com.towerdefense.command;

public interface GameCommand {
    // run comman
    void execute();
    // reverse command
    void undo();
    // return command record for save/load serialization
    CommandRecord toRecord();
}
