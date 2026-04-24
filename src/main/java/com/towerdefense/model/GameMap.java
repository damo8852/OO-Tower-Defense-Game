package com.towerdefense.model;

import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private final int rows;
    private final int cols;
    private final Tile[][] grid;
    private final List<Tile> path;

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];
        this.path = new ArrayList<>();
        initGrid();
        buildPath();
    }

    private void initGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(r, c, Tile.Type.EMPTY);
            }
        }
    }

    // Fixed U-shaped path: left-to-right along row 0, down last col, right-to-left along last row
    private void buildPath() {
        for (int c = 0; c < cols; c++)       markPath(0, c);
        for (int r = 1; r < rows; r++)        markPath(r, cols - 1);
        for (int c = cols - 2; c >= 0; c--)  markPath(rows - 1, c);
    }

    private void markPath(int r, int c) {
        grid[r][c].setType(Tile.Type.PATH);
        path.add(grid[r][c]);
    }

    public Tile getCell(int row, int col) { return grid[row][col]; }
    public List<Tile> getPath() { return List.copyOf(path); }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean canPlaceTower(int row, int col) {
        return grid[row][col].getType() == Tile.Type.EMPTY;
    }

    public void placeTower(int row, int col) {
        grid[row][col].setType(Tile.Type.TOWER);
    }

    public void removeTower(int row, int col) {
        grid[row][col].setType(Tile.Type.EMPTY);
    }
}
