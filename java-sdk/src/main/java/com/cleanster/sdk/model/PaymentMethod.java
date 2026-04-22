package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a saved payment method.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethod {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("last4")
    private String last4;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("isDefault")
    private Boolean isDefault;

    @JsonProperty("expiryMonth")
    private Integer expiryMonth;

    @JsonProperty("expiryYear")
    private Integer expiryYear;

    public PaymentMethod() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public Integer getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(Integer expiryMonth) { this.expiryMonth = expiryMonth; }

    public Integer getExpiryYear() { return expiryYear; }
    public void setExpiryYear(Integer expiryYear) { this.expiryYear = expiryYear; }

    @Override
    public String toString() {
        return "PaymentMethod{id=" + id + ", type='" + type + "', brand='" + brand + "', last4='" + last4 + "'}";
    }
}
