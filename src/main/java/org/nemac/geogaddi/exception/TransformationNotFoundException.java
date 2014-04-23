package org.nemac.geogaddi.exception;

public class TransformationNotFoundException extends Exception {

    public TransformationNotFoundException() {
        super();
    }

    public TransformationNotFoundException(String message) {
        super(message);
    }

    public TransformationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformationNotFoundException(Throwable cause) {
        super(cause);
    }
}
