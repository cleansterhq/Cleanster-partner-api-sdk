/**
 * Utility functions for verifying Cleanster webhook delivery signatures.
 *
 * When Cleanster sends a webhook, it computes an HMAC-SHA256 of the raw request
 * body using the `secret` returned when the webhook was created, and includes
 * the hex-encoded digest in the `X-Webhook-Signature` header.
 *
 * @example
 * ```typescript
 * import { verifyWebhookSignature } from "cleanster";
 *
 * // Express handler
 * app.post("/webhook", express.raw({ type: "application/json" }), (req, res) => {
 *   const secret    = process.env.CLEANSTER_WEBHOOK_SECRET!;
 *   const payload   = req.body.toString("utf8");
 *   const signature = req.headers["x-webhook-signature"] as string;
 *
 *   if (!verifyWebhookSignature(secret, payload, signature)) {
 *     return res.status(401).json({ error: "Invalid signature" });
 *   }
 *   // process event...
 *   res.sendStatus(200);
 * });
 * ```
 */

import { createHmac, timingSafeEqual } from "crypto";

/**
 * Compute the HMAC-SHA256 of `payload` keyed with `secret`.
 *
 * @param secret  Webhook secret returned by the Cleanster API on creation.
 * @param payload Raw request body as a UTF-8 string.
 * @returns Lowercase hex-encoded HMAC-SHA256 digest.
 */
export function computeWebhookSignature(secret: string, payload: string): string {
  return createHmac("sha256", secret).update(payload, "utf8").digest("hex");
}

/**
 * Verify that `signature` matches the expected HMAC-SHA256 of `payload`.
 * Uses a constant-time comparison to prevent timing attacks.
 *
 * @param secret    Webhook secret returned by the Cleanster API.
 * @param payload   Raw request body as a UTF-8 string.
 * @param signature Value from the `X-Webhook-Signature` header.
 * @returns `true` if the signature is valid, `false` otherwise.
 */
export function verifyWebhookSignature(secret: string, payload: string, signature: string): boolean {
  if (!secret || !payload || !signature) return false;
  const expected = Buffer.from(computeWebhookSignature(secret, payload), "utf8");
  const actual   = Buffer.from(signature, "utf8");
  if (expected.length !== actual.length) return false;
  return timingSafeEqual(expected, actual);
}
