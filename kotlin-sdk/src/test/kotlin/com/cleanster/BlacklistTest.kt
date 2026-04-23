package com.cleanster

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlacklistTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `listBlacklistedCleaners sends GET`() = runTest {
        mock.succeedList(); client.blacklist.listBlacklistedCleaners()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `listBlacklistedCleaners correct path`() = runTest {
        mock.succeedList(); client.blacklist.listBlacklistedCleaners()
        assertTrue(mock.capturedUrl?.endsWith("/v1/blacklist/cleaner") == true)
    }

    @Test fun `addToBlacklist sends POST`() = runTest {
        mock.succeedEmpty(); client.blacklist.addToBlacklist(789)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `addToBlacklist correct path`() = runTest {
        mock.succeedEmpty(); client.blacklist.addToBlacklist(789)
        assertTrue(mock.capturedUrl?.endsWith("/v1/blacklist/cleaner") == true)
    }

    @Test fun `addToBlacklist encodes cleanerId`() = runTest {
        mock.succeedEmpty(); client.blacklist.addToBlacklist(789)
        assertEquals(789.0, mock.capturedBody?.get("cleanerId"))
    }

    @Test fun `addToBlacklist encodes optional reason`() = runTest {
        mock.succeedEmpty(); client.blacklist.addToBlacklist(789, reason = "Repeated issues")
        assertEquals("Repeated issues", mock.capturedBody?.get("reason"))
    }

    @Test fun `removeFromBlacklist sends DELETE`() = runTest {
        mock.succeedEmpty(); client.blacklist.removeFromBlacklist(789)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `removeFromBlacklist correct path`() = runTest {
        mock.succeedEmpty(); client.blacklist.removeFromBlacklist(789)
        assertTrue(mock.capturedUrl?.endsWith("/v1/blacklist/cleaner") == true)
    }

    @Test fun `removeFromBlacklist encodes cleanerId`() = runTest {
        mock.succeedEmpty(); client.blacklist.removeFromBlacklist(789)
        assertEquals(789.0, mock.capturedBody?.get("cleanerId"))
    }
}
