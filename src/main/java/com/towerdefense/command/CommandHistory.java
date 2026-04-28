package com.towerdefense.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class CommandHistory {

    private final Deque<GameCommand> history = new ArrayDeque<>();

    // run command and pop to history stack for undo
    public void execute(GameCommand command) {
        command.execute();
        history.push(command);
    }

    // pops and undoes the most recent command 
    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }

    // true if there are commands to undo
    public boolean canUndo() {
        return !history.isEmpty();
    }

    // empty command history
    public void clear() {
        history.clear();
    }

    // return command history 
    public List<CommandRecord> getRecords() {
        List<CommandRecord> list = new ArrayList<>();
        history.forEach(cmd -> list.add(cmd.toRecord()));
        Collections.reverse(list);
        return list;
    }
}
