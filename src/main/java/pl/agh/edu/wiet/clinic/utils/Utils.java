package pl.agh.edu.wiet.clinic.utils;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import pl.agh.edu.wiet.clinic.model.ExaminationType;

import java.io.IOException;
import java.util.Optional;

import static pl.agh.edu.wiet.clinic.config.Config.EXCHANGE_NAME;

public class Utils {

    public static Channel createChannel(String host, String exchangeName, BuiltinExchangeType exchangeType)
            throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, exchangeType);
        return channel;
    }

    public static void joinQueue(Channel channel, String queueName, String... queueKeys) throws IOException {
        channel.queueDeclare(queueName, false, false, false, null);
        for (String queueKey : queueKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, queueKey);
        }
    }

    public static ExaminationType getExaminationTypeByIndex(int index) {
        ExaminationType[] examinationTypes = ExaminationType.values();
        return index < examinationTypes.length ? examinationTypes[index] : ExaminationType.UNRECOGNIZED;
    }

}
