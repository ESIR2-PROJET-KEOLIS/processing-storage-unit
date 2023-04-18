package com.treatmentunit.simulation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpAPIRequests {

    public String requestToAIAPIforFlowSimulation(
            String line,
            String sens,
            double avg_distance,
            double avg_time_diff,
            double bus_count,
            double lengthOfLine,
            double day
    ) throws IOException {

        String url = "http://localhost:5001/predict/Nombus=" + line + "&Sens=" + sens;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java HttpURLConnection");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        Map<String, Double> data = Map.of(
                "avg_distance", avg_distance,
                "avg_time_diff", avg_time_diff,
                "bus_count", bus_count,
                "length", lengthOfLine,
                "day", day
        );

        StringBuilder dataString = new StringBuilder();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            if (dataString.length() != 0) {
                dataString.append("&");
            }
            dataString.append(entry.getKey());
            dataString.append("=");
            dataString.append(entry.getValue());
        }

        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] dataBytes = dataString.toString().getBytes(StandardCharsets.UTF_8);
            wr.write(dataBytes);
        }

        // Read response from server
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        System.out.println(con.getResponseCode());
        System.out.println(response.toString());

        return response.toString();

    }

}