package com.towerdefense.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.towerdefense.command.CommandRecord;
import com.towerdefense.model.GameState;

public class GameSaveService {

    static final String FIELD_LIVES = "lives";
    static final String FIELD_SCORE = "score";
    static final String FIELD_WAVE = "wave";
    static final String FIELD_GOLD = "gold";
    static final String FIELD_COMMANDS = "commands";
    static final String FIELD_TYPE = "type";
    static final String FIELD_TOWER = "tower";
    static final String FIELD_ROW = "row";
    static final String FIELD_COL = "col";

    // save game state and commands to json file
    public void save(List<CommandRecord> commands, String filePath, GameState state) throws IOException {
        String json = buildJson(commands, state);
        Files.writeString(Path.of(filePath), json);
    }

    // build the full json string
    private String buildJson(List<CommandRecord> commands, GameState state) {
        StringBuilder sb = new StringBuilder("{\n");
        appendStateFields(sb, state);
        appendCommandsArray(sb, commands);
        sb.append('}');
        return sb.toString();
    }

    // append state fields to json
    private void appendStateFields(StringBuilder sb, GameState state) {
        sb.append("  \"").append(FIELD_LIVES).append("\": ").append(state.getLives()).append(",\n");
        sb.append("  \"").append(FIELD_SCORE).append("\": ").append(state.getScore()).append(",\n");
        sb.append("  \"").append(FIELD_WAVE).append("\": ").append(state.getWave()).append(",\n");
        sb.append("  \"").append(FIELD_GOLD).append("\": ").append(state.getGold()).append(",\n");
    }

    // append commands array to json
    private void appendCommandsArray(StringBuilder sb, List<CommandRecord> commands) {
        sb.append("  \"").append(FIELD_COMMANDS).append("\": [\n");
        for (int i = 0; i < commands.size(); i++) {
            sb.append(serializeCommand(commands.get(i)));
            if (i < commands.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ]\n");
    }

    // serialize one command to json object
    private String serializeCommand(CommandRecord r) {
        StringBuilder sb = new StringBuilder("    {");
        sb.append('"').append(FIELD_TYPE).append("\":\"").append(r.type()).append('"');
        if (r.towerType() != null) {
            sb.append(",\"").append(FIELD_TOWER).append("\":\"").append(r.towerType()).append('"');
        }
        sb.append(",\"").append(FIELD_ROW).append("\":").append(r.row());
        sb.append(",\"").append(FIELD_COL).append("\":").append(r.col());
        sb.append('}');
        return sb.toString();
    }
}
