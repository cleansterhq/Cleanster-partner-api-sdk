package com.cleanster.android

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebhookUtilsTest {

    private val secret   = "test-secret-key"
    private val payload  = "{\"event\":\"BOOKING_CREATED\",\"bookingId\":42}"
    private val expected = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364"

    @Test fun `computeSignature returns correct hex`() {
        assertEquals(expected, WebhookUtils.computeSignature(secret, payload))
    }

    @Test fun `verifySignature valid signature returns true`() {
        assertTrue(WebhookUtils.verifySignature(secret, payload, expected))
    }

    @Test fun `verifySignature wrong signature returns false`() {
        assertFalse(WebhookUtils.verifySignature(secret, payload, "badsignature"))
    }

    @Test fun `verifySignature wrong secret returns false`() {
        val wrongSig = WebhookUtils.computeSignature("wrong-secret", payload)
        assertFalse(WebhookUtils.verifySignature(secret, payload, wrongSig))
    }
}
