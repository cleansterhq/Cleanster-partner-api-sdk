package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a Cleanster property. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {

    private Long   id;
    private Long   userId;
    private Long   serviceId;
    private String name;
    private String nickName;
    private String apt;
    private String street;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String zipCode;
    private Integer bedrooms;
    private Double  bathrooms;
    @JsonProperty("square_feet")         private Integer squareFeet;
    private String notes;
    @JsonProperty("access_instructions") private String accessInstructions;
    @JsonProperty("created_at")          private String createdAt;
    @JsonProperty("updated_at")          private String updatedAt;
    private Boolean isActive;
    private Boolean isEnable;
    private String pets;
    private String publicName;
    private String wifiName;
    private String wifiPassword;
    private Boolean laundry;
    private String garbage;
    private Boolean extraSupplies;
    private String createdDate;
    private String access;
    private String suppliesLocation;
    private String parking;
    private String otherNote;
    private Double latitude;
    private Double longitude;

    public Long    getId()                   { return id; }
    public Long    getUserId()               { return userId; }
    public Long    getServiceId()            { return serviceId; }
    public String  getName()                 { return name; }
    public String  getNickName()             { return nickName; }
    public String  getApt()                  { return apt; }
    public String  getStreet()               { return street; }
    public String  getAddress()              { return address; }
    public String  getCity()                 { return city; }
    public String  getState()                { return state; }
    public String  getCountry()              { return country; }
    public String  getZip()                  { return zip; }
    public String  getZipCode()              { return zipCode; }
    public Integer getBedrooms()             { return bedrooms; }
    public Double  getBathrooms()            { return bathrooms; }
    public Integer getSquareFeet()           { return squareFeet; }
    public String  getNotes()                { return notes; }
    public String  getAccessInstructions()   { return accessInstructions; }
    public String  getCreatedAt()            { return createdAt; }
    public String  getUpdatedAt()            { return updatedAt; }
    public Boolean getIsActive()             { return isActive; }
    public Boolean getIsEnable()             { return isEnable; }
    public String getPets()                  { return pets; }
    public String getPublicName()            { return publicName; }
    public String getWifiName()              { return wifiName; }
    public String getWifiPassword()          { return wifiPassword; }
    public Boolean getLaundry()              { return laundry; }
    public String getGarbage()               { return garbage; }
    public Boolean getExtraSupplies()        { return extraSupplies; }
    public String getCreatedDate()           { return createdDate; }
    public String getAccess()                { return access; }
    public String getSuppliesLocation()      { return suppliesLocation; }
    public String getParking()               { return parking; }
    public String getOtherNote()             { return otherNote; }
    public Double getLatitude()              { return latitude; }
    public Double getLongitude()             { return longitude; }

    public void setId(Long id)                           { this.id = id; }
    public void setUserId(Long userId)                   { this.userId = userId; }
    public void setServiceId(Long serviceId)             { this.serviceId = serviceId; }
    public void setName(String name)                     { this.name = name; }
    public void setNickName(String nickName)              { this.nickName = nickName; }
    public void setApt(String apt)                        { this.apt = apt; }
    public void setStreet(String street)                  { this.street = street; }
    public void setAddress(String address)               { this.address = address; }
    public void setCity(String city)                     { this.city = city; }
    public void setState(String state)                   { this.state = state; }
    public void setCountry(String country)               { this.country = country; }
    public void setZip(String zip)                       { this.zip = zip; }
    public void setZipCode(String zipCode)               { this.zipCode = zipCode; }
    public void setBedrooms(Integer bedrooms)            { this.bedrooms = bedrooms; }
    public void setBathrooms(Double bathrooms)           { this.bathrooms = bathrooms; }
    public void setSquareFeet(Integer squareFeet)        { this.squareFeet = squareFeet; }
    public void setNotes(String notes)                   { this.notes = notes; }
    public void setAccessInstructions(String s)          { this.accessInstructions = s; }
    public void setCreatedAt(String createdAt)           { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)           { this.updatedAt = updatedAt; }
    public void setIsActive(Boolean isActive)            { this.isActive = isActive; }
    public void setIsEnable(Boolean isEnable)            { this.isEnable = isEnable; }
    public void setPets(String pets)                     { this.pets = pets; }
    public void setPublicName(String publicName)         { this.publicName = publicName; }
    public void setWifiName(String wifiName)             { this.wifiName = wifiName; }
    public void setWifiPassword(String wifiPassword)     { this.wifiPassword = wifiPassword; }
    public void setLaundry(Boolean laundry)              { this.laundry = laundry; }
    public void setGarbage(String garbage)               { this.garbage = garbage; }
    public void setExtraSupplies(Boolean extraSupplies)  { this.extraSupplies = extraSupplies; }
    public void setCreatedDate(String createdDate)       { this.createdDate = createdDate; }
    public void setAccess(String access)                 { this.access = access; }
    public void setSuppliesLocation(String value)        { this.suppliesLocation = value; }
    public void setParking(String parking)               { this.parking = parking; }
    public void setOtherNote(String otherNote)           { this.otherNote = otherNote; }
    public void setLatitude(Double latitude)             { this.latitude = latitude; }
    public void setLongitude(Double longitude)           { this.longitude = longitude; }
}
