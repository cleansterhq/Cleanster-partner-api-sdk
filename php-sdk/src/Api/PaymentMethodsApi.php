<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;
use Cleanster\Models\PaymentMethod;

/**
 * Manages Stripe and PayPal payment methods.
 */
final class PaymentMethodsApi
{
    public function __construct(private readonly HttpClient $http) {}

    /**
     * Return Stripe SetupIntent details for client-side card collection.
     * Use the returned clientSecret with Stripe.js on the client side.
     */
    public function getSetupIntentDetails(): ApiResponse
    {
        $raw = $this->http->get('/v1/payment-methods/setup-intent-details');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return a PayPal client token for rendering the PayPal button client-side. */
    public function getPaypalClientToken(): ApiResponse
    {
        $raw = $this->http->get('/v1/payment-methods/paypal-client-token');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Save a new payment method after client-side tokenization.
     *
     * @param string $paymentMethodId The tokenized payment method ID from Stripe.js or PayPal.
     */
    public function addPaymentMethod(string $paymentMethodId): ApiResponse
    {
        $raw = $this->http->post('/v1/payment-methods', ['paymentMethodId' => $paymentMethodId]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Return all saved payment methods for the current user.
     *
     * @return ApiResponse<PaymentMethod[]>
     */
    public function getPaymentMethods(): ApiResponse
    {
        $raw   = $this->http->get('/v1/payment-methods');
        $items = array_map(fn(array $m) => new PaymentMethod($m), $raw['data'] ?? []);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $items);
    }

    /** Remove a saved payment method. */
    public function deletePaymentMethod(int $paymentMethodId): ApiResponse
    {
        $raw = $this->http->delete("/v1/payment-methods/{$paymentMethodId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Make a saved payment method the default for future bookings. */
    public function setDefaultPaymentMethod(int $paymentMethodId): ApiResponse
    {
        $raw = $this->http->put("/v1/payment-methods/{$paymentMethodId}/default");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
