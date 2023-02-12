package com.treatmentunit.formating;

import com.fasterxml.jackson.databind.util.TypeKey;
import org.apache.tomcat.util.json.JSONParser;
import org.ietf.jgss.GSSContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class DataFormating {

    static HashMap<String, Integer> BusNumber = new HashMap<>();

    // FormatedString Components
    public String FormatedStringHeader = """
                {
                "type": "FeatureCollection",
                "features": [
                """;
    public String FormatedStringContent = "";
    public String FormatedStringFooter = """
                ]}
                """;

    public static String readFileAsString(String file)throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static void main(String[] args) throws Exception {

        // Pour tester avec un fichier json provenant d'un curl
        String test_file = "C:\\Users\\1234Y\\OneDrive\\Documents\\DossierEtudes2\\PROJ-SI\\rest-service\\rest-service\\src\\main\\java\\com\\treatmentunit\\formating\\exemple.json";
        String json = readFileAsString(test_file);

        DataFormating dataFormating = new DataFormating();
        System.out.println(dataFormating.formatReceivedJSON(json));
    }

    public String formatReceivedJSON(String src) {
        System.out.println("[*] Formatting ...");

        FormatedStringHeader = """
                {
                "type": "FeatureCollection",
                "features": [
                """;

        FormatedStringContent = "";
        FormatedStringFooter = """
                ]}
                """;

        JSONObject obj = new JSONObject(src);
        Object number_of_entries = obj.getJSONObject("parameters").get("rows");
        System.out.println("Number of entries processed: " + (obj.getJSONObject("parameters").get("rows")));

        JSONArray array = obj.getJSONArray("records");
        if (array != null) {
            System.out.println("Number of entries in the array: " + array.length());
            boolean empty = true;
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject("fields").has("nomcourtligne") && array.getJSONObject(i).getJSONObject("fields").has("coordonnees")) {
                    if (!empty) {
                        FormatedStringContent += ",\n";
                    }
                    String num_bus = array.getJSONObject(i).getJSONObject("fields").getString("nomcourtligne");
                    JSONArray array_coords = array.getJSONObject(i).getJSONObject("fields").getJSONArray("coordonnees");
                    if (array_coords != null && array_coords.length() == 2) {
                        float longitude = array_coords.getFloat(1);
                        float latitude = array_coords.getFloat(0);
                        String FormatedStringFeature = """
                                {
                                "type" : "Feature",
                                    "properties" : {
                                        "icon" : "bus",
                                        "line" : \" """ + num_bus + "\"" + """
                                \n\t},
                                \t"geometry" : {
                                   \t"type" : "Point", 
                                   \t"coordinates" : [""" + longitude + ", " + latitude + "]\n" + """
                                    }
                                }""";

                        FormatedStringContent += FormatedStringFeature;
                        empty = false;
                        // Feed Auxiliary HashMap
                        if (BusNumber.containsKey(num_bus)) {
                            BusNumber.put(num_bus, BusNumber.get(num_bus) + 1);
                        } else {
                            BusNumber.put(num_bus, 1);
                        }
                    }
                }
            }
        }
        FormatedStringHeader += FormatedStringContent;
        FormatedStringHeader += FormatedStringFooter;
        return FormatedStringHeader;
    }

    // For future purposes
    public void printHashMap() {
        for(String name : BusNumber.keySet()) {
            String key = name;
            int val = BusNumber.get(key);
            System.out.println(key + " - " + val);
        }
    }

}