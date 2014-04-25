package org.nemac.geogaddi.exception;

public class PropertiesParseException extends Exception {
    public PropertiesParseException() {
        super();
    }

    public PropertiesParseException(String message) {
        super(message);
    }

    public PropertiesParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesParseException(Throwable cause) {
        super(cause);
    }
}