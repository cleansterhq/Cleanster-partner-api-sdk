package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface BlacklistService {
    @GET("v1/blacklist/cleaner")
    suspend fun listBlacklistedCleaners(): ApiResponse<List<BlacklistEntry>>

    @POST("v1/blacklist/cleaner")
    suspend fun addToBlacklist(@Body body: AddToBlacklistRequest): ApiResponse<Any>

    @HTTP(method = "DELETE", path = "v1/blacklist/cleaner", hasBody = true)
    suspend fun removeFromBlacklist(@Body body: RemoveFromBlacklistRequest): ApiResponse<Any>
}

class BlacklistApi(retrofit: Retrofit) {
    private val service = retrofit.create(BlacklistService::class.java)

    suspend fun listBlacklistedCleaners() = wrap { service.listBlacklistedCleaners() }

    suspend fun addToBlacklist(cleanerId: Int, reason: String? = null) =
        wrap { service.addToBlacklist(AddToBlacklistRequest(cleanerId, reason)) }

    suspend fun removeFromBlacklist(cleanerId: Int) =
        wrap { service.removeFromBlacklist(RemoveFromBlacklistRequest(cleanerId)) }
}
