package ai.strategies;

import game.Board;
import game.BoardCommand;
import game.BoardLoc;
import game.Piece;
import game.Team;

import java.util.Optional;

/**
 * Ethan Petuchowski 8/30/15
 */
public class GreedyAI implements Strategy {
    private final Team team;
    private final Board board;
    private PieceEvaluator pieceEvaluator;

    public GreedyAI(Team team, Board board, PieceEvaluator pieceEvaluator) {
        this.team = team;
        this.board = board;
        this.pieceEvaluator = pieceEvaluator;
    }

    private double evaluate(BoardLoc move) {
        Optional<Piece> opt = board.getPieceAt(move);
        return !opt.isPresent() ? 0                                       // no one home
            : opt.get().team != team ? pieceEvaluator.valueOf(opt.get()) // enemy guy in sight
            : Double.NEGATIVE_INFINITY;                                   // own team
    }

    @Override public BoardCommand chooseMove() {

        AIMove best = new AIMove(BoardCommand.empty(), Double.NEGATIVE_INFINITY);
        for (Piece p : board.livePiecesFor(team)) {
            for (BoardLoc move : p.possibleMoves()) {
                double value = evaluate(move);
                if (value > best.value) {
                    BoardCommand command = new BoardCommand(p.getLoc(), move);
                    best = new AIMove(command, value);
                }
            }
        }
        if (best.value == Double.NEGATIVE_INFINITY) {
            throw new IllegalStateException("GAME OVER:\n" +
                "Stale Mate: opponent has no legal moves available");
        }
        return best.command;
    }
}
