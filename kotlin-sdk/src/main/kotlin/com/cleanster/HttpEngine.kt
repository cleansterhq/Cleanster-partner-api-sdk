package com.cleanster

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/** Minimal HTTP request model used internally by the SDK. */
data class HttpRequest(
    val method:  String,
    val url:     String,
    val headers: Map<String, String>,
    val body:    String?,
)

/** Minimal HTTP response model. */
data class HttpResponse(
    val statusCode: Int,
    val body:       String,
)

/** Abstraction over the HTTP transport layer — injectable for testing. */
interface HttpEngine {
    suspend fun execute(request: HttpRequest): HttpResponse

    /** Upload an image as multipart/form-data. Returns a raw HTTP response. */
    suspend fun executeMultipart(
        url:       String,
        headers:   Map<String, String>,
        imageData: ByteArray,
        fileName:  String,
    ): HttpResponse
}

/** Default implementation backed by OkHttp. */
class OkHttpEngine(
    private val okClient: OkHttpClient = OkHttpClient(),
) : HttpEngine {

    private val json = "application/json; charset=utf-8".toMediaType()

    override suspend fun execute(request: HttpRequest): HttpResponse = withContext(Dispatchers.IO) {
        val body = request.body?.toRequestBody(json)

        val okRequest = Request.Builder()
            .url(request.url)
            .method(request.method, body)
            .apply { request.headers.forEach { (k, v) -> addHeader(k, v) } }
            .build()

        val response: Response = okClient.newCall(okRequest).execute()
        HttpResponse(
            statusCode = response.code,
            body       = response.body?.string() ?: "",
        )
    }

    override suspend fun executeMultipart(
        url:       String,
        headers:   Map<String, String>,
        imageData: ByteArray,
        fileName:  String,
    ): HttpResponse = withContext(Dispatchers.IO) {
        val imageBody = imageData.toRequestBody("image/*".toMediaType())
        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", fileName, imageBody)
            .build()

        val okRequest = Request.Builder()
            .url(url)
            .post(multipart)
            .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
            .build()

        val response: Response = okClient.newCall(okRequest).execute()
        HttpResponse(
            statusCode = response.code,
            body       = response.body?.string() ?: "",
        )
    }
}
