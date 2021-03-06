package player;

import player.strategies.GreedyAI;
import player.strategies.Strategy;
import game.cmd.AbstractCommand;
import game.Board;
import game.cmd.BoardCommand;
import game.Team;

/**
 * Ethan Petuchowski 7/8/15
 */
public class AIPlayer implements Player {

    private final Team team;
    private final Board board;
    private Strategy strategy;

    public AIPlayer(Team team, Board board, Strategy strategy) {
        this.board = board;
        this.team = team;
        this.strategy = strategy;
    }

    public static AIPlayer newGreedyTextbookAI(Team team, Board board) {
        GreedyAI evaluator = new GreedyAI(team, board, Strategy.PieceEvaluator.textbook());
        return new AIPlayer(team, board, evaluator);
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
        return strategy.chooseMove();
    }

    public Board getBoard() {
        return board;
    }
}
