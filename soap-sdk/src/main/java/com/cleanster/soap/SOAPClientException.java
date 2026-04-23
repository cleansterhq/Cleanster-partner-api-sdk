package com.cleanster.soap;

/**
 * Runtime exception thrown when a SOAP operation fails.
 * Wraps HTTP errors, network failures, and serialization errors.
 */
public class SOAPClientException extends RuntimeException {

    private final int httpStatus;

    public SOAPClientException(String message) {
        super(message);
        this.httpStatus = extractStatus(message);
    }

    public SOAPClientException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = 0;
    }

    /** The HTTP status code, or 0 if not applicable. */
    public int getHttpStatus() {
        return httpStatus;
    }

    private static int extractStatus(String message) {
        if (message != null && message.startsWith("HTTP ")) {
            try {
                return Integer.parseInt(message.substring(5, 8));
            } catch (NumberFormatException ignored) { }
        }
        return 0;
    }
}
