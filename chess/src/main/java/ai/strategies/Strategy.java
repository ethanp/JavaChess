package ai.strategies;

import game.AbstractCommand.BoardCommand;
import game.Piece;
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


}
