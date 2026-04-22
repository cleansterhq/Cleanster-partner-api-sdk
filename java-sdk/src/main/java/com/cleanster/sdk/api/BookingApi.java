package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * API operations for bookings: create, list, get, cancel, reschedule, cleaner assignment,
 * hours adjustment, chat, feedback, tip, expenses, and inspection.
 */
public class BookingApi {

    private final HttpClient httpClient;

    public BookingApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Get upcoming and past bookings with optional filters.
     *
     * @param pageNo  Page number (starts at 1), or null for page 1
     * @param status  Filter by status: OPEN, CLEANER_ASSIGNED, COMPLETED, CANCELLED, REMOVED, or null for all
     * @return API response containing list of bookings
     */
    public ApiResponse<Object> getBookings(Integer pageNo, String status) {
        StringBuilder path = new StringBuilder("/v1/bookings?");
        if (pageNo != null) path.append("pageNo=").append(pageNo).append("&");
        if (status != null && !status.isBlank()) path.append("status=").append(status).append("&");
        String url = path.toString().replaceAll("[?&]$", "");
        return httpClient.get(url, new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Create (schedule) a new booking.
     *
     * @param request Booking details
     * @return API response with the created booking
     */
    public ApiResponse<Booking> createBooking(CreateBookingRequest request) {
        return httpClient.post("/v1/bookings/create", request,
                new TypeReference<ApiResponse<Booking>>() {});
    }

    /**
     * Get detailed information for a specific booking.
     *
     * @param bookingId The booking ID
     * @return API response with booking details
     */
    public ApiResponse<Booking> getBookingDetails(int bookingId) {
        return httpClient.get("/v1/bookings/" + bookingId,
                new TypeReference<ApiResponse<Booking>>() {});
    }

    /**
     * Assign a cleaner to a booking.
     *
     * @param bookingId The booking ID
     * @param request   Cleaner assignment details
     * @return API response
     */
    public ApiResponse<Object> assignCleaner(int bookingId, CleanerAssignmentRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/cleaner", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Remove the assigned cleaner from a booking.
     *
     * @param bookingId The booking ID
     * @return API response
     */
    public ApiResponse<Object> removeAssignedCleaner(int bookingId) {
        return httpClient.delete("/v1/bookings/" + bookingId + "/cleaner",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Adjust the hours for a booking.
     *
     * @param bookingId The booking ID
     * @param request   New hours
     * @return API response
     */
    public ApiResponse<Object> adjustHours(int bookingId, AdjustHoursRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/hours", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Reschedule a booking to a new date/time.
     *
     * @param bookingId The booking ID
     * @param request   New date and time
     * @return API response
     */
    public ApiResponse<Object> rescheduleBooking(int bookingId, RescheduleBookingRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/reschedule", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Pay expenses for a booking.
     * Can be called anytime before completion and up to 72 hours after completion.
     *
     * @param bookingId The booking ID
     * @param request   Expense details
     * @return API response
     */
    public ApiResponse<Object> payExpenses(int bookingId, PayExpensesRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/expenses", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Cancel a booking.
     *
     * @param bookingId The booking ID
     * @param request   Cancellation reason
     * @return API response
     */
    public ApiResponse<Object> cancelBooking(int bookingId, CancelBookingRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/cancel", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get the inspection report for a booking.
     *
     * @param bookingId The booking ID
     * @return API response with inspection data
     */
    public ApiResponse<Object> getBookingInspection(int bookingId) {
        return httpClient.get("/v1/bookings/" + bookingId + "/inspection",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Get detailed inspection information for a booking.
     *
     * @param bookingId The booking ID
     * @return API response with detailed inspection data
     */
    public ApiResponse<Object> getBookingInspectionDetails(int bookingId) {
        return httpClient.get("/v1/bookings/" + bookingId + "/inspection/details",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Assign a checklist to a booking.
     *
     * @param bookingId   The booking ID
     * @param checklistId The checklist ID to assign
     * @return API response
     */
    public ApiResponse<Object> assignChecklistToBooking(int bookingId, int checklistId) {
        return httpClient.put("/v1/bookings/" + bookingId + "/checklist/" + checklistId, null,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Submit feedback (rating and comment) for a completed booking.
     *
     * @param bookingId The booking ID
     * @param request   Feedback details
     * @return API response
     */
    public ApiResponse<Object> submitFeedback(int bookingId, FeedbackRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/feedback", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Add a tip to a completed booking.
     * Can only be called within 72 hours after the booking is marked as completed.
     *
     * @param bookingId The booking ID
     * @param request   Tip details
     * @return API response
     */
    public ApiResponse<Object> addTip(int bookingId, TipRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/tip", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    // ---- Chat ----

    /**
     * Retrieve chat messages for a specific booking.
     * Chat is available ±24 hours around the booking. No time restriction for hanging state jobs.
     *
     * @param bookingId The booking ID
     * @return API response with chat messages
     */
    public ApiResponse<Object> getChat(int bookingId) {
        return httpClient.get("/v1/bookings/" + bookingId + "/chat",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Send a new chat message on a booking.
     *
     * @param bookingId The booking ID
     * @param request   Message content
     * @return API response
     */
    public ApiResponse<Object> sendMessage(int bookingId, SendMessageRequest request) {
        return httpClient.post("/v1/bookings/" + bookingId + "/chat", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Delete a chat message from a booking.
     *
     * @param bookingId The booking ID
     * @param messageId The message ID to delete
     * @return API response
     */
    public ApiResponse<Object> deleteMessage(int bookingId, String messageId) {
        return httpClient.delete("/v1/bookings/" + bookingId + "/chat/" + messageId,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
