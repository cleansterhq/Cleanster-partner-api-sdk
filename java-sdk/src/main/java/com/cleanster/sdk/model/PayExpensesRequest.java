package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for paying expenses on a booking.
 * Can be called anytime before booking completion and up to 72 hours after completion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayExpensesRequest {

    @JsonProperty("amount")
    private Float amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("paymentMethodId")
    private Integer paymentMethodId;

    public PayExpensesRequest() {}

    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}
