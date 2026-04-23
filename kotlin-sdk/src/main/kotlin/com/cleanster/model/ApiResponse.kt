package com.cleanster.model

/**
 * Standard response envelope returned by every Cleanster API endpoint.
 *
 * @param T The type of the `data` payload.
 * @property status HTTP-style status code (200, 400, 401, 404, 500).
 * @property message Human-readable status description.
 * @property data The response payload; `null` on errors.
 */
data class ApiResponse<T>(
    val status:  Int,
    val message: String,
    val data:    T?
)
