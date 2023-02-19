package ru.gb.app.producer;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ProducerAppITBlog {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private static String routingKey;
    private static String message;
    private static int count;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Scanner sc = new Scanner(System.in);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            while (true) {
                if (chooseTopic(sc)) break;
            }
            for (int i = 1; i <= count; i++) {
                String msgCount = message + ":" + i;
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, msgCount.getBytes(StandardCharsets.UTF_8));

            }
            System.out.println("Отпрален блог c темой: '" + routingKey.split("\\.")[1] + "'  Текст блога: '" + message + "'");
        }
    }

    private static boolean chooseTopic(Scanner sc) {
        System.out.println("Укажите тему блога для отправки подписчикам: php или java?");
        String msg = sc.nextLine();
        System.out.println("Укажите количество постов:");
        count = sc.nextInt();

        if ("php".equals(msg)) {
            routingKey = "prog.php";
            message = "php message";
            return true;
        }
        if ("java".equals(msg)) {
            routingKey = "prog.java";
            message = "java message";
            return true;
        } else System.out.println("\nНекорректная тема! \n");
        return false;
    }
}