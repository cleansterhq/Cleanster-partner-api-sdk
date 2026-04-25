using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Utility and reference data endpoints used when building booking flows.</summary>
public sealed class OtherApi
{
    private readonly ICleansterHttpClient _http;
    internal OtherApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Return all available cleaning service types.</summary>
    public async Task<ApiResponse<JsonElement>> GetServicesAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/services", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return all available booking plans for a given property.</summary>
    public async Task<ApiResponse<JsonElement>> GetPlansAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/plans",
            new Dictionary<string, string> { ["propertyId"] = propertyId.ToString() }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>
    /// Return the recommended number of cleaning hours based on property size.
    /// Use the result to pre-fill <c>hours</c> in <c>Bookings.CreateBookingAsync</c>.
    /// </summary>
    public async Task<ApiResponse<JsonElement>> GetRecommendedHoursAsync(
        int propertyId, int bathroomCount, int roomCount, CancellationToken ct = default)
    {
        var query = new Dictionary<string, string>
        {
            ["propertyId"]    = propertyId.ToString(),
            ["bathroomCount"] = bathroomCount.ToString(),
            ["roomCount"]     = roomCount.ToString(),
        };
        var root = await _http.GetAsync("/v1/recommended-hours", query, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Calculate the estimated price for a potential booking.</summary>
    /// <param name="propertyId">Property ID.</param>
    /// <param name="planId">Plan ID from <see cref="GetPlansAsync"/>.</param>
    /// <param name="hours">Cleaning duration.</param>
    /// <param name="couponCode">Optional discount coupon code.</param>
    public async Task<ApiResponse<JsonElement>> GetCostEstimateAsync(
        int propertyId, int planId, double hours,
        string? couponCode = null, CancellationToken ct = default)
    {
        var body = new Dictionary<string, object?>
        {
            ["propertyId"] = propertyId,
            ["planId"]     = planId,
            ["hours"]      = hours,
        };
        if (couponCode is not null) body["couponCode"] = couponCode;
        var root = await _http.PostAsync("/v1/cost-estimate", body, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return available add-on services for a given service type.</summary>
    public async Task<ApiResponse<JsonElement>> GetCleaningExtrasAsync(int serviceId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/cleaning-extras/{serviceId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Find cleaners available for a specific property, date, and time slot.</summary>
    public async Task<ApiResponse<JsonElement>> GetAvailableCleanersAsync(
        int propertyId, string date, string time, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/available-cleaners",
            new { propertyId, date, time }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return all valid coupon codes.</summary>
    public async Task<ApiResponse<JsonElement>> GetCouponsAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/coupons", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>List all cleaners, with optional status and search filters.</summary>
    /// <param name="status">Filter by cleaner status ('active', 'inactive', 'pending').</param>
    /// <param name="search">Partial match against cleaner name or email.</param>
    public async Task<ApiResponse<JsonElement>> ListCleanersAsync(
        string? status = null, string? search = null, CancellationToken ct = default)
    {
        var query = new Dictionary<string, string>();
        if (status is not null) query["status"] = status;
        if (search is not null) query["search"] = search;
        var root = await _http.GetAsync("/v1/cleaners", query.Count > 0 ? query : null, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Retrieve a single cleaner by their ID.</summary>
    /// <param name="cleanerId">The cleaner's unique ID.</param>
    public async Task<ApiResponse<JsonElement>> GetCleanerAsync(int cleanerId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/cleaners/{cleanerId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
