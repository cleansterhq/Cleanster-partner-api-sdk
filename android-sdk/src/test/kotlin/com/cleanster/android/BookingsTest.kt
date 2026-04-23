package com.cleanster.android

import com.cleanster.android.model.CreateBookingRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BookingsTest {
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

    // ─── getBookings ──────────────────────────────────────────────────────────
    @Test fun `getBookings returns list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":1,"status":"OPEN"},{"id":2,"status":"COMPLETED"}]}""")
        val resp = client.bookings.getBookings()
        assertEquals(200, resp.status)
        assertEquals(2, resp.data?.size)
    }

    @Test fun `getBookings with status filter sends query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"id":1,"status":"OPEN"}]}""")
        client.bookings.getBookings(status = "OPEN")
        val req = server.takeRequest()
        assert(req.path!!.contains("status=OPEN"))
    }

    @Test fun `getBookings with pageNo sends query param`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getBookings(pageNo = 3)
        val req = server.takeRequest()
        assert(req.path!!.contains("pageNo=3"))
    }

    @Test fun `getBookings empty data returns empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.bookings.getBookings()
        assertEquals(0, resp.data?.size)
    }

    // ─── getBookingDetails ───────────────────────────────────────────────────
    @Test fun `getBookingDetails returns booking`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":16926,"status":"OPEN","date":"2025-09-15","time":"09:00","hours":3.0}}""")
        val resp = client.bookings.getBookingDetails(16926)
        assertEquals(16926, resp.data?.id)
        assertEquals("OPEN", resp.data?.status)
    }

    @Test fun `getBookingDetails hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":16926}}""")
        client.bookings.getBookingDetails(16926)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/bookings/16926"))
    }

    @Test fun `getBookingDetails null data on 404`() = runTest {
        enqueue("""{"status":404,"message":"Not found","data":null}""", 200)
        val resp = client.bookings.getBookingDetails(99999)
        assertEquals(404, resp.status)
        assertNull(resp.data)
    }

    // ─── createBooking ───────────────────────────────────────────────────────
    @Test fun `createBooking returns new booking`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":555,"status":"OPEN"}}""")
        val resp = client.bookings.createBooking(
            CreateBookingRequest(
                date = "2025-09-15", time = "09:00", propertyId = 1004,
                planId = 2, hours = 3.0, roomCount = 2, bathroomCount = 1,
                extraSupplies = false, paymentMethodId = 55,
            )
        )
        assertEquals(555, resp.data?.id)
        assertEquals("OPEN", resp.data?.status)
    }

    @Test fun `createBooking sends POST`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.bookings.createBooking(
            CreateBookingRequest("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55)
        )
        val req = server.takeRequest()
        assertEquals("POST", req.method)
    }

    @Test fun `createBooking includes coupon code in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.bookings.createBooking(
            CreateBookingRequest("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55, couponCode = "20POFF")
        )
        val req = server.takeRequest()
        assert(req.body.readUtf8().contains("20POFF"))
    }

    // ─── cancelBooking ───────────────────────────────────────────────────────
    @Test fun `cancelBooking sends POST to correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.cancelBooking(16926, reason = "Test")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.path!!.contains("v1/bookings/16926/cancel"))
    }

    @Test fun `cancelBooking without reason`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.bookings.cancelBooking(16926)
        assertEquals(200, resp.status)
    }

    // ─── rescheduleBooking ───────────────────────────────────────────────────
    @Test fun `rescheduleBooking sends correct date and time`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.rescheduleBooking(16926, "2025-10-01", "11:00")
        val req = server.takeRequest()
        val body = req.body.readUtf8()
        assert(body.contains("2025-10-01"))
        assert(body.contains("11:00"))
    }

    // ─── assignCleaner ───────────────────────────────────────────────────────
    @Test fun `assignCleaner sends cleanerId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.assignCleaner(16926, cleanerId = 789)
        val req = server.takeRequest()
        assert(req.body.readUtf8().contains("789"))
    }

    @Test fun `assignCleaner hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.assignCleaner(16926, 789)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/bookings/16926/cleaner"))
    }

    // ─── removeAssignedCleaner ───────────────────────────────────────────────
    @Test fun `removeAssignedCleaner sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.removeAssignedCleaner(16926)
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("v1/bookings/16926/cleaner"))
    }

    // ─── adjustHours ─────────────────────────────────────────────────────────
    @Test fun `adjustHours sends hours in body`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.adjustHours(16926, 4.5)
        val req = server.takeRequest()
        assert(req.body.readUtf8().contains("4.5"))
    }

    // ─── payExpenses ─────────────────────────────────────────────────────────
    @Test fun `payExpenses sends paymentMethodId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.payExpenses(16926, 55)
        val req = server.takeRequest()
        assert(req.body.readUtf8().contains("55"))
    }

    // ─── getBookingInspection ────────────────────────────────────────────────
    @Test fun `getBookingInspection hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.bookings.getBookingInspection(16926)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/bookings/16926/inspection"))
    }

    // ─── getBookingInspectionDetails ─────────────────────────────────────────
    @Test fun `getBookingInspectionDetails hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.bookings.getBookingInspectionDetails(16926)
        val req = server.takeRequest()
        assert(req.path!!.contains("inspection/details"))
    }

    // ─── assignChecklistToBooking ────────────────────────────────────────────
    @Test fun `assignChecklistToBooking sends PUT`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.assignChecklistToBooking(16926, 77)
        val req = server.takeRequest()
        assertEquals("PUT", req.method)
        assert(req.path!!.contains("v1/bookings/16926/checklist/77"))
    }

    // ─── submitFeedback ───────────────────────────────────────────────────────
    @Test fun `submitFeedback sends rating`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.submitFeedback(16926, rating = 5, comment = "Perfect!")
        val req = server.takeRequest()
        val body = req.body.readUtf8()
        assert(body.contains("5"))
        assert(body.contains("Perfect!"))
    }

    @Test fun `submitFeedback without comment`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.bookings.submitFeedback(16926, rating = 4)
        assertEquals(200, resp.status)
    }

    // ─── addTip ───────────────────────────────────────────────────────────────
    @Test fun `addTip sends amount and paymentMethodId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.addTip(16926, 15.0, 55)
        val req = server.takeRequest()
        val body = req.body.readUtf8()
        assert(body.contains("15.0"))
        assert(body.contains("55"))
    }

    // ─── getChat ──────────────────────────────────────────────────────────────
    @Test fun `getChat returns messages`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[{"messageId":"m1","content":"Hello","senderType":"client"}]}""")
        val resp = client.bookings.getChat(16926)
        assertEquals(1, resp.data?.size)
        assertEquals("Hello", resp.data?.first()?.content)
    }

    @Test fun `getChat hits correct path`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getChat(16926)
        val req = server.takeRequest()
        assert(req.path!!.contains("v1/bookings/16926/chat"))
        assertEquals("GET", req.method)
    }

    // ─── sendMessage ──────────────────────────────────────────────────────────
    @Test fun `sendMessage sends POST with message`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.sendMessage(16926, "On my way!")
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assert(req.body.readUtf8().contains("On my way!"))
    }

    // ─── deleteMessage ────────────────────────────────────────────────────────
    @Test fun `deleteMessage sends DELETE`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.deleteMessage(16926, "-OLPrlE06uD8")
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assert(req.path!!.contains("-OLPrlE06uD8"))
    }

    // ─── auth headers ─────────────────────────────────────────────────────────
    @Test fun `request includes access-key header`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getBookings()
        val req = server.takeRequest()
        assertEquals("test-key", req.getHeader("access-key"))
    }

    @Test fun `request includes token header`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getBookings()
        val req = server.takeRequest()
        assertEquals("test-token", req.getHeader("token"))
    }

    // ─── additional coverage ──────────────────────────────────────────────────
    @Test fun `getBookings sends GET method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        client.bookings.getBookings()
        assertEquals("GET", server.takeRequest().method)
    }

    @Test fun `getBookingDetails response status is 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1,"status":"OPEN"}}""")
        val resp = client.bookings.getBookingDetails(1)
        assertEquals(200, resp.status)
    }

    @Test fun `createBooking sends path v1 bookings create`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.bookings.createBooking(
            CreateBookingRequest("2025-09-15","09:00",1004,2,3.0,2,1,false,55)
        )
        assert(server.takeRequest().path!!.contains("v1/bookings/create"))
    }

    @Test fun `rescheduleBooking sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.rescheduleBooking(16926, "2025-10-01", "11:00")
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `assignCleaner sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.assignCleaner(16926, 789)
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `adjustHours sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.adjustHours(16926, 3.0)
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `payExpenses sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.payExpenses(16926, 55)
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `getBookingInspection sends GET method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.bookings.getBookingInspection(16926)
        assertEquals("GET", server.takeRequest().method)
    }

    @Test fun `submitFeedback sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.submitFeedback(16926, 5)
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `addTip sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.addTip(16926, 10.0, 55)
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `sendMessage sends POST method`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.sendMessage(16926, "Hi")
        assertEquals("POST", server.takeRequest().method)
    }

    @Test fun `cancelBooking returns 200`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        val resp = client.bookings.cancelBooking(16926)
        assertEquals(200, resp.status)
    }

    @Test fun `rescheduleBooking path contains bookingId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.rescheduleBooking(16926, "2025-10-01", "11:00")
        assert(server.takeRequest().path!!.contains("16926"))
    }

    @Test fun `adjustHours path contains bookingId`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":null}""")
        client.bookings.adjustHours(55555, 2.0)
        assert(server.takeRequest().path!!.contains("55555"))
    }

    @Test fun `createBooking with extras list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{"id":1}}""")
        client.bookings.createBooking(
            CreateBookingRequest("2025-09-15","09:00",1004,2,3.0,2,1,false,55, extras = listOf(3,7,9))
        )
        val body = server.takeRequest().body.readUtf8()
        assert(body.contains("extras"))
    }

    @Test fun `getChat returns empty list`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":[]}""")
        val resp = client.bookings.getChat(16926)
        assertEquals(0, resp.data?.size)
    }

    @Test fun `getBookingInspectionDetails sends GET`() = runTest {
        enqueue("""{"status":200,"message":"OK","data":{}}""")
        client.bookings.getBookingInspectionDetails(16926)
        assertEquals("GET", server.takeRequest().method)
    }
}
