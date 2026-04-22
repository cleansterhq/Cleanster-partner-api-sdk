using Cleanster.Api;

namespace Cleanster;

/// <summary>
/// Main entry point for the Cleanster Partner API SDK.
///
/// Create via <see cref="Sandbox"/> or <see cref="Production"/> factory methods.
/// After creating a user, call <see cref="SetAccessToken"/> with the user bearer token
/// so every subsequent request includes it automatically.
///
/// <code>
/// var client = CleansterClient.Sandbox(Environment.GetEnvironmentVariable("CLEANSTER_API_KEY")!);
///
/// var userResp  = await client.Users.CreateUserAsync("jane@example.com", "Jane", "Smith");
/// var tokenResp = await client.Users.FetchAccessTokenAsync(userResp.Data.Id);
/// client.SetAccessToken(tokenResp.Data.Token!);
///
/// var bookings = await client.Bookings.GetBookingsAsync();
/// </code>
/// </summary>
public sealed class CleansterClient : IDisposable
{
    private readonly ICleansterHttpClient _http;

    /// <summary>Booking lifecycle and management.</summary>
    public BookingsApi        Bookings       { get; }

    /// <summary>User account creation and bearer-token management.</summary>
    public UsersApi           Users          { get; }

    /// <summary>Property (location) management.</summary>
    public PropertiesApi      Properties     { get; }

    /// <summary>Cleaning task list management.</summary>
    public ChecklistsApi      Checklists     { get; }

    /// <summary>Reference data and utility endpoints.</summary>
    public OtherApi           Other          { get; }

    /// <summary>Cleaner blacklist management.</summary>
    public BlacklistApi       Blacklist      { get; }

    /// <summary>Stripe and PayPal payment method management.</summary>
    public PaymentMethodsApi  PaymentMethods { get; }

    /// <summary>Webhook endpoint management.</summary>
    public WebhooksApi        Webhooks       { get; }

    /// <summary>
    /// Create a client using an explicit config and optional custom HTTP transport.
    /// Pass a custom <paramref name="httpClient"/> for unit testing.
    /// </summary>
    public CleansterClient(CleansterConfig config, ICleansterHttpClient? httpClient = null)
    {
        _http          = httpClient ?? new CleansterHttpClient(config);
        Bookings       = new BookingsApi(_http);
        Users          = new UsersApi(_http);
        Properties     = new PropertiesApi(_http);
        Checklists     = new ChecklistsApi(_http);
        Other          = new OtherApi(_http);
        Blacklist      = new BlacklistApi(_http);
        PaymentMethods = new PaymentMethodsApi(_http);
        Webhooks       = new WebhooksApi(_http);
    }

    /// <summary>Create a client configured for the sandbox environment (no real charges).</summary>
    public static CleansterClient Sandbox(string accessKey)
        => new(CleansterConfig.Sandbox(accessKey));

    /// <summary>Create a client configured for the production environment (live traffic).</summary>
    public static CleansterClient Production(string accessKey)
        => new(CleansterConfig.Production(accessKey));

    /// <summary>
    /// Set the user bearer token for all subsequent requests.
    ///
    /// Call this after <see cref="UsersApi.FetchAccessTokenAsync"/> returns a token.
    /// The token is long-lived — store it and reuse it across requests.
    /// </summary>
    public void SetAccessToken(string token) => _http.SetToken(token);

    /// <summary>Return the currently active bearer token, or <see cref="string.Empty"/> if not set.</summary>
    public string GetAccessToken() => _http.GetToken();

    /// <inheritdoc/>
    public void Dispose() => _http.Dispose();
}
