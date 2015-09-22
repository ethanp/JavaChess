package ai.strategies;

import ai.AIPlayer;
import ai.Player;
import game.BoardCommand;
import game.BoardLoc;
import game.Piece;

import java.util.Optional;

/**
 * Ethan Petuchowski 8/30/15
 */
public class GreedyAI implements Strategy {
    PieceEvaluator pieceEvaluator;
    final AIPlayer aiPlayer;

    // TODO delete?
    public Player getPlayer() {
        return aiPlayer;
    }

    public GreedyAI(AIPlayer aiPlayer, PieceEvaluator pieceEvaluator) {
        this.aiPlayer = aiPlayer;
        this.pieceEvaluator = pieceEvaluator;
    }

    private double evaluate(BoardLoc move) {
        Optional<Piece> opt = aiPlayer.getBoard().getPieceAt(move);
        return !opt.isPresent() ? 0                                       // no one home
            : opt.get().team != aiPlayer.getTeam() ? pieceEvaluator.valueOf(opt.get()) // enemy guy in sight
            : Double.NEGATIVE_INFINITY;                                   // own team
    }

    @Override public BoardCommand chooseMove() {

        AIMove best = new AIMove(BoardCommand.empty(), Double.NEGATIVE_INFINITY);
        for (Piece p : aiPlayer.getBoard().livePiecesFor(aiPlayer.getTeam())) {
            for (BoardLoc move : p.possibleMoves()) {
                double value = evaluate(move);
                if (value > best.value) {
                    BoardCommand command = new BoardCommand(p.getLoc(), move);
                    best = new AIMove(command, value);
                }
            }
        }
        if (best.value == Double.NEGATIVE_INFINITY) {
            throw new IllegalStateException("GAME OVER:\n"+
                "Stale Mate: opponent has no legal moves available");
        }
        return best.command;
    }
}
