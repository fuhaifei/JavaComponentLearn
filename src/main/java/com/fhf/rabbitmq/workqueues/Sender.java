package com.fhf.rabbitmq.workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Sender {
    private final static String QUEUE_NAME = "hello";
    private final static String MQ_HOST = "192.168.190.100";



    public static void main(String[] args)throws Exception {
        //初始化连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setUsername("test_user");
        factory.setPassword("123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()){
            //创建队列
            channel.queueDeclare(QUEUE_NAME,false, false, false, null);
            //向队列中不间断的发送20条消息
            for(int i = 1;i <= 20;i ++ ){
                int workTime = new Random().nextInt(4) + 1;
                channel.basicPublish("",QUEUE_NAME, null, String.format("%d %d", i, workTime).getBytes(StandardCharsets.UTF_8));
                System.out.println("sender send message:"+ i+ " to" + QUEUE_NAME);
                Thread.sleep(200);
            }

        }
    }
}
