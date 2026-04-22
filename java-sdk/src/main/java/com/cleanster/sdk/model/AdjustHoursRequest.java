package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for adjusting booking hours.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdjustHoursRequest {

    @JsonProperty("hours")
    private Float hours;

    public AdjustHoursRequest() {}

    public AdjustHoursRequest(Float hours) {
        this.hours = hours;
    }

    public Float getHours() { return hours; }
    public void setHours(Float hours) { this.hours = hours; }
}
