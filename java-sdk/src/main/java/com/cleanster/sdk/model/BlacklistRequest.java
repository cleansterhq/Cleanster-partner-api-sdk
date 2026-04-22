package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for blacklisting or un-blacklisting a cleaner.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlacklistRequest {

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    @JsonProperty("reason")
    private String reason;

    public BlacklistRequest() {}

    public BlacklistRequest(Integer cleanerId, String reason) {
        this.cleanerId = cleanerId;
        this.reason = reason;
    }

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
