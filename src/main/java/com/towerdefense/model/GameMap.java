package com.towerdefense.model;

import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private final int rows;
    private final int cols;
    private final Tile[][] grid;
    private final List<Tile> path;

    private GameMap(Builder builder) {
        this.rows = builder.rows;
        this.cols = builder.cols;
        this.grid = new Tile[rows][cols];
        this.path = new ArrayList<>();
        initGrid();
        for (int[] coord : builder.pathCoords)
            markPath(coord[0], coord[1]);
    }

    public static class Builder {
        private int rows = 10;
        private int cols = 10;
        private final List<int[]> pathCoords = new ArrayList<>();

        // set grid size
        public Builder size(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            return this;
        }

        // queue a tile as part of the path
        public Builder markPath(int r, int c) {
            pathCoords.add(new int[]{r, c});
            return this;
        }

        // trace a U-shaped path around the grid
        public Builder withUPath() {
            for (int c = 0; c < cols; c++) markPath(0, c);
            for (int r = 1; r < rows; r++) markPath(r, cols - 1);
            for (int c = cols - 2; c >= 0; c--) markPath(rows - 1, c);
            return this;
        }

        // pick and apply the right path shape
        public Builder shape(MapType maptype){
            if(maptype == MapType.U_SHAPE){
                withUPath();
            }else{
                withSPath();
            }
            return this;
        }

        // trace an S-shaped snake path across rows
        public Builder withSPath() {
            for (int r = 0; r < rows; r += 2) {
                boolean leftToRight = (r / 2) % 2 == 0;
                if (leftToRight) {
                    for (int c = 0; c < cols; c++) markPath(r, c);
                    if (r + 1 < rows) markPath(r + 1, cols - 1);
                } else {
                    for (int c = cols - 1; c >= 0; c--) markPath(r, c);
                    if (r + 1 < rows) markPath(r + 1, 0);
                }
            }
            return this;
        }

        // build and return the map
        public GameMap build() {
            return new GameMap(this);
        }

        // default 10x10 u-shape map
        public static GameMap defaultMap() {
            return new Builder().size(10, 10).withUPath().build();
        }

        // default 10x10 s-shape map
        public static GameMap sShapeMap() {
            return new Builder().size(10, 10).withSPath().build();
        }

        // custom size u-shape map
        public static GameMap customMap(int rows, int cols) {
            return new Builder().size(rows, cols).withUPath().build();
        }
    }

    // fill all cells as empty
    private void initGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(r, c, Tile.Type.EMPTY);
            }
        }
    }

    // mark tile as path and add to path list
    private void markPath(int r, int c) {
        grid[r][c].setType(Tile.Type.PATH);
        path.add(grid[r][c]);
    }

    // return tile at row, col
    public Tile getCell(int row, int col) { return grid[row][col]; }
    // return ordered path tiles
    public List<Tile> getPath() { return List.copyOf(path); }
    // return row count
    public int getRows() { return rows; }
    // return column count
    public int getCols() { return cols; }

    // true if cell is empty and can place tower
    public boolean canPlaceTower(int row, int col) {
        return grid[row][col].getType() == Tile.Type.EMPTY;
    }

    // mark cell as tower
    public void placeTower(int row, int col) {
        grid[row][col].setType(Tile.Type.TOWER);
    }

    // reset tower cell back to empty
    public void removeTower(int row, int col) {
        grid[row][col].setType(Tile.Type.EMPTY);
    }
}
