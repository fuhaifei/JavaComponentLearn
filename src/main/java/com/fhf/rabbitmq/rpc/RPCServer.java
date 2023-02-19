package com.fhf.rabbitmq.rpc;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RPCServer {

    private static class Server extends Thread{
        private int serverNum;
        private static final String RPC_QUE = "rpc_que";

        public Server(int serverNum){
            this.serverNum = serverNum;
        }

        private static int fbi(int n){
            if(n == 1){
                return 1;
            }
            int ppre = 0;
            int pre = 1;
            for(int i = 0;i < n;i++){
                int temp = ppre + pre;
                ppre = pre;
                pre = temp;
            }
            return pre;
        }

        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();
                //能者多劳
                c.basicQos(1);
                DeliverCallback d = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        c.basicPublish("", message.getProperties().getReplyTo(), new AMQP.BasicProperties.Builder()
                                        .correlationId(message.getProperties().getCorrelationId())
                                        .build(),
                                String.valueOf(fbi(Integer.parseInt(new String(message.getBody())))).getBytes(StandardCharsets.UTF_8));
                        c.basicAck(message.getEnvelope().getDeliveryTag(), false);
                        System.out.printf("server:%d finish task %s\n",serverNum, new String(message.getBody()));
                    }
                };
                c.basicConsume(RPC_QUE, false, d, consumerTag -> {});

            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }

        }

        public static void main(String[] args) {
            for(int i = 0;i < 3;i++){
                new Server(i + 1).start();
            }
        }

    }
}
