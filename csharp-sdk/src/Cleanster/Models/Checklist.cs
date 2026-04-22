using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>A named collection of cleaning task items.</summary>
public sealed record Checklist
{
    [JsonPropertyName("id")]    public int               Id    { get; init; }
    [JsonPropertyName("name")]  public string            Name  { get; init; } = "";
    [JsonPropertyName("items")] public List<ChecklistItem> Items { get; init; } = [];
}
