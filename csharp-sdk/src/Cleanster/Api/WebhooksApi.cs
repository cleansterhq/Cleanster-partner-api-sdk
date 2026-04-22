using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages real-time event notification endpoints.</summary>
public sealed class WebhooksApi
{
    private readonly ICleansterHttpClient _http;
    internal WebhooksApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Return all configured webhook endpoints.</summary>
    public async Task<ApiResponse<JsonElement>> ListWebhooksAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/webhooks", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Register a new webhook endpoint for booking event notifications.</summary>
    /// <param name="url">Your HTTPS endpoint URL.</param>
    /// <param name="eventType">Event type (e.g., "booking.status_changed").</param>
    public async Task<ApiResponse<JsonElement>> CreateWebhookAsync(
        string url, string eventType, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/webhooks", new { url, @event = eventType }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Update the URL or event type of an existing webhook.</summary>
    public async Task<ApiResponse<JsonElement>> UpdateWebhookAsync(
        int webhookId, string url, string eventType, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/webhooks/{webhookId}", new { url, @event = eventType }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Remove a webhook endpoint.</summary>
    public async Task<ApiResponse<JsonElement>> DeleteWebhookAsync(int webhookId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/webhooks/{webhookId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
