package com.cleanster.xml;

import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.CleansterXmlException;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.Booking;
import com.cleanster.xml.model.XmlApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 47 tests covering all 17 Bookings endpoints plus Booking JAXB XML serialisation.
 */
class BookingsTest {

    private MockWebServer     server;
    private CleansterXmlClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        client = CleansterXmlClient.custom(server.url("/").toString(), "test-key", null);
    }

    @AfterEach
    void tearDown() throws Exception { server.shutdown(); }

    // helpers
    private void enqueueBooking(int id, String status) {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\","
                        + "\"data\":{\"id\":" + id + ",\"status\":\"" + status + "\","
                        + "\"date\":\"2025-09-15\",\"time\":\"09:00\",\"hours\":3.0,"
                        + "\"totalPrice\":120.0,\"currency\":\"USD\"}}")
                .addHeader("Content-Type", "application/json"));
    }

    private void enqueueList(String... ids) {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"message\":\"OK\",\"data\":[");
        for (int i = 0; i < ids.length; i++) {
            sb.append("{\"id\":").append(ids[i]).append(",\"status\":\"confirmed\"}");
            if (i < ids.length - 1) sb.append(",");
        }
        sb.append("]}");
        server.enqueue(new MockResponse().setBody(sb.toString())
                .addHeader("Content-Type", "application/json"));
    }

    // ─── listBookings ──────────────────────────────────────────────────────────

    @Test
    void listBookings_returnsAll() throws Exception {
        enqueueList("1", "2", "3");
        XmlApiResponse<List<Booking>> resp = client.bookings().listBookings();
        assertTrue(resp.isSuccess());
        assertEquals(3, resp.getData().size());
    }

    @Test
    void listBookings_emptyList() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":[]}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<List<Booking>> resp = client.bookings().listBookings();
        assertTrue(resp.getData().isEmpty());
    }

    @Test
    void listBookings_usesGET() throws Exception {
        enqueueList("1");
        client.bookings().listBookings();
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().endsWith("/bookings"));
    }

    // ─── getBooking ────────────────────────────────────────────────────────────

    @Test
    void getBooking_returnsBooking() throws Exception {
        enqueueBooking(10, "confirmed");
        XmlApiResponse<Booking> resp = client.bookings().getBooking(10);
        assertTrue(resp.isSuccess());
        assertEquals(10, resp.getData().getId());
        assertEquals("confirmed", resp.getData().getStatus());
    }

    @Test
    void getBooking_correctPath() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().getBooking(10);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/bookings/10"));
        assertEquals("GET", req.getMethod());
    }

    @Test
    void getBooking_notFound_throws() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not found\"}"));
        assertThrows(CleansterXmlException.class, () -> client.bookings().getBooking(9999));
    }

    @Test
    void getBooking_hasPrice() throws Exception {
        enqueueBooking(10, "confirmed");
        Booking b = client.bookings().getBooking(10).getData();
        assertEquals(120.0, b.getTotalPrice());
        assertEquals("USD", b.getCurrency());
    }

    // ─── createBooking ─────────────────────────────────────────────────────────

    @Test
    void createBooking_returnsPending() throws Exception {
        enqueueBooking(100, "pending");
        XmlApiResponse<Booking> resp = client.bookings().createBooking(
                "2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);
        assertEquals("pending", resp.getData().getStatus());
        assertEquals(100, resp.getData().getId());
    }

    @Test
    void createBooking_usesPOST() throws Exception {
        enqueueBooking(100, "pending");
        client.bookings().createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().endsWith("/bookings"));
    }

    @Test
    void createBooking_bodyContainsDate() throws Exception {
        enqueueBooking(100, "pending");
        client.bookings().createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);
        RecordedRequest req = server.takeRequest();
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("2025-09-15"));
        assertTrue(body.contains("09:00"));
    }

    @Test
    void createBooking_bodyContainsPropertyId() throws Exception {
        enqueueBooking(100, "pending");
        client.bookings().createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("1004"));
    }

    @Test
    void createBooking_serverError_throws() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{\"error\":\"Server error\"}"));
        assertThrows(CleansterXmlException.class, () ->
                client.bookings().createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55));
    }

    // ─── updateBooking ─────────────────────────────────────────────────────────

    @Test
    void updateBooking_usesPUT() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().updateBooking(10, java.util.Map.of("notes", "test"));
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertTrue(req.getPath().endsWith("/bookings/10"));
    }

    @Test
    void updateBooking_returnsUpdated() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Updated\","
                        + "\"data\":{\"id\":10,\"status\":\"confirmed\",\"notes\":\"clean carefully\"}}")
                .addHeader("Content-Type", "application/json"));
        XmlApiResponse<Booking> resp = client.bookings()
                .updateBooking(10, java.util.Map.of("notes", "clean carefully"));
        assertEquals("clean carefully", resp.getData().getNotes());
    }

    // ─── cancelBooking ─────────────────────────────────────────────────────────

    @Test
    void cancelBooking_usesDELETE() throws Exception {
        enqueueBooking(10, "cancelled");
        client.bookings().cancelBooking(10);
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
    }

    @Test
    void cancelBooking_returnsCancelled() throws Exception {
        enqueueBooking(10, "cancelled");
        Booking b = client.bookings().cancelBooking(10).getData();
        assertEquals("cancelled", b.getStatus());
    }

    @Test
    void cancelBooking_withReason_sendsPOST() throws Exception {
        enqueueBooking(10, "cancelled");
        client.bookings().cancelBooking(10, "No longer needed");
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getBody().readUtf8().contains("No longer needed"));
    }

    // ─── rescheduleBooking ─────────────────────────────────────────────────────

    @Test
    void rescheduleBooking_sendsNewDateTime() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().rescheduleBooking(10, "2025-10-01", "10:00");
        RecordedRequest req = server.takeRequest();
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("2025-10-01"));
        assertTrue(body.contains("10:00"));
    }

    @Test
    void rescheduleBooking_correctPath() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().rescheduleBooking(10, "2025-10-01", "10:00");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10/reschedule"));
    }

    // ─── confirmBooking ────────────────────────────────────────────────────────

    @Test
    void confirmBooking_usesPostAndCorrectPath() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().confirmBooking(10);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().contains("/bookings/10/confirm"));
    }

    // ─── completeBooking ───────────────────────────────────────────────────────

    @Test
    void completeBooking_usesPostAndCorrectPath() throws Exception {
        enqueueBooking(10, "completed");
        client.bookings().completeBooking(10);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().contains("/bookings/10/complete"));
    }

    // ─── disputeBooking ────────────────────────────────────────────────────────

    @Test
    void disputeBooking_sendsReason() throws Exception {
        enqueueBooking(10, "disputed");
        client.bookings().disputeBooking(10, "Cleaner didn't show up");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("Cleaner didn't show up"));
    }

    // ─── addTip ────────────────────────────────────────────────────────────────

    @Test
    void addTip_sendsAmount() throws Exception {
        enqueueBooking(10, "completed");
        client.bookings().addTip(10, 15.0);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("15.0"));
    }

    @Test
    void addTip_correctPath() throws Exception {
        enqueueBooking(10, "completed");
        client.bookings().addTip(10, 15.0);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10/tip"));
    }

    // ─── getReceipt ────────────────────────────────────────────────────────────

    @Test
    void getReceipt_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{\"total\":120.0}}")
                .addHeader("Content-Type", "application/json"));
        client.bookings().getReceipt(10);
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().contains("/bookings/10/receipt"));
    }

    // ─── leaveReview ───────────────────────────────────────────────────────────

    @Test
    void leaveReview_sendsRatingAndComment() throws Exception {
        enqueueBooking(10, "completed");
        client.bookings().leaveReview(10, 5, "Excellent!");
        RecordedRequest req = server.takeRequest();
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("5"));
        assertTrue(body.contains("Excellent!"));
    }

    // ─── listUpcomingBookings ──────────────────────────────────────────────────

    @Test
    void listUpcomingBookings_correctPath() throws Exception {
        enqueueList("11", "12");
        client.bookings().listUpcomingBookings();
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/bookings/upcoming"));
    }

    @Test
    void listUpcomingBookings_returnsList() throws Exception {
        enqueueList("11", "12");
        XmlApiResponse<List<Booking>> resp = client.bookings().listUpcomingBookings();
        assertEquals(2, resp.getData().size());
    }

    // ─── listPastBookings ──────────────────────────────────────────────────────

    @Test
    void listPastBookings_correctPath() throws Exception {
        enqueueList("3");
        client.bookings().listPastBookings();
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().endsWith("/bookings/past"));
    }

    // ─── applyCoupon ───────────────────────────────────────────────────────────

    @Test
    void applyCoupon_sendsCouponCode() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().applyCoupon(10, "100POFF");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("100POFF"));
    }

    @Test
    void applyCoupon_correctPath() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().applyCoupon(10, "50POFF");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10/apply-coupon"));
    }

    // ─── notifyCleaner ─────────────────────────────────────────────────────────

    @Test
    void notifyCleaner_sendsMessage() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().notifyCleaner(10, "Please bring eco supplies");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("eco supplies"));
    }

    // ─── getBookingChecklist ───────────────────────────────────────────────────

    @Test
    void getBookingChecklist_usesGET() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"OK\",\"data\":{}}")
                .addHeader("Content-Type", "application/json"));
        client.bookings().getBookingChecklist(10);
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().contains("/bookings/10/checklist"));
    }

    // ─── JAXB XML serialisation ─────────────────────────────────────────────────

    @Test
    void booking_toXml_containsId() {
        Booking b = new Booking();
        b.setId(42);
        b.setStatus("confirmed");
        b.setDate("2025-09-15");
        String xml = XmlConverter.toXml(b);
        assertTrue(xml.contains("<id>42</id>"));
        assertTrue(xml.contains("<status>confirmed</status>"));
        assertTrue(xml.contains("<date>2025-09-15</date>"));
    }

    @Test
    void booking_fromXml_roundTrip() {
        Booking original = new Booking();
        original.setId(77);
        original.setStatus("pending");
        original.setTotalPrice(99.99);
        original.setCurrency("USD");
        String  xml      = XmlConverter.toXml(original);
        Booking restored = XmlConverter.fromXml(xml, Booking.class);
        assertEquals(77,      restored.getId());
        assertEquals("pending", restored.getStatus());
        assertEquals(99.99,   restored.getTotalPrice(), 0.001);
        assertEquals("USD",   restored.getCurrency());
    }

    @Test
    void booking_toXml_isValidXml() {
        Booking b = new Booking();
        b.setId(1);
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(b)));
    }

    @Test
    void clientToXml_convenience_works() {
        Booking b = new Booking();
        b.setId(5);
        b.setStatus("confirmed");
        String xml = CleansterXmlClient.toXml(b);
        assertTrue(xml.contains("<id>5</id>"));
    }

    @Test
    void clientFromXml_convenience_works() {
        Booking original = new Booking();
        original.setId(6);
        String xml     = CleansterXmlClient.toXml(original);
        Booking restored = CleansterXmlClient.fromXml(xml, Booking.class);
        assertEquals(6, restored.getId());
    }

    @Test
    void booking_hoursAndRoomCount_roundTrip() {
        Booking b = new Booking();
        b.setHours(3.5);
        b.setRoomCount(4);
        b.setBathroomCount(2);
        b.setExtraSupplies(true);
        String  xml = XmlConverter.toXml(b);
        Booking r   = XmlConverter.fromXml(xml, Booking.class);
        assertEquals(3.5, r.getHours(), 0.001);
        assertEquals(4,   r.getRoomCount());
        assertEquals(2,   r.getBathroomCount());
        assertTrue(r.getExtraSupplies());
    }

    @Test
    void createBooking_bodyContainsPlanId() throws Exception {
        enqueueBooking(100, "pending");
        client.bookings().createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("planId"));
    }

    @Test
    void updateBooking_bodyContainsUpdate() throws Exception {
        enqueueBooking(10, "confirmed");
        client.bookings().updateBooking(10, java.util.Map.of("hours", 4.0));
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getBody().readUtf8().contains("4.0"));
    }

    @Test
    void cancelBooking_withReason_bodyContainsPath() throws Exception {
        enqueueBooking(10, "cancelled");
        client.bookings().cancelBooking(10, "Changed my mind");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10"));
    }

    @Test
    void leaveReview_correctPath() throws Exception {
        enqueueBooking(10, "completed");
        client.bookings().leaveReview(10, 4, "Good job");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10/review"));
        assertEquals("POST", req.getMethod());
    }

    @Test
    void disputeBooking_correctPath() throws Exception {
        enqueueBooking(10, "disputed");
        client.bookings().disputeBooking(10, "Missed spot");
        RecordedRequest req = server.takeRequest();
        assertTrue(req.getPath().contains("/bookings/10/dispute"));
    }

    @Test
    void confirmBooking_returnsConfirmed() throws Exception {
        enqueueBooking(10, "confirmed");
        Booking b = client.bookings().confirmBooking(10).getData();
        assertEquals("confirmed", b.getStatus());
    }

    @Test
    void completeBooking_returnsCompleted() throws Exception {
        enqueueBooking(10, "completed");
        Booking b = client.bookings().completeBooking(10).getData();
        assertEquals("completed", b.getStatus());
    }

    @Test
    void booking_cancelReasonAndCancelledAt_roundTrip() {
        Booking b = new Booking();
        b.setId(50);
        b.setCancelReason("Schedule conflict");
        b.setCancelledAt("2025-09-15T10:00:00Z");
        String  xml = XmlConverter.toXml(b);
        Booking r   = XmlConverter.fromXml(xml, Booking.class);
        assertEquals("Schedule conflict",      r.getCancelReason());
        assertEquals("2025-09-15T10:00:00Z",   r.getCancelledAt());
    }
}
