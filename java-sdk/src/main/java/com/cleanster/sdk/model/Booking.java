package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Cleanster booking/service appointment.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("propertyId")
    private Integer propertyId;

    @JsonProperty("pets")
    private String pets;

    @JsonProperty("hours")
    private Float hours;

    @JsonProperty("beforeAfterLink")
    private String beforeAfterLink;

    @JsonProperty("completionOrCancellationDate")
    private String completionOrCancellationDate;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("additionalSupplies")
    private Float additionalSupplies;

    @JsonProperty("planId")
    private Integer planId;

    @JsonProperty("roomCount")
    private Integer roomCount;

    @JsonProperty("bathroomCount")
    private Integer bathroomCount;

    @JsonProperty("tax")
    private Float tax;

    @JsonProperty("isLaundry")
    private Boolean isLaundry;

    @JsonProperty("totalAmount")
    private Float totalAmount;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("serviceId")
    private Integer serviceId;

    @JsonProperty("bookingDate")
    private String bookingDate;

    @JsonProperty("note")
    private String note;

    @JsonProperty("status")
    private String status;

    @JsonProperty("cleanerStatus")
    private String cleanerStatus;

    @JsonProperty("isExtraSupplies")
    private Boolean isExtraSupplies;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("hourlyRate")
    private Float hourlyRate;

    @JsonProperty("currencySymbol")
    private String currencySymbol;

    @JsonProperty("postedBy")
    private Integer postedBy;

    @JsonProperty("tip")
    private Float tip;

    @JsonProperty("bookingFee")
    private Float bookingFee;

    @JsonProperty("cleanerId")
    private Integer cleanerId;

    @JsonProperty("cleanerName")
    private String cleanerName;

    @JsonProperty("coupon")
    private String coupon;

    public Booking() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPropertyId() { return propertyId; }
    public void setPropertyId(Integer propertyId) { this.propertyId = propertyId; }

    public String getPets() { return pets; }
    public void setPets(String pets) { this.pets = pets; }

    public Float getHours() { return hours; }
    public void setHours(Float hours) { this.hours = hours; }

    public String getBeforeAfterLink() { return beforeAfterLink; }
    public void setBeforeAfterLink(String beforeAfterLink) { this.beforeAfterLink = beforeAfterLink; }

    public String getCompletionOrCancellationDate() { return completionOrCancellationDate; }
    public void setCompletionOrCancellationDate(String completionOrCancellationDate) {
        this.completionOrCancellationDate = completionOrCancellationDate;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Float getAdditionalSupplies() { return additionalSupplies; }
    public void setAdditionalSupplies(Float additionalSupplies) { this.additionalSupplies = additionalSupplies; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public Integer getRoomCount() { return roomCount; }
    public void setRoomCount(Integer roomCount) { this.roomCount = roomCount; }

    public Integer getBathroomCount() { return bathroomCount; }
    public void setBathroomCount(Integer bathroomCount) { this.bathroomCount = bathroomCount; }

    public Float getTax() { return tax; }
    public void setTax(Float tax) { this.tax = tax; }

    public Boolean getIsLaundry() { return isLaundry; }
    public void setIsLaundry(Boolean isLaundry) { this.isLaundry = isLaundry; }

    public Float getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Float totalAmount) { this.totalAmount = totalAmount; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCleanerStatus() { return cleanerStatus; }
    public void setCleanerStatus(String cleanerStatus) { this.cleanerStatus = cleanerStatus; }

    public Boolean getIsExtraSupplies() { return isExtraSupplies; }
    public void setIsExtraSupplies(Boolean isExtraSupplies) { this.isExtraSupplies = isExtraSupplies; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Float getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Float hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public Integer getPostedBy() { return postedBy; }
    public void setPostedBy(Integer postedBy) { this.postedBy = postedBy; }

    public Float getTip() { return tip; }
    public void setTip(Float tip) { this.tip = tip; }

    public Float getBookingFee() { return bookingFee; }
    public void setBookingFee(Float bookingFee) { this.bookingFee = bookingFee; }

    public Integer getCleanerId() { return cleanerId; }
    public void setCleanerId(Integer cleanerId) { this.cleanerId = cleanerId; }

    public String getCleanerName() { return cleanerName; }
    public void setCleanerName(String cleanerName) { this.cleanerName = cleanerName; }

    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", status='" + status + "', bookingDate='" + bookingDate + "'}";
    }
}
