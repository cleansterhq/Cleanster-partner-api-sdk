package com.cleanster

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `createUser sends POST`() = runTest {
        mock.succeed(mapOf("id" to 1, "email" to "a@b.com"))
        client.users.createUser("a@b.com", "A", "B")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `createUser sends correct path`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "A", "B")
        assertTrue(mock.capturedUrl?.endsWith("/v1/user/account") == true)
    }

    @Test fun `createUser sends access-key header`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "A", "B")
        assertEquals("test-key", mock.capturedHeaders?.get("access-key"))
    }

    @Test fun `createUser encodes email`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("alice@example.com", "Alice", "Smith")
        assertEquals("alice@example.com", mock.capturedBody?.get("email"))
    }

    @Test fun `createUser encodes firstName`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "Alice", "Smith")
        assertEquals("Alice", mock.capturedBody?.get("firstName"))
    }

    @Test fun `createUser encodes lastName`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "Alice", "Smith")
        assertEquals("Smith", mock.capturedBody?.get("lastName"))
    }

    @Test fun `createUser encodes optional phone`() = runTest {
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "A", "B", phone = "+14155551234")
        assertEquals("+14155551234", mock.capturedBody?.get("phone"))
    }

    @Test fun `createUser decodes response id`() = runTest {
        mock.succeed(mapOf("id" to 42.0, "email" to "a@b.com"))
        val resp = client.users.createUser("a@b.com", "A", "B")
        assertEquals(42, resp.data?.id)
    }

    @Test fun `fetchAccessToken sends GET`() = runTest {
        mock.succeed(mapOf("token" to "jwt-abc"))
        client.users.fetchAccessToken(123)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `fetchAccessToken sends correct path`() = runTest {
        mock.succeed(mapOf("token" to "jwt-abc"))
        client.users.fetchAccessToken(123)
        assertTrue(mock.capturedUrl?.endsWith("/v1/user/access-token/123") == true)
    }

    @Test fun `fetchAccessToken decodes token`() = runTest {
        mock.succeed(mapOf("token" to "eyJhbGciOi"))
        val resp = client.users.fetchAccessToken(123)
        assertEquals("eyJhbGciOi", resp.data?.token)
    }

    @Test fun `fetchAccessToken interpolates userId`() = runTest {
        mock.succeed(mapOf("token" to "jwt"))
        client.users.fetchAccessToken(9999)
        assertTrue(mock.capturedUrl?.contains("9999") == true)
    }

    @Test fun `verifyJwt sends POST`() = runTest {
        mock.succeedEmpty()
        client.users.verifyJwt("jwt-token")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `verifyJwt sends correct path`() = runTest {
        mock.succeedEmpty()
        client.users.verifyJwt("jwt-token")
        assertTrue(mock.capturedUrl?.endsWith("/v1/user/verify-jwt") == true)
    }

    @Test fun `verifyJwt encodes token`() = runTest {
        mock.succeedEmpty()
        client.users.verifyJwt("my-secret-jwt")
        assertEquals("my-secret-jwt", mock.capturedBody?.get("token"))
    }

    @Test fun `setToken updates client token`() = runTest {
        client.setToken("new-jwt")
        assertEquals("new-jwt", client.getToken())
    }

    @Test fun `token is sent as header`() = runTest {
        client.setToken("user-jwt-123")
        mock.succeed(mapOf("id" to 1))
        client.users.createUser("a@b.com", "A", "B")
        assertEquals("user-jwt-123", mock.capturedHeaders?.get("token"))
    }
}
