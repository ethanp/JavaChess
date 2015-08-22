package chess.game;

/**
 * Ethan Petuchowski 8/15/15
 */
public class CoordinateCommandLineRenderer extends CommandLineRenderer {
    public CoordinateCommandLineRenderer(Board board) { super(board); }
    @Override char[] getTopValues() { return new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'}; }
    @Override char[] getSideValues() { return new char[] {'8', '7', '6', '5', '4', '3', '2', '1'}; }
}
