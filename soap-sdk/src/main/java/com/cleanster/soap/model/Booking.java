package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a Cleanster cleaning booking. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    private Long   id;
    private String status;
    @JsonProperty("scheduled_at")  private String scheduledAt;
    @JsonProperty("duration_hours") private Double durationHours;
    @JsonProperty("service_type")  private String serviceType;
    private String notes;
    private Property property;
    private Cleaner  cleaner;
    @JsonProperty("checklist_id")  private Long   checklistId;
    @JsonProperty("created_at")    private String createdAt;
    @JsonProperty("updated_at")    private String updatedAt;

    public Long   getId()            { return id; }
    public String getStatus()        { return status; }
    public String getScheduledAt()   { return scheduledAt; }
    public Double getDurationHours() { return durationHours; }
    public String getServiceType()   { return serviceType; }
    public String getNotes()         { return notes; }
    public Property getProperty()    { return property; }
    public Cleaner  getCleaner()     { return cleaner; }
    public Long   getChecklistId()   { return checklistId; }
    public String getCreatedAt()     { return createdAt; }
    public String getUpdatedAt()     { return updatedAt; }

    public void setId(Long id)                     { this.id = id; }
    public void setStatus(String status)           { this.status = status; }
    public void setScheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; }
    public void setDurationHours(Double h)         { this.durationHours = h; }
    public void setServiceType(String s)           { this.serviceType = s; }
    public void setNotes(String notes)             { this.notes = notes; }
    public void setProperty(Property p)            { this.property = p; }
    public void setCleaner(Cleaner c)              { this.cleaner = c; }
    public void setChecklistId(Long id)            { this.checklistId = id; }
    public void setCreatedAt(String s)             { this.createdAt = s; }
    public void setUpdatedAt(String s)             { this.updatedAt = s; }
}
