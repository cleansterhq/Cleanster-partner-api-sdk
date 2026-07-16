"""Unit tests for WebhookUtils HMAC-SHA256 signing helpers."""

import unittest

from cleanster.webhook_utils import compute_webhook_signature, verify_webhook_signature

SECRET   = "test-secret-key"
PAYLOAD  = '{"event":"BOOKING_CREATED","bookingId":42}'
EXPECTED = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364"


class TestWebhookUtils(unittest.TestCase):

    def test_compute_signature_returns_correct_hex(self):
        self.assertEqual(EXPECTED, compute_webhook_signature(SECRET, PAYLOAD))

    def test_verify_signature_valid_returns_true(self):
        self.assertTrue(verify_webhook_signature(SECRET, PAYLOAD, EXPECTED))

    def test_verify_signature_wrong_signature_returns_false(self):
        self.assertFalse(verify_webhook_signature(SECRET, PAYLOAD, "badsignature"))

    def test_verify_signature_wrong_secret_returns_false(self):
        wrong_sig = compute_webhook_signature("wrong-secret", PAYLOAD)
        self.assertFalse(verify_webhook_signature(SECRET, PAYLOAD, wrong_sig))


if __name__ == "__main__":
    unittest.main()
