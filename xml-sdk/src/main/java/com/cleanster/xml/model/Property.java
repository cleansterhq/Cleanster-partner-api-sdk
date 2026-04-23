package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    @XmlElement private Integer id;
    @XmlElement private String  name;
    @XmlElement private String  address;
    @XmlElement private String  city;
    @XmlElement private String  state;
    @XmlElement private String  zipCode;
    @XmlElement private String  country;
    @XmlElement private Integer roomCount;
    @XmlElement private Integer bathroomCount;
    @XmlElement private Double  squareFootage;
    @XmlElement private String  propertyType;
    @XmlElement private Boolean active;
    @XmlElement private String  notes;
    @XmlElement private String  accessInstructions;
    @XmlElement private String  createdAt;
    @XmlElement private String  updatedAt;

    public Property() {}

    public Integer getId()                  { return id; }
    public String  getName()               { return name; }
    public String  getAddress()            { return address; }
    public String  getCity()               { return city; }
    public String  getState()              { return state; }
    public String  getZipCode()            { return zipCode; }
    public String  getCountry()            { return country; }
    public Integer getRoomCount()          { return roomCount; }
    public Integer getBathroomCount()      { return bathroomCount; }
    public Double  getSquareFootage()      { return squareFootage; }
    public String  getPropertyType()       { return propertyType; }
    public Boolean getActive()             { return active; }
    public String  getNotes()              { return notes; }
    public String  getAccessInstructions() { return accessInstructions; }
    public String  getCreatedAt()          { return createdAt; }
    public String  getUpdatedAt()          { return updatedAt; }

    public void setId(Integer id)                           { this.id = id; }
    public void setName(String name)                       { this.name = name; }
    public void setAddress(String address)                 { this.address = address; }
    public void setCity(String city)                       { this.city = city; }
    public void setState(String state)                     { this.state = state; }
    public void setZipCode(String zipCode)                 { this.zipCode = zipCode; }
    public void setCountry(String country)                 { this.country = country; }
    public void setRoomCount(Integer roomCount)            { this.roomCount = roomCount; }
    public void setBathroomCount(Integer bathroomCount)    { this.bathroomCount = bathroomCount; }
    public void setSquareFootage(Double squareFootage)     { this.squareFootage = squareFootage; }
    public void setPropertyType(String propertyType)       { this.propertyType = propertyType; }
    public void setActive(Boolean active)                  { this.active = active; }
    public void setNotes(String notes)                     { this.notes = notes; }
    public void setAccessInstructions(String instructions) { this.accessInstructions = instructions; }
    public void setCreatedAt(String createdAt)             { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)             { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Property{id=" + id + ", name='" + name + "', city='" + city + "'}";
    }
}
