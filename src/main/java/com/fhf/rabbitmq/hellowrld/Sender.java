package com.fhf.rabbitmq.hellowrld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Sender {
    private final static String QUEUE_NAME = "mirror_test";
    private final static String MQ_HOST = "192.168.190.100";

    public static void main(String[] args)throws Exception {
        //初始化连接并创建队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5673);
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()){
            //创建队列
            channel.queueDeclare(QUEUE_NAME,false, false, false, null);
            //向默认交换器中发送消息，其中路由键等于队列名（因为默认交换器为直连型交换器）
            channel.basicPublish("",QUEUE_NAME, null, "hello world".getBytes(StandardCharsets.UTF_8));
            System.out.println("sender send message to " + QUEUE_NAME);
        }
    }
}
