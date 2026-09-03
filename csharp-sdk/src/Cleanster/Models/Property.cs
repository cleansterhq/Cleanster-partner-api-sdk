using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Represents a physical location where cleanings take place.</summary>
public sealed record Property
{
    [JsonPropertyName("id")]            public int    Id            { get; init; }
    [JsonPropertyName("userId")]        public int?   UserId        { get; init; }
    [JsonPropertyName("name")]          public string Name          { get; init; } = "";
    [JsonPropertyName("nickName")]      public string? NickName     { get; init; }
    [JsonPropertyName("apt")]           public string? Apt          { get; init; }
    [JsonPropertyName("street")]        public string? Street       { get; init; }
    [JsonPropertyName("address")]       public string Address       { get; init; } = "";
    [JsonPropertyName("city")]          public string City          { get; init; } = "";
    [JsonPropertyName("state")]         public string? State        { get; init; }
    [JsonPropertyName("country")]       public string Country       { get; init; } = "";
    [JsonPropertyName("zipCode")]       public string? ZipCode      { get; init; }
    [JsonPropertyName("roomCount")]     public int    RoomCount     { get; init; }
    [JsonPropertyName("bathroomCount")] public int    BathroomCount { get; init; }
    [JsonPropertyName("serviceId")]     public int    ServiceId     { get; init; }
    /// <summary>Active state - <see langword="null"/> when not returned by this endpoint.</summary>
    [JsonPropertyName("isEnabled")]     public bool?  IsEnabled     { get; init; }
    [JsonPropertyName("isActive")]      public bool?  IsActive      { get; init; }
    [JsonPropertyName("isEnable")]      public bool?  IsEnable      { get; init; }
    [JsonPropertyName("pets")]          public string? Pets         { get; init; }
    [JsonPropertyName("publicName")]    public string? PublicName   { get; init; }
    [JsonPropertyName("wifiName")]      public string? WifiName     { get; init; }
    [JsonPropertyName("wifiPassword")]  public string? WifiPassword { get; init; }
    [JsonPropertyName("laundry")]       public bool? Laundry        { get; init; }
    [JsonPropertyName("garbage")]       public string? Garbage      { get; init; }
    [JsonPropertyName("extraSupplies")] public bool? ExtraSupplies  { get; init; }
    [JsonPropertyName("createdDate")]   public string? CreatedDate  { get; init; }
    [JsonPropertyName("access")]        public string? Access       { get; init; }
    [JsonPropertyName("suppliesLocation")] public string? SuppliesLocation { get; init; }
    [JsonPropertyName("parking")]       public string? Parking      { get; init; }
    [JsonPropertyName("otherNote")]     public string? OtherNote    { get; init; }
    [JsonPropertyName("latitude")]      public double? Latitude     { get; init; }
    [JsonPropertyName("longitude")]     public double? Longitude    { get; init; }
}
