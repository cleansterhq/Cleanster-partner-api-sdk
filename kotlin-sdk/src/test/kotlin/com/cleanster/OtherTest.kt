package com.cleanster

import com.cleanster.model.AvailableCleanersRequest
import com.cleanster.model.CostEstimateRequest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OtherTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `getServices sends GET`() = runTest {
        mock.succeedList(); client.other.getServices()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getServices correct path`() = runTest {
        mock.succeedList(); client.other.getServices()
        assertTrue(mock.capturedUrl?.endsWith("/v1/services") == true)
    }

    @Test fun `getPlans sends GET`() = runTest {
        mock.succeedList(); client.other.getPlans(1004)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getPlans correct path`() = runTest {
        mock.succeedList(); client.other.getPlans(1004)
        assertTrue(mock.capturedUrl?.contains("/v1/plans") == true)
    }

    @Test fun `getPlans encodes propertyId`() = runTest {
        mock.succeedList(); client.other.getPlans(1004)
        assertTrue(mock.capturedUrl?.contains("propertyId=1004") == true)
    }

    @Test fun `getRecommendedHours sends GET`() = runTest {
        mock.succeed(mapOf("hours" to 3.0))
        client.other.getRecommendedHours(1004, 2, 1)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getRecommendedHours correct path`() = runTest {
        mock.succeed(mapOf("hours" to 3.0))
        client.other.getRecommendedHours(1004, 2, 1)
        assertTrue(mock.capturedUrl?.contains("/v1/recommended-hours") == true)
    }

    @Test fun `getRecommendedHours encodes roomCount`() = runTest {
        mock.succeed(mapOf("hours" to 3.0))
        client.other.getRecommendedHours(1004, 3, 2)
        assertTrue(mock.capturedUrl?.contains("roomCount=3") == true)
    }

    @Test fun `getRecommendedHours encodes bathroomCount`() = runTest {
        mock.succeed(mapOf("hours" to 3.0))
        client.other.getRecommendedHours(1004, 2, 2)
        assertTrue(mock.capturedUrl?.contains("bathroomCount=2") == true)
    }

    @Test fun `getRecommendedHours decodes hours`() = runTest {
        mock.succeed(mapOf("hours" to 4.5))
        val resp = client.other.getRecommendedHours(1004, 3, 2)
        assertEquals(4.5, resp.data?.hours)
    }

    @Test fun `getCostEstimate sends POST`() = runTest {
        mock.succeed(mapOf("subtotal" to 105.0, "discount" to 21.0, "total" to 84.0))
        client.other.getCostEstimate(CostEstimateRequest(1004, 2, 3.0))
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `getCostEstimate correct path`() = runTest {
        mock.succeed(mapOf("subtotal" to 105.0, "discount" to 0.0, "total" to 105.0))
        client.other.getCostEstimate(CostEstimateRequest(1004, 2, 3.0))
        assertTrue(mock.capturedUrl?.endsWith("/v1/cost-estimate") == true)
    }

    @Test fun `getCostEstimate encodes coupon`() = runTest {
        mock.succeed(mapOf("subtotal" to 105.0, "discount" to 52.5, "total" to 52.5))
        client.other.getCostEstimate(CostEstimateRequest(1004, 2, 3.0, couponCode = "50POFF"))
        assertEquals("50POFF", mock.capturedBody?.get("couponCode"))
    }

    @Test fun `getCostEstimate decodes total`() = runTest {
        mock.succeed(mapOf("subtotal" to 105.0, "discount" to 21.0, "total" to 84.0))
        val resp = client.other.getCostEstimate(CostEstimateRequest(1004, 2, 3.0))
        assertEquals(84.0, resp.data?.total)
    }

    @Test fun `getCleaningExtras sends GET`() = runTest {
        mock.succeedList(); client.other.getCleaningExtras(1)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getCleaningExtras correct path`() = runTest {
        mock.succeedList(); client.other.getCleaningExtras(1)
        assertTrue(mock.capturedUrl?.endsWith("/v1/cleaning-extras/1") == true)
    }

    @Test fun `getAvailableCleaners sends POST`() = runTest {
        mock.succeedList()
        client.other.getAvailableCleaners(AvailableCleanersRequest(1004, "2025-09-15", "09:00"))
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `getAvailableCleaners correct path`() = runTest {
        mock.succeedList()
        client.other.getAvailableCleaners(AvailableCleanersRequest(1004, "2025-09-15", "09:00"))
        assertTrue(mock.capturedUrl?.endsWith("/v1/available-cleaners") == true)
    }

    @Test fun `getAvailableCleaners encodes date`() = runTest {
        mock.succeedList()
        client.other.getAvailableCleaners(AvailableCleanersRequest(1004, "2025-09-15", "09:00"))
        assertEquals("2025-09-15", mock.capturedBody?.get("date"))
    }

    @Test fun `getCoupons sends GET`() = runTest {
        mock.succeedList(); client.other.getCoupons()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getCoupons correct path`() = runTest {
        mock.succeedList(); client.other.getCoupons()
        assertTrue(mock.capturedUrl?.endsWith("/v1/coupons") == true)
    }
}
