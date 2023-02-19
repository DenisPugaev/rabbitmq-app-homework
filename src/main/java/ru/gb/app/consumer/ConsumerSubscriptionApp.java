package ru.gb.app.consumer;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsumerSubscriptionApp {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private static String routingKey;
    private static String message;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = channel.queueDeclare().getQueue();
        System.out.println("Имя очереди: " + queueName);

        Scanner sc = new Scanner(System.in);
        choseTopic(sc);

        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        System.out.println("Ожидаем блог с темой: " + routingKey.split("\\.")[1]);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Получен блог с темой '" + delivery.getEnvelope().getRoutingKey().split("\\.")[1] + "'. Его текст:'" + message + "'");

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

    }
    private static void choseTopic(Scanner sc) {
        while (true) {
            System.out.println("Укажите тему на какую тему подписаться: php или java?");
            String msg = sc.nextLine();
            if ("php".equals(msg)) {
                routingKey = "prog.php";
                break;
            }

            if ("java".equals(msg)) {
                routingKey = "prog.java";
                break;
            } else System.out.println("\nНекорректная тема! \n");
        }
    }
}
