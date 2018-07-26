package pl.agh.edu.wiet.clinic.agents;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import pl.agh.edu.wiet.clinic.model.ExaminationDTO;
import pl.agh.edu.wiet.clinic.model.ExaminationType;
import pl.agh.edu.wiet.clinic.utils.Utils;

import java.io.IOException;
import java.util.Scanner;

import static pl.agh.edu.wiet.clinic.config.Config.*;

public class Doctor extends AdminInfoReceiver {

    private final String examinationQueueName;
    private final ExaminationResultConsumer examinationResultConsumer;

    private Doctor(Channel channel) throws Exception {
        super(channel);
        this.examinationQueueName = "result";
        Utils.joinQueue(channel, examinationQueueName,EXAMINATION_RESULT_KEY_PREFIX + "*");
        this.examinationResultConsumer = new ExaminationResultConsumer(channel);
        listen();
    }

    @Override
    protected void listen() throws Exception {
        new AdminInfoHandler().start();
        new ResultHandler().start();
    }

    private ExaminationDTO scanExaminationData() {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter examination type: ");
        int examinationTypeIndex = Integer.parseInt(in.nextLine());
        ExaminationType examinationType = Utils.getExaminationTypeByIndex(examinationTypeIndex);

        System.out.println("Enter patient's surname");
        String surname = in.nextLine();

        return new ExaminationDTO(examinationType, surname);
    }

    private void sendExaminationRequest(ExaminationDTO examinationDTO) throws Exception {
        channel.basicPublish(
                EXCHANGE_NAME,
                examinationDTO.getExaminationType().getRequestKey(),
                null,
                examinationDTO.getPatientsSurname().getBytes(ENCODING));
        System.out.println("Sent to "
                + examinationDTO.getExaminationType().getRequestKey()
                + ": "
                + examinationDTO.getPatientsSurname());
    }

    private class ExaminationResultConsumer extends DefaultConsumer {
        ExaminationResultConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body, ENCODING);
            System.out.println("Received: " + message);
        }
    }

    private class ResultHandler extends Thread {

        @Override
        public void run() {
            try {
                channel.basicConsume(examinationQueueName, true, examinationResultConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("DOCTOR");
        Doctor doctor = new Doctor(Utils.createChannel(
                HOST,
                EXCHANGE_NAME,
                EXCAHNGE_TYPE));
        while (true) {
            ExaminationDTO examinationDTO = doctor.scanExaminationData();
            if ("exit".equals(examinationDTO.getPatientsSurname())) break;
            doctor.sendExaminationRequest(examinationDTO);
        }
    }
}
