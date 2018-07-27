package examples;

import net.reflxction.simplejson.JsonFile;
import net.reflxction.simplejson.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

public class RunWrite {

    public static void main(String[] args) {
        // Initializing the team
        Player ozil = new Player("Mesut Ozil", 29, true);
        Player boateng = new Player("Jerome Boateng", 29, false);
        Player draxler = new Player("Julian Draxler", 24, false);
        Player lahm = new Player("Philipp Lahm", 34, true);
        Team germanTeam = new Team();
        germanTeam.setPlayers(Arrays.asList(ozil, boateng, draxler, lahm));

        // Using JSON to save it

        // The JSON file
        JsonFile soccer = new JsonFile("C:\\Users\\Germany\\Desktop\\Football\\players.json");
        try {
            // Create a new writer for the file
            JsonWriter writer = new JsonWriter(soccer);

            // Write the data into it
            writer.write(germanTeam);

            // Close the writer IO connection. This is a very important step and must not be forgotten to avoid safety issues
            writer.close();

            // Print a success message
            System.out.println("Successfully wrote the team into the file!");

            // JSON file would like this:
            // https://pastebin.com/raw/GfxZqf4M
        } catch (IOException e) {
            System.out.println("Failed to write data into the file. Stacktrace:");
            e.printStackTrace();
        }

    }

}
