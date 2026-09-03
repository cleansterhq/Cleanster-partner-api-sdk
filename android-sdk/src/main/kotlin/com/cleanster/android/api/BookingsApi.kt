package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface BookingsService {
    @GET("v1/bookings")
    suspend fun getBookings(
        @Query("status") status: String,
        @Query("pageNo") pageNo: Int? = null,
    ): ApiResponse<PagedBookings>

    @GET("v1/bookings/{bookingId}")
    suspend fun getBookingDetails(@Path("bookingId") bookingId: Int): ApiResponse<Booking>

    @POST("v1/bookings/create")
    suspend fun createBooking(@Body body: CreateBookingRequest): ApiResponse<Booking>

    @POST("v1/bookings/{bookingId}/cancel")
    suspend fun cancelBooking(
        @Path("bookingId") bookingId: Int,
        @Body body: CancelBookingRequest,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/reschedule")
    suspend fun rescheduleBooking(
        @Path("bookingId") bookingId: Int,
        @Body body: RescheduleBookingRequest,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/cleaner")
    suspend fun assignCleaner(
        @Path("bookingId") bookingId: Int,
        @Body body: AssignCleanerRequest,
    ): ApiResponse<Any>

    @DELETE("v1/bookings/{bookingId}/cleaner")
    suspend fun removeAssignedCleaner(@Path("bookingId") bookingId: Int): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/hours")
    suspend fun adjustHours(
        @Path("bookingId") bookingId: Int,
        @Body body: AdjustHoursRequest,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/expenses")
    suspend fun payExpenses(
        @Path("bookingId") bookingId: Int,
        @Body body: PayExpensesRequest,
    ): ApiResponse<Any>

    @GET("v1/bookings/{bookingId}/inspection")
    suspend fun getBookingInspection(@Path("bookingId") bookingId: Int): ApiResponse<String>

    @PUT("v1/bookings/{bookingId}/checklist/{checklistId}")
    suspend fun assignChecklistToBooking(
        @Path("bookingId") bookingId: Int,
        @Path("checklistId") checklistId: Int,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/feedback")
    suspend fun submitFeedback(
        @Path("bookingId") bookingId: Int,
        @Body body: FeedbackRequest,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/tip")
    suspend fun addTip(
        @Path("bookingId") bookingId: Int,
        @Body body: TipRequest,
    ): ApiResponse<Any>

    @GET("v1/bookings/{bookingId}/chat")
    suspend fun getChat(@Path("bookingId") bookingId: Int): ApiResponse<List<ChatMessage>>

    @POST("v1/bookings/{bookingId}/chat")
    suspend fun sendMessage(
        @Path("bookingId") bookingId: Int,
        @Body body: SendMessageRequest,
    ): ApiResponse<Any>

    @DELETE("v1/bookings/{bookingId}/chat/{messageId}")
    suspend fun deleteMessage(
        @Path("bookingId") bookingId: Int,
        @Path("messageId") messageId: String,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/tasks")
    suspend fun updateTask(
        @Path("bookingId") bookingId: Int,
        @Body body: UpdateTaskRequest,
    ): ApiResponse<Any>

    @POST("v1/bookings/{bookingId}/sqft")
    suspend fun updateSqft(
        @Path("bookingId") bookingId: Int,
        @Body body: UpdateSqftRequest,
    ): ApiResponse<Any>
}

class BookingsApi(retrofit: Retrofit) {
    private val service = retrofit.create(BookingsService::class.java)

    suspend fun getBookings(status: String, pageNo: Int? = null) =
        wrap { service.getBookings(status, pageNo) }

    suspend fun getBookingDetails(bookingId: Int) =
        wrap { service.getBookingDetails(bookingId) }

    suspend fun createBooking(request: CreateBookingRequest) =
        wrap { service.createBooking(request) }

    suspend fun cancelBooking(bookingId: Int, reason: String? = null) =
        wrap { service.cancelBooking(bookingId, CancelBookingRequest(reason)) }

    suspend fun rescheduleBooking(bookingId: Int, date: String, time: String) =
        wrap { service.rescheduleBooking(bookingId, RescheduleBookingRequest(date, time)) }

    suspend fun assignCleaner(bookingId: Int, cleanerId: Int) =
        wrap { service.assignCleaner(bookingId, AssignCleanerRequest(cleanerId)) }

    suspend fun removeAssignedCleaner(bookingId: Int) =
        wrap { service.removeAssignedCleaner(bookingId) }

    suspend fun adjustHours(bookingId: Int, hours: Double) =
        wrap { service.adjustHours(bookingId, AdjustHoursRequest(hours)) }

    suspend fun payExpenses(bookingId: Int, paymentMethodId: Int) =
        wrap { service.payExpenses(bookingId, PayExpensesRequest(paymentMethodId)) }

    suspend fun getBookingInspection(bookingId: Int) =
        wrap { service.getBookingInspection(bookingId) }

    /**
     * @deprecated The details endpoint does not exist. Use [getBookingInspection] instead.
     */
    @Deprecated("The details endpoint does not exist. Use getBookingInspection instead.")
    suspend fun getBookingInspectionDetails(bookingId: Int) =
        getBookingInspection(bookingId)

    suspend fun assignChecklistToBooking(bookingId: Int, checklistId: Int) =
        wrap { service.assignChecklistToBooking(bookingId, checklistId) }

    suspend fun submitFeedback(bookingId: Int, rating: Int, comment: String? = null) =
        wrap { service.submitFeedback(bookingId, FeedbackRequest(rating, comment)) }

    suspend fun addTip(bookingId: Int, amount: Double, paymentMethodId: Int) =
        wrap { service.addTip(bookingId, TipRequest(amount, paymentMethodId)) }

    suspend fun getChat(bookingId: Int) =
        wrap { service.getChat(bookingId) }

    suspend fun sendMessage(bookingId: Int, message: String) =
        wrap { service.sendMessage(bookingId, SendMessageRequest(message)) }

    suspend fun deleteMessage(bookingId: Int, messageId: String) =
        wrap { service.deleteMessage(bookingId, messageId) }

    suspend fun updateTask(bookingId: Int, tasks: List<TaskQuantity>) =
        wrap { service.updateTask(bookingId, UpdateTaskRequest(tasks)) }

    suspend fun updateSqft(bookingId: Int, totalSqFt: Double) =
        wrap { service.updateSqft(bookingId, UpdateSqftRequest(totalSqFt)) }
}
