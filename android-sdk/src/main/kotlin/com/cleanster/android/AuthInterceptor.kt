package com.cleanster.android

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val accessKey: String,
    private val tokenProvider: () -> String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("access-key", accessKey)
            .also { builder ->
                tokenProvider()?.let { builder.addHeader("token", it) }
            }
            .build()
        return chain.proceed(request)
    }
}
