package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.PaymentMethod;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Payment Methods API - manage Stripe and PayPal payment methods.
 *
 * <h3>Endpoints (6)</h3>
 * <ol>
 *   <li>GET    /v1/payment-methods/setup-intent-details        - Stripe SetupIntent details</li>
 *   <li>GET    /v1/payment-methods/paypal-client-token         - PayPal client token</li>
 *   <li>POST   /v1/payment-methods                             - add payment method</li>
 *   <li>GET    /v1/payment-methods                             - list payment methods</li>
 *   <li>DELETE /v1/payment-methods/{id}                        - delete payment method</li>
 *   <li>PUT    /v1/payment-methods/{id}/default                - set default</li>
 * </ol>
 */
public class PaymentMethodsXmlApi {

    private final XmlHttpClient http;

    public PaymentMethodsXmlApi(XmlHttpClient http) { this.http = http; }

    /**
     * Get Stripe SetupIntent details for collecting card information client-side.
     * Use the returned {@code clientSecret} with Stripe.js.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getSetupIntentDetails() {
        String json = http.get("/v1/payment-methods/setup-intent-details");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Get a PayPal client token for rendering the PayPal button client-side.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPaypalClientToken() {
        String json = http.get("/v1/payment-methods/paypal-client-token");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Save a new payment method after client-side tokenization.
     *
     * @param request  Map with paymentMethodId (Stripe token) or PayPal nonce.
     */
    public XmlApiResponse<PaymentMethod> addPaymentMethod(Map<String, Object> request) {
        String json = http.post("/v1/payment-methods", request);
        return http.fromJson(json, new TypeToken<XmlApiResponse<PaymentMethod>>(){}.getType());
    }

    /** Return all saved payment methods for the current user. */
    public XmlApiResponse<List<PaymentMethod>> listPaymentMethods() {
        String json = http.get("/v1/payment-methods");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<PaymentMethod>>>(){}.getType());
    }

    /**
     * Delete a saved payment method.
     *
     * @param paymentMethodId  The payment method ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse deletePaymentMethod(int paymentMethodId) {
        String json = http.delete("/v1/payment-methods/" + paymentMethodId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Set a payment method as the default for future bookings.
     *
     * @param paymentMethodId  The payment method ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse setDefaultPaymentMethod(int paymentMethodId) {
        String json = http.put("/v1/payment-methods/" + paymentMethodId + "/default", null);
        return http.fromJson(json, XmlApiResponse.class);
    }
}
