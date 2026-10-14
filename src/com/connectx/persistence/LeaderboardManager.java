package com.connectx.persistence;

import com.connectx.model.PlayerEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the persistence of player leaderboard rankings to and from 'leaderboard.txt'.
 * Directly reproduces the C++ scoring rules (+10 for winner, -10 for loser, top 10 preserved).
 */
public class LeaderboardManager {
    public static final String DEFAULT_FILE_NAME = "leaderboard.txt";
    public static final int WIN_POINTS = 10;
    public static final int LOSS_POINTS = -10;
    public static final int MAX_TOP_PLAYERS = 10;

    private final File leaderboardFile;

    public LeaderboardManager() {
        this(DEFAULT_FILE_NAME);
    }

    public LeaderboardManager(String filePath) {
        this.leaderboardFile = new File(filePath);
    }

    /**
     * Checks if a player name contains at least one alphabetical character,
     * mirroring the C++ isValidName() logic.
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads all entries from the leaderboard file.
     *
     * @return List of PlayerEntry objects, sorted descending by score.
     */
    public synchronized List<PlayerEntry> loadLeaderboard() {
        List<PlayerEntry> players = new ArrayList<>();
        if (!leaderboardFile.exists()) {
            return players;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(leaderboardFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Expect "PlayerName score" (separated by whitespace)
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    try {
                        int score = Integer.parseInt(parts[parts.length - 1]);
                        // In case name has multiple words or spaces
                        StringBuilder nameBuilder = new StringBuilder();
                        for (int i = 0; i < parts.length - 1; i++) {
                            if (i > 0) nameBuilder.append(" ");
                            nameBuilder.append(parts[i]);
                        }
                        players.add(new PlayerEntry(nameBuilder.toString(), score));
                    } catch (NumberFormatException ignored) {
                        // Skip malformed entries
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read leaderboard file: " + e.getMessage());
        }

        Collections.sort(players);
        return players;
    }

    /**
     * Updates scores for the winner (+10) and loser (-10), sorts them,
     * keeps the top 10, and writes them back to the leaderboard file.
     */
    public synchronized void recordGameResult(String winnerName, String loserName) {
        if (winnerName == null || loserName == null) return;
        winnerName = winnerName.trim();
        loserName = loserName.trim();

        List<PlayerEntry> players = loadLeaderboard();

        boolean winnerFound = false;
        boolean loserFound = false;

        for (PlayerEntry entry : players) {
            if (entry.getName().equalsIgnoreCase(winnerName)) {
                entry.addScore(WIN_POINTS);
                winnerFound = true;
            } else if (entry.getName().equalsIgnoreCase(loserName)) {
                entry.addScore(LOSS_POINTS);
                loserFound = true;
            }
        }

        if (!winnerFound) {
            players.add(new PlayerEntry(winnerName, WIN_POINTS));
        }
        if (!loserFound) {
            players.add(new PlayerEntry(loserName, LOSS_POINTS));
        }

        Collections.sort(players);

        if (players.size() > MAX_TOP_PLAYERS) {
            players = new ArrayList<>(players.subList(0, MAX_TOP_PLAYERS));
        }

        saveLeaderboard(players);
    }

    /**
     * Writes the given entries to the leaderboard file.
     */
    public synchronized void saveLeaderboard(List<PlayerEntry> players) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(leaderboardFile, false))) {
            for (PlayerEntry p : players) {
                writer.write(p.getName() + " " + p.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    /**
     * Clears all leaderboard records (equivalent to C++ option 'C').
     */
    public synchronized void clearLeaderboard() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(leaderboardFile, false))) {
            // Truncate file
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing leaderboard: " + e.getMessage());
        }
    }

    public File getLeaderboardFile() {
        return leaderboardFile;
    }
}
