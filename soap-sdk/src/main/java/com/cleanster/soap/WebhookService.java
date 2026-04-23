package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all webhook-related SOAP operations. */
public class WebhookService {

    private final SOAPTransport transport;

    public WebhookService(SOAPTransport transport) {
        this.transport = transport;
    }

    public List<Webhook> listWebhooks() {
        JsonNode root = transport.get("/v1/webhooks");
        JsonNode data = transport.extractData(root);
        List<Webhook> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Webhook.class));
            }
        }
        return list;
    }

    public Webhook createWebhook(String url, String event) {
        Map<String, Object> body = new HashMap<>();
        body.put("url", url);
        body.put("event", event);
        JsonNode root = transport.post("/v1/webhooks", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Webhook.class);
    }

    public Webhook updateWebhook(long webhookId, String url, String event) {
        Map<String, Object> body = new HashMap<>();
        if (url != null)   body.put("url", url);
        if (event != null) body.put("event", event);
        JsonNode root = transport.put("/v1/webhooks/" + webhookId, body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Webhook.class);
    }

    public ApiResponse deleteWebhook(long webhookId) {
        JsonNode root = transport.delete("/v1/webhooks/" + webhookId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }
}
