package com.towerdefense.pattern.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class CommandHistory {

    private final Deque<GameCommand> history = new ArrayDeque<>();

    public void execute(GameCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void clear() {
        history.clear();
    }

    public List<CommandRecord> getRecords() {
        List<CommandRecord> list = new ArrayList<>();
        history.forEach(cmd -> list.add(cmd.toRecord()));
        Collections.reverse(list);
        return list;
    }
}
