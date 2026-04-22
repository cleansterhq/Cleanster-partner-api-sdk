namespace Cleanster;

/// <summary>
/// Configuration for <see cref="CleansterClient"/>.
/// Create via <see cref="Sandbox"/> or <see cref="Production"/> factory methods,
/// or construct directly for custom base URLs or timeouts.
/// </summary>
public sealed class CleansterConfig
{
    /// <summary>Base URL for the sandbox environment (development/testing — no real charges).</summary>
    public const string SandboxBaseUrl =
        "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public";

    /// <summary>Base URL for the production environment (live traffic — real cleaners, real charges).</summary>
    public const string ProductionBaseUrl =
        "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public";

    /// <summary>Default HTTP request timeout (30 seconds).</summary>
    public static readonly TimeSpan DefaultTimeout = TimeSpan.FromSeconds(30);

    /// <summary>Your partner access key — sent as the "access-key" header on every request.</summary>
    public string AccessKey { get; }

    /// <summary>API base URL.</summary>
    public string BaseUrl { get; }

    /// <summary>HTTP request timeout.</summary>
    public TimeSpan Timeout { get; }

    /// <summary>
    /// Create a config with explicit values.
    /// </summary>
    /// <param name="accessKey">Your partner access key (must not be blank).</param>
    /// <param name="baseUrl">API base URL (must not be blank).</param>
    /// <param name="timeout">HTTP timeout (defaults to <see cref="DefaultTimeout"/>).</param>
    /// <exception cref="ArgumentException">Thrown when <paramref name="accessKey"/> or <paramref name="baseUrl"/> is blank.</exception>
    public CleansterConfig(string accessKey, string baseUrl, TimeSpan? timeout = null)
    {
        if (string.IsNullOrWhiteSpace(accessKey))
            throw new ArgumentException("Cleanster: accessKey must not be blank.", nameof(accessKey));
        if (string.IsNullOrWhiteSpace(baseUrl))
            throw new ArgumentException("Cleanster: baseUrl must not be blank.", nameof(baseUrl));

        AccessKey = accessKey;
        BaseUrl   = baseUrl;
        Timeout   = timeout ?? DefaultTimeout;
    }

    /// <summary>Returns a config pre-configured for the sandbox environment.</summary>
    public static CleansterConfig Sandbox(string accessKey)
        => new(accessKey, SandboxBaseUrl);

    /// <summary>Returns a config pre-configured for the production environment.</summary>
    public static CleansterConfig Production(string accessKey)
        => new(accessKey, ProductionBaseUrl);
}
