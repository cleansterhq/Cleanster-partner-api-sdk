package com.cleanster.sdk.exception;

/**
 * Base exception for all Cleanster SDK errors.
 */
public class CleansterException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public CleansterException(String message) {
        super(message);
        this.statusCode = -1;
        this.responseBody = null;
    }

    public CleansterException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }

    public CleansterException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Returns the HTTP status code, or -1 if not applicable.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the raw response body from the server, or null if not applicable.
     */
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "CleansterException{statusCode=" + statusCode + ", message=" + getMessage() + "}";
    }
}
