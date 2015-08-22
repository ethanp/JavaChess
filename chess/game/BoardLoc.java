package chess.game;

/**
 * Ethan Petuchowski 7/7/15
 */
public class BoardLoc {
    public final int row;
    public final int col;

    private BoardLoc(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public  static boolean  onBoard(int row, int col)   { return row >= 0 && col >= 0 && row < 8 && col < 8; }
    public  static BoardLoc at(int row, int col)        { return new BoardLoc(row, col); }
    private static int      ctoi(char c)                { return c - '0'; }

    /**
     * @return the same BoardLoc but on the other team's territory
     */
    public BoardLoc mirror()       { return BoardLoc.at(7-row, col); }

    /**
     * @return the same BoardLoc but flipped right-to-left
     */
    public BoardLoc opposite()     { return BoardLoc.at(row, 7-col); }

    /**
     * @return the BoardLoc that would look the same as this one
     *         but after you spin the board 180˚
     */
    public BoardLoc reverse()      { return BoardLoc.at(7-row, 7-col); }

    public BoardLoc left()         { return left(1); }
    public BoardLoc left(int n)    { return BoardLoc.at(row, col-n); }
    public BoardLoc right()        { return right(1); }
    public BoardLoc right(int n)   { return BoardLoc.at(row, col+n); }
    public Team     getTerritory() { return row < 4 ? Team.BLACK : Team.WHITE; }

    /**
     * @return true iff this location is within the bounds of the board's size
     */
    public boolean  onBoard()      { return onBoard(row, col); }

    /**
     * @return e.g. "BoardLoc: row 3, col 5"
     */
    @Override public String toString() { return "BoardLoc: row "+row+", col "+col; }

    public static BoardLoc[] corners(int row, int col) {
        BoardLoc loc = BoardLoc.at(row, col);
        return new BoardLoc[]{
            loc,
            loc.mirror(),
            loc.opposite(),
            loc.reverse()
        };
    }

    /**
     * Turns an input string of either the standard A-H coordinate system,
     * or my original format "ab", where
     *
     *     `a` -- row ID
     *     `b` -- column ID
     *
     * into its associated BoardLoc object.
     * e.g. "34" would result in `BoardLoc: row 3, col 4`
     */
    public static BoardLoc parse(String s) {
        char[] arr = s.toCharArray();
        int row = ctoi(arr[0]);
        int col = ctoi(arr[1]);

        /* input is in original index format */
        if (row >= 0 && row < 8) {
            return new BoardLoc(row, col);
        }

        /* input is in algebraic format */
        // the column comes FIRST, and is A -> H
        col = standardFormCol(arr[0]);
        // the row comes second, and is 1-8 from BOTTOM -> UP
        row = 8 -ctoi(arr[1]);

        return new BoardLoc(row, col);
    }

    private static int standardFormCol(char c) {
        if (c >= 'A' && c <= 'H') return c-'A';
        if (c >= 'a' && c <= 'h') return c-'a';
        throw new IllegalArgumentException("couldn't parse column: "+c);
    }

    /**
     * @return true iff `o` represents the same row and column as `this`
     */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardLoc)) return false;
        BoardLoc loc = (BoardLoc) o;
        return row == loc.row && col == loc.col;
    }

    @Override public int hashCode() {
        int result = row;
        result = 31*result+col;
        return result;
    }
}
