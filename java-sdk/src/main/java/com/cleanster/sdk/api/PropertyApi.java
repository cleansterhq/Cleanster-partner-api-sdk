package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for property management: CRUD, cleaners, iCal links, and checklists.
 */
public class PropertyApi {

    private final HttpClient httpClient;

    public PropertyApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * List all properties. Optionally filter by service type.
     *
     * @param serviceId Filter by service ID, or null for all services
     * @return API response with list of properties
     */
    public ApiResponse<Object> listProperties(Integer serviceId) {
        String path = "/v1/properties" + (serviceId != null ? "?serviceId=" + serviceId : "");
        return httpClient.get(path, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Add a new property.
     *
     * @param request Property details
     * @return API response with created property
     */
    public ApiResponse<Property> addProperty(CreatePropertyRequest request) {
        return httpClient.post("/v1/properties", request,
                new TypeReference<ApiResponse<Property>>() {});
    }

    /**
     * Get details of a specific property.
     *
     * @param propertyId The property ID
     * @return API response with property details
     */
    public ApiResponse<Property> getProperty(int propertyId) {
        return httpClient.get("/v1/properties/" + propertyId,
                new TypeReference<ApiResponse<Property>>() {});
    }

    /**
     * Update an existing property.
     *
     * @param propertyId The property ID
     * @param request    Updated property details
     * @return API response
     */
    public ApiResponse<Property> updateProperty(int propertyId, CreatePropertyRequest request) {
        return httpClient.put("/v1/properties/" + propertyId, request,
                new TypeReference<ApiResponse<Property>>() {});
    }

    /**
     * Update additional information for a property.
     *
     * @param propertyId The property ID
     * @param request    Additional information fields
     * @return API response
     */
    public ApiResponse<Object> updateAdditionalInformation(int propertyId, Object request) {
        return httpClient.put("/v1/properties/" + propertyId + "/additional-information", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Enable or disable a property.
     *
     * @param propertyId The property ID
     * @param request    Enable/disable flag
     * @return API response
     */
    public ApiResponse<Object> enableOrDisableProperty(int propertyId, EnableDisablePropertyRequest request) {
        return httpClient.post("/v1/properties/" + propertyId + "/enable-disable", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Delete a property.
     *
     * @param propertyId The property ID
     * @return API response
     */
    public ApiResponse<Object> deleteProperty(int propertyId) {
        return httpClient.delete("/v1/properties/" + propertyId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    // ---- Property Cleaners ----

    /**
     * Get the list of cleaners assigned to a property.
     *
     * @param propertyId The property ID
     * @return API response with list of cleaners
     */
    public ApiResponse<Object> getPropertyCleaners(int propertyId) {
        return httpClient.get("/v1/properties/" + propertyId + "/cleaners",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Assign a cleaner to a property.
     *
     * @param propertyId The property ID
     * @param request    Cleaner assignment details
     * @return API response
     */
    public ApiResponse<Object> assignCleanerToProperty(int propertyId, AssignCleanerToPropertyRequest request) {
        return httpClient.post("/v1/properties/" + propertyId + "/cleaners", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Unassign a cleaner from a property.
     *
     * @param propertyId The property ID
     * @param cleanerId  The cleaner ID to unassign
     * @return API response
     */
    public ApiResponse<Object> unassignCleanerFromProperty(int propertyId, int cleanerId) {
        return httpClient.delete("/v1/properties/" + propertyId + "/cleaners/" + cleanerId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    // ---- iCal ----

    /**
     * Add an iCal link to a property.
     *
     * @param propertyId The property ID
     * @param request    iCal URL
     * @return API response
     */
    public ApiResponse<Object> addICalLink(int propertyId, ICalRequest request) {
        return httpClient.put("/v1/properties/" + propertyId + "/ical", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Retrieve the iCal link for a property.
     *
     * @param propertyId The property ID
     * @return API response with iCal URL
     */
    public ApiResponse<Object> getICalLink(int propertyId) {
        return httpClient.get("/v1/properties/" + propertyId + "/ical",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Remove the iCal link from a property.
     *
     * @param propertyId The property ID
     * @param request    iCal link to remove
     * @return API response
     */
    public ApiResponse<Object> removeICalLink(int propertyId, ICalRequest request) {
        return httpClient.delete("/v1/properties/" + propertyId + "/ical", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    // ---- Property Checklist ----

    /**
     * Assign a checklist to a property.
     *
     * @param propertyId              The property ID
     * @param checklistId             The checklist ID
     * @param updateUpcomingBookings  Whether to apply the checklist to upcoming bookings
     * @return API response
     */
    public ApiResponse<Object> setDefaultChecklist(int propertyId, int checklistId,
            boolean updateUpcomingBookings) {
        String path = "/v1/properties/" + propertyId + "/checklist/" + checklistId
                + "?updateUpcomingBookings=" + updateUpcomingBookings;
        return httpClient.put(path, null, new TypeReference<ApiResponse<Object>>() {});
    }
}
