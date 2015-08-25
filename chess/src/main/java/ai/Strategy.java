package ai;

import ai.Strategy.PieceEvaluator.TextbookEvaluator;
import ai.Strategy.PieceEvaluator.UniformEvaluator;
import game.AbstractCommand.BoardCommand;
import game.Board;
import game.Board.StateChange;
import game.BoardLoc;
import game.ChessGame;
import game.Piece;
import game.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Stack;

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
        int evaluate(Piece p);

        class UniformEvaluator implements PieceEvaluator {
            @Override public int evaluate(Piece p) {
                return 5;
            }
        }

        class TextbookEvaluator implements PieceEvaluator {
            @Override public int evaluate(Piece p) {
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
                : opt.get().team != team ? pieceEvaluator.evaluate(opt.get()) // enemy guy in sight
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

    class DepthFirstAI implements Strategy {
        final Board board;
        final Team team;
        static final int SEARCH_DEPTH = 2;
        PieceEvaluator pieceEvaluator = new TextbookEvaluator();

        public DepthFirstAI(Board board, Team team) {
            this.board = board;
            this.team = team;
        }
        public Team getTeam() {
            return team;
        }

        // TODO FINISH_ME
        @Override public BoardCommand chooseMove() {
            AIMove best = new AIMove(BoardCommand.empty(), Double.NEGATIVE_INFINITY);
            for (Piece p : board.livePiecesFor(team)) {
                AIMove move = dfsBest(p);
                if (move.value > best.value) {
                    best = move;
                }
            }
            return best.command;
        }

        private AIMove bestMoveBacktracker(Team team, Stack<StateChange> changes, int curScore) {
            if (changes.size() >= SEARCH_DEPTH) {

            }
            else {
                for (Piece p : board.livePiecesFor(team)) {
                    for (BoardLoc toLoc : p.possibleMoves()) {
                        BoardCommand cmd = new BoardCommand(p.getLoc(), toLoc);
                        Optional<Piece> killed = board.getPieceAt(toLoc);
                        StateChange stateChange = new StateChange(killed, cmd);
                        changes.push(stateChange);
                        board.execute(cmd);
                        if (killed.isPresent()) {
                            int pieceVal = pieceEvaluator.evaluate(killed.get());
                            curScore = team == this.team ?
                                  curScore + pieceVal
                                : curScore - pieceVal;
                        }
                        AIMove bestThere = bestMoveBacktracker(team.other(), changes, curScore);
                        // TODO undo the move
                    }
                }
            }
        }

        private AIMove dfsBest(Piece p) {
            Stack<StateChange> stack = new Stack<>();
            for (BoardLoc move : p.possibleMoves()) {
                Optional<Piece> opt = board.getPieceAt(move);
                do {
                    for (BoardLoc toLoc : opt.get().possibleMoves()) {
                        if (stack.size() > SEARCH_DEPTH) {
                            BoardCommand cmd = new BoardCommand(p.getLoc(), move);
                            stack.push(new StateChange(opt, cmd));
                            board.execute(cmd);
                        }
                    }
                } while (!stack.isEmpty());
            }
            return null;
        }
    }
}
