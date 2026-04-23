package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface WebhooksService {
    @GET("v1/webhooks")
    suspend fun listWebhooks(): ApiResponse<List<Webhook>>

    @POST("v1/webhooks")
    suspend fun createWebhook(@Body body: CreateWebhookRequest): ApiResponse<Webhook>

    @PUT("v1/webhooks/{webhookId}")
    suspend fun updateWebhook(
        @Path("webhookId") webhookId: Int,
        @Body body: CreateWebhookRequest,
    ): ApiResponse<Webhook>

    @DELETE("v1/webhooks/{webhookId}")
    suspend fun deleteWebhook(@Path("webhookId") webhookId: Int): ApiResponse<Any>
}

class WebhooksApi(retrofit: Retrofit) {
    private val service = retrofit.create(WebhooksService::class.java)

    suspend fun listWebhooks() = wrap { service.listWebhooks() }

    suspend fun createWebhook(url: String, event: String) =
        wrap { service.createWebhook(CreateWebhookRequest(url, event)) }

    suspend fun updateWebhook(webhookId: Int, url: String, event: String) =
        wrap { service.updateWebhook(webhookId, CreateWebhookRequest(url, event)) }

    suspend fun deleteWebhook(webhookId: Int) = wrap { service.deleteWebhook(webhookId) }
}
