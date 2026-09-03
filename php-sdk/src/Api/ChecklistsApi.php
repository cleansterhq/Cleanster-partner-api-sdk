<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;
use Cleanster\Models\Checklist;

/**
 * Manages cleaning task lists.
 */
final class ChecklistsApi
{
    public function __construct(private readonly HttpClient $http) {}

    /**
     * Return all checklists for the partner account.
     *
     * @return ApiResponse<Checklist[]>
     */
    public function listChecklists(): ApiResponse
    {
        $raw   = $this->http->get('/v1/checklist');
        $items = array_map(fn(array $c) => new Checklist($c), $raw['data'] ?? []);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $items);
    }

    /**
     * Return a specific checklist including all its task items.
     *
     * @return ApiResponse<Checklist>
     */
    public function getChecklist(int $checklistId): ApiResponse
    {
        $raw = $this->http->get("/v1/checklist/{$checklistId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Checklist($raw['data'] ?? []));
    }

    /**
     * Create a new checklist.
     *
     * @param string $title Checklist title.
     * @param array  $tasks Task objects using image_name, title, totalSubtasks,
     *                      subtasks, description, flag_request_photos, and photos.
     *
     * @return ApiResponse<Checklist>
     */
    public function createChecklist(string $title, array $tasks): ApiResponse
    {
        $raw = $this->http->post('/v1/checklist', ['title' => $title, 'tasks' => $tasks]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Checklist($raw['data'] ?? []));
    }

    /**
     * Replace the name and task items of an existing checklist.
     *
     * @param int      $checklistId Checklist to update.
     * @param string $title New checklist title.
     * @param array  $tasks New task objects using the live checklist wire keys.
     *
     * @return ApiResponse<Checklist>
     */
    public function updateChecklist(int $checklistId, string $title, array $tasks): ApiResponse
    {
        $raw = $this->http->put("/v1/checklist/{$checklistId}", ['title' => $title, 'tasks' => $tasks]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Checklist($raw['data'] ?? []));
    }

    /** Permanently delete a checklist. */
    public function deleteChecklist(int $checklistId): ApiResponse
    {
        $raw = $this->http->delete("/v1/checklist/{$checklistId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Upload an image via multipart/form-data.
     *
     * Sends the image as multipart/form-data in the "file" form field.
     *
     * @param  string  $imageData    Raw binary image content.
     * @param  string  $fileName     File name for the multipart part (e.g. "photo.jpg").
     * @return ApiResponse
     */
    public function uploadChecklistImage(string $imageData, string $fileName = 'image.jpg'): ApiResponse
    {
        $raw = $this->http->postMultipart("/v1/checklist/upload-image", $imageData, $fileName);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
