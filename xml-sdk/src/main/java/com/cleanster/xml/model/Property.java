package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    @XmlElement private Integer id;
    @XmlElement private Integer userId;
    @XmlElement private Integer serviceId;
    @XmlElement private String  name;
    @XmlElement private String  nickName;
    @XmlElement private String  apt;
    @XmlElement private String  street;
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
    @XmlElement private Boolean isActive;
    @XmlElement private Boolean isEnable;
    @XmlElement private String  pets;
    @XmlElement private String  publicName;
    @XmlElement private String  wifiName;
    @XmlElement private String  wifiPassword;
    @XmlElement private Boolean laundry;
    @XmlElement private String  garbage;
    @XmlElement private Boolean extraSupplies;
    @XmlElement private String  createdDate;
    @XmlElement private String  access;
    @XmlElement private String  suppliesLocation;
    @XmlElement private String  parking;
    @XmlElement private String  otherNote;
    @XmlElement private Double latitude;
    @XmlElement private Double longitude;

    public Property() {}

    public Integer getId()                  { return id; }
    public Integer getUserId()              { return userId; }
    public Integer getServiceId()           { return serviceId; }
    public String  getName()               { return name; }
    public String  getNickName()           { return nickName; }
    public String  getApt()                { return apt; }
    public String  getStreet()             { return street; }
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
    public Boolean getIsActive()           { return isActive; }
    public Boolean getIsEnable()           { return isEnable; }
    public String getPets()                { return pets; }
    public String getPublicName()          { return publicName; }
    public String getWifiName()            { return wifiName; }
    public String getWifiPassword()        { return wifiPassword; }
    public Boolean getLaundry()            { return laundry; }
    public String getGarbage()             { return garbage; }
    public Boolean getExtraSupplies()      { return extraSupplies; }
    public String getCreatedDate()         { return createdDate; }
    public String getAccess()              { return access; }
    public String getSuppliesLocation()    { return suppliesLocation; }
    public String getParking()             { return parking; }
    public String getOtherNote()           { return otherNote; }
    public Double getLatitude()            { return latitude; }
    public Double getLongitude()           { return longitude; }

    public void setId(Integer id)                           { this.id = id; }
    public void setUserId(Integer userId)                   { this.userId = userId; }
    public void setServiceId(Integer serviceId)             { this.serviceId = serviceId; }
    public void setName(String name)                       { this.name = name; }
    public void setNickName(String nickName)               { this.nickName = nickName; }
    public void setApt(String apt)                         { this.apt = apt; }
    public void setStreet(String street)                   { this.street = street; }
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
    public void setIsActive(Boolean isActive)              { this.isActive = isActive; }
    public void setIsEnable(Boolean isEnable)              { this.isEnable = isEnable; }
    public void setPets(String pets)                       { this.pets = pets; }
    public void setPublicName(String publicName)           { this.publicName = publicName; }
    public void setWifiName(String wifiName)               { this.wifiName = wifiName; }
    public void setWifiPassword(String wifiPassword)       { this.wifiPassword = wifiPassword; }
    public void setLaundry(Boolean laundry)                { this.laundry = laundry; }
    public void setGarbage(String garbage)                 { this.garbage = garbage; }
    public void setExtraSupplies(Boolean extraSupplies)    { this.extraSupplies = extraSupplies; }
    public void setCreatedDate(String createdDate)         { this.createdDate = createdDate; }
    public void setAccess(String access)                   { this.access = access; }
    public void setSuppliesLocation(String value)          { this.suppliesLocation = value; }
    public void setParking(String parking)                 { this.parking = parking; }
    public void setOtherNote(String otherNote)             { this.otherNote = otherNote; }
    public void setLatitude(Double latitude)               { this.latitude = latitude; }
    public void setLongitude(Double longitude)             { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Property{id=" + id + ", name='" + name + "', city='" + city + "'}";
    }
}
