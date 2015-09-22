package ai.strategies;

import game.Board;
import game.BoardCommand;
import game.Piece;
import game.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ethan Petuchowski 8/24/15
 */
public interface Strategy {
    BoardCommand chooseMove();

    /* static final (implicitly [it's a Java thing]) */
    Logger STRATEGY_LOGGER = LoggerFactory.getLogger(Strategy.class);

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

        static UniformEvaluator uniform() {
            return new UniformEvaluator();
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
                if (p instanceof Piece.ZERO_VALUE) return 0;
                STRATEGY_LOGGER.error("can't evaluate piece {}", p);
                return -1;
            }
        }
    }


    /**
     * Ethan Petuchowski 9/22/15
     */
    abstract class BoardEvaluator {
        final Team team;

        protected BoardEvaluator(Team team, PieceEvaluator evaluator) {
            this.team = team;
            this.pieceEvaluator = evaluator;
        }

        final PieceEvaluator pieceEvaluator;

        BoardEvaluator(Team team) {
            this(team, new PieceEvaluator.TextbookEvaluator());
        }

        abstract double evaluate(Board board);

        public double evaluatePiece(Piece piece) {
            return pieceEvaluator.valueOf(piece);
        }

        static class EvaluateByPieces extends BoardEvaluator {

            EvaluateByPieces(Team team) { super(team); }

            @Override public double evaluate(Board board) {
                double sum = 0;
                for (Piece p : board.getLivePieces()) {
                    if (p.team == team) {
                        sum += evaluatePiece(p);
                    }
                    else {
                        sum -= evaluatePiece(p);
                    }
                }
                return sum;
            }
        }
    }
}
