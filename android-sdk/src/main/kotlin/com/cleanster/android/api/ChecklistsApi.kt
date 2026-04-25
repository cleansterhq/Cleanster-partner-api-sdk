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

    suspend fun createChecklist(name: String, items: List<String>) =
        wrap { service.createChecklist(CreateChecklistRequest(name, items)) }

    suspend fun updateChecklist(checklistId: Int, name: String, items: List<String>) =
        wrap { service.updateChecklist(checklistId, CreateChecklistRequest(name, items)) }

    suspend fun deleteChecklist(checklistId: Int) = wrap { service.deleteChecklist(checklistId) }

    suspend fun uploadChecklistImage(imageBytes: ByteArray, fileName: String): ApiResponse<Any> {
        val requestBody = imageBytes.toRequestBody()
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
        return wrap { service.uploadChecklistImage(part) }
    }
}
