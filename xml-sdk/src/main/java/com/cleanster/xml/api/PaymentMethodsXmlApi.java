package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.PaymentMethod;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Payment Methods API — manage stored payment cards.
 *
 * <h3>Endpoints (6)</h3>
 * <ol>
 *   <li>GET    /payment-methods          — list payment methods</li>
 *   <li>GET    /payment-methods/{id}     — get payment method</li>
 *   <li>POST   /payment-methods          — add payment method</li>
 *   <li>PUT    /payment-methods/{id}     — update payment method</li>
 *   <li>DELETE /payment-methods/{id}     — delete payment method</li>
 *   <li>POST   /payment-methods/{id}/default — set as default</li>
 * </ol>
 */
public class PaymentMethodsXmlApi {

    private final XmlHttpClient http;

    public PaymentMethodsXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<PaymentMethod>> listPaymentMethods() {
        String json = http.get("/payment-methods");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<PaymentMethod>>>(){}.getType());
    }

    public XmlApiResponse<PaymentMethod> getPaymentMethod(int methodId) {
        String json = http.get("/payment-methods/" + methodId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }

    public XmlApiResponse<PaymentMethod> addPaymentMethod(String stripeToken, String holderName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("stripeToken", stripeToken);
        body.put("holderName",  holderName);
        String json = http.post("/payment-methods", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }

    public XmlApiResponse<PaymentMethod> updatePaymentMethod(int methodId, Map<String, Object> body) {
        String json = http.put("/payment-methods/" + methodId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }

    public XmlApiResponse<PaymentMethod> deletePaymentMethod(int methodId) {
        String json = http.delete("/payment-methods/" + methodId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }

    public XmlApiResponse<PaymentMethod> setDefaultPaymentMethod(int methodId) {
        String json = http.post("/payment-methods/" + methodId + "/default", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }
}
