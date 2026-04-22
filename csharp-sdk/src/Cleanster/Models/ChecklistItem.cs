using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>A single task item within a <see cref="Checklist"/>.</summary>
public sealed record ChecklistItem
{
    [JsonPropertyName("id")]          public int     Id          { get; init; }
    [JsonPropertyName("description")] public string  Description { get; init; } = "";
    [JsonPropertyName("isCompleted")] public bool    IsCompleted { get; init; }
    /// <summary>URL of the proof photo uploaded by the cleaner, or <see langword="null"/> if not uploaded.</summary>
    [JsonPropertyName("imageUrl")]    public string? ImageUrl    { get; init; }
}
