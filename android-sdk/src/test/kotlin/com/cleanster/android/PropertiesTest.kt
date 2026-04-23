package com.cleanster.android

import com.cleanster.android.model.CreatePropertyRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PropertiesTest {
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

    private val propJson = """{"id":1004,"name":"Downtown Loft","address":"123 Main St","city":"Atlanta","country":"US","roomCount":2,"bathroomCount":1,"serviceId":1}"""

    // ─── listProperties ───────────────────────────────────────────────────────
    @Test fun `listProperties returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[$propJson,$propJson]}""")
        val resp = client.properties.listProperties()
        assertEquals(200, resp.status)
        assertEquals(2, resp.data?.size)
    }

    @Test fun `listProperties with serviceId sends query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.properties.listProperties(serviceId = 1)
        val req = server.takeRequest()
        assert(req.path!!.contains("serviceId=1"))
    }

    @Test fun `listProperties without serviceId omits query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.properties.listProperties()
        val req = server.takeRequest()
        assert(!req.path!!.contains("serviceId"))
    }

    // ─── addProperty ──────────────────────────────────────────────────────────
    @Test fun `addProperty returns created property`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        val resp = client.properties.addProperty(
            CreatePropertyRequest(
                name = "Downtown Loft", address = "123 Main St",
                city = "Atlanta", country = "US",
                roomCount = 2, bathroomCount = 1, serviceId = 1,
            )
        )
        assertEquals(1004, resp.data?.id)
        assertEquals("Downtown Loft", resp.data?.name)
    }

    @Test fun `addProperty sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        client.properties.addProperty(
            CreatePropertyRequest("X", "Y", "Z", "US", 1, 1, 1)
        )
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/properties"))
    }

    @Test fun `addProperty includes optional fields`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        client.properties.addProperty(
            CreatePropertyRequest(
                name = "Home", address = "1 St", city = "NYC", country = "US",
                roomCount = 3, bathroomCount = 2, serviceId = 1,
                state = "NY", zip = "10001", timezone = "America/New_York",
                note = "Ring bell", latitude = 40.7, longitude = -74.0,
            )
        )
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("NY"))
        assert(body.contains("10001"))
        assert(body.contains("America/New_York"))
    }

    // ─── getProperty ──────────────────────────────────────────────────────────
    @Test fun `getProperty returns single property`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        val resp = client.properties.getProperty(1004)
        assertEquals(1004, resp.data?.id)
        assertEquals("Atlanta", resp.data?.city)
    }

    @Test fun `getProperty hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        client.properties.getProperty(1004)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/properties/1004"))
    }

    // ─── updateProperty ───────────────────────────────────────────────────────
    @Test fun `updateProperty sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        client.properties.updateProperty(1004,
            CreatePropertyRequest("New Name", "123", "Atlanta", "US", 3, 2, 1)
        )
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/properties/1004"))
    }

    // ─── deleteProperty ───────────────────────────────────────────────────────
    @Test fun `deleteProperty sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.deleteProperty(1004)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/properties/1004"))
    }

    // ─── updateAdditionalInformation ──────────────────────────────────────────
    @Test fun `updateAdditionalInformation sends PUT with map`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.updateAdditionalInformation(1004, mapOf("parkingInfo" to "Lot B"))
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.body.readUtf8().contains("parkingInfo"))
    }

    // ─── enableOrDisableProperty ──────────────────────────────────────────────
    @Test fun `enableProperty sends enabled true`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.enableOrDisableProperty(1004, enabled = true)
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("true"))
    }

    @Test fun `disableProperty sends enabled false`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.enableOrDisableProperty(1004, enabled = false)
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("false"))
    }

    // ─── getPropertyCleaners ──────────────────────────────────────────────────
    @Test fun `getPropertyCleaners returns cleaner list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":789,"firstName":"John","lastName":"Doe"}]}""")
        val resp = client.properties.getPropertyCleaners(1004)
        assertEquals(1, resp.data?.size)
        assertEquals(789, resp.data?.first()?.id)
    }

    @Test fun `getPropertyCleaners hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.properties.getPropertyCleaners(1004)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/properties/1004/cleaners"))
    }

    // ─── addCleanerToProperty ─────────────────────────────────────────────────
    @Test fun `addCleanerToProperty sends POST with cleanerId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.addCleanerToProperty(1004, 789)
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.body.readUtf8().contains("789"))
    }

    // ─── removeCleanerFromProperty ────────────────────────────────────────────
    @Test fun `removeCleanerFromProperty sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.removeCleanerFromProperty(1004, 789)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/properties/1004/cleaners/789"))
    }

    // ─── setICalLink ──────────────────────────────────────────────────────────
    @Test fun `setICalLink sends PUT with link`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.setICalLink(1004, "https://airbnb.com/ical/12345.ics")
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.body.readUtf8().contains("airbnb"))
    }

    @Test fun `setICalLink hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.setICalLink(1004, "https://airbnb.com/ical/12345.ics")
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/properties/1004/ical"))
    }

    // ─── getICalLink ──────────────────────────────────────────────────────────
    @Test fun `getICalLink sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"icalLink":"https://airbnb.com/ical/12345.ics"}}""")
        client.properties.getICalLink(1004)
        val req = server.takeRequest()
        assertEquals("GET", req.method)
    }

    // ─── deleteICalLink ───────────────────────────────────────────────────────
    @Test fun `deleteICalLink sends DELETE with body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.deleteICalLink(1004, "https://airbnb.com/ical/12345.ics")
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.body.readUtf8().contains("airbnb"))
    }

    // ─── setDefaultChecklist ──────────────────────────────────────────────────
    @Test fun `setDefaultChecklist sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.setDefaultChecklist(1004, 77)
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/properties/1004/checklist/77"))
    }

    @Test fun `setDefaultChecklist with updateUpcomingBookings sends query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.properties.setDefaultChecklist(1004, 77, updateUpcomingBookings = true)
        val req = server.takeRequest()
        assert(req.path!!.contains("updateUpcomingBookings=true"))
    }

    @Test fun `listProperties returns 200 status`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.properties.listProperties()
        assertEquals(200, resp.status)
    }

    @Test fun `getProperty message is OK`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":$propJson}""")
        val resp = client.properties.getProperty(1004)
        assertEquals("OK", resp.message)
    }

    @Test fun `deleteProperty returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.properties.deleteProperty(1004)
        assertEquals(200, resp.status)
    }
}
