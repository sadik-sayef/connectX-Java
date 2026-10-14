package com.connectx.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Menu screen for Connect X.
 * Reflects the classic arcade terminal menu from the original C++ program.
 */
public class MainMenuPanel extends JPanel {
    public interface MenuActionListener {
        void onStartGame();
        void onRules();
        void onSettings();
        void onLeaderboard();
        void onExit();
    }

    public MainMenuPanel(MenuActionListener listener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42)); // Slate 900

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 41, 59)); // Slate 800
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                new EmptyBorder(35, 50, 40, 50)
        ));

        // Title Header
        JLabel welcomeLabel = new JLabel("WELCOME TO");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        welcomeLabel.setForeground(new Color(148, 163, 184));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(welcomeLabel);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        titleRow.setOpaque(false);
        JLabel titleConnect = new JLabel("CONNECT ");
        titleConnect.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleConnect.setForeground(Color.WHITE);

        JLabel titleX = new JLabel("X");
        titleX.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleX.setForeground(new Color(239, 68, 68)); // Red accent

        titleRow.add(titleConnect);
        titleRow.add(titleX);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleRow);

        JLabel subLabel = new JLabel("Customizable 2-Player Tactical Strategy Game");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLabel.setForeground(new Color(148, 163, 184));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subLabel);

        card.add(Box.createVerticalStrut(30));

        // Menu Buttons
        JButton startBtn = createStyledButton("[1]  Start Game", new Color(34, 197, 94), Color.WHITE);
        JButton rulesBtn = createStyledButton("[2]  Controls and Rules", new Color(59, 130, 246), Color.WHITE);
        JButton settingsBtn = createStyledButton("[3]  Settings (Size & Rules)", new Color(168, 85, 247), Color.WHITE);
        JButton leaderboardBtn = createStyledButton("[4]  View Leaderboard", new Color(234, 179, 8), Color.BLACK);
        JButton exitBtn = createStyledButton("[5]  Exit Game", new Color(239, 68, 68), Color.WHITE);

        startBtn.addActionListener(e -> listener.onStartGame());
        rulesBtn.addActionListener(e -> listener.onRules());
        settingsBtn.addActionListener(e -> listener.onSettings());
        leaderboardBtn.addActionListener(e -> listener.onLeaderboard());
        exitBtn.addActionListener(e -> listener.onExit());

        card.add(startBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(rulesBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(settingsBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(leaderboardBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(exitBtn);

        add(card);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(320, 44));
        btn.setPreferredSize(new Dimension(320, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }
}
