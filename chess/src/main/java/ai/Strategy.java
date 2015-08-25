package ai;

import game.Board;
import game.BoardLoc;
import game.Piece;
import game.Team;

import java.util.Optional;

/**
 * Ethan Petuchowski 8/24/15
 */
public interface Strategy {
    double evaluate(BoardLoc move);

    class GreedyAI implements Strategy {
        final Board board;
        final Team team;
        public GreedyAI(Board board, Team team) {
            this.board = board;
            this.team = team;
        }
        @Override public double evaluate(BoardLoc move) {
            Optional<Piece> opt = board.getPieceAt(move);
            return !opt.isPresent() ? 0         // no one home          -- 0
                : opt.get().team != team ? 5    // enemy guy in sight   -- 5
                : Double.NEGATIVE_INFINITY;     // own team             -- -∞
        }
    }
}
