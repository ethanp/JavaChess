package chess.game;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Ethan Petuchowski 7/22/15
 */
public class ChessGameTest {

    ChessGame game;

    @Before public void setup() {
        game = new ChessGame();
    }

    /**
     * after moving, the board should be appropriately altered
     * and the game should not yet be over
     */
    @Test public void makeFirstMove() {
        BoardCommand firstMove = new BoardCommand(BoardLoc.at(6, 5), BoardLoc.at(5, 5)); // move pawn out

        assertFalse(game.won() || game.lost());
        game.board.execute(firstMove);
        assertFalse(game.won() || game.lost());

        assertTrue(game.board.hasPieceAt(5, 5));
    }

    /**
     * classic two-move check-mate actually results in a LOSS for WHITE
     */
    @Test public void foolsMate() {
        BoardCommand firstWhite = new BoardCommand(BoardLoc.at(6, 5), BoardLoc.at(5, 5)); // move pawn out
        BoardCommand firstBlack = new BoardCommand(BoardLoc.at(1, 4), BoardLoc.at(3, 4)); // move pawn out
        BoardCommand scndWhite  = new BoardCommand(BoardLoc.at(6, 6), BoardLoc.at(4, 6)); // move other pawn
        BoardCommand scndBlack = new BoardCommand(BoardLoc.at(0, 3), BoardLoc.at(4, 7)); // move queen out

        assertFalse(game.won() || game.lost());

        game.board.execute(firstWhite);
        assertFalse(game.won() || game.lost());

        game.board.execute(firstBlack);
        assertFalse(game.won() || game.lost());

        game.board.execute(scndWhite);
        assertFalse(game.won() || game.lost());

        game.board.execute(scndBlack);
        game.board.draw();
        assertFalse(game.won());
        assertTrue(game.lost());
    }

    /**
     * If a piece can get in the way of a king in danger but the king has no available moves,
     * the game should not be over, and irrelevant moves should NOT be available.
     */
    @Test public void saviour() {
        BoardCommand queenPawn = new BoardCommand(BoardLoc.at(6, 3), BoardLoc.at(5, 3));
        BoardCommand leftBishopPawn = new BoardCommand(BoardLoc.at(1, 2), BoardLoc.at(2, 2));
        BoardCommand throwAwayMove = new BoardCommand(BoardLoc.at(6, 7), BoardLoc.at(4, 7));
        BoardCommand queenSaysCheck = new BoardCommand(BoardLoc.at(0, 3), BoardLoc.at(3, 0));

        assertFalse(game.won() || game.lost());

        game.board.execute(queenPawn);
        assertFalse(game.won() || game.lost());

        game.board.execute(leftBishopPawn);
        assertFalse(game.won() || game.lost());

        game.board.execute(throwAwayMove);
        assertFalse(game.won() || game.lost());

        game.board.execute(queenSaysCheck);
        game.board.draw();

        assertFalse(game.won());

        // WHITE can either move leftBishopPawn or Queen to block
        // BLACK queen's attack
        assertFalse(game.lost());
    }

    @Test public void testEnPassant() {
        ChessGame testSetup = ChessGame.emptyBoard();
        Set<Piece> pieces = new HashSet<>();
        Board board = testSetup.board;
        Piece pawn1 = new Piece.Pawn(board, BoardLoc.parse("B4"), Team.BLACK);
        Piece pawn2 = new Piece.Pawn(board, BoardLoc.parse("A2"), Team.WHITE);
        pieces.addAll(Arrays.asList(pawn1, pawn2));
        testSetup.forceResetPiecesTo(pieces);
        board.execute("A2 A4");
        // this should NOT throw an IllegalStateException for not being a "possibleMove"
        board.execute("B4 A3");
        assertTrue(board.hasPieceAt("A3"));
        // piece should have actually been killed and therefore no longer there
        assertFalse(board.hasPieceAt("A4"));
    }
    /* TODO : I JUST HIT THE FOLLOWING BUG

        A    B    C    D    E    F    G    H
      +---------------------------------------+
    8 | Rw |    | Bb | Qb |    | Bb |    | Rb |
    7 |    | Pb | Pb | Pb | Pb | Kb |    | Pb |
    6 |    |    |    |    |    |    |    |    |
    5 |    |    |    |    | Pw | Pb |    |    |
    4 |    | Pw |    | Nw |    |    | Pb |    |
    3 |    |    |    |    |    |    | Pw |    |
    2 |    |    | Pw |    |    | Pw |    | Pw |
    1 |    |    | Bw | Qw | Kw | Bw |    | Rw |
      +---------------------------------------+
    Make your move:
    H2 H3


        A    B    C    D    E    F    G    H
      +---------------------------------------+
    8 | Rw |    | Bb | Qb |    | Bb |    | Rb |
    7 |    | Pb | Pb | Pb | Pb | Kb |    | Pb |
    6 |    |    |    |    |    |    |    |    |
    5 |    |    |    |    | Pw | Pb |    |    |
    4 |    | Pw |    | Nw |    |    | Pb |    |
    3 |    |    |    |    |    |    | Pw | Pw |
    2 |    |    | Pw |    |    | Pw |    |    |
    1 |    |    | Bw | Qw | Kw | Bw |    | Rw |
      +---------------------------------------+
    Exception in thread "main" java.util.NoSuchElementException: No value present
        at java.util.Optional.get(Optional.java:135)
        at main.java.of_2015.chess.game.Board.lastPieceMoved(Board.java:157)
        at main.java.of_2015.chess.game.Piece$Pawn.canEnPassant(Piece.java:218)
        at main.java.of_2015.chess.game.Piece$Pawn.possibleMoves(Piece.java:208)
        at main.java.of_2015.chess.game.Piece.moveInvalid(Piece.java:46)
        at main.java.of_2015.chess.game.Piece.move(Piece.java:67)
        at main.java.of_2015.chess.game.Pieces.moveFromTo(Pieces.java:89)
        at main.java.of_2015.chess.game.Board.execute(Board.java:67)
        at main.java.of_2015.chess.ai.Opponent.move(Opponent.java:39)
        at main.java.of_2015.chess.game.ChessGame.startInterpreter(ChessGame.java:66)
        at main.java.of_2015.chess.game.ChessGame.consoleInterpreted(ChessGame.java:54)
        at main.java.of_2015.chess.game.ChessGame.main(ChessGame.java:21)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:483)
        at com.intellij.rt.execution.application.AppMain.main(AppMain.java:140)
    */
}
