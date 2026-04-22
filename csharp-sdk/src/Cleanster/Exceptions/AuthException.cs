namespace Cleanster.Exceptions;

/// <summary>
/// Thrown when the API responds with HTTP 401 Unauthorized.
///
/// Indicates an invalid or missing partner access key or user bearer token.
/// Inspect <see cref="ResponseBody"/> for the raw error message from the API.
/// </summary>
public sealed class AuthException : CleansterException
{
    /// <summary>HTTP status code — always 401.</summary>
    public int StatusCode { get; }

    /// <summary>Raw response body returned by the API.</summary>
    public string ResponseBody { get; }

    /// <param name="statusCode">HTTP status code (always 401).</param>
    /// <param name="responseBody">Raw response body from the API.</param>
    /// <param name="message">Human-readable description.</param>
    public AuthException(
        int    statusCode   = 401,
        string responseBody = "",
        string message      = "Authentication failed (HTTP 401).")
        : base(message)
    {
        StatusCode   = statusCode;
        ResponseBody = responseBody;
    }
}
