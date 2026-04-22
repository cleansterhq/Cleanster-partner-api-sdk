<?php

declare(strict_types=1);

namespace Cleanster\Exceptions;

/**
 * Thrown when the API responds with a non-2xx HTTP status (other than 401).
 *
 * Check $statusCode to distinguish client errors (4xx) from server errors (5xx).
 * The raw response body is available via $responseBody.
 */
class ApiException extends CleansterException
{
    /**
     * @param int    $statusCode   HTTP status code (e.g., 404, 422, 500).
     * @param string $responseBody Raw response body from the API.
     * @param string $message      Human-readable description (auto-generated if empty).
     */
    public function __construct(
        public readonly int    $statusCode,
        public readonly string $responseBody,
        string                 $message = '',
    ) {
        parent::__construct($message ?: "API error (HTTP {$statusCode})", $statusCode);
    }
}
