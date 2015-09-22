package ai.strategies;

import game.BoardCommand;
import game.Board;
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

        protected BoardEvaluator(Team team) {this.team = team;}

        abstract double evaluate(Board board);

        static class EvaluateByPieces extends BoardEvaluator {
            final PieceEvaluator pieceEvaluator;

            EvaluateByPieces(Team team) {
                this(team, new PieceEvaluator.TextbookEvaluator());
            }

            EvaluateByPieces(Team team, PieceEvaluator evaluator) {
                super(team);
                EvaluateByPieces.this.pieceEvaluator = evaluator;
            }

            @Override public double evaluate(Board board) {
                double sum = 0;
                for (Piece p : board.getLivePieces()) {
                    if (p.team == team) {
                        sum += EvaluateByPieces.this.pieceEvaluator.valueOf(p);
                    }
                    else {
                        sum -= EvaluateByPieces.this.pieceEvaluator.valueOf(p);
                    }
                }
                return sum;
            }
        }
    }
}
