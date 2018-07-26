package pl.agh.edu.wiet.clinic.model;

import pl.agh.edu.wiet.clinic.config.Config;

public enum ExaminationType {
    KNEE,
    HIP,
    ELBOW,
    ARM,
    UNRECOGNIZED;

    public String getRequestKey() {
        return Config.EXAMINATION_REQUEST_KEY_PREFIX + this.name().toLowerCase();
    }
    public String getResultKey() {
        return Config.EXAMINATION_RESULT_KEY_PREFIX + this.name().toLowerCase();
    }
    public String getQueueName() {
        return "queue-" + this.name().toLowerCase();
    }
}
