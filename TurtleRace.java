import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TurtleRace {
    private static final int TRACK_LENGTH = 50
    private static final int DELAY_MS = 200;
    private static final String[] TURTLE_NAMES = {"Speedy", "Shelly", "Turbo"};
    private static final Color[] TURTLE_COLORS = {
            new Color(48, 151, 88), new Color(49, 123, 196), new Color(206, 122, 37)
    };

    private final Random random = new Random();
    private final int[] positions = new int[TURTLE_NAMES.length];
    private final JComboBox<String> turtleChoice = new JComboBox<>(TURTLE_NAMES);
    private final JButton startButton = new JButton("Verseny indítása");
    private final JLabel roundLabel = new JLabel("Kör: 0");
    private final JLabel resultLabel = new JLabel("Válassz teknőst, majd indítsd el a versenyt!");
    private final RacePanel racePanel = new RacePanel();
    private final Timer timer = new Timer(DELAY_MS, event -> advanceRound());
    private int selectedTurtle;
    private int round;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TurtleRace().showWindow());
    }

    private void showWindow() {
        JFrame frame = new JFrame("Teknősverseny");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        controls.add(new JLabel("Szerinted ki nyer?"));
        controls.add(turtleChoice);
        controls.add(startButton);
        startButton.addActionListener(event -> startRace());

        JPanel status = new JPanel(new GridLayout(2, 1, 0, 8));
        status.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));
        status.add(roundLabel);
        status.add(resultLabel);

        frame.add(controls, BorderLayout.NORTH);
        frame.add(racePanel, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                timer.stop();
            }
        });
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startRace() {
        if (timer.isRunning()) {
            return;
        }
        Arrays.fill(positions, 0);
        round = 0;
        selectedTurtle = turtleChoice.getSelectedIndex();
        roundLabel.setText("Kör: 0");
        resultLabel.setText("Tipped: " + TURTLE_NAMES[selectedTurtle] + ". A verseny mindjárt indul!");
        turtleChoice.setEnabled(false);
        startButton.setEnabled(false);
        racePanel.repaint();
        timer.setInitialDelay(1500);
        timer.start();
    }

    private void advanceRound() {
        round++;
        boolean finished = false;
        // Everyone moves before checking the result, so ties remain possible.
        for (int i = 0; i < positions.length; i++) {
            positions[i] = Math.min(TRACK_LENGTH, positions[i] + random.nextInt(6) + 1);
            finished |= positions[i] == TRACK_LENGTH;
        }
        roundLabel.setText("Kör: " + round);
        resultLabel.setText("Verseny folyamatban. Tipped: " + TURTLE_NAMES[selectedTurtle]);
        racePanel.repaint();
        if (finished) {
            timer.stop();
            showResult();
        }
    }

    private void showResult() {
        StringJoiner winners = new StringJoiner(", ");
        int winnerCount = 0;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == TRACK_LENGTH) {
                winners.add(TURTLE_NAMES[i]);
                winnerCount++;
            }
        }
        String outcome = positions[selectedTurtle] == TRACK_LENGTH
                ? "Nyertél!" : "Most nem nyertél.";
        resultLabel.setText((winnerCount > 1 ? "Döntetlen! Győztesek: " : "Győztes: ")
                + winners + ". Tipped: " + TURTLE_NAMES[selectedTurtle] + ". " + outcome);
        turtleChoice.setEnabled(true);
        startButton.setText("Új verseny indítása");
        startButton.setEnabled(true);
    }

    private class RacePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        RacePanel() {
            setPreferredSize(new Dimension(820, 330));
            setBackground(new Color(247, 250, 245));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int startX = 150;
                int finishX = getWidth() - 65;
                int top = 45;
                int laneHeight = (getHeight() - top - 15) / TURTLE_NAMES.length;
                g.setColor(Color.DARK_GRAY);
                g.drawString("RAJT", startX - 16, 25);
                g.drawString("CÉL", finishX - 10, 25);

                for (int i = 0; i < TURTLE_NAMES.length; i++) {
                    int laneTop = top + i * laneHeight;
                    int centerY = laneTop + laneHeight / 2;
                    g.setColor(i % 2 == 0 ? new Color(230, 240, 226) : new Color(240, 246, 236));
                    g.fillRoundRect(12, laneTop, getWidth() - 24, laneHeight - 4, 12, 12);
                    g.setColor(Color.DARK_GRAY);
                    g.drawString(TURTLE_NAMES[i], 26, centerY - 5);
                    g.drawString(positions[i] + " / " + TRACK_LENGTH, 26, centerY + 15);
                    g.setColor(new Color(180, 195, 177));
                    g.drawLine(startX, centerY, finishX, centerY);
                    g.drawLine(startX, laneTop + 5, startX, laneTop + laneHeight - 9);
                    for (int y = laneTop + 5, row = 0; y + 8 <= laneTop + laneHeight - 9; y += 8, row++) {
                        for (int column = 0; column < 2; column++) {
                            g.setColor((row + column) % 2 == 0 ? Color.DARK_GRAY : Color.WHITE);
                            g.fillRect(finishX + column * 8, y, 8, 8);
                        }
                    }
                    int x = startX + (int) ((double) positions[i] / TRACK_LENGTH * (finishX - startX));
                    drawTurtle(g, x, centerY, TURTLE_COLORS[i]);
                }
            } finally {
                g.dispose();
            }
        }

        private void drawTurtle(Graphics2D g, int x, int y, Color color) {
            g.setColor(color.darker());
            g.fillOval(x - 17, y - 19, 13, 13);
            g.fillOval(x + 4, y - 19, 13, 13);
            g.fillOval(x - 17, y + 6, 13, 13);
            g.fillOval(x + 4, y + 6, 13, 13);
            g.fillOval(x + 16, y - 8, 18, 16);
            g.setColor(color);
            g.fillOval(x - 23, y - 15, 46, 30);
            g.setColor(color.darker());
            g.setStroke(new BasicStroke(2));
            g.drawOval(x - 16, y - 10, 30, 20);
            g.drawLine(x - 1, y - 10, x - 1, y + 10);
            g.setColor(Color.WHITE);
            g.fillOval(x + 25, y - 5, 5, 5);
            g.setColor(Color.BLACK);
            g.fillOval(x + 27, y - 4, 2, 2);
        }
    }
}