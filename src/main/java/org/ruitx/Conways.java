package org.ruitx;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Conway's Game of Life
 * Simple implementation of the 'game'.
 * Made to get familiar with Java syntax and, of course, fun.
 * Rules of the game:
 * 1 - Any live cell with fewer than two live neighbors dies, as if by underpopulation.
 * 2 - Any live cell with two or three live neighbors lives on to the next generation.
 * 3 - Any live cell with more than three live neighbors dies, as if by overpopulation.
 * 4 - Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
 * How it works:
 * Make a grid, process the game and outputs it.
 * There is no animation, it just loops through all the generations and that's it.
 * Game grid is a simple boolean array matrix, a 2d array.
 * If an index of the game grid is true, then a cell is alive, if not then it's dead.
 * The game grid starts at 0,0 and goes to, for example, 24, 80
 */
public class Conways {

    private static final String MAPS_DIRECTORY = "src" + File.separator +
                                                 "main" + File.separator +
                                                 "java" + File.separator +
                                                 "org" + File.separator +
                                                 "ruitx" + File.separator +
                                                 "maps" + File.separator;

    private static final int MAX_SIMULATIONS = 500;
    private static final int RENDER_SPEED_IN_MILLIS = 100;

    private static final int MAX_GRID_LENGTH_Y = 30;
    private static final int MAX_GRID_LENGTH_X = 100;

    //"█■☼Θ"
    private static final String ALIVE_CHAR = "☼";
    private static final String DEAD_CHAR = " ";

    private GameGrid currentGameGrid;
    private ArrayList<GameGrid> generations;
    private int currentWorldGenerations;
    private String currentWorldPath;

    public Conways() {
        this.currentGameGrid = new GameGrid();
        this.generations = new ArrayList<>();
        this.currentWorldPath = "";
        this.currentWorldGenerations = 0;
    }

    public String toString() {
        return "This is a Conways Class";
    }

    private void resetGenerations() {
        this.generations = new ArrayList<>();
    }

    public String loadMap() {
        this.resetGenerations();
        GameGrid newGameGrid = new GameGrid();

        String currentFilePath = this.pickMapFile();
        if (currentFilePath == null) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(currentFilePath));
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        String currentFileLine = "";
        int line = 0;

        while (true) {
            try {
                if (!((currentFileLine = reader.readLine()) != null)) break;
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            String[] currentFileLineArray = currentFileLine.split("");

            int xSize = currentFileLineArray.length;
            if (xSize > MAX_GRID_LENGTH_X)  {
                xSize = MAX_GRID_LENGTH_X;
            }

            for (int x = 0; x < xSize; x++) {
                newGameGrid.setCoordinatesValueTo(line, x, currentFileLineArray[x].equals("*"));
            }

            line++;
            if (line > MAX_GRID_LENGTH_Y)  {
                break;
            }
        }

        this.generations.add(newGameGrid);
        this.currentGameGrid = newGameGrid;
        this.simulateMap();

        return currentFilePath;
    }

    public void printMap() {
        this.printCurrentMap();
    }

    public void drawMap() {
        this.drawCurrentMap();
    }

    /**
     *
     */
    public String generateRandomMap() {
        this.resetGenerations();
        this.generateRandomMapGrid();
        return "Random Map with " + this.generations.size() + " generations";
    }

    /**
     *
     */
    public String generateRandomStableMap() {

        this.resetGenerations();
        this.generateRandomMapGrid();

        if (this.generations.size() == MAX_SIMULATIONS ) {
            this.currentWorldGenerations++;
            System.out.print(" " + this.currentWorldGenerations + " ");
            this.generateRandomStableMap();
        }

        this.currentWorldGenerations = 0;
        return "Random Stable Map with " + this.generations.size() + " generations";
    }

    private String pickMapFile() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try {
            System.out.print("[map]> ");
            input = reader.readLine();
        } catch (IOException e) {
            System.out.println("Something went wrong. Please try again.\nDetails: " + e.getMessage());
            return null;
        }

        File checkFile = new File(MAPS_DIRECTORY + input);
        if (!checkFile.exists()) {
            System.out.println("Something went wrong. Please try again.\nDetails: File does not exist.");
            return null;
        }


        return checkFile.getPath();
    }

    /**
     * Process's the game next generation.
     * The meat and potatoes of the game.
     * It checks for the 4 rules on each cell of the game grid.
     * After it generates a new game grid with the new generation.
     */
    private GameGrid nextGeneration() {
        GameGrid newGameGrid = new GameGrid();

        for (int y = 0; y < this.currentGameGrid.lengthY(); y++) {
            for (int x = 0; x < this.currentGameGrid.lengthX(); x++) {
                // if cell is alive
                if (this.currentGameGrid.checkCoordinates(y, x)) {
                    if (checkIfCellIsGoingToDie(y, x)) {
                        newGameGrid.setCoordinatesValueTo(y, x, false);
                    }
                    if (checkIfCellStaysAlive(y, x)) {
                        newGameGrid.setCoordinatesValueTo(y, x, true);
                    }
                    // if cell is dead
                } else {
                    if (checkIfCellIsGoingToBirth(y, x)) {
                        newGameGrid.setCoordinatesValueTo(y, x, true);
                    }
                }
            }
        }

        return newGameGrid;
    }

    /**
     * Prints the current game grid.
     * If current cell is false, then it's a dead cell.
     * If current cell is true, then it's an alive cell.
     * Note: some characters won't work well on Windows.
     */
    private void printGrid(GameGrid gameGrid) {
        for (int y = 0; y < gameGrid.lengthY(); y++) {
            //System.out.print("|");
            for (int x = 0; x < gameGrid.lengthX(); x++) {
                if (gameGrid.checkCoordinates(y, x)) {
                    System.out.printf("%1s", ALIVE_CHAR);
                } else {
                    System.out.printf("%1s", DEAD_CHAR);
                }

            }
            System.out.println(" ");
        }
        System.out.println(" --- ");
    }

    /**
     * Generates a random map grid.
     * It randomizes each time the game is run.
     * Simple randomness: Gets a number between 0 and {@code gridLengthY} * {@code gridLengthX};
     * If is an even number, the cell is alive, if not then it's dead.
     */
    private void generateRandomMapGrid() {
        // random map grid
        for (int y = 0; y < this.currentGameGrid.lengthY(); y++) {
            for (int x = 0; x < this.currentGameGrid.lengthX(); x++) {
                int chance = (int) Math.floor(Math.random() * (this.currentGameGrid.lengthY() * this.currentGameGrid.lengthX()));
                if (chance % 2 == 0) {
                    this.currentGameGrid.setCoordinatesValueTo(y, x, false);
                } else {
                    this.currentGameGrid.setCoordinatesValueTo(y, x, true);
                }
            }
        }

        this.simulateMap();
    }

    private int simulateMap() {

        for (int i = 0; i < MAX_SIMULATIONS; i++) {
            GameGrid newGameGrid = this.nextGeneration();
            if (this.compareGameGrid(this.currentGameGrid, newGameGrid)) {
                //System.out.println("End of simulation found at simulation nº " + i + ": Found equal game grid.");
                return i;
            }

            // sets the current game grid to the new game grid
            this.currentGameGrid = newGameGrid;
            this.generations.add(newGameGrid);
        }

        //System.out.println("End of simulation found at simulation nº " + MAX_SIMULATIONS + ": Max simulation times reached.");
        return MAX_SIMULATIONS;
    }

    private boolean compareGameGrid(GameGrid gameGrid1, GameGrid gameGrid2) {
        for (int i = 0; i < gameGrid1.lengthY(); i++) {
            for (int j = 0; j < gameGrid1.lengthX(); j++) {
                if ((gameGrid1.checkCoordinates(i, j) != gameGrid2.checkCoordinates(i, j)))
                    return false;
            }
        }

        return true;
    }

    private void printCurrentMap() {
        Iterator<GameGrid> iterator = this.generations.iterator();
        while (iterator.hasNext()) {
            this.printGrid(iterator.next());
            try {
                Thread.sleep(RENDER_SPEED_IN_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void drawCurrentMap() {
        Tui.init(MAX_GRID_LENGTH_X, MAX_GRID_LENGTH_Y);

        Iterator<GameGrid> iterator = this.generations.iterator();
        while (iterator.hasNext()) {
            Tui.drawGrid(iterator.next());
            try {
                Thread.sleep(RENDER_SPEED_IN_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Tui.close();
    }

    /**
     * Check if a cell is going to birth on the next generation.
     * Rule 4: Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
     * Counts the neighbors with {@code getCellNeighborsArray}.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return isGoingToBirth  {@code boolean}
     */
    private boolean checkIfCellIsGoingToBirth(int gridY, int gridX) {
        boolean isGoingToBirth = false;
        boolean[] neighborArray = getCellNeighborsArray(gridY, gridX);

        int countLiveNeighbor = 0;
        for (int i = 0; i < neighborArray.length; i++) {
            if (neighborArray[i]) {
                countLiveNeighbor++;
            }
        }

        if (countLiveNeighbor == 3) {
            isGoingToBirth = true;
        }

        return isGoingToBirth;
    }

    /**
     * Check if a cell is going to die on the next generation.
     * Rule 1: Any live cell with fewer than two live neighbors dies, as if by underpopulation.
     * Rule 3: Any live cell with more than three live neighbors dies, as if by overpopulation.
     * Counts the neighbors with {@code getCellNeighborsArray}.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return isGoingToDie    {@code boolean}
     */
    private boolean checkIfCellIsGoingToDie(int gridY, int gridX) {
        boolean isGoingToDie = false;
        boolean[] neighborArray = getCellNeighborsArray(gridY, gridX);

        int countLiveNeighbor = 0;
        for (int i = 0; i < neighborArray.length; i++) {
            if (neighborArray[i]) {
                countLiveNeighbor++;
            }
        }

        if (countLiveNeighbor < 2 || countLiveNeighbor > 3) {
            isGoingToDie = true;
        }

        return isGoingToDie;
    }

    /**
     * Check if a cell is going to stay alive on the next generation.
     * Rule 2: Any live cell with two or three live neighbors lives on to the next generation.
     * Counts the neighbors with the result of {@code getCellNeighborsArray}.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return isAlive         {@code boolean}
     */
    private boolean checkIfCellStaysAlive(int gridY, int gridX) {
        boolean isAlive = false;
        boolean[] neighborArray = getCellNeighborsArray(gridY, gridX);

        int countLiveNeighbor = 0;
        for (int i = 0; i < neighborArray.length; i++) {
            if (neighborArray[i]) {
                countLiveNeighbor++;
            }
        }

        if (countLiveNeighbor == 2 || countLiveNeighbor == 3) {
            isAlive = true;
        }

        return isAlive;
    }

    /**
     * Check for a cell current neighbors.
     * Each cell has 8 neighbors, which are the adjacent cells.
     * We check for all of them and returns {@code []boolean}.
     * If an index of the array is true, then has a neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return neighborsExistence  {@code []boolean}
     */
    private boolean[] getCellNeighborsArray(int gridY, int gridX) {
        // clockwise check
        // top          -> y-1, x
        // top-right    -> y-1, x+1
        // right        -> y, x+1
        // down-right   -> y+1, x+1
        // down         -> y+1, x
        // down-left    -> y+1, x-1
        // left         -> y, x-1
        // top-left     -> y-1, x-1

        // 8 possible neighbors
        boolean[] neighborsExistence = new boolean[8];

        neighborsExistence[0] = checkNeighborTop(gridY, gridX);
        neighborsExistence[1] = checkNeighborTopRight(gridY, gridX);
        neighborsExistence[2] = checkNeighborRight(gridY, gridX);
        neighborsExistence[3] = checkNeighborDownRight(gridY, gridX);
        neighborsExistence[4] = checkNeighborDown(gridY, gridX);
        neighborsExistence[5] = checkNeighborDownLeft(gridY, gridX);
        neighborsExistence[6] = checkNeighborLeft(gridY, gridX);
        neighborsExistence[7] = checkNeighborTopLeft(gridY, gridX);

        return neighborsExistence;
    }

    /**
     * Check if a cell has a top neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborTop(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // top          -> y-1, x
        // out-of-bounds check
        if (gridY - 1 >= 0) {
            if (this.currentGameGrid.checkCoordinates(gridY - 1, gridX)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a top right neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborTopRight(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // top-right    -> y-1, x+1
        // out-of-bounds check
        if (gridY - 1 >= 0 && gridX + 1 < this.currentGameGrid.lengthX()) {
            if (this.currentGameGrid.checkCoordinates(gridY - 1, gridX + 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a right neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborRight(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // right        -> y, x+1
        // out-of-bounds check
        if (gridX + 1 < this.currentGameGrid.lengthX()) {
            if (this.currentGameGrid.checkCoordinates(gridY, gridX + 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a down right neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborDownRight(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // down-right   -> y+1, x+1
        // out-of-bounds check
        if (gridY + 1 < this.currentGameGrid.lengthY() && gridX + 1 < this.currentGameGrid.lengthX()) {
            if (this.currentGameGrid.checkCoordinates(gridY + 1, gridX + 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a down neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborDown(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // down         -> y+1, x
        // out-of-bounds check
        if (gridY + 1 < this.currentGameGrid.lengthY()) {
            if (this.currentGameGrid.checkCoordinates(gridY + 1, gridX)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a down left neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborDownLeft(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // down-left    -> y+1, x-1
        // out-of-bounds check
        if (gridY + 1 < this.currentGameGrid.lengthY() && gridX - 1 >= 0) {
            if (this.currentGameGrid.checkCoordinates(gridY + 1, gridX - 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a left neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborLeft(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // left         -> y, x-1
        // out-of-bounds check
        if (gridX - 1 >= 0) {
            if (this.currentGameGrid.checkCoordinates(gridY, gridX - 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    /**
     * Check if a cell has a top left neighbor.
     *
     * @param gridY {@code int}
     * @param gridX {@code int}
     * @return hasNeighbor     {@code boolean}
     */
    private boolean checkNeighborTopLeft(int gridY, int gridX) {
        boolean hasNeighbor = false;
        // top-left     -> y-1, x-1
        // out-of-bounds check
        if (gridY - 1 >= 0 && gridX - 1 >= 0) {
            if (this.currentGameGrid.checkCoordinates(gridY - 1, gridX - 1)) {
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    public static class GameGrid {

        private int gridLengthY;
        private int gridLengthX;
        private Boolean[][] gameGrid;

        public GameGrid() {
            this.gridLengthY = MAX_GRID_LENGTH_Y;
            this.gridLengthX = MAX_GRID_LENGTH_X;
            this.gameGrid = new Boolean[gridLengthY][gridLengthX];

            this.initGrid();
        }

        int lengthY() {
            return this.gameGrid.length;
        }

        int lengthX() {
            return this.gameGrid[this.lengthY() - 1].length;
        }

        boolean checkCoordinates(int y, int x) {
            return this.gameGrid[y][x];
        }

        private void setCoordinatesValueTo(int y, int x, boolean value) {
            this.gameGrid[y][x] = value;
        }

        private void initGrid() {
            for (int y = 0; y < this.lengthY(); y++) {
                for (int x = 0; x < this.lengthX(); x++) {
                    this.setCoordinatesValueTo(y, x, false);
                }
            }
        }
    }
}