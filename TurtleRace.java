import java.util.Random;

public class TurtleRace {

    private static final int TRACK_LENGTH = 50;
    private static final int NAME_WIDTH = 12;
    private static final int DELAY_MS = 200;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        String[] turtleNames = {
                "Turtle 1",
                "Turtle 2",
                "Turtle 3"
        };

        int[] positions = {
                0,
                0,
                0
        };

        boolean raceFinished = false;
        int round = 0;

        while (!raceFinished) {
            round++;

            // Move every turtle between 1 and 6 spaces.
            for (int i = 0; i < positions.length; i++) {
                int movement = random.nextInt(6) + 1;
                positions[i] += movement;

                // Do not allow the turtle to move beyond the finish line.
                if (positions[i] > TRACK_LENGTH) {
                    positions[i] = TRACK_LENGTH;
                }
            }

            clearConsole();
            printRace(turtleNames, positions, round);

            // Check whether at least one turtle reached the finish.
            for (int position : positions) {
                if (position >= TRACK_LENGTH) {
                    raceFinished = true;
                    break;
                }
            }

            Thread.sleep(DELAY_MS);
        }

        printWinners(turtleNames, positions);
    }

    private static void printRace(
            String[] turtleNames,
            int[] positions,
            int round
    ) {
        System.out.println("=== TURTLE RACE ===");
        System.out.println("Round: " + round);
        System.out.println();

        printHeader();

        for (int i = 0; i < turtleNames.length; i++) {
            printTrack(turtleNames[i], positions[i]);
        }

        printBorder();
    }

    private static void printHeader() {
        System.out.printf(
                "%-" + NAME_WIDTH + "s  %-44s%s%n",
                "",
                "START",
                "FINISH"
        );

        printBorder();
    }

    private static void printBorder() {
        System.out.printf(
                "%-" + NAME_WIDTH + "s |%s|%n",
                "",
                "-".repeat(TRACK_LENGTH)
        );
    }

    private static void printTrack(String name, int position) {
        System.out.printf("%-" + NAME_WIDTH + "s |", name);

        for (int space = 1; space <= TRACK_LENGTH; space++) {
            if (space == position) {
                System.out.print("T");
            } else {
                System.out.print(".");
            }
        }

        System.out.println("|");
    }

    private static void printWinners(
            String[] turtleNames,
            int[] positions
    ) {
        System.out.println();
        System.out.println("=== RACE FINISHED ===");

        boolean multipleWinners = false;
        int winnerCount = 0;

        for (int position : positions) {
            if (position >= TRACK_LENGTH) {
                winnerCount++;
            }
        }

        multipleWinners = winnerCount > 1;

        if (multipleWinners) {
            System.out.println("It is a tie!");
        }

        for (int i = 0; i < positions.length; i++) {
            if (positions[i] >= TRACK_LENGTH) {
                System.out.println(turtleNames[i] + " wins!");
            }
        }
    }

    private static void clearConsole() {
        // ANSI escape codes for clearing the terminal.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}