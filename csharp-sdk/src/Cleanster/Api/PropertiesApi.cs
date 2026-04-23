using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages physical cleaning locations.</summary>
public sealed class PropertiesApi
{
    private readonly ICleansterHttpClient _http;
    internal PropertiesApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Return all properties, optionally filtered by service type.</summary>
    /// <param name="serviceId">Pass <see langword="null"/> to return all service types.</param>
    public async Task<ApiResponse<List<Property>>> ListPropertiesAsync(
        int? serviceId = null, CancellationToken ct = default)
    {
        var query = serviceId.HasValue
            ? new Dictionary<string, string> { ["serviceId"] = serviceId.Value.ToString() }
            : null;
        var root = await _http.GetAsync("/v1/properties", query, ct);
        return JsonHelper.ParseList<Property>(root);
    }

    /// <summary>Add a new property to the partner account.</summary>
    /// <param name="name">Property display name.</param>
    /// <param name="address">Street address.</param>
    /// <param name="city">City.</param>
    /// <param name="country">Country.</param>
    /// <param name="roomCount">Number of rooms.</param>
    /// <param name="bathroomCount">Number of bathrooms.</param>
    /// <param name="serviceId">Service type ID from <see cref="OtherApi.GetServicesAsync"/>.</param>
    /// <param name="state">Optional state/province.</param>
    /// <param name="zip">Optional postal/ZIP code.</param>
    /// <param name="timezone">Optional IANA timezone (e.g. "America/New_York").</param>
    /// <param name="note">Optional internal note visible to cleaners.</param>
    /// <param name="latitude">Optional GPS latitude.</param>
    /// <param name="longitude">Optional GPS longitude.</param>
    public async Task<ApiResponse<Property>> AddPropertyAsync(
        string name, string address, string city, string country,
        int roomCount, int bathroomCount, int serviceId,
        string? state = null, string? zip = null, string? timezone = null,
        string? note = null, double? latitude = null, double? longitude = null,
        CancellationToken ct = default)
    {
        var body = new Dictionary<string, object?>
        {
            ["name"]          = name,
            ["address"]       = address,
            ["city"]          = city,
            ["country"]       = country,
            ["roomCount"]     = roomCount,
            ["bathroomCount"] = bathroomCount,
            ["serviceId"]     = serviceId,
        };
        if (state     is not null) body["state"]     = state;
        if (zip       is not null) body["zip"]       = zip;
        if (timezone  is not null) body["timezone"]  = timezone;
        if (note      is not null) body["note"]      = note;
        if (latitude  is not null) body["latitude"]  = latitude;
        if (longitude is not null) body["longitude"] = longitude;
        var root = await _http.PostAsync("/v1/properties", body, ct);
        return JsonHelper.ParseSingle<Property>(root);
    }

    /// <summary>Return details of a specific property.</summary>
    public async Task<ApiResponse<Property>> GetPropertyAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/properties/{propertyId}", ct: ct);
        return JsonHelper.ParseSingle<Property>(root);
    }

    /// <summary>Replace all fields of an existing property.</summary>
    /// <param name="state">Optional state/province.</param>
    /// <param name="zip">Optional postal/ZIP code.</param>
    /// <param name="timezone">Optional IANA timezone (e.g. "America/New_York").</param>
    /// <param name="note">Optional internal note visible to cleaners.</param>
    /// <param name="latitude">Optional GPS latitude.</param>
    /// <param name="longitude">Optional GPS longitude.</param>
    public async Task<ApiResponse<Property>> UpdatePropertyAsync(
        int propertyId, string name, string address, string city, string country,
        int roomCount, int bathroomCount, int serviceId,
        string? state = null, string? zip = null, string? timezone = null,
        string? note = null, double? latitude = null, double? longitude = null,
        CancellationToken ct = default)
    {
        var body = new Dictionary<string, object?>
        {
            ["name"]          = name,
            ["address"]       = address,
            ["city"]          = city,
            ["country"]       = country,
            ["roomCount"]     = roomCount,
            ["bathroomCount"] = bathroomCount,
            ["serviceId"]     = serviceId,
        };
        if (state     is not null) body["state"]     = state;
        if (zip       is not null) body["zip"]       = zip;
        if (timezone  is not null) body["timezone"]  = timezone;
        if (note      is not null) body["note"]      = note;
        if (latitude  is not null) body["latitude"]  = latitude;
        if (longitude is not null) body["longitude"] = longitude;
        var root = await _http.PutAsync($"/v1/properties/{propertyId}", body, ct);
        return JsonHelper.ParseSingle<Property>(root);
    }

    /// <summary>Update freeform additional information for a property.</summary>
    public async Task<ApiResponse<JsonElement>> UpdateAdditionalInformationAsync(
        int propertyId, object data, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/properties/{propertyId}/additional-information", data, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Toggle a property's active state.</summary>
    public async Task<ApiResponse<JsonElement>> EnableOrDisablePropertyAsync(
        int propertyId, bool enabled, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/properties/{propertyId}/enable-disable", new { enabled }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Permanently delete a property.</summary>
    public async Task<ApiResponse<JsonElement>> DeletePropertyAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/properties/{propertyId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return all cleaners currently assigned to a property.</summary>
    public async Task<ApiResponse<JsonElement>> GetPropertyCleanersAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/properties/{propertyId}/cleaners", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Add a cleaner to a property's default cleaner pool.</summary>
    public async Task<ApiResponse<JsonElement>> AssignCleanerToPropertyAsync(
        int propertyId, int cleanerId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/properties/{propertyId}/cleaners", new { cleanerId }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Remove a cleaner from a property's default cleaner pool.</summary>
    public async Task<ApiResponse<JsonElement>> UnassignCleanerFromPropertyAsync(
        int propertyId, int cleanerId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/properties/{propertyId}/cleaners/{cleanerId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Set an iCal feed URL on a property for availability syncing.</summary>
    public async Task<ApiResponse<JsonElement>> AddICalLinkAsync(
        int propertyId, string icalLink, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/properties/{propertyId}/ical", new { icalLink }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return the current iCal feed URL for a property.</summary>
    public async Task<ApiResponse<JsonElement>> GetICalLinkAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/properties/{propertyId}/ical", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Remove the iCal feed URL from a property.</summary>
    public async Task<ApiResponse<JsonElement>> RemoveICalLinkAsync(
        int propertyId, string icalLink, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/properties/{propertyId}/ical", new { icalLink }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Set the default checklist for a property.</summary>
    /// <param name="updateUpcomingBookings">If <see langword="true"/>, also applies to all future bookings at this property.</param>
    public async Task<ApiResponse<JsonElement>> SetDefaultChecklistAsync(
        int propertyId, int checklistId, bool updateUpcomingBookings = false, CancellationToken ct = default)
    {
        var path = $"/v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={updateUpcomingBookings.ToString().ToLower()}";
        var root = await _http.PutAsync(path, ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
