package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a property in the Cleanster system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("nickName")
    private String nickName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("street")
    private String street;

    @JsonProperty("apt")
    private String apt;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zip")
    private String zip;

    @JsonProperty("zipCode")
    private String zipCode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("roomCount")
    private Integer roomCount;

    @JsonProperty("bathroomCount")
    private Integer bathroomCount;

    @JsonProperty("serviceId")
    private Integer serviceId;

    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("note")
    private String note;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isEnable")
    private Boolean isEnable;

    @JsonProperty("pets")
    private String pets;

    @JsonProperty("publicName")
    private String publicName;

    @JsonProperty("wifiName")
    private String wifiName;

    @JsonProperty("wifiPassword")
    private String wifiPassword;

    @JsonProperty("laundry")
    private Boolean laundry;

    @JsonProperty("garbage")
    private String garbage;

    @JsonProperty("extraSupplies")
    private Boolean extraSupplies;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("access")
    private String access;

    @JsonProperty("suppliesLocation")
    private String suppliesLocation;

    @JsonProperty("parking")
    private String parking;

    @JsonProperty("otherNote")
    private String otherNote;

    public Property() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getApt() { return apt; }
    public void setApt(String apt) { this.apt = apt; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getRoomCount() { return roomCount; }
    public void setRoomCount(Integer roomCount) { this.roomCount = roomCount; }

    public Integer getBathroomCount() { return bathroomCount; }
    public void setBathroomCount(Integer bathroomCount) { this.bathroomCount = bathroomCount; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsEnable() { return isEnable; }
    public void setIsEnable(Boolean isEnable) { this.isEnable = isEnable; }

    public String getPets() { return pets; }
    public void setPets(String pets) { this.pets = pets; }

    public String getPublicName() { return publicName; }
    public void setPublicName(String publicName) { this.publicName = publicName; }

    public String getWifiName() { return wifiName; }
    public void setWifiName(String wifiName) { this.wifiName = wifiName; }

    public String getWifiPassword() { return wifiPassword; }
    public void setWifiPassword(String wifiPassword) { this.wifiPassword = wifiPassword; }

    public Boolean getLaundry() { return laundry; }
    public void setLaundry(Boolean laundry) { this.laundry = laundry; }

    public String getGarbage() { return garbage; }
    public void setGarbage(String garbage) { this.garbage = garbage; }

    public Boolean getExtraSupplies() { return extraSupplies; }
    public void setExtraSupplies(Boolean extraSupplies) { this.extraSupplies = extraSupplies; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }

    public String getSuppliesLocation() { return suppliesLocation; }
    public void setSuppliesLocation(String suppliesLocation) { this.suppliesLocation = suppliesLocation; }

    public String getParking() { return parking; }
    public void setParking(String parking) { this.parking = parking; }

    public String getOtherNote() { return otherNote; }
    public void setOtherNote(String otherNote) { this.otherNote = otherNote; }

    @Override
    public String toString() {
        return "Property{id=" + id + ", name='" + name + "', address='" + address + "'}";
    }
}
