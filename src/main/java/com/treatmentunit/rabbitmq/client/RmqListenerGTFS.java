package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.utils.ZipExtractor;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RmqListenerGTFS extends RmqListener implements Runnable{

    private final static String QUEUE_NAME = "GTFS";
    private Connection connection = null;
    private Channel channel = null;
    private DatabaseBinding databaseBinding = new DatabaseBinding();

    public RmqListenerGTFS(String host) {
        super(host);
    }

    public RmqListenerGTFS(String host, String username, String password) {
        super(host, username, password);
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            System.out.println("[*] Received URL : " + message);

            String FILE_NAME = "GTFS.zip";
            String FILE_NAME_DEZIP = "GTFS";

            InputStream in = new URL(message).openStream();
            System.out.println("[*] Début de la récupération de l'archive.");
            Files.copy(in, Paths.get("GTFS.zip"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[*] Fin de la récupération de l'archive.");
            System.out.println("[*] Archive " + FILE_NAME + " prête.");
            ZipExtractor zipExtractor = new ZipExtractor("GTFS.zip", FILE_NAME_DEZIP);

            databaseBinding.requestFetch("""
                    SET FOREIGN_KEY_CHECKS=0;\s
                    SELECT CONCAT('TRUNCATE TABLE ', table_schema, '.', table_name, ';')
                    FROM information_schema.tables
                    WHERE table_schema = 'mydb';
                    """);

            File folder = new File(FILE_NAME_DEZIP);
            if(folder.isDirectory()) {
                File[] files = folder.listFiles();
                if(files != null) {
                    for(File file : files) {
                        if(!file.isDirectory() && file.isFile() && file.exists() && file.getName().contains(".txt")) {
                            System.out.println("[*] Processing of file " + file.getName());
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME_DEZIP+"/"+file.getName()));
                                String line;
                                int line_count = 0;
                                reader.readLine();
                                String giga_insert = "";
                                while((line = reader.readLine()) != null) {

                                    // Line refactoring
                                    line = "(" + line + "),\n";
                                    giga_insert += line;

                                    if(line_count > 0) {

                                        if(line_count % 1000 == 0) {
                                            giga_insert = giga_insert.substring(0, giga_insert.length()-2);
                                            String table_name = file.getName().substring(0, file.getName().length()-4);
                                            databaseBinding.requestInsert("INSERT INTO " + table_name + " VALUES " + giga_insert);
                                            giga_insert = "";
                                        }
                                    }
                                    line_count += 1;
                                }
                                if(!giga_insert.isEmpty()) {
                                    giga_insert = giga_insert.substring(0, giga_insert.length()-2);
                                    String table_name = file.getName().substring(0, file.getName().length()-4);
                                    databaseBinding.requestInsert("INSERT INTO " + table_name + " VALUES " + giga_insert);
                                    giga_insert = "";
                                }
                                reader.close();
                            } catch (IOException e) {
                                System.out.println("[!] Erreur lors du traitement des données CSV de " + FILE_NAME_DEZIP + " !");
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("[*] Base de données remplie avec succès ! ");
                } else {
                    System.out.println("[!] Dossier " + FILE_NAME_DEZIP + " vide !");
                }
            } else {
                System.out.println("[!] Chemin invalide ! ");
            }
        };

        try {
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
