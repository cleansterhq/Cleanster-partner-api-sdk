package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Other / Utilities API - reference data used in booking flows.
 *
 * <h3>Endpoints (11)</h3>
 * <ol>
 *   <li>GET  /v1/services               - list service types</li>
 *   <li>GET  /v1/plans                  - list plans for a property</li>
 *   <li>GET  /v1/recommended-hours      - recommended cleaning hours</li>
 *   <li>POST /v1/cost-estimate          - cost estimate</li>
 *   <li>GET  /v1/cleaning-extras/{id}   - cleaning extras for a service</li>
 *   <li>POST /v1/available-cleaners     - find available cleaners</li>
 *   <li>GET  /v1/coupons                - list valid coupons</li>
 *   <li>GET  /v1/cleaners               - list cleaners</li>
 *   <li>GET  /v1/cleaners/{id}          - get a cleaner</li>
 *   <li>GET  /v1/tasks                  - list tasks</li>
 *   <li>GET  /v1/services/{id}/subcategories - list subcategories for a service</li>
 * </ol>
 */
public class OtherXmlApi {

    private final XmlHttpClient http;

    public OtherXmlApi(XmlHttpClient http) { this.http = http; }

    /** Return all available cleaning service types (e.g. Residential, Airbnb). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getServices() {
        String json = http.get("/v1/services");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Return available booking plans for a given property.
     *
     * @param propertyId  The property ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPlans(int propertyId, Integer subcatId) {
        String path = "/v1/plans?propertyId=" + propertyId;
        if (subcatId != null) path += "&subcatId=" + subcatId;
        String json = http.get(path);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Return available booking plans for a given property (no subcategory filter). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getPlans(int propertyId) {
        return getPlans(propertyId, null);
    }

    /**
     * Get the system-recommended cleaning hours based on property size.
     *
     * @param propertyId     The property ID.
     * @param bathroomCount  Number of bathrooms.
     * @param roomCount      Number of rooms/bedrooms.
     * @param subcatId       Optional service subcategory ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getRecommendedHours(int propertyId, int bathroomCount, int roomCount, Integer subcatId) {
        String path = "/v1/recommended-hours?propertyId=" + propertyId
                + "&bathroomCount=" + bathroomCount
                + "&roomCount=" + roomCount;
        if (subcatId != null) path += "&subcatId=" + subcatId;
        String json = http.get(path);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Get the system-recommended cleaning hours (no subcategory filter). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getRecommendedHours(int propertyId, int bathroomCount, int roomCount) {
        return getRecommendedHours(propertyId, bathroomCount, roomCount, null);
    }

    /**
     * Calculate the estimated cost for a potential booking.
     *
     * @param request  Map with keys: propertyId, planId, hours, and optionally couponCode.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getCostEstimate(Map<String, Object> request) {
        String json = http.post("/v1/cost-estimate", request);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Get available add-on services for a given service type.
     *
     * @param serviceId  The service type ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getCleaningExtras(int serviceId) {
        String json = http.get("/v1/cleaning-extras/" + serviceId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Find cleaners available for a specific property, date, and time.
     *
     * @param request  Map with keys: propertyId, date, time.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getAvailableCleaners(Map<String, Object> request) {
        String json = http.post("/v1/available-cleaners", request);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Return all valid coupon codes available for use. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getCoupons() {
        String json = http.get("/v1/coupons");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * List all cleaners, with optional status and search filters.
     *
     * @param status  Filter by cleaner status (active, inactive, pending). Null returns all.
     * @param search  Partial match against cleaner name or email. Null returns all.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse listCleaners(String status, String search) {
        List<String> params = new ArrayList<>();
        if (status != null) params.add("status=" + status);
        if (search != null) params.add("search=" + search);
        String path = "/v1/cleaners" + (params.isEmpty() ? "" : "?" + String.join("&", params));
        String json = http.get(path);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** List all cleaners (no filters). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse listCleaners() {
        return listCleaners(null, null);
    }

    /**
     * Retrieve a single cleaner by their ID.
     *
     * @param cleanerId  The cleaner's unique ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getCleaner(int cleanerId) {
        String json = http.get("/v1/cleaners/" + cleanerId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * List service tasks, filterable by property and service type. Supports pagination.
     *
     * @param propertyId  The property ID.
     * @param serviceId   The service type ID.
     * @param pageNo      Optional page number.
     * @param pageSize    Optional page size.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getTasks(int propertyId, int serviceId, Integer pageNo, Integer pageSize) {
        StringBuilder path = new StringBuilder("/v1/tasks?propertyId=" + propertyId + "&serviceId=" + serviceId);
        if (pageNo != null) path.append("&pageNo=").append(pageNo);
        if (pageSize != null) path.append("&pageSize=").append(pageSize);
        String json = http.get(path.toString());
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** List service tasks (no pagination). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getTasks(int propertyId, int serviceId) {
        return getTasks(propertyId, serviceId, null, null);
    }

    /**
     * Get the subcategories available under a given service type.
     *
     * @param serviceId  The service type ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getSubcategories(int serviceId) {
        String json = http.get("/v1/services/" + serviceId + "/subcategories");
        return http.fromJson(json, XmlApiResponse.class);
    }
}
