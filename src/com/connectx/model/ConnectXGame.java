package com.connectx.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model class representing the pure business logic of the Connect X game.
 * Completely independent of any GUI or rendering code (MVC Pattern).
 */
public class ConnectXGame {
    public static final char EMPTY = ' ';
    public static final char PLAYER_RED = 'R';
    public static final char PLAYER_BLUE = 'B';

    public static final int DEFAULT_ROWS = 6;
    public static final int DEFAULT_COLS = 7;
    public static final int DEFAULT_WIN_COUNT = 4;

    private int rows;
    private int cols;
    private int winCount;
    private char[][] board;
    private char currentPlayer;
    private String player1Name;
    private String player2Name;
    private boolean gameOver;
    private char winner; // 'R', 'B', or EMPTY for draw/ongoing
    private List<Point> winningCells;

    /**
     * Constructs a ConnectX game with default 6x7 dimensions and Connect 4.
     */
    public ConnectXGame() {
        this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_WIN_COUNT);
    }

    /**
     * Constructs a ConnectX game with customizable dimensions and win criteria.
     *
     * @param rows     Number of rows (4-20)
     * @param cols     Number of columns (4-20)
     * @param winCount Number of consecutive tokens needed to win
     */
    public ConnectXGame(int rows, int cols, int winCount) {
        setDimensions(rows, cols, winCount);
        this.player1Name = "Player 1";
        this.player2Name = "Player 2";
        this.currentPlayer = PLAYER_RED;
        this.winningCells = new ArrayList<>();
        resetBoard();
    }

    /**
     * Configures the board dimensions and target win length.
     */
    public void setDimensions(int rows, int cols, int winCount) {
        if (rows < 4 || rows > 20) {
            throw new IllegalArgumentException("Rows must be between 4 and 20.");
        }
        if (cols < 4 || cols > 20) {
            throw new IllegalArgumentException("Columns must be between 4 and 20.");
        }
        int maxAllowedWin = Math.min(rows, cols);
        if (winCount < 3 || winCount > maxAllowedWin) {
            throw new IllegalArgumentException("Win target must be between 3 and " + maxAllowedWin + ".");
        }

        this.rows = rows;
        this.cols = cols;
        this.winCount = winCount;
        this.board = new char[rows][cols];
        resetBoard();
    }

    /**
     * Resets the board state for a new match while retaining players and settings.
     */
    public void resetBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = EMPTY;
            }
        }
        this.currentPlayer = PLAYER_RED;
        this.gameOver = false;
        this.winner = EMPTY;
        this.winningCells = new ArrayList<>();
    }

    /**
     * Sets the names of the two competing players.
     */
    public void setPlayerNames(String p1, String p2) {
        if (p1 == null || p1.trim().isEmpty() || p2 == null || p2.trim().isEmpty()) {
            throw new IllegalArgumentException("Player names cannot be empty.");
        }
        this.player1Name = p1.trim();
        this.player2Name = p2.trim();
    }

    /**
     * Checks if a specific column has room for another piece.
     *
     * @param col 0-based column index
     * @return true if column is not full
     */
    public boolean isValidMove(int col) {
        if (col < 0 || col >= cols || gameOver) {
            return false;
        }
        return board[0][col] == EMPTY;
    }

    /**
     * Drops a piece for the current player into the specified column using gravity.
     *
     * @param col 0-based column index
     * @return the row index where the piece settled, or -1 if the move is invalid
     */
    public int dropPiece(int col) {
        if (!isValidMove(col)) {
            return -1;
        }

        // Drop from bottom up (highest row index to 0)
        int targetRow = -1;
        for (int r = rows - 1; r >= 0; r--) {
            if (board[r][col] == EMPTY) {
                board[r][col] = currentPlayer;
                targetRow = r;
                break;
            }
        }

        if (targetRow != -1) {
            if (checkWinFrom(targetRow, col, currentPlayer)) {
                gameOver = true;
                winner = currentPlayer;
            } else if (isBoardFull()) {
                gameOver = true;
                winner = EMPTY; // Draw
            } else {
                // Switch turn
                currentPlayer = (currentPlayer == PLAYER_RED) ? PLAYER_BLUE : PLAYER_RED;
            }
        }

        return targetRow;
    }

    /**
     * Checks if the board is completely filled.
     */
    public boolean isBoardFull() {
        for (int c = 0; c < cols; c++) {
            if (board[0][c] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the latest move resulted in a win across horizontal, vertical,
     * or either diagonal axis, collecting the winning cells for visual highlighting.
     */
    private boolean checkWinFrom(int lastRow, int lastCol, char player) {
        winningCells.clear();

        // 4 directions: Horizontal (0, 1), Vertical (1, 0), Diagonal Down-Right (1, 1), Diagonal Up-Right (-1, 1)
        int[][] directions = {
            {0, 1},   // Horizontal
            {1, 0},   // Vertical
            {1, 1},   // Diagonal (\)
            {-1, 1}   // Anti-Diagonal (/)
        };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            List<Point> line = new ArrayList<>();
            line.add(new Point(lastCol, lastRow)); // Point(x, y) = Point(col, row)

            // Positive direction
            int r = lastRow + dr;
            int c = lastCol + dc;
            while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
                line.add(new Point(c, r));
                r += dr;
                c += dc;
            }

            // Negative direction
            r = lastRow - dr;
            c = lastCol - dc;
            while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
                line.add(new Point(c, r));
                r -= dr;
                c -= dc;
            }

            if (line.size() >= winCount) {
                winningCells.addAll(line);
                return true;
            }
        }

        return false;
    }

    // Getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getWinCount() {
        return winCount;
    }

    public char getCell(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return EMPTY;
        }
        return board[r][c];
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentPlayerName() {
        return (currentPlayer == PLAYER_RED) ? player1Name : player2Name;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public char getWinner() {
        return winner;
    }

    public String getWinnerName() {
        if (winner == PLAYER_RED) return player1Name;
        if (winner == PLAYER_BLUE) return player2Name;
        return null;
    }

    public String getLoserName() {
        if (winner == PLAYER_RED) return player2Name;
        if (winner == PLAYER_BLUE) return player1Name;
        return null;
    }

    public boolean isDraw() {
        return gameOver && winner == EMPTY;
    }

    public List<Point> getWinningCells() {
        return Collections.unmodifiableList(winningCells);
    }
}
