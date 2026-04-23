package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all booking-related SOAP operations. */
public class BookingService {

    private final SOAPTransport transport;

    public BookingService(SOAPTransport transport) {
        this.transport = transport;
    }

    public Booking getBooking(long bookingId) {
        JsonNode root = transport.get("/v1/bookings/" + bookingId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Booking.class);
    }

    public List<Booking> listBookings(ListBookingsRequest request) {
        StringBuilder path = new StringBuilder("/v1/bookings?");
        if (request.getStatus() != null)
            path.append("status=").append(request.getStatus()).append("&");
        if (request.getPropertyId() != null)
            path.append("property_id=").append(request.getPropertyId()).append("&");
        if (request.getPage() != null)
            path.append("page=").append(request.getPage()).append("&");
        if (request.getPerPage() != null)
            path.append("per_page=").append(request.getPerPage()).append("&");

        JsonNode root = transport.get(path.toString());
        JsonNode data = transport.extractData(root);
        List<Booking> bookings = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                bookings.add(transport.getObjectMapper().convertValue(node, Booking.class));
            }
        }
        return bookings;
    }

    public Booking createBooking(CreateBookingRequest request) {
        JsonNode root = transport.post("/v1/bookings", request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Booking.class);
    }

    public ApiResponse cancelBooking(long bookingId, String reason) {
        Map<String, Object> body = new HashMap<>();
        if (reason != null) body.put("reason", reason);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/cancel", body);
        JsonNode data = transport.extractData(root);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        String message = data.has("message") ? data.get("message").asText() : "OK";
        return new ApiResponse(status, message);
    }

    public Booking rescheduleBooking(RescheduleBookingRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("scheduled_at", request.getScheduledAt());
        if (request.getDurationHours() != null)
            body.put("duration_hours", request.getDurationHours());
        JsonNode root = transport.post(
                "/v1/bookings/" + request.getBookingId() + "/reschedule", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Booking.class);
    }

    public Booking assignCleaner(long bookingId, long cleanerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("cleaner_id", cleanerId);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/assign-cleaner", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Booking.class);
    }
}
