using System.Text.Json;
using System.Text.Json.Serialization;
using Cleanster.Models;

namespace Cleanster;

/// <summary>Shared JSON parsing utilities used by all API service classes.</summary>
internal static class JsonHelper
{
    internal static readonly JsonSerializerOptions Options = new()
    {
        PropertyNameCaseInsensitive = true,
        DefaultIgnoreCondition      = JsonIgnoreCondition.WhenWritingNull,
    };

    /// <summary>Parse a single model from the "data" field of an API response.</summary>
    internal static ApiResponse<T> ParseSingle<T>(JsonElement root) where T : notnull
    {
        var status  = root.GetProperty("status").GetInt32();
        var message = root.GetProperty("message").GetString() ?? "OK";
        var data    = root.GetProperty("data").Deserialize<T>(Options)
                      ?? throw new InvalidOperationException("API returned null data.");
        return new ApiResponse<T>(status, message, data);
    }

    /// <summary>Parse a list of models from the "data" field of an API response.</summary>
    internal static ApiResponse<List<T>> ParseList<T>(JsonElement root)
    {
        var status  = root.GetProperty("status").GetInt32();
        var message = root.GetProperty("message").GetString() ?? "OK";
        var data    = root.GetProperty("data").Deserialize<List<T>>(Options) ?? [];
        return new ApiResponse<List<T>>(status, message, data);
    }

    /// <summary>Return the "data" field as a raw <see cref="JsonElement"/>.</summary>
    internal static ApiResponse<JsonElement> ParseRaw(JsonElement root)
    {
        var status  = root.GetProperty("status").GetInt32();
        var message = root.GetProperty("message").GetString() ?? "OK";
        var data    = root.GetProperty("data").Clone();
        return new ApiResponse<JsonElement>(status, message, data);
    }
}
