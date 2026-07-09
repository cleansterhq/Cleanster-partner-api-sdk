package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Booking;
import com.cleanster.xml.model.PagedBookings;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Bookings API - full lifecycle management for cleaning appointments.
 *
 * <h3>Endpoints (19)</h3>
 * <ol>
 *   <li>GET    /v1/bookings                                - list bookings</li>
 *   <li>POST   /v1/bookings/create                         - create booking</li>
 *   <li>GET    /v1/bookings/{id}                           - get booking</li>
 *   <li>POST   /v1/bookings/{id}/cancel                    - cancel booking</li>
 *   <li>POST   /v1/bookings/{id}/reschedule                - reschedule booking</li>
 *   <li>POST   /v1/bookings/{id}/cleaner                   - assign cleaner</li>
 *   <li>DELETE /v1/bookings/{id}/cleaner                   - remove cleaner</li>
 *   <li>POST   /v1/bookings/{id}/hours                     - adjust hours</li>
 *   <li>POST   /v1/bookings/{id}/expenses                  - pay expenses</li>
 *   <li>GET    /v1/bookings/{id}/inspection                - get inspection</li>
 *   <li>GET    /v1/bookings/{id}/inspection/details        - get inspection details</li>
 *   <li>PUT    /v1/bookings/{id}/checklist/{checklistId}   - assign checklist</li>
 *   <li>POST   /v1/bookings/{id}/feedback                  - submit feedback</li>
 *   <li>POST   /v1/bookings/{id}/tip                       - add tip</li>
 *   <li>GET    /v1/bookings/{id}/chat                      - get chat messages</li>
 *   <li>POST   /v1/bookings/{id}/chat                      - send chat message</li>
 *   <li>DELETE /v1/bookings/{id}/chat/{messageId}          - delete chat message</li>
 *   <li>POST   /v1/bookings/{id}/tasks                     - update task quantities</li>
 *   <li>POST   /v1/bookings/{id}/sqft                      - update total square footage</li>
 * </ol>
 */
public class BookingsXmlApi {

    private final XmlHttpClient http;

    public BookingsXmlApi(XmlHttpClient http) { this.http = http; }

    /**
     * List all bookings, filtered by required status, with optional pagination.
     *
     * @param status  Required. One of {@code COMPLETED}, {@code CANCELLED}, {@code UPCOMING}.
     * @param pageNo  1-based page number (optional).
     */
    public XmlApiResponse<PagedBookings> listBookings(String status, Integer pageNo) {
        StringBuilder path = new StringBuilder("/v1/bookings");
        List<String> params = new ArrayList<>();
        params.add("status=" + status);
        if (pageNo != null) params.add("pageNo=" + pageNo);
        path.append("?").append(String.join("&", params));
        String json = http.get(path.toString());
        return http.fromJson(json, new TypeToken<XmlApiResponse<PagedBookings>>(){}.getType());
    }

    /** List all bookings with a required status filter (no pagination). */
    public XmlApiResponse<PagedBookings> listBookings(String status) {
        return listBookings(status, null);
    }

    /**
     * Schedule a new cleaning appointment.
     *
     * @param date             YYYY-MM-DD
     * @param time             HH:mm (24-hour)
     * @param propertyId       Property ID
     * @param planId           Plan ID (from getPlans)
     * @param hours            Duration in hours
     * @param roomCount        Number of rooms
     * @param bathroomCount    Number of bathrooms
     * @param extraSupplies    Include cleaning supplies?
     * @param paymentMethodId  Payment method ID
     */
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
        String json = http.post("/v1/bookings/create", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Retrieve a single booking by ID. */
    public XmlApiResponse<Booking> getBooking(int bookingId) {
        String json = http.get("/v1/bookings/" + bookingId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Cancel a booking with an optional reason. */
    public XmlApiResponse<Booking> cancelBooking(int bookingId, String reason) {
        Map<String, Object> body = (reason != null && !reason.isBlank())
                ? Map.of("reason", reason) : Map.of();
        String json = http.post("/v1/bookings/" + bookingId + "/cancel", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Cancel a booking (no reason). */
    public XmlApiResponse<Booking> cancelBooking(int bookingId) {
        return cancelBooking(bookingId, null);
    }

    /** Reschedule a booking to a new date and time. */
    public XmlApiResponse<Booking> rescheduleBooking(int bookingId, String newDate, String newTime) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("date", newDate);
        body.put("time", newTime);
        String json = http.post("/v1/bookings/" + bookingId + "/reschedule", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Booking>>(){}.getType());
    }

    /** Manually assign a specific cleaner to a booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse assignCleaner(int bookingId, int cleanerId) {
        String json = http.post("/v1/bookings/" + bookingId + "/cleaner",
                Map.of("cleanerId", cleanerId));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Remove the currently assigned cleaner from a booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse removeAssignedCleaner(int bookingId) {
        String json = http.delete("/v1/bookings/" + bookingId + "/cleaner");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Change the number of hours for a booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse adjustHours(int bookingId, double hours) {
        String json = http.post("/v1/bookings/" + bookingId + "/hours",
                Map.of("hours", hours));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Pay outstanding expenses for a completed booking (within 72h of completion). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse payExpenses(int bookingId, int paymentMethodId) {
        String json = http.post("/v1/bookings/" + bookingId + "/expenses",
                Map.of("paymentMethodId", paymentMethodId));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Get the inspection report for a completed booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getBookingInspection(int bookingId) {
        String json = http.get("/v1/bookings/" + bookingId + "/inspection");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Get detailed inspection information for a completed booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getBookingInspectionDetails(int bookingId) {
        String json = http.get("/v1/bookings/" + bookingId + "/inspection/details");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Attach a checklist to a booking (overrides the property default). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse assignChecklistToBooking(int bookingId, int checklistId) {
        String json = http.put("/v1/bookings/" + bookingId + "/checklist/" + checklistId, null);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Submit a star rating and optional comment after a booking completes. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse submitFeedback(int bookingId, int rating, String comment) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("rating", rating);
        if (comment != null) body.put("comment", comment);
        String json = http.post("/v1/bookings/" + bookingId + "/feedback", body);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Add a tip for the cleaner (within 72h of booking completion). */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse addTip(int bookingId, double amount, int paymentMethodId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount",          amount);
        body.put("paymentMethodId", paymentMethodId);
        String json = http.post("/v1/bookings/" + bookingId + "/tip", body);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Retrieve all chat messages for a booking thread. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse getChat(int bookingId) {
        String json = http.get("/v1/bookings/" + bookingId + "/chat");
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Post a chat message in a booking thread. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse sendMessage(int bookingId, String message) {
        String json = http.post("/v1/bookings/" + bookingId + "/chat",
                Map.of("message", message));
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Delete a specific chat message. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse deleteMessage(int bookingId, String messageId) {
        String json = http.delete("/v1/bookings/" + bookingId + "/chat/" + messageId);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /**
     * Update task quantities for a booking.
     *
     * @param bookingId  The booking ID.
     * @param tasks      List of maps, each with keys: id, quantity.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse updateTask(int bookingId, List<Map<String, Object>> tasks) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tasks", tasks);
        String json = http.post("/v1/bookings/" + bookingId + "/tasks", body);
        return http.fromJson(json, XmlApiResponse.class);
    }

    /** Update the total square footage for a booking. */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse updateSqft(int bookingId, double totalSqFt) {
        String json = http.post("/v1/bookings/" + bookingId + "/sqft",
                Map.of("totalSqFt", totalSqFt));
        return http.fromJson(json, XmlApiResponse.class);
    }
}
