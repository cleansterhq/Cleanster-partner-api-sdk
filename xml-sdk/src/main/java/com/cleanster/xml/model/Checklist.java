package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@XmlRootElement(name = "checklist")
@XmlAccessorType(XmlAccessType.FIELD)
public class Checklist {

    @XmlElement private Integer     id;
    @SerializedName("is_default") @XmlElement private Boolean isDefault;
    @XmlElement private Boolean     disabled;
    @XmlElement private String      title;
    @XmlElement private String      type;
    @XmlElement private Integer     totalTasks;
    @XmlElement private Integer     totalSubTasks;
    private List<ChecklistTask> tasks;
    @XmlElement private String      name;
    @XmlElement private String      description;
    @XmlElement private Boolean     active;
    @XmlElement private Integer     propertyId;
    @XmlElementWrapper(name = "items")
    @XmlElement(name   = "item")
    private List<ChecklistItem> items;
    @XmlElement private String      createdAt;
    @XmlElement private String      updatedAt;

    public Checklist() {}

    public Integer      getId()          { return id; }
    public String       getName()        { return name; }
    public String       getDescription() { return description; }
    public Boolean      getActive()      { return active; }
    public Integer      getPropertyId()  { return propertyId; }
    public List<ChecklistItem> getItems() { return items; }
    public String       getCreatedAt()   { return createdAt; }
    public String       getUpdatedAt()   { return updatedAt; }
    public Boolean getIsDefault() { return isDefault; }
    public Boolean getDisabled() { return disabled; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public Integer getTotalTasks() { return totalTasks; }
    public Integer getTotalSubTasks() { return totalSubTasks; }
    public List<ChecklistTask> getTasks() { return tasks; }

    public void setId(Integer id)               { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setActive(Boolean active)       { this.active = active; }
    public void setPropertyId(Integer pid)      { this.propertyId = pid; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }
    public void setCreatedAt(String createdAt)  { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)  { this.updatedAt = updatedAt; }
    public void setIsDefault(Boolean v) { isDefault = v; }
    public void setDisabled(Boolean v) { disabled = v; }
    public void setTitle(String v) { title = v; }
    public void setType(String v) { type = v; }
    public void setTotalTasks(Integer v) { totalTasks = v; }
    public void setTotalSubTasks(Integer v) { totalSubTasks = v; }
    public void setTasks(List<ChecklistTask> v) { tasks = v; }

    @Override
    public String toString() {
        return "Checklist{id=" + id + ", name='" + name + "'}";
    }
}
