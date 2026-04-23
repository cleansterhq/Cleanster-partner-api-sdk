package com.cleanster.android

sealed class CleansterError(message: String) : Exception(message) {
    class Unauthorized(message: String = "Unauthorized — check access-key and token") :
        CleansterError(message)

    class ApiError(val statusCode: Int, message: String) :
        CleansterError("API error $statusCode: $message")

    class NetworkError(message: String) :
        CleansterError("Network error: $message")

    class DecodingError(message: String) :
        CleansterError("Decoding error: $message")
}
