package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for registering and managing webhook endpoints. */
class WebhooksApi internal constructor(private val client: CleansterClient) {

    /** List all registered webhook endpoints. */
    suspend fun listWebhooks(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/webhooks",
    )

    /**
     * Register a new webhook endpoint.
     *
     * @param url   HTTPS URL that will receive POST payloads.
     * @param event Event name to subscribe to (e.g. `"booking.completed"`).
     */
    suspend fun createWebhook(url: String, event: String): ApiResponse<Webhook> = client.request(
        method = "POST",
        path   = "/v1/webhooks",
        body   = CreateWebhookRequest(url = url, event = event),
    )

    /** Update an existing webhook's URL or event. */
    suspend fun updateWebhook(webhookId: Int, url: String, event: String): ApiResponse<Webhook> = client.request(
        method = "PUT",
        path   = "/v1/webhooks/$webhookId",
        body   = CreateWebhookRequest(url = url, event = event),
    )

    /** Delete a webhook endpoint. */
    suspend fun deleteWebhook(webhookId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/webhooks/$webhookId",
    )
}
