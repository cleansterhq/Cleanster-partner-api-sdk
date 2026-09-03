package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/** A checklist response item and its optional photo evidence URL. */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChecklistItem {

    @XmlElement private Integer id;
    @XmlElement private String description;
    @XmlElement private Boolean isCompleted;
    @XmlElement private String imageUrl;

    public ChecklistItem() {}

    public Integer getId() { return id; }
    public String getDescription() { return description; }
    public Boolean getIsCompleted() { return isCompleted; }
    public String getImageUrl() { return imageUrl; }

    public void setId(Integer id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}