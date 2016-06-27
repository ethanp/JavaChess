package game;

import org.junit.Test;
import ui.CommandLineRenderer;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Ethan Petuchowski 6/27/16
 */
public class CommandLineRendererTest {
    @Test public void parseBoardExample() {
        String boardConfig =
            "    A    B    C    D    E    F    G    H   \n" +
            "  +---------------------------------------+\n" +
            "8 | Rb |    | Bb | Qb | Kb |    | Nb | Rb |\n" +
            "7 |    |    |    |    |    |    |    |    |\n" +
            "6 |    |    |    |    |    |    |    |    |\n" +
            "5 |    |    |    |    |    |    |    |    |\n" +
            "4 | Pw |    |    |    |    |    |    |    |\n" +
            "3 |    |    |    |    |    |    |    |    |\n" +
            "2 |    |    | Nb |    |    |    |    | Pw |\n" +
            "1 |    |    |    |    |    |    |    | Rw |\n" +
            "  +---------------------------------------+";
        Board board = new Board();
        Set<Piece> parsedPieceSet = CommandLineRenderer.parseBoard(boardConfig, board);
        Board idealBoard = new Board();
        Set<Piece> idealPieces = new HashSet<>();
        idealPieces.add(new Piece.Rook(idealBoard, BoardLoc.at(0, 0), Team.BLACK));
        idealPieces.add(new Piece.Pawn(idealBoard, BoardLoc.at(4, 0), Team.WHITE));
        idealPieces.add(new Piece.Bishop(idealBoard, BoardLoc.at(0, 2), Team.BLACK));
        idealPieces.add(new Piece.Knight(idealBoard, BoardLoc.at(6, 2), Team.BLACK));
        idealPieces.add(new Piece.Queen(idealBoard, BoardLoc.at(0, 3), Team.BLACK));
        idealPieces.add(new Piece.King(idealBoard, BoardLoc.at(0, 4), Team.BLACK));
        idealPieces.add(new Piece.Knight(idealBoard, BoardLoc.at(0, 6), Team.BLACK));
        idealPieces.add(new Piece.Rook(idealBoard, BoardLoc.at(0, 7), Team.BLACK));
        idealPieces.add(new Piece.Pawn(idealBoard, BoardLoc.at(6, 7), Team.WHITE));
        idealPieces.add(new Piece.Rook(idealBoard, BoardLoc.at(7, 7), Team.WHITE));
        for (Piece parsedPiece : parsedPieceSet) {
            assertTrue(idealPieces.contains(parsedPiece));
        }
    }
}
