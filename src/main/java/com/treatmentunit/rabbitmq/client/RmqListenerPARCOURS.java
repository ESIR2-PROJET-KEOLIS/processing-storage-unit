package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.formating.DataFormating;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class RmqListenerPARCOURS extends RmqListener implements Runnable{

    private final static String QUEUE_NAME = "PARCOURS";
    private Connection connection = null;
    private Channel channel = null;
    private DatabaseBinding databaseBinding = new DatabaseBinding();
    DataFormating dataFormating = new DataFormating();

    public RmqListenerPARCOURS(String host) {
        super(host);
    }
    public RmqListenerPARCOURS(String host, String username, String password) {
        super(host, username, password);
    }

    @Override
    public void run() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        if(username != "" && password != "") {
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);
        }

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[*] Listening on " + QUEUE_NAME + "...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[*] Raw JSON data received on " + QUEUE_NAME);
            StringBuilder giga_insert = new StringBuilder();
            StringBuilder coords_insert = new StringBuilder();
            if(message.startsWith("{")) {

                // TODO -> Truncate specific tables on both listeners !
                // TODO -> Metadata fetching function for API endpoints !
                /**
                 *                     TRUNCATE ` Parcours des lignes de bus du RÃ©seau star`;
                 *                     TRUNCATE `Parcour_geo`;
                 *
                 */
                databaseBinding.requestInsert("TRUNCATE parcours_lignes_bus_star");
                databaseBinding.requestInsert("TRUNCATE parcours_geo");

                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray = jsonObject.getJSONArray("records");
                System.out.println("[*] Processing ...");
                for(int i = 0 ; i < jsonArray.length() ; i++) {
                    ArrayList<String> result = dataFormating.formatReceivedJSON_PARCOURS(jsonArray.getJSONObject(i));
                    giga_insert.append("(");
                    for(int j = 0 ; j < result.size()-2 ; j++) {
                        String escaped = result.get(j).replace('\'', '-');
                        giga_insert.append('\'').append(escaped).append("',");
                        if(j == result.size()-3) {
                            giga_insert = new StringBuilder(giga_insert.substring(0, giga_insert.length() - 1));
                        }
                    }
                    giga_insert.append("),\n");

                    coords_insert.append("(")
                            .append("\'" + result.get(result.size()-2) + "\',")
                            .append("\'" + String.valueOf(i) + "\',")
                            .append("\'" + result.get(result.size()-1) + "\'")
                            .append("),\n");

                }

                /**
                 * https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-bus-topologie-parcours-td&q=&rows=50
                 */

                giga_insert = new StringBuilder(giga_insert.substring(0, giga_insert.length() - 2));
                coords_insert = new StringBuilder(coords_insert.substring(0, coords_insert.length()-2));
                String REQUEST_PARCOURS = "INSERT INTO parcours_lignes_bus_star VALUES " + giga_insert;
                String REQUEST_COORDS = "INSERT INTO parcours_geo VALUES " + coords_insert;

                databaseBinding.requestInsert(REQUEST_PARCOURS);
                databaseBinding.requestInsert(REQUEST_COORDS);

                giga_insert = new StringBuilder();
                coords_insert = new StringBuilder();
            }
            System.out.println("[*] Data received on PARCOURS successfully processed.");

        };

        try {
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
