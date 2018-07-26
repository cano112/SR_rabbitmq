package pl.agh.edu.wiet.clinic.exceptions;

public class UnrecognizedExaminationTypeException extends RuntimeException {

    public UnrecognizedExaminationTypeException() {
    }

    public UnrecognizedExaminationTypeException(String message) {
        super(message);
    }

    public UnrecognizedExaminationTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnrecognizedExaminationTypeException(Throwable cause) {
        super(cause);
    }

    public UnrecognizedExaminationTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
