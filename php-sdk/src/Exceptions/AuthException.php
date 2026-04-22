<?php

declare(strict_types=1);

namespace Cleanster\Exceptions;

/**
 * Thrown when the API responds with HTTP 401 Unauthorized.
 *
 * This indicates an invalid or missing partner access key or user bearer token.
 * Inspect $responseBody for the raw API error message.
 */
class AuthException extends CleansterException
{
    /**
     * @param int    $statusCode   Always 401.
     * @param string $responseBody Raw response body from the API.
     */
    public function __construct(
        public readonly int    $statusCode,
        public readonly string $responseBody,
        string                 $message = 'Authentication failed (HTTP 401).',
    ) {
        parent::__construct($message, $statusCode);
    }
}
