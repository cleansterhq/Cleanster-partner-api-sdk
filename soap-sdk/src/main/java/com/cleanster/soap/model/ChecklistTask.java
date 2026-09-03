package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChecklistTask {
    @JsonProperty("image_name") private String imageName;
    private String title;
    private Integer totalSubtasks;
    private List<ChecklistSubtask> subtasks;
    public String getImageName() { return imageName; } public void setImageName(String v) { imageName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public Integer getTotalSubtasks() { return totalSubtasks; } public void setTotalSubtasks(Integer v) { totalSubtasks = v; }
    public List<ChecklistSubtask> getSubtasks() { return subtasks; } public void setSubtasks(List<ChecklistSubtask> v) { subtasks = v; }
}