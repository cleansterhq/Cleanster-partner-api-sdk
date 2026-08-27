package com.cleanster.sdk.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility methods for verifying Cleanster webhook delivery signatures.
 *
 * <p>When Cleanster sends a webhook, it computes an HMAC-SHA256 of the raw request
 * body using the {@code secret} returned when the webhook was created, and includes
 * the hex-encoded digest in the {@code X-Webhook-Signature} header.</p>
 *
 * <pre>{@code
 * String secret  = createdWebhook.getSecret();
 * String payload = readRawBody(request);
 * String header  = request.getHeader("X-Webhook-Signature");
 *
 * if (!WebhookUtils.verifySignature(secret, payload, header)) {
 *     throw new SecurityException("Invalid webhook signature");
 * }
 * }</pre>
 */
public final class WebhookUtils {

    private WebhookUtils() {}

    /**
     * Compute the HMAC-SHA256 of {@code payload} keyed with {@code secret}.
     *
     * @param secret  Webhook secret returned by the Cleanster API on creation.
     * @param payload Raw request body as a UTF-8 string.
     * @return Lowercase hex-encoded HMAC-SHA256 digest.
     */
    public static String computeSignature(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] out = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 computation failed", e);
        }
    }

    /**
     * Verify that {@code signature} matches the expected HMAC-SHA256 of {@code payload}.
     * Uses constant-time comparison to prevent timing attacks.
     *
     * @param secret    Webhook secret returned by the Cleanster API.
     * @param payload   Raw request body as a UTF-8 string.
     * @param signature Value from the {@code X-Webhook-Signature} header.
     * @return {@code true} if the signature is valid.
     */
    public static boolean verifySignature(String secret, String payload, String signature) {
        if (secret == null || payload == null || signature == null) return false;
        String expected = computeSignature(secret, payload);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8));
    }
}
