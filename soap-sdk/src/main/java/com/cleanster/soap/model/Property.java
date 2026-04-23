package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a Cleanster property. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {

    private Long   id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private Integer bedrooms;
    private Double  bathrooms;
    @JsonProperty("square_feet")         private Integer squareFeet;
    private String notes;
    @JsonProperty("access_instructions") private String accessInstructions;
    @JsonProperty("created_at")          private String createdAt;
    @JsonProperty("updated_at")          private String updatedAt;

    public Long    getId()                   { return id; }
    public String  getName()                 { return name; }
    public String  getAddress()              { return address; }
    public String  getCity()                 { return city; }
    public String  getState()                { return state; }
    public String  getZip()                  { return zip; }
    public Integer getBedrooms()             { return bedrooms; }
    public Double  getBathrooms()            { return bathrooms; }
    public Integer getSquareFeet()           { return squareFeet; }
    public String  getNotes()                { return notes; }
    public String  getAccessInstructions()   { return accessInstructions; }
    public String  getCreatedAt()            { return createdAt; }
    public String  getUpdatedAt()            { return updatedAt; }

    public void setId(Long id)                           { this.id = id; }
    public void setName(String name)                     { this.name = name; }
    public void setAddress(String address)               { this.address = address; }
    public void setCity(String city)                     { this.city = city; }
    public void setState(String state)                   { this.state = state; }
    public void setZip(String zip)                       { this.zip = zip; }
    public void setBedrooms(Integer bedrooms)            { this.bedrooms = bedrooms; }
    public void setBathrooms(Double bathrooms)           { this.bathrooms = bathrooms; }
    public void setSquareFeet(Integer squareFeet)        { this.squareFeet = squareFeet; }
    public void setNotes(String notes)                   { this.notes = notes; }
    public void setAccessInstructions(String s)          { this.accessInstructions = s; }
    public void setCreatedAt(String createdAt)           { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)           { this.updatedAt = updatedAt; }
}
