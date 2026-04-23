package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "checklist")
@XmlAccessorType(XmlAccessType.FIELD)
public class Checklist {

    @XmlElement private Integer     id;
    @XmlElement private String      name;
    @XmlElement private String      description;
    @XmlElement private Boolean     active;
    @XmlElement private Integer     propertyId;
    @XmlElementWrapper(name = "items")
    @XmlElement(name   = "item")
    private List<String> items;
    @XmlElement private String      createdAt;
    @XmlElement private String      updatedAt;

    public Checklist() {}

    public Integer      getId()          { return id; }
    public String       getName()        { return name; }
    public String       getDescription() { return description; }
    public Boolean      getActive()      { return active; }
    public Integer      getPropertyId()  { return propertyId; }
    public List<String> getItems()       { return items; }
    public String       getCreatedAt()   { return createdAt; }
    public String       getUpdatedAt()   { return updatedAt; }

    public void setId(Integer id)               { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setActive(Boolean active)       { this.active = active; }
    public void setPropertyId(Integer pid)      { this.propertyId = pid; }
    public void setItems(List<String> items)    { this.items = items; }
    public void setCreatedAt(String createdAt)  { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)  { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Checklist{id=" + id + ", name='" + name + "'}";
    }
}
