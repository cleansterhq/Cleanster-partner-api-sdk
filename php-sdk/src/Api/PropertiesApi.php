<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;
use Cleanster\Models\Property;

/**
 * Manages physical cleaning locations.
 */
final class PropertiesApi
{
    public function __construct(private readonly HttpClient $http) {}

    /**
     * Return all properties, optionally filtered by service type.
     *
     * @param int|null $serviceId Pass null to return all service types.
     * @return ApiResponse<Property[]>
     */
    public function listProperties(?int $serviceId = null): ApiResponse
    {
        $query = [];
        if ($serviceId !== null) {
            $query['serviceId'] = $serviceId;
        }
        $raw   = $this->http->get('/v1/properties', $query);
        $items = array_map(fn(array $p) => new Property($p), $raw['data'] ?? []);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $items);
    }

    /**
     * Add a new property to the partner account.
     *
     * @return ApiResponse<Property>
     */
    public function addProperty(array $request): ApiResponse
    {
        $raw = $this->http->post('/v1/properties', $request);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Property($raw['data'] ?? []));
    }

    /**
     * Return details of a specific property.
     *
     * @return ApiResponse<Property>
     */
    public function getProperty(int $propertyId): ApiResponse
    {
        $raw = $this->http->get("/v1/properties/{$propertyId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Property($raw['data'] ?? []));
    }

    /**
     * Replace all fields of an existing property.
     *
     * @return ApiResponse<Property>
     */
    public function updateProperty(int $propertyId, array $request): ApiResponse
    {
        $raw = $this->http->put("/v1/properties/{$propertyId}", $request);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new Property($raw['data'] ?? []));
    }

    /** Update freeform additional information for a property. */
    public function updateAdditionalInformation(int $propertyId, array $data): ApiResponse
    {
        $raw = $this->http->put("/v1/properties/{$propertyId}/additional-information", $data);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Toggle a property's active state. */
    public function enableOrDisableProperty(int $propertyId, bool $enabled): ApiResponse
    {
        $raw = $this->http->post("/v1/properties/{$propertyId}/enable-disable", ['enabled' => $enabled]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Permanently delete a property. */
    public function deleteProperty(int $propertyId): ApiResponse
    {
        $raw = $this->http->delete("/v1/properties/{$propertyId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return all cleaners assigned to a property. */
    public function getPropertyCleaners(int $propertyId): ApiResponse
    {
        $raw = $this->http->get("/v1/properties/{$propertyId}/cleaners");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Add a cleaner to a property's default cleaner pool. */
    public function assignCleanerToProperty(int $propertyId, int $cleanerId): ApiResponse
    {
        $raw = $this->http->post("/v1/properties/{$propertyId}/cleaners", ['cleanerId' => $cleanerId]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Remove a cleaner from a property's default cleaner pool. */
    public function unassignCleanerFromProperty(int $propertyId, int $cleanerId): ApiResponse
    {
        $raw = $this->http->delete("/v1/properties/{$propertyId}/cleaners/{$cleanerId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Set an iCal feed URL on a property for calendar availability syncing. */
    public function addICalLink(int $propertyId, string $icalLink): ApiResponse
    {
        $raw = $this->http->put("/v1/properties/{$propertyId}/ical", ['icalLink' => $icalLink]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Return the current iCal feed URL for a property. */
    public function getICalLink(int $propertyId): ApiResponse
    {
        $raw = $this->http->get("/v1/properties/{$propertyId}/ical");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Remove the iCal feed URL from a property. */
    public function removeICalLink(int $propertyId, string $icalLink): ApiResponse
    {
        $raw = $this->http->delete("/v1/properties/{$propertyId}/ical", ['icalLink' => $icalLink]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Set the default checklist for a property.
     *
     * @param bool $updateUpcomingBookings If true, also applies to all future bookings at this property.
     */
    public function setDefaultChecklist(
        int  $propertyId,
        int  $checklistId,
        bool $updateUpcomingBookings = false
    ): ApiResponse {
        $path = "/v1/properties/{$propertyId}/checklist/{$checklistId}"
              . '?updateUpcomingBookings=' . ($updateUpcomingBookings ? 'true' : 'false');
        $raw  = $this->http->put($path);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
