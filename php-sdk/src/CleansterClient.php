<?php

declare(strict_types=1);

namespace Cleanster;

use Cleanster\Api\BlacklistApi;
use Cleanster\Api\BookingsApi;
use Cleanster\Api\ChecklistsApi;
use Cleanster\Api\OtherApi;
use Cleanster\Api\PaymentMethodsApi;
use Cleanster\Api\PropertiesApi;
use Cleanster\Api\UsersApi;
use Cleanster\Api\WebhooksApi;

/**
 * Main entry point for the Cleanster Partner API SDK.
 *
 * Create a client using the sandbox() or production() factory methods.
 * After creating a user, call setAccessToken() with the user bearer token
 * so it is automatically included on every subsequent request.
 *
 * ```php
 * $client = CleansterClient::sandbox($_ENV['CLEANSTER_API_KEY']);
 *
 * $userResp = $client->users()->createUser('jane@example.com', 'Jane', 'Smith');
 * $token    = $client->users()->fetchAccessToken($userResp->data->id);
 * $client->setAccessToken($token->data->token);
 *
 * $bookings = $client->bookings()->getBookings();
 * ```
 */
final class CleansterClient
{
    private readonly HttpClient      $http;
    private readonly BookingsApi     $bookings;
    private readonly UsersApi        $users;
    private readonly PropertiesApi   $properties;
    private readonly ChecklistsApi   $checklists;
    private readonly OtherApi        $other;
    private readonly BlacklistApi    $blacklist;
    private readonly PaymentMethodsApi $paymentMethods;
    private readonly WebhooksApi     $webhooks;

    /**
     * @param Config          $config     SDK configuration.
     * @param HttpClient|null $httpClient Optional custom HTTP client — used in tests.
     */
    public function __construct(Config $config, ?HttpClient $httpClient = null)
    {
        $this->http           = $httpClient ?? new HttpClient($config);
        $this->bookings       = new BookingsApi($this->http);
        $this->users          = new UsersApi($this->http);
        $this->properties     = new PropertiesApi($this->http);
        $this->checklists     = new ChecklistsApi($this->http);
        $this->other          = new OtherApi($this->http);
        $this->blacklist      = new BlacklistApi($this->http);
        $this->paymentMethods = new PaymentMethodsApi($this->http);
        $this->webhooks       = new WebhooksApi($this->http);
    }

    /** Create a client configured for the sandbox environment (no real charges). */
    public static function sandbox(string $accessKey): self
    {
        return new self(Config::sandbox($accessKey));
    }

    /** Create a client configured for the production environment (live traffic). */
    public static function production(string $accessKey): self
    {
        return new self(Config::production($accessKey));
    }

    /**
     * Set the user bearer token.
     *
     * Call this after fetching a user token with users()->fetchAccessToken().
     * The token is sent as the "token" HTTP header on every subsequent request.
     *
     * @param string $token User bearer token from fetchAccessToken().
     */
    public function setAccessToken(string $token): void
    {
        $this->http->setToken($token);
    }

    /** Return the currently active bearer token, or '' if not set. */
    public function getAccessToken(): string
    {
        return $this->http->getToken();
    }

    /** Access booking management methods. */
    public function bookings(): BookingsApi
    {
        return $this->bookings;
    }

    /** Access user account and authentication methods. */
    public function users(): UsersApi
    {
        return $this->users;
    }

    /** Access property management methods. */
    public function properties(): PropertiesApi
    {
        return $this->properties;
    }

    /** Access checklist management methods. */
    public function checklists(): ChecklistsApi
    {
        return $this->checklists;
    }

    /** Access utility/reference data methods (services, plans, cost estimates, etc.). */
    public function other(): OtherApi
    {
        return $this->other;
    }

    /** Access cleaner blacklist management methods. */
    public function blacklist(): BlacklistApi
    {
        return $this->blacklist;
    }

    /** Access payment method management methods. */
    public function paymentMethods(): PaymentMethodsApi
    {
        return $this->paymentMethods;
    }

    /** Access webhook management methods. */
    public function webhooks(): WebhooksApi
    {
        return $this->webhooks;
    }
}
