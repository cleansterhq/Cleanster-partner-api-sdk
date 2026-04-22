package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for calculating a booking cost estimate.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostEstimateRequest {

    @JsonProperty("propertyId")
    private Integer propertyId;

    @JsonProperty("planId")
    private Integer planId;

    @JsonProperty("hours")
    private Float hours;

    @JsonProperty("extraSupplies")
    private Boolean extraSupplies;

    @JsonProperty("laundry")
    private Boolean laundry;

    @JsonProperty("extraIds")
    private List<Integer> extraIds;

    @JsonProperty("coupon")
    private String coupon;

    public CostEstimateRequest() {}

    public Integer getPropertyId() { return propertyId; }
    public void setPropertyId(Integer propertyId) { this.propertyId = propertyId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public Float getHours() { return hours; }
    public void setHours(Float hours) { this.hours = hours; }

    public Boolean getExtraSupplies() { return extraSupplies; }
    public void setExtraSupplies(Boolean extraSupplies) { this.extraSupplies = extraSupplies; }

    public Boolean getLaundry() { return laundry; }
    public void setLaundry(Boolean laundry) { this.laundry = laundry; }

    public List<Integer> getExtraIds() { return extraIds; }
    public void setExtraIds(List<Integer> extraIds) { this.extraIds = extraIds; }

    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }
}
