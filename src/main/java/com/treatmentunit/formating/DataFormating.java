package com.treatmentunit.formating;

import com.fasterxml.jackson.databind.util.TypeKey;
import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.restservice.APIController;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.apache.tomcat.util.json.JSONParser;
import org.ietf.jgss.GSSContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class DataFormating {

    static HashMap<String, Integer> BusNumber = new HashMap<>();
    private DatabaseBinding databaseBinding = new DatabaseBinding();
    private OptimisationAndFormating optimisationAndFormating = new OptimisationAndFormating();

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

    public ArrayList<String> formatReceivedJSON_PARCOURS(JSONObject src) {
        ArrayList<String> res = new ArrayList<>();

        try {

        JSONObject fields = src.getJSONObject("fields");
        String sens = String.valueOf(fields.getInt("sens"));
        String code = fields.getString("code");
        String type = fields.getString("type");
        String geo_point_2d = fields.getJSONArray("geo_point_2d").toString();
        String sens_commercial = fields.getString("senscommercial");
        String est_version_active = fields.getString("estversionactive");
        String id_ligne = fields.getString("idligne");
        String libelle_long = fields.getString("libellelong");
        String id_arret_arrivee = fields.getString("idarretarrivee");
        JSONObject parcours = fields.getJSONObject("parcours");
        String coordinates_parcours = parcours.getJSONArray("coordinates").toString();
        String coordinates_type = parcours.getString("type");
        String id = fields.getString("id");
        String nom_arret_depart;
        if(fields.has("nomarretdepart")) {
            nom_arret_depart = fields.getString("nomarretdepart");
        } else {
            nom_arret_depart = "0";
        }
        String date_debut_version = fields.getString("datedebutversion");
        String id_arret_depart = fields.getString("idarretdepart");
        String est_accessible_pmr = fields.getString("estaccessiblepmr");
        String visibilite = fields.getString("visibilite");
        String date_fin_version = "";
        if(fields.has("datefinversion")) {
            date_fin_version = fields.getString("datefinversion");
        } else {
            date_fin_version = "0";
        }
        String nom_court_ligne = fields.getString("nomcourtligne");
        String nom_arret_arrivee;
            if(fields.has("nomarretarrivee")) {
                nom_arret_arrivee = fields.getString("nomarretarrivee");
            } else {
                nom_arret_arrivee = "0";
            }
        String longueur = String.valueOf(fields.getDouble("longueur"));
        String couleur_trace = fields.getString("couleurtrace");

        // PAS DE FACET LORS DE l'APPEL API !!

        res.add(id);
        res.add(date_debut_version);
        res.add(date_fin_version);
        res.add(est_version_active);
        res.add(code);
        res.add(id_ligne);
        res.add(nom_court_ligne);
        res.add(sens);
        res.add(sens_commercial);
        res.add(type);
        res.add(libelle_long);
        res.add(id_arret_depart);
        res.add(nom_arret_depart);
        res.add(id_arret_arrivee);
        res.add(nom_arret_arrivee);
        res.add(est_accessible_pmr);
        res.add(longueur);
        res.add(couleur_trace);
        res.add(visibilite);
        res.add(geo_point_2d);

        res.add(id);
        res.add(coordinates_parcours);

        } catch (JSONException e) {
            System.out.println("STOOOOOOOOOOOOOOOOOOOOOOOOOOOP :::");
            e.printStackTrace();
            System.out.println("[!] Erreur lors du traitement des données JSON !");
        }

        return res;
    }


    public String formatReceivedJSON(String src) throws SQLException, IOException {
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
                    String nom_bus = array.getJSONObject(i).getJSONObject("fields").getString("nomcourtligne");
                    JSONArray array_coords = array.getJSONObject(i).getJSONObject("fields").getJSONArray("coordonnees");
                    String sens = String.valueOf(array.getJSONObject(i).getJSONObject("fields").getInt("sens"));

                    if (array_coords != null && array_coords.length() == 2) {
                        float longitude = array_coords.getFloat(1);
                        float latitude = array_coords.getFloat(0);

                        String REQ = "SELECT DISTINCT tab_coordonnes FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and nomcourtligne = '" + nom_bus + "' and sens = '" + sens + "\'";
                        ArrayList<String> single_val = databaseBinding.requestFetchSingleValue(REQ);

                        String returned = optimisationAndFormating.getOutput(single_val.get(0), "["+ latitude + "," + longitude +"]");
                        String[] returned_splited = returned.split(";");

                        String FormatedStringFeature = """
                                {
                                "type" : "Feature",
                                    "id" : """ + i + ",\n" + """
                                    "properties" : {
                                        "icon" : "bus",
                                        "line" : \" """ + nom_bus + "\"" + ",\n"+ """
                                        \t"nextindex" : \"""" + returned_splited[1] + "\",\n" + """
                                        \t"sens" : \"""" + sens + "\"" + """ 
                                \n\t},
                                \t"geometry" : {
                                   \t"type" : "Point", 
                                   \t"coordinates" : [""" + longitude + ", " + latitude + "]\n" + """
                                    }
                                }""";

                        FormatedStringContent += FormatedStringFeature;
                        empty = false;

                        // Feed Auxiliary HashMap
                        if (BusNumber.containsKey(nom_bus)) {
                            BusNumber.put(nom_bus, BusNumber.get(nom_bus) + 1);
                        } else {
                            BusNumber.put(nom_bus, 1);
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