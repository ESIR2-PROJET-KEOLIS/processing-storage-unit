package com.treatmentunit.restservice;

import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.formating.DataFormating;
import com.treatmentunit.simulation.HttpAPIRequests;
import com.treatmentunit.simulation.OptimisationAndFormating;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@RestController
public class APIController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();
    static DatabaseBinding databaseBinding = new DatabaseBinding();

    static OptimisationAndFormating optAndForm = new OptimisationAndFormating();

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

    @GetMapping("/allpaths")
    public static String parcoursArray() throws SQLException, InterruptedException {
        String REQ = "SELECT DISTINCT nomcourtligne, tab_coordonnes, sens FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and type=\'Principal\'";
        ArrayList<ArrayList<String>> fetched = databaseBinding.requestFetchNColumns(REQ);
        return optAndForm.convertFromArrayListOfArrayListsToJSON(fetched);
    }

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

    @GetMapping("/theoricposition")
    public static String theoricPosition(@RequestParam(value = "line") String line, @RequestParam(value = "hour") String hour, @RequestParam(value = "day") String day) throws SQLException, InterruptedException {

        String ret = "";
        String sql_req = "SELECT DISTINCT * FROM simulation_en_toute_heure WHERE route_short_name = '" + line + "' AND " + day  + " = '1' AND TIME(' " + hour + "') BETWEEN min_departure_time AND max_arrival_time;\n";
        String theorical_location = "";
        //System.out.println(sql_req);

        ArrayList<ArrayList<String>> val = databaseBinding.requestFetchNColumns(sql_req);
        if(val.size() != 0) {
            theorical_location = optAndForm.getTheoricalLocationPerHour(val, hour);
        } else {
            return "[!] Empty return, can't process theorical positions on API call.";
        }
        return theorical_location;
    }


    @GetMapping("/flowsimulation")
     public static synchronized String getSimulationFlow(@RequestParam("line") String line, @RequestParam("sens") String sens, @RequestParam("day") String day, @RequestParam("hour") String hour) throws SQLException, InterruptedException, IOException {

        String ret = "{ \"simulation_flow\" = ";

        ArrayList<String> days = new ArrayList<>(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"));

        // ---------------------------- Not on start ! ----------------------------

        // Number of buses
        String sql_req_bus_count = "SELECT DISTINCT COUNT(*) FROM simulation_en_toute_heure WHERE " + day + " = '1' AND TIME(' " + hour + "') BETWEEN min_departure_time AND max_arrival_time AND route_short_name = '" + line + "';\n";
        String fetched = databaseBinding.requestFetchSingleValue(sql_req_bus_count);
        double number_of_buses_currently = (float)Double.parseDouble(fetched);

        // Mean of Bus Delays
        HashMap<String, Integer> delaysHashmap = DataFormating.getDelaysHashMap();
        int size = delaysHashmap.size();
        //System.out.println("SIIIIZIZZZE : " + size);
        int additionOfDelays = 0;
        Set<String> keys = delaysHashmap.keySet();
        for(String value : keys) {
            additionOfDelays += delaysHashmap.get(value);
        }

        // Line Length
        String sql_req_line_length = "SELECT DISTINCT longueur FROM `parcours_lignes_bus_star` WHERE nomcourtligne = '" + line + "' AND sens = '" + sens + "' AND type='Principal'";
        double fetched_line_length = (float)Double.parseDouble(databaseBinding.requestFetchSingleValue(sql_req_line_length));

        // Average distance
        String theoric_pos = theoricPosition(line, hour, day);
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

        try {
            avg_distance = (additionOfDistances/number_of_buses);
            avg_time_diff = additionOfDelays/size;
            bus_count = number_of_buses_currently;
            lengthOfLine = fetched_line_length;
            day_d = (double)days.indexOf(day);

        } catch (ArithmeticException e) {
            System.out.println("[!] Veuillez relancer le collecteur !");
            return "[/!\'] !!! Veuillez relancer le collecteur !!!";
        }

        ret += "}";


        String debug = "avg_distance = " + avg_distance + " / avg_time_diff = " + avg_time_diff + " / bus_count = " + bus_count + " / lengthOfLine = " + lengthOfLine + " / day_d = " + day_d;
        System.out.println(debug);

        HttpAPIRequests httpAPIRequests = new HttpAPIRequests();
        String reponse = httpAPIRequests.requestToAIAPIforFlowSimulation(line, sens, avg_distance, avg_time_diff, number_of_buses, lengthOfLine, day_d);
        //System.out.println("REPOOOOONSE DE AIIII : " + reponse);

        JSONObject api_response = new JSONObject(reponse);

        // TEST
        return "{}";
    }

}