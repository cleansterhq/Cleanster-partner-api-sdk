package com.cleanster.android.api

import com.cleanster.android.CleansterError
import com.cleanster.android.model.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.IOException

internal interface UsersService {
    @POST("v1/user/account")
    suspend fun createUser(@Body body: CreateUserRequest): ApiResponse<User>

    @GET("v1/user/access-token/{userId}")
    suspend fun fetchAccessToken(@Path("userId") userId: Int): ApiResponse<User>

    @POST("v1/user/verify-jwt")
    suspend fun verifyJwt(@Body body: VerifyJwtRequest): ApiResponse<Any>
}

class UsersApi(retrofit: Retrofit) {
    private val service = retrofit.create(UsersService::class.java)

    suspend fun createUser(
        email: String,
        firstName: String,
        lastName: String,
        phone: String? = null,
    ): ApiResponse<User> = wrap {
        service.createUser(CreateUserRequest(email, firstName, lastName, phone))
    }

    suspend fun fetchAccessToken(userId: Int): ApiResponse<User> = wrap {
        service.fetchAccessToken(userId)
    }

    suspend fun verifyJwt(token: String): ApiResponse<Any> = wrap {
        service.verifyJwt(VerifyJwtRequest(token))
    }
}

internal suspend fun <T> wrap(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        if (e.code() == 401) throw CleansterError.Unauthorized()
        throw CleansterError.ApiError(e.code(), e.message())
    } catch (e: IOException) {
        throw CleansterError.NetworkError(e.message ?: "Unknown network error")
    }
}
