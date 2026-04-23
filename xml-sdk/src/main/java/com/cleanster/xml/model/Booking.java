package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "booking")
@XmlAccessorType(XmlAccessType.FIELD)
public class Booking {

    @XmlElement private Integer id;
    @XmlElement private String  status;
    @XmlElement private String  date;
    @XmlElement private String  time;
    @XmlElement private Double  hours;
    @XmlElement private Integer propertyId;
    @XmlElement private String  propertyName;
    @XmlElement private Integer planId;
    @XmlElement private String  planName;
    @XmlElement private Integer roomCount;
    @XmlElement private Integer bathroomCount;
    @XmlElement private Boolean extraSupplies;
    @XmlElement private Double  totalPrice;
    @XmlElement private String  currency;
    @XmlElement private Integer paymentMethodId;
    @XmlElement private String  notes;
    @XmlElement private String  createdAt;
    @XmlElement private String  updatedAt;
    @XmlElement private String  cancelledAt;
    @XmlElement private String  cancelReason;

    public Booking() {}

    public Integer getId()             { return id; }
    public String  getStatus()         { return status; }
    public String  getDate()           { return date; }
    public String  getTime()           { return time; }
    public Double  getHours()          { return hours; }
    public Integer getPropertyId()     { return propertyId; }
    public String  getPropertyName()   { return propertyName; }
    public Integer getPlanId()         { return planId; }
    public String  getPlanName()       { return planName; }
    public Integer getRoomCount()      { return roomCount; }
    public Integer getBathroomCount()  { return bathroomCount; }
    public Boolean getExtraSupplies()  { return extraSupplies; }
    public Double  getTotalPrice()     { return totalPrice; }
    public String  getCurrency()       { return currency; }
    public Integer getPaymentMethodId(){ return paymentMethodId; }
    public String  getNotes()          { return notes; }
    public String  getCreatedAt()      { return createdAt; }
    public String  getUpdatedAt()      { return updatedAt; }
    public String  getCancelledAt()    { return cancelledAt; }
    public String  getCancelReason()   { return cancelReason; }

    public void setId(Integer id)                      { this.id = id; }
    public void setStatus(String status)               { this.status = status; }
    public void setDate(String date)                   { this.date = date; }
    public void setTime(String time)                   { this.time = time; }
    public void setHours(Double hours)                 { this.hours = hours; }
    public void setPropertyId(Integer propertyId)      { this.propertyId = propertyId; }
    public void setPropertyName(String propertyName)   { this.propertyName = propertyName; }
    public void setPlanId(Integer planId)              { this.planId = planId; }
    public void setPlanName(String planName)           { this.planName = planName; }
    public void setRoomCount(Integer roomCount)        { this.roomCount = roomCount; }
    public void setBathroomCount(Integer bathroomCount){ this.bathroomCount = bathroomCount; }
    public void setExtraSupplies(Boolean extraSupplies){ this.extraSupplies = extraSupplies; }
    public void setTotalPrice(Double totalPrice)       { this.totalPrice = totalPrice; }
    public void setCurrency(String currency)           { this.currency = currency; }
    public void setPaymentMethodId(Integer id)         { this.paymentMethodId = id; }
    public void setNotes(String notes)                 { this.notes = notes; }
    public void setCreatedAt(String createdAt)         { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)         { this.updatedAt = updatedAt; }
    public void setCancelledAt(String cancelledAt)     { this.cancelledAt = cancelledAt; }
    public void setCancelReason(String cancelReason)   { this.cancelReason = cancelReason; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", status='" + status + "', date='" + date + "', time='" + time + "'}";
    }
}
