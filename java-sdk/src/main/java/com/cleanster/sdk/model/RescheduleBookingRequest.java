package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for rescheduling an existing booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RescheduleBookingRequest {

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    public RescheduleBookingRequest() {}

    public RescheduleBookingRequest(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
