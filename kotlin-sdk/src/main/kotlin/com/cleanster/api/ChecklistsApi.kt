package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for managing reusable cleaning task lists. */
class ChecklistsApi internal constructor(private val client: CleansterClient) {

    /** List all checklists on the partner account. */
    suspend fun listChecklists(): ApiResponse<List<Checklist>> = client.request(
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
     * @param title Display title.
     * @param tasks Structured checklist tasks.
     */
    suspend fun createChecklist(title: String, tasks: List<*>): ApiResponse<Checklist> = client.request(
        method = "POST",
        path   = "/v1/checklist",
        body   = CreateChecklistRequest(title = title, tasks = tasks.toChecklistTasks()),
    )

    /** Replace an existing checklist's title and tasks entirely. */
    suspend fun updateChecklist(checklistId: Int, title: String, tasks: List<*>): ApiResponse<Checklist> = client.request(
        method = "PUT",
        path   = "/v1/checklist/$checklistId",
        body   = CreateChecklistRequest(title = title, tasks = tasks.toChecklistTasks()),
    )

    /** Permanently delete a checklist. */
    suspend fun deleteChecklist(checklistId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/checklist/$checklistId",
    )

    /**
     * Upload an image via multipart/form-data.
     *
     * Sends the image as multipart/form-data in the `file` form field.
     *
     * @param imageData   Raw bytes of the image to upload.
     * @param fileName    File name for the multipart part (e.g. "photo.jpg").
     */
    suspend fun uploadChecklistImage(
        imageData:   ByteArray,
        fileName:    String = "image.jpg",
    ): ApiResponse<Map<String, Any>> = client.requestMultipart(
        path      = "/v1/checklist/upload-image",
        imageData = imageData,
        fileName  = fileName,
    )

    private fun List<*>.toChecklistTasks(): List<ChecklistTask> = map {
        when (it) {
            is ChecklistTask -> it
            is String -> ChecklistTask(title = it)
            else -> throw IllegalArgumentException("Checklist tasks must be ChecklistTask instances")
        }
    }
}
