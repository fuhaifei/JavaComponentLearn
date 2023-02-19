package com.fhf.rabbitmq.rpc;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RPCClient {
    private static final String RPC_QUE = "rpc_que";
    private static Channel c;

    public static byte[] rpcCall(String callQue, String returnQue, byte[] parameter) throws IOException, ExecutionException, InterruptedException {
        if(returnQue == null){
            returnQue = c.queueDeclare().getQueue();
        }
        String opID = String.valueOf(UUID.randomUUID());
        BasicProperties properties = new BasicProperties.Builder()
                .correlationId(opID)
                .replyTo(returnQue)
                .build();
        c.queueDeclare(callQue, false, false, false, null);
        c.basicPublish("", callQue, properties, parameter);

        //回调
        final CompletableFuture<byte[]> response = new CompletableFuture<>();
        DeliverCallback deliverCallback = new DeliverCallback() {
            @Override
            public void handle(String consumerTag, Delivery message) throws IOException {
                if(opID.equals(message.getProperties().getCorrelationId())){
                    response.complete(message.getBody());
                }
            }
        };
        String ct = c.basicConsume(returnQue, true, deliverCallback, consumerTag -> {});
        byte[] result = response.get();
        c.basicCancel(ct);
        return result;
    }

    public static int fib(int n) throws IOException, ExecutionException, InterruptedException {
        byte[] result = rpcCall("rpc_que", null, String.valueOf(n).getBytes());
        return Integer.parseInt(new String(result));
    }

    public static void main(String[] args) throws IOException, TimeoutException, ExecutionException, InterruptedException {
        c = RabbitMQUtils.getChannel();
        for(int i = 0;i < 10;i++){
            int n = new Random().nextInt(1000);
            System.out.println("send rpc call:" + n);
            System.out.println(fib(n));
        }

    }
}
