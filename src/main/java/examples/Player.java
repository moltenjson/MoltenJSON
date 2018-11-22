package examples;

/**
 * Represents a football player
 */
public class Player {

    // The player name
    private String fullName;

    // The player age.
    private int age;

    // Whether the player is retired or not.
    private boolean retired;

    /**
     * Creates a new Player
     *
     * @param fullName Full name of the player
     * @param age      Age of the player
     * @param retired  Whether the player is retired or not
     */
    public Player(String fullName, int age, boolean retired) {
        this.fullName = fullName;
        this.age = age;
        this.retired = retired;
    }

    /**
     * The player's full name
     *
     * @return Full name, first and last name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * The age
     *
     * @return Player's age int
     */
    public int getAge() {
        return age;
    }

    /**
     * Retired
     *
     * @return Whether the player is retired or not
     */
    public boolean isRetired() {
        return retired;
    }
}
