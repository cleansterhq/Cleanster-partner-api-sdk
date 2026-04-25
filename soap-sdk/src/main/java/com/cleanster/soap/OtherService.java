package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements services/cost/extras and other miscellaneous SOAP operations. */
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

    public JsonNode getPlans(long propertyId) {
        return transport.extractData(
                transport.get("/v1/plans?propertyId=" + propertyId));
    }

    public JsonNode getRecommendedHours(long propertyId, int bathroomCount, int roomCount) {
        return transport.extractData(transport.get(
                "/v1/recommended-hours?propertyId=" + propertyId
                + "&bathroomCount=" + bathroomCount
                + "&roomCount=" + roomCount));
    }

    public JsonNode getCostEstimate(Map<String, Object> request) {
        return transport.extractData(transport.post("/v1/cost-estimate", request));
    }

    public JsonNode getCleaningExtras(long serviceId) {
        return transport.extractData(
                transport.get("/v1/cleaning-extras/" + serviceId));
    }

    public JsonNode getAvailableCleaners(Map<String, Object> request) {
        return transport.extractData(transport.post("/v1/available-cleaners", request));
    }

    public JsonNode getCoupons() {
        return transport.extractData(transport.get("/v1/coupons"));
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
