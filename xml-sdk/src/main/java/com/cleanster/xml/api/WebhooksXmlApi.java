package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Webhook;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Webhooks API - register and manage webhook endpoints for real-time events.
 *
 * <h3>Endpoints (4)</h3>
 * <ol>
 *   <li>GET    /v1/webhooks       - list webhooks</li>
 *   <li>POST   /v1/webhooks       - create webhook</li>
 *   <li>PUT    /v1/webhooks/{id}  - update webhook</li>
 *   <li>DELETE /v1/webhooks/{id}  - delete webhook</li>
 * </ol>
 */
public class WebhooksXmlApi {

    private final XmlHttpClient http;

    public WebhooksXmlApi(XmlHttpClient http) { this.http = http; }

    /** Return all registered webhooks for the partner account. */
    public XmlApiResponse<List<Webhook>> listWebhooks() {
        String json = http.get("/v1/webhooks");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Webhook>>>(){}.getType());
    }

    /**
     * Register a new webhook.
     *
     * @param url    The endpoint URL that will receive POST requests.
     * @param event  The event type to subscribe to (e.g. {@code "booking.completed"}).
     */
    public XmlApiResponse<Webhook> createWebhook(String url, String event) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url",   url);
        body.put("event", event);
        String json = http.post("/v1/webhooks", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Webhook>>(){}.getType());
    }

    /**
     * Update an existing webhook's URL or event subscription.
     *
     * @param webhookId  The webhook ID.
     * @param url        New endpoint URL.
     * @param event      New event type.
     */
    public XmlApiResponse<Webhook> updateWebhook(int webhookId, String url, String event) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url",   url);
        body.put("event", event);
        String json = http.put("/v1/webhooks/" + webhookId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Webhook>>(){}.getType());
    }

    /**
     * Delete a webhook registration.
     *
     * @param webhookId  The webhook ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse deleteWebhook(int webhookId) {
        String json = http.delete("/v1/webhooks/" + webhookId);
        return http.fromJson(json, XmlApiResponse.class);
    }
}
