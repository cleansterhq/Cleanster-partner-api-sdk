package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.Webhook;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 11 tests covering all 4 Webhooks endpoints plus Webhook JAXB XML serialisation.
 */
class WebhooksTest {

    private MockWebServer     server;
    private CleansterXmlClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        client = CleansterXmlClient.custom(server.url("/").toString(), "key-test", null);
    }

    @AfterEach
    void tearDown() throws Exception { server.shutdown(); }

    private void enqueueWebhook(int id, String url) {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":" + id + ",\"url\":\"" + url + "\",\"active\":true}}")
                .addHeader("Content-Type", "application/json"));
    }

    // ─── listWebhooks ──────────────────────────────────────────────────────────

    @Test
    void listWebhooks_returnsAll() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":["
                        + "{\"id\":1,\"url\":\"https://example.com/wh1\"},"
                        + "{\"id\":2,\"url\":\"https://example.com/wh2\"}]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<Webhook>> resp = client.webhooks().listWebhooks();
        assertEquals(2, resp.getData().size());
    }

    @Test
    void listWebhooks_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.webhooks().listWebhooks();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/webhooks"));
    }

    // ─── getWebhook ────────────────────────────────────────────────────────────

    @Test
    void getWebhook_returnsWebhook() throws Exception {
        enqueueWebhook(1, "https://hooks.example.com");
        Webhook w = client.webhooks().getWebhook(1).getData();
        assertEquals(1, w.getId());
        assertEquals("https://hooks.example.com", w.getUrl());
    }

    @Test
    void getWebhook_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.webhooks().getWebhook(9999));
    }

    // ─── createWebhook ─────────────────────────────────────────────────────────

    @Test
    void createWebhook_usesPOST() throws Exception {
        enqueueWebhook(3, "https://new.example.com");
        client.webhooks().createWebhook("https://new.example.com",
                List.of("booking.created", "booking.cancelled"));
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/webhooks"));
    }

    @Test
    void createWebhook_bodyContainsUrl() throws Exception {
        enqueueWebhook(3, "https://new.example.com");
        client.webhooks().createWebhook("https://new.example.com", List.of("booking.created"));
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("new.example.com"));
    }

    @Test
    void createWebhook_returnsCreated() throws Exception {
        enqueueWebhook(5, "https://cb.example.com");
        Webhook w = client.webhooks()
                .createWebhook("https://cb.example.com", List.of("booking.created")).getData();
        assertEquals(5, w.getId());
    }

    // ─── deleteWebhook ─────────────────────────────────────────────────────────

    @Test
    void deleteWebhook_usesDELETE() throws Exception {
        enqueueWebhook(2, "https://old.example.com");
        client.webhooks().deleteWebhook(2);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertTrue(req.getPath().endsWith("/webhooks/2"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void webhook_toXml_containsFields() {
        Webhook w = new Webhook();
        w.setId(1);
        w.setUrl("https://example.com/hook");
        w.setActive(true);
        String xml = XmlConverter.toXml(w);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("https://example.com/hook"));
        assertTrue(xml.contains("<active>true</active>"));
    }

    @Test
    void webhook_fromXml_roundTrip() {
        Webhook original = new Webhook();
        original.setId(9);
        original.setUrl("https://rt.example.com");
        original.setActive(false);
        original.setEvents(List.of("booking.created", "booking.completed"));
        String  xml      = XmlConverter.toXml(original);
        Webhook restored = XmlConverter.fromXml(xml, Webhook.class);
        assertEquals(9,                        restored.getId());
        assertEquals("https://rt.example.com", restored.getUrl());
        assertFalse(restored.getActive());
        assertNotNull(restored.getEvents());
        assertEquals(2, restored.getEvents().size());
    }

    @Test
    void deleteWebhook_successResponse() throws Exception {
        enqueueWebhook(2, "https://old.example.com");
        assertTrue(client.webhooks().deleteWebhook(2).isSuccess());
    }
}
