package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for cancelling a booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelBookingRequest {

    @JsonProperty("reason")
    private String reason;

    public CancelBookingRequest() {}

    public CancelBookingRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
