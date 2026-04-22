using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages Stripe and PayPal payment methods.</summary>
public sealed class PaymentMethodsApi
{
    private readonly ICleansterHttpClient _http;
    internal PaymentMethodsApi(ICleansterHttpClient http) => _http = http;

    /// <summary>
    /// Return Stripe SetupIntent details for client-side card collection.
    /// Use the returned <c>clientSecret</c> with Stripe.js on the client.
    /// </summary>
    public async Task<ApiResponse<JsonElement>> GetSetupIntentDetailsAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/payment-methods/setup-intent-details", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return a PayPal client token for rendering the PayPal button client-side.</summary>
    public async Task<ApiResponse<JsonElement>> GetPaypalClientTokenAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/payment-methods/paypal-client-token", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Save a new payment method after client-side tokenization.</summary>
    /// <param name="paymentMethodId">The tokenized payment method ID from Stripe.js or PayPal.</param>
    public async Task<ApiResponse<JsonElement>> AddPaymentMethodAsync(
        string paymentMethodId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/payment-methods", new { paymentMethodId }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return all saved payment methods for the current user.</summary>
    public async Task<ApiResponse<List<PaymentMethod>>> GetPaymentMethodsAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/payment-methods", ct: ct);
        return JsonHelper.ParseList<PaymentMethod>(root);
    }

    /// <summary>Remove a saved payment method.</summary>
    public async Task<ApiResponse<JsonElement>> DeletePaymentMethodAsync(
        int paymentMethodId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/payment-methods/{paymentMethodId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Make a payment method the default for future bookings.</summary>
    public async Task<ApiResponse<JsonElement>> SetDefaultPaymentMethodAsync(
        int paymentMethodId, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/payment-methods/{paymentMethodId}/default", ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
