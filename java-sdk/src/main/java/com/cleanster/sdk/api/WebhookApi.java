package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for webhook management: list, create, update, and delete webhooks.
 *
 * <p>Webhooks allow you to receive real-time notifications when booking events occur
 * (e.g., booking created, status changed, cleaner assigned).</p>
 */
public class WebhookApi {

    private final HttpClient httpClient;

    public WebhookApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * List all configured webhooks.
     *
     * @return API response with list of webhooks
     */
    public ApiResponse<Object> listWebhooks() {
        return httpClient.get("/v1/webhooks", new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Create a new webhook endpoint.
     *
     * @param request Webhook configuration (url, events)
     * @return API response with created webhook
     */
    public ApiResponse<Object> createWebhook(Object request) {
        return httpClient.post("/v1/webhooks", request, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Update an existing webhook.
     *
     * @param webhookId The webhook ID
     * @param request   Updated webhook configuration
     * @return API response
     */
    public ApiResponse<Object> updateWebhook(int webhookId, Object request) {
        return httpClient.put("/v1/webhooks/" + webhookId, request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Delete a webhook.
     *
     * @param webhookId The webhook ID
     * @return API response
     */
    public ApiResponse<Object> deleteWebhook(int webhookId) {
        return httpClient.delete("/v1/webhooks/" + webhookId,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
