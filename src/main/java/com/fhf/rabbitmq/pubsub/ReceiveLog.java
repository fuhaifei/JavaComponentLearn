package com.fhf.rabbitmq.pubsub;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLog {

    private static class Receiver extends Thread{
        int receiverNum;

        public Receiver(int receiverNum){
            this.receiverNum = receiverNum;
        }

        @Override
        public void run(){
            try {
                Channel channel = RabbitMQUtils.getChannel();

                //生成一个临时队列
                String queName = channel.queueDeclare().getQueue();

                channel.queueBind(queName, "log_exchange", "");

                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        System.out.printf("receiver:%d receive log:%s\n",receiverNum, new String(message.getBody()));
                    }
                };
                channel.basicConsume(queName, deliverCallback, consumerTag -> {});

            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) {
        for(int i = 1;i < 3;i++){
            new Receiver(i).start();
        }
    }
}
