package player.strategies;

import game.ChessGame;
import game.Team;
import org.junit.Before;
import org.junit.Test;

/**
 * Ethan Petuchowski 6/26/16
 */
public class GreedyAITest {
    private ChessGame game;

    @Before public void setup() {
        game = new ChessGame();
    }

    /**
     * it is black's turn, and a good greedy AI will win right away
     */
    @Test public void nextMoveShouldMate() {
        String boardConfig =
            "    A    B    C    D    E    F    G    H   \n" +
            "  +---------------------------------------+\n" +
            "8 | Rb |    | Bb | Qb | Kb | Bb | Nb | Rb |\n" +
            "7 | Pb | Pb | Pb | Pb | Pb | Pb | Pb | Pb |\n" +
            "6 |    |    |    |    |    |    |    |    |\n" +
            "5 |    |    |    | Nw |    |    |    |    |\n" +
            "4 | Pw |    |    |    |    |    |    |    |\n" +
            "3 |    |    |    |    |    |    |    |    |\n" +
            "2 |    | Pw | Nb |    | Pw | Pw | Pw | Pw |\n" +
            "1 | Rw |    | Bw | Qw | Kw | Bw | Nw | Rw |\n" +
            "  +---------------------------------------+";

        ChessGame.fromPrintout(boardConfig, Team.BLACK);
    }

}
