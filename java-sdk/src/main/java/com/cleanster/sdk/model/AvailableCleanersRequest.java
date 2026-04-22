package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for fetching available cleaners for a booking slot.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailableCleanersRequest {

    @JsonProperty("propertyId")
    private Integer propertyId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("hours")
    private Float hours;

    @JsonProperty("serviceId")
    private Integer serviceId;

    public AvailableCleanersRequest() {}

    public Integer getPropertyId() { return propertyId; }
    public void setPropertyId(Integer propertyId) { this.propertyId = propertyId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public Float getHours() { return hours; }
    public void setHours(Float hours) { this.hours = hours; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
}
