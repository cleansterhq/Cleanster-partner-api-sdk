<?php

declare(strict_types=1);

namespace Cleanster\Api;

use Cleanster\HttpClient;
use Cleanster\Models\ApiResponse;
use Cleanster\Models\User;

/**
 * Manages end-user accounts and authentication tokens.
 */
final class UsersApi
{
    public function __construct(private readonly HttpClient $http) {}

    /**
     * Register a new user account under your partner.
     *
     * @param string      $email     User email address.
     * @param string      $firstName First name.
     * @param string      $lastName  Last name.
     * @param string|null $phone     Optional phone number.
     *
     * @return ApiResponse<User>
     */
    public function createUser(
        string  $email,
        string  $firstName,
        string  $lastName,
        ?string $phone = null
    ): ApiResponse {
        $body = [
            'email'     => $email,
            'firstName' => $firstName,
            'lastName'  => $lastName,
        ];
        if ($phone !== null && $phone !== '') {
            $body['phone'] = $phone;
        }
        $raw = $this->http->post('/v1/user/account', $body);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new User($raw['data'] ?? []));
    }

    /**
     * Fetch the long-lived bearer token for a user.
     *
     * Pass the returned token to CleansterClient::setAccessToken() for all subsequent requests.
     * The token is long-lived — store it in your database and reuse across sessions.
     *
     * @return ApiResponse<User>  User object with the ->token field populated.
     */
    public function fetchAccessToken(int $userId): ApiResponse
    {
        $raw = $this->http->get("/v1/user/access-token/{$userId}");
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', new User($raw['data'] ?? []));
    }

    /**
     * Verify that a JWT token is valid and has not expired.
     *
     * @param string $token The JWT to verify.
     */
    public function verifyJwt(string $token): ApiResponse
    {
        $raw = $this->http->post('/v1/user/verify-jwt', ['token' => $token]);
        return new ApiResponse($raw['status'] ?? 200, $raw['message'] ?? 'OK', $raw['data'] ?? []);
    }
}
