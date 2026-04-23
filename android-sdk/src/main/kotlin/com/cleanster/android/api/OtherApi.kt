package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface OtherService {
    @GET("v1/services")
    suspend fun getServices(): ApiResponse<List<Any>>

    @GET("v1/plans")
    suspend fun getPlans(@Query("propertyId") propertyId: Int): ApiResponse<List<Any>>

    @GET("v1/recommended-hours")
    suspend fun getRecommendedHours(
        @Query("propertyId")    propertyId: Int,
        @Query("roomCount")     roomCount: Int,
        @Query("bathroomCount") bathroomCount: Int,
    ): ApiResponse<Any>

    @POST("v1/cost-estimate")
    suspend fun getCostEstimate(@Body body: CostEstimateRequest): ApiResponse<CostEstimate>

    @GET("v1/cleaning-extras/{serviceId}")
    suspend fun getCleaningExtras(@Path("serviceId") serviceId: Int): ApiResponse<List<Any>>

    @POST("v1/available-cleaners")
    suspend fun getAvailableCleaners(@Body body: AvailableCleanersRequest): ApiResponse<List<Cleaner>>

    @GET("v1/coupons")
    suspend fun getCoupons(): ApiResponse<List<Coupon>>
}

class OtherApi(retrofit: Retrofit) {
    private val service = retrofit.create(OtherService::class.java)

    suspend fun getServices() = wrap { service.getServices() }

    suspend fun getPlans(propertyId: Int) = wrap { service.getPlans(propertyId) }

    suspend fun getRecommendedHours(
        propertyId: Int,
        roomCount: Int,
        bathroomCount: Int,
    ) = wrap { service.getRecommendedHours(propertyId, roomCount, bathroomCount) }

    suspend fun getCostEstimate(request: CostEstimateRequest) =
        wrap { service.getCostEstimate(request) }

    suspend fun getCleaningExtras(serviceId: Int) = wrap { service.getCleaningExtras(serviceId) }

    suspend fun getAvailableCleaners(request: AvailableCleanersRequest) =
        wrap { service.getAvailableCleaners(request) }

    suspend fun getCoupons() = wrap { service.getCoupons() }
}
