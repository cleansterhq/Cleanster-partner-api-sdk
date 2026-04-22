package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for adding a tip to a booking.
 * Can only be called within 72 hours after the booking is marked as completed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipRequest {

    @JsonProperty("amount")
    private Float amount;

    @JsonProperty("paymentMethodId")
    private Integer paymentMethodId;

    public TipRequest() {}

    public TipRequest(Float amount, Integer paymentMethodId) {
        this.amount = amount;
        this.paymentMethodId = paymentMethodId;
    }

    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}
