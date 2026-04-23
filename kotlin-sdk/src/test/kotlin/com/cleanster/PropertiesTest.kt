package com.cleanster

import com.cleanster.model.CreatePropertyRequest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PropertiesTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    private fun propReq(name: String = "Home", state: String? = null, zip: String? = null,
                        timezone: String? = null, lat: Double? = null, lon: Double? = null) =
        CreatePropertyRequest(name = name, address = "123 Main", city = "Atlanta",
            country = "US", roomCount = 2, bathroomCount = 1, serviceId = 1,
            state = state, zip = zip, timezone = timezone, latitude = lat, longitude = lon)

    @Test fun `listProperties sends GET`() = runTest {
        mock.succeedList(); client.properties.listProperties()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `listProperties correct path`() = runTest {
        mock.succeedList(); client.properties.listProperties()
        assertTrue(mock.capturedUrl?.contains("/v1/properties") == true)
    }

    @Test fun `listProperties with serviceId`() = runTest {
        mock.succeedList(); client.properties.listProperties(serviceId = 1)
        assertTrue(mock.capturedUrl?.contains("serviceId=1") == true)
    }

    @Test fun `listProperties no serviceId has no query param`() = runTest {
        mock.succeedList(); client.properties.listProperties()
        assertFalse(mock.capturedUrl?.contains("serviceId") == true)
    }

    @Test fun `addProperty sends POST`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq())
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `addProperty correct path`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq())
        assertTrue(mock.capturedUrl?.endsWith("/v1/properties") == true)
    }

    @Test fun `addProperty encodes name`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq("Downtown Loft"))
        assertEquals("Downtown Loft", mock.capturedBody?.get("name"))
    }

    @Test fun `addProperty encodes optional state`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq(state = "GA"))
        assertEquals("GA", mock.capturedBody?.get("state"))
    }

    @Test fun `addProperty encodes optional zip`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq(zip = "30301"))
        assertEquals("30301", mock.capturedBody?.get("zip"))
    }

    @Test fun `addProperty encodes optional timezone`() = runTest {
        mock.succeed(mapOf("id" to 1004.0))
        client.properties.addProperty(propReq(timezone = "America/New_York"))
        assertEquals("America/New_York", mock.capturedBody?.get("timezone"))
    }

    @Test fun `addProperty encodes latitude`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq(lat = 33.749))
        assertEquals(33.749, mock.capturedBody?.get("latitude"))
    }

    @Test fun `addProperty encodes longitude`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.addProperty(propReq(lon = -84.388))
        assertEquals(-84.388, mock.capturedBody?.get("longitude"))
    }

    @Test fun `addProperty decodes id`() = runTest {
        mock.succeed(mapOf("id" to 1004.0))
        val resp = client.properties.addProperty(propReq())
        assertEquals(1004, resp.data?.id)
    }

    @Test fun `getProperty sends GET`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.getProperty(1004)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getProperty interpolates id`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.getProperty(1004)
        assertTrue(mock.capturedUrl?.contains("1004") == true)
    }

    @Test fun `updateProperty sends PUT`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.updateProperty(1004, propReq())
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `updateProperty correct path`() = runTest {
        mock.succeed(mapOf("id" to 1004.0)); client.properties.updateProperty(1004, propReq())
        assertTrue(mock.capturedUrl?.endsWith("/v1/properties/1004") == true)
    }

    @Test fun `deleteProperty sends DELETE`() = runTest {
        mock.succeedEmpty(); client.properties.deleteProperty(1004)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `deleteProperty correct path`() = runTest {
        mock.succeedEmpty(); client.properties.deleteProperty(1004)
        assertTrue(mock.capturedUrl?.endsWith("/v1/properties/1004") == true)
    }

    @Test fun `enableOrDisableProperty sends POST`() = runTest {
        mock.succeedEmpty(); client.properties.enableOrDisableProperty(1004, true)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `enableOrDisableProperty encodes enabled`() = runTest {
        mock.succeedEmpty(); client.properties.enableOrDisableProperty(1004, false)
        assertEquals(false, mock.capturedBody?.get("enabled"))
    }

    @Test fun `enableOrDisableProperty correct path`() = runTest {
        mock.succeedEmpty(); client.properties.enableOrDisableProperty(1004, true)
        assertTrue(mock.capturedUrl?.endsWith("/v1/properties/1004/enable-disable") == true)
    }

    @Test fun `getPropertyCleaners sends GET`() = runTest {
        mock.succeedList(); client.properties.getPropertyCleaners(1004)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `addCleanerToProperty sends POST`() = runTest {
        mock.succeedEmpty(); client.properties.addCleanerToProperty(1004, 789)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `addCleanerToProperty encodes cleanerId`() = runTest {
        mock.succeedEmpty(); client.properties.addCleanerToProperty(1004, 789)
        assertEquals(789.0, mock.capturedBody?.get("cleanerId"))
    }

    @Test fun `removeCleanerFromProperty sends DELETE`() = runTest {
        mock.succeedEmpty(); client.properties.removeCleanerFromProperty(1004, 789)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `removeCleanerFromProperty correct path`() = runTest {
        mock.succeedEmpty(); client.properties.removeCleanerFromProperty(1004, 789)
        assertTrue(mock.capturedUrl?.endsWith("/v1/properties/1004/cleaners/789") == true)
    }

    @Test fun `setICalLink sends PUT`() = runTest {
        mock.succeedEmpty()
        client.properties.setICalLink(1004, "https://airbnb.com/cal.ics")
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `setICalLink encodes link`() = runTest {
        mock.succeedEmpty()
        client.properties.setICalLink(1004, "https://airbnb.com/cal.ics")
        assertEquals("https://airbnb.com/cal.ics", mock.capturedBody?.get("icalLink"))
    }

    @Test fun `getICalLink sends GET`() = runTest {
        mock.succeed(mapOf("icalLink" to "https://x.com/cal.ics"))
        client.properties.getICalLink(1004)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `deleteICalLink sends DELETE`() = runTest {
        mock.succeedEmpty()
        client.properties.deleteICalLink(1004, "https://airbnb.com/cal.ics")
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `setDefaultChecklist sends PUT`() = runTest {
        mock.succeedEmpty(); client.properties.setDefaultChecklist(1004, 77)
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `setDefaultChecklist correct path`() = runTest {
        mock.succeedEmpty(); client.properties.setDefaultChecklist(1004, 77)
        assertTrue(mock.capturedUrl?.contains("/v1/properties/1004/checklist/77") == true)
    }

    @Test fun `setDefaultChecklist updateUpcoming param`() = runTest {
        mock.succeedEmpty()
        client.properties.setDefaultChecklist(1004, 77, updateUpcomingBookings = true)
        assertTrue(mock.capturedUrl?.contains("updateUpcomingBookings=true") == true)
    }
}
