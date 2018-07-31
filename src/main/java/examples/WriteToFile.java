package examples;

import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class WriteToFile {

    public static void main(String[] args) {
        // Initializing the team
        Player ozil = new Player("Mesut Ozil", 29, true);
        Player boateng = new Player("Jerome Boateng", 29, false);
        Player draxler = new Player("Julian Draxler", 24, false);
        Player lahm = new Player("Philipp Lahm", 34, true);
        Team germanTeam = new Team();
        germanTeam.setPlayers(Arrays.asList(ozil, boateng, draxler, lahm));

        // Using JSON to save it

        // The system file separator. This is always a better option than using "\" so it works across all platforms.
        final String s = File.separator;

        try {
            // The JSON file
            JsonFile playersFile = new JsonFile("C:" + s + "Users" + s + "Germany" + s + "Desktop" + s + "Football" + s + "players.json", true);

            // Create a new writer for the file
            JsonWriter writer = new JsonWriter(playersFile);

            // Write the data into it
            writer.write(germanTeam, true);

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
