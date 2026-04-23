package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for cleaning checklists: list, get by ID, create, update, and delete.
 */
public class ChecklistApi {

    private final HttpClient httpClient;

    public ChecklistApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * List all checklists.
     *
     * @return API response with list of checklists
     */
    public ApiResponse<Object> listChecklists() {
        return httpClient.get("/v1/checklist", new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get a specific checklist by ID.
     *
     * @param checklistId The checklist ID
     * @return API response with checklist details
     */
    public ApiResponse<Checklist> getChecklist(int checklistId) {
        return httpClient.get("/v1/checklist/" + checklistId,
                new TypeReference<ApiResponse<Checklist>>() {});
    }

    /**
     * Create a new checklist.
     *
     * @param request Checklist name and items
     * @return API response with created checklist
     */
    public ApiResponse<Checklist> createChecklist(CreateChecklistRequest request) {
        return httpClient.post("/v1/checklist", request,
                new TypeReference<ApiResponse<Checklist>>() {});
    }

    /**
     * Update an existing checklist.
     *
     * @param checklistId The checklist ID
     * @param request     Updated checklist data
     * @return API response
     */
    public ApiResponse<Checklist> updateChecklist(int checklistId, CreateChecklistRequest request) {
        return httpClient.put("/v1/checklist/" + checklistId, request,
                new TypeReference<ApiResponse<Checklist>>() {});
    }

    /**
     * Delete a checklist.
     *
     * @param checklistId The checklist ID
     * @return API response
     */
    public ApiResponse<Object> deleteChecklist(int checklistId) {
        return httpClient.delete("/v1/checklist/" + checklistId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Upload an image for a checklist.
     *
     * <p>Sends the image as multipart/form-data in the {@code image} form field.
     * The {@code fileName} is used as the part name (e.g. "photo.jpg").</p>
     *
     * @param checklistId The checklist ID
     * @param imageBytes  Raw bytes of the image to upload
     * @param fileName    File name for the multipart part (e.g. "photo.jpg")
     * @return API response
     */
    public ApiResponse<Object> uploadChecklistImage(int checklistId, byte[] imageBytes, String fileName) {
        return httpClient.postMultipart(
                "/v1/checklist/" + checklistId + "/upload",
                imageBytes,
                fileName,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
