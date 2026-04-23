package com.cleanster

import com.cleanster.model.CreateBookingRequest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookingsTest {

    private lateinit var mock: MockHttpEngine
    private lateinit var client: CleansterClient

    @BeforeEach fun setUp() {
        mock   = MockHttpEngine()
        client = testClient(mock)
    }

    private fun bookingReq(extras: List<Int>? = null, coupon: String? = null) = CreateBookingRequest(
        date = "2025-09-15", time = "10:00", propertyId = 1004, planId = 2,
        hours = 3.0, roomCount = 2, bathroomCount = 1, extraSupplies = false,
        paymentMethodId = 55, couponCode = coupon, extras = extras,
    )

    @Test fun `getBookings sends GET`() = runTest {
        mock.succeedList(); client.bookings.getBookings()
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getBookings correct path`() = runTest {
        mock.succeedList(); client.bookings.getBookings()
        assertTrue(mock.capturedUrl?.contains("/v1/bookings") == true)
    }

    @Test fun `getBookings with status`() = runTest {
        mock.succeedList(); client.bookings.getBookings(status = "OPEN")
        assertTrue(mock.capturedUrl?.contains("status=OPEN") == true)
    }

    @Test fun `getBookings with pageNo`() = runTest {
        mock.succeedList(); client.bookings.getBookings(pageNo = 2)
        assertTrue(mock.capturedUrl?.contains("pageNo=2") == true)
    }

    @Test fun `getBookings with status and page`() = runTest {
        mock.succeedList(); client.bookings.getBookings(pageNo = 3, status = "COMPLETED")
        assertTrue(mock.capturedUrl?.contains("COMPLETED") == true)
        assertTrue(mock.capturedUrl?.contains("pageNo=3") == true)
    }

    @Test fun `createBooking sends POST`() = runTest {
        mock.succeed(mapOf("id" to 101.0, "status" to "OPEN"))
        client.bookings.createBooking(bookingReq())
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `createBooking correct path`() = runTest {
        mock.succeed(mapOf("id" to 101.0))
        client.bookings.createBooking(bookingReq())
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/create") == true)
    }

    @Test fun `createBooking encodes date`() = runTest {
        mock.succeed(mapOf("id" to 101.0))
        client.bookings.createBooking(bookingReq())
        assertEquals("2025-09-15", mock.capturedBody?.get("date"))
    }

    @Test fun `createBooking encodes propertyId`() = runTest {
        mock.succeed(mapOf("id" to 101.0))
        client.bookings.createBooking(bookingReq())
        assertEquals(1004.0, mock.capturedBody?.get("propertyId"))
    }

    @Test fun `createBooking encodes couponCode`() = runTest {
        mock.succeed(mapOf("id" to 101.0))
        client.bookings.createBooking(bookingReq(coupon = "20POFF"))
        assertEquals("20POFF", mock.capturedBody?.get("couponCode"))
    }

    @Test fun `createBooking decodes id`() = runTest {
        mock.succeed(mapOf("id" to 16459.0, "status" to "OPEN"))
        val resp = client.bookings.createBooking(bookingReq())
        assertEquals(16459, resp.data?.id)
    }

    @Test fun `getBookingDetails sends GET`() = runTest {
        mock.succeed(mapOf("id" to 16459.0)); client.bookings.getBookingDetails(16459)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getBookingDetails interpolates id`() = runTest {
        mock.succeed(mapOf("id" to 16459.0)); client.bookings.getBookingDetails(16459)
        assertTrue(mock.capturedUrl?.contains("16459") == true)
    }

    @Test fun `cancelBooking sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.cancelBooking(16459)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `cancelBooking correct path`() = runTest {
        mock.succeedEmpty(); client.bookings.cancelBooking(16459)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/cancel") == true)
    }

    @Test fun `cancelBooking encodes reason`() = runTest {
        mock.succeedEmpty()
        client.bookings.cancelBooking(16459, reason = "Customer request")
        assertEquals("Customer request", mock.capturedBody?.get("reason"))
    }

    @Test fun `rescheduleBooking sends POST`() = runTest {
        mock.succeedEmpty()
        client.bookings.rescheduleBooking(16459, "2025-10-01", "11:00")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `rescheduleBooking encodes date`() = runTest {
        mock.succeedEmpty()
        client.bookings.rescheduleBooking(16459, "2025-10-01", "11:00")
        assertEquals("2025-10-01", mock.capturedBody?.get("date"))
    }

    @Test fun `rescheduleBooking correct path`() = runTest {
        mock.succeedEmpty()
        client.bookings.rescheduleBooking(16459, "2025-10-01", "11:00")
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/reschedule") == true)
    }

    @Test fun `assignCleaner sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.assignCleaner(16459, 789)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `assignCleaner encodes cleanerId`() = runTest {
        mock.succeedEmpty(); client.bookings.assignCleaner(16459, 789)
        assertEquals(789.0, mock.capturedBody?.get("cleanerId"))
    }

    @Test fun `removeAssignedCleaner sends DELETE`() = runTest {
        mock.succeedEmpty(); client.bookings.removeAssignedCleaner(16459)
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `removeAssignedCleaner correct path`() = runTest {
        mock.succeedEmpty(); client.bookings.removeAssignedCleaner(16459)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/cleaner") == true)
    }

    @Test fun `adjustHours sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.adjustHours(16459, 4.5)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `adjustHours encodes hours`() = runTest {
        mock.succeedEmpty(); client.bookings.adjustHours(16459, 4.5)
        assertEquals(4.5, mock.capturedBody?.get("hours"))
    }

    @Test fun `payExpenses sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.payExpenses(16459, 55)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `payExpenses encodes paymentMethodId`() = runTest {
        mock.succeedEmpty(); client.bookings.payExpenses(16459, 55)
        assertEquals(55.0, mock.capturedBody?.get("paymentMethodId"))
    }

    @Test fun `submitFeedback sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.submitFeedback(16459, 5)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `submitFeedback correct path`() = runTest {
        mock.succeedEmpty(); client.bookings.submitFeedback(16459, 5)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/feedback") == true)
    }

    @Test fun `submitFeedback encodes rating`() = runTest {
        mock.succeedEmpty(); client.bookings.submitFeedback(16459, 4)
        assertEquals(4.0, mock.capturedBody?.get("rating"))
    }

    @Test fun `submitFeedback encodes comment`() = runTest {
        mock.succeedEmpty()
        client.bookings.submitFeedback(16459, 5, comment = "Great service!")
        assertEquals("Great service!", mock.capturedBody?.get("comment"))
    }

    @Test fun `addTip sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.addTip(16459, 10.0, 55)
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `addTip encodes amount`() = runTest {
        mock.succeedEmpty(); client.bookings.addTip(16459, 15.0, 55)
        assertEquals(15.0, mock.capturedBody?.get("amount"))
    }

    @Test fun `getChat sends GET`() = runTest {
        mock.succeedList(); client.bookings.getChat(17142)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getChat correct path`() = runTest {
        mock.succeedList(); client.bookings.getChat(17142)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/17142/chat") == true)
    }

    @Test fun `sendMessage sends POST`() = runTest {
        mock.succeedEmpty(); client.bookings.sendMessage(17142, "On the way!")
        assertEquals("POST", mock.capturedMethod)
    }

    @Test fun `sendMessage encodes message`() = runTest {
        mock.succeedEmpty(); client.bookings.sendMessage(17142, "On the way!")
        assertEquals("On the way!", mock.capturedBody?.get("message"))
    }

    @Test fun `deleteMessage sends DELETE`() = runTest {
        mock.succeedEmpty(); client.bookings.deleteMessage(17142, "msg-001")
        assertEquals("DELETE", mock.capturedMethod)
    }

    @Test fun `deleteMessage correct path`() = runTest {
        mock.succeedEmpty(); client.bookings.deleteMessage(17142, "msg-001")
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/17142/chat/msg-001") == true)
    }

    @Test fun `assignChecklistToBooking sends PUT`() = runTest {
        mock.succeedEmpty(); client.bookings.assignChecklistToBooking(16459, 77)
        assertEquals("PUT", mock.capturedMethod)
    }

    @Test fun `assignChecklistToBooking correct path`() = runTest {
        mock.succeedEmpty(); client.bookings.assignChecklistToBooking(16459, 77)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/checklist/77") == true)
    }

    @Test fun `getBookingInspection sends GET`() = runTest {
        mock.succeed(emptyMap()); client.bookings.getBookingInspection(16459)
        assertEquals("GET", mock.capturedMethod)
    }

    @Test fun `getBookingInspectionDetails correct path`() = runTest {
        mock.succeed(emptyMap()); client.bookings.getBookingInspectionDetails(16459)
        assertTrue(mock.capturedUrl?.endsWith("/v1/bookings/16459/inspection/details") == true)
    }
}
