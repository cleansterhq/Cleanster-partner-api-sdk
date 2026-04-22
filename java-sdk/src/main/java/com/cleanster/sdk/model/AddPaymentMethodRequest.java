package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for adding a payment method.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPaymentMethodRequest {

    @JsonProperty("paymentMethodId")
    private String paymentMethodId;

    @JsonProperty("type")
    private String type;

    public AddPaymentMethodRequest() {}

    public AddPaymentMethodRequest(String paymentMethodId, String type) {
        this.paymentMethodId = paymentMethodId;
        this.type = type;
    }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
