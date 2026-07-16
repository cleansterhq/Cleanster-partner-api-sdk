import CryptoKit
import Foundation

/// Utility functions for verifying Cleanster webhook delivery signatures.
///
/// When Cleanster sends a webhook it computes an HMAC-SHA256 of the raw request
/// body using the `secret` returned when the webhook was created, and includes
/// the hex-encoded digest in the `X-Cleanster-Signature` header.
///
/// ```swift
/// func handleWebhook(_ request: URLRequest) throws {
///     guard
///         let body      = request.httpBody,
///         let payload   = String(data: body, encoding: .utf8),
///         let signature = request.value(forHTTPHeaderField: "X-Cleanster-Signature")
///     else { throw WebhookError.missingData }
///
///     let secret = ProcessInfo.processInfo.environment["CLEANSTER_WEBHOOK_SECRET"]!
///     guard WebhookUtils.verifySignature(secret: secret, payload: payload, signature: signature) else {
///         throw WebhookError.invalidSignature
///     }
///     // process event…
/// }
/// ```
public enum WebhookUtils {

    /// Compute the HMAC-SHA256 of `payload` keyed with `secret`.
    ///
    /// - Parameters:
    ///   - secret:  Webhook secret returned by the Cleanster API on creation.
    ///   - payload: Raw request body as a UTF-8 string.
    /// - Returns: Lowercase hex-encoded HMAC-SHA256 digest.
    public static func computeSignature(secret: String, payload: String) -> String {
        let key  = SymmetricKey(data: Data(secret.utf8))
        let code = HMAC<SHA256>.authenticationCode(for: Data(payload.utf8), using: key)
        return code.map { String(format: "%02x", $0) }.joined()
    }

    /// Verify that `signature` matches the expected HMAC-SHA256 of `payload`.
    /// Uses a constant-time byte comparison to prevent timing attacks.
    ///
    /// - Parameters:
    ///   - secret:    Webhook secret returned by the Cleanster API.
    ///   - payload:   Raw request body as a UTF-8 string.
    ///   - signature: Value from the `X-Cleanster-Signature` header.
    /// - Returns: `true` if the signature is valid.
    public static func verifySignature(secret: String, payload: String, signature: String) -> Bool {
        guard !secret.isEmpty, !payload.isEmpty, !signature.isEmpty else { return false }
        let expected = computeSignature(secret: secret, payload: payload)
        guard expected.count == signature.count else { return false }
        // Constant-time comparison via XOR accumulation
        let a = Array(expected.utf8)
        let b = Array(signature.utf8)
        var diff: UInt8 = 0
        for (x, y) in zip(a, b) { diff |= x ^ y }
        return diff == 0
    }
}
