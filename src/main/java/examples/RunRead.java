package examples;

import net.reflxction.simplejson.JsonFile;
import net.reflxction.simplejson.JsonReader;

public class RunRead {

    public static void main(String[] args) {

        // Identify the JSON file
        JsonFile soccer = new JsonFile("C:\\Users\\Germany\\Desktop\\Football\\players.json");

        // Create a reader for the file
        JsonReader reader = new JsonReader(soccer);

        // Parse the team from the JSON file
        Team germanTeam = reader.readJson(Team.class);

        // Use the team after parsing
        for (Player player : germanTeam.getPlayers()) {
            System.out.println("Name: " + player.getFullName() + " - Age: " + player.getAge() + " - Retired: " + player.getRetired());
        }

        // Close the reader to avoid IO issues. This is an important step and must not be forgotten to avoid safety issues.
        reader.close();

        /*
        Output:
        - Name: Mesut Ozil - Age: 29 - Retired: true
        - Name: Jerome Boateng - Age: 29 - Retired: false
        - Name: Julian Draxler - Age: 24 - Retired: false
        - Name: Philipp Lahm - Age: 34 - Retired: true
         */

    }

}
