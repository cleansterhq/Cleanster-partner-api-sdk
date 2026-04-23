using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages cleaning task lists.</summary>
public sealed class ChecklistsApi
{
    private readonly ICleansterHttpClient _http;
    internal ChecklistsApi(ICleansterHttpClient http) => _http = http;

    /// <summary>Return all checklists for the partner account.</summary>
    public async Task<ApiResponse<List<Checklist>>> ListChecklistsAsync(CancellationToken ct = default)
    {
        var root = await _http.GetAsync("/v1/checklist", ct: ct);
        return JsonHelper.ParseList<Checklist>(root);
    }

    /// <summary>Return a specific checklist including all its typed task items.</summary>
    public async Task<ApiResponse<Checklist>> GetChecklistAsync(int checklistId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/checklist/{checklistId}", ct: ct);
        return JsonHelper.ParseSingle<Checklist>(root);
    }

    /// <summary>Create a new checklist.</summary>
    /// <param name="name">Checklist name.</param>
    /// <param name="items">Array of task description strings.</param>
    public async Task<ApiResponse<Checklist>> CreateChecklistAsync(
        string name, IEnumerable<string> items, CancellationToken ct = default)
    {
        var root = await _http.PostAsync("/v1/checklist", new { name, items = items.ToArray() }, ct);
        return JsonHelper.ParseSingle<Checklist>(root);
    }

    /// <summary>Replace the name and task items of an existing checklist.</summary>
    public async Task<ApiResponse<Checklist>> UpdateChecklistAsync(
        int checklistId, string name, IEnumerable<string> items, CancellationToken ct = default)
    {
        var root = await _http.PutAsync($"/v1/checklist/{checklistId}",
            new { name, items = items.ToArray() }, ct);
        return JsonHelper.ParseSingle<Checklist>(root);
    }

    /// <summary>Permanently delete a checklist.</summary>
    public async Task<ApiResponse<JsonElement>> DeleteChecklistAsync(
        int checklistId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/checklist/{checklistId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>
    /// Upload an image for a checklist.
    /// Sends the image as multipart/form-data in the <c>image</c> form field.
    /// </summary>
    /// <param name="checklistId">The checklist ID.</param>
    /// <param name="imageData">Raw bytes of the image to upload.</param>
    /// <param name="fileName">File name for the multipart part (e.g. "photo.jpg").</param>
    public async Task<ApiResponse<JsonElement>> UploadChecklistImageAsync(
        int checklistId, byte[] imageData, string fileName = "image.jpg", CancellationToken ct = default)
    {
        var root = await _http.PostMultipartAsync($"/v1/checklist/{checklistId}/upload", imageData, fileName, ct);
        return JsonHelper.ParseRaw(root);
    }
}
