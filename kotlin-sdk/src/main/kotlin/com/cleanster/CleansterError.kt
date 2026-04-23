package com.cleanster

/**
 * Sealed class representing all errors thrown by the Cleanster SDK.
 */
sealed class CleansterError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** The API returned an error status code. */
    class ApiError(val statusCode: Int, override val message: String) :
        CleansterError("API error $statusCode: $message")

    /** The `access-key` or `token` header was rejected (HTTP 401). */
    class Unauthorized(override val message: String = "Unauthorized — check your access-key and token.") :
        CleansterError(message)

    /** The requested resource was not found (HTTP 404). */
    class NotFound(override val message: String = "Resource not found.") :
        CleansterError(message)

    /** A network-level error (e.g. no connectivity, timeout). */
    class NetworkError(override val message: String, cause: Throwable? = null) :
        CleansterError(message, cause)

    /** The server response could not be parsed. */
    class DecodingError(override val message: String, cause: Throwable? = null) :
        CleansterError(message, cause)
}
