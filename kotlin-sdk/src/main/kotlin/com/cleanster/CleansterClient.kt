package com.cleanster

import com.cleanster.api.*
import com.cleanster.model.ApiResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Main entry point for the Cleanster Partner API SDK.
 *
 * ### Sandbox (development)
 * ```kotlin
 * val client = CleansterClient.sandbox("your-access-key")
 * client.setToken("user-jwt")
 * ```
 *
 * ### Production (go-live)
 * ```kotlin
 * val client = CleansterClient.production("your-access-key")
 * ```
 */
class CleansterClient private constructor(
    private val accessKey: String,
    @Volatile private var token: String = "",
    private val baseUrl: String,
    private val engine: HttpEngine = OkHttpEngine(),
) {
    companion object {
        const val SANDBOX_URL    = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public"
        const val PRODUCTION_URL = "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public"

        /** Create a client targeting the **sandbox** environment. */
        fun sandbox(accessKey: String, engine: HttpEngine = OkHttpEngine()) =
            CleansterClient(accessKey = accessKey, baseUrl = SANDBOX_URL, engine = engine)

        /** Create a client targeting the **production** environment. */
        fun production(accessKey: String, engine: HttpEngine = OkHttpEngine()) =
            CleansterClient(accessKey = accessKey, baseUrl = PRODUCTION_URL, engine = engine)

        /** Internal factory — used in tests to inject a mock engine. */
        internal fun withEngine(accessKey: String, baseUrl: String, engine: HttpEngine) =
            CleansterClient(accessKey = accessKey, baseUrl = baseUrl, engine = engine)
    }

    private val gson = Gson()

    // ── API service namespaces ─────────────────────────────────────────────────

    val bookings:       BookingsApi       by lazy { BookingsApi(this) }
    val properties:     PropertiesApi     by lazy { PropertiesApi(this) }
    val users:          UsersApi          by lazy { UsersApi(this) }
    val checklists:     ChecklistsApi     by lazy { ChecklistsApi(this) }
    val paymentMethods: PaymentMethodsApi by lazy { PaymentMethodsApi(this) }
    val webhooks:       WebhooksApi       by lazy { WebhooksApi(this) }
    val blacklist:      BlacklistApi      by lazy { BlacklistApi(this) }
    val other:          OtherApi          by lazy { OtherApi(this) }

    // ── Token management ──────────────────────────────────────────────────────

    /** Set the per-user JWT. Call after `users.fetchAccessToken(userId)`. */
    fun setToken(token: String) { this.token = token }

    /** Return current token (package-private). */
    internal fun getToken(): String = token

    // ── Core HTTP execution ───────────────────────────────────────────────────

    internal suspend inline fun <reified T> request(
        method:      String,
        path:        String,
        queryParams: Map<String, String> = emptyMap(),
        body:        Any?               = null,
    ): ApiResponse<T> {
        val url = buildUrl(path, queryParams)
        val requestBody = body?.let { gson.toJson(it) }

        val httpRequest = HttpRequest(
            method  = method,
            url     = url,
            headers = mapOf(
                "access-key"   to accessKey,
                "token"        to token,
                "Content-Type" to "application/json",
                "Accept"       to "application/json",
            ),
            body = requestBody,
        )

        val response = try {
            engine.execute(httpRequest)
        } catch (e: Exception) {
            throw CleansterError.NetworkError(e.message ?: "Network failure", e)
        }

        val type: Type = TypeToken.getParameterized(ApiResponse::class.java, T::class.java).type
        val parsed: ApiResponse<T> = try {
            gson.fromJson(response.body, type)
        } catch (e: JsonSyntaxException) {
            throw CleansterError.DecodingError("Failed to parse response: ${e.message}", e)
        }

        when (response.statusCode) {
            401  -> throw CleansterError.Unauthorized()
            404  -> throw CleansterError.NotFound()
            in 400..599 -> throw CleansterError.ApiError(response.statusCode, parsed.message)
        }

        return parsed
    }

    /** Upload a checklist image via multipart/form-data. */
    internal suspend fun requestMultipart(path: String, imageData: ByteArray, fileName: String): ApiResponse<Map<String, Any>> {
        val url = buildUrl(path, emptyMap())
        val headers = mapOf(
            "access-key" to accessKey,
            "token"      to token,
            "Accept"     to "application/json",
        )
        val response = try {
            engine.executeMultipart(url, headers, imageData, fileName)
        } catch (e: Exception) {
            throw CleansterError.NetworkError(e.message ?: "Network failure", e)
        }

        val type: com.google.gson.reflect.TypeToken<ApiResponse<Map<String, Any>>> = object : com.google.gson.reflect.TypeToken<ApiResponse<Map<String, Any>>>() {}
        val parsed: ApiResponse<Map<String, Any>> = try {
            gson.fromJson(response.body, type.type)
        } catch (e: com.google.gson.JsonSyntaxException) {
            throw CleansterError.DecodingError("Failed to parse response: ${e.message}", e)
        }

        when (response.statusCode) {
            401  -> throw CleansterError.Unauthorized()
            404  -> throw CleansterError.NotFound()
            in 400..599 -> throw CleansterError.ApiError(response.statusCode, parsed.message)
        }

        return parsed
    }

    private fun buildUrl(path: String, queryParams: Map<String, String>): String {
        val base = baseUrl + path
        if (queryParams.isEmpty()) return base
        val query = queryParams.entries.joinToString("&") { (k, v) ->
            "${java.net.URLEncoder.encode(k, "UTF-8")}=${java.net.URLEncoder.encode(v, "UTF-8")}"
        }
        return "$base?$query"
    }
}
