package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents an available Cleanster cleaning service type. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceType {

    private Long    id;
    private String  name;
    private String  slug;
    private String  description;
    @JsonProperty("base_duration_hours") private Double  baseDurationHours;
    private Boolean active;

    public Long    getId()                { return id; }
    public String  getName()              { return name; }
    public String  getSlug()              { return slug; }
    public String  getDescription()       { return description; }
    public Double  getBaseDurationHours() { return baseDurationHours; }
    public Boolean getActive()            { return active; }

    public void setId(Long id)                         { this.id = id; }
    public void setName(String name)                   { this.name = name; }
    public void setSlug(String slug)                   { this.slug = slug; }
    public void setDescription(String description)     { this.description = description; }
    public void setBaseDurationHours(Double h)         { this.baseDurationHours = h; }
    public void setActive(Boolean active)              { this.active = active; }
}
