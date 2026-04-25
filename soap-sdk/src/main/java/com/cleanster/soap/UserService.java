package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/** Implements all user-related SOAP operations. */
public class UserService {

    private final SOAPTransport transport;

    public UserService(SOAPTransport transport) {
        this.transport = transport;
    }

    public User createUser(CreateUserRequest request) {
        JsonNode root = transport.post("/v1/user/account", request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), User.class);
    }

    public User fetchAccessToken(long userId) {
        JsonNode root = transport.get("/v1/user/access-token/" + userId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), User.class);
    }

    public ApiResponse verifyJwt(String token) {
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        JsonNode root = transport.post("/v1/user/verify-jwt", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        String message = root.has("message") ? root.get("message").asText() : "Valid";
        return new ApiResponse(status, message);
    }
}
