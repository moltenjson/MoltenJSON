package examples;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Player {

    // The player name
    @SerializedName("fullName")
    @Expose
    private String fullName;

    // The player age. Must NOT be the primitive type, but instead the wrapper class!
    @SerializedName("age")
    @Expose
    private Integer age;

    // Whether the player is retired or not. Must NOT be the primitive type, but instead the wrapper class!
    @SerializedName("retired")
    @Expose
    private Boolean retired;

    public Player(String fullName, Integer age, Boolean retired) {
        this.fullName = fullName;
        this.age = age;
        this.retired = retired;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getAge() {
        return age;
    }

    public Boolean getRetired() {
        return retired;
    }
}