using System.Text.Json;
using Cleanster.Models;

namespace Cleanster.Api;

/// <summary>Manages the full lifecycle of cleaning appointments.</summary>
public sealed class BookingsApi
{
    private readonly ICleansterHttpClient _http;
    internal BookingsApi(ICleansterHttpClient http) => _http = http;

    // -------------------------------------------------------------------------
    // Listing and retrieval
    // -------------------------------------------------------------------------

    /// <summary>Retrieve a paginated list of bookings.</summary>
    /// <param name="pageNo">Optional page number (1-based).</param>
    /// <param name="status">Optional status filter: OPEN | CLEANER_ASSIGNED | COMPLETED | CANCELLED | REMOVED</param>
    public async Task<ApiResponse<List<Booking>>> GetBookingsAsync(
        int?   pageNo = null, string? status = null, CancellationToken ct = default)
    {
        var query = new Dictionary<string, string>();
        if (pageNo.HasValue)  query["pageNo"] = pageNo.Value.ToString();
        if (status is not null) query["status"] = status;
        var root = await _http.GetAsync("/v1/bookings", query.Count > 0 ? query : null, ct);
        return JsonHelper.ParseList<Booking>(root);
    }

    /// <summary>Schedule a new cleaning appointment.</summary>
    /// <param name="date">Booking date (YYYY-MM-DD).</param>
    /// <param name="time">Start time (HH:mm 24-hour).</param>
    /// <param name="propertyId">Property ID from <c>Properties.ListAsync</c>.</param>
    /// <param name="roomCount">Number of rooms.</param>
    /// <param name="bathroomCount">Number of bathrooms.</param>
    /// <param name="planId">Plan ID from <c>Other.GetPlansAsync</c>.</param>
    /// <param name="hours">Duration from <c>Other.GetRecommendedHoursAsync</c>.</param>
    /// <param name="extraSupplies">Whether to include cleaning supplies.</param>
    /// <param name="paymentMethodId">Payment method ID.</param>
    /// <param name="couponCode">Optional discount coupon code.</param>
    /// <param name="extras">Optional add-on service IDs.</param>
    public async Task<ApiResponse<Booking>> CreateBookingAsync(
        string   date, string  time, int propertyId, int roomCount, int bathroomCount,
        int      planId, double hours, bool extraSupplies, int paymentMethodId,
        string?  couponCode = null, IEnumerable<int>? extras = null,
        CancellationToken ct = default)
    {
        var body = new Dictionary<string, object?>
        {
            ["date"]            = date,
            ["time"]            = time,
            ["propertyId"]      = propertyId,
            ["roomCount"]       = roomCount,
            ["bathroomCount"]   = bathroomCount,
            ["planId"]          = planId,
            ["hours"]           = hours,
            ["extraSupplies"]   = extraSupplies,
            ["paymentMethodId"] = paymentMethodId,
        };
        if (couponCode is not null)                   body["couponCode"] = couponCode;
        if (extras is not null)                       body["extras"]     = extras.ToArray();
        var root = await _http.PostAsync("/v1/bookings/create", body, ct);
        return JsonHelper.ParseSingle<Booking>(root);
    }

    /// <summary>Return full details of a specific booking.</summary>
    public async Task<ApiResponse<Booking>> GetBookingDetailsAsync(int bookingId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/bookings/{bookingId}", ct: ct);
        return JsonHelper.ParseSingle<Booking>(root);
    }

    // -------------------------------------------------------------------------
    // Lifecycle mutations
    // -------------------------------------------------------------------------

    /// <summary>Cancel a booking.</summary>
    public async Task<ApiResponse<JsonElement>> CancelBookingAsync(
        int bookingId, string? reason = null, CancellationToken ct = default)
    {
        object? body = reason is { Length: > 0 } r ? new { reason = r } : null;
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/cancel", body, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Move a booking to a new date and time.</summary>
    public async Task<ApiResponse<JsonElement>> RescheduleBookingAsync(
        int bookingId, string date, string time, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/reschedule",
            new { date, time }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Manually assign a specific cleaner to a booking.</summary>
    public async Task<ApiResponse<JsonElement>> AssignCleanerAsync(
        int bookingId, int cleanerId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/cleaner",
            new { cleanerId }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Remove the currently assigned cleaner from a booking.</summary>
    public async Task<ApiResponse<JsonElement>> RemoveAssignedCleanerAsync(
        int bookingId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/bookings/{bookingId}/cleaner", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Update the number of cleaning hours for a booking.</summary>
    public async Task<ApiResponse<JsonElement>> AdjustHoursAsync(
        int bookingId, double hours, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/hours", new { hours }, ct);
        return JsonHelper.ParseRaw(root);
    }

    // -------------------------------------------------------------------------
    // Post-completion actions
    // -------------------------------------------------------------------------

    /// <summary>Pay outstanding expenses within 72 hours of booking completion.</summary>
    public async Task<ApiResponse<JsonElement>> PayExpensesAsync(
        int bookingId, int paymentMethodId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/expenses",
            new { paymentMethodId }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return the inspection report for a completed booking.</summary>
    public async Task<ApiResponse<JsonElement>> GetBookingInspectionAsync(
        int bookingId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/bookings/{bookingId}/inspection", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Return detailed inspection information for a completed booking.</summary>
    public async Task<ApiResponse<JsonElement>> GetBookingInspectionDetailsAsync(
        int bookingId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/bookings/{bookingId}/inspection/details", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Override the property's default checklist for this specific booking.</summary>
    public async Task<ApiResponse<JsonElement>> AssignChecklistToBookingAsync(
        int bookingId, int checklistId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/checklist/{checklistId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Submit a star rating (1–5) and optional comment after a booking completes.</summary>
    public async Task<ApiResponse<JsonElement>> SubmitFeedbackAsync(
        int bookingId, int rating, string? comment = null, CancellationToken ct = default)
    {
        object body = comment is { Length: > 0 } c
            ? new { rating, comment = c }
            : new { rating };
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/feedback", body, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Add a tip within 72 hours of booking completion.</summary>
    public async Task<ApiResponse<JsonElement>> AddTipAsync(
        int bookingId, double amount, int paymentMethodId, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/tip",
            new { amount, paymentMethodId }, ct);
        return JsonHelper.ParseRaw(root);
    }

    // -------------------------------------------------------------------------
    // Chat
    // -------------------------------------------------------------------------

    /// <summary>Retrieve all chat messages in a booking thread.</summary>
    public async Task<ApiResponse<JsonElement>> GetChatAsync(
        int bookingId, CancellationToken ct = default)
    {
        var root = await _http.GetAsync($"/v1/bookings/{bookingId}/chat", ct: ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Post a message to a booking's chat thread.</summary>
    public async Task<ApiResponse<JsonElement>> SendMessageAsync(
        int bookingId, string message, CancellationToken ct = default)
    {
        var root = await _http.PostAsync($"/v1/bookings/{bookingId}/chat", new { message }, ct);
        return JsonHelper.ParseRaw(root);
    }

    /// <summary>Delete a specific chat message from a booking thread.</summary>
    public async Task<ApiResponse<JsonElement>> DeleteMessageAsync(
        int bookingId, string messageId, CancellationToken ct = default)
    {
        var root = await _http.DeleteAsync($"/v1/bookings/{bookingId}/chat/{messageId}", ct: ct);
        return JsonHelper.ParseRaw(root);
    }
}
