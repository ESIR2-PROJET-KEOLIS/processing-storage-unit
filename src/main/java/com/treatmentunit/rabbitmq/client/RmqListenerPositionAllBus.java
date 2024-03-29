package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.treatmentunit.formating.DataFormating;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Classe qui gère la queue "PositionAllBus" de rabbitMQ
 * Utilisé pour récupérer en direct la position des bus
 */
public class RmqListenerPositionAllBus extends RmqListener implements Runnable{

    private final static String QUEUE_NAME = "PositionAllBus";
    private DataFormating dataFormating = new DataFormating();

    public RmqListenerPositionAllBus(String host) {
        super(host);
    }

    public RmqListenerPositionAllBus(String host, String username, String password) {
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
        Connection connection = null;
        Channel channel = null;
        if(username != "" && password != "") {
            rmqPublisher = new RmqPublisher(host, username, password);
        } else {
            rmqPublisher = new RmqPublisher(host);
        }

        try {
            publisherChannel = rmqPublisher.setUpPublisherConnectionAndChannel();
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[*] Listening on " + QUEUE_NAME + "...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(!RmqListenerPARCOURS.isBusy() && !RmqListenerGTFS.isBusy()) {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                if (message.startsWith("{")) {
                    System.out.println("[*] Raw JSON data received on " + QUEUE_NAME);
                    try {
                        rmqPublisher.PublishQueue(dataFormating.formatReceivedJSON(message), publisherChannel);
                        System.out.println("[*] JSON data formated and sent to " + host + " on queue " + rmqPublisher.getTargetQueue() + ".");
                    } catch (TimeoutException | SQLException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                System.out.println("[BUSY] THE DATABASE IS CURRENTLY BEING FILLED, NO DATA PROCESSED.");
            }
        };

        try {
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
