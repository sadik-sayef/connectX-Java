package com.connectx.model;

import java.util.Objects;

/**
 * Encapsulates a player's leaderboard entry with their name and accumulated score.
 * Retains the parity with C++ struct PlayerEntry and descending score comparator.
 */
public class PlayerEntry implements Comparable<PlayerEntry> {
    private String name;
    private int score;

    public PlayerEntry(String name, int score) {
        this.name = name != null ? name.trim() : "Unknown";
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int delta) {
        this.score += delta;
    }

    @Override
    public int compareTo(PlayerEntry other) {
        // Descending order of score, tie-break by name alphabetically
        int scoreCompare = Integer.compare(other.score, this.score);
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerEntry)) return false;
        PlayerEntry that = (PlayerEntry) o;
        return score == that.score && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public String toString() {
        return name + " " + score;
    }
}
