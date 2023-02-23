package com.treatmentunit.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.treatmentunit.formating.DataFormating;

import java.util.concurrent.TimeUnit;

public class RmqListener {

    protected String host = "";
    protected String username = "";
    protected String password = "";

    private DataFormating dataFormating = new DataFormating();

    RmqPublisher rmqPublisher = null;
    Channel publisherChannel = null;

    public RmqListener(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public RmqListener(String host) {
        this.host = host;
    }

}
