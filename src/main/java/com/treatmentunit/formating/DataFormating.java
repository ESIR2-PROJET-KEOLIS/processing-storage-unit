package com.treatmentunit.formating;

import com.fasterxml.jackson.databind.util.TypeKey;
import com.rabbitmq.client.impl.ForgivingExceptionHandler;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * La classe DataFormating sert à la manipulation et réécriture de différent JSON reçu et envoyé dans le process-unit
 */
public class DataFormating {

    static HashMap<String, Integer> BusNumber = new HashMap<>();
    private DatabaseBinding databaseBinding = new DatabaseBinding();
    private OptimisationAndFormating optimisationAndFormating = new OptimisationAndFormating();
    private static HashMap<String, Integer> busDelays = new HashMap<>();


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

    /**
     *  Renvoie dans un String tout le contenue de file
     * @param file un chemin
     * @return String contenant ce qu'il y a dans le fichier file
     * @throws Exception
     */
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

    /**
     * Réécrit un JSONObject en ArrayList< String>
     * @param src prend un JSONObject
     * @return ArrayList< String>
     */
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

    public static HashMap<String, Integer> getDelaysHashMap() {
        return busDelays;
    }

    /**
     * Formate (JSON) le src pour l'envoi de position théorique
     * @param src String
     * @return String formaté (JSON)
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public String formatReceivedJSON(String src) throws SQLException, IOException, InterruptedException {
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

        /*
        Object number_of_entries;
        try {
            number_of_entries = obj.getJSONObject("parameters").get("rows");
        } catch (Exception f) {
            return "{}";
        }*/

        //System.out.println("Number of entries processed: " + (obj.getJSONObject("parameters").get("rows")));

        JSONArray array = obj.getJSONArray("records");
        if (array != null) {
            System.out.println("Number of entries in the array: " + array.length());
            boolean empty = true;
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject("fields").has("nomcourtligne") && array.getJSONObject(i).getJSONObject("fields").has("coordonnees") && array.getJSONObject(i).getJSONObject("fields").has("ecartsecondes")) {
                    if (!empty) {
                        FormatedStringContent += ",\n";
                    }
                    String nom_bus = array.getJSONObject(i).getJSONObject("fields").getString("nomcourtligne");
                    JSONArray array_coords = array.getJSONObject(i).getJSONObject("fields").getJSONArray("coordonnees");
                    String sens = String.valueOf(array.getJSONObject(i).getJSONObject("fields").getInt("sens"));

                    // Pour retard moyen -> Simulation de flux passagers.
                    Integer retard_en_secondes = Integer.parseInt(String.valueOf(array.getJSONObject(i).getJSONObject("fields").getInt("ecartsecondes")));

                    if (array_coords != null && array_coords.length() == 2) {
                        float longitude = array_coords.getFloat(1);
                        float latitude = array_coords.getFloat(0);

                        String REQ = "SELECT DISTINCT tab_coordonnes FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and type='Principal' and nomcourtligne = '" + nom_bus + "' and sens = '" + sens + "\'";
                        //ArrayList<String> single_val = databaseBinding.requestFetchSingleValue(REQ);

                        String single_val = databaseBinding.requestFetchSingleValue(REQ);

                        String returned = optimisationAndFormating.getOutput(single_val, "["+ latitude + "," + longitude +"]");
                        String[] returned_splited = returned.split(";");

                        busDelays.put(nom_bus, retard_en_secondes);

                        HashMap<String, Double> retrieved_hashmap_from_api_controller = null;
                        String filling_level = "N/A";
                        double filling_proba = 0.0;
                        LocalTime currentTime = LocalTime.now();
                        LocalDate currentDate = LocalDate.now();
                        int hour = currentTime.getHour();
                        int minute = currentTime.getMinute();
                        int second = currentTime.getSecond();
                        int day_of_week = currentDate.getDayOfWeek().getValue();
                        ArrayList<String> days = new ArrayList<>(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"));
                        int day_of_week_int_value = 0;

                        // LIMITATION A LA LIGNE C1
                        if(Objects.equals(nom_bus, "C1") && Objects.equals(sens, "0")) {
                            String concated_hour = String.valueOf(hour);
                            String concated_minute = String.valueOf(minute);
                            String concated_seconds = String.valueOf(second);

                            if(hour <= 9) {
                                concated_hour = "0".concat(String.valueOf(hour));
                            }

                            if(minute <= 9) {
                                concated_minute = "0".concat(String.valueOf(minute));
                            }

                            if(second <= 9) {
                                concated_seconds = "0".concat(String.valueOf(second));
                            }

                            String _concated_ = String.valueOf(concated_hour) + ":" + concated_minute + ":" + concated_seconds;
                            retrieved_hashmap_from_api_controller = APIController.getSimulationFlow(nom_bus, sens, days.get(day_of_week-1), _concated_);
                            if(retrieved_hashmap_from_api_controller != null) {
                                for (Map.Entry<String, Double> entry : retrieved_hashmap_from_api_controller.entrySet()) {
                                    if (entry.getValue() > filling_proba) {
                                        filling_proba = entry.getValue();
                                        filling_level = entry.getKey();
                                    }
                                }
                            }
                        }

                        String FormatedStringFeature = """
                                {
                                "type" : "Feature",
                                    "id" : """ + i + ",\n" + """
                                    "properties" : {
                                        "icon" : "bus",
                                        "line" : \" """ + nom_bus + "\"" + ",\n"+ """
                                        \t"nextindex" : \"""" + returned_splited[1] + "\",\n" + """
                                        \t"sens" : \"""" + sens + "\",\n" + """
                                        \t"filling_level" : \"""" + filling_level + "\",\n" + """
                                        \t"filling_proba" : \"""" + filling_proba + "\"\n" + """
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
        } else {
            return "{}";
        }
        FormatedStringHeader += FormatedStringContent;
        FormatedStringHeader += FormatedStringFooter;
        return FormatedStringHeader;
    }

    // For future purposes

    /**
     * Print dans la console BusNumber
     */
    public void printHashMap() {
        for(String name : BusNumber.keySet()) {
            String key = name;
            int val = BusNumber.get(key);
            System.out.println(key + " - " + val);
        }
    }

}