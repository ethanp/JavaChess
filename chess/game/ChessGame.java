package main.java.of_2015.chess.game;

import main.java.of_2015.chess.ai.Opponent;

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

    public static void main(String[] args) {
        ChessGame.consoleInterpreted();
    }


    /** FIELDS **/

    final Board board;
    private final Scanner sc = new Scanner(System.in);
    private final Opponent opponent;


    /** CONSTRUCTORS **/

    public ChessGame() {
        this(new Board());
    }

    public ChessGame(Board board) {
        this.board = board;
        this.opponent = new Opponent(board);
    }


    /** FACTORIES **/

    public static ChessGame emptyBoard() {
        return new ChessGame(Board.empty());
    }


    /** METHODS **/

    private static void consoleInterpreted() {
        new ChessGame().startInterpreter();
    }

    private void startInterpreter() {
        while (true) {
            board.draw();
            AbstractCommand command = promptForInput();
            if (command == AbstractCommand.UndoCommandSingleton.getInstance()) {
                if (board.canUndoMove()) {
                    board.undoMove(); // undo opponent's last move
                    board.undoMove(); // undo your own last move
                    continue;
                } else {
                    System.err.println("There are no moves to undo.");
                }
            }
            if (command instanceof BoardCommand) {
                board.execute((BoardCommand) command);
                if (won() || stalemate(Team.BLACK)) {
                    return;
                }
                board.draw();
                opponent.move();
                if (lost() || stalemate(Team.WHITE)) {
                    return;
                }
            }
        }
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
     * So iterate through each of the pieces, and check if still in "check".
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

    boolean lost() {
        boolean whiteLost = gameOverFor(Team.WHITE);
        if (whiteLost) {
            System.out.println("YOU LOST!");
        }
        return whiteLost;
    }

    boolean won() {
        boolean whiteWon = gameOverFor(Team.BLACK);
        if (whiteWon) {
            System.out.println("YOU WON!");
        }
        return whiteWon;
    }

    private AbstractCommand promptForInput() {
        System.out.println("Make your move:");
        String console = sc.nextLine();
        if (console.equals("exit")) {
            System.out.println("Quitting.");
            System.exit(0);
        }
        System.out.println("\n");

        return AbstractCommand.parse(console);
    }

    public void forceResetPiecesTo(Set<Piece> pieces) {
        board.forceResetPiecesTo(pieces);
    }
}
