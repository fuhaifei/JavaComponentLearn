package com.fhf.rabbitmq.workqueues;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.TreeSet;

public class Receiver {
    private final static String QUEUE_NAME = "hello";
    private final static String MQ_HOST = "192.168.190.100";

    private static class Worker extends Thread{
        private int workerNum;
        private Connection connection;
        private int totalTime;
        public Worker(int workerNum, Connection connection){
            this.workerNum = workerNum;
            this.connection = connection;
        }

        @Override
        public void run() {
            System.out.println("worker:" + workerNum + " ready to work");
            try {
                //创建channel，并初始化队列
                Channel channel = connection.createChannel();
                if(workerNum == 1){
                    channel.basicQos(3);
                }else{
                    channel.basicQos(2);
                }
                channel.queueDeclare(QUEUE_NAME,false, false, false, null);
                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        String[] work = new String(message.getBody()).split(" ");
                        System.out.printf("[receive]worker:%d, get work %s time:%s%n", workerNum, work[0],work[1]);
                        try {
                            Thread.sleep(Integer.parseInt(work[1]) * 1000L);
                            if(workerNum == 1){
                                Thread.sleep(30000);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        System.out.println(String.format("[finish]worker:%d, finish work %s time:%s", workerNum, work[0],
                                totalTime += Integer.parseInt(work[1])));

                    }
                };

                channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args)throws Exception {
        //初始化连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setUsername("test_user");
        factory.setPassword("123");
        Connection connection = factory.newConnection();

        //创建两个线程
        for(int i = 1;i < 3;i++){
            new Worker(i, connection).start();
        }
    }
}
