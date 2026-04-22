<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Represents a physical location where cleanings take place. */
final class Property
{
    public readonly int     $id;
    public readonly string  $name;
    public readonly string  $address;
    public readonly string  $city;
    public readonly string  $country;
    public readonly int     $roomCount;
    public readonly int     $bathroomCount;
    public readonly int     $serviceId;
    public readonly ?bool   $isEnabled;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id            = (int)($data['id'] ?? 0);
        $this->name          = (string)($data['name'] ?? '');
        $this->address       = (string)($data['address'] ?? '');
        $this->city          = (string)($data['city'] ?? '');
        $this->country       = (string)($data['country'] ?? '');
        $this->roomCount     = (int)($data['roomCount'] ?? 0);
        $this->bathroomCount = (int)($data['bathroomCount'] ?? 0);
        $this->serviceId     = (int)($data['serviceId'] ?? 0);
        $this->isEnabled     = isset($data['isEnabled']) && $data['isEnabled'] !== null
                               ? (bool)$data['isEnabled'] : null;
        $this->raw           = $data;
    }
}
