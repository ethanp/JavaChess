package game;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

/**
 * Ethan Petuchowski 7/7/15
 */
public class Board {

    // this looks like "bad form" with the whole "null" aspect
    public Board() {
        this(null);
    }

    public Board(Pieces pieces) {
        if (pieces == null) {
            this.pieces = Pieces.completeSet(this);
        }
        else this.pieces = pieces;
    }

    public static Board empty() {
        return new Board(Pieces.none());
    }

    public boolean canUndoMove() {
        return !undoStack.isEmpty();
    }

    static class StateChange {
        final Optional<Piece> killedPiece;
        final BoardCommand command;
        public StateChange(Optional<Piece> killedPiece, BoardCommand command) {
            this.killedPiece = killedPiece;
            this.command = command;
        }
    }

    /** FIELDS */
    private final Pieces pieces;
    private BoardRenderer boardRenderer = new CoordinateCommandLineRenderer(this);
    private final Stack<StateChange> undoStack = new Stack<>();

    /** API */

    /**
     * doesn't check whether the move is valid.
     * for example we have to move pawns backwards.
     */
    public void forceExecute(BoardCommand command) {
        Optional<Piece> movedPiece = getPieceAt(command.from);
        if (!movedPiece.isPresent()) {
            throw new IllegalStateException("Can't undo, no one home at "+command.from);
        }
        pieces.forceMove(command.from, command.to);
    }

    /**
     * @return the piece that was killed if there was one
     */
    public Optional<Piece> execute(BoardCommand command) {
        Optional<Piece> killedPiece = getPieceAt(command.to);
        Optional<Piece> movedPiece = getPieceAt(command.from);
        if (!movedPiece.isPresent()) {
            System.err.println("No one home at "+command.from);
            return Optional.empty();
        }
        pieces.moveFromTo(command.from, command.to);

        /* Special case for castling: move the Rook too.
         *
         * We add the Rook to the `undoStack` first so that the King gets
         * popped off first on undo so that we know it was a castle and
         * that we also have to undo the Rook's movement.
         */
        moveRookIfCastling(command, movedPiece);

        /* Special case for en passant:
         *
         * kill the appropriate pawn.
         */
        killIfEnPassant(command, killedPiece, movedPiece);

        undoStack.add(new StateChange(killedPiece, command));

        return killedPiece;
    }

    private void moveRookIfCastling(BoardCommand command, Optional<Piece> movedPiece) {
        boolean isCastling = movedPiece.get() instanceof Piece.King && command.distance() > 1;
        if (isCastling) {
            boolean isLeftward = command.from.left(2).equals(command.to);
            BoardCommand leftRookFollowSuit = new BoardCommand(command.from.left(4), command.from.left());
            BoardCommand rightRookFollowSuit = new BoardCommand(command.from.right(3), command.from.right());
            if (isLeftward) execute(leftRookFollowSuit);
            else execute(rightRookFollowSuit);
        }
    }

    /**
     * Conditions for En Passant:
     *
     * 1. We moved a pawn
     * 2. No one was killed in the move
     * 3. A piece is in the appropriate piece for en-passant capture
     * 4. That piece is also a pawn
     */
    private void killIfEnPassant(BoardCommand command, Optional<Piece> killedPiece, Optional<Piece> movedPiece) {
        if (!(movedPiece.get() instanceof Piece.Pawn)) return;
        if (killedPiece.isPresent()) return;
        Optional<Piece> wouldEnPass = getPieceAt(BoardLoc.at(command.from.row, command.to.col));
        if (!wouldEnPass.isPresent()) return;
        if (!(wouldEnPass.get() instanceof Piece.Pawn)) return;
        wouldEnPass.get().kill();
    }

    public void undoMove() {
        if (!canUndoMove()) return; // TODO is this how I want to handle this IllegalState?
        StateChange lastChange = undoStack.pop();
        forceExecute(lastChange.command.opposite());

        /* Restore the piece that was killed as a result of this move
         *
         * TODO this could potentially go wrong
         *
         *      -- if that piece was already revived for any other reason,
         *         then we will be teleporting it to this location; which is incorrect.
         *
         *      -- what we SHOULD do instead is make a COPY of the killed piece, and
         *         move THAT one to the listed location.
         */
        if (lastChange.killedPiece.isPresent()) {
            lastChange.killedPiece.get().forceMove(lastChange.command.to);
        }

        /* special case for castling, we only undid the King, now un-move Rook too */
        Piece lastMovedPiece = getPieceAt(lastChange.command.from).get();
        boolean wasCastle = lastMovedPiece instanceof Piece.King
                         && lastChange.command.distance() > 1;
        if (wasCastle) {
            undoMove();
        }
    }

    /* A BIG BAG OF ONE-LINERS */

    /**
     * @throws ClassCastException if you don't give it a normal chess move like "E4 E5"
     */
    public Optional<Piece> execute(String cmdStr) {
        AbstractCommand comm = AbstractCommand.parse(cmdStr);
        return execute((BoardCommand)comm);
    }

    public Collection<Piece> livePiecesFor(Team team)   { return pieces.livePieces(team); }
    public Optional<Piece>  getPieceAt(BoardLoc loc)    { return pieces.getPieceAt(loc); }
    public Piece.King       getKing(Team team)          { return pieces.getKing(team); }
    public Iterable<Piece>  getLivePieces()             { return pieces.livePieces(); }
    public void             draw()                      { boardRenderer.draw(); }
    public boolean          hasPieceAt(BoardLoc at)     { return getPieceAt(at).isPresent(); }
    public boolean          hasPieceAt(String str)      { return getPieceAt(BoardLoc.parse(str)).isPresent(); }
    public boolean          hasPieceAt(int row, int col) { return hasPieceAt(BoardLoc.at(row, col)); }
    public boolean          inCheck(Team team)          { return pieces.getKing(team).isThreatened(); }
    public StateChange      lastMove()                  { return undoStack.isEmpty() ? null : undoStack.peek(); }
    public Piece            lastPieceMoved()            { return undoStack.isEmpty() ? null : getPieceAt(undoStack.peek().command.to).get(); }
    public boolean          hasLegalMoves(Team team)    { return livePiecesFor(team).stream().anyMatch(Piece::hasMoves); }
    public void             forceResetPiecesTo(Set<Piece> pieces) { this.pieces.forceResetPiecesTo(pieces); }

    /**
     * @return true iff the other team could attack this location on their next turn
     */
    public boolean isThreatened(Team team, BoardLoc loc) {
        return pieces.livePieces(team.other()).stream()
            .anyMatch(p -> {

                // this is "ugly" to make debugging `.possibleMoves()` easier
                boolean threatens = false;

                /* Same logic for king would cause infinite recursion
                 * bc finding a King's `possibleMoves` requires determining
                 * where it `isThreatened`. This way of calculating `isThreatened`
                 * may not be strictly correct for all cases, but it's definitely
                 * pretty close & "close enough".
                 */
                if (p instanceof Piece.King) {
                    threatens = ((Piece.King) p).withinOneSquareOf(loc);
                }
                else {
                    Set<BoardLoc> moveSet = p.possibleMoves();
                    threatens = moveSet.contains(loc);
                }
                return threatens;
            }
        );
    }

}