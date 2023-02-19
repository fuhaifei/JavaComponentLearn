package com.fhf.rabbitmq.DLX;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DeathLetterDemo {
    //死信相关名称
    private static final String DLX = "DLX";
    private static final String DLX_QUE = "DLXQUE";
    private static final String DLX_KEY = "DLX";

    //普通队列相关名称
    private static final String NORMAL_X = "NORMAL_X";
    private static final String NORMAL_QUE = "NORMAL_QUE";
    private static final String NORMAL_KEY = "NORMAL_KEY";

    public static void init() throws IOException, TimeoutException {
        Channel c = RabbitMQUtils.getChannel();
        //首先声明死信交换机和队列
        c.exchangeDeclare(DLX, BuiltinExchangeType.DIRECT);
        c.queueDeclare(DLX_QUE, false, false, false, null);
        c.queueBind(DLX_QUE, DLX, DLX_KEY);

        //声明普通交换机和队列
        c.exchangeDeclare(NORMAL_X, BuiltinExchangeType.DIRECT);
        //设置dead-letter-exchange属性
        Map<String, Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange", DLX);
        properties.put("x-dead-letter-routing-key", DLX);
        //声明普通队列
        c.queueDeclare(NORMAL_QUE, false, false, false, properties);
        c.queueBind(NORMAL_QUE, NORMAL_X, NORMAL_KEY);
    }

    private static class Producer extends Thread{
        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();

                Scanner scanner = new Scanner(System.in);
                while(scanner.nextInt() != -1){
                    //输入类型为:"sleepTime,ifRefuse,msg"
                    String msg = scanner.next();
                    AMQP.BasicProperties b = new AMQP.BasicProperties.Builder()
                            //超时时间为10s
                            .expiration("5000")
                            .build();
                    c.basicPublish(NORMAL_X,  NORMAL_KEY, b, msg.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class NormalConsumer extends  Thread{
        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();
                c.basicQos(1);
                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        String[] allMsg = new String(message.getBody()).split(",");
                        long tag = message.getEnvelope().getDeliveryTag();
                        if(Integer.parseInt(allMsg[0]) == 1){
                            c.basicReject(tag, false);
                        }else{
                            try {
                                System.out.println("[normal] consume:" + tag
                                         + "after " + Long.parseLong(allMsg[1]) + "s get:" + allMsg[2]);
                                Thread.sleep(Long.parseLong(allMsg[1]) * 1000);
                                c.basicAck(tag, false);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                };
                c.basicConsume(NORMAL_QUE, false, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static class DLConsumer extends  Thread{
        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();

                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        System.out.println("[DL consumer] consume:" + message.getEnvelope().getDeliveryTag()
                                +" death_reason" + message.getProperties().getHeaders().get("x-death"));
                    }
                };
                c.basicConsume(DLX_QUE, true, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        //初始化队列和交换机
        init();

        //启动消费者
        new NormalConsumer().start();
        new DLConsumer().start();

        //启动生产者
        new Producer().start();
    }
}
