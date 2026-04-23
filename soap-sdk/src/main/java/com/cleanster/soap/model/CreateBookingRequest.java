package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request object for the CreateBooking SOAP operation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBookingRequest {

    @JsonProperty("property_id")    private Long   propertyId;
    @JsonProperty("scheduled_at")   private String scheduledAt;
    @JsonProperty("duration_hours") private Double durationHours;
    @JsonProperty("service_type")   private String serviceType;
    private String notes;
    @JsonProperty("cleaner_id")     private Long   cleanerId;
    @JsonProperty("checklist_id")   private Long   checklistId;

    public Long   getPropertyId()    { return propertyId; }
    public String getScheduledAt()   { return scheduledAt; }
    public Double getDurationHours() { return durationHours; }
    public String getServiceType()   { return serviceType; }
    public String getNotes()         { return notes; }
    public Long   getCleanerId()     { return cleanerId; }
    public Long   getChecklistId()   { return checklistId; }

    public CreateBookingRequest setPropertyId(Long id)        { this.propertyId = id; return this; }
    public CreateBookingRequest setScheduledAt(String s)      { this.scheduledAt = s; return this; }
    public CreateBookingRequest setDurationHours(Double h)    { this.durationHours = h; return this; }
    public CreateBookingRequest setServiceType(String s)      { this.serviceType = s; return this; }
    public CreateBookingRequest setNotes(String notes)        { this.notes = notes; return this; }
    public CreateBookingRequest setCleanerId(Long id)         { this.cleanerId = id; return this; }
    public CreateBookingRequest setChecklistId(Long id)       { this.checklistId = id; return this; }
}
