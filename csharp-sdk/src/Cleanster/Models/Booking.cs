using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Represents a single cleaning appointment.</summary>
public sealed record Booking
{
    [JsonPropertyName("id")]              public int     Id              { get; init; }
    [JsonPropertyName("status")]          public string  Status          { get; init; } = "";
    [JsonPropertyName("date")]            public string  Date            { get; init; } = "";
    [JsonPropertyName("time")]            public string  Time            { get; init; } = "";
    [JsonPropertyName("hours")]           public double  Hours           { get; init; }
    [JsonPropertyName("cost")]            public double  Cost            { get; init; }
    [JsonPropertyName("propertyId")]      public int     PropertyId      { get; init; }
    [JsonPropertyName("cleanerId")]       public int?    CleanerId       { get; init; }
    [JsonPropertyName("planId")]          public int     PlanId          { get; init; }
    [JsonPropertyName("roomCount")]       public int     RoomCount       { get; init; }
    [JsonPropertyName("bathroomCount")]   public int     BathroomCount   { get; init; }
    [JsonPropertyName("extraSupplies")]   public bool    ExtraSupplies   { get; init; }
    [JsonPropertyName("paymentMethodId")] public int     PaymentMethodId { get; init; }
}
