package com.cleanster.sdk.exception;

/**
 * Thrown when the API returns a non-2xx HTTP response.
 */
public class CleansterApiException extends CleansterException {

    public CleansterApiException(int statusCode, String message, String responseBody) {
        super(statusCode, message, responseBody);
    }
}
