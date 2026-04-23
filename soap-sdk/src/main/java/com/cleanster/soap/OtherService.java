package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements services/chat and other miscellaneous SOAP operations. */
public class OtherService {

    private final SOAPTransport transport;

    public OtherService(SOAPTransport transport) {
        this.transport = transport;
    }

    public List<ServiceType> getServices() {
        JsonNode root = transport.get("/v1/services");
        JsonNode data = transport.extractData(root);
        List<ServiceType> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, ServiceType.class));
            }
        }
        return list;
    }

    public List<ChatMessage> getChat(long bookingId) {
        JsonNode root = transport.get("/v1/bookings/" + bookingId + "/chat");
        JsonNode data = transport.extractData(root);
        List<ChatMessage> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, ChatMessage.class));
            }
        }
        return list;
    }

    public ChatMessage sendMessage(long bookingId, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/chat", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), ChatMessage.class);
    }
}
