package com.treatmentunit.rabbitmq.client;

import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.treatmentunit.database.DatabaseBinding;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.Assert.assertEquals;

public class RmqListenerTest {

    @Test
    public void RmqListenerGTFSBusyTest() {
        RmqListenerGTFS rmqListenerGTFS = new RmqListenerGTFS("localhost", "guest", "guest");
        rmqListenerGTFS.run();
        boolean busy_state = RmqListenerGTFS.isBusy();
        assertEquals(true, busy_state);
    }

    @Test
    public void RmqListenerParcoursConnectionTest() {
        RmqListenerPARCOURS rmqListener = new RmqListenerPARCOURS("localhost", "guest", "guest");
        rmqListener.run();
        Assert.assertNotNull(rmqListener.getConnection());
        Assert.assertNotNull(rmqListener.getChannel());
    }

    @Test(expected = RuntimeException.class)
    public void RmqListenerParcoursInvalidHostTest() throws Exception {
        RmqListenerPARCOURS rmqListener = new RmqListenerPARCOURS("locolhost");
        rmqListener.run();
    }

    @Test
    public void RmqListenerParcoursBusyTest() {
        RmqListenerPARCOURS rmqListenerParcours = new RmqListenerPARCOURS("localhost", "guest", "guest");
        rmqListenerParcours.run();
        boolean busy_state = RmqListenerPARCOURS.isBusy();
        assertEquals(true, busy_state);
    }

    @Test
    public void RmqPublisherConnectionTest() throws IOException, TimeoutException {
        RmqPublisher publisher = new RmqPublisher("localhost", "guest", "guest");
        Channel channel = publisher.setUpPublisherConnectionAndChannel();
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
    }

    @Test
    public void RmqPublisherGetTargetQueueTest() {
        RmqPublisher publisher = new RmqPublisher("localhost", "guest", "guest");
        assertEquals(RmqPublisher.TARGET_QUEUE, publisher.getTargetQueue());
    }

    @Test
    public void RmqPublishersetUpPublisherConnectionAndChannelNotNullTest() throws IOException, TimeoutException {
        RmqPublisher publisher = new RmqPublisher("localhost", "guest", "guest");
        Channel ch = publisher.setUpPublisherConnectionAndChannel();
        Assert.assertNotNull(ch);
    }

    @Test(expected = UnknownHostException.class)
    public void RmqPublishersetUpPublisherConnectionUnknownHostTest() throws IOException, TimeoutException {
        RmqPublisher publisher = new RmqPublisher("locolhost", "aaa", "bbb");
        Channel ch = publisher.setUpPublisherConnectionAndChannel();
    }

    @Test
    public void rmqListenerPositionAllBusConnectionTest() {
        RmqListenerPositionAllBus rmqListenerPositionAllBus = new RmqListenerPositionAllBus("localhost", "guest", "guest");
        Assert.assertNotNull(rmqListenerPositionAllBus);
    }


}
