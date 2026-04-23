package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.BlacklistEntry;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 9 tests covering all 3 Blacklist endpoints plus BlacklistEntry JAXB XML serialisation.
 */
class BlacklistTest {

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

    // ─── listBlacklist ─────────────────────────────────────────────────────────

    @Test
    void listBlacklist_returnsEntries() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":["
                        + "{\"id\":1,\"userId\":101,\"reason\":\"No show\"}]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<BlacklistEntry>> resp = client.blacklist().listBlacklist();
        assertEquals(1, resp.getData().size());
        assertEquals(101, resp.getData().get(0).getUserId());
    }

    @Test
    void listBlacklist_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.blacklist().listBlacklist();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/blacklist"));
    }

    // ─── addToBlacklist ────────────────────────────────────────────────────────

    @Test
    void addToBlacklist_usesPOST() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Added\","
                        + "\"data\":{\"id\":5,\"userId\":202,\"reason\":\"Rude\"}}")
                .addHeader("Content-Type", "application/json"));
        client.blacklist().addToBlacklist(202, "Rude");
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/blacklist"));
    }

    @Test
    void addToBlacklist_bodyContainsUserIdAndReason() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Added\","
                        + "\"data\":{\"id\":5,\"userId\":303,\"reason\":\"Damage\"}}")
                .addHeader("Content-Type", "application/json"));
        client.blacklist().addToBlacklist(303, "Damage");
        RecordedRequest req = server.takeRequest();
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("303"));
        assertTrue(body.contains("Damage"));
    }

    @Test
    void addToBlacklist_returnsEntry() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Added\","
                        + "\"data\":{\"id\":6,\"userId\":404,\"reason\":\"Theft\"}}")
                .addHeader("Content-Type", "application/json"));
        BlacklistEntry entry = client.blacklist().addToBlacklist(404, "Theft").getData();
        assertEquals(404,    entry.getUserId());
        assertEquals("Theft", entry.getReason());
    }

    // ─── removeFromBlacklist ───────────────────────────────────────────────────

    @Test
    void removeFromBlacklist_usesDELETE() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Removed\",\"data\":{}}")
                .addHeader("Content-Type", "application/json"));
        client.blacklist().removeFromBlacklist(101);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertTrue(req.getPath().endsWith("/blacklist/101"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void blacklistEntry_toXml_containsFields() {
        BlacklistEntry e = new BlacklistEntry();
        e.setId(1);
        e.setUserId(55);
        e.setReason("Misconduct");
        String xml = XmlConverter.toXml(e);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<userId>55</userId>"));
        assertTrue(xml.contains("<reason>Misconduct</reason>"));
    }

    @Test
    void blacklistEntry_fromXml_roundTrip() {
        BlacklistEntry original = new BlacklistEntry();
        original.setId(3);
        original.setUserId(77);
        original.setReason("Property damage");
        String         xml      = XmlConverter.toXml(original);
        BlacklistEntry restored = XmlConverter.fromXml(xml, BlacklistEntry.class);
        assertEquals(3,                  restored.getId());
        assertEquals(77,                 restored.getUserId());
        assertEquals("Property damage",  restored.getReason());
    }

    @Test
    void blacklistEntry_toXml_isValidXml() {
        BlacklistEntry e = new BlacklistEntry();
        e.setId(1);
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(e)));
    }
}
