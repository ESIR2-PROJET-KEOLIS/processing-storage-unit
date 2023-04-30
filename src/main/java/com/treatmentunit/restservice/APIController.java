package com.treatmentunit.restservice;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.formating.DataFormating;
import com.treatmentunit.simulation.HttpAPIRequests;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Classe qui gère les endpoints
 */
@RestController
public class APIController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();
    static DatabaseBinding databaseBinding = new DatabaseBinding();
    static OptimisationAndFormating optAndForm = new OptimisationAndFormating();
    static List<JSONObject> FormatedTheoricalPositionWithPredictedFilling;

    /**
     * EndPoint pour récupérer le parcours simplifié d'une ligne de bus
     * @param line nomCourtLine dans la BDD
     * @param sens 0 ou 1 dans la base de donnée
     * @return le parcours "optimisé" c'est-à-dire avec moins de points
     * @throws IOException
     * @throws SQLException
     */
    @GetMapping("/optimizedpath")
    public static String parcours(@RequestParam(value = "line") String line, @RequestParam(value = "sens") String sens) throws IOException, SQLException {
        // 0 Aller
        // 1 Retour
        try {
            String REQ = "SELECT DISTINCT tab_coordonnes FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and nomcourtligne = '" + line + "' and sens = '" + sens + "\'" + " and type=\'Principal\'";
            String single_val = databaseBinding.requestFetchSingleValue(REQ);
            String returned = optAndForm.getOutput(single_val, "");
            String[] val = returned.split(";");
            return val[0];

        } catch (SQLException | InterruptedException sqlException) {
            System.out.println("[!] Erreur SQL !");
            sqlException.printStackTrace();
            return "Erreur SQL ! ";
        }
    }

    /**
     * Endpoint pour récupérer tous les chemins des lignes de bus
     * @return String formater en JSON contenant tous les chemins.
     * @throws SQLException
     * @throws InterruptedException
     */
    @GetMapping("/allpaths")
    public static String parcoursArray() throws SQLException, InterruptedException {
        String REQ = "SELECT DISTINCT nomcourtligne, tab_coordonnes, sens FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and type=\'Principal\'";
        ArrayList<ArrayList<String>> fetched = databaseBinding.requestFetchNColumns(REQ);
        return optAndForm.convertFromArrayListOfArrayListsToJSON(fetched);
    }

    /**
     * Endpoint qui renvoie la couleur d'une ligne de bus sur le réseau stars
     * @param line nomCourtLigne dans la BDD
     * @return String contenant la couleur
     * @throws SQLException
     * @throws InterruptedException
     */
    @GetMapping("/linecolor")
    public static String lineColor(@RequestParam(value = "line") String line ) throws SQLException, InterruptedException {
        String sql_req = "SELECT DISTINCT couleurtrace FROM `parcours_lignes_bus_star` WHERE nomcourtligne=\'" + line + "\'";
        String single_val = databaseBinding.requestFetchSingleValue(sql_req);
        return single_val;
    }

    /* SELECT * FROM stop_times WHERE TIME('05:30:00') BETWEEN departure_time and arrival_time ORDER BY `stop_id` ASC */

    /*

    SELECT * FROM stop_times, trips, routes, calendar WHERE stop_times.trip_id = trips.trip_id AND trips.route_id = routes.route_id AND routes.route_short_name = 'C1' AND trips.direction_id = 1 AND trips.service_id = calendar.service_id AND monday = '0' AND tuesday = '0' AND wednesday = '0' AND thursday = '0' AND saturday = '0' AND sunday = '0' AND friday='1' AND TIME('17:30:00') BETWEEN departure_time AND arrival_time ORDER BY `stop_id` ASC;

    SELECT * FROM parcours_geo, stop_times, trips, routes, calendar WHERE stop_times.trip_id = trips.trip_id AND trips.route_id = routes.route_id AND routes.route_short_name = 'C1' AND trips.direction_id = 1 AND trips.service_id = calendar.service_id AND monday = '0' AND tuesday = '0' AND wednesday = '0' AND thursday = '0' AND saturday = '0' AND sunday = '0' AND friday='1' AND parcours_geo.parcours_lignes_bus_star_id = trips.shape_id AND TIME('17:30:00') BETWEEN departure_time AND arrival_time ORDER BY `stop_id` ASC LIMIT 1;

     */

    /**
     * Endpoint qui renvoie une position théorique en fonction de la ligne de l'heure et du jour
     * @param line route_short_name dans la BDD
     * @param hour l'heure à laquelle on simule
     * @param day le jour de la simulation
     * @return une string contenant les positions théoriques.
     * @throws SQLException
     * @throws InterruptedException
     */
    @GetMapping("/theoricposition")
    public static String theoricPosition(@RequestParam(value = "line") String line, @RequestParam(value = "hour") String hour, @RequestParam(value = "day") String day) throws SQLException, InterruptedException {
        FormatedTheoricalPositionWithPredictedFilling = new ArrayList<>();
        String ret = "";
        String sql_req = "SELECT DISTINCT * FROM simulation_en_toute_heure WHERE route_short_name = '" + line + "' AND " + day  + " = '1' AND TIME(' " + hour + "') BETWEEN min_departure_time AND max_arrival_time;\n";
        String theorical_location = "";

        ArrayList<ArrayList<String>> val = databaseBinding.requestFetchNColumns(sql_req);
        if(val.size() != 0) {
            theorical_location = optAndForm.getTheoricalLocationPerHour(val, hour);
                try {
                    String filling_level = "N/A";
                    double filling_proba = 0.0;
                    //System.out.println(theorical_location);
                    JSONArray jsonArray = new JSONArray(theorical_location);
                    for(int i = 0 ; i < jsonArray.length() ; i++) {
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                        if(jsonObject.getInt("sens") == 0 && line.equals("C1")) {
                            HashMap<String, Double> fillingPredictions = getSimulationFlow(line, String.valueOf(Integer.valueOf(jsonObject.getInt("sens"))), day, hour, theorical_location);
                            if(fillingPredictions != null) {
                                //System.out.println("[DEBUG] Not null & size = " + fillingPredictions.size());
                                for (Map.Entry<String, Double> entry : fillingPredictions.entrySet()) {
                                    if (entry.getValue() > filling_proba) {
                                        filling_proba = entry.getValue();
                                        filling_level = entry.getKey();
                                    }
                                }
                                jsonObject.put("filling_level", filling_level);
                                jsonObject.put("filling_proba", filling_proba);
                                FormatedTheoricalPositionWithPredictedFilling.add(jsonObject);
                                String to_return = new JSONArray(FormatedTheoricalPositionWithPredictedFilling).toString();
                                System.out.println("---------------------- DEBUG ---------------------- ");
                                System.out.println("Returned theorical position : " + to_return);
                                System.out.println("--------------------------------------------------- ");
                                return to_return;
                            }
                        } else {
                            return theorical_location;
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return "[!] Empty return, can't process theorical positions on API call.";
    }

    /**
     * Un endpoint qui renvoie une string contenant toutes les vitesses moyennes des bus dans la direction 0
     * @return
     * @throws IOException
     */
    @GetMapping("/speed0")
    public static String BusSpeedSens0() throws IOException {
        return """
                {
                   "C7ex":59.02799999999999,
                   "32":16.34776470588235,
                   "34":37.846199999999996,
                   "C4":16.575913043478263,
                   "13":21.876872727272726,
                   "156ex":20.66895,
                   "56":20.875021276595746,
                   "210":21.503249999999998,
                   "52":28.88592,
                   "159ex":28.713000000000005,
                   "55":34.087142857142865,
                   "10":19.913172413793106,
                   "81":42.314068965517244,
                   "82":46.2015,
                   "68":39.75207692307692,
                   "65":35.26466666666667,
                   "59":26.862363636363632,
                   "178ex":23.818838709677415,
                   "61":31.789304347826086,
                   "62":30.7005,
                   "173ex":38.657999999999994,
                   "73":35.559599999999996,
                   "74":35.843032258064525,
                   "75":31.782719999999998,
                   "78":24.095166666666668,
                   "38":22.447200000000002,
                   "76":30.926108108108103,
                   "63":22.74872727272727,
                   "RPI-1":35.907599999999995,
                   "Ts82":26.407636363636364,
                   "Ts75":31.255826086956525,
                   "Ts67":27.89172972972973,
                   "Ts66":36.332689655172416,
                   "Ts61":20.63674285714286,
                   "Ts57":31.674642857142857,
                   "Ts54":24.20307692307692,
                   "Ts53":20,
                   "Ts52":30.382695652173915,
                   "Ts46":27.490956521739136,
                   "Ts45":28.1402,
                   "Ts44":22.6442,
                   "C7":59.977384615384615,
                   "Ts43":19.918758620689655,
                   "Ts41":25.385657142857145,
                   "Ts38":23.560714285714287,
                   "Ts34":23.653125,
                   "Ts22":17.223,
                   "Ts36":27.909,
                   "Ts33":22.27313513513513,
                   "Ts32":23.530772727272726,
                   "Ts20":29.254,
                   "Ts3":29.20589189189189,
                   "Ts1":22.354186046511625,
                   "Ts13":17.949391304347827,
                   "Ts12":25.030216216216214,
                   "Ts11":27.05562162162162,
                   "Ts10":28.122810810810815,
                   "Ts8":31.895785714285715,
                   "Ts7":37.47868965517242,
                   "Ts6":32.82396,
                   "Ts5":38.454,
                   "202":20.344153846153844,
                   "204":18.88217142857143,
                   "205":16.845428571428574,
                   "206":19.8525,
                   "208":14.298727272727271,
                   "211":30.076080000000005,
                   "212":27.58696551724138,
                   "216":17.161565217391303,
                   "218":19.72121739130435,
                   "221":19.687949999999997,
                   "222":17.776,
                   "223":18.8623125,
                   "224":15.796727272727273,
                   "225":24.832695652173914,
                   "226":28.48156097560976,
                   "227":29.900571428571432,
                   "228":19.536620689655177,
                   "229":26.92575,
                   "231":34.1654,
                   "232":8.5504,
                   "234":28.168571428571433,
                   "235":42.12709090909091,
                   "236":24.4692,
                   "237":32.75942857142857,
                   "239":22.680600000000002,
                   "241":26.342880000000005,
                   "242":40.20445161290323,
                   "243":27.725999999999996,
                   "53":29.2333125,
                   "153ex":28.347454545454543,
                   "N1":21.850125,
                   "N3":23.549599999999998,
                   "N4":22.8888,
                   "N5":22.580999999999996,
                   "TTZ":20,
                   "14":26.321000000000005,
                   "50":26.918333333333337,
                   "51":24.135545454545458,
                   "54":28.376615384615384,
                   "67":40.254400000000004,
                   "71":32.76527272727273,
                   "72":23.2071,
                   "77":17.089199999999998,
                   "a":20,
                   "b":20,
                   "39":30.740999999999996,
                   "90":31.38218181818182,
                   "Ts71":35.80145454545455,
                   "C3":18.31490909090909,
                   "155ex":30.376266666666666,
                   "201":27.67625,
                   "La Navette":20,
                   "154ex":28.175027027027024,
                   "214":17.316193548387098,
                   "Ts35":30.7312,
                   "Ts9":32.886833333333335,
                   "Ts37":22.190785714285713,
                   "219":20.28066666666667,
                   "245":20.632744186046512,
                   "238":23.36616,
                   "C6":15.795970588235294,
                   "12":19.366682926829267,
                   "91":45.7833,
                   "79":28.861833333333333,
                   "C5":17.554875,
                   "N2":18.457,
                   "80":40.11552,
                   "200":20.66965714285714,
                   "220":24.852272727272723,
                   "164ex":27.093,
                   "64":33.8124705882353,
                   "E1":20,
                   "240":49.099411764705884,
                   "C1":24.700363636363637,
                   "C2":19.095574468085104,
                   "11":23.11990243902439,
                   "Ts55":31.861826086956526,
                   "Ts91":17.677666666666667,
                   "213":26.2626,
                   "230":28.29790243902439,
                   "233":37.4032,
                   "Ts4":32.50736842105263,
                   "Ts51":28.96634482758621,
                   "E8":20,
                   "b relais":73.0685,
                   "E4":20,
                   "70":44.08941176470588,
                   "83":25.262375,
                   "37":20.49192,
                   "209":16.412571428571432,
                   "175ex":28.286076923076923,
                   "172ex":32.993647058823534,
                   "168ex":36.91264285714286,
                   "167ex":32.431636363636365,
                   "165ex":36.25038461538462,
                   "161ex":19.912636363636363,
                   "152ex":32.2335,
                   "Bzh7":20
                }
                """;
    }

    /**
     * Un endpoint qui renvoie une string contenant toutes les vitesses moyennes des bus dans la direction 1
     * @return
     * @throws IOException
     */
    @GetMapping("/speed1")
    public static String BusSpeedSens1() throws IOException {
        return """
                
                {
                   "C7ex":59.02799999999999,
                   "32":16.34776470588235,
                   "34":37.846199999999996,
                   "C4":16.575913043478263,
                   "13":21.876872727272726,
                   "156ex":20.66895,
                   "56":20.875021276595746,
                   "210":21.503249999999998,
                   "52":28.88592,
                   "159ex":28.713000000000005,
                   "55":34.087142857142865,
                   "10":19.913172413793106,
                   "81":42.314068965517244,
                   "82":46.2015,
                   "68":39.75207692307692,
                   "65":35.26466666666667,
                   "59":26.862363636363632,
                   "178ex":23.818838709677415,
                   "61":31.789304347826086,
                   "62":30.7005,
                   "173ex":38.657999999999994,
                   "73":35.559599999999996,
                   "74":35.843032258064525,
                   "75":31.782719999999998,
                   "78":24.095166666666668,
                   "38":22.447200000000002,
                   "76":30.926108108108103,
                   "63":22.74872727272727,
                   "RPI-1":35.907599999999995,
                   "Ts82":26.407636363636364,
                   "Ts75":31.255826086956525,
                   "Ts67":27.89172972972973,
                   "Ts66":36.332689655172416,
                   "Ts61":20.63674285714286,
                   "Ts57":31.674642857142857,
                   "Ts54":24.20307692307692,
                   "Ts53":20,
                   "Ts52":30.382695652173915,
                   "Ts46":27.490956521739136,
                   "Ts45":28.1402,
                   "Ts44":22.6442,
                   "C7":59.977384615384615,
                   "Ts43":19.918758620689655,
                   "Ts41":25.385657142857145,
                   "Ts38":23.560714285714287,
                   "Ts34":23.653125,
                   "Ts22":17.223,
                   "Ts36":27.909,
                   "Ts33":22.27313513513513,
                   "Ts32":23.530772727272726,
                   "Ts20":29.254,
                   "Ts3":29.20589189189189,
                   "Ts1":22.354186046511625,
                   "Ts13":17.949391304347827,
                   "Ts12":25.030216216216214,
                   "Ts11":27.05562162162162,
                   "Ts10":28.122810810810815,
                   "Ts8":31.895785714285715,
                   "Ts7":37.47868965517242,
                   "Ts6":32.82396,
                   "Ts5":38.454,
                   "202":20.344153846153844,
                   "204":18.88217142857143,
                   "205":16.845428571428574,
                   "206":19.8525,
                   "208":14.298727272727271,
                   "211":30.076080000000005,
                   "212":27.58696551724138,
                   "216":17.161565217391303,
                   "218":19.72121739130435,
                   "221":19.687949999999997,
                   "222":17.776,
                   "223":18.8623125,
                   "224":15.796727272727273,
                   "225":24.832695652173914,
                   "226":28.48156097560976,
                   "227":29.900571428571432,
                   "228":19.536620689655177,
                   "229":26.92575,
                   "231":34.1654,
                   "232":8.5504,
                   "234":28.168571428571433,
                   "235":42.12709090909091,
                   "236":24.4692,
                   "237":32.75942857142857,
                   "239":22.680600000000002,
                   "241":26.342880000000005,
                   "242":40.20445161290323,
                   "243":27.725999999999996,
                   "53":29.2333125,
                   "153ex":28.347454545454543,
                   "N1":21.850125,
                   "N3":23.549599999999998,
                   "N4":22.8888,
                   "N5":22.580999999999996,
                   "TTZ":20,
                   "14":26.321000000000005,
                   "50":26.918333333333337,
                   "51":24.135545454545458,
                   "54":28.376615384615384,
                   "67":40.254400000000004,
                   "71":32.76527272727273,
                   "72":23.2071,
                   "77":17.089199999999998,
                   "a":20,
                   "b":20,
                   "39":30.740999999999996,
                   "90":31.38218181818182,
                   "Ts71":35.80145454545455,
                   "C3":18.31490909090909,
                   "155ex":30.376266666666666,
                   "201":27.67625,
                   "La Navette":20,
                   "154ex":28.175027027027024,
                   "214":17.316193548387098,
                   "Ts35":30.7312,
                   "Ts9":32.886833333333335,
                   "Ts37":22.190785714285713,
                   "219":20.28066666666667,
                   "245":20.632744186046512,
                   "238":23.36616,
                   "C6":15.795970588235294,
                   "12":19.366682926829267,
                   "91":45.7833,
                   "79":28.861833333333333,
                   "C5":17.554875,
                   "N2":18.457,
                   "80":40.11552,
                   "200":20.66965714285714,
                   "220":24.852272727272723,
                   "164ex":27.093,
                   "64":33.8124705882353,
                   "E1":20,
                   "240":49.099411764705884,
                   "C1":24.700363636363637,
                   "C2":19.095574468085104,
                   "11":23.11990243902439,
                   "Ts55":31.861826086956526,
                   "Ts91":17.677666666666667,
                   "213":26.2626,
                   "230":28.29790243902439,
                   "233":37.4032,
                   "Ts4":32.50736842105263,
                   "Ts51":28.96634482758621,
                   "E8":20,
                   "b relais":73.0685,
                   "E4":20,
                   "70":44.08941176470588,
                   "83":25.262375,
                   "37":20.49192,
                   "209":16.412571428571432,
                   "175ex":28.286076923076923,
                   "172ex":32.993647058823534,
                   "168ex":36.91264285714286,
                   "167ex":32.431636363636365,
                   "165ex":36.25038461538462,
                   "161ex":19.912636363636363,
                   "152ex":32.2335,
                   "Bzh7":20
                }
                """;
    }

    //@GetMapping("/flowsimulation")

    /**
     * Une méthode qui renvoie le remplissage d'une ligne de bus en fonction de ça ligne, du sens et de la date / heure.
     * @param line route_short_name dans la BDD
     * @param sens sens dans la BDD
     * @param day
     * @param hour
     * @return HashMap < String, Double>
     * @throws SQLException
     * @throws InterruptedException
     * @throws IOException
     */
    public static synchronized HashMap<String, Double> getSimulationFlow(@RequestParam("line") String line, @RequestParam("sens") String sens, @RequestParam("day") String day, @RequestParam("hour") String hour, String theo_pos) throws SQLException, InterruptedException, IOException {

        ArrayList<String> days = new ArrayList<>(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"));

        // ---------------------------- Not on start ! ----------------------------

        // Number of buses
        String sql_req_bus_count = "SELECT DISTINCT COUNT(*) FROM simulation_en_toute_heure WHERE " + day + " = '1' AND TIME(' " + hour + "') BETWEEN min_departure_time AND max_arrival_time AND route_short_name = '" + line + "';\n";
        String fetched = databaseBinding.requestFetchSingleValue(sql_req_bus_count);
        double number_of_buses_currently = (float)Double.parseDouble(fetched);

        // Mean of Bus Delays
        HashMap<String, Integer> delaysHashmap = DataFormating.getDelaysHashMap();
        int size = delaysHashmap.size();
        int additionOfDelays = 0;
        Set<String> keys = delaysHashmap.keySet();
        for(String value : keys) {
            additionOfDelays += delaysHashmap.get(value);
        }

        // Line Length
        String sql_req_line_length = "SELECT DISTINCT longueur FROM `parcours_lignes_bus_star` WHERE nomcourtligne = '" + line + "' AND sens = '" + sens + "' AND type='Principal'";
        //double fetched_line_length = (float)Double.parseDouble(databaseBinding.requestFetchSingleValue(sql_req_line_length));
        String lineLengthValue = databaseBinding.requestFetchSingleValue(sql_req_line_length);
        double fetched_line_length = StringUtils.isNotBlank(lineLengthValue) ? Double.parseDouble(lineLengthValue) : 0.0;

        /*
        System.out.println("------------------------------------------------");
        System.out.println("sql_req_line_length: " + sql_req_line_length);
        System.out.println("fetched_line_length: " + fetched_line_length);
        System.out.println("------------------------------------------------");
        */



        // Average distance
        String theoric_pos = "";
        // Problème de récurssivité ici !!!!
        if(theo_pos == null) {
            theoric_pos = theoricPosition(line, hour, day);
        } else {
            theoric_pos = theo_pos;
        }

        JSONArray array_of_elements = new JSONArray(theoric_pos);
        int additionOfDistances = 0;
        int number_of_buses = array_of_elements.length();
        double last_lat = 0;
        double last_lng = 0;
        for(int i = 0 ; i < number_of_buses ; i++) {
            JSONArray coords = array_of_elements.getJSONObject(i).getJSONArray("position");
            if(i>1) {
                double lat = coords.getFloat(1);
                double lng = coords.getFloat(0);
                String frmtd_coord = lat + "," + lng;
                additionOfDistances += optAndForm.haversine(last_lat, last_lng, lat, lng);
                last_lat = lat;
                last_lng = lng;
            }
        }

        double avg_distance = 0;
        double avg_time_diff = 0;
        double bus_count = 0;
        double lengthOfLine = 0;
        double day_d = 0;
        double day_index_for_ai = 0;

        try {

            avg_distance = (additionOfDistances/number_of_buses);
            avg_time_diff = additionOfDelays/size;
            bus_count = number_of_buses_currently;
            lengthOfLine = fetched_line_length;
            day_d = (double)days.indexOf(day);
            if(day_d == 5) {
                day_index_for_ai = 1;
            } else if(day_d == 6) {
                day_index_for_ai = 2;
            }

        } catch (ArithmeticException e) {
            //e.printStackTrace();
            System.out.println("[!] Veuillez relancer le collecteur !");
            return null;
        }

        String debug = "avg_distance = " + avg_distance + " / avg_time_diff = " + avg_time_diff + " / bus_count = " + bus_count + " / lengthOfLine = " + lengthOfLine + " / day_d = " + day_d;

        HttpAPIRequests httpAPIRequests = new HttpAPIRequests();
        String reponse = httpAPIRequests.requestToAIAPIforFlowSimulation(line, sens, avg_distance, avg_time_diff, number_of_buses, lengthOfLine, day_index_for_ai);

        System.out.println("[DEBUG] API Response  : " + reponse);

        if(reponse.charAt(0) == '{') {
            JSONObject api_response = new JSONObject(reponse);
            HashMap<String, Double> filling_level_and_proba = new HashMap<>();

            for(String key : api_response.keySet()) {
                //System.out.println("key: " + key);
                //System.out.println("value : " + api_response.getDouble(key));
                BigDecimal bdValue = (BigDecimal) api_response.get(key);
                double doubleValue = bdValue.doubleValue();
                filling_level_and_proba.put(key, doubleValue);
            }
            return filling_level_and_proba;
        }

        return null;
    }

}