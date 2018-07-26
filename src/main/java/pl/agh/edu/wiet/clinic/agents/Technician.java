package pl.agh.edu.wiet.clinic.agents;

import com.rabbitmq.client.*;
import pl.agh.edu.wiet.clinic.exceptions.NoExaminationTypeProvidedException;
import pl.agh.edu.wiet.clinic.exceptions.UnrecognizedExaminationTypeException;
import pl.agh.edu.wiet.clinic.model.ExaminationType;
import pl.agh.edu.wiet.clinic.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static pl.agh.edu.wiet.clinic.config.Config.*;

public class Technician extends AdminInfoReceiver {
    private final List<ExaminationType> examinationTypes;

    private Technician(Channel channel, ExaminationType... examinationTypes) throws IOException {
        super(channel);
        this.examinationTypes = Arrays.asList(examinationTypes);
        this.examinationTypes.forEach(type -> {
            try {
                Utils.joinQueue(channel, type.getQueueName(), type.getRequestKey());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    protected void listen() throws Exception {
        new AdminInfoHandler().start();
        examinationTypes.forEach(type -> new ExaminationRequestHandler(type).start());
    }

    private class ExaminationRequestConsumer extends DefaultConsumer {
        private final ExaminationType examinationType;

        ExaminationRequestConsumer(Channel channel, ExaminationType examinationType) {
            super(channel);
            this.examinationType = examinationType;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body, ENCODING);
            System.out.println("Received: " + message);
            try {
                sendExaminationResult("Result for "
                        + message
                        + ", examination type: "
                        + examinationType.name().toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendExaminationResult(String result) throws Exception {
            channel.basicPublish(
                    EXCHANGE_NAME,
                    examinationType.getResultKey(),
                    null,
                    result.getBytes(ENCODING));
            System.out.println("Sent to "
                    + examinationType.getResultKey()
                    + ": "
                    + result);
        }
    }

    private class ExaminationRequestHandler extends Thread {
        private final ExaminationType type;
        private final Consumer requestConsumer;

        ExaminationRequestHandler(ExaminationType type) {
            this.type = type;
            this.requestConsumer = new ExaminationRequestConsumer(channel, type);
        }

        @Override
        public void run() {
            try {
                channel.basicConsume(type.getQueueName(), true, requestConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length < 2)
            throw new NoExaminationTypeProvidedException("No examination types in program's parameters provided");

        ExaminationType examinationType1 = Utils.getExaminationTypeByIndex(Integer.valueOf(args[0]));
        ExaminationType examinationType2 = Utils.getExaminationTypeByIndex(Integer.valueOf(args[1]));
        if(examinationType1 == ExaminationType.UNRECOGNIZED || examinationType2 == ExaminationType.UNRECOGNIZED)
            throw new UnrecognizedExaminationTypeException("Examination type index out of bounds");

        System.out.println("Technician for "
                + examinationType1.name().toLowerCase() + " and "
                + examinationType2.name().toLowerCase());

        Technician technician = new Technician(
                Utils.createChannel(
                        HOST,
                        EXCHANGE_NAME,
                        EXCAHNGE_TYPE),
                examinationType1,
                examinationType2);

        technician.listen();
    }
}
