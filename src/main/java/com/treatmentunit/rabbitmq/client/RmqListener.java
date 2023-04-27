package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.treatmentunit.formating.DataFormating;

import java.util.concurrent.TimeUnit;

/**
 * La classe RmqListener qui stock les informations pour ce connecter a une queue RabbitMQ
 */
public class RmqListener {

    protected String host = "";
    protected String username = "";
    protected String password = "";

    private DataFormating dataFormating = new DataFormating();

    RmqPublisher rmqPublisher = null;
    Channel publisherChannel = null;

    /**
     *  Constructeur de RmqListener
     * @param host
     * @param username
     * @param password
     */
    public RmqListener(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * Changer l'attribut host
     * @param host
     */
    public RmqListener(String host) {
        this.host = host;
    }

}
