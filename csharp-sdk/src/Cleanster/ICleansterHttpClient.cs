using System.Text.Json;

namespace Cleanster;

/// <summary>
/// HTTP transport abstraction for the Cleanster SDK.
///
/// The default implementation uses <see cref="System.Net.Http.HttpClient"/> with cURL-style
/// per-request auth headers. Implement this interface to substitute a custom transport
/// (e.g., a Polly-wrapped retry client or a mock for unit testing).
/// </summary>
public interface ICleansterHttpClient : IDisposable
{
    /// <summary>Send a GET request and return the parsed JSON response body.</summary>
    Task<JsonElement> GetAsync(string path, IDictionary<string, string>? query = null, CancellationToken ct = default);

    /// <summary>Send a POST request and return the parsed JSON response body.</summary>
    Task<JsonElement> PostAsync(string path, object? body = null, CancellationToken ct = default);

    /// <summary>Send a PUT request and return the parsed JSON response body.</summary>
    Task<JsonElement> PutAsync(string path, object? body = null, CancellationToken ct = default);

    /// <summary>Send a DELETE request and return the parsed JSON response body.</summary>
    Task<JsonElement> DeleteAsync(string path, object? body = null, CancellationToken ct = default);

    /// <summary>Set the user bearer token sent as the "token" header on every request.</summary>
    void SetToken(string token);

    /// <summary>Return the currently active bearer token.</summary>
    string GetToken();
}
