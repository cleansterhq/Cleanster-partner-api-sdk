package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request object for the RescheduleBooking SOAP operation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RescheduleBookingRequest {

    @JsonProperty("booking_id")     private Long   bookingId;
    @JsonProperty("scheduled_at")   private String scheduledAt;
    @JsonProperty("duration_hours") private Double durationHours;

    public Long   getBookingId()     { return bookingId; }
    public String getScheduledAt()   { return scheduledAt; }
    public Double getDurationHours() { return durationHours; }

    public RescheduleBookingRequest setBookingId(Long id)     { this.bookingId = id; return this; }
    public RescheduleBookingRequest setScheduledAt(String s)  { this.scheduledAt = s; return this; }
    public RescheduleBookingRequest setDurationHours(Double h){ this.durationHours = h; return this; }
}
