package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Property;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Properties API — manage cleaning locations (homes, offices, etc.).
 *
 * <h3>Endpoints (14)</h3>
 * <ol>
 *   <li>GET    /properties              — list properties</li>
 *   <li>GET    /properties/{id}         — get property</li>
 *   <li>POST   /properties              — create property</li>
 *   <li>PUT    /properties/{id}         — update property</li>
 *   <li>DELETE /properties/{id}         — delete property</li>
 *   <li>GET    /properties/{id}/bookings — property bookings</li>
 *   <li>GET    /properties/{id}/checklists — property checklists</li>
 *   <li>POST   /properties/{id}/archive  — archive property</li>
 *   <li>POST   /properties/{id}/restore  — restore property</li>
 *   <li>GET    /properties/active        — active properties</li>
 *   <li>GET    /properties/archived      — archived properties</li>
 *   <li>POST   /properties/{id}/duplicate — duplicate property</li>
 *   <li>GET    /properties/{id}/access-info — access instructions</li>
 *   <li>PUT    /properties/{id}/access-info — update access instructions</li>
 * </ol>
 */
public class PropertiesXmlApi {

    private final XmlHttpClient http;

    public PropertiesXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<Property>> listProperties() {
        String json = http.get("/properties");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Property>>>(){}.getType());
    }

    public XmlApiResponse<Property> getProperty(int propertyId) {
        String json = http.get("/properties/" + propertyId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    public XmlApiResponse<Property> createProperty(Map<String, Object> body) {
        String json = http.post("/properties", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    public XmlApiResponse<Property> createProperty(String name, String address, String city,
                                                    String state, String zipCode, String country,
                                                    int roomCount, int bathroomCount) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name",          name);
        body.put("address",       address);
        body.put("city",          city);
        body.put("state",         state);
        body.put("zipCode",       zipCode);
        body.put("country",       country);
        body.put("roomCount",     roomCount);
        body.put("bathroomCount", bathroomCount);
        return createProperty(body);
    }

    public XmlApiResponse<Property> updateProperty(int propertyId, Map<String, Object> body) {
        String json = http.put("/properties/" + propertyId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    public XmlApiResponse<Property> deleteProperty(int propertyId) {
        String json = http.delete("/properties/" + propertyId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPropertyBookings(int propertyId) {
        String json = http.get("/properties/" + propertyId + "/bookings");
        return http.fromJson(json, XmlApiResponse.class);
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPropertyChecklists(int propertyId) {
        String json = http.get("/properties/" + propertyId + "/checklists");
        return http.fromJson(json, XmlApiResponse.class);
    }

    public XmlApiResponse<Property> archiveProperty(int propertyId) {
        String json = http.post("/properties/" + propertyId + "/archive", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    public XmlApiResponse<Property> restoreProperty(int propertyId) {
        String json = http.post("/properties/" + propertyId + "/restore", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    public XmlApiResponse<List<Property>> listActiveProperties() {
        String json = http.get("/properties/active");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Property>>>(){}.getType());
    }

    public XmlApiResponse<List<Property>> listArchivedProperties() {
        String json = http.get("/properties/archived");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Property>>>(){}.getType());
    }

    public XmlApiResponse<Property> duplicateProperty(int propertyId) {
        String json = http.post("/properties/" + propertyId + "/duplicate", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getAccessInfo(int propertyId) {
        String json = http.get("/properties/" + propertyId + "/access-info");
        return http.fromJson(json, XmlApiResponse.class);
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse updateAccessInfo(int propertyId, String instructions) {
        String json = http.put("/properties/" + propertyId + "/access-info",
                Map.of("accessInstructions", instructions));
        return http.fromJson(json, XmlApiResponse.class);
    }
}
