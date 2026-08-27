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

    public JsonNode getPlans(long propertyId, Long subcatId) {
        String path = "/v1/plans?propertyId=" + propertyId;
        if (subcatId != null) path += "&subcatId=" + subcatId;
        return transport.extractData(transport.get(path));
    }

    public JsonNode getPlans(long propertyId) {
        return getPlans(propertyId, null);
    }

    public JsonNode getRecommendedHours(long propertyId, int bathroomCount, int roomCount, Long subcatId) {
        String path = "/v1/recommended-hours?propertyId=" + propertyId
                + "&bathroomCount=" + bathroomCount
                + "&roomCount=" + roomCount;
        if (subcatId != null) path += "&subcatId=" + subcatId;
        return transport.extractData(transport.get(path));
    }

    public JsonNode getRecommendedHours(long propertyId, int bathroomCount, int roomCount) {
        return getRecommendedHours(propertyId, bathroomCount, roomCount, null);
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

    public JsonNode getTasks(long propertyId, long serviceId, Integer pageNo, Integer pageSize) {
        StringBuilder path = new StringBuilder("/v1/tasks?propertyId=" + propertyId + "&serviceId=" + serviceId);
        if (pageNo != null) path.append("&pageNo=").append(pageNo);
        if (pageSize != null) path.append("&pageSize=").append(pageSize);
        return transport.extractData(transport.get(path.toString()));
    }

    public JsonNode getTasks(long propertyId, long serviceId) {
        return getTasks(propertyId, serviceId, null, null);
    }

    public JsonNode getSubcategories(long serviceId) {
        return transport.extractData(transport.get("/v1/services/" + serviceId + "/subcategories"));
    }
}
