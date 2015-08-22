package game;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ethan Petuchowski 7/7/15
 */
public abstract class Piece {

    /**
     * must be implemented by each type of chess piece
     *
     * @return set of legal locations for this piece to move to on this turn,
     *         given which team its own and where other pieces are on the board
     */
    public abstract Set<BoardLoc> possibleMoves();

    private Piece(Board board, BoardLoc loc, Team team, char symbol) {
        this.loc = loc;
        this.team = team;
        this.board = board;
        this.alive = true;
        this.symbol = symbol;
    }

    public Piece(Board board, BoardLoc loc, char symbol) {
        this(board, loc, loc.getTerritory(), symbol);
    }

    /** FIELDS */
    final           Board     board;
    private         BoardLoc  loc;
    public final    Team      team;
    private         boolean   alive;
    private final   char      symbol;
    protected       boolean   hasMoved; // both king and rook NEED it, plus good for debugging

    /** SHORT UTILITIES */
    public  String   repr()                 { return symbol+team.getRepr(); }
    public  BoardLoc getLoc()               { return loc; }
    public  boolean  isAlive()              { return alive; }
    public  void     kill()                 { alive = false; }
    public  boolean  moveInvalid(BoardLoc to) { return !to.onBoard() || !possibleMoves().contains(to); }
    private void     setLoc(BoardLoc loc)   { this.loc = loc; }
    public  boolean  hasMoves()             { return !possibleMoves().isEmpty(); }

    /**
     * For a King, this may also be called by the King's special `isThreatened`
     * method, which is called before every turn (all the way up at
     * `startInterpreter`) to see if the game is over.
     */
    public  void     setHasMoved()          { hasMoved = true; }

    boolean addLogic(int row, int col, Set<BoardLoc> locSet) {
        return addLogic(BoardLoc.at(row, col), locSet);
    }

    /**
     * Places this piece on the board at the given location, replacing the current
     * location the piece is set to be at. If the piece was dead, it will be
     * restored to full health by moving it onto the board.
     */
    public void move(BoardLoc to) {
        if (moveInvalid(to)) {
            System.err.println("invalid move from: "+getLoc()+" to: "+to);
        }
        alive = true;
        setHasMoved();
        setLoc(to);
    }

    /**
     * used while undoing a move
     * @param to the location the moved piece will end up
     */
    public void forceMove(BoardLoc to) {
        if (!to.onBoard()) { // don't check if this move is valid for this piece
            throw new IllegalStateException(to+ " is not on the board.");
        }
        alive = true;
        // TODO how am I going to UNSET `hasMoved`?
        setLoc(to);
    }

    /** Utilities for use in building the set of `possibleMoves()` */

    BoardLoc forward(int spaces) {
        return team == Team.BLACK
             ? BoardLoc.at(loc.row+spaces, loc.col)
             : BoardLoc.at(loc.row-spaces, loc.col);
    }

    /* used by queen and rook */
    @SuppressWarnings("StatementWithEmptyBody")
    Set<BoardLoc> straightMoves() {
        Set<BoardLoc> locSet = new HashSet<>();
        for (int r = getLoc().row-1; r >= 0 && addLogic(r, getLoc().col, locSet); r--);
        for (int r = getLoc().row+1; r < 8  && addLogic(r, getLoc().col, locSet); r++);
        for (int c = getLoc().col-1; c >= 0 && addLogic(getLoc().row, c, locSet); c--);
        for (int c = getLoc().col+1; c < 8  && addLogic(getLoc().row, c, locSet); c++);
        return locSet;
    }

    /* used by queen and bishop */
    Set<BoardLoc> diagonalMoves() {
        Set<BoardLoc> locSet = new HashSet<>();
        int r, c;

        r = getLoc().row-1;
        c = getLoc().col-1;
        while (r >= 0 && c >= 0 && addLogic(r, c, locSet)) { r--; c--; }

        r = getLoc().row+1;
        c = getLoc().col+1;
        while (r < 8 && c < 8 && addLogic(r, c, locSet)) { r++; c++; }

        r = getLoc().row+1;
        c = getLoc().col-1;
        while (r < 8 && c >= 0 && addLogic(r, c, locSet)) { r++; c--; }

        r = getLoc().row-1;
        c = getLoc().col+1;
        while (r >= 0 && c < 8  && addLogic(r, c, locSet)) { r--; c++; }

        return locSet;
    }

    /**
     * adds the location if it is either empty or the opponent's piece is occupying it
     * @return true iff the slot was empty
     *
     * the point is that for a piece who can move up to N tiles in some direction,
     * will continue iterating through the tiles until addLogic == true. This way
     * it is simple to use in a for-loop, e.g. see the `straightMoves` function above.
     */
    boolean addLogic(BoardLoc loc, Set<BoardLoc> locSet) {
        if (!loc.onBoard()) return true;
        if (loc.equals(getLoc())) return true; // crucially, don't add it to the set
        Optional<Piece> at = board.getPieceAt(loc);
        if (at.isPresent()) {
            if (!at.get().team.equals(team)) {
                locSet.add(loc);
            }
            return false;
        }
        locSet.add(loc);
        return true;
    }

    boolean hasEnemyPiece(BoardLoc loc) {
        Optional<Piece> at = board.getPieceAt(loc);
        return at.isPresent() && !at.get().team.equals(team);
    }

    public static class Rook extends Piece {
        public Rook(Board board, BoardLoc loc) {
            super(board, loc, 'R');
        }

        @Override public Set<BoardLoc> possibleMoves() {
            return straightMoves();
        }
    }

    public static class Pawn extends Piece {
        public Pawn(Board board, BoardLoc loc) {
            super(board, loc, 'P');
        }

        public Pawn(Board board, BoardLoc loc, Team team) {
            super(board, loc, team, 'P');
        }

        boolean inHomeRow() {
            return team == Team.BLACK
                 ? getLoc().row == 1
                 : getLoc().row == 6;
        }

        @Override public Set<BoardLoc> possibleMoves() {
            Set<BoardLoc> locSet = new HashSet<>();

            // the normal forward move(s)
            final BoardLoc f1 = forward(1);
            final BoardLoc f2 = forward(2);
            if (!hasEnemyPiece(f1)) {
                if (addLogic(f1, locSet) && inHomeRow() && !hasEnemyPiece(f2)) {
                    addLogic(f2, locSet);
                }
            }

            // the normal capture scenarios
            BoardLoc upLeft = f1.left();
            if (hasEnemyPiece(upLeft))
                addLogic(upLeft, locSet);
            BoardLoc upRight = f1.right();
            if (hasEnemyPiece(upRight))
                addLogic(upRight, locSet);

            // "EN PASSANT" (in passing) capture
            // https://www.wikiwand.com/en/En_passant
            // when their pawn moves out two paces, but your pawn WOULD have been
            // able to capture it had it only moved one, you can pretend like it
            // only went one square and capture it where it WOULD have been.
            if (canEnPassant(upLeft)) addLogic(upLeft, locSet);
            if (canEnPassant(upRight)) addLogic(upRight, locSet);

            return locSet;
        }

        boolean canEnPassant(BoardLoc loc) {
            return (team != Team.WHITE || loc.row == 2)
                && (team != Team.BLACK || loc.row == 5)
                && board.lastMove() != null
                && board.lastPieceMoved() != null // this happened while I was playing, not sure how
                && board.lastPieceMoved() instanceof Pawn
                && board.lastMove().command.distance() == 2
                && board.lastMove().command.to.col == loc.col;
        }
    }

    public static class Knight extends Piece {
        public Knight(Board board, BoardLoc loc) {
            super(board, loc, 'N');
        }

        @Override public Set<BoardLoc> possibleMoves() {
            Set<BoardLoc> locSet = new HashSet<>();
            int[] rows = { getLoc().row-2, getLoc().row+2 };
            int[] rowV = { getLoc().col-1, getLoc().col+1 };
            int[] cols = { getLoc().col-2, getLoc().col+2 };
            int[] colV = { getLoc().row-1, getLoc().row+1 };
            for (int r : rows) for (int v : rowV) addLogic(r, v, locSet);
            for (int c : cols) for (int v : colV) addLogic(v, c, locSet);
            return locSet;
        }
    }

    public static class Bishop extends Piece {
        public Bishop(Board board, BoardLoc loc) {
            super(board, loc, 'B');
        }

        @Override public Set<BoardLoc> possibleMoves() {
            return diagonalMoves();
        }
    }

    public static class King extends Piece {
        public King(Board board, BoardLoc loc) {
            super(board, loc, 'K');
        }

        @Override public Set<BoardLoc> possibleMoves() {
            Set<BoardLoc> locSet = new HashSet<>();
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++)
                    addLogic(getLoc().row+i, getLoc().col+j, locSet);

            locSet = locSet.stream()
                .filter(loc -> !board.isThreatened(team, loc))
                .collect(Collectors.toSet());

            /* add castling possibilities iff they are available */
            if (!hasMoved) {
                boolean leftSideEmpty = true;
                for (int i = 1; i <= 3; i++) {
                    if (board.hasPieceAt(getLoc().left(i))) {
                        leftSideEmpty = false;
                        break;
                    }
                }
                Optional<Piece> leftRook = board.getPieceAt(getLoc().left(4));
                if (castleValid(leftSideEmpty, leftRook)) {
                    addLogic(getLoc().left(2), locSet);
                }

                boolean rightSideEmpty = true;
                for (int i = 1; i <= 2; i++) {
                    if (board.hasPieceAt(getLoc().right(i))) {
                        rightSideEmpty = false;
                        break;
                    }
                }
                Optional<Piece> rightRook = board.getPieceAt(getLoc().right(3));
                if (castleValid(rightSideEmpty, rightRook)) {
                    addLogic(getLoc().right(2), locSet);
                }
            }

            return locSet;
        }

        private static boolean castleValid(boolean sideIsEmpty, Optional<Piece> rookOpt) {
            return sideIsEmpty
                && rookOpt.isPresent()
                && rookOpt.get() instanceof Rook
                && !rookOpt.get().hasMoved;
        }

        boolean isThreatened() {
            boolean threatened = board.isThreatened(team, getLoc());
            if (threatened) {
                setHasMoved();
            }
            return threatened;
        }

        /**
         * @return `true` iff @param `loc` is one of the 9 squares
         *          surrounding and containing `this` Piece
         */
        public boolean withinOneSquareOf(BoardLoc loc) {
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++)
                    if (BoardLoc.at(getLoc().row+i, getLoc().col+j).equals(loc))
                        return true;
            return false;
        }
    }

    public static class Queen extends Piece {
        public Queen(Board board, BoardLoc loc) {
            super(board, loc, 'Q');
        }

        @Override public Set<BoardLoc> possibleMoves() {
            Set<BoardLoc> moves = diagonalMoves();
            moves.addAll(straightMoves()); // java is *very* ugly.
            return moves;
        }
    }
}