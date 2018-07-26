package pl.agh.edu.wiet.clinic.agents;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.agh.edu.wiet.clinic.utils.Utils;

import java.io.IOException;
import java.util.Scanner;

import static pl.agh.edu.wiet.clinic.config.Config.*;

public class Administrator {

    private final Channel channel;
    private final String queueName;
    private final ExaminationMessageConsumer examinationMessageConsumer;

    private Administrator(Channel channel) throws IOException {
        this.channel = channel;
        this.queueName = "examinations";
        Utils.joinQueue(channel, queueName,EXAMINATION_KEY_PREFIX + "*.*");
        this.examinationMessageConsumer = new ExaminationMessageConsumer(channel);
        new ExaminationMessagesHandler().start();
    }

    private String scanInfoMessage() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private void sendInfoMessage(String message) throws Exception {
        channel.basicPublish(
                EXCHANGE_NAME,
                ADMIN_INFO_KEY,
                null,
                message.getBytes(ENCODING));
        System.out.println("Info sent: " + message);
    }

    private class ExaminationMessageConsumer extends DefaultConsumer {
        ExaminationMessageConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body, ENCODING);
            System.out.println("FROM " + envelope.getRoutingKey() + ": " + message);
        }
    }

    private class ExaminationMessagesHandler extends Thread {

        @Override
        public void run() {
            try {
                channel.basicConsume(queueName, true, examinationMessageConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("ADMINISTRATOR");
        Administrator admin = new Administrator(Utils.createChannel(
                HOST,
                EXCHANGE_NAME,
                EXCAHNGE_TYPE));
        while (true) {
            String message = admin.scanInfoMessage();
            if ("exit".equals(message)) break;
            admin.sendInfoMessage(message);
        }
    }
}
