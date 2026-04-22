package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for payment methods: add, list, delete, mark as default,
 * and retrieve setup intent / PayPal client token.
 */
public class PaymentMethodApi {

    private final HttpClient httpClient;

    public PaymentMethodApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Get Stripe setup intent details for adding a new card.
     *
     * @return API response with setup intent client secret
     */
    public ApiResponse<Object> getSetupIntentDetails() {
        return httpClient.get("/v1/payment-methods/setup-intent-details",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get PayPal client token for PayPal payment method setup.
     *
     * @return API response with PayPal client token
     */
    public ApiResponse<Object> getPaypalClientToken() {
        return httpClient.get("/v1/payment-methods/paypal-client-token",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Add a new payment method (card or PayPal).
     *
     * @param request Payment method details
     * @return API response
     */
    public ApiResponse<Object> addPaymentMethod(AddPaymentMethodRequest request) {
        return httpClient.post("/v1/payment-methods", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get all saved payment methods.
     *
     * @return API response with list of payment methods
     */
    public ApiResponse<Object> getPaymentMethods() {
        return httpClient.get("/v1/payment-methods",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Delete a payment method by ID.
     *
     * @param paymentMethodId The payment method ID
     * @return API response
     */
    public ApiResponse<Object> deletePaymentMethod(int paymentMethodId) {
        return httpClient.delete("/v1/payment-methods/" + paymentMethodId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Mark a payment method as the default.
     *
     * @param paymentMethodId The payment method ID to set as default
     * @return API response
     */
    public ApiResponse<Object> setDefaultPaymentMethod(int paymentMethodId) {
        return httpClient.put("/v1/payment-methods/" + paymentMethodId + "/default", null,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
