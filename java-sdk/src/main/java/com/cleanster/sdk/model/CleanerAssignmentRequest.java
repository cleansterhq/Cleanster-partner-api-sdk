package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for assigning a cleaner to a booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CleanerAssignmentRequest {

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    public CleanerAssignmentRequest() {}

    public CleanerAssignmentRequest(Integer cleanerId) {
        this.cleanerId = cleanerId;
    }

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }
}
