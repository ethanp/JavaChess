package game.cmd;

import game.BoardLoc;

/**
 * Ethan Petuchowski 9/22/15
 */
public class BoardCommand extends AbstractCommand {

    // they can be public because they're Immutable
    public final BoardLoc from;
    public final BoardLoc to;

    public BoardCommand(BoardLoc from, BoardLoc to) {
        this.from = from;
        this.to = to;
    }

    public static BoardCommand empty() {
        return new BoardCommand(BoardLoc.at(0, 0), BoardLoc.at(0, 0));
    }

    /**
     * Note: calling this will not in itself restore any pieces that were eaten by the original
     * move
     */
    public BoardCommand opposite() {
        return new BoardCommand(to, from);
    }

    public int distance() {
        return Math.max(
            Math.abs(from.row - to.row),
            Math.abs(from.col - to.col)
        );
    }

    @Override public String toString() {
        return from + " - " + to;
    }
}
