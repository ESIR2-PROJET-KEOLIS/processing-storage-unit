package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Classe qui gère l'envoi de messages sur différentes queues
 */
public class RmqPublisher {

    public static final String TARGET_QUEUE = "PositionAllBusProcessed";

    public String host = "";
    public String username = "";
    public String password = "";

    public RmqPublisher(String host)  {
        this.host = host;
    }

    public RmqPublisher(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * Ce connect à RabbitMQ et crée un Channel
     * @return Channel sur le RabbitMQ liée aux attributs
     * @throws IOException
     * @throws TimeoutException
     */
    public Channel setUpPublisherConnectionAndChannel() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }

    /**
     * Envoie un message sur la queue
     * @param msg String le message a envoyer
     * @param ch Channel le channel sur quel on envoie le message
     * @throws IOException
     * @throws TimeoutException
     */
    public void PublishQueue(String msg, Channel ch) throws IOException, TimeoutException {
        ch.queueDeclare(TARGET_QUEUE, true, false, false, null);
        ch.basicPublish("", TARGET_QUEUE, null, msg.getBytes(StandardCharsets.UTF_8));
    }

    public String getTargetQueue() { return TARGET_QUEUE; }

}
