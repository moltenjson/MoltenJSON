package examples;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a football player
 */
public class Player {

    // The player name
    @SerializedName("fullName")
    @Expose
    private String fullName;

    // The player age.
    @SerializedName("age")
    @Expose
    private int age;

    // Whether the player is retired or not.
    @SerializedName("retired")
    @Expose
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
