package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for creating or updating a checklist.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateChecklistRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("items")
    private List<String> items;

    public CreateChecklistRequest() {}

    public CreateChecklistRequest(String name, List<String> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }
}
