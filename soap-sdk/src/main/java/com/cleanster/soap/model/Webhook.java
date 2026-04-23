package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a webhook subscription. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Webhook {

    private Long   id;
    private String url;
    private String event;
    private String status;
    @JsonProperty("created_at") private String createdAt;

    public Long   getId()        { return id; }
    public String getUrl()       { return url; }
    public String getEvent()     { return event; }
    public String getStatus()    { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setId(Long id)             { this.id = id; }
    public void setUrl(String url)         { this.url = url; }
    public void setEvent(String event)     { this.event = event; }
    public void setStatus(String status)   { this.status = status; }
    public void setCreatedAt(String s)     { this.createdAt = s; }
}
