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
    public async Task<ApiResponse<Property>> AddPropertyAsync(
        string name, string address, string city, string country,
        int roomCount, int bathroomCount, int serviceId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/properties",
            new { name, address, city, country, roomCount, bathroomCount, serviceId }, ct);
        return JsonHelper.ParseSingle<Property>(root);
    }

    /// <summary>Return details of a specific property.</summary>
    public async Task<ApiResponse<Property>> GetPropertyAsync(int propertyId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/properties/{propertyId}", ct: ct);
        return JsonHelper.ParseSingle<Property>(root);
    }

    /// <summary>Replace all fields of an existing property.</summary>
    public async Task<ApiResponse<Property>> UpdatePropertyAsync(
        int propertyId, string name, string address, string city, string country,
        int roomCount, int bathroomCount, int serviceId, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/properties/{propertyId}",
            new { name, address, city, country, roomCount, bathroomCount, serviceId }, ct);
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
    public async Task<ApiResponse<JsonElement>> AssignChecklistToPropertyAsync(
        int propertyId, int checklistId, bool updateUpcomingBookings = false, CancellationToken ct = default)
    {
        var path = $"/v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={updateUpcomingBookings.ToString().ToLower()}";
        var root = await _http.PutAsync(path, ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
