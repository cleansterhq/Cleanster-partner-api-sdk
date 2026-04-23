package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for attaching and managing Stripe and PayPal payment methods. */
class PaymentMethodsApi internal constructor(private val client: CleansterClient) {

    /**
     * Get the Stripe Setup Intent client secret.
     *
     * Pass the returned `clientSecret` to `Stripe.confirmCardSetup()` in your Android app.
     * After confirmation, pass the resulting `paymentMethod.id` to [addPaymentMethod].
     */
    suspend fun getSetupIntentDetails(): ApiResponse<Map<String, Any>> = client.request(
        method = "GET",
        path   = "/v1/payment-methods/setup-intent-details",
    )

    /** Get a PayPal/Braintree client token for the PayPal Vault SDK. */
    suspend fun getPayPalClientToken(): ApiResponse<Map<String, Any>> = client.request(
        method = "GET",
        path   = "/v1/payment-methods/paypal-client-token",
    )

    /**
     * Save a tokenized payment method to the user's profile.
     *
     * @param paymentMethodId A Stripe `pm_xxx` token or a PayPal Braintree nonce.
     */
    suspend fun addPaymentMethod(paymentMethodId: String): ApiResponse<PaymentMethod> = client.request(
        method = "POST",
        path   = "/v1/payment-methods",
        body   = AddPaymentMethodRequest(paymentMethodId = paymentMethodId),
    )

    /** List all saved payment methods for the current user. */
    suspend fun getPaymentMethods(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/payment-methods",
    )

    /** Mark a payment method as the default for this user. */
    suspend fun setDefaultPaymentMethod(paymentMethodId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "PUT",
        path   = "/v1/payment-methods/$paymentMethodId/default",
    )

    /** Remove a saved payment method permanently. */
    suspend fun deletePaymentMethod(paymentMethodId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/payment-methods/$paymentMethodId",
    )
}
