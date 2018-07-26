package pl.agh.edu.wiet.clinic.config;

import com.rabbitmq.client.BuiltinExchangeType;

public class Config {

    public static String HOST = "localhost";
    public static String EXCHANGE_NAME = "exchange1";
    public static BuiltinExchangeType EXCAHNGE_TYPE = BuiltinExchangeType.TOPIC;

    public static String EXAMINATION_KEY_PREFIX = "examination.";
    public static String EXAMINATION_REQUEST_KEY_PREFIX = EXAMINATION_KEY_PREFIX + "request.";
    public static String EXAMINATION_RESULT_KEY_PREFIX = EXAMINATION_KEY_PREFIX + "result.";
    public static String ADMIN_PREFIX = "admin.";
    public static String ADMIN_INFO_KEY = ADMIN_PREFIX + "info";

    public static String ENCODING = "UTF-8";
}
