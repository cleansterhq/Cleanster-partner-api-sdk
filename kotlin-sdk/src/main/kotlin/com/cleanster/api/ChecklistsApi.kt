package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for managing reusable cleaning task lists. */
class ChecklistsApi internal constructor(private val client: CleansterClient) {

    /** List all checklists on the partner account. */
    suspend fun listChecklists(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/checklist",
    )

    /** Retrieve a single checklist and all its items. */
    suspend fun getChecklist(checklistId: Int): ApiResponse<Checklist> = client.request(
        method = "GET",
        path   = "/v1/checklist/$checklistId",
    )

    /**
     * Create a new checklist.
     *
     * @param name  Display name.
     * @param items Array of task description strings.
     */
    suspend fun createChecklist(name: String, items: List<String>): ApiResponse<Checklist> = client.request(
        method = "POST",
        path   = "/v1/checklist",
        body   = CreateChecklistRequest(name = name, items = items),
    )

    /** Replace an existing checklist's name and items entirely. */
    suspend fun updateChecklist(checklistId: Int, name: String, items: List<String>): ApiResponse<Checklist> = client.request(
        method = "PUT",
        path   = "/v1/checklist/$checklistId",
        body   = CreateChecklistRequest(name = name, items = items),
    )

    /** Permanently delete a checklist. */
    suspend fun deleteChecklist(checklistId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/checklist/$checklistId",
    )
}
