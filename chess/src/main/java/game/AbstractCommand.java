package game;

/**
 * Ethan Petuchowski 8/21/15
 */
public abstract class AbstractCommand {

    public static AbstractCommand parse(String console) {
        if (console.equalsIgnoreCase("undo")) {
            return AbstractCommand.UndoCommandSingleton.getInstance();
        }
        String[] fromTo = console.split(" ");
        return new BoardCommand(
            BoardLoc.parse(fromTo[0]),
            BoardLoc.parse(fromTo[1])
        );
    }

    // src: journaldev.com/1377/java-singleton-design-pattern-best-practices-with-examples
    public static class UndoCommandSingleton extends AbstractCommand {
        private static final UndoCommandSingleton instance = new UndoCommandSingleton();

        // private constructor to avoid client applications to use constructor
        private UndoCommandSingleton(){}

        public static UndoCommandSingleton getInstance() {
            return instance;
        }

        @Override public String toString() {
            return "UndoCommandSingleton";
        }
    }

    public static class BoardCommand extends AbstractCommand {

        // they can be public because they're Immutable
        public final BoardLoc from;
        public final BoardLoc to;

        public BoardCommand(BoardLoc from, BoardLoc to) {
            this.from = from;
            this.to = to;
        }

        /**
         * Note: calling this will not in itself restore any pieces that were
         *       eaten by the original move
         */
        public BoardCommand opposite() {
            return new BoardCommand(to, from);
        }

        public int distance() {
            return Math.max(
                Math.abs(from.row-to.row),
                Math.abs(from.col-to.col)
            );
        }

        @Override public String toString() {
            return from+" - "+to;
        }

        public static BoardCommand empty() {
            return new BoardCommand(BoardLoc.at(0, 0), BoardLoc.at(0, 0));
        }
    }
}
