package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Represents a Cleanster checklist with its line items. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Checklist {

    private Long         id;
    private String       name;
    private List<String> items;
    @JsonProperty("created_at") private String createdAt;

    public Long         getId()        { return id; }
    public String       getName()      { return name; }
    public List<String> getItems()     { return items; }
    public String       getCreatedAt() { return createdAt; }

    public void setId(Long id)               { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setItems(List<String> items) { this.items = items; }
    public void setCreatedAt(String s)       { this.createdAt = s; }
}
