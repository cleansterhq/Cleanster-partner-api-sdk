package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a blacklisted cleaner entry. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlacklistEntry {

    private Long   id;
    @JsonProperty("cleaner_id") private Long   cleanerId;
    private String reason;
    @JsonProperty("created_at") private String createdAt;

    public Long   getId()        { return id; }
    public Long   getCleanerId() { return cleanerId; }
    public String getReason()    { return reason; }
    public String getCreatedAt() { return createdAt; }

    public void setId(Long id)             { this.id = id; }
    public void setCleanerId(Long id)      { this.cleanerId = id; }
    public void setReason(String reason)   { this.reason = reason; }
    public void setCreatedAt(String s)     { this.createdAt = s; }
}
