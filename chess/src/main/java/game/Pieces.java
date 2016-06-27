package game;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ethan Petuchowski 7/7/15
 */
public class Pieces {

    @SuppressWarnings("unused")
    private final Board board;
    private Set<Piece> pieces = new HashSet<>();

    /** for testing */
    private Pieces() {
        this.board = null; // won't be needing this
    }

    private Pieces(Board board) {
        this.board = board;
        placePawns(board);
        placeRooks(board);
        placeKnights(board);
        placeBishops(board);
        placeKings(board);
        placeQueens(board);
    }

    /** FACTORIES **/
    public static Pieces completeSet(Board board) {
        return new Pieces(board);
    }

    /** for testing */
    public static Pieces none() {
        return new Pieces();
    }

    private void placeQueens(Board board) {
        BoardLoc whiteQueenLoc = BoardLoc.at(0, 3);
        pieces.add(new Piece.Queen(board, whiteQueenLoc));
        pieces.add(new Piece.Queen(board, whiteQueenLoc.mirror()));
    }

    private void placeKings(Board board) {
        BoardLoc whiteKingLoc = BoardLoc.at(0, 4);
        pieces.add(new Piece.King(board, whiteKingLoc));
        pieces.add(new Piece.King(board, whiteKingLoc.mirror()));
    }

    private void placeBishops(Board board) {
        for (BoardLoc loc : BoardLoc.corners(0, 2))
            pieces.add(new Piece.Bishop(board, loc));
    }

    private void placeKnights(Board board) {
        for (BoardLoc loc : BoardLoc.corners(0, 1))
            pieces.add(new Piece.Knight(board, loc));
    }

    private void placeRooks(Board board) {
        for (BoardLoc loc : BoardLoc.corners(0, 0))
            pieces.add(new Piece.Rook(board, loc));
    }

    private void placePawns(Board board) {
        for (int i = 0; i < 4; i++)
            for (BoardLoc loc : BoardLoc.corners(1, i))
                pieces.add(new Piece.Pawn(board, loc));
    }

    public List<Piece> livePieces() {
        return pieces.stream()
            .filter(Piece::isAlive)
            .collect(Collectors.toList());
    }

    public List<Piece> livePieces(Team team) {
        return pieces.stream()
            .filter(p -> p.team == team && p.isAlive())
            .collect(Collectors.toList());
    }

    public Optional<Piece> getPieceAt(BoardLoc at) {
        return pieces.stream()
            .filter(p ->
                p.isAlive() && p.getLoc().equals(at)
            ).findAny();
    }

    /**
     * doesn't check whether the move is valid for that piece. Used for undo.
     */
    public void forceMove(BoardLoc from, BoardLoc to) {
        getPieceAt(from).get().forceMove(to);
    }

    public void moveFromTo(BoardLoc from, BoardLoc to) {
        Optional<Piece> f = getPieceAt(from);
        if (f.isPresent()) {
            Optional<Piece> t = getPieceAt(to);
            if (t.isPresent())
                t.get().kill();
            f.get().move(to);
        }
        else throw new RuntimeException("there is no piece at " + from);
    }

    public Piece.King getKing(Team team) {
        return (Piece.King) pieces
            .stream()
            .filter(p -> p.team == team && p instanceof Piece.King)
            .findFirst().get();
    }

    public void forceResetPiecesTo(Set<Piece> pieces) {
        this.pieces = pieces;
    }
}
