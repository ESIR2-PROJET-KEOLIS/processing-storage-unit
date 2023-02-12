package com.treatmentunit.simulation;

import java.util.ArrayList;

public class OptimisationAndFormating {

    /**
     *
     * @param a -> Lat1 a
     * @param b -> Lat2 b
     * @param c -> Long1 c
     * @param d -> Long2 d
     * @return distance en mètres
     */
    public static double distanceViaLatEtLong(double a, double b, double c, double d) {
        double resultat = 0;
        double R = 6371*1000;
        double phi_a_rad = a*(Math.PI/180);
        double phi_b_rad = b*(Math.PI/180);
        double delta_phi_rad = (b-a) * (Math.PI/180);
        double delta_alpha = (d-c) * (Math.PI/180);

        double f1 = Math.sin(delta_phi_rad/2) * Math.sin(delta_phi_rad/2)
                + Math.cos(phi_a_rad) * Math.cos(phi_b_rad) * Math.sin(delta_alpha/2) * Math.sin(delta_alpha/2);

        double f2 = 2 * Math.atan2(Math.sqrt(f1), Math.sqrt(1-f1));

        resultat = R*f2;
        return resultat;
    }

    public static ArrayList<String> FormatingAndInversing(String input) {
        ArrayList<String> arrangedCoords = new ArrayList<>();

        double lastPoint_lat = 0;
        double lastPont_long = 0;

        if(input.charAt(input.length()-1) == ']') {
            input = input.substring(0, input.length()-1);
        }
        String[] splitedCoordsList = input.split("],");
        for (String s : splitedCoordsList) {
            String splitedCoordsSub = s.trim().substring(1);
            //System.out.println(splitedCoordsSub);
            String[] splitedCoords = splitedCoordsSub.split(",");
            String formated = splitedCoords[1]+","+splitedCoords[0];
            formated = formated.trim();
            if(distanceViaLatEtLong(Double.parseDouble(splitedCoords[1]), lastPoint_lat, Double.parseDouble(splitedCoords[0]), lastPont_long) > 30) {
                arrangedCoords.add(formated);
                System.out.println(formated+",");
                lastPoint_lat = Double.parseDouble(splitedCoords[1]);
                lastPont_long = Double.parseDouble(splitedCoords[0]);
            }
        }
        return arrangedCoords;
    }
}
