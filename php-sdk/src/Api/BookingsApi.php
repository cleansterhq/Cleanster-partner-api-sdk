<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;
use Cleanster\Models\Booking;

/**
 * Manages the full lifecycle of cleaning appointments.
 */
final class BookingsApi
{
    public function __construct(private readonly HttpClient $http) {}

    // -------------------------------------------------------------------------
    // Listing and retrieval
    // -------------------------------------------------------------------------

    /**
     * Retrieve a paginated list of bookings.
     *
     * @param int|null    $pageNo Optional page number (1-based).
     * @param string|null $status Optional status filter.
     *                            One of: OPEN | CLEANER_ASSIGNED | COMPLETED | CANCELLED | REMOVED
     *
     * @return ApiResponse<Booking[]>
     */
    public function getBookings(?int $pageNo = null, ?string $status = null): ApiResponse
    {
        $query = [];
        if ($pageNo !== null) {
            $query['pageNo'] = $pageNo;
        }
        if ($status !== null) {
            $query['status'] = $status;
        }
        $raw = $this->http->get('/v1/bookings', $query);
        return $this->wrapList($raw, Booking::class);
    }

    /**
     * Schedule a new cleaning appointment.
     *
     * @param array $request {
     *   @type string $date            Required — YYYY-MM-DD
     *   @type string $time            Required — HH:mm (24-hour)
     *   @type int    $propertyId      Required
     *   @type int    $roomCount       Required
     *   @type int    $bathroomCount   Required
     *   @type int    $planId          Required — from getPlans()
     *   @type float  $hours           Required — from getRecommendedHours()
     *   @type bool   $extraSupplies   Required
     *   @type int    $paymentMethodId Required
     *   @type string $couponCode      Optional
     *   @type int[]  $extras          Optional
     * }
     * @return ApiResponse<Booking>
     */
    public function createBooking(array $request): ApiResponse
    {
        $raw = $this->http->post('/v1/bookings/create', $request);
        return $this->wrapModel($raw, Booking::class);
    }

    /**
     * Return full details of a specific booking.
     *
     * @return ApiResponse<Booking>
     */
    public function getBookingDetails(int $bookingId): ApiResponse
    {
        $raw = $this->http->get("/v1/bookings/{$bookingId}");
        return $this->wrapModel($raw, Booking::class);
    }

    // -------------------------------------------------------------------------
    // Lifecycle mutations
    // -------------------------------------------------------------------------

    /**
     * Cancel a booking.
     *
     * @param int         $bookingId Booking to cancel.
     * @param string|null $reason    Optional cancellation reason.
     */
    public function cancelBooking(int $bookingId, ?string $reason = null): ApiResponse
    {
        $body = [];
        if ($reason !== null && $reason !== '') {
            $body['reason'] = $reason;
        }
        $raw = $this->http->post("/v1/bookings/{$bookingId}/cancel", $body ?: null);
        return $this->wrapRaw($raw);
    }

    /**
     * Move a booking to a new date and time.
     *
     * @param int    $bookingId Booking to reschedule.
     * @param string $date      New date (YYYY-MM-DD).
     * @param string $time      New time (HH:mm).
     */
    public function rescheduleBooking(int $bookingId, string $date, string $time): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/reschedule", [
            'date' => $date,
            'time' => $time,
        ]);
        return $this->wrapRaw($raw);
    }

    /** Manually assign a specific cleaner to a booking. */
    public function assignCleaner(int $bookingId, int $cleanerId): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/cleaner", ['cleanerId' => $cleanerId]);
        return $this->wrapRaw($raw);
    }

    /** Remove the currently assigned cleaner from a booking. */
    public function removeAssignedCleaner(int $bookingId): ApiResponse
    {
        $raw = $this->http->delete("/v1/bookings/{$bookingId}/cleaner");
        return $this->wrapRaw($raw);
    }

    /** Update the number of hours for a booking. */
    public function adjustHours(int $bookingId, float $hours): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/hours", ['hours' => $hours]);
        return $this->wrapRaw($raw);
    }

    // -------------------------------------------------------------------------
    // Post-completion actions
    // -------------------------------------------------------------------------

    /** Pay outstanding expenses within 72 hours of booking completion. */
    public function payExpenses(int $bookingId, int $paymentMethodId): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/expenses", [
            'paymentMethodId' => $paymentMethodId,
        ]);
        return $this->wrapRaw($raw);
    }

    /** Return the inspection report for a completed booking. */
    public function getBookingInspection(int $bookingId): ApiResponse
    {
        $raw = $this->http->get("/v1/bookings/{$bookingId}/inspection");
        return $this->wrapRaw($raw);
    }

    /** Return detailed inspection information for a completed booking. */
    public function getBookingInspectionDetails(int $bookingId): ApiResponse
    {
        $raw = $this->http->get("/v1/bookings/{$bookingId}/inspection/details");
        return $this->wrapRaw($raw);
    }

    /**
     * Attach a checklist to a specific booking, overriding the property's default.
     */
    public function assignChecklistToBooking(int $bookingId, int $checklistId): ApiResponse
    {
        $raw = $this->http->put("/v1/bookings/{$bookingId}/checklist/{$checklistId}");
        return $this->wrapRaw($raw);
    }

    /**
     * Submit a star rating (1–5) and optional comment after a booking completes.
     *
     * @param int         $bookingId Completed booking ID.
     * @param int         $rating    Star rating 1–5.
     * @param string|null $comment   Optional comment.
     */
    public function submitFeedback(int $bookingId, int $rating, ?string $comment = null): ApiResponse
    {
        $body = ['rating' => $rating];
        if ($comment !== null && $comment !== '') {
            $body['comment'] = $comment;
        }
        $raw = $this->http->post("/v1/bookings/{$bookingId}/feedback", $body);
        return $this->wrapRaw($raw);
    }

    /** Add a tip for the cleaner within 72 hours of booking completion. */
    public function addTip(int $bookingId, float $amount, int $paymentMethodId): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/tip", [
            'amount'          => $amount,
            'paymentMethodId' => $paymentMethodId,
        ]);
        return $this->wrapRaw($raw);
    }

    // -------------------------------------------------------------------------
    // Chat
    // -------------------------------------------------------------------------

    /** Retrieve all chat messages in a booking thread. */
    public function getChat(int $bookingId): ApiResponse
    {
        $raw = $this->http->get("/v1/bookings/{$bookingId}/chat");
        return $this->wrapRaw($raw);
    }

    /** Post a chat message to a booking thread. */
    public function sendMessage(int $bookingId, string $message): ApiResponse
    {
        $raw = $this->http->post("/v1/bookings/{$bookingId}/chat", ['message' => $message]);
        return $this->wrapRaw($raw);
    }

    /** Delete a specific chat message from a booking thread. */
    public function deleteMessage(int $bookingId, string $messageId): ApiResponse
    {
        $raw = $this->http->delete("/v1/bookings/{$bookingId}/chat/{$messageId}");
        return $this->wrapRaw($raw);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private function wrapModel(array $raw, string $class): ApiResponse
    {
        return new ApiResponse(
            $raw['status'] ?? 200,
            $raw['message'] ?? 'OK',
            new $class($raw['data'] ?? [])
        );
    }

    private function wrapList(array $raw, string $class): ApiResponse
    {
        $items = array_map(fn(array $item) => new $class($item), $raw['data'] ?? []);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $items);
    }

    private function wrapRaw(array $raw): ApiResponse
    {
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
