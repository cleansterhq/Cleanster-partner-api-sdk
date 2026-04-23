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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlacklistServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        when(transport.getObjectMapper()).thenReturn(MAPPER);
        client = new CleansterSOAPClient(transport);
    }

    // ListBlacklist
    @Test
    @DisplayName("listBlacklist calls GET /v1/blacklist")
    void listBlacklistCallsCorrectPath() {
        when(transport.get("/v1/blacklist")).thenReturn(MAPPER.createArrayNode());
        client.listBlacklist();
        verify(transport).get("/v1/blacklist");
    }

    @Test
    @DisplayName("listBlacklist returns empty list when no entries")
    void listBlacklistReturnsEmptyList() {
        when(transport.get("/v1/blacklist")).thenReturn(MAPPER.createArrayNode());
        List<BlacklistEntry> result = client.listBlacklist();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // AddToBlacklist
    @Test
    @DisplayName("addToBlacklist calls POST /v1/blacklist")
    void addToBlacklistCallsCorrectPath() {
        when(transport.post(eq("/v1/blacklist"), any())).thenReturn(okNode());
        client.addToBlacklist(789L, "No show");
        verify(transport).post(eq("/v1/blacklist"), any());
    }

    @Test
    @DisplayName("addToBlacklist returns success response")
    void addToBlacklistReturnsSuccess() {
        when(transport.post(eq("/v1/blacklist"), any())).thenReturn(okNode());
        ApiResponse resp = client.addToBlacklist(789L, "No show");
        assertTrue(resp.isSuccess());
    }

    // RemoveFromBlacklist
    @Test
    @DisplayName("removeFromBlacklist calls DELETE /v1/blacklist/{id}")
    void removeFromBlacklistCallsCorrectPath() {
        when(transport.delete("/v1/blacklist/789")).thenReturn(okNode());
        client.removeFromBlacklist(789L);
        verify(transport).delete("/v1/blacklist/789");
    }

    @Test
    @DisplayName("removeFromBlacklist returns success response")
    void removeFromBlacklistReturnsSuccess() {
        when(transport.delete("/v1/blacklist/789")).thenReturn(okNode());
        ApiResponse resp = client.removeFromBlacklist(789L);
        assertTrue(resp.isSuccess());
    }

    private JsonNode okNode() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", 200);
        n.put("message", "OK");
        return n;
    }
}
