package pl.agh.edu.wiet.clinic.agents;

import com.rabbitmq.client.*;
import pl.agh.edu.wiet.clinic.utils.Utils;

import java.io.IOException;

import static pl.agh.edu.wiet.clinic.config.Config.ADMIN_INFO_KEY;
import static pl.agh.edu.wiet.clinic.config.Config.ENCODING;

public abstract class AdminInfoReceiver {
    protected final Channel channel;
    protected final String adminInfoQueueName;
    protected final Consumer adminInfoConsumer;

    public AdminInfoReceiver(Channel channel) throws IOException {
        this.channel = channel;
        this.adminInfoQueueName = "admin-info-" + ProcessHandle.current().pid();
        Utils.joinQueue(channel, adminInfoQueueName, ADMIN_INFO_KEY);
        this.adminInfoConsumer = new AdminInfoConsumer(channel);
    }

    protected abstract void listen()  throws Exception;

    protected class AdminInfoHandler extends Thread {

        @Override
        public void run() {
            try {
                channel.basicConsume(adminInfoQueueName, true, ADMIN_INFO_KEY, adminInfoConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class AdminInfoConsumer extends DefaultConsumer {
        AdminInfoConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body, ENCODING);
            System.out.println("Received: " + message);
        }
    }
}
