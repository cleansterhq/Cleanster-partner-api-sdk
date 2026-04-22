package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for sending a chat message on a booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessageRequest {

    @JsonProperty("message")
    private String message;

    public SendMessageRequest() {}

    public SendMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
