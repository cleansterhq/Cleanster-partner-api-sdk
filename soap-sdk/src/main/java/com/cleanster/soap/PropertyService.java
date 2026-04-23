package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all property-related SOAP operations. */
public class PropertyService {

    private final SOAPTransport transport;

    public PropertyService(SOAPTransport transport) {
        this.transport = transport;
    }

    public Property getProperty(long propertyId) {
        JsonNode root = transport.get("/v1/properties/" + propertyId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }

    public List<Property> listProperties(int page, int perPage) {
        JsonNode root = transport.get(
                "/v1/properties?page=" + page + "&per_page=" + perPage);
        JsonNode data = transport.extractData(root);
        List<Property> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Property.class));
            }
        }
        return list;
    }

    public Property createProperty(CreatePropertyRequest request) {
        JsonNode root = transport.post("/v1/properties", request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }

    public Property updateProperty(long propertyId, CreatePropertyRequest request) {
        JsonNode root = transport.put("/v1/properties/" + propertyId, request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }

    public ApiResponse updateAdditionalInformation(long propertyId, Map<String, Object> info) {
        JsonNode root = transport.put("/v1/properties/" + propertyId + "/additional-info", info);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse enableOrDisableProperty(long propertyId, boolean enabled) {
        Map<String, Object> body = new HashMap<>();
        body.put("enabled", enabled);
        JsonNode root = transport.post("/v1/properties/" + propertyId + "/enable-disable", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse deleteProperty(long propertyId) {
        JsonNode root = transport.delete("/v1/properties/" + propertyId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public List<Cleaner> getPropertyCleaners(long propertyId) {
        JsonNode root = transport.get("/v1/properties/" + propertyId + "/cleaners");
        JsonNode data = transport.extractData(root);
        List<Cleaner> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Cleaner.class));
            }
        }
        return list;
    }

    public ApiResponse assignCleanerToProperty(long propertyId, long cleanerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("cleaner_id", cleanerId);
        JsonNode root = transport.post("/v1/properties/" + propertyId + "/cleaners", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse unassignCleanerFromProperty(long propertyId, long cleanerId) {
        JsonNode root = transport.delete("/v1/properties/" + propertyId + "/cleaners/" + cleanerId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse addICalLink(long propertyId, String icalUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("ical_url", icalUrl);
        JsonNode root = transport.post("/v1/properties/" + propertyId + "/ical", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public JsonNode getICalLink(long propertyId) {
        return transport.extractData(transport.get("/v1/properties/" + propertyId + "/ical"));
    }

    public ApiResponse removeICalLink(long propertyId, String icalUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("ical_url", icalUrl);
        JsonNode root = transport.delete("/v1/properties/" + propertyId + "/ical");
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse setDefaultChecklist(long propertyId, long checklistId, boolean updateUpcoming) {
        Map<String, Object> body = new HashMap<>();
        body.put("checklist_id", checklistId);
        body.put("update_upcoming_bookings", updateUpcoming);
        JsonNode root = transport.put("/v1/properties/" + propertyId + "/checklist", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }
}
