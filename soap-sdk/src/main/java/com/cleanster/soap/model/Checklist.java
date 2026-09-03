package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Represents a Cleanster checklist with its line items. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Checklist {

    private Long         id;
    @JsonProperty("is_default") private Boolean isDefault;
    private Boolean disabled;
    private String title;
    private String type;
    private Integer totalTasks;
    private Integer totalSubTasks;
    private List<ChecklistTask> tasks;
    // Legacy response fields.
    private String name;
    private List<ChecklistItem> items;
    @JsonProperty("created_at") private String createdAt;

    public Long         getId()        { return id; }
    public String       getName()      { return name; }
    public List<ChecklistItem> getItems() { return items; }
    public String       getCreatedAt() { return createdAt; }
    public Boolean getIsDefault() { return isDefault; }
    public Boolean getDisabled() { return disabled; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public Integer getTotalTasks() { return totalTasks; }
    public Integer getTotalSubTasks() { return totalSubTasks; }
    public List<ChecklistTask> getTasks() { return tasks; }

    public void setId(Long id)               { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }
    public void setCreatedAt(String s)       { this.createdAt = s; }
    public void setIsDefault(Boolean v) { isDefault = v; }
    public void setDisabled(Boolean v) { disabled = v; }
    public void setTitle(String v) { title = v; }
    public void setType(String v) { type = v; }
    public void setTotalTasks(Integer v) { totalTasks = v; }
    public void setTotalSubTasks(Integer v) { totalSubTasks = v; }
    public void setTasks(List<ChecklistTask> v) { tasks = v; }
}
