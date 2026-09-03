package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for creating or updating a checklist.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateChecklistRequest {

    @JsonProperty("title") private String title;
    @JsonProperty("tasks") private List<ChecklistTask> tasks;

    public CreateChecklistRequest() {}

    public CreateChecklistRequest(String title, List<ChecklistTask> tasks) {
        this.title = title;
        this.tasks = tasks;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<ChecklistTask> getTasks() { return tasks; }
    public void setTasks(List<ChecklistTask> tasks) { this.tasks = tasks; }
}
