using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Represents a Cleanster end-user account.</summary>
public sealed record User
{
    [JsonPropertyName("id")]        public int     Id        { get; init; }
    [JsonPropertyName("email")]     public string  Email     { get; init; } = "";
    [JsonPropertyName("firstName")] public string  FirstName { get; init; } = "";
    [JsonPropertyName("lastName")]  public string  LastName  { get; init; } = "";
    /// <summary>Optional phone number — <see langword="null"/> if not provided.</summary>
    [JsonPropertyName("phone")]     public string? Phone     { get; init; }
    /// <summary>Bearer token — present only after <c>FetchAccessTokenAsync</c>.</summary>
    [JsonPropertyName("token")]     public string? Token     { get; init; }
}
