package com.cleanster

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Utility functions for verifying Cleanster webhook delivery signatures.
 *
 * When Cleanster sends a webhook it computes an HMAC-SHA256 of the raw request
 * body using the `secret` returned when the webhook was created, and includes
 * the hex-encoded digest in the `X-Cleanster-Signature` header.
 *
 * ```kotlin
 * val secret    = System.getenv("CLEANSTER_WEBHOOK_SECRET")
 * val payload   = request.body().string()
 * val signature = request.header("X-Cleanster-Signature") ?: ""
 *
 * if (!WebhookUtils.verifySignature(secret, payload, signature)) {
 *     respond(401, "Invalid signature")
 *     return
 * }
 * ```
 */
object WebhookUtils {

    /**
     * Compute the HMAC-SHA256 of [payload] keyed with [secret].
     *
     * @param secret  Webhook secret returned by the Cleanster API on creation.
     * @param payload Raw request body as a UTF-8 string.
     * @return Lowercase hex-encoded HMAC-SHA256 digest.
     */
    fun computeSignature(secret: String, payload: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * Verify that [signature] matches the expected HMAC-SHA256 of [payload].
     * Uses [MessageDigest.isEqual] for constant-time comparison to prevent timing attacks.
     *
     * @param secret    Webhook secret returned by the Cleanster API.
     * @param payload   Raw request body as a UTF-8 string.
     * @param signature Value from the `X-Cleanster-Signature` header.
     * @return `true` if the signature is valid.
     */
    fun verifySignature(secret: String, payload: String, signature: String): Boolean {
        if (secret.isEmpty() || payload.isEmpty() || signature.isEmpty()) return false
        val expected = computeSignature(secret, payload)
        return MessageDigest.isEqual(
            expected.toByteArray(StandardCharsets.UTF_8),
            signature.toByteArray(StandardCharsets.UTF_8),
        )
    }
}
