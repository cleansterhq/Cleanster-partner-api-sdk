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

    /** Return all available booking plans for a given property, optionally filtered by subcategory. */
    public function getPlans(int $propertyId, ?int $subcatId = null): ApiResponse
    {
        $query = ['propertyId' => $propertyId];
        if ($subcatId !== null) {
            $query['subcatId'] = $subcatId;
        }
        $raw = $this->http->get('/v1/plans', $query);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Return the system-recommended number of cleaning hours.
     *
     * Use the result to pre-fill the 'hours' field in createBooking().
     *
     * @param int      $propertyId    Property to check.
     * @param int      $bathroomCount Number of bathrooms.
     * @param int      $roomCount     Number of rooms.
     * @param int|null $subcatId      Optional service subcategory ID.
     */
    public function getRecommendedHours(int $propertyId, int $bathroomCount, int $roomCount, ?int $subcatId = null): ApiResponse
    {
        $query = [
            'propertyId'    => $propertyId,
            'bathroomCount' => $bathroomCount,
            'roomCount'     => $roomCount,
        ];
        if ($subcatId !== null) {
            $query['subcatId'] = $subcatId;
        }
        $raw = $this->http->get('/v1/recommended-hours', $query);
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
    public function getCostEstimate(array $request): ApiResponse
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

    /**
     * List all cleaners, with optional status and search filters.
     *
     * @param string|null $status Filter by status ('active', 'inactive', 'pending').
     * @param string|null $search Partial match against cleaner name or email.
     */
    public function listCleaners(?string $status = null, ?string $search = null): ApiResponse
    {
        $params = array_filter(['status' => $status, 'search' => $search]);
        $path = '/v1/cleaners' . ($params ? '?' . http_build_query($params) : '');
        $raw = $this->http->get($path);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Retrieve a single cleaner by their ID.
     *
     * @param int $cleanerId The cleaner's unique ID.
     */
    public function getCleaner(int $cleanerId): ApiResponse
    {
        $raw = $this->http->get("/v1/cleaners/{$cleanerId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * List service tasks, filterable by property and service type. Supports pagination.
     *
     * @param int      $propertyId Property to check.
     * @param int      $serviceId  Service type ID.
     * @param int|null $pageNo     Optional page number.
     * @param int|null $pageSize   Optional page size.
     */
    public function getTasks(int $propertyId, int $serviceId, ?int $pageNo = null, ?int $pageSize = null): ApiResponse
    {
        $query = ['propertyId' => $propertyId, 'serviceId' => $serviceId];
        if ($pageNo !== null) {
            $query['pageNo'] = $pageNo;
        }
        if ($pageSize !== null) {
            $query['pageSize'] = $pageSize;
        }
        $raw = $this->http->get('/v1/tasks', $query);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return the subcategories available under a given service type. */
    public function getSubcategories(int $serviceId): ApiResponse
    {
        $raw = $this->http->get("/v1/services/{$serviceId}/subcategories");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
