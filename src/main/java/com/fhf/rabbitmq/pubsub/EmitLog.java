package com.fhf.rabbitmq.pubsub;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EmitLog {

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare("log_exchange", "fanout");

        Scanner scanner = new Scanner(System.in);
        String curString = scanner.nextLine();
        while(!curString.startsWith("end")){
            //写入日志
            channel.basicPublish("log_exchange","", null, curString.getBytes(StandardCharsets.UTF_8));
            curString = scanner.nextLine();
        }
        System.out.println("exit");
    }
}
