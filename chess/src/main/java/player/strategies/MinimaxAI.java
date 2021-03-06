package player.strategies;

import game.Board;
import game.cmd.BoardCommand;
import game.Piece;
import game.Team;

/**
 * Ethan Petuchowski 8/30/15
 *
 * An AI Strategy that chooses moves based on running
 */
public class MinimaxAI implements Strategy {
    private static final int SEARCH_DEPTH = 2;
    private final Board board;
    private final Team team;
    private BoardEvaluator boardEvaluator;

    public MinimaxAI(Board board, Team team) {
        this.board = board;
        this.team = team;
        boardEvaluator = new BoardEvaluator.EvaluateByPieces(team);
    }

    public Team getTeam() {
        return team;
    }

    /**
     * Choose the move that scores highest according to minimax
     *
     * ```scala moves.groupBy(minimax).maxBy(_._1)._2.head ```
     */
    @Override public BoardCommand chooseMove() {
        BoardCommand bestMove = null;
        double bestVal = Double.NEGATIVE_INFINITY;
        for (BoardCommand curMove : board.getMovesFor(team)) {
            double curVal = minimax(curMove);
            if (curVal > bestVal) {
                bestMove = curMove;
                bestVal = curVal;
            }
        }
        return bestMove;
    }

    /**
     * find the minimax score of executing the command
     */
    private double minimax(BoardCommand command) {
        Piece killed = board.execute(command).orElse(Piece.ZERO_VALUE.instance());
        double score = minimax(0, false) + boardEvaluator.evaluatePiece(killed);
        board.undoMove();
        return score;
    }

    /**
     * run the minimax algorithm on the current state of the board
     */
    private double minimax(int curDepth, boolean maximize) {
        if (curDepth == SEARCH_DEPTH) {
            return boardEvaluator.evaluate(board);
        }
        else {
            double bestScore = 0;
            for (BoardCommand move : board.getMovesFor(team)) {
                board.execute(move);
                double score = minimax(curDepth + 1, !maximize);
                bestScore = maximize
                    ? Math.max(score, bestScore)
                    : Math.min(score, bestScore);

                board.undoMove();
            }
            return bestScore;
        }
    }
}
