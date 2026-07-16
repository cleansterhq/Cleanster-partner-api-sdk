<?php

declare(strict_types=1);

namespace Cleanster;

/**
 * Utility methods for verifying Cleanster webhook delivery signatures.
 *
 * When Cleanster sends a webhook it computes an HMAC-SHA256 of the raw request
 * body using the `secret` returned when the webhook was created, and includes
 * the hex-encoded digest in the `X-Cleanster-Signature` header.
 *
 * @example
 * ```php
 * $secret    = getenv('CLEANSTER_WEBHOOK_SECRET');
 * $payload   = file_get_contents('php://input');
 * $signature = $_SERVER['HTTP_X_CLEANSTER_SIGNATURE'] ?? '';
 *
 * if (!WebhookUtils::verifySignature($secret, $payload, $signature)) {
 *     http_response_code(401);
 *     exit;
 * }
 * // process event…
 * ```
 */
final class WebhookUtils
{
    private function __construct() {}

    /**
     * Compute the HMAC-SHA256 of $payload keyed with $secret.
     *
     * @param string $secret  Webhook secret returned by the Cleanster API.
     * @param string $payload Raw request body as a UTF-8 string.
     * @return string Lowercase hex-encoded HMAC-SHA256 digest.
     */
    public static function computeSignature(string $secret, string $payload): string
    {
        return hash_hmac('sha256', $payload, $secret);
    }

    /**
     * Verify that $signature matches the expected HMAC-SHA256 of $payload.
     * Uses hash_equals() for constant-time comparison to prevent timing attacks.
     *
     * @param string $secret    Webhook secret returned by the Cleanster API.
     * @param string $payload   Raw request body as a UTF-8 string.
     * @param string $signature Value from the X-Cleanster-Signature header.
     * @return bool true if the signature is valid.
     */
    public static function verifySignature(string $secret, string $payload, string $signature): bool
    {
        if ($secret === '' || $payload === '' || $signature === '') {
            return false;
        }
        $expected = self::computeSignature($secret, $payload);
        return hash_equals($expected, $signature);
    }
}
