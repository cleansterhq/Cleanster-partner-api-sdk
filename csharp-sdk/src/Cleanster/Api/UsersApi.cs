using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages end-user accounts and bearer tokens.</summary>
public sealed class UsersApi
{
    private readonly ICleansterHttpClient _http;
    internal UsersApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Register a new user account under your partner.</summary>
    /// <param name="email">User email address.</param>
    /// <param name="firstName">First name.</param>
    /// <param name="lastName">Last name.</param>
    /// <param name="phone">Optional phone number — omitted from request if <see langword="null"/>.</param>
    public async Task<ApiResponse<User>> CreateUserAsync(
        string email, string firstName, string lastName,
        string? phone = null, CancellationToken ct = default)
    {
        var body = new Dictionary<string, object?>
        {
            ["email"]     = email,
            ["firstName"] = firstName,
            ["lastName"]  = lastName,
        };
        if (phone is not null) body["phone"] = phone;
        var root = await _http.PostAsync("/v1/user/account", body, ct);
        return JsonHelper.ParseSingle<User>(root);
    }

    /// <summary>
    /// Fetch the long-lived bearer token for a user.
    ///
    /// The returned token is available at <c>response.Data.Token</c>.
    /// Store it and pass it to <see cref="CleansterClient.SetAccessToken"/> for all subsequent requests.
    /// </summary>
    public async Task<ApiResponse<User>> FetchAccessTokenAsync(int userId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/user/access-token/{userId}", ct: ct);
        return JsonHelper.ParseSingle<User>(root);
    }

    /// <summary>Verify that a JWT token is valid and has not expired.</summary>
    public async Task<ApiResponse<JsonElement>> VerifyJwtAsync(
        string token, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/user/verify-jwt", new { token }, ct);
        return JsonHelper.ParseRaw(root);
    }
}
