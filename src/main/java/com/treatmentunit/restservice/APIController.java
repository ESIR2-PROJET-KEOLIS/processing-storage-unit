package com.treatmentunit.restservice;

import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

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


        /**
         * CREATION DE TABLE :
         *
         *
         * CREATE TABLE simulation_en_tout_heure AS SELECT * FROM (SELECT t.trip_id, min_departure_time, max_arrival_time, pg.tab_coordonnes, r.route_short_name, monday, tuesday, wednesday, thursday, friday, saturday, sunday
         * FROM (SELECT trip_id, MIN(departure_time) AS min_departure_time, MAX(arrival_time) AS max_arrival_time FROM stop_times
         * GROUP BY trip_id) as custom, trips t, calendar c, routes r, parcours_geo pg
         * WHERE custom.trip_id = t.trip_id AND t.service_id = c.service_id AND t.route_id = r.route_id AND c.service_id = t.service_id AND shape_id = pg.parcours_lignes_bus_star_id) as cus;
         */

        /* String sql_req = """
                SELECT * FROM (SELECT t.trip_id, min_departure_time, max_arrival_time, pg.tab_coordonnes 
                FROM (SELECT trip_id, MIN(departure_time) AS min_departure_time, MAX(arrival_time) AS max_arrival_time FROM stop_times 
                GROUP BY trip_id) as custom, trips t, calendar c, routes r, parcours_geo pg 
                WHERE custom.trip_id = t.trip_id AND t.service_id = c.service_id AND """ + " " + day + " = '1' " + """ 
                AND t.route_id = r.route_id AND c.service_id = t.service_id AND shape_id = pg.parcours_lignes_bus_star_id 
                AND r.route_short_name = """ + "'" + line + "') as cus WHERE TIME('" + " " + hour +  "')" + """ 
                BETWEEN min_departure_time and max_arrival_time;
                """;
        */

        String sql_req = "SELECT * FROM simulation_en_toute_heure WHERE route_short_name = '" + line + "' AND " + day  + " = '1' AND TIME(' " + hour + "') BETWEEN min_departure_time AND max_arrival_time;\n";
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
}