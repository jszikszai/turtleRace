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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TurtleRace {
    private static final int TRACK_LENGTH = 50;
    private static final int DELAY_MS = 200;
    private static final String[] TURTLE_NAMES = {"Speedy", "Shelly", "Turbo"};
    private static final Color[] TURTLE_COLORS = {
            new Color(48, 151, 88), new Color(49, 123, 196), new Color(206, 122, 37)
    };

    private enum Language { HUNGARIAN, ENGLISH }
    private enum RaceState { READY, STARTING, RUNNING, FINISHED }

    private Language language = Language.HUNGARIAN;
    private RaceState state = RaceState.READY;
    private JFrame frame;
    private final Random random = new Random();
    private final int[] positions = new int[TURTLE_NAMES.length];
    private final JComboBox<String> turtleChoice = new JComboBox<>(TURTLE_NAMES);
    private final JButton startButton = new JButton();
    private final JLabel choiceLabel = new JLabel();
    private final JLabel roundLabel = new JLabel();
    private final JLabel resultLabel = new JLabel();
    private final RacePanel racePanel = new RacePanel();
    private final Timer timer = new Timer(DELAY_MS, event -> advanceRound());
    private int selectedTurtle;
    private int round;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TurtleRace().showWindow());
    }

    private String text(String hungarian, String english) {
        return language == Language.HUNGARIAN ? hungarian : english;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu languageMenu = new JMenu("Nyelv / Language");
        ButtonGroup group = new ButtonGroup();
        for (Language option : Language.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                    option == Language.HUNGARIAN ? "Magyar" : "English",
                    option == language);
            item.addActionListener(event -> {
                language = option;
                refreshLabels();
            });
            group.add(item);
            languageMenu.add(item);
        }
        menuBar.add(languageMenu);
        return menuBar;
    }

    private void showWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        controls.add(choiceLabel);
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
        refreshLabels();
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Derive every translated label from the current state without changing the race.
    private void refreshLabels() {
        if (frame != null) {
            frame.setTitle(text("Tekn\u0151sverseny", "Turtle Race"));
        }
        choiceLabel.setText(text("Szerinted ki nyer?", "Who do you think will win?"));
        roundLabel.setText(text("K\u00f6r: ", "Round: ") + round);
        startButton.setText(state == RaceState.FINISHED
                ? text("\u00daj verseny ind\u00edt\u00e1sa", "Start another race")
                : text("Verseny ind\u00edt\u00e1sa", "Start race"));
        String bet = text("Tipped: ", "Your pick: ") + TURTLE_NAMES[selectedTurtle];
        switch (state) {
            case READY:
                resultLabel.setText(text("V\u00e1lassz tekn\u0151st, majd ind\u00edtsd el a versenyt!",
                        "Choose a turtle, then start the race!"));
                break;
            case STARTING:
                resultLabel.setText(bet + text(". A verseny mindj\u00e1rt indul!",
                        ". The race is about to start!"));
                break;
            case RUNNING:
                resultLabel.setText(text("Verseny folyamatban. ", "Race in progress. ") + bet);
                break;
            case FINISHED:
                StringJoiner winners = new StringJoiner(", ");
                int winnerCount = 0;
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == TRACK_LENGTH) {
                        winners.add(TURTLE_NAMES[i]);
                        winnerCount++;
                    }
                }
                String outcome = positions[selectedTurtle] == TRACK_LENGTH
                        ? text("Nyert\u00e9l!", "You won!")
                        : text("Most nem nyert\u00e9l.", "You lost this time.");
                resultLabel.setText((winnerCount > 1
                        ? text("D\u00f6ntetlen! Gy\u0151ztesek: ", "Tie! Winners: ")
                        : text("Gy\u0151ztes: ", "Winner: "))
                        + winners + ". " + bet + ". " + outcome);
                break;
            default:
                throw new IllegalStateException("Unknown race state: " + state);
        }
        racePanel.repaint();
    }

    private void startRace() {
        if (timer.isRunning()) {
            return;
        }
        Arrays.fill(positions, 0);
        round = 0;
        selectedTurtle = turtleChoice.getSelectedIndex();
        state = RaceState.STARTING;
        turtleChoice.setEnabled(false);
        startButton.setEnabled(false);
        refreshLabels();
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
        state = finished ? RaceState.FINISHED : RaceState.RUNNING;
        if (finished) {
            timer.stop();
            turtleChoice.setEnabled(true);
            startButton.setEnabled(true);
        }
        refreshLabels();
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
                g.drawString(text("RAJT", "START"), startX - 16, 25);
                g.drawString(text("C\u00c9L", "FINISH"), finishX - 10, 25);

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
