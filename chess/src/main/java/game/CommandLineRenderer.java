package game;

/**
 * Ethan Petuchowski 7/26/15
 */
public abstract class CommandLineRenderer implements BoardRenderer {
    private final Board board;
    public CommandLineRenderer(Board board) {
        this.board = board;
    }

    @Override public void draw() {
        drawTopValues();
        drawBorder();
        drawBody();
        drawBorder();
    }

    abstract char[] getTopValues();
    abstract char[] getSideValues();

    private void drawTopValues() {
        char[] topValues = getTopValues();
        StringBuilder sb = new StringBuilder("   ");
        for (int i = 0; i < 8; i++)
            sb.append(" ").append(topValues[i]).append("   ");
        System.out.println(sb);
    }

    public void drawBorder() {
        // this is the one *without* synchronization
        StringBuilder sb = new StringBuilder("  +");
        for (int i = 0; i < 5*8-1; i++)
            sb.append('-');
        sb.append('+');
        System.out.println(sb);
    }

    public void drawBody() {
        char[] sideValues = getSideValues();
        // find the pieces
        String[][] stringBoard = new String[8][8];
        for (Piece piece : board.getLivePieces()) {
            BoardLoc loc = piece.getLoc();
            stringBoard[loc.row][loc.col] = piece.repr();
        }

        // print them out
        int i = 0;
        for (String[] row : stringBoard) {
            StringBuilder sb = new StringBuilder(sideValues[i++]+" |");
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

    public static class CoordinateCommandLineRenderer extends CommandLineRenderer {
        public CoordinateCommandLineRenderer(Board board) { super(board); }
        @Override char[] getTopValues() {
            return new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        }
        @Override char[] getSideValues() {
            return new char[] {'8', '7', '6', '5', '4', '3', '2', '1'};
        }
    }
    public static class IndexCommandLineRenderer extends CommandLineRenderer {
        public IndexCommandLineRenderer(Board board) { super(board); }
        @Override char[] getTopValues() {
            return new char[] {'0', '1', '2', '3', '4', '5', '6', '7'};
        }
        @Override char[] getSideValues() {
            return new char[] {'0', '1', '2', '3', '4', '5', '6', '7'};
        }
    }
}
