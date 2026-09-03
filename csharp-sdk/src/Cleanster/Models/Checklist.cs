using System.Text.Json.Serialization;

namespace Cleanster.Models;

/// <summary>A reusable collection of cleaning tasks.</summary>
public sealed record Checklist
{
    [JsonPropertyName("id")] public int Id { get; init; }
    [JsonPropertyName("is_default")] public bool IsDefault { get; init; }
    [JsonPropertyName("disabled")] public bool Disabled { get; init; }
    [JsonPropertyName("title")] public string Title { get; init; } = "";
    [JsonPropertyName("type")] public string Type { get; init; } = "";
    [JsonPropertyName("totalTasks")] public int TotalTasks { get; init; }
    [JsonPropertyName("totalSubTasks")] public int TotalSubTasks { get; init; }
    [JsonPropertyName("tasks")] public List<ChecklistTask> Tasks { get; init; } = [];

    // Legacy response fields retained for source compatibility.
    [JsonPropertyName("name")] public string Name { get; init; } = "";
    [JsonPropertyName("items")] public List<ChecklistItem> Items { get; init; } = [];
}

/// <summary>A task, including its nested subtasks, in a reusable checklist.</summary>
public sealed record ChecklistTask
{
    [JsonPropertyName("image_name")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? ImageName { get; init; }
    [JsonPropertyName("title")] public string Title { get; init; } = "";
    [JsonPropertyName("totalSubtasks")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public int? TotalSubtasks { get; init; }
    [JsonPropertyName("subtasks")] public List<ChecklistSubtask> Subtasks { get; init; } = [];
}

/// <summary>A subtask in a reusable checklist task.</summary>
public sealed record ChecklistSubtask
{
    [JsonPropertyName("description")] public string Description { get; init; } = "";
    [JsonPropertyName("flag_request_photos")] public bool FlagRequestPhotos { get; init; }
    [JsonPropertyName("photos")] public List<string> Photos { get; init; } = [];
}

/// <summary>The structured checklist body accepted by create and update endpoints.</summary>
public sealed record CreateChecklistRequest
{
    [JsonPropertyName("title")] public string Title { get; init; } = "";
    [JsonPropertyName("tasks")] public List<ChecklistTask> Tasks { get; init; } = [];
}
