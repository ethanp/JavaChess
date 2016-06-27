package game;

import java.util.HashSet;
import java.util.Set;

/**
 * Ethan Petuchowski 7/26/15
 */
public class CommandLineRenderer implements BoardRenderer {
    private final Board board;

    public CommandLineRenderer(Board board) {
        this.board = board;
    }

    static Set<Piece> parseBoard(String rawBoardString, Board board) {
        Set<Piece> toRet = new HashSet<>();
        String[] lines = rawBoardString.split("\n");
        for (int i = 2; i < lines.length - 1; i++) {
            final int row = i - 2;
            final String startFromFirstPiece = lines[i].substring(4);
            final int BOARD_WIDTH = 8;
            final int PIECE_WIDTH = 5;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                final char ch = startFromFirstPiece.charAt(col*PIECE_WIDTH);
                if (ch != ' ') {
                    char teamChar = startFromFirstPiece.charAt(col*PIECE_WIDTH+1);
                    Team team = teamChar == 'b' ? Team.BLACK : Team.WHITE;
                    BoardLoc loc = BoardLoc.at(row, col);
                    if (ch == 'R') toRet.add(new Piece.Rook(board, loc, team));
                    else if (ch == 'P') toRet.add(new Piece.Pawn(board, loc, team));
                    else if (ch == 'B') toRet.add(new Piece.Bishop(board, loc, team));
                    else if (ch == 'N') toRet.add(new Piece.Knight(board, loc, team));
                    else if (ch == 'Q') toRet.add(new Piece.Queen(board, loc, team));
                    else if (ch == 'K') toRet.add(new Piece.King(board, loc, team));
                    else throw new RuntimeException("parse error, no piece has title: " + ch);
                }
            }
        }
        return toRet;
    }

    @Override public void draw() {
        drawTopValues();
        drawBorder();
        drawBody();
        drawBorder();
    }

    private void drawTopValues() {
        char[] topValues = getTopValues(); // e.g. the letters
        StringBuilder sb = new StringBuilder("   ");
        for (int i = 0; i < 8; i++)
            sb.append(" ").append(topValues[i]).append("   ");
        System.out.println(sb);
    }

    /** prints a +------------+ style border line for the board */
    public void drawBorder() {
        // this is the one *without* synchronization
        StringBuilder sb = new StringBuilder("  +");
        for (int i = 0; i < 5*8 - 1; i++)
            sb.append('-');
        sb.append('+');
        System.out.println(sb);
    }

    /** prints out the pieces and the row numbers */
    public void drawBody() {
        char[] sideValues = getSideValues(); // e.g. the numbers 8 to 1
        // find the pieces
        String[][] stringBoard = new String[8][8];
        for (Piece piece : board.getLivePieces()) {
            BoardLoc loc = piece.getLoc();
            stringBoard[loc.row][loc.col] = piece.repr();
        }

        // print them out
        int i = 0;
        for (String[] row : stringBoard) {
            StringBuilder sb = new StringBuilder(sideValues[i++] + " |");
            for (String repr : row) {
                if (repr == null)
                    sb.append("   ");
                else
                    sb.append(" ").append(repr);
                sb.append(" |");
            }
            System.out.println(sb);
        }
    }

    private char[] getTopValues() {
        return new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    }

    private char[] getSideValues() {
        return new char[]{'8', '7', '6', '5', '4', '3', '2', '1'};
    }
}
