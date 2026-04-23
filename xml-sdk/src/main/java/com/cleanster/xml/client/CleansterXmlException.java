package com.cleanster.xml.client;

/**
 * Runtime exception thrown by the Cleanster XML SDK when an API call or XML
 * conversion fails.
 */
public class CleansterXmlException extends RuntimeException {

    private final int httpStatus;
    private final String rawBody;

    public CleansterXmlException(String message) {
        super(message);
        this.httpStatus = -1;
        this.rawBody    = null;
    }

    public CleansterXmlException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = -1;
        this.rawBody    = null;
    }

    public CleansterXmlException(int httpStatus, String message, String rawBody) {
        super(message);
        this.httpStatus = httpStatus;
        this.rawBody    = rawBody;
    }

    /** HTTP status code, or -1 if the error is not HTTP-related. */
    public int    getHttpStatus() { return httpStatus; }

    /** Raw response body if available, otherwise null. */
    public String getRawBody()    { return rawBody; }

    @Override
    public String toString() {
        return "CleansterXmlException{status=" + httpStatus + ", message='" + getMessage() + "'}";
    }
}
