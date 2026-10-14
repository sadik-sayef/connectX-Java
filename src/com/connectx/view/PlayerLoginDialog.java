package com.connectx.view;

import com.connectx.persistence.LeaderboardManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog to capture and validate Player 1 (Red) and Player 2 (Blue) names.
 * Enforces C++ parity:
 * 1. Must contain at least one alphabetical character.
 * 2. Player 1 and Player 2 names cannot be identical.
 */
public class PlayerLoginDialog extends JDialog {
    private final JTextField p1Field;
    private final JTextField p2Field;
    private boolean confirmed = false;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    public PlayerLoginDialog(Frame owner, String defaultP1, String defaultP2) {
        super(owner, "Player Login", true);
        setLayout(new BorderLayout(10, 10));
        setSize(420, 300);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel titleLabel = new JLabel("Enter Player Names");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel subLabel = new JLabel("Names must contain letters and be distinct.");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Player 1 input
        JPanel p1Row = new JPanel(new BorderLayout(10, 5));
        JLabel p1Label = new JLabel("Player 1 (Red):");
        p1Label.setFont(new Font("SansSerif", Font.BOLD, 13));
        p1Label.setForeground(new Color(220, 38, 38));
        p1Field = new JTextField(defaultP1, 15);
        p1Row.add(p1Label, BorderLayout.WEST);
        p1Row.add(p1Field, BorderLayout.CENTER);
        contentPanel.add(p1Row);
        contentPanel.add(Box.createVerticalStrut(12));

        // Player 2 input
        JPanel p2Row = new JPanel(new BorderLayout(10, 5));
        JLabel p2Label = new JLabel("Player 2 (Blue):");
        p2Label.setFont(new Font("SansSerif", Font.BOLD, 13));
        p2Label.setForeground(new Color(37, 99, 235));
        p2Field = new JTextField(defaultP2, 15);
        p2Row.add(p2Label, BorderLayout.WEST);
        p2Row.add(p2Field, BorderLayout.CENTER);
        contentPanel.add(p2Row);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        JButton cancelButton = new JButton("Cancel");
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        startButton.addActionListener(e -> validateAndSubmit());
        p2Field.addActionListener(e -> validateAndSubmit());
        p1Field.addActionListener(e -> p2Field.requestFocusInWindow());

        buttonPanel.add(cancelButton);
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(startButton);
    }

    private void validateAndSubmit() {
        String p1 = p1Field.getText().trim();
        String p2 = p2Field.getText().trim();

        if (!LeaderboardManager.isValidName(p1)) {
            JOptionPane.showMessageDialog(this,
                    "Player 1 name must contain at least one alphabetical letter!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            p1Field.requestFocusInWindow();
            return;
        }

        if (!LeaderboardManager.isValidName(p2)) {
            JOptionPane.showMessageDialog(this,
                    "Player 2 name must contain at least one alphabetical letter!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            p2Field.requestFocusInWindow();
            return;
        }

        if (p1.equalsIgnoreCase(p2)) {
            JOptionPane.showMessageDialog(this,
                    "Player 2 name cannot be the same as Player 1!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            p2Field.requestFocusInWindow();
            return;
        }

        this.player1Name = p1;
        this.player2Name = p2;
        this.confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}
