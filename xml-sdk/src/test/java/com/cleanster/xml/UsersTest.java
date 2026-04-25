package com.cleanster.xml;

import com.cleanster.xml.api.UsersXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.User;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsersTest {

    private XmlHttpClient http;
    private UsersXmlApi   api;

    private static final String USER_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":42,\"email\":\"alice@example.com\",\"firstName\":\"Alice\"}}";
    private static final String TOKEN_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":42,\"token\":\"jwt-abc-123\"}}";
    private static final String VERIFY_JSON = "{\"success\":true,\"message\":\"valid\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new UsersXmlApi(http);
    }

    // ── createUser ─────────────────────────────────────────────────────────────

    @Test void createUser_callsPost_v1UserAccount() {
        doReturn(USER_JSON).when(http).post(eq("/v1/user/account"), any());
        api.createUser("alice@example.com", "Alice", "Smith");
        verify(http).post(eq("/v1/user/account"), any());
    }

    @Test void createUser_returnsParsedUser() {
        doReturn(USER_JSON).when(http).post(eq("/v1/user/account"), any());
        XmlApiResponse<User> resp = api.createUser("alice@example.com", "Alice", "Smith");
        assertTrue(resp.isSuccess());
        assertEquals(42, (int) resp.getData().getId());
    }

    @Test void createUser_mapOverload_callsPost() {
        doReturn(USER_JSON).when(http).post(eq("/v1/user/account"), any());
        Map<String, Object> body = Map.of("email", "alice@example.com");
        api.createUser(body);
        verify(http).post(eq("/v1/user/account"), any());
    }

    // ── fetchAccessToken ───────────────────────────────────────────────────────

    @Test void fetchAccessToken_callsGet_v1UserToken() {
        doReturn(TOKEN_JSON).when(http).get("/v1/user/access-token/42");
        api.fetchAccessToken(42);
        verify(http).get("/v1/user/access-token/42");
    }

    @Test void fetchAccessToken_returnsParsedToken() {
        doReturn(TOKEN_JSON).when(http).get("/v1/user/access-token/42");
        XmlApiResponse<User> resp = api.fetchAccessToken(42);
        assertTrue(resp.isSuccess());
        assertEquals("jwt-abc-123", resp.getData().getToken());
    }

    @Test void fetchAccessToken_differentId_callsCorrectPath() {
        doReturn(TOKEN_JSON).when(http).get("/v1/user/access-token/99");
        api.fetchAccessToken(99);
        verify(http).get("/v1/user/access-token/99");
    }

    // ── verifyJwt ──────────────────────────────────────────────────────────────

    @Test void verifyJwt_callsPost_v1UserVerifyJwt() {
        doReturn(VERIFY_JSON).when(http).post(eq("/v1/user/verify-jwt"), any());
        api.verifyJwt("my-jwt-token");
        verify(http).post(eq("/v1/user/verify-jwt"), any());
    }

    @Test void verifyJwt_returnsSuccess() {
        doReturn(VERIFY_JSON).when(http).post(eq("/v1/user/verify-jwt"), any());
        assertTrue(api.verifyJwt("my-jwt-token").isSuccess());
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void user_toXml_isValidXml() {
        User u = new User(); u.setId(1); u.setEmail("test@example.com");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(u)));
    }

    @Test void user_fromXml_roundTrip() {
        User o = new User(); o.setId(7); o.setEmail("bob@example.com"); o.setToken("tok-xyz");
        User r = XmlConverter.fromXml(XmlConverter.toXml(o), User.class);
        assertEquals(7, (int) r.getId());
        assertEquals("bob@example.com", r.getEmail());
        assertEquals("tok-xyz", r.getToken());
    }
}
