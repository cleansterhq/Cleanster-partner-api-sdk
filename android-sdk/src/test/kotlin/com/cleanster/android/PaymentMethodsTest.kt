package com.cleanster.android

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PaymentMethodsTest {
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

    @Test fun `getSetupIntentDetails returns client secret data`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"clientSecret":"seti_test_xxx"}}""")
        val resp = client.paymentMethods.getSetupIntentDetails()
        assertEquals(200, resp.status)
        assertNotNull(resp.data)
    }

    @Test fun `getSetupIntentDetails sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.paymentMethods.getSetupIntentDetails()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/payment-methods/setup-intent-details"))
    }

    @Test fun `getPayPalClientToken returns token data`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"token":"paypal-client-token-xxx"}}""")
        val resp = client.paymentMethods.getPayPalClientToken()
        assertEquals(200, resp.status)
        assertNotNull(resp.data)
    }

    @Test fun `getPayPalClientToken sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.paymentMethods.getPayPalClientToken()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/payment-methods/paypal-client-token"))
    }

    @Test fun `addPaymentMethod sends POST with paymentMethodId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":55,"brand":"Visa","last4":"4242"}}""")
        client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.body.readUtf8().contains("pm_1OjvDE2eZvKYlo2C"))
    }

    @Test fun `addPaymentMethod returns saved method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":55,"brand":"Visa","last4":"4242","isDefault":false}}""")
        val resp = client.paymentMethods.addPaymentMethod("pm_xxx")
        assertEquals(55, resp.data?.id)
        assertEquals("Visa", resp.data?.brand)
        assertEquals("4242", resp.data?.last4)
    }

    @Test fun `getPaymentMethods returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":55,"brand":"Visa","last4":"4242","isDefault":true},{"id":56,"brand":"Mastercard","last4":"1234","isDefault":false}]}""")
        val resp = client.paymentMethods.getPaymentMethods()
        assertEquals(2, resp.data?.size)
        assertEquals(55, resp.data?.first()?.id)
    }

    @Test fun `getPaymentMethods sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.paymentMethods.getPaymentMethods()
        val req = server.takeRequest()
        assertEquals("GET", req.method)
        assert(req.path!!.contains("v1/payment-methods"))
    }

    @Test fun `setDefaultPaymentMethod sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.paymentMethods.setDefaultPaymentMethod(55)
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/payment-methods/55/default"))
    }

    @Test fun `setDefaultPaymentMethod returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.paymentMethods.setDefaultPaymentMethod(55)
        assertEquals(200, resp.status)
    }

    @Test fun `deletePaymentMethod sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.paymentMethods.deletePaymentMethod(55)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/payment-methods/55"))
    }

    @Test fun `deletePaymentMethod returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.paymentMethods.deletePaymentMethod(55)
        assertEquals(200, resp.status)
    }

    @Test fun `getPaymentMethods isDefault field`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":55,"isDefault":true}]}""")
        val resp = client.paymentMethods.getPaymentMethods()
        assertEquals(true, resp.data?.first()?.isDefault)
    }

    @Test fun `addPaymentMethod path is correct`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":55}}""")
        client.paymentMethods.addPaymentMethod("pm_xxx")
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/payment-methods"))
        assert(!req.path!!.contains("default"))
    }
}
