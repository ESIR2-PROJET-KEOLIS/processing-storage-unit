package com.treatmentunit.simulation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.*;

public class OptimisationAndFormating {

    private static CopyOnWriteArrayList<String> arrangedCoords = new CopyOnWriteArrayList<>();

    /**
     * @param a -> Lat1 a
     * @param b -> Lat2 b
     * @param c -> Long1 c
     * @param d -> Long2 d
     * @return distance en mètres
     */
    public static double distanceViaLatEtLong(double a, double b, double c, double d) {
        double resultat = 0;
        double R = 6371 * 1000;
        double phi_a_rad = a * (Math.PI / 180);
        double phi_b_rad = b * (Math.PI / 180);
        double delta_phi_rad = (b - a) * (Math.PI / 180);
        double delta_alpha = (d - c) * (Math.PI / 180);

        double f1 = Math.sin(delta_phi_rad / 2) * Math.sin(delta_phi_rad / 2)
                + Math.cos(phi_a_rad) * Math.cos(phi_b_rad) * Math.sin(delta_alpha / 2) * Math.sin(delta_alpha / 2);

        double f2 = 2 * atan2(Math.sqrt(f1), Math.sqrt(1 - f1));

        resultat = R * f2;
        return resultat;
    }

    /**
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return Distance entre les deux points en kilomètres
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;  // Earth's radius in kilometers

        double lat1_rad = Math.toRadians(lat1);
        double lon1_rad = Math.toRadians(lon1);
        double lat2_rad = Math.toRadians(lat2);
        double lon2_rad = Math.toRadians(lon2);

        double delta_lat = lat2_rad - lat1_rad;
        double delta_lon = lon2_rad - lon1_rad;

        double a = Math.pow(Math.sin(delta_lat / 2), 2) + Math.cos(lat1_rad) * Math.cos(lat2_rad) * Math.pow(Math.sin(delta_lon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


    /**
     * Méthode qui formate un chemin pour le rendre plus facile a manipuler et moins lourd.
     * @param input String contenant un tableau avec toutes les coordonnées d'un chemin
     * @return CopyOnWriteArrayList de string contenant le même chemin simplifier avec une inversion des longitude et latitude pour chaque point.
     */
    public static CopyOnWriteArrayList<String> FormatingAndInversing(String input) {
        synchronized (arrangedCoords) {
            if (!input.isEmpty()) {

                double lastPoint_lat = 0;
                double lastPont_long = 0;


                input = input.substring(1, input.length() - 1);
                input = input + ",";

                String[] splitedCoordsList = input.split("],");
                for (String s : splitedCoordsList) {
                    String splitedCoordsSub = s.trim().substring(1);
                    String[] splitedCoords = splitedCoordsSub.split(",");
                    String formated = splitedCoords[1] + "," + splitedCoords[0];
                    formated = formated.trim();

                    int DELTA = 10;
                    if (distanceViaLatEtLong(Double.parseDouble(splitedCoords[1]), lastPoint_lat, Double.parseDouble(splitedCoords[0]), lastPont_long) > DELTA) {

                        arrangedCoords.add(formated);

                        lastPoint_lat = Double.parseDouble(splitedCoords[1]);
                        lastPont_long = Double.parseDouble(splitedCoords[0]);
                    }
                }
            }
        }

        return arrangedCoords;
    }

    /**
     *
     * @param input Le chemin d'une ligne de bus
     * @param current_bus_pos la position actuelle d'un bus
     * @return un string contenant le chemin simplifier suivie de la prochaine coordonnée du bus.
     */
    public String getOutput(String input, String current_bus_pos) {

        synchronized (arrangedCoords) {

            FormatingAndInversing(input);

            String beg = "[";
            for (String element : arrangedCoords) {
                element = "[" + element + "],";
                beg += element;
            }
            beg = beg.substring(0, beg.length() - 1);
            beg += "]";

            double nearest_point = 999999999;
            int idx_of_nearest_point = 0;

            if (!Objects.equals(current_bus_pos, "")) {
                current_bus_pos = current_bus_pos.substring(1, current_bus_pos.length() - 1);
                String[] dos = current_bus_pos.split(",");
                double src_lat = Double.parseDouble(dos[0]);
                double src_lon = Double.parseDouble(dos[1]);
                //System.out.println("DEBUG " + src_lat + " " + src_lon);

                for (int i = 0 ; i < arrangedCoords.size() ; i++) {
                    String element = arrangedCoords.get(i);
                    String[] element_splited = element.split(",");
                    double dst_lat = Double.parseDouble(element_splited[0]);
                    double dst_lon = Double.parseDouble(element_splited[1]);
                    double tocompare = distanceViaLatEtLong(src_lat, dst_lat, src_lon, dst_lon);

                    if (nearest_point > tocompare) {
                        nearest_point = tocompare;
                        if(idx_of_nearest_point != arrangedCoords.indexOf(element)) {
                            idx_of_nearest_point = arrangedCoords.indexOf(element);
                        } else {
                            idx_of_nearest_point = idx_of_nearest_point + 1;
                            //System.out.println("Point le plus proche: " + arrangedCoords.get(idx_of_nearest_point));

                            String element_dbg = arrangedCoords.get(idx_of_nearest_point);
                            String[] element_dbg_split = element_dbg.split(",");
                            double dst_lat1_dbg = Double.parseDouble(element_dbg_split[0]);
                            double dst_lon1_dbg = Double.parseDouble(element_dbg_split[1]);
                            nearest_point = distanceViaLatEtLong(src_lat, dst_lat1_dbg, src_lon, dst_lon1_dbg);
                        }
                    }
                }
                if (nearest_point == 999999999) {
                    /*
                    System.out.println("Taille du tableau : " + arrangedCoords);
                    System.out.println("Taille du tableau : " + arrangedCoords.size());
                    System.out.println("@Param input : " + input);
                    System.out.println("@Param Current Bus Pos : " + current_bus_pos);
                    */
                    System.out.println("[!] ERROR: Nearest coords for the current position have not been processed.");
                }
            }
            arrangedCoords.clear();
            return beg + ";" + idx_of_nearest_point;
        }
    }

    /**
     * Renvoie une list de localTime repprésentant n valeur egalement réppartie entre start et end
     * @param start
     * @param end
     * @param n
     * @return
     */
    public static List<LocalTime> generateTimestamps(LocalTime start, LocalTime end, int n) {
        List<LocalTime> timestamps = new ArrayList<>();
        long duration = end.toSecondOfDay() - start.toSecondOfDay();
        long interval = duration / (n + 1);
        for (int i = 1; i <= n; i++) {
            int seconds = (int)(start.toSecondOfDay() + interval * i);
            timestamps.add(LocalTime.ofSecondOfDay(seconds));
        }
        return timestamps;
    }

    /**
     * Renvoie un JSON contenant les coordonnés de la position théorique d'un bus à une heure précise ainsi que la coordonnée
     * de son prochain point dans le tableau de points optimisés.
     * @param input_tab
     * @param target
     * @return
     */
    synchronized public String getTheoricalLocationPerHour(ArrayList<ArrayList<String>> input_tab, String target) {

        ArrayList<LocalTime> _LOCALTIMES_ = new ArrayList<>();
        // Hour format : "14:25:00"
        //System.out.println(input_tab.size());

        String res = "[\n";

        for(int i = 0 ; i < input_tab.size() ; i++) {

            res += "\t{";
            res += "\t\t\"id\" : " + i + ",\n";

            int index_of_nearest = 0;
            int index_of_nearest_opti = 0;

            String jsonArrayString = input_tab.get(i).get(3);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ArrayList<Double>>>(){}.getType();
            ArrayList<ArrayList<Double>> initial_path = gson.fromJson(jsonArrayString, listType);
            CopyOnWriteArrayList OPTIMISED_PATH = FormatingAndInversing(jsonArrayString);
            int N = initial_path.size();
            //System.out.println(arrayList);

            String[] start_f = input_tab.get(i).get(1).split(":");
            String[] end_f = input_tab.get(i).get(2).split(":");
            String direction_id = input_tab.get(i).get(12);
            res += "\t\t\"sens\" : " + direction_id + ",\n";
            //res +=

            LocalTime start = LocalTime.of(Integer.parseInt(start_f[0]), Integer.parseInt(start_f[1]), Integer.parseInt(start_f[2]));
            LocalTime end = LocalTime.of(Integer.parseInt(end_f[0]), Integer.parseInt(end_f[1]), Integer.parseInt(end_f[2]));
            List<LocalTime> timestamps = generateTimestamps(start, end, N);
            List<LocalTime> timestamps_opti = generateTimestamps(start, end, N);

                /*
                System.out.println("SIZE OF INIT PATH: " + initial_path.size());
                System.out.println("SIZE OF TIMESTAMPS: " + timestamps.size());
                */

            LocalTime targetTime = LocalTime.parse(target);

            LocalTime closestTime = null;
            LocalTime closestTime_opti = null;
            int closestDiff = Integer.MAX_VALUE;
            int closestDiff_opti = Integer.MAX_VALUE;

            for (LocalTime time : timestamps) {

                int diff = Math.abs(targetTime.toSecondOfDay() - time.toSecondOfDay());

                if (closestTime == null || diff < closestDiff) {
                    closestTime = time;
                    closestDiff = diff;
                }
            }

            for (LocalTime time : timestamps_opti) {

                int diff = Math.abs(targetTime.toSecondOfDay() - time.toSecondOfDay());

                if (closestTime_opti == null || diff < closestDiff_opti) {
                    closestTime_opti = time;
                    closestDiff_opti = diff;
                }
            }

            index_of_nearest = timestamps.indexOf(closestTime);
            index_of_nearest_opti = timestamps_opti.indexOf(closestTime_opti);
            //System.out.println("LA COOORD : " + initial_path.get(index_of_nearest));
            res += "\t\t\"position\" : " + initial_path.get(index_of_nearest).toString() + ",\n";
            //res += "\t\t\"position_opti\" : [" + OPTIMISED_PATH.get(index_of_nearest_opti).toString() + "],\n";
            //res += "\t\t\"index_in_opti\" : " + index_of_nearest_opti + ",\n";

            //System.out.println("TEST: " + initial_path.get(index_of_nearest).toString());

            String r = initial_path.get(index_of_nearest).toString().substring(1, initial_path.get(index_of_nearest).toString().length()-1);
            String[] reverse = r.split(",");


            // !! getOutput() se charge de l'optimisation !!
            res += "\t\t\"next_index_opti\" : " + getOutput(jsonArrayString, "[" + reverse[1] + "," + reverse[0] + "]").split(";")[1] + ",\n";
            res += "\t\t\"filling_level\" :  \"N/A\",\n";
            res += "\t\t\"filling_proba\" :  0.0\n";
            //System.out.println("GetOutput renvoie le résultat suivant : " + getOutput(jsonArrayString, "[" + reverse[1] + "," + reverse[0] + "]"));

            if(i != input_tab.size()-1) {
                res += "\t},\n";
            } else {
                res += "\t}\n";
            }

        }

        res += "]";

        return res;
    }

    /**
     * Mise en forme d'un tableau de tableau de string en format JSON.
     * @param input ArrayList < ArrayList < String>>
     * @return String des informations de l'input formaté en JSON
     */
    public String convertFromArrayListOfArrayListsToJSON(ArrayList<ArrayList<String>> input) {

        String result = "{\n";

        synchronized (input) {
            for(ArrayList<String> val : input) {
                synchronized (val) {
                    for(int i = 0 ; i < val.size() ; i++) {
                        result += """
                            \n
                            \t{
                                \t\tline: \"""" + val.get(0) + "\",\n\n" + """
                                \t\tsens: \"""" + val.get(2) + "\",\n\n" + """
                                \t\ttraject:  """ + OptimisationAndFormating.FormatingAndInversing(val.get(1)) +  "\n\n";
                        if(i != val.size()-1) {
                            result += "\t},\n";
                        } else {
                            result += "\t}\n";
                        }
                    }
                }
            }
        }

        result += "\n}";

        return result;
    }

}