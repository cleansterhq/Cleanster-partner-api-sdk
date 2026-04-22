using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Represents a physical location where cleanings take place.</summary>
public sealed record Property
{
    [JsonPropertyName("id")]            public int    Id            { get; init; }
    [JsonPropertyName("name")]          public string Name          { get; init; } = "";
    [JsonPropertyName("address")]       public string Address       { get; init; } = "";
    [JsonPropertyName("city")]          public string City          { get; init; } = "";
    [JsonPropertyName("country")]       public string Country       { get; init; } = "";
    [JsonPropertyName("roomCount")]     public int    RoomCount     { get; init; }
    [JsonPropertyName("bathroomCount")] public int    BathroomCount { get; init; }
    [JsonPropertyName("serviceId")]     public int    ServiceId     { get; init; }
    /// <summary>Active state — <see langword="null"/> when not returned by this endpoint.</summary>
    [JsonPropertyName("isEnabled")]     public bool?  IsEnabled     { get; init; }
}
