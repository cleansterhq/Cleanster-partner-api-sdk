package com.cleanster.android

import com.cleanster.android.api.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Main entry point for the Cleanster Android SDK (Retrofit).
 *
 * Usage:
 *   val client = CleansterClient.sandbox("your-access-key")
 *   val token  = client.users.fetchAccessToken(userId)
 *   client.setToken(token.data?.token ?: "")
 *   val bookings = client.bookings.getBookings()
 */
class CleansterClient private constructor(
    private val accessKey: String,
    baseUrl: String,
    enableLogging: Boolean = false,
) {
    private var userToken: String? = null

    private val gson: Gson = GsonBuilder().create()

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(accessKey) { userToken })
        .also { builder ->
            if (enableLogging) {
                builder.addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    /** Bookings — schedule, reschedule, cancel, assign cleaners, pay, feedback, chat */
    val bookings = BookingsApi(retrofit)

    /** Users — create accounts and manage JWTs */
    val users = UsersApi(retrofit)

    /** Properties — CRUD, iCal sync, cleaner lists, checklists */
    val properties = PropertiesApi(retrofit)

    /** Checklists — create and manage reusable task lists */
    val checklists = ChecklistsApi(retrofit)

    /** Other — services, plans, cost estimates, extras, available cleaners, coupons */
    val other = OtherApi(retrofit)

    /** Blacklist — block specific cleaners from your properties */
    val blacklist = BlacklistApi(retrofit)

    /** Payment Methods — Stripe and PayPal payment method management */
    val paymentMethods = PaymentMethodsApi(retrofit)

    /** Webhooks — subscribe to booking lifecycle events */
    val webhooks = WebhooksApi(retrofit)

    /** Set the user-scoped JWT token. Call after fetchAccessToken(). */
    fun setToken(token: String) {
        userToken = token
    }

    /** Clear the current user token (e.g. on logout). */
    fun clearToken() {
        userToken = null
    }

    companion object {
        private const val SANDBOX_URL =
            "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public/"
        private const val PRODUCTION_URL =
            "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public/"

        /**
         * Create a sandbox client for development and testing.
         * @param accessKey Your partner access key
         * @param enableLogging Set true to log requests/responses (development only)
         */
        fun sandbox(accessKey: String, enableLogging: Boolean = false) =
            CleansterClient(accessKey, SANDBOX_URL, enableLogging)

        /**
         * Create a production client.
         * @param accessKey Your partner access key
         */
        fun production(accessKey: String) =
            CleansterClient(accessKey, PRODUCTION_URL, enableLogging = false)

        /**
         * Create a client with a custom base URL (used in tests with MockWebServer).
         */
        fun custom(accessKey: String, baseUrl: String, enableLogging: Boolean = false) =
            CleansterClient(accessKey, baseUrl, enableLogging)
    }
}
