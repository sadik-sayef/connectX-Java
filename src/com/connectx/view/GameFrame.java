package com.connectx.view;

import com.connectx.model.ConnectXGame;
import com.connectx.model.PlayerEntry;
import com.connectx.persistence.LeaderboardManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Main application window and controller coordinator (MVC Pattern).
 * Implements screen switching via CardLayout, top JMenuBar,
 * gameplay interaction loop, and outcome dialogs with Play Again / Menu options.
 */
public class GameFrame extends JFrame {
    public static final String CARD_MENU = "MENU";
    public static final String CARD_GAME = "GAME";
    public static final String CARD_SETTINGS = "SETTINGS";
    public static final String CARD_RULES = "RULES";
    public static final String CARD_LEADERBOARD = "LEADERBOARD";

    private final ConnectXGame game;
    private final LeaderboardManager leaderboardManager;

    private final CardLayout cardLayout;
    private final JPanel cardsPanel;

    // Screens
    private final MainMenuPanel menuPanel;
    private final JPanel gameContainerPanel;
    private final BoardPanel boardPanel;
    private final SettingsPanel settingsPanel;
    private final RulesPanel rulesPanel;
    private final LeaderboardPanel leaderboardPanel;

    // Game status controls
    private final JLabel turnLabel;
    private final JLabel matchInfoLabel;
    private final JLabel scoreSummaryLabel;

    public GameFrame() {
        super("Connect X - Tactical Strategy Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Initialize Model & Persistence
        this.game = new ConnectXGame();
        this.leaderboardManager = new LeaderboardManager();

        // Setup CardLayout Container
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Build Menu Bar
        setJMenuBar(createMenuBar());

        // 1. Menu Screen
        menuPanel = new MainMenuPanel(new MainMenuPanel.MenuActionListener() {
            @Override
            public void onStartGame() {
                promptLoginAndStartMatch();
            }

            @Override
            public void onRules() {
                rulesPanel.updateConfig(game);
                showScreen(CARD_RULES);
            }

            @Override
            public void onSettings() {
                settingsPanel.syncWithGame(game);
                showScreen(CARD_SETTINGS);
            }

            @Override
            public void onLeaderboard() {
                leaderboardPanel.refreshData();
                showScreen(CARD_LEADERBOARD);
            }

            @Override
            public void onExit() {
                System.exit(0);
            }
        });

        // 2. Game Screen with Header & Status Bar
        gameContainerPanel = new JPanel(new BorderLayout());
        gameContainerPanel.setBackground(new Color(15, 23, 42));

        // Top Status Header
        JPanel gameHeader = new JPanel(new BorderLayout());
        gameHeader.setBackground(new Color(30, 41, 59));
        gameHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);
        turnLabel = new JLabel("Player 1's Turn");
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnLabel.setForeground(new Color(239, 68, 68));
        leftHeader.add(turnLabel);

        matchInfoLabel = new JLabel("Goal: Connect 4");
        matchInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        matchInfoLabel.setForeground(new Color(148, 163, 184));
        leftHeader.add(matchInfoLabel);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        JButton restartMatchBtn = new JButton("Reset Match");
        JButton backToMenuBtn = new JButton("Main Menu");

        restartMatchBtn.addActionListener(e -> resetCurrentMatch());
        backToMenuBtn.addActionListener(e -> showScreen(CARD_MENU));

        rightHeader.add(restartMatchBtn);
        rightHeader.add(backToMenuBtn);

        gameHeader.add(leftHeader, BorderLayout.WEST);
        gameHeader.add(rightHeader, BorderLayout.EAST);
        gameContainerPanel.add(gameHeader, BorderLayout.NORTH);

        // Interactive Board
        boardPanel = new BoardPanel(game);
        boardPanel.setColumnClickListener(this::handleColumnClick);
        gameContainerPanel.add(boardPanel, BorderLayout.CENTER);

        // Bottom Footer
        JPanel gameFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        gameFooter.setBackground(new Color(30, 41, 59));
        scoreSummaryLabel = new JLabel("Points: Win = +10 pts | Loss = -10 pts");
        scoreSummaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        scoreSummaryLabel.setForeground(new Color(148, 163, 184));
        gameFooter.add(scoreSummaryLabel);
        gameContainerPanel.add(gameFooter, BorderLayout.SOUTH);

        // 3. Settings Screen
        settingsPanel = new SettingsPanel(game, new SettingsPanel.SettingsListener() {
            @Override
            public void onSaveSettings(int rows, int cols, int winCount) {
                try {
                    game.setDimensions(rows, cols, winCount);
                    JOptionPane.showMessageDialog(GameFrame.this,
                            "Settings updated: " + rows + "x" + cols + ", Connect " + winCount + " to win!",
                            "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
                    showScreen(CARD_MENU);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(GameFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onCancel() {
                showScreen(CARD_MENU);
            }
        });

        // 4. Rules Screen
        rulesPanel = new RulesPanel(game, () -> showScreen(CARD_MENU));

        // 5. Leaderboard Screen
        leaderboardPanel = new LeaderboardPanel(leaderboardManager, () -> showScreen(CARD_MENU));

        // Add all cards
        cardsPanel.add(menuPanel, CARD_MENU);
        cardsPanel.add(gameContainerPanel, CARD_GAME);
        cardsPanel.add(settingsPanel, CARD_SETTINGS);
        cardsPanel.add(rulesPanel, CARD_RULES);
        cardsPanel.add(leaderboardPanel, CARD_LEADERBOARD);

        add(cardsPanel);

        // Show main menu initially
        showScreen(CARD_MENU);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem itemStart = new JMenuItem("Start New Game");
        JMenuItem itemMenu = new JMenuItem("Main Menu");
        JMenuItem itemExit = new JMenuItem("Exit");

        itemStart.addActionListener(e -> promptLoginAndStartMatch());
        itemMenu.addActionListener(e -> showScreen(CARD_MENU));
        itemExit.addActionListener(e -> System.exit(0));

        gameMenu.add(itemStart);
        gameMenu.add(itemMenu);
        gameMenu.addSeparator();
        gameMenu.add(itemExit);

        JMenu viewMenu = new JMenu("View");
        JMenuItem itemSettings = new JMenuItem("Settings");
        JMenuItem itemRules = new JMenuItem("Rules & Controls");
        JMenuItem itemLeaderboard = new JMenuItem("Leaderboard");

        itemSettings.addActionListener(e -> {
            settingsPanel.syncWithGame(game);
            showScreen(CARD_SETTINGS);
        });
        itemRules.addActionListener(e -> {
            rulesPanel.updateConfig(game);
            showScreen(CARD_RULES);
        });
        itemLeaderboard.addActionListener(e -> {
            leaderboardPanel.refreshData();
            showScreen(CARD_LEADERBOARD);
        });

        viewMenu.add(itemSettings);
        viewMenu.add(itemRules);
        viewMenu.add(itemLeaderboard);

        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        return menuBar;
    }

    public void showScreen(String screenKey) {
        cardLayout.show(cardsPanel, screenKey);
    }

    /**
     * Prompts for player names, enforces validation, and starts game.
     */
    private void promptLoginAndStartMatch() {
        PlayerLoginDialog dialog = new PlayerLoginDialog(this, game.getPlayer1Name(), game.getPlayer2Name());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            game.setPlayerNames(dialog.getPlayer1Name(), dialog.getPlayer2Name());
            resetCurrentMatch();
            showScreen(CARD_GAME);
        }
    }

    private void resetCurrentMatch() {
        game.resetBoard();
        updateGameHeader();
        boardPanel.repaint();
    }

    private void handleColumnClick(int col) {
        if (game.isGameOver()) return;

        if (!game.isValidMove(col)) {
            JOptionPane.showMessageDialog(this, "Column " + (col + 1) + " is already full! Please choose another column.",
                    "Column Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        game.dropPiece(col);
        boardPanel.repaint();
        updateGameHeader();

        // Check outcome
        if (game.isGameOver()) {
            SwingUtilities.invokeLater(this::handleGameOverOutcome);
        }
    }

    private void updateGameHeader() {
        if (game.isGameOver()) {
            if (game.isDraw()) {
                turnLabel.setText("Match Drawn! Board is full.");
                turnLabel.setForeground(Color.YELLOW);
            } else {
                turnLabel.setText("Winner: " + game.getWinnerName() + "!");
                turnLabel.setForeground(new Color(251, 191, 36));
            }
        } else {
            String pName = game.getCurrentPlayerName();
            char pChar = game.getCurrentPlayer();
            turnLabel.setText(pName + "'s Turn (" + (pChar == ConnectXGame.PLAYER_RED ? "Red" : "Blue") + ")");
            turnLabel.setForeground(pChar == ConnectXGame.PLAYER_RED ? new Color(239, 68, 68) : new Color(59, 130, 246));
        }

        matchInfoLabel.setText("Connect " + game.getWinCount() + " to Win | Size: " + game.getRows() + "x" + game.getCols());

        // Display current player score preview from leaderboard
        List<PlayerEntry> entries = leaderboardManager.loadLeaderboard();
        int p1Score = getPlayerScore(entries, game.getPlayer1Name());
        int p2Score = getPlayerScore(entries, game.getPlayer2Name());
        scoreSummaryLabel.setText(String.format("Leaderboard: %s: %d pts | %s: %d pts",
                game.getPlayer1Name(), p1Score, game.getPlayer2Name(), p2Score));
    }

    private int getPlayerScore(List<PlayerEntry> entries, String name) {
        for (PlayerEntry p : entries) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getScore();
            }
        }
        return 0;
    }

    private void handleGameOverOutcome() {
        if (game.isDraw()) {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "The board is full! The match ended in a Draw.\nWould you like to play again with the same settings?",
                    "Game Draw",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Play Again", "Main Menu"},
                    "Play Again"
            );
            if (choice == JOptionPane.YES_OPTION) {
                resetCurrentMatch();
            } else {
                showScreen(CARD_MENU);
            }
        } else {
            String winner = game.getWinnerName();
            String loser = game.getLoserName();

            // Record to leaderboard (+10, -10)
            leaderboardManager.recordGameResult(winner, loser);

            updateGameHeader();

            String message = String.format(
                    "🎉 Congratulations, %s won the match!\n\nPoints Awarded:\n• %s: +10 points\n• %s: -10 points\n\nLeaderboard has been updated.",
                    winner, winner, loser
            );

            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Victory!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Play Again", "Main Menu"},
                    "Play Again"
            );

            if (choice == JOptionPane.YES_OPTION) {
                resetCurrentMatch();
            } else {
                showScreen(CARD_MENU);
            }
        }
    }
}
