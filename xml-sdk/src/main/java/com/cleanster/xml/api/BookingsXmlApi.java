package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Booking;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Bookings API — full lifecycle management for cleaning bookings.
 *
 * <h3>Endpoints (17)</h3>
 * <ol>
 *   <li>GET    /bookings                          — list bookings</li>
 *   <li>GET    /bookings/{id}                     — get booking</li>
 *   <li>POST   /bookings                          — create booking</li>
 *   <li>PUT    /bookings/{id}                     — update booking</li>
 *   <li>DELETE /bookings/{id}                     — cancel booking</li>
 *   <li>POST   /bookings/{id}/reschedule          — reschedule booking</li>
 *   <li>POST   /bookings/{id}/confirm             — confirm booking</li>
 *   <li>POST   /bookings/{id}/complete            — mark complete</li>
 *   <li>POST   /bookings/{id}/dispute             — dispute booking</li>
 *   <li>POST   /bookings/{id}/tip                 — add tip</li>
 *   <li>GET    /bookings/{id}/receipt             — get receipt</li>
 *   <li>POST   /bookings/{id}/review              — leave review</li>
 *   <li>GET    /bookings/upcoming                 — upcoming bookings</li>
 *   <li>GET    /bookings/past                     — past bookings</li>
 *   <li>POST   /bookings/{id}/apply-coupon        — apply coupon</li>
 *   <li>POST   /bookings/{id}/notify-cleaner      — notify cleaner</li>
 *   <li>GET    /bookings/{id}/checklist           — booking checklist</li>
 * </ol>
 */
public class BookingsXmlApi {

    private final XmlHttpClient http;

    public BookingsXmlApi(XmlHttpClient http) { this.http = http; }

    /** List all bookings for the authenticated user. */
    public XmlApiResponse<List<Booking>> listBookings() {
        String json = http.get("/bookings");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Booking>>>(){}.getType());
    }

    /** Retrieve a single booking by ID. */
    public XmlApiResponse<Booking> getBooking(int bookingId) {
        String json = http.get("/bookings/" + bookingId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Create a new booking. */
    public XmlApiResponse<Booking> createBooking(Map<String, Object> body) {
        String json = http.post("/bookings", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Convenience overload — build the request map from typed parameters. */
    public XmlApiResponse<Booking> createBooking(String date, String time, int propertyId,
                                                  int planId, double hours,
                                                  int roomCount, int bathroomCount,
                                                  boolean extraSupplies, int paymentMethodId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("date",            date);
        body.put("time",            time);
        body.put("propertyId",      propertyId);
        body.put("planId",          planId);
        body.put("hours",           hours);
        body.put("roomCount",       roomCount);
        body.put("bathroomCount",   bathroomCount);
        body.put("extraSupplies",   extraSupplies);
        body.put("paymentMethodId", paymentMethodId);
        return createBooking(body);
    }

    /** Update a booking's mutable fields. */
    public XmlApiResponse<Booking> updateBooking(int bookingId, Map<String, Object> body) {
        String json = http.put("/bookings/" + bookingId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Cancel a booking. */
    public XmlApiResponse<Booking> cancelBooking(int bookingId) {
        String json = http.delete("/bookings/" + bookingId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Cancel a booking with a reason. */
    public XmlApiResponse<Booking> cancelBooking(int bookingId, String reason) {
        Map<String, Object> body = reason != null ? Map.of("reason", reason) : Map.of();
        String json = http.post("/bookings/" + bookingId + "/cancel", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Reschedule a booking to a new date/time. */
    public XmlApiResponse<Booking> rescheduleBooking(int bookingId, String newDate, String newTime) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("date", newDate);
        body.put("time", newTime);
        String json = http.post("/bookings/" + bookingId + "/reschedule", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Confirm a booking. */
    public XmlApiResponse<Booking> confirmBooking(int bookingId) {
        String json = http.post("/bookings/" + bookingId + "/confirm", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Mark a booking as complete. */
    public XmlApiResponse<Booking> completeBooking(int bookingId) {
        String json = http.post("/bookings/" + bookingId + "/complete", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Dispute a booking. */
    public XmlApiResponse<Booking> disputeBooking(int bookingId, String reason) {
        String json = http.post("/bookings/" + bookingId + "/dispute", Map.of("reason", reason));
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Add a tip to a completed booking. */
    public XmlApiResponse<Booking> addTip(int bookingId, double tipAmount) {
        String json = http.post("/bookings/" + bookingId + "/tip", Map.of("amount", tipAmount));
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Retrieve the receipt for a booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getReceipt(int bookingId) {
        String json = http.get("/bookings/" + bookingId + "/receipt");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Leave a review for a completed booking. */
    public XmlApiResponse<Booking> leaveReview(int bookingId, int rating, String comment) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("rating",  rating);
        body.put("comment", comment);
        String json = http.post("/bookings/" + bookingId + "/review", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** List upcoming bookings. */
    public XmlApiResponse<List<Booking>> listUpcomingBookings() {
        String json = http.get("/bookings/upcoming");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Booking>>>(){}.getType());
    }

    /** List past bookings. */
    public XmlApiResponse<List<Booking>> listPastBookings() {
        String json = http.get("/bookings/past");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Booking>>>(){}.getType());
    }

    /** Apply a coupon code to a booking. */
    public XmlApiResponse<Booking> applyCoupon(int bookingId, String couponCode) {
        String json = http.post("/bookings/" + bookingId + "/apply-coupon",
                Map.of("couponCode", couponCode));
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Send a notification to the assigned cleaner. */
    public XmlApiResponse<Booking> notifyCleaner(int bookingId, String message) {
        String json = http.post("/bookings/" + bookingId + "/notify-cleaner",
                Map.of("message", message));
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Retrieve the checklist for a specific booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getBookingChecklist(int bookingId) {
        String json = http.get("/bookings/" + bookingId + "/checklist");
        return http.fromJson(json, XmlApiResponse.class);
    }
}
