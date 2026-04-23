package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.Coupon;
import com.cleanster.xml.model.Plan;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 21 tests covering all 7 Other endpoints plus Plan/Coupon JAXB XML serialisation.
 */
class OtherTest {

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

    // ─── listPlans ─────────────────────────────────────────────────────────────

    @Test
    void listPlans_returnsPlans() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":["
                        + "{\"id\":1,\"name\":\"Standard\",\"basePrice\":80.0},"
                        + "{\"id\":2,\"name\":\"Deep Clean\",\"basePrice\":150.0}]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<Plan>> resp = client.other().listPlans();
        assertEquals(2, resp.getData().size());
        assertEquals("Standard", resp.getData().get(0).getName());
    }

    @Test
    void listPlans_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.other().listPlans();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/plans"));
    }

    @Test
    void listPlans_empty() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        assertTrue(client.other().listPlans().getData().isEmpty());
    }

    // ─── getPlan ───────────────────────────────────────────────────────────────

    @Test
    void getPlan_returnsPlan() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":2,\"name\":\"Deep Clean\",\"basePrice\":150.0}}")
                .addHeader("Content-Type", "application/json"));
        Plan p = client.other().getPlan(2).getData();
        assertEquals(2,          p.getId());
        assertEquals("Deep Clean", p.getName());
        assertEquals(150.0,      p.getBasePrice(), 0.001);
    }

    @Test
    void getPlan_correctPath() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":3}}")
                .addHeader("Content-Type", "application/json"));
        client.other().getPlan(3);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/plans/3"));
    }

    // ─── validateCoupon ────────────────────────────────────────────────────────

    @Test
    void validateCoupon_validCode() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Valid\","
                        + "\"data\":{\"id\":1,\"code\":\"100POFF\",\"type\":\"percent\",\"value\":100.0,\"active\":true}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<Coupon> resp = client.other().validateCoupon("100POFF");
        assertTrue(resp.isSuccess());
        assertEquals("100POFF", resp.getData().getCode());
        assertEquals(100.0,     resp.getData().getValue(), 0.001);
    }

    @Test
    void validateCoupon_usesPOST() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Valid\","
                        + "\"data\":{\"id\":2,\"code\":\"50POFF\",\"type\":\"percent\",\"value\":50.0}}")
                .addHeader("Content-Type", "application/json"));
        client.other().validateCoupon("50POFF");
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/coupons/validate"));
    }

    @Test
    void validateCoupon_bodyContainsCode() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Valid\",\"data\":{\"code\":\"20POFF\"}}")
                .addHeader("Content-Type", "application/json"));
        client.other().validateCoupon("20POFF");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("20POFF"));
    }

    @Test
    void validateCoupon_invalidCode_returnsFailure() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":false,\"message\":\"Invalid coupon\",\"data\":null}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<Coupon> resp = client.other().validateCoupon("EXPIRED");
        assertFalse(resp.isSuccess());
        assertEquals("Invalid coupon", resp.getMessage());
    }

    // ─── listExtras ────────────────────────────────────────────────────────────

    @Test
    void listExtras_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.other().listExtras();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/extras"));
    }

    // ─── getChatRules ──────────────────────────────────────────────────────────

    @Test
    void getChatRules_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"maxMessages\":50}}")
                .addHeader("Content-Type", "application/json"));
        client.other().getChatRules();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/chat/rules"));
    }

    // ─── getTimeslots ──────────────────────────────────────────────────────────

    @Test
    void getTimeslots_includesDateParam() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.other().getTimeslots("2025-09-15", null);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("date=2025-09-15"));
    }

    @Test
    void getTimeslots_includesPropertyIdParam() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.other().getTimeslots("2025-09-15", 42);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("propertyId=42"));
    }

    // ─── getConfig ─────────────────────────────────────────────────────────────

    @Test
    void getConfig_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{}}")
                .addHeader("Content-Type", "application/json"));
        client.other().getConfig();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/config"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void plan_toXml_containsFields() {
        Plan p = new Plan();
        p.setId(1);
        p.setName("Standard");
        p.setBasePrice(80.0);
        p.setCurrency("USD");
        String xml = XmlConverter.toXml(p);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<name>Standard</name>"));
        assertTrue(xml.contains("<basePrice>80.0</basePrice>"));
    }

    @Test
    void plan_fromXml_roundTrip() {
        Plan original = new Plan();
        original.setId(2);
        original.setName("Deep Clean");
        original.setHourlyRate(25.0);
        original.setActive(true);
        String restored_xml = XmlConverter.toXml(original);
        Plan   restored     = XmlConverter.fromXml(restored_xml, Plan.class);
        assertEquals(2,           restored.getId());
        assertEquals("Deep Clean", restored.getName());
        assertEquals(25.0,        restored.getHourlyRate(), 0.001);
        assertTrue(restored.getActive());
    }

    @Test
    void coupon_toXml_containsCode() {
        Coupon c = new Coupon();
        c.setCode("100POFF");
        c.setType("percent");
        c.setValue(100.0);
        String xml = XmlConverter.toXml(c);
        assertTrue(xml.contains("<code>100POFF</code>"));
        assertTrue(xml.contains("<type>percent</type>"));
    }

    @Test
    void coupon_fromXml_roundTrip() {
        Coupon original = new Coupon();
        original.setCode("200OFF");
        original.setType("fixed");
        original.setValue(200.0);
        original.setActive(true);
        String restored_xml = XmlConverter.toXml(original);
        Coupon restored     = XmlConverter.fromXml(restored_xml, Coupon.class);
        assertEquals("200OFF",  restored.getCode());
        assertEquals("fixed",   restored.getType());
        assertEquals(200.0,     restored.getValue(), 0.001);
        assertTrue(restored.getActive());
    }

    @Test
    void getPlan_returnsSuccess() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":1,\"name\":\"Basic\"}}")
                .addHeader("Content-Type", "application/json"));
        assertTrue(client.other().getPlan(1).isSuccess());
    }

    @Test
    void validateCoupon_50POFF_parsedCorrectly() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Valid\","
                        + "\"data\":{\"code\":\"50POFF\",\"type\":\"percent\",\"value\":50.0}}")
                .addHeader("Content-Type", "application/json"));
        Coupon c = client.other().validateCoupon("50POFF").getData();
        assertEquals("50POFF", c.getCode());
        assertEquals(50.0,     c.getValue(), 0.001);
    }

    @Test
    void getConfig_returnsSuccess() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{}}")
                .addHeader("Content-Type", "application/json"));
        assertTrue(client.other().getConfig().isSuccess());
    }
}
