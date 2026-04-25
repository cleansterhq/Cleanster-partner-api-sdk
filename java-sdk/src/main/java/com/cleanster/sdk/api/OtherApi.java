package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Miscellaneous API operations: services, plans, cost estimates, cleaning extras,
 * available cleaners, recommended hours, and coupons.
 */
public class OtherApi {

    private final HttpClient httpClient;

    public OtherApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * View all available cleaning services.
     *
     * @return API response with list of services
     */
    public ApiResponse<Object> getServices() {
        return httpClient.get("/v1/services", new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get available booking plans for a property.
     *
     * @param propertyId The property ID
     * @return API response with list of plans
     */
    public ApiResponse<Object> getPlans(int propertyId) {
        return httpClient.get("/v1/plans?propertyId=" + propertyId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get recommended cleaning hours based on property attributes.
     *
     * @param propertyId     The property ID
     * @param bathroomCount  Number of bathrooms
     * @param roomCount      Number of rooms
     * @return API response with recommended hours
     */
    public ApiResponse<Object> getRecommendedHours(int propertyId, int bathroomCount, int roomCount) {
        return httpClient.get(
                "/v1/recommended-hours?propertyId=" + propertyId
                        + "&bathroomCount=" + bathroomCount
                        + "&roomCount=" + roomCount,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Calculate the cost estimate for a potential booking.
     *
     * @param request Cost estimate parameters
     * @return API response with pricing breakdown
     */
    public ApiResponse<Object> getCostEstimate(CostEstimateRequest request) {
        return httpClient.post("/v1/cost-estimate", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get available cleaning extras (add-on services) for a given service type.
     *
     * @param serviceId The service ID
     * @return API response with list of extras
     */
    public ApiResponse<Object> getCleaningExtras(int serviceId) {
        return httpClient.get("/v1/cleaning-extras/" + serviceId,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get a list of available cleaners for a specific date/time/property combination.
     *
     * @param request Availability search parameters
     * @return API response with list of available cleaners
     */
    public ApiResponse<Object> getAvailableCleaners(AvailableCleanersRequest request) {
        return httpClient.post("/v1/available-cleaners", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get available coupon codes.
     *
     * @return API response with list of coupon codes
     */
    public ApiResponse<Object> getCoupons() {
        return httpClient.get("/v1/coupons", new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * List all cleaners, with optional status and search filters.
     *
     * @param status Filter by cleaner status ('active', 'inactive', 'pending'). Pass null to omit.
     * @param search Partial match against cleaner name or email. Pass null to omit.
     * @return API response with list of cleaner objects
     */
    public ApiResponse<Object> listCleaners(String status, String search) {
        StringBuilder path = new StringBuilder("/v1/cleaners");
        String sep = "?";
        if (status != null && !status.isEmpty()) { path.append(sep).append("status=").append(status); sep = "&"; }
        if (search != null && !search.isEmpty()) { path.append(sep).append("search=").append(search); }
        return httpClient.get(path.toString(), new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Retrieve a single cleaner by their ID.
     *
     * @param cleanerId The cleaner's unique ID.
     * @return API response with cleaner details
     */
    public ApiResponse<Object> getCleaner(int cleanerId) {
        return httpClient.get("/v1/cleaners/" + cleanerId, new TypeReference<ApiResponse<Object>>() {});
    }
}
