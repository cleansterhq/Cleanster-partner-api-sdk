package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.Checklist;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 18 tests covering all 5 Checklists endpoints plus Checklist JAXB XML serialisation.
 */
class ChecklistsTest {

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

    private void enqueueChecklist(int id, String name) {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":" + id + ",\"name\":\"" + name + "\",\"active\":true}}")
                .addHeader("Content-Type", "application/json"));
    }

    // ─── listChecklists ────────────────────────────────────────────────────────

    @Test
    void listChecklists_returnsAll() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":["
                        + "{\"id\":1,\"name\":\"Standard\"},{\"id\":2,\"name\":\"Deep Clean\"}]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<Checklist>> resp = client.checklists().listChecklists();
        assertTrue(resp.isSuccess());
        assertEquals(2, resp.getData().size());
    }

    @Test
    void listChecklists_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.checklists().listChecklists();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/checklists"));
    }

    @Test
    void listChecklists_empty() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        assertTrue(client.checklists().listChecklists().getData().isEmpty());
    }

    // ─── getChecklist ──────────────────────────────────────────────────────────

    @Test
    void getChecklist_returnsChecklist() throws Exception {
        enqueueChecklist(1, "Standard Clean");
        XmlApiResponse<Checklist> resp = client.checklists().getChecklist(1);
        assertEquals("Standard Clean", resp.getData().getName());
        assertEquals(1, resp.getData().getId());
    }

    @Test
    void getChecklist_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.checklists().getChecklist(9999));
    }

    @Test
    void getChecklist_correctPath() throws Exception {
        enqueueChecklist(3, "Move Out");
        client.checklists().getChecklist(3);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/checklists/3"));
        assertEquals("GET", req.getMethod());
    }

    // ─── createChecklist ───────────────────────────────────────────────────────

    @Test
    void createChecklist_usesPOST() throws Exception {
        enqueueChecklist(5, "New");
        client.checklists().createChecklist("New", "Desc", List.of("Vacuum", "Mop"), null);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/checklists"));
    }

    @Test
    void createChecklist_bodyContainsName() throws Exception {
        enqueueChecklist(5, "New");
        client.checklists().createChecklist("New", "A checklist", List.of("Task 1"), null);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("New"));
    }

    @Test
    void createChecklist_returnsCreated() throws Exception {
        enqueueChecklist(5, "New Checklist");
        Checklist c = client.checklists()
                .createChecklist("New Checklist", "Desc", List.of(), null).getData();
        assertEquals("New Checklist", c.getName());
    }

    // ─── updateChecklist ───────────────────────────────────────────────────────

    @Test
    void updateChecklist_usesPUT() throws Exception {
        enqueueChecklist(2, "Updated");
        client.checklists().updateChecklist(2, java.util.Map.of("name", "Updated"));
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getPath().endsWith("/checklists/2"));
    }

    @Test
    void updateChecklist_bodyContainsUpdate() throws Exception {
        enqueueChecklist(2, "Updated Name");
        client.checklists().updateChecklist(2, java.util.Map.of("name", "Updated Name"));
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("Updated Name"));
    }

    // ─── deleteChecklist ───────────────────────────────────────────────────────

    @Test
    void deleteChecklist_usesDELETE() throws Exception {
        enqueueChecklist(3, "Deleted");
        client.checklists().deleteChecklist(3);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertTrue(req.getPath().endsWith("/checklists/3"));
    }

    @Test
    void deleteChecklist_successResponse() throws Exception {
        enqueueChecklist(3, "Deleted");
        assertTrue(client.checklists().deleteChecklist(3).isSuccess());
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void checklist_toXml_containsFields() {
        Checklist c = new Checklist();
        c.setId(1);
        c.setName("Standard");
        c.setActive(true);
        String xml = XmlConverter.toXml(c);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<name>Standard</name>"));
    }

    @Test
    void checklist_fromXml_roundTrip() {
        Checklist original = new Checklist();
        original.setId(7);
        original.setName("Deep Clean");
        original.setDescription("Full deep clean protocol");
        String    xml      = XmlConverter.toXml(original);
        Checklist restored = XmlConverter.fromXml(xml, Checklist.class);
        assertEquals(7,                         restored.getId());
        assertEquals("Deep Clean",              restored.getName());
        assertEquals("Full deep clean protocol", restored.getDescription());
    }

    @Test
    void checklist_withItems_roundTrip() {
        Checklist c = new Checklist();
        c.setId(8);
        c.setName("Custom");
        c.setItems(List.of("Vacuum floors", "Wipe counters", "Clean bathrooms"));
        String    xml = XmlConverter.toXml(c);
        Checklist r   = XmlConverter.fromXml(xml, Checklist.class);
        assertNotNull(r.getItems());
        assertEquals(3, r.getItems().size());
        assertTrue(r.getItems().contains("Vacuum floors"));
    }

    @Test
    void createChecklist_withPropertyId_sendsId() throws Exception {
        enqueueChecklist(5, "Prop Checklist");
        client.checklists().createChecklist("Prop Checklist", "Desc", List.of(), 42);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("42"));
    }

    @Test
    void updateChecklist_returnsUpdated() throws Exception {
        enqueueChecklist(2, "Renamed");
        Checklist c = client.checklists()
                .updateChecklist(2, java.util.Map.of("name", "Renamed")).getData();
        assertEquals("Renamed", c.getName());
    }
}
