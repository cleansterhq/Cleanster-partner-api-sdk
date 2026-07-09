package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Property;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Properties API - manage cleaning locations (homes, offices, etc.).
 *
 * <h3>Endpoints (14)</h3>
 * <ol>
 *   <li>GET    /v1/properties                                          - list properties</li>
 *   <li>POST   /v1/properties                                          - create property</li>
 *   <li>GET    /v1/properties/{id}                                     - get property</li>
 *   <li>PUT    /v1/properties/{id}                                     - update property</li>
 *   <li>DELETE /v1/properties/{id}                                     - delete property</li>
 *   <li>PUT    /v1/properties/{id}/additional-information              - update additional info</li>
 *   <li>POST   /v1/properties/{id}/enable-disable                      - enable/disable property</li>
 *   <li>GET    /v1/properties/{id}/cleaners                            - list property cleaners</li>
 *   <li>POST   /v1/properties/{id}/cleaners                            - assign cleaner</li>
 *   <li>DELETE /v1/properties/{id}/cleaners/{cleanerId}                - remove cleaner</li>
 *   <li>PUT    /v1/properties/{id}/ical                                - add iCal link</li>
 *   <li>GET    /v1/properties/{id}/ical                                - get iCal link</li>
 *   <li>DELETE /v1/properties/{id}/ical                                - remove iCal link</li>
 *   <li>PUT    /v1/properties/{id}/checklist/{checklistId}             - set default checklist</li>
 * </ol>
 */
public class PropertiesXmlApi {

    private final XmlHttpClient http;

    public PropertiesXmlApi(XmlHttpClient http) { this.http = http; }

    /** List all properties, optionally filtered by service type. */
    public XmlApiResponse<List<Property>> listProperties(Integer serviceId) {
        String path = serviceId != null
                ? "/v1/properties?serviceId=" + serviceId
                : "/v1/properties";
        String json = http.get(path);
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Property>>>(){}.getType());
    }

    /** List all properties (no filter). */
    public XmlApiResponse<List<Property>> listProperties() {
        return listProperties(null);
    }

    /** Create a new property. */
    public XmlApiResponse<Property> createProperty(Map<String, Object> body) {
        String json = http.post("/v1/properties", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    /** Convenience overload - build the body from typed parameters. */
    public XmlApiResponse<Property> createProperty(String name, String address, String city,
                                                    String country, int roomCount,
                                                    int bathroomCount, int serviceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name",          name);
        body.put("address",       address);
        body.put("city",          city);
        body.put("country",       country);
        body.put("roomCount",     roomCount);
        body.put("bathroomCount", bathroomCount);
        body.put("serviceId",     serviceId);
        return createProperty(body);
    }

    /** Get details of a specific property. */
    public XmlApiResponse<Property> getProperty(int propertyId) {
        String json = http.get("/v1/properties/" + propertyId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    /** Update an existing property's details. */
    public XmlApiResponse<Property> updateProperty(int propertyId, Map<String, Object> body) {
        String json = http.put("/v1/properties/" + propertyId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Property>>(){}.getType());
    }

    /** Permanently delete a property. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse deleteProperty(int propertyId) {
        String json = http.delete("/v1/properties/" + propertyId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Update additional/supplemental information fields for a property. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse updateAdditionalInformation(int propertyId, Map<String, Object> data) {
        String json = http.put("/v1/properties/" + propertyId + "/additional-information", data);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Enable or disable a property. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse enableOrDisableProperty(int propertyId, boolean enabled) {
        String json = http.post("/v1/properties/" + propertyId + "/enable-disable",
                Map.of("enabled", enabled));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Get the list of cleaners assigned to a property. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPropertyCleaners(int propertyId) {
        String json = http.get("/v1/properties/" + propertyId + "/cleaners");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Assign a cleaner to a property's default cleaner pool. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse assignCleanerToProperty(int propertyId, int cleanerId) {
        String json = http.post("/v1/properties/" + propertyId + "/cleaners",
                Map.of("cleanerId", cleanerId));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Remove a cleaner from a property's default cleaner pool. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse unassignCleanerFromProperty(int propertyId, int cleanerId) {
        String json = http.delete("/v1/properties/" + propertyId + "/cleaners/" + cleanerId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Add one or more iCal calendar links to a property for availability syncing.
     * Each URL must be a live, publicly fetchable .ics feed - the API validates
     * the feed content, not just the URL shape.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse addICalLink(int propertyId, java.util.List<String> calendarLinks) {
        String json = http.put("/v1/properties/" + propertyId + "/ical",
                Map.of("calendarLinks", calendarLinks));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Retrieve the calendar links currently attached to a property (each with numeric id + calendarLink URL). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getICalLink(int propertyId) {
        String json = http.get("/v1/properties/" + propertyId + "/ical");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Remove one or more calendar links from a property, by numeric link ID
     * (from getICalLink) - not by URL.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse removeICalLink(int propertyId, java.util.List<Integer> ids) {
        String json = http.delete("/v1/properties/" + propertyId + "/ical", Map.of("ids", ids));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Set a default checklist for a property.
     *
     * @param propertyId              The property ID.
     * @param checklistId             The checklist ID.
     * @param updateUpcomingBookings  If true, applies to all upcoming bookings too.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse setDefaultChecklist(int propertyId, int checklistId,
                                               boolean updateUpcomingBookings) {
        String path = "/v1/properties/" + propertyId + "/checklist/" + checklistId
                + "?updateUpcomingBookings=" + updateUpcomingBookings;
        String json = http.put(path, null);
        return http.fromJson(json, XmlApiResponse.class);
    }
}
