package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChecklistTask {
    @JsonProperty("image_name") private String imageName;
    @JsonProperty("title") private String title;
    @JsonProperty("totalSubtasks") private Integer totalSubtasks;
    @JsonProperty("subtasks") private List<ChecklistSubtask> subtasks;
    public ChecklistTask() {}
    public ChecklistTask(String imageName, String title, Integer totalSubtasks, List<ChecklistSubtask> subtasks) {
        this.imageName = imageName; this.title = title; this.totalSubtasks = totalSubtasks; this.subtasks = subtasks;
    }
    public String getImageName() { return imageName; } public void setImageName(String v) { imageName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public Integer getTotalSubtasks() { return totalSubtasks; } public void setTotalSubtasks(Integer v) { totalSubtasks = v; }
    public List<ChecklistSubtask> getSubtasks() { return subtasks; } public void setSubtasks(List<ChecklistSubtask> v) { subtasks = v; }
}