package main.java.of_2015.chess.ai;

import main.java.of_2015.chess.game.Board;
import main.java.of_2015.chess.game.BoardLoc;
import main.java.of_2015.chess.game.BoardCommand;
import main.java.of_2015.chess.game.Piece;
import main.java.of_2015.chess.game.Team;

import java.util.Optional;

/**
 * Ethan Petuchowski 7/8/15
 */
public class Opponent {

    private final Board board;
    private final Team team = Team.BLACK;
    private Strategy strategy = new SimpleStrategy();

    private static class AIMove {
        final BoardCommand command;
        final double value;

        public AIMove(BoardCommand command, double value) {
            this.command = command;
            this.value = value;
        }
    }

    public Opponent(Board board) {
        this.board = board;
    }

    /**
     * in which the opponent makes his move
     */
    public void move() {
        BoardCommand command = chooseMove();
        board.execute(command);
    }

    private BoardCommand chooseMove() {
        AIMove best = new AIMove(new BoardCommand(BoardLoc.at(0, 0), BoardLoc.at(0, 0)), Double.NEGATIVE_INFINITY);
        for (Piece p : board.livePiecesFor(Team.BLACK)) {
            for (BoardLoc move : p.possibleMoves()) {
                double value = strategy.evaluate(move);
                if (value > best.value) {
                    BoardCommand command = new BoardCommand(p.getLoc(), move);
                    best = new AIMove(command, value);
                }
            }
        }
        if (best.command.from.equals(best.command.to))
            throw new IllegalStateException(
                "GAME OVER:\n" +
                "Stale Mate: opponent has no legal moves available");
        return best.command;
    }

    interface Strategy {
        double evaluate(BoardLoc move);
    }

    class SimpleStrategy implements Strategy {
        @Override public double evaluate(BoardLoc move) {
            Optional<Piece> opt = board.getPieceAt(move);
            if (opt.isPresent()) {
                if (opt.get().team == team) {
                    return Double.NEGATIVE_INFINITY;
                }
                else return 5;
            }
            else return 0;
        }
    }
}
