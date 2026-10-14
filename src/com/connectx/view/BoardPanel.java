package com.connectx.view;

import com.connectx.model.ConnectXGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

/**
 * Custom Swing component that renders the Connect X grid.
 * Handles mouse interactions (hover column indicator, column clicks),
 * and paints tokens, empty slots, and winning line highlights.
 */
public class BoardPanel extends JPanel {
    public interface ColumnClickListener {
        void onColumnClicked(int columnIndex);
    }

    private final ConnectXGame game;
    private ColumnClickListener listener;
    private int hoveredCol = -1;

    // Visual design constants
    private static final Color BOARD_BLUE = new Color(30, 58, 138); // Deep royal blue
    private static final Color BOARD_BORDER = new Color(15, 23, 42);
    private static final Color SLOT_EMPTY = new Color(15, 23, 42);   // Dark cutout
    private static final Color RED_PIECE = new Color(239, 68, 68);
    private static final Color BLUE_PIECE = new Color(59, 130, 246);
    private static final Color WIN_GOLD = new Color(251, 191, 36);

    public BoardPanel(ConnectXGame game) {
        this.game = game;
        setBackground(new Color(15, 23, 42)); // Match dark theme
        setOpaque(true);

        // Mouse motion for column hovering
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (game.isGameOver()) {
                    setHoveredCol(-1);
                    return;
                }
                int col = calculateColumnFromX(e.getX());
                setHoveredCol(col);
            }
        });

        // Mouse click for column drop
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (game.isGameOver() || listener == null) return;
                int col = calculateColumnFromX(e.getX());
                if (col >= 0 && col < game.getCols()) {
                    listener.onColumnClicked(col);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHoveredCol(-1);
            }
        });
    }

    public void setColumnClickListener(ColumnClickListener listener) {
        this.listener = listener;
    }

    public void setHoveredCol(int col) {
        if (this.hoveredCol != col) {
            this.hoveredCol = col;
            repaint();
        }
    }

    private int calculateColumnFromX(int mouseX) {
        int cols = game.getCols();
        int margin = 30;
        int topHeaderHeight = 50;
        int usableWidth = getWidth() - (margin * 2);
        if (usableWidth <= 0 || cols <= 0) return -1;

        int cellSize = Math.min(usableWidth / cols, (getHeight() - topHeaderHeight - margin * 2) / game.getRows());
        if (cellSize <= 0) cellSize = 20;

        int boardWidth = cellSize * cols;
        int startX = (getWidth() - boardWidth) / 2;

        if (mouseX < startX || mouseX >= startX + boardWidth) {
            return -1;
        }
        return (mouseX - startX) / cellSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = game.getRows();
        int cols = game.getCols();

        int margin = 30;
        int topHeaderHeight = 50;

        int availW = getWidth() - (margin * 2);
        int availH = getHeight() - topHeaderHeight - (margin * 2);

        int cellSize = Math.min(availW / cols, availH / rows);
        cellSize = Math.max(22, cellSize); // Minimum size

        int boardW = cellSize * cols;
        int boardH = cellSize * rows;

        int startX = (getWidth() - boardW) / 2;
        int startY = topHeaderHeight + (availH - boardH) / 2;

        // 1. Draw Hover Piece above the board
        if (!game.isGameOver() && hoveredCol >= 0 && hoveredCol < cols) {
            int hx = startX + hoveredCol * cellSize + cellSize / 8;
            int hy = startY - cellSize + cellSize / 8;
            int pieceSize = (int) (cellSize * 0.75);

            Color ghostColor = (game.getCurrentPlayer() == ConnectXGame.PLAYER_RED)
                    ? new Color(239, 68, 68, 180)
                    : new Color(59, 130, 246, 180);

            g2.setColor(ghostColor);
            g2.fillOval(hx, hy, pieceSize, pieceSize);

            // Draw downward arrow
            g2.setColor(Color.WHITE);
            int arrowX = startX + hoveredCol * cellSize + cellSize / 2;
            int arrowY = startY - 12;
            int[] xPoints = {arrowX - 8, arrowX + 8, arrowX};
            int[] yPoints = {arrowY - 10, arrowY - 10, arrowY};
            g2.fillPolygon(xPoints, yPoints, 3);
        }

        // 2. Draw Main Board Frame
        g2.setColor(BOARD_BLUE);
        int arc = 20;
        g2.fillRoundRect(startX - 10, startY - 10, boardW + 20, boardH + 20, arc, arc);

        g2.setColor(BOARD_BORDER);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(startX - 10, startY - 10, boardW + 20, boardH + 20, arc, arc);

        // Highlight column under hover
        if (!game.isGameOver() && hoveredCol >= 0 && hoveredCol < cols) {
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fillRect(startX + hoveredCol * cellSize, startY, cellSize, boardH);
        }

        // 3. Draw Slots and Tokens
        List<Point> winningCells = game.getWinningCells();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int cx = startX + c * cellSize + cellSize / 8;
                int cy = startY + r * cellSize + cellSize / 8;
                int diameter = (int) (cellSize * 0.75);

                char cell = game.getCell(r, c);

                if (cell == ConnectXGame.EMPTY) {
                    // Empty slot
                    g2.setColor(SLOT_EMPTY);
                    g2.fillOval(cx, cy, diameter, diameter);
                    g2.setColor(new Color(0, 0, 0, 70));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(cx, cy, diameter, diameter);
                } else {
                    // Piece color
                    Color pieceBase = (cell == ConnectXGame.PLAYER_RED) ? RED_PIECE : BLUE_PIECE;
                    g2.setColor(pieceBase);
                    g2.fillOval(cx, cy, diameter, diameter);

                    // 3D specular highlight
                    RadialGradientPaint rgp = new RadialGradientPaint(
                            cx + diameter * 0.35f,
                            cy + diameter * 0.35f,
                            diameter * 0.5f,
                            new float[]{0.0f, 1.0f},
                            new Color[]{new Color(255, 255, 255, 140), new Color(0, 0, 0, 80)}
                    );
                    g2.setPaint(rgp);
                    g2.fillOval(cx, cy, diameter, diameter);

                    // Check if this cell is part of winning sequence
                    boolean isWinningCell = false;
                    for (Point pt : winningCells) {
                        if (pt.x == c && pt.y == r) {
                            isWinningCell = true;
                            break;
                        }
                    }

                    if (isWinningCell) {
                        g2.setColor(WIN_GOLD);
                        g2.setStroke(new BasicStroke(4));
                        g2.drawOval(cx - 2, cy - 2, diameter + 4, diameter + 4);

                        // Star or inner dot indicator
                        g2.setColor(Color.WHITE);
                        int starSize = diameter / 4;
                        g2.fillOval(cx + (diameter - starSize) / 2, cy + (diameter - starSize) / 2, starSize, starSize);
                    }
                }
            }
        }

        // 4. Column numbers at bottom
        g2.setFont(new Font("Monospaced", Font.BOLD, Math.max(11, cellSize / 4)));
        for (int c = 0; c < cols; c++) {
            String colLabel = String.valueOf(c + 1);
            FontMetrics fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(colLabel);
            int lx = startX + c * cellSize + (cellSize - labelW) / 2;
            int ly = startY + boardH + 25;

            if (c == hoveredCol && !game.isGameOver()) {
                g2.setColor(Color.YELLOW);
            } else {
                g2.setColor(new Color(148, 163, 184));
            }
            g2.drawString(colLabel, lx, ly);
        }

        g2.dispose();
    }
}
