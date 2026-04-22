using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages the list of cleaners blocked from auto-assignment.</summary>
public sealed class BlacklistApi
{
    private readonly ICleansterHttpClient _http;
    internal BlacklistApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Return all cleaners currently on the blacklist.</summary>
    public async Task<ApiResponse<JsonElement>> ListBlacklistedCleanersAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/blacklist/cleaner", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Prevent a cleaner from being auto-assigned to bookings.</summary>
    /// <param name="cleanerId">Cleaner to blacklist.</param>
    /// <param name="reason">Optional reason — omitted from request if <see langword="null"/>.</param>
    public async Task<ApiResponse<JsonElement>> AddToBlacklistAsync(
        int cleanerId, string? reason = null, CancellationToken ct = default)
    {
        object body = reason is { Length: > 0 } r
            ? new { cleanerId, reason = r }
            : new { cleanerId };
        var root = await _http.PostAsync("/v1/blacklist/cleaner", body, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Re-enable a previously blacklisted cleaner for auto-assignment.</summary>
    public async Task<ApiResponse<JsonElement>> RemoveFromBlacklistAsync(int cleanerId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync("/v1/blacklist/cleaner", new { cleanerId }, ct);
        return JsonHelper.ParseRaw(root);
    }
}
