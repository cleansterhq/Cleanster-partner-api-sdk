package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChecklistSubtask {
    private String description;
    @JsonProperty("flag_request_photos") private Boolean flagRequestPhotos;
    private List<String> photos;
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public Boolean getFlagRequestPhotos() { return flagRequestPhotos; } public void setFlagRequestPhotos(Boolean v) { flagRequestPhotos = v; }
    public List<String> getPhotos() { return photos; } public void setPhotos(List<String> v) { photos = v; }
}