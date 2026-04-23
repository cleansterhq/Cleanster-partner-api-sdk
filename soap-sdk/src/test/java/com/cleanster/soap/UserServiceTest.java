package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        when(transport.getObjectMapper()).thenReturn(MAPPER);
        client = new CleansterSOAPClient(transport);
    }

    // CreateUser
    @Test
    @DisplayName("createUser calls POST /v1/user")
    void createUserCallsCorrectPath() {
        when(transport.post(eq("/v1/user"), any())).thenReturn(userNode(1L));
        CreateUserRequest req = new CreateUserRequest()
                .setName("Alice").setEmail("alice@example.com").setPassword("secret");
        client.createUser(req);
        verify(transport).post(eq("/v1/user"), any());
    }

    @Test
    @DisplayName("createUser returns new User")
    void createUserReturnsUser() {
        when(transport.post(eq("/v1/user"), any())).thenReturn(userNode(1L));
        User result = client.createUser(new CreateUserRequest().setEmail("alice@example.com"));
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
    }

    // FetchAccessToken
    @Test
    @DisplayName("fetchAccessToken calls GET /v1/user/{id}/access-token")
    void fetchAccessTokenCallsCorrectPath() {
        when(transport.get("/v1/user/1/access-token")).thenReturn(userNode(1L));
        client.fetchAccessToken(1L);
        verify(transport).get("/v1/user/1/access-token");
    }

    @Test
    @DisplayName("fetchAccessToken returns User with token")
    void fetchAccessTokenReturnsUser() {
        when(transport.get("/v1/user/1/access-token")).thenReturn(userNode(1L));
        User result = client.fetchAccessToken(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // VerifyJwt
    @Test
    @DisplayName("verifyJwt calls POST /v1/user/verify-jwt")
    void verifyJwtCallsCorrectPath() {
        when(transport.post(eq("/v1/user/verify-jwt"), any())).thenReturn(okNode());
        client.verifyJwt("some.jwt.token");
        verify(transport).post(eq("/v1/user/verify-jwt"), any());
    }

    @Test
    @DisplayName("verifyJwt returns success response")
    void verifyJwtReturnsSuccess() {
        when(transport.post(eq("/v1/user/verify-jwt"), any())).thenReturn(okNode());
        ApiResponse resp = client.verifyJwt("some.jwt.token");
        assertTrue(resp.isSuccess());
    }

    // Helpers
    private JsonNode userNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("name", "Alice");
        n.put("email", "alice@example.com");
        return n;
    }

    private JsonNode okNode() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", 200);
        n.put("message", "OK");
        return n;
    }
}
