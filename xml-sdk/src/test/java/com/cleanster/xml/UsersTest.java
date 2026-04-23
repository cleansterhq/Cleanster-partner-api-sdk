package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.User;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 18 tests covering Users endpoints and User JAXB XML serialisation.
 */
class UsersTest {

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

    // ─── fetchAccessToken ──────────────────────────────────────────────────────

    @Test
    void fetchAccessToken_returnsToken() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":7,\"token\":\"tok-abc\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> resp = client.users().fetchAccessToken(7);
        assertTrue(resp.isSuccess());
        assertEquals("tok-abc", resp.getData().getToken());
    }

    @Test
    void fetchAccessToken_setsUserId() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":42,\"token\":\"t42\"}}")
                .addHeader("Content-Type", "application/json"));
        client.users().fetchAccessToken(42);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/users/42/token"));
        assertEquals("POST", req.getMethod());
    }

    @Test
    void fetchAccessToken_headersPresent() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":1,\"token\":\"t1\"}}")
                .addHeader("Content-Type", "application/json"));
        client.users().fetchAccessToken(1);
        RecordedRequest req = server.takeRequest();
        assertEquals("key-test", req.getHeader("access-key"));
    }

    @Test
    void fetchAccessToken_httpError_throws() {
        server.enqueue(new MockResponse().setResponseCode(401).setBody("{\"error\":\"Unauthorized\"}"));
        assertThrows(CleansterXmlException.class, () -> client.users().fetchAccessToken(1));
    }

    @Test
    void fetchAccessToken_successFalse_parsedCorrectly() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":false,\"message\":\"User not found\",\"data\":null}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> resp = client.users().fetchAccessToken(999);
        assertFalse(resp.isSuccess());
        assertEquals("User not found", resp.getMessage());
    }

    // ─── getUserProfile ─────────────────────────────────────────────────────────

    @Test
    void getUserProfile_returnsUser() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":5,\"email\":\"a@b.com\",\"firstName\":\"Alice\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> resp = client.users().getUserProfile(5);
        assertTrue(resp.isSuccess());
        assertEquals("Alice", resp.getData().getFirstName());
        assertEquals("a@b.com", resp.getData().getEmail());
    }

    @Test
    void getUserProfile_correctPath() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":5}}")
                .addHeader("Content-Type", "application/json"));
        client.users().getUserProfile(5);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/users/5"));
        assertEquals("GET", req.getMethod());
    }

    @Test
    void getUserProfile_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.users().getUserProfile(9999));
    }

    @Test
    void getUserProfile_setsToken_inSubsequentCall() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":1,\"token\":\"my-tok\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> tokenResp = client.users().fetchAccessToken(1);
        client.setToken(tokenResp.getData().getToken());
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":1}}")
                .addHeader("Content-Type", "application/json"));
        client.users().getUserProfile(1);
        RecordedRequest req = server.takeRequest(); // first call (token)
        server.takeRequest();                       // second call (profile)
        assertEquals("my-tok", client.getToken());
    }

    // ─── updateUserProfile ──────────────────────────────────────────────────────

    @Test
    void updateUserProfile_sendsBody() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Updated\",\"data\":{\"id\":3,\"firstName\":\"Bob\"}}")
                .addHeader("Content-Type", "application/json"));
        client.users().updateUserProfile(3, "Bob", null, null);
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getBody().readUtf8().contains("Bob"));
    }

    @Test
    void updateUserProfile_returnsUpdatedUser() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Updated\",\"data\":{\"id\":3,\"firstName\":\"Bob\",\"lastName\":\"Smith\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> resp = client.users().updateUserProfile(3, "Bob", "Smith", null);
        assertEquals("Bob",   resp.getData().getFirstName());
        assertEquals("Smith", resp.getData().getLastName());
    }

    @Test
    void updateUserProfile_correctPath() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":8}}")
                .addHeader("Content-Type", "application/json"));
        client.users().updateUserProfile(8, "Test", "User", "+1555000");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/users/8"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void user_toXml_containsFields() {
        User u = new User();
        u.setId(1);
        u.setEmail("test@example.com");
        u.setFirstName("Jane");
        String xml = XmlConverter.toXml(u);
        assertTrue(xml.contains("<id>1</id>"));
        assertTrue(xml.contains("<email>test@example.com</email>"));
        assertTrue(xml.contains("<firstName>Jane</firstName>"));
    }

    @Test
    void user_fromXml_roundTrip() {
        User original = new User();
        original.setId(9);
        original.setEmail("round@trip.com");
        original.setFirstName("Round");
        original.setLastName("Trip");
        String xml     = XmlConverter.toXml(original);
        User   restored = XmlConverter.fromXml(xml, User.class);
        assertEquals(9,                restored.getId());
        assertEquals("round@trip.com", restored.getEmail());
        assertEquals("Round",          restored.getFirstName());
        assertEquals("Trip",           restored.getLastName());
    }

    @Test
    void user_toXml_validXml() {
        User u = new User();
        u.setId(2);
        String xml = XmlConverter.toXml(u);
        assertTrue(XmlConverter.isXml(xml));
        assertTrue(xml.contains("<user>") || xml.contains("<user "));
    }

    @Test
    void xmlConverter_isXml_detectsNonXml() {
        assertFalse(XmlConverter.isXml(null));
        assertFalse(XmlConverter.isXml(""));
        assertFalse(XmlConverter.isXml("plain text"));
        assertTrue(XmlConverter.isXml("<?xml version=\"1.0\"?><root/>"));
        assertTrue(XmlConverter.isXml("<root/>"));
    }

    @Test
    void fetchAccessToken_tokenStoredViaSetToken() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"id\":3,\"token\":\"stored-tok\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<User> resp = client.users().fetchAccessToken(3);
        client.setToken(resp.getData().getToken());
        assertEquals("stored-tok", client.getToken());
    }

    @Test
    void user_allFieldsRoundTrip() {
        User u = new User();
        u.setId(100);
        u.setEmail("all@fields.com");
        u.setFirstName("Full");
        u.setLastName("Fields");
        u.setPhone("+1-555-0199");
        u.setRole("partner");
        u.setActive(true);
        String xml = XmlConverter.toXml(u);
        User r     = XmlConverter.fromXml(xml, User.class);
        assertEquals(100,           r.getId());
        assertEquals("all@fields.com", r.getEmail());
        assertEquals("Full",        r.getFirstName());
        assertEquals("Fields",      r.getLastName());
        assertEquals("+1-555-0199", r.getPhone());
        assertEquals("partner",     r.getRole());
        assertTrue(r.getActive());
    }
}
