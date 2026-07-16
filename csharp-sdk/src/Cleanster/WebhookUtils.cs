using System.Security.Cryptography;
using System.Text;

namespace Cleanster;

/// <summary>
/// Utility methods for verifying Cleanster webhook delivery signatures.
/// </summary>
/// <remarks>
/// When Cleanster sends a webhook it computes an HMAC-SHA256 of the raw request
/// body using the <c>secret</c> returned when the webhook was created, and includes
/// the hex-encoded digest in the <c>X-Cleanster-Signature</c> header.
/// </remarks>
/// <example>
/// <code>
/// var secret    = Environment.GetEnvironmentVariable("CLEANSTER_WEBHOOK_SECRET")!;
/// var payload   = await new StreamReader(Request.Body).ReadToEndAsync();
/// var signature = Request.Headers["X-Cleanster-Signature"].ToString();
///
/// if (!WebhookUtils.VerifySignature(secret, payload, signature))
///     return Unauthorized();
/// </code>
/// </example>
public static class WebhookUtils
{
    /// <summary>
    /// Compute the HMAC-SHA256 of <paramref name="payload"/> keyed with <paramref name="secret"/>.
    /// </summary>
    /// <param name="secret">Webhook secret returned by the Cleanster API.</param>
    /// <param name="payload">Raw request body as a UTF-8 string.</param>
    /// <returns>Lowercase hex-encoded HMAC-SHA256 digest.</returns>
    public static string ComputeSignature(string secret, string payload)
    {
        var keyBytes  = Encoding.UTF8.GetBytes(secret);
        var msgBytes  = Encoding.UTF8.GetBytes(payload);
        var hashBytes = HMACSHA256.HashData(keyBytes, msgBytes);
        return Convert.ToHexString(hashBytes).ToLowerInvariant();
    }

    /// <summary>
    /// Verify that <paramref name="signature"/> matches the expected HMAC-SHA256 of
    /// <paramref name="payload"/>. Uses <see cref="CryptographicOperations.FixedTimeEquals"/>
    /// for constant-time comparison to prevent timing attacks.
    /// </summary>
    /// <param name="secret">Webhook secret returned by the Cleanster API.</param>
    /// <param name="payload">Raw request body as a UTF-8 string.</param>
    /// <param name="signature">Value from the <c>X-Cleanster-Signature</c> header.</param>
    /// <returns><c>true</c> if the signature is valid.</returns>
    public static bool VerifySignature(string secret, string payload, string signature)
    {
        if (string.IsNullOrEmpty(secret) || string.IsNullOrEmpty(payload) || string.IsNullOrEmpty(signature))
            return false;

        var expectedBytes = Encoding.UTF8.GetBytes(ComputeSignature(secret, payload));
        var actualBytes   = Encoding.UTF8.GetBytes(signature);

        if (expectedBytes.Length != actualBytes.Length) return false;
        return CryptographicOperations.FixedTimeEquals(expectedBytes, actualBytes);
    }
}
