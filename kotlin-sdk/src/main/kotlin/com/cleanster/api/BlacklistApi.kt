package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for blocking specific cleaners from being assigned to bookings. */
class BlacklistApi internal constructor(private val client: CleansterClient) {

    /** List all blacklisted cleaners. */
    suspend fun listBlacklistedCleaners(): ApiResponse<List<Any>> = client.request(
        method = "GET",
        path   = "/v1/blacklist/cleaner",
    )

    /**
     * Add a cleaner to the blacklist.
     *
     * @param cleanerId The cleaner's user ID.
     * @param reason    Optional internal note.
     */
    suspend fun addToBlacklist(cleanerId: Int, reason: String? = null): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/blacklist/cleaner",
        body   = BlacklistRequest(cleanerId = cleanerId, reason = reason),
    )

    /** Remove a cleaner from the blacklist. */
    suspend fun removeFromBlacklist(cleanerId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/blacklist/cleaner",
        body   = BlacklistRequest(cleanerId = cleanerId),
    )
}
