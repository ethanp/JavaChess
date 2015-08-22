package main.java.of_2015.chess.game;

/**
 * Ethan Petuchowski 8/15/15
 */
public class IndexCommandLineRenderer extends CommandLineRenderer {
    public IndexCommandLineRenderer(Board board) { super(board); }
    @Override char[] getTopValues() { return new char[] {'0', '1', '2', '3', '4', '5', '6', '7'}; }
    @Override char[] getSideValues() { return new char[] {'0', '1', '2', '3', '4', '5', '6', '7'}; }
}
