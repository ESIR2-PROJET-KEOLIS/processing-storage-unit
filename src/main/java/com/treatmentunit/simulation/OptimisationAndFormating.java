package com.treatmentunit.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OptimisationAndFormating {

    private static CopyOnWriteArrayList<String> arrangedCoords = new CopyOnWriteArrayList<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();


    /**
     * @param a -> Lat1 a
     * @param b -> Lat2 b
     * @param c -> Long1 c
     * @param d -> Long2 d
     * @return distance en mètres
     */
    private static double distanceViaLatEtLong(double a, double b, double c, double d) {
        double resultat = 0;
        double R = 6371 * 1000;
        double phi_a_rad = a * (Math.PI / 180);
        double phi_b_rad = b * (Math.PI / 180);
        double delta_phi_rad = (b - a) * (Math.PI / 180);
        double delta_alpha = (d - c) * (Math.PI / 180);

        double f1 = Math.sin(delta_phi_rad / 2) * Math.sin(delta_phi_rad / 2)
                + Math.cos(phi_a_rad) * Math.cos(phi_b_rad) * Math.sin(delta_alpha / 2) * Math.sin(delta_alpha / 2);

        double f2 = 2 * Math.atan2(Math.sqrt(f1), Math.sqrt(1 - f1));

        resultat = R * f2;
        return resultat;
    }

    public static CopyOnWriteArrayList<String> FormatingAndInversing(String input) {
        synchronized (arrangedCoords) {
            if (!input.isEmpty()) {

                double lastPoint_lat = 0;
                double lastPont_long = 0;

                //  !!!! PARSE THE JSON TAB CONTAINING THE COORDS

                //if(input.charAt(input.length()-1) == ']') {
                //input = input.substring(1, input.length()-1);
                //}

                input = input.substring(1, input.length() - 1);
                input = input + ",";

                String[] splitedCoordsList = input.split("],");
                for (String s : splitedCoordsList) {
                    String splitedCoordsSub = s.trim().substring(1);
                    //System.out.println(splitedCoordsSub);
                    String[] splitedCoords = splitedCoordsSub.split(",");
                    String formated = splitedCoords[1] + "," + splitedCoords[0];
                    formated = formated.trim();
                    //System.out.println("formated out : " + formated);

                    int DELTA = 30;
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

    public enum Direction {
        NORD, EST, SUD, OUEST;
    }

    public Direction getDirection(double latitude, double longitude, double latitudeRef, double longitudeRef) {
        double deltaLatitude = latitude - latitudeRef;
        double deltaLongitude = longitude - longitudeRef;

        if (Math.abs(deltaLatitude) > Math.abs(deltaLongitude)) {
            return deltaLatitude > 0 ? Direction.NORD : Direction.SUD;
        } else {
            return deltaLongitude > 0 ? Direction.EST : Direction.OUEST;
        }
    }

   /* public String getOutput(String input, String current_bus_pos) {

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
                    System.out.println("[!] ERROR: Nearest coords for the current position have not been processed.");
                }
            }
            arrangedCoords.clear();
            return beg + ";" + idx_of_nearest_point;
        }
    } */

    public String getOutput2(String input, String current_bus_pos) {

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
                    System.out.println("[!] ERROR: Nearest coords for the current position have not been processed.");
                }
            }
            arrangedCoords.clear();
            return beg + ";" + idx_of_nearest_point;
        }
    }

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


    /**
     * Calcule la distance entre deux points d'un plan.
     *
     * @param x1 première coordonnée x
     * @param y1 première coordonnée y
     * @param x2 deuxième coordonnée x
     * @param y2 deuxième coordonée y
     * @return ((x1-x2)**2 + (y1-y2)**2)**0.5
     *
     */
    private double distance(double x1, double y1, double x2, double y2){
        return  Math.pow(Math.pow((x1-x2),2) + Math.pow((y1-y2),2), 0.5);
    }


    /**
     * Calcule la différence d'angle entre .
     *
     * @param x1 première coordonnée x
     * @param y1 première coordonnée y
     * @param x2 deuxième coordonnée x
     * @param y2 deuxième coordonée y
     * @return la valeurs absolu de la différence entre les deux angles.
     *
     */
    private double diff_abs_angle(double x1, double y1, double x2, double y2){
        double angl1 = Math.atan2(y1, x1);
        double angl2 = Math.atan2(y2, x2);
        return Math.abs(angl1-angl2);
    }



    /**
     * Renvoie la coordonnées de X en cartésien en fonctin de la latitude et longitude
     * @param lat 
     * @param lon 
     * @return la coordonées suivant x en cartésian
     */
    private double cartX(double lat, double lon){
        double rayT = 6300000;

        return rayT * Math.cos(lat) * Math.cos(lon);
    }

 /**
     * Renvoie la coordonnées de X en cartésien en fonctin de la latitude et longitude
     * @param lat 
     * @param lon 
     * @return la coordonées suivant y en cartésian
     */
    private double cartY(double lat, double lon){
        double rayT = 6300000;

        return rayT * Math.sin(lat) * Math.sin(lon);
    }



    /**
     * Calcule la différence d'angle entre .
     *
     * @param input
     * @param current_bus_pos
     * @return Une string avec un tableau contenant toute les position d'arrets ainsi que le prochaine index du bus
     *
     */
    public String getOutput(String input, String current_bus_pos) {

        String beg;
        int out_ind = 0;
        synchronized (arrangedCoords) {

            FormatingAndInversing(input);

            beg = "[";
            for (String element : arrangedCoords) {
                element = "[" + element + "],";
                beg += element;
            }
            beg = beg.substring(0, beg.length() - 1);
            beg += "]";


            if (!Objects.equals(current_bus_pos, "")) {
                current_bus_pos = current_bus_pos.substring(1, current_bus_pos.length() - 1);
                String[] dos = current_bus_pos.split(",");
                // ici nous récupperrons x et y qui sont leurs repprésentation
                double x = cartX(Double.parseDouble(dos[0]), Double.parseDouble(dos[0])) ; 
                double y = cartY(Double.parseDouble(dos[0]), Double.parseDouble(dos[0]));
                //System.out.println("DEBUG " + src_lat + " " + src_lon);

                double proch1 = 1000;
                int proch1_ind = 0;
                double proch2 = 1000;
                int proch2_ind = 0;


                for (int i = 0; i < arrangedCoords.size(); i++) {
                    String element = arrangedCoords.get(i);
                    String[] element_splited = element.split(",");
                    double x1 = cartX(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                    double y1 = cartY(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));

                    double dist = distance(x, y, x1, y1);

                    if (dist <= Math.max(proch1, proch2)) {
                        if (dist <= proch1) {
                            proch1 = dist;
                            proch1_ind = i;
                        } else {
                            proch2 = dist;
                            proch2_ind = i;
                        }
                    }
                    if (proch1_ind != 0 && proch2_ind != 0) {
                        element = arrangedCoords.get(proch1_ind);
                        element_splited = element.split(",");
                        x1 =  cartX(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        y1 =  cartY(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        double t1 = x - x1;
                        double t2 = y - y1;

                        element = arrangedCoords.get(proch1_ind - 1);
                        element_splited = element.split(",");
                        double x2 = cartX(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        double y2 = cartY(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        double t3 = x2 - x1;
                        double t4 = y2 - y1;

                        element = arrangedCoords.get(proch2_ind);
                        element_splited = element.split(",");
                        x1 = cartX(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        y1 = cartY(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        double t1b = x - x1;
                        double t2b = y - y1;

                        element = arrangedCoords.get(proch1_ind - 1);
                        element_splited = element.split(",");
                        x2 = cartX(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        y2 = cartY(Double.parseDouble(element_splited[0]), Double.parseDouble(element_splited[1]));
                        double t3b = x2 - x1;
                        double t4b = y2 - y1;
                        if (diff_abs_angle(t1, t2, t3, t4) < diff_abs_angle(t1b, t2b, t3b, t4b)) {
                            arrangedCoords.clear();
                            out_ind = proch1_ind;
                        } else {
                            arrangedCoords.clear();
                            out_ind = proch2_ind;
                        }
                    } else {
                        out_ind = 1;
                    }
                }
            }

        }
        System.out.println("ici je renvoie " + out_ind);
        return beg + ";" + out_ind;
    }
}
