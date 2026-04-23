package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for managing cleaning properties (locations). */
class PropertiesApi internal constructor(private val client: CleansterClient) {

    /** List all properties. Optionally filter by service type. */
    suspend fun listProperties(serviceId: Int? = null): ApiResponse<List<Any>> = client.request(
        method      = "GET",
        path        = "/v1/properties",
        queryParams = serviceId?.let { mapOf("serviceId" to "$it") } ?: emptyMap(),
    )

    /** Create a new property. */
    suspend fun addProperty(request: CreatePropertyRequest): ApiResponse<Property> = client.request(
        method = "POST",
        path   = "/v1/properties",
        body   = request,
    )

    /** Get a single property by ID. */
    suspend fun getProperty(propertyId: Int): ApiResponse<Property> = client.request(
        method = "GET",
        path   = "/v1/properties/$propertyId",
    )

    /** Update an existing property. */
    suspend fun updateProperty(propertyId: Int, request: CreatePropertyRequest): ApiResponse<Property> = client.request(
        method = "PUT",
        path   = "/v1/properties/$propertyId",
        body   = request,
    )

    /** Permanently delete a property. */
    suspend fun deleteProperty(propertyId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/properties/$propertyId",
    )

    /** Update freeform additional information fields on a property. */
    suspend fun updateAdditionalInformation(
        propertyId: Int,
        fields:     Map<String, String>,
    ): ApiResponse<Map<String, Any>> = client.request(
        method = "PUT",
        path   = "/v1/properties/$propertyId/additional-information",
        body   = fields,
    )

    /** Enable or disable a property. Disabled properties cannot receive new bookings. */
    suspend fun enableOrDisableProperty(propertyId: Int, enabled: Boolean): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/properties/$propertyId/enable-disable",
        body   = EnableDisablePropertyRequest(enabled = enabled),
    )

    /** List the cleaners associated with a property. */
    suspend fun getPropertyCleaners(propertyId: Int): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/properties/$propertyId/cleaners",
    )

    /** Add a cleaner to a property's preferred pool. */
    suspend fun addCleanerToProperty(propertyId: Int, cleanerId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/properties/$propertyId/cleaners",
        body   = AddPropertyCleanerRequest(cleanerId = cleanerId),
    )

    /** Remove a cleaner from a property's preferred pool. */
    suspend fun removeCleanerFromProperty(propertyId: Int, cleanerId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/properties/$propertyId/cleaners/$cleanerId",
    )

    /** Set an iCal feed URL for calendar sync (e.g. Airbnb, VRBO). */
    suspend fun setICalLink(propertyId: Int, icalLink: String): ApiResponse<Map<String, Any>> = client.request(
        method = "PUT",
        path   = "/v1/properties/$propertyId/ical",
        body   = SetICalLinkRequest(icalLink = icalLink),
    )

    /** Retrieve the current iCal feed URL for a property. */
    suspend fun getICalLink(propertyId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "GET",
        path   = "/v1/properties/$propertyId/ical",
    )

    /** Remove the iCal feed link from a property. */
    suspend fun deleteICalLink(propertyId: Int, icalLink: String): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/properties/$propertyId/ical",
        body   = DeleteICalLinkRequest(icalLink = icalLink),
    )

    /**
     * Assign a checklist as the default for all future bookings on a property.
     *
     * @param updateUpcomingBookings If `true`, also updates already-scheduled bookings.
     */
    suspend fun setDefaultChecklist(
        propertyId:             Int,
        checklistId:            Int,
        updateUpcomingBookings: Boolean = false,
    ): ApiResponse<Map<String, Any>> = client.request(
        method      = "PUT",
        path        = "/v1/properties/$propertyId/checklist/$checklistId",
        queryParams = mapOf("updateUpcomingBookings" to "$updateUpcomingBookings"),
    )
}
