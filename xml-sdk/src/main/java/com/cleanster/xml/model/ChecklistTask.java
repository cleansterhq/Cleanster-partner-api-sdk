package com.cleanster.xml.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChecklistTask {
    @SerializedName("image_name") private String imageName;
    private String title;
    private Integer totalSubtasks;
    private List<ChecklistSubtask> subtasks;
    public String getImageName() { return imageName; } public void setImageName(String v) { imageName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public Integer getTotalSubtasks() { return totalSubtasks; } public void setTotalSubtasks(Integer v) { totalSubtasks = v; }
    public List<ChecklistSubtask> getSubtasks() { return subtasks; } public void setSubtasks(List<ChecklistSubtask> v) { subtasks = v; }
}