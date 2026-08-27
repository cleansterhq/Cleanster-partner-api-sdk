"""Utility functions for verifying Cleanster webhook delivery signatures.

When Cleanster sends a webhook, it computes an HMAC-SHA256 of the raw request
body using the ``secret`` returned when the webhook was created, and includes
the hex-encoded digest in the ``X-Webhook-Signature`` header.

Example::

    from cleanster import verify_webhook_signature

    @app.route("/webhook", methods=["POST"])
    def handle_webhook():
        secret    = os.environ["CLEANSTER_WEBHOOK_SECRET"]
        payload   = request.get_data(as_text=True)
        signature = request.headers.get("X-Webhook-Signature", "")

        if not verify_webhook_signature(secret, payload, signature):
            abort(401)
        # process event...
        return "", 200
"""

import hashlib
import hmac


def compute_webhook_signature(secret: str, payload: str) -> str:
    """Compute the HMAC-SHA256 of *payload* keyed with *secret*.

    Args:
        secret:  Webhook secret returned by the Cleanster API on creation.
        payload: Raw request body as a UTF-8 string.

    Returns:
        Lowercase hex-encoded HMAC-SHA256 digest.
    """
    return hmac.new(
        secret.encode("utf-8"),
        payload.encode("utf-8"),
        hashlib.sha256,
    ).hexdigest()


def verify_webhook_signature(secret: str, payload: str, signature: str) -> bool:
    """Verify that *signature* matches the expected HMAC-SHA256 of *payload*.

    Uses :func:`hmac.compare_digest` for constant-time comparison to prevent
    timing attacks.

    Args:
        secret:    Webhook secret returned by the Cleanster API.
        payload:   Raw request body as a UTF-8 string.
        signature: Value from the ``X-Webhook-Signature`` header.

    Returns:
        ``True`` if the signature is valid, ``False`` otherwise.
    """
    if not secret or not payload or not signature:
        return False
    expected = compute_webhook_signature(secret, payload)
    return hmac.compare_digest(expected, signature)
