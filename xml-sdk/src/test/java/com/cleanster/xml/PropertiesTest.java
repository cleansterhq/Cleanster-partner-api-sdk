package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.Property;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 26 tests covering all 14 Properties endpoints plus Property JAXB XML serialisation.
 */
class PropertiesTest {

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

    private void enqueueProperty(int id, String name) {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":" + id + ",\"name\":\"" + name + "\","
                        + "\"city\":\"New York\",\"active\":true}}")
                .addHeader("Content-Type", "application/json"));
    }

    private void enqueueList(int count) {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"message\":\"OK\",\"data\":[");
        for (int i = 0; i < count; i++) {
            sb.append("{\"id\":").append(i + 1).append(",\"name\":\"Property ").append(i + 1).append("\"}");
            if (i < count - 1) sb.append(",");
        }
        sb.append("]}");
        server.enqueue(new MockResponse().setBody(sb.toString()).addHeader("Content-Type", "application/json"));
    }

    // ─── listProperties ────────────────────────────────────────────────────────

    @Test
    void listProperties_returnsAll() throws Exception {
        enqueueList(3);
        XmlApiResponse<List<Property>> resp = client.properties().listProperties();
        assertTrue(resp.isSuccess());
        assertEquals(3, resp.getData().size());
    }

    @Test
    void listProperties_usesGET() throws Exception {
        enqueueList(1);
        client.properties().listProperties();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/properties"));
    }

    // ─── getProperty ───────────────────────────────────────────────────────────

    @Test
    void getProperty_returnsProperty() throws Exception {
        enqueueProperty(1, "Sunset Villa");
        XmlApiResponse<Property> resp = client.properties().getProperty(1);
        assertEquals("Sunset Villa", resp.getData().getName());
        assertEquals(1, resp.getData().getId());
    }

    @Test
    void getProperty_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.properties().getProperty(9999));
    }

    @Test
    void getProperty_correctPath() throws Exception {
        enqueueProperty(5, "Beach House");
        client.properties().getProperty(5);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/properties/5"));
    }

    // ─── createProperty ────────────────────────────────────────────────────────

    @Test
    void createProperty_usesPOST() throws Exception {
        enqueueProperty(10, "Office");
        client.properties().createProperty("Office", "123 Main St", "Dallas",
                "TX", "75201", "US", 3, 2);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
    }

    @Test
    void createProperty_bodyContainsName() throws Exception {
        enqueueProperty(10, "Office");
        client.properties().createProperty("Office", "123 Main St", "Dallas",
                "TX", "75201", "US", 3, 2);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("Office"));
    }

    @Test
    void createProperty_returnsCreated() throws Exception {
        enqueueProperty(10, "My Place");
        Property p = client.properties().createProperty(
                "My Place", "1 A St", "Chicago", "IL", "60601", "US", 2, 1).getData();
        assertEquals("My Place", p.getName());
    }

    // ─── updateProperty ────────────────────────────────────────────────────────

    @Test
    void updateProperty_usesPUT() throws Exception {
        enqueueProperty(3, "Updated");
        client.properties().updateProperty(3, java.util.Map.of("name", "Updated"));
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getPath().endsWith("/properties/3"));
    }

    // ─── deleteProperty ────────────────────────────────────────────────────────

    @Test
    void deleteProperty_usesDELETE() throws Exception {
        enqueueProperty(4, "Deleted");
        client.properties().deleteProperty(4);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertTrue(req.getPath().endsWith("/properties/4"));
    }

    // ─── getPropertyBookings ───────────────────────────────────────────────────

    @Test
    void getPropertyBookings_correctPath() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.properties().getPropertyBookings(1);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/properties/1/bookings"));
    }

    // ─── getPropertyChecklists ─────────────────────────────────────────────────

    @Test
    void getPropertyChecklists_correctPath() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.properties().getPropertyChecklists(2);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/properties/2/checklists"));
    }

    // ─── archiveProperty ───────────────────────────────────────────────────────

    @Test
    void archiveProperty_correctPath() throws Exception {
        enqueueProperty(5, "Archived");
        client.properties().archiveProperty(5);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/properties/5/archive"));
        assertEquals("POST", req.getMethod());
    }

    // ─── restoreProperty ───────────────────────────────────────────────────────

    @Test
    void restoreProperty_correctPath() throws Exception {
        enqueueProperty(5, "Restored");
        client.properties().restoreProperty(5);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/properties/5/restore"));
    }

    // ─── listActiveProperties ──────────────────────────────────────────────────

    @Test
    void listActiveProperties_correctPath() throws Exception {
        enqueueList(2);
        client.properties().listActiveProperties();
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/properties/active"));
    }

    // ─── listArchivedProperties ────────────────────────────────────────────────

    @Test
    void listArchivedProperties_correctPath() throws Exception {
        enqueueList(1);
        client.properties().listArchivedProperties();
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/properties/archived"));
    }

    // ─── duplicateProperty ─────────────────────────────────────────────────────

    @Test
    void duplicateProperty_correctPath() throws Exception {
        enqueueProperty(6, "Copy");
        client.properties().duplicateProperty(5);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/properties/5/duplicate"));
    }

    // ─── getAccessInfo ─────────────────────────────────────────────────────────

    @Test
    void getAccessInfo_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"instructions\":\"Ring bell\"}}")
                .addHeader("Content-Type", "application/json"));
        client.properties().getAccessInfo(3);
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().contains("/properties/3/access-info"));
    }

    // ─── updateAccessInfo ──────────────────────────────────────────────────────

    @Test
    void updateAccessInfo_usesPUT() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Updated\",\"data\":{}}")
                .addHeader("Content-Type", "application/json"));
        client.properties().updateAccessInfo(3, "Key under mat");
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getBody().readUtf8().contains("Key under mat"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void property_toXml_containsFields() {
        Property p = new Property();
        p.setId(1);
        p.setName("Sunset Villa");
        p.setCity("Miami");
        p.setRoomCount(3);
        String xml = XmlConverter.toXml(p);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<name>Sunset Villa</name>"));
        assertTrue(xml.contains("<city>Miami</city>"));
        assertTrue(xml.contains("<roomCount>3</roomCount>"));
    }

    @Test
    void property_fromXml_roundTrip() {
        Property original = new Property();
        original.setId(99);
        original.setName("Round Trip House");
        original.setBathroomCount(2);
        String   xml      = XmlConverter.toXml(original);
        Property restored = XmlConverter.fromXml(xml, Property.class);
        assertEquals(99,               restored.getId());
        assertEquals("Round Trip House", restored.getName());
        assertEquals(2,                restored.getBathroomCount());
    }

    @Test
    void createProperty_bodyContainsCity() throws Exception {
        enqueueProperty(10, "City Test");
        client.properties().createProperty("City Test", "1 Main", "Boston",
                "MA", "02101", "US", 2, 1);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("Boston"));
    }

    @Test
    void updateProperty_bodyContainsChange() throws Exception {
        enqueueProperty(3, "New Name");
        client.properties().updateProperty(3, java.util.Map.of("name", "New Name"));
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("New Name"));
    }

    @Test
    void deleteProperty_returnsSuccess() throws Exception {
        enqueueProperty(4, "Deleted");
        assertTrue(client.properties().deleteProperty(4).isSuccess());
    }

    @Test
    void listActiveProperties_returnsCount() throws Exception {
        enqueueList(4);
        assertEquals(4, client.properties().listActiveProperties().getData().size());
    }

    @Test
    void property_allAddressFields_roundTrip() {
        Property p = new Property();
        p.setAddress("100 Oak St");
        p.setCity("Austin");
        p.setState("TX");
        p.setZipCode("78701");
        p.setCountry("US");
        p.setSquareFootage(1200.0);
        String   xml = XmlConverter.toXml(p);
        Property r   = XmlConverter.fromXml(xml, Property.class);
        assertEquals("100 Oak St", r.getAddress());
        assertEquals("Austin",     r.getCity());
        assertEquals("TX",         r.getState());
        assertEquals("78701",      r.getZipCode());
        assertEquals(1200.0,       r.getSquareFootage(), 0.001);
    }
}
