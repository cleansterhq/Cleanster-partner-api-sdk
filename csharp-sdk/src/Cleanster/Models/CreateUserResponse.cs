using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Response returned when registering a new user account.</summary>
public sealed record CreateUserResponse
{
    [JsonPropertyName("userId")]      public int     UserId      { get; init; }
    [JsonPropertyName("accessToken")] public string? AccessToken { get; init; }
}
