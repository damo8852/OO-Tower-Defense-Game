package com.towerdefense.persistence;

import com.towerdefense.engine.GameEngine;
import com.towerdefense.model.Tile;
import com.towerdefense.model.GameState;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.tower.Tower;
import com.towerdefense.model.tower.TowerFactory;
import com.towerdefense.command.CommandHistory;
import com.towerdefense.command.CommandRecord;
import com.towerdefense.command.PlaceTowerCommand;
import com.towerdefense.command.SellTowerCommand;
import com.towerdefense.command.UpgradeTowerCommand;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameLoadService {

    public void load(String filePath, GameEngine engine, GameState state,
                     CommandHistory history) throws IOException {
        String content = Files.readString(Path.of(filePath));
        int lives = extractTopLevelInt(content, GameSaveService.FIELD_LIVES);
        int score = extractTopLevelInt(content, GameSaveService.FIELD_SCORE);
        int wave  = extractTopLevelInt(content, GameSaveService.FIELD_WAVE);
        int gold  = extractTopLevelInt(content, GameSaveService.FIELD_GOLD);

        for (CommandRecord r : parseCommands(content)) {
            replayCommand(r, engine, state, history);
        }
        state.restoreSnapshot(lives, score, wave, gold);
    }

    private void replayCommand(CommandRecord r, GameEngine engine, GameState state,
                               CommandHistory history) {
        switch (r.type()) {
            case CommandRecord.TYPE_PLACE -> {
                Tile cell = engine.getMap().getCell(r.row(), r.col());
                Tower tower = createTower(TowerType.valueOf(r.towerType()), cell);
                history.execute(new PlaceTowerCommand(engine, state, tower));
            }
            case CommandRecord.TYPE_UPGRADE ->
                engine.getTowerAt(r.row(), r.col())
                      .ifPresent(t -> history.execute(new UpgradeTowerCommand(t, state)));
            case CommandRecord.TYPE_SELL ->
                engine.getTowerAt(r.row(), r.col())
                      .ifPresent(t -> history.execute(new SellTowerCommand(engine, state, t)));
        }
    }

    private Tower createTower(TowerType type, Tile cell) {
        return TowerFactory.forType(type).create(cell);
    }

    private List<CommandRecord> parseCommands(String json) {
        List<CommandRecord> list = new ArrayList<>();
        String array = extractCommandsArray(json);
        int pos = 0;
        while (true) {
            int open  = array.indexOf('{', pos);
            if (open == -1) break;
            int close = array.indexOf('}', open);
            if (close == -1) break;
            list.add(parseRecord(array.substring(open, close + 1)));
            pos = close + 1;
        }
        return list;
    }

    private String extractCommandsArray(String json) {
        int idx = json.indexOf('"' + GameSaveService.FIELD_COMMANDS + '"');
        if (idx == -1) return "[]";
        int start = json.indexOf('[', idx);
        if (start == -1) return "[]";
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (ch == '[') depth++;
            else if (ch == ']' && --depth == 0) return json.substring(start, i + 1);
        }
        return "[]";
    }

    private CommandRecord parseRecord(String block) {
        return new CommandRecord(
                extractString(block, GameSaveService.FIELD_TYPE),
                extractString(block, GameSaveService.FIELD_TOWER),
                extractInt(block, GameSaveService.FIELD_ROW),
                extractInt(block, GameSaveService.FIELD_COL));
    }

    private int extractTopLevelInt(String json, String key) {
        int commandsStart = json.indexOf('"' + GameSaveService.FIELD_COMMANDS + '"');
        String topLevel = commandsStart > 0 ? json.substring(0, commandsStart) : json;
        String search = "\"" + key + "\":";
        int idx = topLevel.indexOf(search);
        if (idx == -1) return 0;
        int start = idx + search.length();
        while (start < topLevel.length() && topLevel.charAt(start) == ' ') start++;
        int end = start;
        while (end < topLevel.length() && (Character.isDigit(topLevel.charAt(end)) || topLevel.charAt(end) == '-')) end++;
        return start == end ? 0 : Integer.parseInt(topLevel.substring(start, end));
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        int end = json.indexOf('"', start);
        return end == -1 ? null : json.substring(start, end);
    }

    private int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return 0;
        int start = idx + search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        return start == end ? 0 : Integer.parseInt(json.substring(start, end));
    }
}
