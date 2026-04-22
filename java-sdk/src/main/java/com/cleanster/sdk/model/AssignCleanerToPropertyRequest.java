package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for assigning a cleaner to a property.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignCleanerToPropertyRequest {

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    public AssignCleanerToPropertyRequest() {}

    public AssignCleanerToPropertyRequest(Integer cleanerId) {
        this.cleanerId = cleanerId;
    }

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }
}
