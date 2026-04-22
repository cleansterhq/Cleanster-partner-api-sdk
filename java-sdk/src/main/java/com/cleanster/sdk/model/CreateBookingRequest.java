package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for creating a new booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBookingRequest {

    @JsonProperty("time")
    private String time;

    @JsonProperty("date")
    private String date;

    @JsonProperty("propertyId")
    private Integer propertyId;

    @JsonProperty("roomCount")
    private Integer roomCount;

    @JsonProperty("bathroomCount")
    private Integer bathroomCount;

    @JsonProperty("planId")
    private Integer planId;

    @JsonProperty("hours")
    private Float hours;

    @JsonProperty("extraSupplies")
    private Boolean extraSupplies;

    @JsonProperty("laundry")
    private Boolean laundry;

    @JsonProperty("pets")
    private String pets;

    @JsonProperty("extraIds")
    private List<Integer> extraIds;

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    @JsonProperty("coupon")
    private String coupon;

    @JsonProperty("paymentMethodId")
    private Integer paymentMethodId;

    public CreateBookingRequest() {}

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getPropertyId() { return propertyId; }
    public void setPropertyId(Integer propertyId) { this.propertyId = propertyId; }

    public Integer getRoomCount() { return roomCount; }
    public void setRoomCount(Integer roomCount) { this.roomCount = roomCount; }

    public Integer getBathroomCount() { return bathroomCount; }
    public void setBathroomCount(Integer bathroomCount) { this.bathroomCount = bathroomCount; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public Float getHours() { return hours; }
    public void setHours(Float hours) { this.hours = hours; }

    public Boolean getExtraSupplies() { return extraSupplies; }
    public void setExtraSupplies(Boolean extraSupplies) { this.extraSupplies = extraSupplies; }

    public Boolean getLaundry() { return laundry; }
    public void setLaundry(Boolean laundry) { this.laundry = laundry; }

    public String getPets() { return pets; }
    public void setPets(String pets) { this.pets = pets; }

    public List<Integer> getExtraIds() { return extraIds; }
    public void setExtraIds(List<Integer> extraIds) { this.extraIds = extraIds; }

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }

    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}
