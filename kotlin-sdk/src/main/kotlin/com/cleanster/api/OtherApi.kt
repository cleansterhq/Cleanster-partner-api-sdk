package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for reference data — services, plans, pricing, extras, and cleaners. */
class OtherApi internal constructor(private val client: CleansterClient) {

    /** Get all cleaning service types available on the partner account. */
    suspend fun getServices(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/services",
    )

    /**
     * Get available booking plans for a property.
     *
     * @param propertyId The property to fetch plans for.
     */
    suspend fun getPlans(propertyId: Int): ApiResponse<List<Any>> = client.request(
        method      = "GET",
        path        = "/v1/plans",
        queryParams = mapOf("propertyId" to "$propertyId"),
    )

    /**
     * Get the system-recommended number of cleaning hours.
     *
     * Use the returned `hours` value as the `hours` field when creating a booking.
     */
    suspend fun getRecommendedHours(
        propertyId:    Int,
        roomCount:     Int,
        bathroomCount: Int,
    ): ApiResponse<RecommendedHours> = client.request(
        method      = "GET",
        path        = "/v1/recommended-hours",
        queryParams = mapOf(
            "propertyId"    to "$propertyId",
            "roomCount"     to "$roomCount",
            "bathroomCount" to "$bathroomCount",
        ),
    )

    /**
     * Calculate the estimated total price for a potential booking.
     *
     * Call this to show users a price preview before confirming a booking.
     */
    suspend fun getCostEstimate(request: CostEstimateRequest): ApiResponse<CostEstimate> = client.request(
        method = "POST",
        path   = "/v1/cost-estimate",
        body   = request,
    )

    /**
     * Get available add-on services for a given service type.
     *
     * @param serviceId The service type ID (from [getServices]).
     */
    suspend fun getCleaningExtras(serviceId: Int): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/cleaning-extras/$serviceId",
    )

    /** Find cleaners available for a specific date, time, and property. */
    suspend fun getAvailableCleaners(request: AvailableCleanersRequest): ApiResponse<List<Any>> = client.request(
        method = "POST",
        path   = "/v1/available-cleaners",
        body   = request,
    )

    /** Get all valid coupon codes available for use at booking creation. */
    suspend fun getCoupons(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/coupons",
    )

    /**
     * List all cleaners, with optional status and search filters.
     *
     * @param status Filter by cleaner status ('active', 'inactive', 'pending'). Null omits this filter.
     * @param search Partial match against cleaner name or email. Null omits this filter.
     */
    suspend fun listCleaners(status: String? = null, search: String? = null): ApiResponse<List<Any>> = client.request(
        method      = "GET",
        path        = "/v1/cleaners",
        queryParams = buildMap { status?.let { put("status", it) }; search?.let { put("search", it) } }
            .takeIf { it.isNotEmpty() },
    )

    /**
     * Retrieve a single cleaner by their ID.
     *
     * @param cleanerId The cleaner's unique ID.
     */
    suspend fun getCleaner(cleanerId: Int): ApiResponse<Any> = client.request(
        method = "GET",
        path   = "/v1/cleaners/$cleanerId",
    )
}
