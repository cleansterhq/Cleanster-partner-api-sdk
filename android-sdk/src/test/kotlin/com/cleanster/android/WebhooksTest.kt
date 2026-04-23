package com.cleanster.android

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class WebhooksTest {
    private lateinit var server: MockWebServer
    private lateinit var client: CleansterClient

    @Before fun setUp() {
        server = MockWebServer()
        server.start()
        client = CleansterClient.custom("test-key", server.url("/").toString())
        client.setToken("test-token")
    }

    @After fun tearDown() { server.shutdown() }

    private fun enqueue(body: String, code: Int = 200) =
        server.enqueue(MockResponse().setBody(body).setResponseCode(code)
            .addHeader("Content-Type", "application/json"))

    private val hookJson = """{"id":1,"url":"https://api.yourapp.com/hooks","event":"booking.completed"}"""

    @Test fun `listWebhooks returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[$hookJson]}""")
        val resp = client.webhooks.listWebhooks()
        assertEquals(200, resp.status)
        assertEquals(1, resp.data?.size)
    }

    @Test fun `listWebhooks sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.webhooks.listWebhooks()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/webhooks"))
    }

    @Test fun `createWebhook sends POST with url and event`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$hookJson}""")
        client.webhooks.createWebhook(
            url   = "https://api.yourapp.com/hooks",
            event = "booking.completed",
        )
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        val body = req.body.readUtf8()
        assert(body.contains("booking.completed"))
        assert(body.contains("yourapp"))
    }

    @Test fun `createWebhook returns created webhook`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$hookJson}""")
        val resp = client.webhooks.createWebhook("https://h.io", "booking.created")
        assertEquals(1, resp.data?.id)
        assertEquals("booking.completed", resp.data?.event)
    }

    @Test fun `createWebhook for each event type`() = runTest {
        val events = listOf(
            "booking.created", "booking.cleaner_assigned", "booking.cleaner_removed",
            "booking.rescheduled", "booking.started", "booking.completed",
            "booking.cancelled", "booking.feedback_submitted",
        )
        for (event in events) {
            enqueue("""{"status":200,"message":"OK","data":{"id":1,"url":"https://h.io","event":"$event"}}""")
            val resp = client.webhooks.createWebhook("https://h.io", event)
            assertEquals(event, resp.data?.event)
        }
    }

    @Test fun `updateWebhook sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$hookJson}""")
        client.webhooks.updateWebhook(1, "https://api.yourapp.com/v2/hooks", "booking.cancelled")
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/webhooks/1"))
    }

    @Test fun `updateWebhook body contains new url and event`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$hookJson}""")
        client.webhooks.updateWebhook(1, "https://new-url.io/hook", "booking.started")
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("new-url"))
        assert(body.contains("booking.started"))
    }

    @Test fun `deleteWebhook sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.webhooks.deleteWebhook(1)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/webhooks/1"))
    }

    @Test fun `deleteWebhook returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.webhooks.deleteWebhook(1)
        assertEquals(200, resp.status)
    }

    @Test fun `listWebhooks empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.webhooks.listWebhooks()
        assertEquals(0, resp.data?.size)
    }

    @Test fun `listWebhooks returns event field`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[$hookJson]}""")
        val resp = client.webhooks.listWebhooks()
        assertEquals("booking.completed", resp.data?.first()?.event)
    }
}
