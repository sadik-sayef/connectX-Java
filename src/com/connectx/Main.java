package com.connectx;

import com.connectx.view.GameFrame;

import javax.swing.*;

/**
 * Entry point for the Connect X Java Swing application.
 * Ensures strict thread-safety by launching on the Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Run GUI construction on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply modern System Look and Feel or Nimbus for polished aesthetics
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                    // Fall back to default
                }
            }

            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
