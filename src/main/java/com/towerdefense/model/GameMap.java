package com.towerdefense.model;

import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private final int rows;
    private final int cols;
    private final Cell[][] grid;
    private final List<Cell> path;

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        this.path = new ArrayList<>();
        initGrid();
        buildPath();
    }

    private void initGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, Cell.Type.EMPTY);
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
        grid[r][c].setType(Cell.Type.PATH);
        path.add(grid[r][c]);
    }

    public Cell getCell(int row, int col) { return grid[row][col]; }
    public List<Cell> getPath() { return List.copyOf(path); }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean canPlaceTower(int row, int col) {
        return grid[row][col].getType() == Cell.Type.EMPTY;
    }

    public void placeTower(int row, int col) {
        grid[row][col].setType(Cell.Type.TOWER);
    }

    public void removeTower(int row, int col) {
        grid[row][col].setType(Cell.Type.EMPTY);
    }
}
