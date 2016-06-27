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
        private UndoCommandSingleton() {
        }

        public static UndoCommandSingleton getInstance() {
            return instance;
        }

        @Override public String toString() {
            return "UndoCommandSingleton";
        }
    }
}
