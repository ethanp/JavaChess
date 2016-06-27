package game;

/**
 * Ethan Petuchowski 7/7/15
 */
public enum Team {
    BLACK("b"), WHITE("w");
    private String repr;

    Team(String repr) {
        this.repr = repr;
    }

    public String getRepr() {
        return repr;
    }

    public Team other() {
        return this == BLACK ? WHITE : BLACK;
    }
}
