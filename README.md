# TurtleRace

A fun GUI-based turtle racing game built with Java. Choose your favorite turtle, predict the winner, and watch them race to the finish line!

## Overview

TurtleRace is a Java Swing application that simulates an exciting race between three turtles: Speedy, Shelly, and Turbo. Players select which turtle they think will win before each race starts. The turtles move randomly each round, and the first to reach the finish line wins. Will you predict the winner correctly?

**Features:**
- Simple and intuitive graphical interface
- Three unique turtles with different colors
- Random movement mechanics for unpredictable races
- Hungarian and English-friendly UI (UI text is in Hungarian)
- Portable application design

## Prerequisites

- **Java 21** or higher
- A terminal or command prompt

## Compilation

To compile the TurtleRace application locally:

1. Ensure Java 21 is installed on your system:
   ```bash
   javac -version
   ```

2. Clone or download this repository to your local machine.

3. Navigate to the project directory:
   ```bash
   cd turtleRace
   ```

4. Compile all Java source files:
   ```bash
   javac *.java
   ```

   This will create `.class` files in the same directory.

## Running the Application

After compilation, run the application with:

```bash
java TurtleRace
```

A new window will open with the turtle race GUI. From there:

1. Select a turtle from the dropdown menu (Speedy, Shelly, or Turbo)
2. Click **"Verseny indítása"** (Start Race) to begin
3. Watch the turtles race and see if your prediction was correct
4. Click **"Új verseny indítása"** (New Race) to race again

## Continuous Integration

This project uses **GitHub Actions** for continuous integration. Every push and pull request triggers an automated build that:
- Sets up Java 21
- Compiles all Java source files

See the workflow configuration in [`.github/workflows/java-ci.yml`](.github/workflows/java-ci.yml).

## Project History

This is a redesign of an old university project, modernized and improved for better code quality and maintainability.

## License

This project is provided as-is. See the repository settings for any applicable license information.
