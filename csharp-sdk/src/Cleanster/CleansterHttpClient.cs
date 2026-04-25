using System.Net;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using Cleanster.Exceptions;

namespace Cleanster;

/// <summary>
/// Default HTTP transport — uses <see cref="System.Net.Http.HttpClient"/> with
/// <c>System.Text.Json</c> for serialization. Zero external dependencies.
/// </summary>
internal sealed class CleansterHttpClient : ICleansterHttpClient
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive  = true,
        DefaultIgnoreCondition       = JsonIgnoreCondition.WhenWritingNull,
    };

    private readonly HttpClient _http;
    private readonly CleansterConfig _config;

    private string _token = "";
    private readonly object _tokenLock = new();

    internal CleansterHttpClient(CleansterConfig config)
    {
        _config = config;
        _http   = new HttpClient
        {
            BaseAddress = new Uri(config.BaseUrl.TrimEnd('/') + "/"),
            Timeout     = config.Timeout,
        };
    }

    public void SetToken(string token)
    {
        lock (_tokenLock) { _token = token; }
    }

    public string GetToken()
    {
        lock (_tokenLock) { return _token; }
    }

    public Task<JsonElement> GetAsync(string path, IDictionary<string, string>? query = null, CancellationToken ct = default)
    {
        var url = BuildUrl(path, query);
        return SendRequestAsync(HttpMethod.Get, url, null, ct);
    }

    public Task<JsonElement> PostAsync(string path, object? body = null, CancellationToken ct = default)
        => SendRequestAsync(HttpMethod.Post, path, body, ct);

    public Task<JsonElement> PutAsync(string path, object? body = null, CancellationToken ct = default)
        => SendRequestAsync(HttpMethod.Put, path, body, ct);

    public Task<JsonElement> DeleteAsync(string path, object? body = null, CancellationToken ct = default)
        => SendRequestAsync(HttpMethod.Delete, path, body, ct);

    public async Task<JsonElement> PostMultipartAsync(string path, byte[] imageData, string fileName, CancellationToken ct = default)
    {
        using var content  = new MultipartFormDataContent();
        var imageContent   = new ByteArrayContent(imageData);
        imageContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("image/*");
        content.Add(imageContent, "file", fileName);

        using var req = new HttpRequestMessage(HttpMethod.Post, path.TrimStart('/'))
        {
            Content = content,
        };
        req.Headers.Add("access-key", _config.AccessKey);
        req.Headers.Add("token", GetToken());
        req.Headers.Add("Accept", "application/json");

        HttpResponseMessage resp;
        try
        {
            resp = await _http.SendAsync(req, ct).ConfigureAwait(false);
        }
        catch (TaskCanceledException ex) when (!ct.IsCancellationRequested)
        {
            throw new CleansterException("Request timed out.", ex);
        }
        catch (HttpRequestException ex)
        {
            throw new CleansterException($"Network error: {ex.Message}", ex);
        }

        using (resp)
        {
            var responseBody = await resp.Content.ReadAsStringAsync(ct).ConfigureAwait(false);
            if (resp.StatusCode == HttpStatusCode.Unauthorized)
                throw new AuthException(401, responseBody);
            if (!resp.IsSuccessStatusCode)
                throw new ApiException((int)resp.StatusCode, responseBody);
            try
            {
                using var doc = JsonDocument.Parse(responseBody);
                return doc.RootElement.Clone();
            }
            catch (JsonException ex)
            {
                throw new CleansterException($"Failed to parse response JSON: {ex.Message}", ex);
            }
        }
    }

    public void Dispose() => _http.Dispose();

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static string BuildUrl(string path, IDictionary<string, string>? query)
    {
        if (query is null || query.Count == 0) return path;
        var qs = string.Join("&", query.Select(kv => $"{Uri.EscapeDataString(kv.Key)}={Uri.EscapeDataString(kv.Value)}"));
        return $"{path}?{qs}";
    }

    private async Task<JsonElement> SendRequestAsync(HttpMethod method, string path, object? body, CancellationToken ct)
    {
        using var req = new HttpRequestMessage(method, path.TrimStart('/'));

        req.Headers.Add("access-key", _config.AccessKey);
        req.Headers.Add("token", GetToken());
        req.Headers.Add("Accept", "application/json");

        if (body is not null)
        {
            var json = JsonSerializer.Serialize(body, JsonOptions);
            req.Content = new StringContent(json, Encoding.UTF8, "application/json");
        }

        HttpResponseMessage resp;
        try
        {
            resp = await _http.SendAsync(req, ct).ConfigureAwait(false);
        }
        catch (TaskCanceledException ex) when (!ct.IsCancellationRequested)
        {
            throw new CleansterException("Request timed out.", ex);
        }
        catch (HttpRequestException ex)
        {
            throw new CleansterException($"Network error: {ex.Message}", ex);
        }

        using (resp)
        {
            var responseBody = await resp.Content.ReadAsStringAsync(ct).ConfigureAwait(false);

            if (resp.StatusCode == HttpStatusCode.Unauthorized)
                throw new AuthException(401, responseBody);

            if (!resp.IsSuccessStatusCode)
                throw new ApiException((int)resp.StatusCode, responseBody);

            try
            {
                using var doc = JsonDocument.Parse(responseBody);
                return doc.RootElement.Clone();
            }
            catch (JsonException ex)
            {
                throw new CleansterException($"Failed to parse response JSON: {ex.Message}", ex);
            }
        }
    }
}
