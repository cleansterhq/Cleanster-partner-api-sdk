package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for updating task quantities on a booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTaskRequest {

    @JsonProperty("tasks")
    private List<TaskQuantity> tasks;

    public UpdateTaskRequest() {}

    public UpdateTaskRequest(List<TaskQuantity> tasks) {
        this.tasks = tasks;
    }

    public List<TaskQuantity> getTasks() { return tasks; }
    public void setTasks(List<TaskQuantity> tasks) { this.tasks = tasks; }
}
