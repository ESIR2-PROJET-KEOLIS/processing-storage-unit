package com.treatmentunit.simulation;

import java.util.ArrayList;
import java.util.Objects;

public class OptimisationAndFormating {

    private static ArrayList<String> arrangedCoords = new ArrayList<>();

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

    public static ArrayList<String> FormatingAndInversing(String input) {

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
                    //System.out.println(formated+",");
                    lastPoint_lat = Double.parseDouble(splitedCoords[1]);
                    lastPont_long = Double.parseDouble(splitedCoords[0]);
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

    public String getOutput(String input, String current_bus_pos) {

        FormatingAndInversing(input);

        String beg = "[";
        for (String element : arrangedCoords) {
            element = "[" + element + "],";
            beg += element;
        }
        beg = beg.substring(0, beg.length() - 1);
        beg += "]";

        //System.out.println("[*] Fetched coords: " + arrangedCoords.size());

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
