package ai;

import ai.Strategy.PieceEvaluator.TextbookEvaluator;
import ai.Strategy.PieceEvaluator.UniformEvaluator;
import game.AbstractCommand.BoardCommand;
import game.Board;
import game.BoardLoc;
import game.Piece;
import game.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Ethan Petuchowski 8/24/15
 */
public interface Strategy {
    BoardCommand chooseMove();

    Logger logger = LoggerFactory.getLogger(Strategy.class);

    class AIMove {
        final BoardCommand command;
        final double value;

        public AIMove(BoardCommand command, double value) {
            this.command = command;
            this.value = value;
        }
    }

    interface PieceEvaluator {
        int valueOf(Piece p);

        class UniformEvaluator implements PieceEvaluator {
            @Override public int valueOf(Piece p) {
                return 5;
            }
        }

        class TextbookEvaluator implements PieceEvaluator {
            @Override public int valueOf(Piece p) {
                // wishing Scala were here.
                if (p instanceof Piece.Rook) return 5;
                if (p instanceof Piece.Knight) return 3;
                if (p instanceof Piece.Bishop) return 3;
                if (p instanceof Piece.King) return 100;
                if (p instanceof Piece.Queen) return 9;
                if (p instanceof Piece.Pawn) return 1;
                logger.error("can't evaluate piece {}", p);
                return -1;
            }
        }
    }


    class GreedyAI implements Strategy {
        final Board board;
        final Team team;
        PieceEvaluator pieceEvaluator = new UniformEvaluator();

        public GreedyAI(Board board, Team team) {
            this.board = board;
            this.team = team;
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
                throw new IllegalStateException("GAME OVER:\n"+
                    "Stale Mate: opponent has no legal moves available");
            }
            return best.command;
        }
    }

    class MinimaxAI implements Strategy {
        final Board board;
        final Team team;
        static final int SEARCH_DEPTH = 2;
        PieceEvaluator pieceEvaluator = new TextbookEvaluator();

        public MinimaxAI(Board board, Team team) {
            this.board = board;
            this.team = team;
        }
        public Team getTeam() {
            return team;
        }

        @Override public BoardCommand chooseMove() {
            AIMove move = new AIMove(BoardCommand.empty(), Double.NEGATIVE_INFINITY);
            for (Piece p : board.livePiecesFor(team)) {
                for (BoardLoc toLoc : p.possibleMoves()) {
                    double val = scoreCalc(team, 1, 0);
                    if (val > move.value) {
                        move = new AIMove(new BoardCommand(p.getLoc(), toLoc), val);
                    }
                }
            }
            return move.command;
        }

        /* TODO this is currently simple backtracking, NOT MINIMAX
         * The difference is that this will pretend the opponent
         * always chooses the WORST move, rather than the BEST.
         *
         * Assumes the first move (the one we're evaluating) has already
         * been applied to the board, so we start at a depth of '1'.
         *
         * I'm thinking what it SHOULD do, is label each node of the tree with
         * the best score that the current-level's player could get by moving
         * there. Then we assume the player is going to pick that branch, and
         * from there we base what the scores are for the next-level-UP.
         *
         * But maybe it should also be weighted so that moves nearer-to-NOW
         * have a disproportionately large influence score-wise because they
         * are (~exponentially) more-likely to happen.
         */
        private double scoreCalc(Team team, int depth, double curScore) {
            return depth >= SEARCH_DEPTH ? curScore : bestNextLevel(team, depth, curScore);
        }

        private double bestNextLevel(Team team, int depth, double curScore) {
            double bestNextLevel = Double.NEGATIVE_INFINITY;
            for (Piece p : board.livePiecesFor(team)) {
                for (BoardLoc toLoc : p.possibleMoves()) {
                    // TODO this is basically nonsensical at the moment!
                    bestNextLevel = needsName(team, depth, curScore, bestNextLevel, p, toLoc);
                    double bestThere = needsName(team, depth, curScore, bestNextLevel, p, toLoc);
                }
            }
            return bestNextLevel;
        }

        private double needsName(
            Team team,
            int depth,
            double curScore,
            double bestNextLevel,
            Piece p,
            BoardLoc toLoc)
        {
            Optional<Piece> killed = board.getPieceAt(toLoc);
            board.execute(new BoardCommand(p.getLoc(), toLoc));
            int scoreChange = getScoreChange(team, killed);
            double bestThere = scoreCalc(team.other(), depth+1, curScore+scoreChange);
            bestNextLevel = Math.max(bestNextLevel, bestThere);

            board.undoMove();
            return bestNextLevel;
        }

        private int getScoreChange(Team team, Optional<Piece> killed) {
            int scoreChange = 0;
            if (killed.isPresent()) {
                int pieceVal = pieceEvaluator.valueOf(killed.get());
                scoreChange = team == this.team ? pieceVal : -pieceVal;
            }
            return scoreChange;
        }
    }
}
