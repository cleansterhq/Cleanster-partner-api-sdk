package com.cleanster

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebhooksTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `listWebhooks sends GET`() = runTest {
        mock.succeedList(); client.webhooks.listWebhooks()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `listWebhooks correct path`() = runTest {
        mock.succeedList(); client.webhooks.listWebhooks()
        assertTrue(mock.capturedUrl?.endsWith("/v1/webhooks") == true)
    }

    @Test fun `createWebhook sends POST`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://x.com", "event" to "booking.completed"))
        client.webhooks.createWebhook("https://x.com", "booking.completed")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `createWebhook correct path`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://x.com", "event" to "booking.completed"))
        client.webhooks.createWebhook("https://x.com", "booking.completed")
        assertTrue(mock.capturedUrl?.endsWith("/v1/webhooks") == true)
    }

    @Test fun `createWebhook encodes url`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://myapp.com/hook", "event" to "booking.created"))
        client.webhooks.createWebhook("https://myapp.com/hook", "booking.created")
        assertEquals("https://myapp.com/hook", mock.capturedBody?.get("url"))
    }

    @Test fun `createWebhook encodes event`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://x.com", "event" to "booking.cancelled"))
        client.webhooks.createWebhook("https://x.com", "booking.cancelled")
        assertEquals("booking.cancelled", mock.capturedBody?.get("event"))
    }

    @Test fun `createWebhook decodes id`() = runTest {
        mock.succeed(mapOf("id" to 99.0, "url" to "https://x.com", "event" to "booking.completed"))
        val resp = client.webhooks.createWebhook("https://x.com", "booking.completed")
        assertEquals(99, resp.data?.id)
    }

    @Test fun `updateWebhook sends PUT`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://new.com", "event" to "booking.started"))
        client.webhooks.updateWebhook(1, "https://new.com", "booking.started")
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `updateWebhook correct path`() = runTest {
        mock.succeed(mapOf("id" to 1.0, "url" to "https://new.com", "event" to "booking.started"))
        client.webhooks.updateWebhook(1, "https://new.com", "booking.started")
        assertTrue(mock.capturedUrl?.endsWith("/v1/webhooks/1") == true)
    }

    @Test fun `deleteWebhook sends DELETE`() = runTest {
        mock.succeedEmpty(); client.webhooks.deleteWebhook(1)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `deleteWebhook correct path`() = runTest {
        mock.succeedEmpty(); client.webhooks.deleteWebhook(1)
        assertTrue(mock.capturedUrl?.endsWith("/v1/webhooks/1") == true)
    }
}
