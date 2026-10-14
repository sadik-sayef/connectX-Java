package com.connectx.view;

import com.connectx.model.ConnectXGame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings Screen for configuring board dimensions and win length.
 * Enforces C++ constraints:
 * - Rows: 4 to 20
 * - Columns: 4 to 20
 * - Connect Limit: 3 to min(rows, cols)
 */
public class SettingsPanel extends JPanel {
    public interface SettingsListener {
        void onSaveSettings(int rows, int cols, int winCount);
        void onCancel();
    }

    private final JSpinner rowsSpinner;
    private final JSpinner colsSpinner;
    private final JSpinner winCountSpinner;
    private final SpinnerNumberModel winModel;

    public SettingsPanel(ConnectXGame game, SettingsListener listener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42)); // Slate 900

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 41, 59)); // Slate 800
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel titleLabel = new JLabel("GAME SETTINGS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subLabel = new JLabel("Customize Board Dimensions & Target Connect Win Limit");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(new Color(148, 163, 184));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subLabel);
        card.add(Box.createVerticalStrut(25));

        // Form grid
        JPanel form = new JPanel(new GridLayout(3, 2, 15, 15));
        form.setOpaque(false);

        // Rows
        JLabel rowsLabel = new JLabel("Rows (4 - 20):");
        rowsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        rowsLabel.setForeground(Color.WHITE);
        rowsSpinner = new JSpinner(new SpinnerNumberModel(game.getRows(), 4, 20, 1));
        form.add(rowsLabel);
        form.add(rowsSpinner);

        // Columns
        JLabel colsLabel = new JLabel("Columns (4 - 20):");
        colsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        colsLabel.setForeground(Color.WHITE);
        colsSpinner = new JSpinner(new SpinnerNumberModel(game.getCols(), 4, 20, 1));
        form.add(colsLabel);
        form.add(colsSpinner);

        // Win limit
        int maxAllowed = Math.min(game.getRows(), game.getCols());
        JLabel winLabel = new JLabel("Connect to Win (3 - " + maxAllowed + "):");
        winLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        winLabel.setForeground(Color.WHITE);
        winModel = new SpinnerNumberModel(Math.min(game.getWinCount(), maxAllowed), 3, maxAllowed, 1);
        winCountSpinner = new JSpinner(winModel);
        form.add(winLabel);
        form.add(winCountSpinner);

        // Dynamic listener to update max win limit based on rows/cols
        Runnable updateWinMax = () -> {
            int currentR = (Integer) rowsSpinner.getValue();
            int currentC = (Integer) colsSpinner.getValue();
            int maxWin = Math.min(currentR, currentC);
            winLabel.setText("Connect to Win (3 - " + maxWin + "):");
            winModel.setMaximum(maxWin);
            if ((Integer) winModel.getValue() > maxWin) {
                winModel.setValue(maxWin);
            }
        };

        rowsSpinner.addChangeListener(e -> updateWinMax.run());
        colsSpinner.addChangeListener(e -> updateWinMax.run());

        card.add(form);
        card.add(Box.createVerticalStrut(25));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        JButton defaultBtn = new JButton("Reset Default (6x7, 4)");
        JButton saveBtn = new JButton("Save & Apply");
        JButton cancelBtn = new JButton("Back to Menu");

        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setBackground(new Color(34, 197, 94));
        saveBtn.setForeground(Color.WHITE);

        defaultBtn.addActionListener(e -> {
            rowsSpinner.setValue(ConnectXGame.DEFAULT_ROWS);
            colsSpinner.setValue(ConnectXGame.DEFAULT_COLS);
            updateWinMax.run();
            winCountSpinner.setValue(ConnectXGame.DEFAULT_WIN_COUNT);
        });

        saveBtn.addActionListener(e -> {
            int r = (Integer) rowsSpinner.getValue();
            int c = (Integer) colsSpinner.getValue();
            int w = (Integer) winCountSpinner.getValue();
            listener.onSaveSettings(r, c, w);
        });

        cancelBtn.addActionListener(e -> listener.onCancel());

        btnPanel.add(defaultBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        card.add(btnPanel);
        add(card);
    }

    public void syncWithGame(ConnectXGame game) {
        rowsSpinner.setValue(game.getRows());
        colsSpinner.setValue(game.getCols());
        int max = Math.min(game.getRows(), game.getCols());
        winModel.setMaximum(max);
        winCountSpinner.setValue(Math.min(game.getWinCount(), max));
    }
}
