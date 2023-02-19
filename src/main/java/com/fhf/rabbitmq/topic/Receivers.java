package com.fhf.rabbitmq.topic;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receivers {

    private static class Receiver extends Thread{
        String routingKey;
        public Receiver(String routingKey){
            this.routingKey = routingKey;
        }

        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();
                String queueName = c.queueDeclare().getQueue();
                c.queueBind(queueName, "test_topic", routingKey);

                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        System.out.println("[receiver] " + routingKey + " :"+ new String(message.getBody()));
                    }
                };

                c.basicConsume(queueName, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) {
        //身高：高、中、低；肤色：黑、黄、白；性别：男、女
        String[] allKeys = {"高.黑.男", "#.女", "中.*.男", "#"};

        for (String key:allKeys){
            new Receiver(key).start();
        }
    }
}
