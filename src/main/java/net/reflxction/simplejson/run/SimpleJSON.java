/*
 * * Copyright 2017-2018 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reflxction.simplejson.run;

import net.reflxction.simplejson.JsonFile;
import net.reflxction.simplejson.JsonReader;
import net.reflxction.simplejson.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

public class SimpleJSON {

    private static String path = "C:\\Users\\user456.user456-PC\\Desktop\\Java\\FileManager\\src\\main\\resources\\data.json";

    public static void main(String[] args) {
        JsonReader reader = new JsonReader(new JsonFile(path));
        Player p1 = new Player("Reflxction");
        Player p2 = new Player("HacksMuch");
        PlayersList list = new PlayersList();
        list.setPlayers(Arrays.asList(p1, p2));
        try {
            JsonWriter writer = new JsonWriter(new JsonFile(path));
            writer.write(list);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PlayersList jsonList = reader.readJson(PlayersList.class);
        for (Player player : jsonList.getPlayers()) {
            System.out.println(player.getName());
        }
        reader.close();
    }

}
