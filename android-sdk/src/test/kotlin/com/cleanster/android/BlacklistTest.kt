package com.cleanster.android

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BlacklistTest {
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

    @Test fun `listBlacklistedCleaners returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"cleanerId":789,"reason":"Late"},{"cleanerId":790}]}""")
        val resp = client.blacklist.listBlacklistedCleaners()
        assertEquals(200, resp.status)
        assertEquals(2, resp.data?.size)
    }

    @Test fun `listBlacklistedCleaners sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.blacklist.listBlacklistedCleaners()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/blacklist/cleaner"))
    }

    @Test fun `listBlacklistedCleaners empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.blacklist.listBlacklistedCleaners()
        assertEquals(0, resp.data?.size)
    }

    @Test fun `addToBlacklist sends POST with cleanerId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.blacklist.addToBlacklist(cleanerId = 789, reason = "Late")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        val body = req.body.readUtf8()
        assert(body.contains("789"))
        assert(body.contains("Late"))
    }

    @Test fun `addToBlacklist without reason`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.blacklist.addToBlacklist(789)
        assertEquals(200, resp.status)
    }

    @Test fun `addToBlacklist hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.blacklist.addToBlacklist(789)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/blacklist/cleaner"))
    }

    @Test fun `removeFromBlacklist sends DELETE with body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.blacklist.removeFromBlacklist(789)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.body.readUtf8().contains("789"))
    }

    @Test fun `removeFromBlacklist returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.blacklist.removeFromBlacklist(789)
        assertEquals(200, resp.status)
    }

    @Test fun `listBlacklistedCleaners returns first entry cleanerId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"cleanerId":789}]}""")
        val resp = client.blacklist.listBlacklistedCleaners()
        assertEquals(789, resp.data?.first()?.cleanerId)
    }
}
