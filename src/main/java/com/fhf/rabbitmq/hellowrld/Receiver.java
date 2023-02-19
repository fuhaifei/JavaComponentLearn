package com.fhf.rabbitmq.hellowrld;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Receiver {
    private final static String QUEUE_NAME = "mirror_test";
    private final static String MQ_HOST = "192.168.190.100";

    public static void main(String[] args)throws Exception {
        //初始化连接并创建队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5674);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false, false, false, null);
        //等待消费到队列内容
        System.out.println("Receiver is waiting for message");
        DeliverCallback deliverCallback = new DeliverCallback() {
            @Override
            public void handle(String consumerTag, Delivery message) throws IOException {
                System.out.println(new String(message.getBody()));
                channel.basicAck(message.getEnvelope().getDeliveryTag(), true);
            }

        };
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }
}
