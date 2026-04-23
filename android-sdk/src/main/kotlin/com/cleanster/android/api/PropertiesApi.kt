package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface PropertiesService {
    @GET("v1/properties")
    suspend fun listProperties(@Query("serviceId") serviceId: Int? = null): ApiResponse<List<Property>>

    @POST("v1/properties")
    suspend fun addProperty(@Body body: CreatePropertyRequest): ApiResponse<Property>

    @GET("v1/properties/{propertyId}")
    suspend fun getProperty(@Path("propertyId") propertyId: Int): ApiResponse<Property>

    @PUT("v1/properties/{propertyId}")
    suspend fun updateProperty(
        @Path("propertyId") propertyId: Int,
        @Body body: CreatePropertyRequest,
    ): ApiResponse<Property>

    @DELETE("v1/properties/{propertyId}")
    suspend fun deleteProperty(@Path("propertyId") propertyId: Int): ApiResponse<Any>

    @PUT("v1/properties/{propertyId}/additional-information")
    suspend fun updateAdditionalInformation(
        @Path("propertyId") propertyId: Int,
        @Body body: Map<String, String>,
    ): ApiResponse<Any>

    @POST("v1/properties/{propertyId}/enable-disable")
    suspend fun enableOrDisableProperty(
        @Path("propertyId") propertyId: Int,
        @Body body: EnableDisableRequest,
    ): ApiResponse<Any>

    @GET("v1/properties/{propertyId}/cleaners")
    suspend fun getPropertyCleaners(@Path("propertyId") propertyId: Int): ApiResponse<List<Cleaner>>

    @POST("v1/properties/{propertyId}/cleaners")
    suspend fun addCleanerToProperty(
        @Path("propertyId") propertyId: Int,
        @Body body: AddCleanerToPropertyRequest,
    ): ApiResponse<Any>

    @DELETE("v1/properties/{propertyId}/cleaners/{cleanerId}")
    suspend fun removeCleanerFromProperty(
        @Path("propertyId") propertyId: Int,
        @Path("cleanerId") cleanerId: Int,
    ): ApiResponse<Any>

    @PUT("v1/properties/{propertyId}/ical")
    suspend fun setICalLink(
        @Path("propertyId") propertyId: Int,
        @Body body: ICalRequest,
    ): ApiResponse<Any>

    @GET("v1/properties/{propertyId}/ical")
    suspend fun getICalLink(@Path("propertyId") propertyId: Int): ApiResponse<Any>

    @HTTP(method = "DELETE", path = "v1/properties/{propertyId}/ical", hasBody = true)
    suspend fun deleteICalLink(
        @Path("propertyId") propertyId: Int,
        @Body body: ICalRequest,
    ): ApiResponse<Any>

    @PUT("v1/properties/{propertyId}/checklist/{checklistId}")
    suspend fun setDefaultChecklist(
        @Path("propertyId") propertyId: Int,
        @Path("checklistId") checklistId: Int,
        @Query("updateUpcomingBookings") updateUpcomingBookings: Boolean? = null,
    ): ApiResponse<Any>
}

class PropertiesApi(retrofit: Retrofit) {
    private val service = retrofit.create(PropertiesService::class.java)

    suspend fun listProperties(serviceId: Int? = null) =
        wrap { service.listProperties(serviceId) }

    suspend fun addProperty(request: CreatePropertyRequest) =
        wrap { service.addProperty(request) }

    suspend fun getProperty(propertyId: Int) =
        wrap { service.getProperty(propertyId) }

    suspend fun updateProperty(propertyId: Int, request: CreatePropertyRequest) =
        wrap { service.updateProperty(propertyId, request) }

    suspend fun deleteProperty(propertyId: Int) =
        wrap { service.deleteProperty(propertyId) }

    suspend fun updateAdditionalInformation(propertyId: Int, info: Map<String, String>) =
        wrap { service.updateAdditionalInformation(propertyId, info) }

    suspend fun enableOrDisableProperty(propertyId: Int, enabled: Boolean) =
        wrap { service.enableOrDisableProperty(propertyId, EnableDisableRequest(enabled)) }

    suspend fun getPropertyCleaners(propertyId: Int) =
        wrap { service.getPropertyCleaners(propertyId) }

    suspend fun addCleanerToProperty(propertyId: Int, cleanerId: Int) =
        wrap { service.addCleanerToProperty(propertyId, AddCleanerToPropertyRequest(cleanerId)) }

    suspend fun removeCleanerFromProperty(propertyId: Int, cleanerId: Int) =
        wrap { service.removeCleanerFromProperty(propertyId, cleanerId) }

    suspend fun setICalLink(propertyId: Int, icalLink: String) =
        wrap { service.setICalLink(propertyId, ICalRequest(icalLink)) }

    suspend fun getICalLink(propertyId: Int) =
        wrap { service.getICalLink(propertyId) }

    suspend fun deleteICalLink(propertyId: Int, icalLink: String) =
        wrap { service.deleteICalLink(propertyId, ICalRequest(icalLink)) }

    suspend fun setDefaultChecklist(
        propertyId: Int,
        checklistId: Int,
        updateUpcomingBookings: Boolean? = null,
    ) = wrap { service.setDefaultChecklist(propertyId, checklistId, updateUpcomingBookings) }
}
