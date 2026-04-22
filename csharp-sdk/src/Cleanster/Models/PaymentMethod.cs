using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>Represents a saved Stripe card or PayPal payment method.</summary>
public sealed record PaymentMethod
{
    [JsonPropertyName("id")]        public int     Id        { get; init; }
    [JsonPropertyName("type")]      public string  Type      { get; init; } = "";
    /// <summary>Last 4 digits — present for card payment methods, <see langword="null"/> for others.</summary>
    [JsonPropertyName("lastFour")]  public string? LastFour  { get; init; }
    /// <summary>Card brand (e.g., "visa", "mastercard") — <see langword="null"/> for non-card methods.</summary>
    [JsonPropertyName("brand")]     public string? Brand     { get; init; }
    [JsonPropertyName("isDefault")] public bool    IsDefault { get; init; }
}
