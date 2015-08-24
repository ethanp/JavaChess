package ai;

import game.AbstractCommand;
import game.Board;
import game.BoardLoc;
import game.Piece;
import game.Team;

import java.util.Optional;

/**
 * Ethan Petuchowski 7/8/15
 */
public class Opponent implements Player {

    private final Board board;
    private final Team team = Team.BLACK;
    private Strategy strategy = new GreedyAI();

    public Opponent(Board board) {
        this.board = board;
    }

    /**
     * in which the opponent makes his move
     */
    @Override public void move() {
        AbstractCommand.BoardCommand command = chooseMove();
        board.execute(command);
    }

    private AbstractCommand.BoardCommand chooseMove() {
        class AIMove {
            final AbstractCommand.BoardCommand command;
            final double value;

            public AIMove(AbstractCommand.BoardCommand command, double value) {
                this.command = command;
                this.value = value;
            }
        }

        AIMove best = new AIMove(new AbstractCommand.BoardCommand(BoardLoc.at(0, 0), BoardLoc.at(0, 0)), Double.NEGATIVE_INFINITY);
        for (Piece p : board.livePiecesFor(Team.BLACK)) {
            for (BoardLoc move : p.possibleMoves()) {
                double value = strategy.evaluate(move);
                if (value > best.value) {
                    AbstractCommand.BoardCommand command = new AbstractCommand.BoardCommand(p.getLoc(), move);
                    best = new AIMove(command, value);
                }
            }
        }
        if (best.command.from.equals(best.command.to))
            throw new IllegalStateException(
                "GAME OVER:\n"+
                    "Stale Mate: opponent has no legal moves available");
            return best.command;
    }

    interface Strategy {
        double evaluate(BoardLoc move);
    }

    public class GreedyAI implements Strategy {
        @Override public double evaluate(BoardLoc move) {
            Optional<Piece> opt = board.getPieceAt(move);
            return !opt.isPresent() ? 0         // no one home          -- 0
                : opt.get().team != team ? 5    // enemy guy in sight   -- 5
                : Double.NEGATIVE_INFINITY;     // own team             -- -∞
        }
    }
}
