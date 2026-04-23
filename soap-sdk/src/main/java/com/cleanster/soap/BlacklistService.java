package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all blacklist-related SOAP operations. */
public class BlacklistService {

    private final SOAPTransport transport;

    public BlacklistService(SOAPTransport transport) {
        this.transport = transport;
    }

    public List<BlacklistEntry> listBlacklist() {
        JsonNode root = transport.get("/v1/blacklist");
        JsonNode data = transport.extractData(root);
        List<BlacklistEntry> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, BlacklistEntry.class));
            }
        }
        return list;
    }

    public ApiResponse addToBlacklist(long cleanerId, String reason) {
        Map<String, Object> body = new HashMap<>();
        body.put("cleaner_id", cleanerId);
        if (reason != null) body.put("reason", reason);
        JsonNode root = transport.post("/v1/blacklist", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        String message = root.has("message") ? root.get("message").asText() : "OK";
        return new ApiResponse(status, message);
    }

    public ApiResponse removeFromBlacklist(long cleanerId) {
        JsonNode root = transport.delete("/v1/blacklist/" + cleanerId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }
}
