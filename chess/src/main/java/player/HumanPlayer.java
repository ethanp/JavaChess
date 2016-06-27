package player;

import game.AbstractCommand;
import game.Team;

import java.util.Scanner;

/**
 * Ethan Petuchowski 8/24/15
 */
public class HumanPlayer implements Player {

    final private Scanner scanner;
    private final Team team;

    public HumanPlayer(Team team, Scanner scanner) {
        this.team = team;
        this.scanner = scanner;
    }

    /**
     * in which the player makes his move
     */
    @Override public AbstractCommand move() {
        return promptForInput();
    }

    @Override public Team getTeam() {
        return team;
    }

    private AbstractCommand promptForInput() {
        System.out.println("your move::> ");
        String console = scanner.nextLine();
        if (console.equals("exit")) {
            System.out.println("Quitting.");
            System.exit(0);
        }
        System.out.println("\n");
        return AbstractCommand.parse(console);
    }
}
