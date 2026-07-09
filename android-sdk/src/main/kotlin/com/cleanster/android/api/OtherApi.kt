package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface OtherService {
    @GET("v1/services")
    suspend fun getServices(): ApiResponse<List<Any>>

    @GET("v1/plans")
    suspend fun getPlans(
        @Query("propertyId") propertyId: Int,
        @Query("subcatId") subcatId: Int? = null,
    ): ApiResponse<List<Any>>

    @GET("v1/recommended-hours")
    suspend fun getRecommendedHours(
        @Query("propertyId")    propertyId: Int,
        @Query("roomCount")     roomCount: Int,
        @Query("bathroomCount") bathroomCount: Int,
        @Query("subcatId")      subcatId: Int? = null,
    ): ApiResponse<Any>

    @GET("v1/tasks")
    suspend fun getTasks(
        @Query("propertyId") propertyId: Int,
        @Query("serviceId")  serviceId: Int,
        @Query("pageNo")     pageNo: Int? = null,
        @Query("pageSize")   pageSize: Int? = null,
    ): ApiResponse<List<Any>>

    @GET("v1/services/{id}/subcategories")
    suspend fun getSubcategories(@Path("id") serviceId: Int): ApiResponse<List<Any>>

    @POST("v1/cost-estimate")
    suspend fun getCostEstimate(@Body body: CostEstimateRequest): ApiResponse<CostEstimate>

    @GET("v1/cleaning-extras/{serviceId}")
    suspend fun getCleaningExtras(@Path("serviceId") serviceId: Int): ApiResponse<List<Any>>

    @POST("v1/available-cleaners")
    suspend fun getAvailableCleaners(@Body body: AvailableCleanersRequest): ApiResponse<List<Cleaner>>

    @GET("v1/coupons")
    suspend fun getCoupons(): ApiResponse<List<Coupon>>

    @GET("v1/cleaners")
    suspend fun listCleaners(
        @Query("status") status: String?,
        @Query("search") search: String?,
    ): ApiResponse<List<Cleaner>>

    @GET("v1/cleaners/{id}")
    suspend fun getCleaner(@Path("id") cleanerId: Int): ApiResponse<Cleaner>
}

class OtherApi(retrofit: Retrofit) {
    private val service = retrofit.create(OtherService::class.java)

    suspend fun getServices() = wrap { service.getServices() }

    suspend fun getPlans(propertyId: Int, subcatId: Int? = null) =
        wrap { service.getPlans(propertyId, subcatId) }

    suspend fun getRecommendedHours(
        propertyId: Int,
        roomCount: Int,
        bathroomCount: Int,
        subcatId: Int? = null,
    ) = wrap { service.getRecommendedHours(propertyId, roomCount, bathroomCount, subcatId) }

    suspend fun getTasks(propertyId: Int, serviceId: Int, pageNo: Int? = null, pageSize: Int? = null) =
        wrap { service.getTasks(propertyId, serviceId, pageNo, pageSize) }

    suspend fun getSubcategories(serviceId: Int) = wrap { service.getSubcategories(serviceId) }

    suspend fun getCostEstimate(request: CostEstimateRequest) =
        wrap { service.getCostEstimate(request) }

    suspend fun getCleaningExtras(serviceId: Int) = wrap { service.getCleaningExtras(serviceId) }

    suspend fun getAvailableCleaners(request: AvailableCleanersRequest) =
        wrap { service.getAvailableCleaners(request) }

    suspend fun getCoupons() = wrap { service.getCoupons() }

    suspend fun listCleaners(status: String? = null, search: String? = null) =
        wrap { service.listCleaners(status, search) }

    suspend fun getCleaner(cleanerId: Int) = wrap { service.getCleaner(cleanerId) }
}
