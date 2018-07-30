package examples;

import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonURLReader;
import net.reflxction.simplejson.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

/**
 * Example on using the library to read from a web URL
 */
public class RunReadFromURL {

    public static void main(String[] args) {

        // The system file separator. This is always a better option than using "\" so it works across all platforms.
        final String s = File.separator;

        try {
            // Create a new reader with the given URL
            JsonURLReader reader = new JsonURLReader("https://pastebin.com/raw/GfxZqf4M");

            // Get the JSON as a string from the reader
            String json = reader.readContent().toString();

            // Prettify the JSON text
            String prettyJson = JsonUtils.setPretty(json);

            // Print the JSON
            System.out.println(prettyJson);

            // Identify the JSON file
            JsonFile playersFile = new JsonFile("C:" + s + "Users" + s + "Germany" + s + "Desktop" + s + "Football" + s + "players.json");

            // Write the content locally
            reader.writeToFile(playersFile, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
