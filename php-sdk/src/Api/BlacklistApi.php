<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;

/**
 * Manages the list of cleaners blocked from auto-assignment.
 */
final class BlacklistApi
{
    public function __construct(private readonly HttpClient $http) {}

    /** Return all cleaners currently on the blacklist. */
    public function listBlacklistedCleaners(): ApiResponse
    {
        $raw = $this->http->get('/v1/blacklist/cleaner');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Prevent a cleaner from being auto-assigned to bookings.
     *
     * @param int         $cleanerId Cleaner to blacklist.
     * @param string|null $reason    Optional reason.
     */
    public function addToBlacklist(int $cleanerId, ?string $reason = null): ApiResponse
    {
        $body = ['cleanerId' => $cleanerId];
        if ($reason !== null && $reason !== '') {
            $body['reason'] = $reason;
        }
        $raw = $this->http->post('/v1/blacklist/cleaner', $body);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Re-enable a previously blacklisted cleaner for auto-assignment. */
    public function removeFromBlacklist(int $cleanerId): ApiResponse
    {
        $raw = $this->http->delete('/v1/blacklist/cleaner', ['cleanerId' => $cleanerId]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
