package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for updating a booking's total square footage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateSqftRequest {

    @JsonProperty("totalSqFt")
    private Float totalSqFt;

    public UpdateSqftRequest() {}

    public UpdateSqftRequest(Float totalSqFt) {
        this.totalSqFt = totalSqFt;
    }

    public Float getTotalSqFt() { return totalSqFt; }
    public void setTotalSqFt(Float totalSqFt) { this.totalSqFt = totalSqFt; }
}
