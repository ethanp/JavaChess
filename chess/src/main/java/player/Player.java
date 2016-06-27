package player;

import game.AbstractCommand;
import game.Team;

/**
 * Ethan Petuchowski 8/24/15
 */
public interface Player {
    /**
     * in which the player makes his move
     */
    AbstractCommand move();
    Team getTeam();
}
