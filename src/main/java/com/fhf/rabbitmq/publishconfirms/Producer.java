package com.fhf.rabbitmq.publishconfirms;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static class Consumer extends Thread{
        int consumerId;

        public Consumer(int consumerId){
            this.consumerId = consumerId;
        }

        @Override
        public void run(){
            try {
                Channel c = RabbitMQUtils.getChannel();
                DeliverCallback deliverCallback = new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        System.out.println("customer:"+consumerId + " " + consumerTag + " " +
                                message.getEnvelope() + " " + new String(message.getBody()));
                    }
                };
                c.basicConsume("pc_test", true, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //开启发布确认
        Channel c = RabbitMQUtils.getChannel();
        c.confirmSelect();
        //声明队列
        c.queueDeclare("pc_test", false, false, false, null);

        //启动消费者
        for(int i = 1;i < 4;i++){
            new Consumer(i).start();
        }

        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.next();

        //方式1：单个发布确认
//        while(!inputStr.equals("end")){
//            c.basicPublish("", "pc_test", null, inputStr.getBytes(StandardCharsets.UTF_8));
//            c.waitForConfirmsOrDie(5000);
//            inputStr = scanner.next();
//        }

        //方式2：批量发布确认
//        int bachCount = 10;
//        int curCount = 0;
//        while(!inputStr.equals("end")){
//            c.basicPublish("", "pc_test", null, inputStr.getBytes(StandardCharsets.UTF_8));
//            if(++curCount == bachCount){
//                c.waitForConfirmsOrDie(5000);
//                curCount = 0;
//            }
//            inputStr = scanner.next();
//        }
//        if(curCount > 0){
//            c.waitForConfirmsOrDie(5000);
//        }

        //3.异步发布确认
        ConcurrentNavigableMap<Long, byte[]> confirmMap = new ConcurrentSkipListMap<>();

        ConfirmCallback confirmCallback = new ConfirmCallback() {
            @Override
            public void handle(long deliveryTag, boolean multiple) throws IOException {
                //multi表示大于等于当前序列号的均已确认
                if(multiple){
                    ConcurrentNavigableMap<Long, byte[]>  confirmed = confirmMap.headMap(deliveryTag, true);
                    confirmed.clear();
                }else{
                    confirmMap.remove(deliveryTag);
                    System.out.println("confirm message:" + deliveryTag);
                }
            }
        };

        c.addConfirmListener(confirmCallback, new ConfirmCallback() {
            @Override
            public void handle(long deliveryTag, boolean multiple) throws IOException {
                byte[] body = confirmMap.get(deliveryTag);
                System.err.format(
                        "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                        new String(body), deliveryTag, multiple
                );
                confirmCallback.handle(deliveryTag, multiple);
            }
        });

        while(!inputStr.equals("end")){
            confirmMap.put(c.getNextPublishSeqNo(), inputStr.getBytes(StandardCharsets.UTF_8));
            c.basicPublish("", "pc_test", null, inputStr.getBytes(StandardCharsets.UTF_8));
            inputStr = scanner.next();
        }


    }
}
