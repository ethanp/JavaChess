package player.strategies;

import game.cmd.AbstractCommand;
import game.Board;
import game.cmd.BoardCommand;
import game.BoardLoc;
import game.ChessGame;
import game.Team;
import org.junit.Test;
import player.AIPlayer;
import player.HumanPlayer;
import player.Player;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Ethan Petuchowski 6/26/16
 */
public class GreedyTextbookAITest {

    /**
     * it is black's turn, and a textbook greedy AI will win right away
     *//*
     * This is the sequence of movies against the greedy AI that brought
     * me to this place: (though that was against the [now defunct] uniform AI)
     *
     * ==============
     * WHITE || BLACK
     * ==============
     * b1 c3 || b8 c6
     * d2 d4 || c6 d4
     * c3 d5 || d4 c2
     * a2 a4 || c2 a1
     *
     */
    @Test public void eatKingIfPossible() {
        String boardConfig = "" + // <-- for auto-formatter
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

        Board board = Board.fromPrintout(boardConfig);
        Player player1 = new HumanPlayer(Team.WHITE, ChessGame.STDIN_scanner);
        Player player2 = AIPlayer.newGreedyTextbookAI(Team.BLACK, board);
        ChessGame game = new ChessGame(board, player1, player2);
        AbstractCommand cmd = player2.move();
        assertThat(cmd, instanceOf(BoardCommand.class));
        BoardCommand bc = (BoardCommand) cmd;
        assertEquals(BoardLoc.parse("c2"), bc.from);
        assertEquals(BoardLoc.parse("e1"), bc.to);
    }
}
