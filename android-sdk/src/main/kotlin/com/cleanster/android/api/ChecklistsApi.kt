package com.cleanster.android.api

import com.cleanster.android.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.http.*

internal interface ChecklistsService {
    @GET("v1/checklist")
    suspend fun listChecklists(): ApiResponse<List<Checklist>>

    @GET("v1/checklist/{checklistId}")
    suspend fun getChecklist(@Path("checklistId") checklistId: Int): ApiResponse<Checklist>

    @POST("v1/checklist")
    suspend fun createChecklist(@Body body: CreateChecklistRequest): ApiResponse<Checklist>

    @PUT("v1/checklist/{checklistId}")
    suspend fun updateChecklist(
        @Path("checklistId") checklistId: Int,
        @Body body: CreateChecklistRequest,
    ): ApiResponse<Checklist>

    @DELETE("v1/checklist/{checklistId}")
    suspend fun deleteChecklist(@Path("checklistId") checklistId: Int): ApiResponse<Any>

    @Multipart
    @POST("v1/checklist/upload-image")
    suspend fun uploadChecklistImage(
        @Part file: MultipartBody.Part,
    ): ApiResponse<Any>
}

class ChecklistsApi(retrofit: Retrofit) {
    private val service = retrofit.create(ChecklistsService::class.java)

    suspend fun listChecklists() = wrap { service.listChecklists() }

    suspend fun getChecklist(checklistId: Int) = wrap { service.getChecklist(checklistId) }

    suspend fun createChecklist(title: String, tasks: List<*>) =
        wrap { service.createChecklist(CreateChecklistRequest(title, tasks.toChecklistTasks())) }

    suspend fun updateChecklist(checklistId: Int, title: String, tasks: List<*>) =
        wrap { service.updateChecklist(checklistId, CreateChecklistRequest(title, tasks.toChecklistTasks())) }

    suspend fun deleteChecklist(checklistId: Int) = wrap { service.deleteChecklist(checklistId) }

    suspend fun uploadChecklistImage(imageBytes: ByteArray, fileName: String): ApiResponse<Any> {
        val requestBody = imageBytes.toRequestBody()
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
        return wrap { service.uploadChecklistImage(part) }
    }

    private fun List<*>.toChecklistTasks(): List<ChecklistTask> = map {
        when (it) {
            is ChecklistTask -> it
            is String -> ChecklistTask(title = it)
            else -> throw IllegalArgumentException("Checklist tasks must be ChecklistTask instances")
        }
    }
}
