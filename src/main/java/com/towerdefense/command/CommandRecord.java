package com.towerdefense.command;

public record CommandRecord(String type, String towerType, int row, int col) {

    public static final String TYPE_PLACE   = "PLACE";
    public static final String TYPE_SELL    = "SELL";
    public static final String TYPE_UPGRADE = "UPGRADE";
}
