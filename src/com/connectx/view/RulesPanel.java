package com.connectx.view;

import com.connectx.model.ConnectXGame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Rules and Controls screen detailing mechanics, scoring, and instructions.
 * Accurately replicates the C++ Rules() output.
 */
public class RulesPanel extends JPanel {
    public interface RulesActionListener {
        void onBackToMenu();
    }

    private final JLabel currentConfigLabel;

    public RulesPanel(ConnectXGame game, RulesActionListener listener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42)); // Slate 900

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                new EmptyBorder(30, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(650, 480));

        JLabel titleLabel = new JLabel("RULES & HOW TO PLAY");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        currentConfigLabel = new JLabel("Current Settings: " + game.getRows() + " rows × " + game.getCols() + " cols | Connect " + game.getWinCount() + " to win");
        currentConfigLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        currentConfigLabel.setForeground(new Color(251, 191, 36));
        currentConfigLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(currentConfigLabel);
        card.add(Box.createVerticalStrut(20));

        // Content Area
        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rulesText.setForeground(new Color(226, 232, 240));
        rulesText.setBackground(new Color(15, 23, 42));
        rulesText.setBorder(new EmptyBorder(15, 15, 15, 15));

        rulesText.setText(
            "GAME RULES:\n" +
            "1. Two-player tactical turn-based battle (Player 1: Red vs. Player 2: Blue).\n" +
            "2. Players alternate turns dropping a colored piece into any non-full column.\n" +
            "3. Gravity pulls each piece straight down to the lowest unoccupied slot.\n" +
            "4. A player wins immediately if they align the required number of pieces:\n" +
            "   - Horizontally (across a row)\n" +
            "   - Vertically (down a column)\n" +
            "   - Diagonally (left-to-right or right-to-left)\n" +
            "5. If all slots fill up without a winner, the match is declared a Draw.\n\n" +
            "POINT SYSTEM & LEADERBOARD:\n" +
            "• Winner gains +10 points on the leaderboard.\n" +
            "• Loser receives -10 points on the leaderboard.\n" +
            "• Top 10 rankings are automatically preserved in 'leaderboard.txt'.\n\n" +
            "CONTROLS:\n" +
            "• Hover over any column to see the target trajectory.\n" +
            "• Click anywhere in the column to drop your piece."
        );

        JScrollPane scrollPane = new JScrollPane(rulesText);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        scrollPane.setPreferredSize(new Dimension(580, 270));
        card.add(scrollPane);

        card.add(Box.createVerticalStrut(20));

        JButton backBtn = new JButton("Return to Main Menu");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setBackground(new Color(59, 130, 246));
        backBtn.setForeground(Color.WHITE);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> listener.onBackToMenu());
        card.add(backBtn);

        add(card);
    }

    public void updateConfig(ConnectXGame game) {
        currentConfigLabel.setText("Current Settings: " + game.getRows() + " rows × " + game.getCols() + " cols | Connect " + game.getWinCount() + " to win");
    }
}
