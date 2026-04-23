package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request object for the CreateProperty SOAP operation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePropertyRequest {

    private String  address;
    private String  city;
    private String  state;
    private String  zip;
    private String  name;
    private Integer bedrooms;
    private Double  bathrooms;
    @JsonProperty("square_feet")         private Integer squareFeet;
    private String  notes;
    @JsonProperty("access_instructions") private String  accessInstructions;

    public String  getAddress()            { return address; }
    public String  getCity()               { return city; }
    public String  getState()              { return state; }
    public String  getZip()                { return zip; }
    public String  getName()               { return name; }
    public Integer getBedrooms()           { return bedrooms; }
    public Double  getBathrooms()          { return bathrooms; }
    public Integer getSquareFeet()         { return squareFeet; }
    public String  getNotes()              { return notes; }
    public String  getAccessInstructions() { return accessInstructions; }

    public CreatePropertyRequest setAddress(String a)            { this.address = a; return this; }
    public CreatePropertyRequest setCity(String c)               { this.city = c; return this; }
    public CreatePropertyRequest setState(String s)              { this.state = s; return this; }
    public CreatePropertyRequest setZip(String z)                { this.zip = z; return this; }
    public CreatePropertyRequest setName(String n)               { this.name = n; return this; }
    public CreatePropertyRequest setBedrooms(Integer b)          { this.bedrooms = b; return this; }
    public CreatePropertyRequest setBathrooms(Double b)          { this.bathrooms = b; return this; }
    public CreatePropertyRequest setSquareFeet(Integer sf)       { this.squareFeet = sf; return this; }
    public CreatePropertyRequest setNotes(String notes)          { this.notes = notes; return this; }
    public CreatePropertyRequest setAccessInstructions(String s) { this.accessInstructions = s; return this; }
}
