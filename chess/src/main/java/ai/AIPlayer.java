package ai;

import ai.strategies.GreedyAI;
import ai.strategies.Strategy;
import game.AbstractCommand;
import game.AbstractCommand.BoardCommand;
import game.Board;
import game.Team;

/**
 * Ethan Petuchowski 7/8/15
 */
public class AIPlayer implements Player {

    private final Board board;
    private Strategy strategy;
    final Team team;

    public AIPlayer(Team team, Board board, Strategy strategy) {
        this.board = board;
        this.team = team;
        this.strategy = strategy;
    }

    public static AIPlayer newGreedyUniformAI(Team team, Board board) {
        return new AIPlayer(team, board, new GreedyAI(this, Strategy.PieceEvaluator.uniform()));
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
}
