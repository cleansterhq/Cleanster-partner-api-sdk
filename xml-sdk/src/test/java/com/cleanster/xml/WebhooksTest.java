package com.cleanster.xml;

import com.cleanster.xml.api.WebhooksXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Webhook;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebhooksTest {

    private XmlHttpClient   http;
    private WebhooksXmlApi  api;

    private static final String LIST_JSON  = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":1,\"url\":\"https://example.com/hook\",\"events\":[\"booking.completed\"]}]}";
    private static final String HOOK_JSON  = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":1,\"url\":\"https://example.com/hook\",\"events\":[\"booking.completed\"]}}";
    private static final String OK_JSON    = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new WebhooksXmlApi(http);
    }

    // ── listWebhooks ───────────────────────────────────────────────────────────

    @Test void listWebhooks_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/webhooks");
        api.listWebhooks();
        verify(http).get("/v1/webhooks");
    }

    @Test void listWebhooks_returnsParsedList() {
        doReturn(LIST_JSON).when(http).get("/v1/webhooks");
        XmlApiResponse<List<Webhook>> resp = api.listWebhooks();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
    }

    @Test void listWebhooks_emptyList() {
        doReturn("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .when(http).get("/v1/webhooks");
        assertTrue(api.listWebhooks().getData().isEmpty());
    }

    // ── createWebhook ──────────────────────────────────────────────────────────

    @Test void createWebhook_callsPost() {
        doReturn(HOOK_JSON).when(http).post(eq("/v1/webhooks"), any());
        api.createWebhook("https://example.com/hook", "booking.completed");
        verify(http).post(eq("/v1/webhooks"), any());
    }

    @Test void createWebhook_returnsParsedWebhook() {
        doReturn(HOOK_JSON).when(http).post(eq("/v1/webhooks"), any());
        XmlApiResponse<Webhook> resp = api.createWebhook("https://example.com/hook", "booking.completed");
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().getId());
    }

    // ── updateWebhook ──────────────────────────────────────────────────────────

    @Test void updateWebhook_callsPut_withId() {
        doReturn(HOOK_JSON).when(http).put(eq("/v1/webhooks/1"), any());
        api.updateWebhook(1, "https://new.example.com/hook", "booking.completed");
        verify(http).put(eq("/v1/webhooks/1"), any());
    }

    @Test void updateWebhook_differentId_usesCorrectPath() {
        doReturn(HOOK_JSON).when(http).put(eq("/v1/webhooks/99"), any());
        api.updateWebhook(99, "https://example.com/hook", "booking.cancelled");
        verify(http).put(eq("/v1/webhooks/99"), any());
    }

    // ── deleteWebhook ──────────────────────────────────────────────────────────

    @Test void deleteWebhook_callsDelete_withId() {
        doReturn(OK_JSON).when(http).delete("/v1/webhooks/1");
        api.deleteWebhook(1);
        verify(http).delete("/v1/webhooks/1");
    }

    @Test void deleteWebhook_returnsSuccess() {
        doReturn(OK_JSON).when(http).delete("/v1/webhooks/1");
        assertTrue(api.deleteWebhook(1).isSuccess());
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void webhook_toXml_isValidXml() {
        Webhook w = new Webhook(); w.setId(1); w.setEvents(List.of("booking.completed")); w.setUrl("https://x.com");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(w)));
    }

    @Test void webhook_fromXml_roundTrip() {
        Webhook o = new Webhook(); o.setId(5); o.setEvents(List.of("booking.cancelled")); o.setUrl("https://t.com");
        Webhook r = XmlConverter.fromXml(XmlConverter.toXml(o), Webhook.class);
        assertEquals(5, r.getId());
        assertNotNull(r.getEvents());
        assertTrue(r.getEvents().contains("booking.cancelled"));
    }
}
