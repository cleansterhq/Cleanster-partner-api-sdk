package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.PaymentMethod;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 14 tests covering all 6 PaymentMethods endpoints plus PaymentMethod JAXB XML serialisation.
 */
class PaymentMethodsTest {

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

    private void enqueueMethod(int id, String brand, String last4) {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":" + id + ",\"brand\":\"" + brand
                        + "\",\"last4\":\"" + last4 + "\",\"isDefault\":false}}")
                .addHeader("Content-Type", "application/json"));
    }

    // ─── listPaymentMethods ────────────────────────────────────────────────────

    @Test
    void listPaymentMethods_returnsAll() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":["
                        + "{\"id\":1,\"brand\":\"Visa\",\"last4\":\"4242\"},"
                        + "{\"id\":2,\"brand\":\"Mastercard\",\"last4\":\"5555\"}]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<PaymentMethod>> resp = client.paymentMethods().listPaymentMethods();
        assertEquals(2, resp.getData().size());
    }

    @Test
    void listPaymentMethods_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        client.paymentMethods().listPaymentMethods();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/payment-methods"));
    }

    // ─── getPaymentMethod ──────────────────────────────────────────────────────

    @Test
    void getPaymentMethod_returnsMethod() throws Exception {
        enqueueMethod(1, "Visa", "4242");
        PaymentMethod pm = client.paymentMethods().getPaymentMethod(1).getData();
        assertEquals("Visa",  pm.getBrand());
        assertEquals("4242",  pm.getLast4());
        assertEquals(1,       pm.getId());
    }

    @Test
    void getPaymentMethod_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.paymentMethods().getPaymentMethod(9999));
    }

    // ─── addPaymentMethod ──────────────────────────────────────────────────────

    @Test
    void addPaymentMethod_usesPOST() throws Exception {
        enqueueMethod(3, "Amex", "0005");
        client.paymentMethods().addPaymentMethod("tok_stripe_123", "John Doe");
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/payment-methods"));
    }

    @Test
    void addPaymentMethod_bodyContainsToken() throws Exception {
        enqueueMethod(3, "Amex", "0005");
        client.paymentMethods().addPaymentMethod("tok_stripe_abc", "Jane Doe");
        RecordedRequest req = server.takeRequest();
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("tok_stripe_abc"));
        assertTrue(body.contains("Jane Doe"));
    }

    // ─── updatePaymentMethod ───────────────────────────────────────────────────

    @Test
    void updatePaymentMethod_usesPUT() throws Exception {
        enqueueMethod(2, "Visa", "1111");
        client.paymentMethods().updatePaymentMethod(2, java.util.Map.of("holderName", "New Name"));
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getPath().endsWith("/payment-methods/2"));
    }

    // ─── deletePaymentMethod ───────────────────────────────────────────────────

    @Test
    void deletePaymentMethod_usesDELETE() throws Exception {
        enqueueMethod(2, "Visa", "1111");
        client.paymentMethods().deletePaymentMethod(2);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertTrue(req.getPath().endsWith("/payment-methods/2"));
    }

    // ─── setDefaultPaymentMethod ───────────────────────────────────────────────

    @Test
    void setDefault_correctPath() throws Exception {
        enqueueMethod(1, "Visa", "4242");
        client.paymentMethods().setDefaultPaymentMethod(1);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/payment-methods/1/default"));
        assertEquals("POST", req.getMethod());
    }

    @Test
    void setDefault_returnsMethod() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":1,\"brand\":\"Visa\",\"last4\":\"4242\",\"isDefault\":true}}")
                .addHeader("Content-Type", "application/json"));
        PaymentMethod pm = client.paymentMethods().setDefaultPaymentMethod(1).getData();
        assertTrue(pm.getIsDefault());
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void paymentMethod_toXml_containsFields() {
        PaymentMethod pm = new PaymentMethod();
        pm.setId(1);
        pm.setBrand("Visa");
        pm.setLast4("4242");
        pm.setIsDefault(true);
        String xml = XmlConverter.toXml(pm);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<brand>Visa</brand>"));
        assertTrue(xml.contains("<last4>4242</last4>"));
    }

    @Test
    void paymentMethod_fromXml_roundTrip() {
        PaymentMethod original = new PaymentMethod();
        original.setId(5);
        original.setBrand("Mastercard");
        original.setLast4("5555");
        original.setExpMonth(12);
        original.setExpYear(2027);
        String        xml      = XmlConverter.toXml(original);
        PaymentMethod restored = XmlConverter.fromXml(xml, PaymentMethod.class);
        assertEquals(5,            restored.getId());
        assertEquals("Mastercard", restored.getBrand());
        assertEquals("5555",       restored.getLast4());
        assertEquals(12,           restored.getExpMonth());
        assertEquals(2027,         restored.getExpYear());
    }

    @Test
    void addPaymentMethod_returnsCreated() throws Exception {
        enqueueMethod(4, "Discover", "6011");
        PaymentMethod pm = client.paymentMethods()
                .addPaymentMethod("tok_discover_001", "Test User").getData();
        assertEquals("Discover", pm.getBrand());
        assertEquals("6011",     pm.getLast4());
    }

    @Test
    void listPaymentMethods_emptyList() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        assertTrue(client.paymentMethods().listPaymentMethods().getData().isEmpty());
    }
}
