package examples;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a football team
 */
public class Team {

    // The list of the team players
    private List<Player> players = new ArrayList<>();

    /**
     * List of the players
     *
     * @return ^
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players
     *
     * @param players List to set to
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
