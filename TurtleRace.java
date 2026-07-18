import java.util.Random;
import java.util.Scanner;

public class TurtleRace {

    private static final int TRACK_LENGTH = 50;
    private static final int NAME_WIDTH = 12;
    private static final int DELAY_MS = 200;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        String[] turtleNames = {
                "Speedy",
                "Shelly",
                "Turbo"
        };

        int[] positions = {
                0,
                0,
                0
        };

        int selectedTurtle = askForBet(scanner, turtleNames);

        System.out.println();
        System.out.println("You selected " + turtleNames[selectedTurtle] + ".");
        System.out.println("The race is about to start!");

        Thread.sleep(1500);

        boolean raceFinished = false;
        int round = 0;

        while (!raceFinished) {
            round++;

            for (int i = 0; i < positions.length; i++) {
                int movement = random.nextInt(6) + 1;
                positions[i] += movement;

                if (positions[i] > TRACK_LENGTH) {
                    positions[i] = TRACK_LENGTH;
                }
            }

            clearConsole();
            printRace(turtleNames, positions, round);

            for (int position : positions) {
                if (position >= TRACK_LENGTH) {
                    raceFinished = true;
                    break;
                }
            }

            Thread.sleep(DELAY_MS);
        }

        printResult(turtleNames, positions, selectedTurtle);

        scanner.close();
    }

    private static int askForBet(
            Scanner scanner,
            String[] turtleNames
    ) {
        System.out.println("=== TURTLE RACE BETTING ===");
        System.out.println();
        System.out.println("Choose the turtle you think will win:");
        System.out.println();

        for (int i = 0; i < turtleNames.length; i++) {
            System.out.println((i + 1) + ". " + turtleNames[i]);
        }

        System.out.println();

        while (true) {
            System.out.print("Enter turtle number: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();

                if (choice >= 1 && choice <= turtleNames.length) {
                    return choice - 1;
                }
            } else {
                scanner.next();
            }

            System.out.println(
                    "Invalid choice. Please enter a number between 1 and "
                            + turtleNames.length + "."
            );
        }
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

    private static void printResult(
            String[] turtleNames,
            int[] positions,
            int selectedTurtle
    ) {
        System.out.println();
        System.out.println("=== RACE FINISHED ===");

        int winnerCount = 0;

        for (int position : positions) {
            if (position >= TRACK_LENGTH) {
                winnerCount++;
            }
        }

        if (winnerCount > 1) {
            System.out.println("The race ended in a tie!");
        }

        System.out.println();
        System.out.println("Winner:");

        for (int i = 0; i < positions.length; i++) {
            if (positions[i] >= TRACK_LENGTH) {
                System.out.println("- " + turtleNames[i]);
            }
        }

        System.out.println();
        System.out.println(
                "Your bet: " + turtleNames[selectedTurtle]
        );

        if (positions[selectedTurtle] >= TRACK_LENGTH) {
            System.out.println("You won the bet!");
        } else {
            System.out.println("You lost the bet.");
        }
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}