<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;

/**
 * Utility/reference endpoints used when building booking flows.
 */
final class OtherApi
{
    public function __construct(private readonly HttpClient $http) {}

    /** Return all available cleaning service types. */
    public function getServices(): ApiResponse
    {
        $raw = $this->http->get('/v1/services');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return all available booking plans for a given property. */
    public function getPlans(int $propertyId): ApiResponse
    {
        $raw = $this->http->get('/v1/plans', ['propertyId' => $propertyId]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Return the system-recommended number of cleaning hours.
     *
     * Use the result to pre-fill the 'hours' field in createBooking().
     *
     * @param int $propertyId   Property to check.
     * @param int $bathroomCount Number of bathrooms.
     * @param int $roomCount    Number of rooms.
     */
    public function getRecommendedHours(int $propertyId, int $bathroomCount, int $roomCount): ApiResponse
    {
        $raw = $this->http->get('/v1/recommended-hours', [
            'propertyId'    => $propertyId,
            'bathroomCount' => $bathroomCount,
            'roomCount'     => $roomCount,
        ]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Calculate the estimated price for a potential booking.
     *
     * @param array $request {
     *   @type int    $propertyId  Required
     *   @type int    $planId      Required
     *   @type float  $hours       Required
     *   @type string $couponCode  Optional
     *   @type int[]  $extras      Optional
     * }
     */
    public function calculateCost(array $request): ApiResponse
    {
        $raw = $this->http->post('/v1/cost-estimate', $request);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Return available add-on services for a given service type.
     * (e.g., inside fridge, inside oven, laundry)
     */
    public function getCleaningExtras(int $serviceId): ApiResponse
    {
        $raw = $this->http->get("/v1/cleaning-extras/{$serviceId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Find cleaners available for a specific property, date, and time slot.
     *
     * @param array $request {
     *   @type int    $propertyId Required
     *   @type string $date       Required — YYYY-MM-DD
     *   @type string $time       Required — HH:mm
     * }
     */
    public function getAvailableCleaners(array $request): ApiResponse
    {
        $raw = $this->http->post('/v1/available-cleaners', $request);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return all valid coupon codes. */
    public function getCoupons(): ApiResponse
    {
        $raw = $this->http->get('/v1/coupons');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
