package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for enabling or disabling a property.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnableDisablePropertyRequest {

    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    public EnableDisablePropertyRequest() {}

    public EnableDisablePropertyRequest(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
}
