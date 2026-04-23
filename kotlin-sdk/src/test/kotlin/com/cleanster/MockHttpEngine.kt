package com.cleanster

import com.google.gson.Gson

/** Configurable mock for [HttpEngine] used in all unit tests. */
class MockHttpEngine : HttpEngine {
    var responseBody: String = "{}"
    var responseStatusCode: Int = 200
    var capturedRequest: HttpRequest? = null

    private val gson = Gson()

    override suspend fun execute(request: HttpRequest): HttpResponse {
        capturedRequest = request
        return HttpResponse(statusCode = responseStatusCode, body = responseBody)
    }

    var capturedMultipartUrl: String?   = null
    var capturedMultipartFile: String?  = null

    override suspend fun executeMultipart(
        url:       String,
        headers:   Map<String, String>,
        imageData: ByteArray,
        fileName:  String,
    ): HttpResponse {
        capturedMultipartUrl  = url
        capturedMultipartFile = fileName
        return HttpResponse(statusCode = responseStatusCode, body = responseBody)
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** Configure a success envelope wrapping [payload]. */
    fun succeed(payload: Map<String, Any?>, status: Int = 200) {
        val envelope = mapOf("status" to status, "message" to "OK", "data" to payload)
        responseBody       = gson.toJson(envelope)
        responseStatusCode = status
    }

    /** Configure a success envelope wrapping an array. */
    fun succeedList(payload: List<Any> = emptyList(), status: Int = 200) {
        val envelope = mapOf("status" to status, "message" to "OK", "data" to payload)
        responseBody       = gson.toJson(envelope)
        responseStatusCode = status
    }

    /** Configure a success envelope with no data. */
    fun succeedEmpty(status: Int = 200) {
        val envelope = mapOf("status" to status, "message" to "OK")
        responseBody       = gson.toJson(envelope)
        responseStatusCode = status
    }

    /** Configure an error response. */
    fun fail(statusCode: Int, message: String = "Error") {
        val envelope       = mapOf("status" to statusCode, "message" to message)
        responseBody       = gson.toJson(envelope)
        responseStatusCode = statusCode
    }

    // ── Inspection helpers ─────────────────────────────────────────────────────

    val capturedMethod: String? get() = capturedRequest?.method
    val capturedUrl:    String? get() = capturedRequest?.url
    val capturedBody:   Map<*, *>?
        get() = capturedRequest?.body?.let { gson.fromJson(it, Map::class.java) }
    val capturedHeaders: Map<String, String>? get() = capturedRequest?.headers
}

// ── Test client factory ────────────────────────────────────────────────────────

internal fun testClient(mock: MockHttpEngine): CleansterClient =
    CleansterClient.withEngine(
        accessKey = "test-key",
        baseUrl   = CleansterClient.SANDBOX_URL,
        engine    = mock,
    )
