package com.cleanster

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentMethodsTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    @Test fun `getSetupIntentDetails sends GET`() = runTest {
        mock.succeed(mapOf("clientSecret" to "seti_xxx"))
        client.paymentMethods.getSetupIntentDetails()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getSetupIntentDetails correct path`() = runTest {
        mock.succeed(mapOf("clientSecret" to "seti_xxx"))
        client.paymentMethods.getSetupIntentDetails()
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods/setup-intent-details") == true)
    }

    @Test fun `getPayPalClientToken sends GET`() = runTest {
        mock.succeed(mapOf("clientToken" to "paypal_xxx"))
        client.paymentMethods.getPayPalClientToken()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getPayPalClientToken correct path`() = runTest {
        mock.succeed(mapOf("clientToken" to "paypal_xxx"))
        client.paymentMethods.getPayPalClientToken()
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods/paypal-client-token") == true)
    }

    @Test fun `addPaymentMethod sends POST`() = runTest {
        mock.succeed(mapOf("id" to 55.0, "type" to "card"))
        client.paymentMethods.addPaymentMethod("pm_abc")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `addPaymentMethod correct path`() = runTest {
        mock.succeed(mapOf("id" to 55.0))
        client.paymentMethods.addPaymentMethod("pm_abc")
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods") == true)
    }

    @Test fun `addPaymentMethod encodes token`() = runTest {
        mock.succeed(mapOf("id" to 55.0))
        client.paymentMethods.addPaymentMethod("pm_abc123")
        assertEquals("pm_abc123", mock.capturedBody?.get("paymentMethodId"))
    }

    @Test fun `addPaymentMethod decodes id`() = runTest {
        mock.succeed(mapOf("id" to 55.0, "type" to "card", "brand" to "visa"))
        val resp = client.paymentMethods.addPaymentMethod("pm_xxx")
        assertEquals(55, resp.data?.id)
    }

    @Test fun `addPaymentMethod decodes type`() = runTest {
        mock.succeed(mapOf("id" to 55.0, "type" to "paypal"))
        val resp = client.paymentMethods.addPaymentMethod("nonce_xxx")
        assertEquals("paypal", resp.data?.type)
    }

    @Test fun `getPaymentMethods sends GET`() = runTest {
        mock.succeedList(); client.paymentMethods.getPaymentMethods()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getPaymentMethods correct path`() = runTest {
        mock.succeedList(); client.paymentMethods.getPaymentMethods()
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods") == true)
    }

    @Test fun `setDefaultPaymentMethod sends PUT`() = runTest {
        mock.succeedEmpty(); client.paymentMethods.setDefaultPaymentMethod(55)
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `setDefaultPaymentMethod correct path`() = runTest {
        mock.succeedEmpty(); client.paymentMethods.setDefaultPaymentMethod(55)
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods/55/default") == true)
    }

    @Test fun `deletePaymentMethod sends DELETE`() = runTest {
        mock.succeedEmpty(); client.paymentMethods.deletePaymentMethod(55)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `deletePaymentMethod correct path`() = runTest {
        mock.succeedEmpty(); client.paymentMethods.deletePaymentMethod(55)
        assertTrue(mock.capturedUrl?.endsWith("/v1/payment-methods/55") == true)
    }
}
