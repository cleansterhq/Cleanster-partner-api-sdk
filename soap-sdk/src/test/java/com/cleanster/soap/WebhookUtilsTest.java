package com.cleanster.soap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookUtilsTest {

    private static final String SECRET   = "test-secret-key";
    private static final String PAYLOAD  = "{\"event\":\"BOOKING_CREATED\",\"bookingId\":42}";
    private static final String EXPECTED = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364";

    @Test
    @DisplayName("computeSignature returns correct HMAC-SHA256 hex")
    void computeSignature_returnsCorrectHex() {
        assertEquals(EXPECTED, WebhookUtils.computeSignature(SECRET, PAYLOAD));
    }

    @Test
    @DisplayName("verifySignature returns true for a valid signature")
    void verifySignature_validSignatureReturnsTrue() {
        assertTrue(WebhookUtils.verifySignature(SECRET, PAYLOAD, EXPECTED));
    }

    @Test
    @DisplayName("verifySignature returns false for a wrong signature string")
    void verifySignature_wrongSignatureReturnsFalse() {
        assertFalse(WebhookUtils.verifySignature(SECRET, PAYLOAD, "badsignature"));
    }

    @Test
    @DisplayName("verifySignature returns false when secret does not match")
    void verifySignature_wrongSecretReturnsFalse() {
        String wrongSig = WebhookUtils.computeSignature("wrong-secret", PAYLOAD);
        assertFalse(WebhookUtils.verifySignature(SECRET, PAYLOAD, wrongSig));
    }
}
