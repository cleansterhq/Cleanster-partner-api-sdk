package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a cleaning checklist.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Checklist {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("is_default") private Boolean isDefault;
    @JsonProperty("disabled") private Boolean disabled;
    @JsonProperty("title") private String title;
    @JsonProperty("type") private String type;
    @JsonProperty("totalTasks") private Integer totalTasks;
    @JsonProperty("totalSubTasks") private Integer totalSubTasks;
    @JsonProperty("tasks") private List<ChecklistTask> tasks;
    // Retained for responses produced by older API versions.
    @JsonProperty("name") private String name;
    @JsonProperty("items") private List<ChecklistItem> items;

    public Checklist() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ChecklistItem> getItems() { return items; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }
    public Integer getTotalSubTasks() { return totalSubTasks; }
    public void setTotalSubTasks(Integer totalSubTasks) { this.totalSubTasks = totalSubTasks; }
    public List<ChecklistTask> getTasks() { return tasks; }
    public void setTasks(List<ChecklistTask> tasks) { this.tasks = tasks; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChecklistItem {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("description")
        private String description;
        @JsonProperty("isCompleted")
        private Boolean isCompleted;
        @JsonProperty("imageUrl")
        private String imageUrl;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getIsCompleted() { return isCompleted; }
        public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    @Override
    public String toString() {
        return "Checklist{id=" + id + ", name='" + name + "'}";
    }
}
