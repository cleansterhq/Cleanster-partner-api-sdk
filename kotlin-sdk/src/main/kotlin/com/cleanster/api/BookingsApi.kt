package com.cleanster.api

import com.cleanster.CleansterClient
import com.cleanster.model.*

/** API methods for the full booking lifecycle. */
class BookingsApi internal constructor(private val client: CleansterClient) {

    /**
     * List all bookings. Requires a status filter and supports pagination.
     *
     * @param status Required. `COMPLETED`, `CANCELLED`, or `UPCOMING`.
     * @param pageNo 1-based page number.
     */
    suspend fun getBookings(
        status: String,
        pageNo: Int? = null,
    ): ApiResponse<PagedBookings> {
        val params = buildMap<String, String> {
            put("status", status)
            pageNo?.let { put("pageNo", "$it") }
        }
        return client.request(method = "GET", path = "/v1/bookings", queryParams = params)
    }

    /** Create a new booking. */
    suspend fun createBooking(request: CreateBookingRequest): ApiResponse<Booking> = client.request(
        method = "POST",
        path   = "/v1/bookings/create",
        body   = request,
    )

    /** Get full details for a single booking. */
    suspend fun getBookingDetails(bookingId: Int): ApiResponse<Booking> = client.request(
        method = "GET",
        path   = "/v1/bookings/$bookingId",
    )

    /** Cancel a booking with an optional reason. */
    suspend fun cancelBooking(bookingId: Int, reason: String? = null): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/cancel",
        body   = CancelBookingRequest(reason = reason),
    )

    /** Reschedule a booking to a new date and time. */
    suspend fun rescheduleBooking(bookingId: Int, date: String, time: String): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/reschedule",
        body   = RescheduleBookingRequest(date = date, time = time),
    )

    /**
     * Manually assign a cleaner to a booking.
     * The cleaner must be in the property's cleaner pool.
     */
    suspend fun assignCleaner(bookingId: Int, cleanerId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/cleaner",
        body   = AssignCleanerRequest(cleanerId = cleanerId),
    )

    /** Remove the assigned cleaner. The booking returns to `OPEN` status. */
    suspend fun removeAssignedCleaner(bookingId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/bookings/$bookingId/cleaner",
    )

    /** Adjust the duration of a booking. */
    suspend fun adjustHours(bookingId: Int, hours: Double): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/hours",
        body   = AdjustHoursRequest(hours = hours),
    )

    /** Pay outstanding balance-on-completion expenses. */
    suspend fun payExpenses(bookingId: Int, paymentMethodId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/expenses",
        body   = PayExpensesRequest(paymentMethodId = paymentMethodId),
    )

    /**
     * Retrieve a signed inspection report URL. Opening it displays before photos, after photos,
     * checklist photo evidence, and property problems.
     */
    suspend fun getBookingInspection(bookingId: Int): ApiResponse<String> = client.request(
        method = "GET",
        path   = "/v1/bookings/$bookingId/inspection",
    )

    /**
     * @deprecated The details endpoint does not exist. Use [getBookingInspection].
     */
    @Deprecated("The details endpoint does not exist. Use getBookingInspection instead.")
    suspend fun getBookingInspectionDetails(bookingId: Int): ApiResponse<String> =
        getBookingInspection(bookingId)

    /** Override the property's default checklist for a specific booking only. */
    suspend fun assignChecklistToBooking(bookingId: Int, checklistId: Int): ApiResponse<Map<String, Any>> = client.request(
        method = "PUT",
        path   = "/v1/bookings/$bookingId/checklist/$checklistId",
    )

    /**
     * Submit a star rating and optional comment after a completed booking.
     *
     * @param rating 1–5 star rating.
     * @param comment Optional written feedback.
     */
    suspend fun submitFeedback(
        bookingId: Int,
        rating:    Int,
        comment:   String? = null,
    ): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/feedback",
        body   = SubmitFeedbackRequest(rating = rating, comment = comment),
    )

    /** Add a gratuity for the cleaner. Must be called within 72 hours of booking completion. */
    suspend fun addTip(
        bookingId:       Int,
        amount:          Double,
        paymentMethodId: Int,
    ): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/tip",
        body   = AddTipRequest(amount = amount, paymentMethodId = paymentMethodId),
    )

    /** Retrieve the chat thread for a booking. Available ±24 hours of booking start time. */
    suspend fun getChat(bookingId: Int): ApiResponse<List<ChatMessage>> = client.request(
        method = "GET",
        path   = "/v1/bookings/$bookingId/chat",
    )

    /** Send a chat message for a booking. */
    suspend fun sendMessage(bookingId: Int, message: String): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/chat",
        body   = SendMessageRequest(message = message),
    )

    /** Delete a chat message by its ID. */
    suspend fun deleteMessage(bookingId: Int, messageId: String): ApiResponse<Map<String, Any>> = client.request(
        method = "DELETE",
        path   = "/v1/bookings/$bookingId/chat/$messageId",
    )

    /** Update task quantities for a booking. */
    suspend fun updateTask(bookingId: Int, tasks: List<TaskQuantity>): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/tasks",
        body   = UpdateTaskRequest(tasks = tasks),
    )

    /** Update the total square footage for a booking. */
    suspend fun updateSqft(bookingId: Int, totalSqFt: Double): ApiResponse<Map<String, Any>> = client.request(
        method = "POST",
        path   = "/v1/bookings/$bookingId/sqft",
        body   = UpdateSqftRequest(totalSqFt = totalSqFt),
    )
}
