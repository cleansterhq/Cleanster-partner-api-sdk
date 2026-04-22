<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;

/**
 * Manages real-time event notification endpoints.
 */
final class WebhooksApi
{
    public function __construct(private readonly HttpClient $http) {}

    /** Return all configured webhook endpoints. */
    public function listWebhooks(): ApiResponse
    {
        $raw = $this->http->get('/v1/webhooks');
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Register a new webhook endpoint for booking event notifications.
     *
     * @param string $url   Your HTTPS endpoint URL.
     * @param string $event Event type (e.g., "booking.status_changed").
     */
    public function createWebhook(string $url, string $event): ApiResponse
    {
        $raw = $this->http->post('/v1/webhooks', ['url' => $url, 'event' => $event]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /**
     * Update the URL or event type of an existing webhook.
     *
     * @param int    $webhookId Webhook to update.
     * @param string $url       New endpoint URL.
     * @param string $event     New event type.
     */
    public function updateWebhook(int $webhookId, string $url, string $event): ApiResponse
    {
        $raw = $this->http->put("/v1/webhooks/{$webhookId}", ['url' => $url, 'event' => $event]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }

    /** Remove a webhook endpoint. */
    public function deleteWebhook(int $webhookId): ApiResponse
    {
        $raw = $this->http->delete("/v1/webhooks/{$webhookId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
