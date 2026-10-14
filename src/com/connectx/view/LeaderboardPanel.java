package com.connectx.view;

import com.connectx.model.PlayerEntry;
import com.connectx.persistence.LeaderboardManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard screen rendering Top 10 players inside a JTable in a JScrollPane.
 * Offers reset/clear data functionality matching C++ choice 'C'.
 */
public class LeaderboardPanel extends JPanel {
    public interface LeaderboardActionListener {
        void onBackToMenu();
    }

    private final LeaderboardManager manager;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel statusLabel;

    public LeaderboardPanel(LeaderboardManager manager, LeaderboardActionListener listener) {
        this.manager = manager;
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42)); // Slate 900

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                new EmptyBorder(30, 35, 30, 35)
        ));
        card.setPreferredSize(new Dimension(600, 500));

        JLabel titleLabel = new JLabel("LEADERBOARD (TOP 10)");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(251, 191, 36)); // Gold
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        statusLabel = new JLabel("Scoring: Winner +10 pts, Loser -10 pts");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(148, 163, 184));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(20));

        // Table setup
        String[] columns = {"RANK", "PLAYER NAME", "SCORE"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBackground(new Color(15, 23, 42));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(59, 130, 246));
        table.setSelectionForeground(Color.WHITE);

        // Center align rank and score
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);

        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        table.getColumnModel().getColumn(1).setPreferredWidth(280);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        scrollPane.setPreferredSize(new Dimension(530, 260));
        card.add(scrollPane);
        card.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        JButton resetBtn = new JButton("Reset Leaderboard (C)");
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn = new JButton("Back to Menu (R)");

        resetBtn.setBackground(new Color(239, 68, 68));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        backBtn.setBackground(new Color(59, 130, 246));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to clear all leaderboard records in 'leaderboard.txt'?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                manager.clearLeaderboard();
                refreshData();
                JOptionPane.showMessageDialog(this, "Leaderboard has been reset!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> refreshData());
        backBtn.addActionListener(e -> listener.onBackToMenu());

        btnPanel.add(resetBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);

        card.add(btnPanel);
        add(card);

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<PlayerEntry> players = manager.loadLeaderboard();
        if (players.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "No games recorded yet", "-"});
        } else {
            int rank = 1;
            for (PlayerEntry p : players) {
                String rankStr = (rank == 1) ? "1st 🏆" : (rank == 2 ? "2nd 🥈" : (rank == 3 ? "3rd 🥉" : String.valueOf(rank)));
                String scoreFormatted = (p.getScore() > 0 ? "+" : "") + p.getScore();
                tableModel.addRow(new Object[]{rankStr, p.getName(), scoreFormatted});
                rank++;
            }
        }
    }
}
