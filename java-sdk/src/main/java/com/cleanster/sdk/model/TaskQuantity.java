package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single task ID and its updated quantity, used within UpdateTaskRequest.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskQuantity {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("quantity")
    private Integer quantity;

    public TaskQuantity() {}

    public TaskQuantity(Integer id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
