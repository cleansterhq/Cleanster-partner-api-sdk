package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a blacklisted cleaner entry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlacklistEntry {

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    @JsonProperty("cleanerName")
    private String cleanerName;

    @JsonProperty("reason")
    private String reason;

    public BlacklistEntry() {}

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }

    public String getCleanerName() { return cleanerName; }
    public void setCleanerName(String cleanerName) { this.cleanerName = cleanerName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "BlacklistEntry{cleanerId=" + cleanerId + ", cleanerName='" + cleanerName + "'}";
    }
}
