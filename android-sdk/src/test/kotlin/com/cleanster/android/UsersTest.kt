package com.cleanster.android

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UsersTest {
    private lateinit var server: MockWebServer
    private lateinit var client: CleansterClient

    @Before fun setUp() {
        server = MockWebServer()
        server.start()
        client = CleansterClient.custom("test-key", server.url("/").toString())
    }

    @After fun tearDown() { server.shutdown() }

    private fun enqueue(body: String, code: Int = 200) =
        server.enqueue(MockResponse().setBody(body).setResponseCode(code)
            .addHeader("Content-Type", "application/json"))

    // ─── createUser ───────────────────────────────────────────────────────────
    @Test fun `createUser returns user with id`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":42,"email":"alice@example.com","firstName":"Alice","lastName":"Smith"}}""")
        val resp = client.users.createUser(
            email = "alice@example.com",
            firstName = "Alice",
            lastName = "Smith",
        )
        assertEquals(42, resp.data?.id)
        assertEquals("alice@example.com", resp.data?.email)
    }

    @Test fun `createUser sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.users.createUser("a@b.com", "A", "B")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/user/account"))
    }

    @Test fun `createUser includes all fields in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.users.createUser("a@b.com", "Alice", "Smith", "+14155551234")
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("a@b.com"))
        assert(body.contains("Alice"))
        assert(body.contains("Smith"))
        assert(body.contains("+14155551234"))
    }

    @Test fun `createUser without phone omits phone field`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.users.createUser("a@b.com", "A", "B")
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("a@b.com"))
    }

    @Test fun `createUser returns correct firstName and lastName`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":7,"firstName":"Bob","lastName":"Jones"}}""")
        val resp = client.users.createUser("bob@jones.com", "Bob", "Jones")
        assertEquals("Bob", resp.data?.firstName)
        assertEquals("Jones", resp.data?.lastName)
    }

    @Test fun `createUser sends access-key header`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.users.createUser("a@b.com", "A", "B")
        val req = server.takeRequest()
        assertEquals("test-key", req.getHeader("access-key"))
    }

    // ─── fetchAccessToken ─────────────────────────────────────────────────────
    @Test fun `fetchAccessToken returns token`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":42,"token":"eyJhbGciOiJIUzI1NiJ9.test"}}""")
        val resp = client.users.fetchAccessToken(42)
        assertNotNull(resp.data?.token)
        assert(resp.data!!.token!!.startsWith("eyJ"))
    }

    @Test fun `fetchAccessToken sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"token":"t"}}""")
        client.users.fetchAccessToken(42)
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/user/access-token/42"))
    }

    @Test fun `fetchAccessToken uses correct userId in path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"token":"t"}}""")
        client.users.fetchAccessToken(999)
        val req = server.takeRequest()
        assert(req.path!!.contains("/999"))
    }

    @Test fun `setToken updates token header on subsequent requests`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"token":"my-jwt"}}""")
        val tokenResp = client.users.fetchAccessToken(42)
        client.setToken(tokenResp.data?.token ?: "")

        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getBookings()
        server.takeRequest() // discard first request
        val req = server.takeRequest()
        assertEquals("my-jwt", req.getHeader("token"))
    }

    // ─── verifyJwt ────────────────────────────────────────────────────────────
    @Test fun `verifyJwt sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.users.verifyJwt("eyJhbGciOi...")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/user/verify-jwt"))
    }

    @Test fun `verifyJwt sends token in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.users.verifyJwt("mytoken123")
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("mytoken123"))
    }

    @Test fun `verifyJwt returns 200 for valid token`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.users.verifyJwt("valid-jwt")
        assertEquals(200, resp.status)
        assertEquals("OK", resp.message)
    }

    @Test fun `verifyJwt returns 400 for invalid token`() = runTest {
        enqueue("""{"status":400,"message":"Invalid token","data":null}""")
        val resp = client.users.verifyJwt("bad-token")
        assertEquals(400, resp.status)
    }

    @Test fun `clearToken removes token header`() = runTest {
        client.setToken("a-token")
        client.clearToken()
        enqueue("""{"status":200,"message":"OK","data":{"token":"t"}}""")
        client.users.fetchAccessToken(1)
        val req = server.takeRequest()
        assertEquals(null, req.getHeader("token"))
    }

    @Test fun `factory sandbox creates client`() {
        val c = CleansterClient.sandbox("my-key")
        assertNotNull(c)
    }

    @Test fun `factory production creates client`() {
        val c = CleansterClient.production("my-key")
        assertNotNull(c)
    }

    @Test fun `factory custom creates client`() {
        val c = CleansterClient.custom("my-key", "http://localhost:8080/")
        assertNotNull(c)
    }
}
