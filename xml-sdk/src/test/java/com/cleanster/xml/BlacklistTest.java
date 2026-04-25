package com.cleanster.xml;

import com.cleanster.xml.api.BlacklistXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.BlacklistEntry;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BlacklistTest {

    private XmlHttpClient   http;
    private BlacklistXmlApi api;

    private static final String LIST_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":1,\"userId\":5,\"reason\":\"No show\"}]}";
    private static final String ENTRY_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":1,\"userId\":5,\"reason\":\"No show\"}}";
    private static final String OK_JSON = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new BlacklistXmlApi(http);
    }

    // ── listBlacklist ──────────────────────────────────────────────────────────

    @Test void listBlacklist_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/blacklist/cleaner");
        api.listBlacklist();
        verify(http).get("/v1/blacklist/cleaner");
    }

    @Test void listBlacklist_returnsParsedList() {
        doReturn(LIST_JSON).when(http).get("/v1/blacklist/cleaner");
        XmlApiResponse<List<BlacklistEntry>> resp = api.listBlacklist();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
        assertEquals(5, (int) resp.getData().get(0).getUserId());
    }

    @Test void listBlacklist_emptyList_returnsEmpty() {
        doReturn("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .when(http).get("/v1/blacklist/cleaner");
        assertTrue(api.listBlacklist().getData().isEmpty());
    }

    // ── addToBlacklist ─────────────────────────────────────────────────────────

    @Test void addToBlacklist_callsPost() {
        doReturn(ENTRY_JSON).when(http).post(eq("/v1/blacklist/cleaner"), any());
        api.addToBlacklist(5, "No show");
        verify(http).post(eq("/v1/blacklist/cleaner"), any());
    }

    @Test void addToBlacklist_returnsParsedEntry() {
        doReturn(ENTRY_JSON).when(http).post(eq("/v1/blacklist/cleaner"), any());
        XmlApiResponse<BlacklistEntry> resp = api.addToBlacklist(5, "No show");
        assertTrue(resp.isSuccess());
        assertEquals(5, (int) resp.getData().getUserId());
    }

    @Test void addToBlacklist_nullReason_stillCallsPost() {
        doReturn(ENTRY_JSON).when(http).post(eq("/v1/blacklist/cleaner"), any());
        api.addToBlacklist(5, null);
        verify(http).post(eq("/v1/blacklist/cleaner"), any());
    }

    // ── removeFromBlacklist ────────────────────────────────────────────────────

    @Test void removeFromBlacklist_callsDelete() {
        doReturn(OK_JSON).when(http).delete("/v1/blacklist/cleaner");
        api.removeFromBlacklist(5);
        verify(http).delete("/v1/blacklist/cleaner");
    }

    @Test void removeFromBlacklist_returnsSuccess() {
        doReturn(OK_JSON).when(http).delete("/v1/blacklist/cleaner");
        assertTrue(api.removeFromBlacklist(5).isSuccess());
    }

    // ── JAXB ──────────────────────────────────────────────────────────────────

    @Test void blacklistEntry_toXml_containsUserId() {
        BlacklistEntry e = new BlacklistEntry(); e.setUserId(5);
        assertTrue(XmlConverter.toXml(e).contains("5"));
    }

    @Test void blacklistEntry_toXml_isValidXml() {
        BlacklistEntry e = new BlacklistEntry(); e.setUserId(1);
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(e)));
    }

    @Test void blacklistEntry_fromXml_roundTrip() {
        BlacklistEntry o = new BlacklistEntry(); o.setUserId(7); o.setReason("Bad");
        BlacklistEntry r = XmlConverter.fromXml(XmlConverter.toXml(o), BlacklistEntry.class);
        assertEquals(7, (int) r.getUserId());
        assertEquals("Bad", r.getReason());
    }
}
