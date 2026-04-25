package com.cleanster.xml;

import com.cleanster.xml.api.BookingsXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Booking;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingsTest {

    private XmlHttpClient  http;
    private BookingsXmlApi api;

    private static final String LIST_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":100,\"status\":\"scheduled\"}]}";
    private static final String BK_JSON   = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":100,\"status\":\"scheduled\"}}";
    private static final String OK_JSON   = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new BookingsXmlApi(http);
    }

    // ── listBookings ───────────────────────────────────────────────────────────

    @Test void listBookings_noFilters_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/bookings");
        api.listBookings();
        verify(http).get("/v1/bookings");
    }

    @Test void listBookings_withPageNo_callsGetWithParam() {
        doReturn(LIST_JSON).when(http).get("/v1/bookings?pageNo=2");
        api.listBookings(2, null);
        verify(http).get("/v1/bookings?pageNo=2");
    }

    @Test void listBookings_withStatus_callsGetWithParam() {
        doReturn(LIST_JSON).when(http).get("/v1/bookings?status=scheduled");
        api.listBookings(null, "scheduled");
        verify(http).get("/v1/bookings?status=scheduled");
    }

    @Test void listBookings_returnsList() {
        doReturn(LIST_JSON).when(http).get("/v1/bookings");
        XmlApiResponse<List<Booking>> resp = api.listBookings();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
        assertEquals(100, (int) resp.getData().get(0).getId());
    }

    // ── createBooking ──────────────────────────────────────────────────────────

    @Test void createBooking_callsPost_v1BookingsCreate() {
        doReturn(BK_JSON).when(http).post(eq("/v1/bookings/create"), any());
        api.createBooking("2025-09-15", "09:00", 1, 2, 3.0, 2, 1, false, 10);
        verify(http).post(eq("/v1/bookings/create"), any());
    }

    @Test void createBooking_returnsParsedBooking() {
        doReturn(BK_JSON).when(http).post(eq("/v1/bookings/create"), any());
        XmlApiResponse<Booking> resp = api.createBooking("2025-09-15", "09:00", 1, 2, 3.0, 2, 1, false, 10);
        assertTrue(resp.isSuccess());
        assertEquals(100, (int) resp.getData().getId());
    }

    // ── getBooking ─────────────────────────────────────────────────────────────

    @Test void getBooking_callsGetWithId() {
        doReturn(BK_JSON).when(http).get("/v1/bookings/100");
        api.getBooking(100);
        verify(http).get("/v1/bookings/100");
    }

    @Test void getBooking_returnsParsed() {
        doReturn(BK_JSON).when(http).get("/v1/bookings/100");
        assertEquals("scheduled", api.getBooking(100).getData().getStatus());
    }

    // ── cancelBooking ──────────────────────────────────────────────────────────

    @Test void cancelBooking_callsPostWithCancelPath() {
        doReturn(BK_JSON).when(http).post(eq("/v1/bookings/100/cancel"), any());
        api.cancelBooking(100, "Not needed");
        verify(http).post(eq("/v1/bookings/100/cancel"), any());
    }

    @Test void cancelBooking_noReason_callsPost() {
        doReturn(BK_JSON).when(http).post(eq("/v1/bookings/100/cancel"), any());
        api.cancelBooking(100);
        verify(http).post(eq("/v1/bookings/100/cancel"), any());
    }

    // ── rescheduleBooking ──────────────────────────────────────────────────────

    @Test void rescheduleBooking_callsPostWithReschedulePath() {
        doReturn(BK_JSON).when(http).post(eq("/v1/bookings/100/reschedule"), any());
        api.rescheduleBooking(100, "2025-10-01", "10:00");
        verify(http).post(eq("/v1/bookings/100/reschedule"), any());
    }

    // ── assignCleaner / removeAssignedCleaner ──────────────────────────────────

    @Test void assignCleaner_callsPostWithCleanerPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/cleaner"), any());
        api.assignCleaner(100, 42);
        verify(http).post(eq("/v1/bookings/100/cleaner"), any());
    }

    @Test void removeAssignedCleaner_callsDeleteWithCleanerPath() {
        doReturn(OK_JSON).when(http).delete("/v1/bookings/100/cleaner");
        api.removeAssignedCleaner(100);
        verify(http).delete("/v1/bookings/100/cleaner");
    }

    // ── adjustHours ────────────────────────────────────────────────────────────

    @Test void adjustHours_callsPostWithHoursPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/hours"), any());
        api.adjustHours(100, 4.0);
        verify(http).post(eq("/v1/bookings/100/hours"), any());
    }

    // ── payExpenses ────────────────────────────────────────────────────────────

    @Test void payExpenses_callsPostWithExpensesPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/expenses"), any());
        api.payExpenses(100, 10);
        verify(http).post(eq("/v1/bookings/100/expenses"), any());
    }

    // ── inspection ─────────────────────────────────────────────────────────────

    @Test void getBookingInspection_callsGetWithInspectionPath() {
        doReturn(OK_JSON).when(http).get("/v1/bookings/100/inspection");
        api.getBookingInspection(100);
        verify(http).get("/v1/bookings/100/inspection");
    }

    @Test void getBookingInspectionDetails_callsGetWithDetailsPath() {
        doReturn(OK_JSON).when(http).get("/v1/bookings/100/inspection/details");
        api.getBookingInspectionDetails(100);
        verify(http).get("/v1/bookings/100/inspection/details");
    }

    // ── assignChecklistToBooking ───────────────────────────────────────────────

    @Test void assignChecklistToBooking_callsPutWithPath() {
        doReturn(OK_JSON).when(http).put(eq("/v1/bookings/100/checklist/5"), any());
        api.assignChecklistToBooking(100, 5);
        verify(http).put(eq("/v1/bookings/100/checklist/5"), any());
    }

    // ── feedback / tip ─────────────────────────────────────────────────────────

    @Test void submitFeedback_callsPostWithFeedbackPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/feedback"), any());
        api.submitFeedback(100, 5, "Great job!");
        verify(http).post(eq("/v1/bookings/100/feedback"), any());
    }

    @Test void addTip_callsPostWithTipPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/tip"), any());
        api.addTip(100, 10.00, 10);
        verify(http).post(eq("/v1/bookings/100/tip"), any());
    }

    // ── chat ───────────────────────────────────────────────────────────────────

    @Test void getChat_callsGetWithChatPath() {
        doReturn(OK_JSON).when(http).get("/v1/bookings/100/chat");
        api.getChat(100);
        verify(http).get("/v1/bookings/100/chat");
    }

    @Test void sendMessage_callsPostWithChatPath() {
        doReturn(OK_JSON).when(http).post(eq("/v1/bookings/100/chat"), any());
        api.sendMessage(100, "Hello cleaner!");
        verify(http).post(eq("/v1/bookings/100/chat"), any());
    }

    @Test void deleteMessage_callsDeleteWithChatMessagePath() {
        doReturn(OK_JSON).when(http).delete("/v1/bookings/100/chat/msg-5");
        api.deleteMessage(100, "msg-5");
        verify(http).delete("/v1/bookings/100/chat/msg-5");
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void booking_toXml_isValidXml() {
        Booking b = new Booking(); b.setId(1); b.setStatus("scheduled");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(b)));
    }

    @Test void booking_fromXml_roundTrip() {
        Booking o = new Booking(); o.setId(100); o.setStatus("completed");
        Booking r = XmlConverter.fromXml(XmlConverter.toXml(o), Booking.class);
        assertEquals(100, (int) r.getId());
        assertEquals("completed", r.getStatus());
    }
}
