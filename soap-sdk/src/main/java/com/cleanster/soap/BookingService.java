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
        JsonNode root = transport.post("/v1/bookings/create", request);
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
        body.put("cleanerId", cleanerId);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/cleaner", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Booking.class);
    }

    public ApiResponse removeAssignedCleaner(long bookingId) {
        JsonNode root = transport.delete("/v1/bookings/" + bookingId + "/cleaner");
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse adjustHours(long bookingId, double hours) {
        Map<String, Object> body = new HashMap<>();
        body.put("hours", hours);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/hours", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse payExpenses(long bookingId, long paymentMethodId) {
        Map<String, Object> body = new HashMap<>();
        body.put("payment_method_id", paymentMethodId);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/expenses", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public JsonNode getBookingInspection(long bookingId) {
        return transport.extractData(transport.get("/v1/bookings/" + bookingId + "/inspection"));
    }

    /**
     * Returns the signed inspection-report URL. Opening it displays before and
     * after photos, checklist photo evidence, and property problems.
     */
    public String getBookingInspectionUrl(long bookingId) {
        return getBookingInspection(bookingId).asText();
    }

    /**
     * @deprecated Use {@link #getBookingInspection(long)} or
     * {@link #getBookingInspectionUrl(long)}. There is no separate details
     * endpoint; this compatibility alias calls {@code /inspection}.
     */
    @Deprecated
    public JsonNode getBookingInspectionDetails(long bookingId) {
        return getBookingInspection(bookingId);
    }

    public ApiResponse assignChecklistToBooking(long bookingId, long checklistId) {
        JsonNode root = transport.put("/v1/bookings/" + bookingId + "/checklist/" + checklistId, null);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse submitFeedback(long bookingId, int rating, String comment) {
        Map<String, Object> body = new HashMap<>();
        body.put("rating", rating);
        if (comment != null) body.put("comment", comment);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/feedback", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse addTip(long bookingId, double amount, long paymentMethodId) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("payment_method_id", paymentMethodId);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/tip", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public List<ChatMessage> getChat(long bookingId) {
        JsonNode root = transport.get("/v1/bookings/" + bookingId + "/chat");
        JsonNode data = transport.extractData(root);
        List<ChatMessage> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, ChatMessage.class));
            }
        }
        return list;
    }

    public ChatMessage sendMessage(long bookingId, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/chat", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), ChatMessage.class);
    }

    public ApiResponse deleteMessage(long bookingId, String messageId) {
        JsonNode root = transport.delete("/v1/bookings/" + bookingId + "/chat/" + messageId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse updateTask(long bookingId, List<Map<String, Object>> tasks) {
        Map<String, Object> body = new HashMap<>();
        body.put("tasks", tasks);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/tasks", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse updateSqft(long bookingId, double totalSqFt) {
        Map<String, Object> body = new HashMap<>();
        body.put("totalSqFt", totalSqFt);
        JsonNode root = transport.post("/v1/bookings/" + bookingId + "/sqft", body);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }
}
