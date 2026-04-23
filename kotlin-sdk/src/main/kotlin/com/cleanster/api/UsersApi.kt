package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for creating users and managing authentication tokens. */
class UsersApi internal constructor(private val client: CleansterClient) {

    /**
     * Create a new end-user account.
     *
     * Save the returned `data.id` — you will need it to fetch the user's JWT.
     */
    suspend fun createUser(
        email:     String,
        firstName: String,
        lastName:  String,
        phone:     String? = null,
    ): ApiResponse<User> = client.request(
        method = "POST",
        path   = "/v1/user/account",
        body   = CreateUserRequest(email = email, firstName = firstName,
                                   lastName = lastName, phone = phone),
    )

    /**
     * Fetch the long-lived JWT for a user.
     *
     * After calling this, pass the returned token to [CleansterClient.setToken].
     */
    suspend fun fetchAccessToken(userId: Int): ApiResponse<User> = client.request(
        method = "GET",
        path   = "/v1/user/access-token/$userId",
    )

    /**
     * Verify that a JWT is valid and has not expired.
     */
    suspend fun verifyJwt(token: String): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/user/verify-jwt",
        body   = VerifyJwtRequest(token = token),
    )
}
