package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        lenient().when(transport.getObjectMapper()).thenReturn(MAPPER);
        lenient().when(transport.extractData(any())).thenAnswer(inv -> inv.getArgument(0));
        client = new CleansterSOAPClient(transport);
    }

    // ListWebhooks
    @Test
    @DisplayName("listWebhooks calls GET /v1/webhooks")
    void listWebhooksCallsCorrectPath() {
        when(transport.get("/v1/webhooks")).thenReturn(MAPPER.createArrayNode());
        client.listWebhooks();
        verify(transport).get("/v1/webhooks");
    }

    @Test
    @DisplayName("listWebhooks returns empty list when none registered")
    void listWebhooksReturnsEmptyList() {
        when(transport.get("/v1/webhooks")).thenReturn(MAPPER.createArrayNode());
        List<Webhook> result = client.listWebhooks();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // CreateWebhook
    @Test
    @DisplayName("createWebhook calls POST /v1/webhooks")
    void createWebhookCallsCorrectPath() {
        when(transport.post(eq("/v1/webhooks"), any())).thenReturn(webhookNode(5L));
        client.createWebhook("https://example.com/hook", "booking.completed");
        verify(transport).post(eq("/v1/webhooks"), any());
    }

    @Test
    @DisplayName("createWebhook returns new Webhook")
    void createWebhookReturnsWebhook() {
        when(transport.post(eq("/v1/webhooks"), any())).thenReturn(webhookNode(5L));
        Webhook result = client.createWebhook("https://example.com/hook", "booking.completed");
        assertEquals(5L, result.getId());
    }

    // UpdateWebhook
    @Test
    @DisplayName("updateWebhook calls PUT /v1/webhooks/{id}")
    void updateWebhookCallsCorrectPath() {
        when(transport.put(eq("/v1/webhooks/5"), any())).thenReturn(webhookNode(5L));
        client.updateWebhook(5L, "https://example.com/hook2", "booking.cancelled");
        verify(transport).put(eq("/v1/webhooks/5"), any());
    }

    @Test
    @DisplayName("updateWebhook returns updated Webhook")
    void updateWebhookReturnsWebhook() {
        when(transport.put(eq("/v1/webhooks/5"), any())).thenReturn(webhookNode(5L));
        Webhook result = client.updateWebhook(5L, null, "booking.cancelled");
        assertEquals(5L, result.getId());
    }

    // DeleteWebhook
    @Test
    @DisplayName("deleteWebhook calls DELETE /v1/webhooks/{id}")
    void deleteWebhookCallsCorrectPath() {
        when(transport.delete("/v1/webhooks/5")).thenReturn(okNode());
        client.deleteWebhook(5L);
        verify(transport).delete("/v1/webhooks/5");
    }

    @Test
    @DisplayName("deleteWebhook returns success response")
    void deleteWebhookReturnsSuccess() {
        when(transport.delete("/v1/webhooks/5")).thenReturn(okNode());
        ApiResponse resp = client.deleteWebhook(5L);
        assertTrue(resp.isSuccess());
    }

    private JsonNode webhookNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("url", "https://example.com/hook");
        n.put("event", "booking.completed");
        return n;
    }

    private JsonNode okNode() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", 200);
        n.put("message", "OK");
        return n;
    }
}
