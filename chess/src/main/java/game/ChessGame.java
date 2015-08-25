package game;


import ai.AIPlayer;
import ai.HumanPlayer;
import ai.Player;
import game.AbstractCommand.BoardCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.Set;

/**
 * Ethan Petuchowski 7/7/15
 *
 * 11:00 got the idea to try this
 * 12:00 pawns are printing on the board
 * 12:30 all pieces are printing on the board
 * 1:00 basic command-line based piece-movement is working
 */
public class ChessGame {

    /** MAIN **/

    static final Logger logger = LoggerFactory.getLogger(ChessGame.class);

    public static void main(String[] args) {
//        ChessGame.humanVsGreedyAI();
        ChessGame.greedyAI_v_greedyAI();
    }


    /** FIELDS **/

    public final Board board;
    private static final Scanner STDIN_scanner = new Scanner(System.in);
    private final Player player1;
    private final Player player2;


    /** CONSTRUCTORS **/

    public ChessGame() {
        this(Board.completeSet());
    }

    public ChessGame(Board board) {
        this.board = board;
        this.player1 = new HumanPlayer(Team.WHITE, STDIN_scanner);
        this.player2 = new AIPlayer(Team.BLACK, board);
    }

    public ChessGame(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
    }


    /** FACTORIES **/

    public static ChessGame emptyBoard() {
        return new ChessGame(Board.empty());
    }


    /** METHODS **/

    private static void humanVsGreedyAI() {
        Board board = Board.completeSet();
        Player player1 = new HumanPlayer(Team.WHITE, STDIN_scanner);
        Player player2 = new AIPlayer(Team.BLACK, board);
        ChessGame game = new ChessGame(board, player1, player2);
        game.startInterpreter();
    }

    static void greedyAI_v_greedyAI() {
        Board board = Board.completeSet();
        Player player1 = new AIPlayer(Team.WHITE, board);
        Player player2 = new AIPlayer(Team.BLACK, board);
        ChessGame game = new ChessGame(board, player1, player2);
        game.startInterpreter();
    }

    private void movePlayer(Player player) {
        AbstractCommand command = player.move();
        if (command == AbstractCommand.UndoCommandSingleton.getInstance()) {
            if (board.canUndoMove()) {
                board.undoMove(); // undo player2's last move
                board.undoMove(); // undo your own last move
            }
            else System.err.println("There are no moves to undo.");
        }
        else if (command instanceof BoardCommand) {
            BoardCommand boardCommand = (BoardCommand) command;
            if (board.locHasTeam(boardCommand.from, player.getTeam())) {
                board.execute(boardCommand);
            }
            else logger.error("player {} cannot issue BoardCommand {}", player, boardCommand);
        }
        else logger.error("invalid command {}", command);

        logger.debug("{}: {}", player.getTeam(), command);
    }

    private void startInterpreter() {
        final int MOVE_LIMIT = 100;
        for (int moveCounter = 0; moveCounter < MOVE_LIMIT; moveCounter++) {
            board.draw();
            movePlayer(player1);
            if (won() || stalemate(player1.getTeam())) return;

            board.draw();
            movePlayer(player2);
            if (won() || stalemate(player2.getTeam())) return;
        }
        board.draw();
        logger.warn("Game over: STALE-MATE, move limit of {} exceeded without a winner", MOVE_LIMIT);
    }

    private boolean stalemate(Team team) {
        return !board.hasLegalMoves(team);
    }

    private boolean inCheckWithNoKingMoves(Team team) {
        return board.inCheck(team)
            && board.getKing(team).possibleMoves().isEmpty();
    }

    private boolean gameOverFor(Team team) {
        return board.inCheck(team)
            && board.getKing(team).possibleMoves().isEmpty()
            && !isSaviour(team);
    }

    /**
     * What if another piece is able to get in-the-way of the attacker?
     * So iterate through possible moves of each of the pieces,
     * and check if still in "check".
     */
    boolean isSaviour(Team team) {
        boolean canBeSaved = false;
        for (Piece p : board.livePiecesFor(team)) {
            if (p instanceof Piece.King) continue;
            for (BoardLoc possibleLoc : p.possibleMoves()) {
                BoardCommand possibility = new BoardCommand(p.getLoc(), possibleLoc);
                board.execute(possibility);

                // it worked; there is a way out of check
                if (!board.inCheck(team))
                    canBeSaved = true;

                // regardless, restore original game state
                board.undoMove();
                if (canBeSaved) {
                    return true;
                }
            }
        }
        // all pieces exhausted, no saviour to be found
        return false;
    }

    public boolean lost() {
        boolean whiteLost = gameOverFor(Team.WHITE);
        if (whiteLost) {
            logger.warn("YOU LOST!");
        }
        return whiteLost;
    }

    public boolean won() {
        boolean whiteWon = gameOverFor(Team.BLACK);
        if (whiteWon) {
            logger.warn("YOU WON!");
        }
        return whiteWon;
    }

    public void forceResetPiecesTo(Set<Piece> pieces) {
        board.forceResetPiecesTo(pieces);
    }
}
