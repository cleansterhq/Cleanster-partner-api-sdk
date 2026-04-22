package com.cleanster.sdk.exception;

/**
 * Thrown when the API returns a 401 Unauthorized response.
 * This typically means the access token is missing, malformed, or expired.
 */
public class CleansterAuthException extends CleansterException {

    public CleansterAuthException(String message, String responseBody) {
        super(401, message, responseBody);
    }

    public CleansterAuthException(String message) {
        super(401, message, null);
    }
}
