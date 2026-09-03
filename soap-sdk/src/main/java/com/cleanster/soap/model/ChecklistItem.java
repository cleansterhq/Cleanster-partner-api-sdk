package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A checklist response item, optionally with its photo evidence URL. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChecklistItem {

    private Long id;
    private String description;
    @JsonProperty("isCompleted") private Boolean isCompleted;
    @JsonProperty("imageUrl") private String imageUrl;

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Boolean getIsCompleted() { return isCompleted; }
    public String getImageUrl() { return imageUrl; }

    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}