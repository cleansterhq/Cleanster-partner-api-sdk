package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Webhook;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Webhooks API — register and manage webhook endpoints.
 *
 * <h3>Endpoints (4)</h3>
 * <ol>
 *   <li>GET    /webhooks       — list webhooks</li>
 *   <li>GET    /webhooks/{id}  — get webhook</li>
 *   <li>POST   /webhooks       — create webhook</li>
 *   <li>DELETE /webhooks/{id}  — delete webhook</li>
 * </ol>
 */
public class WebhooksXmlApi {

    private final XmlHttpClient http;

    public WebhooksXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<Webhook>> listWebhooks() {
        String json = http.get("/webhooks");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Webhook>>>(){}.getType());
    }

    public XmlApiResponse<Webhook> getWebhook(int webhookId) {
        String json = http.get("/webhooks/" + webhookId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Webhook>>(){}.getType());
    }

    public XmlApiResponse<Webhook> createWebhook(String url, List<String> events) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url",    url);
        body.put("events", events != null ? events : List.of());
        String json = http.post("/webhooks", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Webhook>>(){}.getType());
    }

    public XmlApiResponse<Webhook> deleteWebhook(int webhookId) {
        String json = http.delete("/webhooks/" + webhookId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Webhook>>(){}.getType());
    }
}
