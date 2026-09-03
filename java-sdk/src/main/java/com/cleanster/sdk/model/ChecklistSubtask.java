package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChecklistSubtask {
    @JsonProperty("description") private String description;
    @JsonProperty("flag_request_photos") private Boolean flagRequestPhotos;
    @JsonProperty("photos") private List<String> photos;
    public ChecklistSubtask() {}
    public ChecklistSubtask(String description, Boolean flagRequestPhotos, List<String> photos) {
        this.description = description; this.flagRequestPhotos = flagRequestPhotos; this.photos = photos;
    }
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public Boolean getFlagRequestPhotos() { return flagRequestPhotos; } public void setFlagRequestPhotos(Boolean v) { flagRequestPhotos = v; }
    public List<String> getPhotos() { return photos; } public void setPhotos(List<String> v) { photos = v; }
}