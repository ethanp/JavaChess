package game;

import game.Board;
import game.BoardCommand;
import game.BoardLoc;
import game.ChessGame;
import game.Piece;
import game.Team;
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

    private ChessGame game;

    @Before public void setup() {
        game = new ChessGame();
    }

    /**
     * after moving, the board should be appropriately altered and the game should not yet be over
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
        BoardCommand scndWhite = new BoardCommand(BoardLoc.at(6, 6), BoardLoc.at(4, 6)); // move other pawn
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
     * If a piece can get in the way of a king in danger but the king has no available moves, the
     * game should not be over, and irrelevant moves should NOT be available.
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

    @Test public void enPassant() {
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

    @Test public void castleWhiteLeft() {
        ChessGame testSetup = ChessGame.emptyBoard();
        Set<Piece> pieces = new HashSet<>();
        Board board = testSetup.board;
        Piece king = new Piece.King(board, BoardLoc.parse("E1"), Team.WHITE);
        Piece rook = new Piece.Rook(board, BoardLoc.parse("A1"), Team.WHITE);
        pieces.addAll(Arrays.asList(king, rook));
        testSetup.forceResetPiecesTo(pieces);

        board.execute("E1 C1");
        assertTrue(board.hasPieceAt("C1"));
        assertTrue(board.hasPieceAt("D1"));
        assertFalse(board.hasPieceAt("E1"));
        assertFalse(board.hasPieceAt("A1"));

        board.undoMove();
        assertFalse(board.hasPieceAt("C1"));
        assertFalse(board.hasPieceAt("D1"));
        assertTrue(board.hasPieceAt("E1"));
        assertTrue(board.hasPieceAt("A1"));
    }

    @Test public void castleWhiteRight() {
        ChessGame testSetup = ChessGame.emptyBoard();
        Set<Piece> pieces = new HashSet<>();
        Board board = testSetup.board;
        Piece king = new Piece.King(board, BoardLoc.parse("E1"), Team.WHITE);
        Piece rook = new Piece.Rook(board, BoardLoc.parse("H1"), Team.WHITE);
        pieces.addAll(Arrays.asList(king, rook));
        testSetup.forceResetPiecesTo(pieces);

        board.execute("E1 G1");
        assertTrue(board.hasPieceAt("G1"));
        assertTrue(board.hasPieceAt("F1"));
        assertFalse(board.hasPieceAt("E1"));
        assertFalse(board.hasPieceAt("H1"));

        board.undoMove();
        assertFalse(board.hasPieceAt("G1"));
        assertFalse(board.hasPieceAt("F1"));
        assertTrue(board.hasPieceAt("E1"));
        assertTrue(board.hasPieceAt("H1"));
    }
}
