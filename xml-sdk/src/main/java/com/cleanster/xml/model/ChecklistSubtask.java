package com.cleanster.xml.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChecklistSubtask {
    private String description;
    @SerializedName("flag_request_photos") private Boolean flagRequestPhotos;
    private List<String> photos;
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public Boolean getFlagRequestPhotos() { return flagRequestPhotos; } public void setFlagRequestPhotos(Boolean v) { flagRequestPhotos = v; }
    public List<String> getPhotos() { return photos; } public void setPhotos(List<String> v) { photos = v; }
}