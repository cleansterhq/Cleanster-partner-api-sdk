namespace Cleanster.Exceptions;

/// <summary>
/// Thrown when the API responds with a non-2xx HTTP status other than 401.
///
/// Check <see cref="StatusCode"/> to distinguish client errors (4xx) from
/// server errors (5xx). The raw response body is available via <see cref="ResponseBody"/>.
/// </summary>
public sealed class ApiException : CleansterException
{
    /// <summary>HTTP status code (e.g., 404, 422, 500).</summary>
    public int StatusCode { get; }

    /// <summary>Raw response body returned by the API.</summary>
    public string ResponseBody { get; }

    /// <param name="statusCode">HTTP status code.</param>
    /// <param name="responseBody">Raw response body from the API.</param>
    /// <param name="message">Human-readable description (auto-generated if blank).</param>
    public ApiException(
        int    statusCode,
        string responseBody = "",
        string message      = "")
        : base(string.IsNullOrEmpty(message) ? $"API error (HTTP {statusCode})" : message)
    {
        StatusCode   = statusCode;
        ResponseBody = responseBody;
    }
}
