<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Response returned when registering a new user account. */
final class CreateUserResponse
{
    public readonly int     $userId;
    public readonly ?string $accessToken;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->userId      = (int)($data['userId'] ?? 0);
        $this->accessToken = isset($data['accessToken']) && $data['accessToken'] !== null
                             ? (string)$data['accessToken'] : null;
        $this->raw         = $data;
    }
}
