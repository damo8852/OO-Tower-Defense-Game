package com.towerdefense.model;

public class Tile {

    public enum Type { EMPTY, PATH, TOWER }

    private final int row;
    private final int col;
    private Type type;

    public Tile(int row, int col, Type type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public double distanceTo(Tile other) {
        int dr = this.row - other.row;
        int dc = this.col - other.col;
        return Math.sqrt(dr * dr + dc * dc);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public Type getType() { return type; }
    void setType(Type type) { this.type = type; }
}
