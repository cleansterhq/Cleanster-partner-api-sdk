package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all payment-method-related SOAP operations. */
public class PaymentMethodService {

    private final SOAPTransport transport;

    public PaymentMethodService(SOAPTransport transport) {
        this.transport = transport;
    }

    public JsonNode getSetupIntentDetails() {
        return transport.extractData(transport.get("/v1/payment-methods/setup-intent"));
    }

    public JsonNode getPaypalClientToken() {
        return transport.extractData(transport.get("/v1/payment-methods/paypal-token"));
    }

    public PaymentMethod addPaymentMethod(String paymentMethodId) {
        Map<String, Object> body = new HashMap<>();
        body.put("payment_method_id", paymentMethodId);
        JsonNode root = transport.post("/v1/payment-methods", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), PaymentMethod.class);
    }

    public List<PaymentMethod> getPaymentMethods() {
        JsonNode root = transport.get("/v1/payment-methods");
        JsonNode data = transport.extractData(root);
        List<PaymentMethod> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, PaymentMethod.class));
            }
        }
        return list;
    }

    public ApiResponse deletePaymentMethod(long paymentMethodId) {
        JsonNode root = transport.delete("/v1/payment-methods/" + paymentMethodId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse setDefaultPaymentMethod(long paymentMethodId) {
        JsonNode root = transport.put("/v1/payment-methods/" + paymentMethodId + "/default", Map.of());
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }
}
