package com.cleanster.sdk;

import com.cleanster.sdk.util.WebhookUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookUtilsTest {

    private static final String SECRET   = "test-secret-key";
    private static final String PAYLOAD  = "{\"event\":\"BOOKING_CREATED\",\"bookingId\":42}";
    private static final String EXPECTED = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364";

    @Test void computeSignature_returnsCorrectHex() {
        assertEquals(EXPECTED, WebhookUtils.computeSignature(SECRET, PAYLOAD));
    }

    @Test void verifySignature_validSignatureReturnsTrue() {
        assertTrue(WebhookUtils.verifySignature(SECRET, PAYLOAD, EXPECTED));
    }

    @Test void verifySignature_wrongSignatureReturnsFalse() {
        assertFalse(WebhookUtils.verifySignature(SECRET, PAYLOAD, "badsignature"));
    }

    @Test void verifySignature_wrongSecretReturnsFalse() {
        String wrongSig = WebhookUtils.computeSignature("wrong-secret", PAYLOAD);
        assertFalse(WebhookUtils.verifySignature(SECRET, PAYLOAD, wrongSig));
    }
}
