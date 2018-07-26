package pl.agh.edu.wiet.clinic.exceptions;

public class NoExaminationTypeProvidedException extends RuntimeException {
    public NoExaminationTypeProvidedException() {
    }

    public NoExaminationTypeProvidedException(String message) {
        super(message);
    }

    public NoExaminationTypeProvidedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoExaminationTypeProvidedException(Throwable cause) {
        super(cause);
    }

    public NoExaminationTypeProvidedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
