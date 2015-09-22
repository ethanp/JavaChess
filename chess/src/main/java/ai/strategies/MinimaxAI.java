package ai.strategies;

import game.Board;
import game.BoardCommand;
import game.Team;

/**
 * Ethan Petuchowski 8/30/15
 *
 * An AI Strategy that chooses moves based on running
 */
public class MinimaxAI implements Strategy {
    final Board board;
    final Team team;
    static final int SEARCH_DEPTH = 2;
    BoardEvaluator boardEvaluator;

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
     * ```scala
     * moves.groupBy(minimax).maxBy(_._1)._2.head
     * ```
     */
    @Override public BoardCommand chooseMove() {
        BoardCommand bestMove = null;
        double bestVal = Double.NEGATIVE_INFINITY;
        for(BoardCommand curMove : board.getMovesFor(team)) {
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
    double minimax(BoardCommand command) {
        board.execute(command);
        double score = minimax(0, true);
        board.undoMove();
        return score;
    }

    /**
     * run the minimax algorithm on the current state of the board
     */
    private double minimax(int curDepth, boolean maximize) {
        if (curDepth == SEARCH_DEPTH) {
            return boardEvaluator.evaluate(board);
        } else {
            double bestScore = 0;
            for (BoardCommand move : board.getMovesFor(team)) {
                board.execute(move);
                double score = minimax(curDepth+1, !maximize);
                if (score > bestScore && maximize) {
                    bestScore = score;
                }
                else if (score < bestScore && !maximize) {
                    bestScore = score;
                }
                board.undoMove();
            }
            return bestScore;
        }
    }
}
