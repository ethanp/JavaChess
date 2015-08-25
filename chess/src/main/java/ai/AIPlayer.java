package ai;

import game.AbstractCommand;
import game.AbstractCommand.BoardCommand;
import game.Board;
import game.BoardLoc;
import game.Piece;
import game.Team;

/**
 * Ethan Petuchowski 7/8/15
 */
public class AIPlayer implements Player {

    private final Board board;
    private Strategy strategy;
    final Team team;

    public AIPlayer(Team team, Board board) {
        this.board = board;
        this.team = team;
        strategy = new Strategy.GreedyAI(board, team);
    }

    /**
     * in which the opponent makes his move
     *
     * caller is responsible for checking if the move makes sense
     */
    @Override public AbstractCommand move() {
        return chooseMove();
    }

    @Override public Team getTeam() {
        return team;
    }

    private BoardCommand chooseMove() {

        class AIMove {
            final BoardCommand command;
            final double value;

            public AIMove(BoardCommand command, double value) {
                this.command = command;
                this.value = value;
            }
        }

        AIMove best = new AIMove(BoardCommand.empty(), Double.NEGATIVE_INFINITY);
        for (Piece p : board.livePiecesFor(getTeam())) {
            for (BoardLoc move : p.possibleMoves()) {
                double value = strategy.evaluate(move);
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
