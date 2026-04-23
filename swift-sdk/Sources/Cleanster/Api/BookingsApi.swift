import Foundation

/// API methods for the full booking lifecycle.
public final class BookingsApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all bookings. Optionally filter by status and paginate.
    ///
    /// - Parameters:
    ///   - pageNo: 1-based page number. Defaults to 1.
    ///   - status: Filter by booking status: `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED`.
    public func getBookings(pageNo: Int? = nil, status: String? = nil) async throws -> ApiResponse<AnyCodable> {
        var query: [URLQueryItem] = []
        if let p = pageNo { query.append(URLQueryItem(name: "pageNo", value: "\(p)")) }
        if let s = status  { query.append(URLQueryItem(name: "status", value: s)) }
        return try await client.requestRaw(
            method: "GET",
            path: "/v1/bookings",
            queryItems: query.isEmpty ? nil : query
        )
    }

    /// Create a new booking.
    public func createBooking(_ request: CreateBookingRequest) async throws -> ApiResponse<Booking> {
        return try await client.request(method: "POST", path: "/v1/bookings/create", body: request)
    }

    /// Get full details for a single booking.
    public func getBookingDetails(_ bookingId: Int) async throws -> ApiResponse<Booking> {
        return try await client.request(method: "GET", path: "/v1/bookings/\(bookingId)")
    }

    /// Cancel a booking with an optional reason.
    public func cancelBooking(_ bookingId: Int, reason: String? = nil) async throws -> ApiResponse<AnyCodable> {
        let body = CancelBookingRequest(reason: reason)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/cancel",
            body: body
        )
    }

    /// Reschedule a booking to a new date and time.
    public func rescheduleBooking(_ bookingId: Int, date: String, time: String) async throws -> ApiResponse<AnyCodable> {
        let body = RescheduleBookingRequest(date: date, time: time)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/reschedule",
            body: body
        )
    }

    /// Manually assign a cleaner to a booking.
    ///
    /// The cleaner must be in the property's cleaner pool.
    public func assignCleaner(_ bookingId: Int, cleanerId: Int) async throws -> ApiResponse<AnyCodable> {
        let body = AssignCleanerRequest(cleanerId: cleanerId)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/cleaner",
            body: body
        )
    }

    /// Remove the assigned cleaner. The booking returns to `OPEN` status.
    public func removeAssignedCleaner(_ bookingId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "DELETE", path: "/v1/bookings/\(bookingId)/cleaner")
    }

    /// Adjust the duration of a booking.
    public func adjustHours(_ bookingId: Int, hours: Double) async throws -> ApiResponse<AnyCodable> {
        let body = AdjustHoursRequest(hours: hours)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/hours",
            body: body
        )
    }

    /// Pay outstanding balance-on-completion expenses.
    public func payExpenses(_ bookingId: Int, paymentMethodId: Int) async throws -> ApiResponse<AnyCodable> {
        let body = PayExpensesRequest(paymentMethodId: paymentMethodId)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/expenses",
            body: body
        )
    }

    /// Retrieve the post-booking inspection report.
    public func getBookingInspection(_ bookingId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/bookings/\(bookingId)/inspection")
    }

    /// Retrieve detailed inspection data including photos and notes.
    public func getBookingInspectionDetails(_ bookingId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/bookings/\(bookingId)/inspection/details")
    }

    /// Override the property's default checklist for a specific booking only.
    public func assignChecklistToBooking(_ bookingId: Int, checklistId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "PUT",
            path: "/v1/bookings/\(bookingId)/checklist/\(checklistId)"
        )
    }

    /// Submit a star rating and optional comment after a completed booking.
    ///
    /// - Parameters:
    ///   - rating: 1–5 star rating.
    ///   - comment: Optional written feedback.
    public func submitFeedback(
        _ bookingId: Int,
        rating: Int,
        comment: String? = nil
    ) async throws -> ApiResponse<AnyCodable> {
        let body = SubmitFeedbackRequest(rating: rating, comment: comment)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/feedback",
            body: body
        )
    }

    /// Add a gratuity for the cleaner. Must be called within 72 hours of booking completion.
    public func addTip(
        _ bookingId: Int,
        amount: Double,
        paymentMethodId: Int
    ) async throws -> ApiResponse<AnyCodable> {
        let body = AddTipRequest(amount: amount, paymentMethodId: paymentMethodId)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/tip",
            body: body
        )
    }

    /// Retrieve the chat thread for a booking.
    ///
    /// Chat is available ±24 hours around the booking start time.
    public func getChat(_ bookingId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/bookings/\(bookingId)/chat")
    }

    /// Send a chat message for a booking.
    public func sendMessage(_ bookingId: Int, message: String) async throws -> ApiResponse<AnyCodable> {
        let body = SendMessageRequest(message: message)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/bookings/\(bookingId)/chat",
            body: body
        )
    }

    /// Delete a chat message by its ID.
    public func deleteMessage(_ bookingId: Int, messageId: String) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "DELETE",
            path: "/v1/bookings/\(bookingId)/chat/\(messageId)"
        )
    }
}
