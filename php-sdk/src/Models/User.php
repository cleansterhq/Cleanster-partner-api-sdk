<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Represents a Cleanster end-user account. */
final class User
{
    public readonly int     $id;
    public readonly string  $email;
    public readonly string  $firstName;
    public readonly string  $lastName;
    public readonly ?string $phone;
    /** Bearer token — only present after fetchAccessToken(). */
    public readonly ?string $token;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id        = (int)($data['id'] ?? 0);
        $this->email     = (string)($data['email'] ?? '');
        $this->firstName = (string)($data['firstName'] ?? '');
        $this->lastName  = (string)($data['lastName'] ?? '');
        $this->phone     = isset($data['phone']) && $data['phone'] !== null
                           ? (string)$data['phone'] : null;
        $this->token     = isset($data['token']) && $data['token'] !== null
                           ? (string)$data['token'] : null;
        $this->raw       = $data;
    }
}
