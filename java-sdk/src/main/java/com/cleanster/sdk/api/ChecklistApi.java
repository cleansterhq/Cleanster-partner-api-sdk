package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for cleaning checklists: list, get by ID, create, update, delete, and upload image.
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
}
