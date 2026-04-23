package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a stored payment method. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethod {

    private Long    id;
    private String  brand;
    private String  last4;
    @JsonProperty("exp_month") private Integer expMonth;
    @JsonProperty("exp_year")  private Integer expYear;
    @JsonProperty("is_default") private Boolean isDefault;
    @JsonProperty("created_at") private String  createdAt;

    public Long    getId()        { return id; }
    public String  getBrand()     { return brand; }
    public String  getLast4()     { return last4; }
    public Integer getExpMonth()  { return expMonth; }
    public Integer getExpYear()   { return expYear; }
    public Boolean getIsDefault() { return isDefault; }
    public String  getCreatedAt() { return createdAt; }

    public void setId(Long id)              { this.id = id; }
    public void setBrand(String brand)      { this.brand = brand; }
    public void setLast4(String last4)      { this.last4 = last4; }
    public void setExpMonth(Integer m)      { this.expMonth = m; }
    public void setExpYear(Integer y)       { this.expYear = y; }
    public void setIsDefault(Boolean b)     { this.isDefault = b; }
    public void setCreatedAt(String s)      { this.createdAt = s; }
}
