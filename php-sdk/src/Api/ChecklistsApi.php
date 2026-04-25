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
     * @param string   $name  Checklist name.
     * @param string[] $items Array of task description strings.
     *
     * @return ApiResponse<Checklist>
     */
    public function createChecklist(string $name, array $items): ApiResponse
    {
        $raw = $this->http->post('/v1/checklist', ['name' => $name, 'items' => $items]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Checklist($raw['data'] ?? []));
    }

    /**
     * Replace the name and task items of an existing checklist.
     *
     * @param int      $checklistId Checklist to update.
     * @param string   $name        New name.
     * @param string[] $items       New task description strings.
     *
     * @return ApiResponse<Checklist>
     */
    public function updateChecklist(int $checklistId, string $name, array $items): ApiResponse
    {
        $raw = $this->http->put("/v1/checklist/{$checklistId}", ['name' => $name, 'items' => $items]);
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
