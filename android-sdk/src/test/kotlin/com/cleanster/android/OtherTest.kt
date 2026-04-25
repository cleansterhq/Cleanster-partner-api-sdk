package com.cleanster.android

import com.cleanster.android.model.AvailableCleanersRequest
import com.cleanster.android.model.CostEstimateRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OtherTest {
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

    // ─── getServices ──────────────────────────────────────────────────────────
    @Test fun `getServices returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":1,"name":"Residential"},{"id":2,"name":"Commercial"}]}""")
        val resp = client.other.getServices()
        assertEquals(200, resp.status)
        assertNotNull(resp.data)
    }

    @Test fun `getServices sends GET to v1 services`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.getServices()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/services"))
    }

    // ─── getPlans ─────────────────────────────────────────────────────────────
    @Test fun `getPlans sends propertyId as query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":1,"name":"Standard"},{"id":2,"name":"Deep Clean"}]}""")
        client.other.getPlans(1004)
        val req = server.takeRequest()
        assert(req.path!!.contains("propertyId=1004"))
    }

    @Test fun `getPlans returns plan list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":1},{"id":2}]}""")
        val resp = client.other.getPlans(1004)
        assertEquals(2, resp.data?.size)
    }

    // ─── getRecommendedHours ──────────────────────────────────────────────────
    @Test fun `getRecommendedHours sends all query params`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"hours":3.0}}""")
        client.other.getRecommendedHours(1004, 2, 1)
        val req = server.takeRequest()
        val path = req.path!!
        assert(path.contains("propertyId=1004"))
        assert(path.contains("roomCount=2"))
        assert(path.contains("bathroomCount=1"))
    }

    @Test fun `getRecommendedHours returns hours`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"hours":4.5}}""")
        val resp = client.other.getRecommendedHours(1004, 3, 2)
        assertNotNull(resp.data)
        assertEquals(200, resp.status)
    }

    // ─── getCostEstimate ──────────────────────────────────────────────────────
    @Test fun `getCostEstimate returns total`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"subtotal":120.0,"discount":24.0,"total":96.0}}""")
        val resp = client.other.getCostEstimate(
            CostEstimateRequest(propertyId = 1004, planId = 2, hours = 3.0)
        )
        assertEquals(96.0, resp.data?.total)
        assertEquals(24.0, resp.data?.discount)
    }

    @Test fun `getCostEstimate with coupon sends coupon in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"subtotal":100.0,"discount":50.0,"total":50.0}}""")
        client.other.getCostEstimate(
            CostEstimateRequest(1004, 2, 3.0, couponCode = "50POFF")
        )
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("50POFF"))
    }

    @Test fun `getCostEstimate sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"subtotal":100.0,"discount":0.0,"total":100.0}}""")
        client.other.getCostEstimate(CostEstimateRequest(1004, 2, 3.0))
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/cost-estimate"))
    }

    @Test fun `getCostEstimate with extras`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"subtotal":145.0,"discount":0.0,"total":145.0}}""")
        client.other.getCostEstimate(
            CostEstimateRequest(1004, 2, 3.0, extras = listOf(3, 7))
        )
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("extras"))
    }

    // ─── getCleaningExtras ────────────────────────────────────────────────────
    @Test fun `getCleaningExtras sends serviceId in path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":3,"name":"Oven"},{"id":7,"name":"Fridge"}]}""")
        client.other.getCleaningExtras(1)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/cleaning-extras/1"))
    }

    @Test fun `getCleaningExtras returns extras`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":3},{"id":7}]}""")
        val resp = client.other.getCleaningExtras(1)
        assertEquals(2, resp.data?.size)
    }

    // ─── getAvailableCleaners ─────────────────────────────────────────────────
    @Test fun `getAvailableCleaners sends POST with body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":789,"firstName":"Jane","lastName":"Doe"}]}""")
        client.other.getAvailableCleaners(
            AvailableCleanersRequest(1004, "2025-09-15", "09:00")
        )
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/available-cleaners"))
        val body = req.body.readUtf8()
        assert(body.contains("2025-09-15"))
        assert(body.contains("09:00"))
    }

    @Test fun `getAvailableCleaners returns cleaner list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":789},{"id":790}]}""")
        val resp = client.other.getAvailableCleaners(
            AvailableCleanersRequest(1004, "2025-09-15", "09:00")
        )
        assertEquals(2, resp.data?.size)
    }

    // ─── getCoupons ───────────────────────────────────────────────────────────
    @Test fun `getCoupons returns coupon list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"code":"20POFF","discount":"20%"},{"code":"100OFF","discount":"$100"}]}""")
        val resp = client.other.getCoupons()
        assertEquals(2, resp.data?.size)
        assertEquals("20POFF", resp.data?.first()?.code)
    }

    @Test fun `getCoupons sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.getCoupons()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/coupons"))
    }

    @Test fun `getServices returns 200 status`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.other.getServices()
        assertEquals(200, resp.status)
    }

    @Test fun `getCostEstimate subtotal field`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"subtotal":150.0,"discount":0.0,"total":150.0}}""")
        val resp = client.other.getCostEstimate(CostEstimateRequest(1004, 2, 4.0))
        assertEquals(150.0, resp.data?.subtotal)
    }

    @Test fun `getPlans sends GET method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.getPlans(1004)
        assertEquals("GET", server.takeRequest().method)
    }

    @Test fun `getAvailableCleaners includes propertyId in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.getAvailableCleaners(AvailableCleanersRequest(9999, "2025-09-15", "09:00"))
        assert(server.takeRequest().body.readUtf8().contains("9999"))
    }

    @Test fun `getCoupons empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.other.getCoupons()
        assertEquals(0, resp.data?.size)
    }

    @Test fun `listCleaners sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.listCleaners()
        assertEquals("GET", server.takeRequest().method)
    }

    @Test fun `listCleaners correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.other.listCleaners()
        assert(server.takeRequest().path?.contains("/v1/cleaners") == true)
    }

    @Test fun `getCleaner sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.other.getCleaner(789)
        assertEquals("GET", server.takeRequest().method)
    }

    @Test fun `getCleaner correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.other.getCleaner(789)
        assert(server.takeRequest().path?.contains("/v1/cleaners/789") == true)
    }
}
