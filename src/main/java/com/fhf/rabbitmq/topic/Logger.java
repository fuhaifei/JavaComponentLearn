package com.fhf.rabbitmq.topic;

import com.fhf.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Logger {

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel c = RabbitMQUtils.getChannel();

        c.exchangeDeclare("test_topic", "topic");


        Scanner scanner = new Scanner(System.in);
        while(!scanner.next().equals("end")){
            String routingKey = scanner.next();
            String message = scanner.next();
            c.basicPublish("test_topic", routingKey, null, message.getBytes());
        }
    }
}
