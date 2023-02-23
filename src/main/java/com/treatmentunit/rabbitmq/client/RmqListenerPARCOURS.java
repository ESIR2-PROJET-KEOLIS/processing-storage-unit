package com.treatmentunit.rabbitmq.client;

public class RmqListenerPARCOURS extends RmqListener implements Runnable {

    private final static String QUEUE_NAME = "PARCOURS";

    public RmqListenerPARCOURS(String host) {
        super(host);
    }

    public RmqListenerPARCOURS(String host, String username, String password) {
        super(host, username, password);
    }

    @Override
    public void run() {

    }
}
