<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Represents a physical location where cleanings take place. */
final class Property
{
    public readonly int     $id;
    public readonly ?int    $userId;
    public readonly string  $name;
    public readonly ?string $nickname;
    public readonly ?string $apt;
    public readonly ?string $street;
    public readonly string  $address;
    public readonly string  $city;
    public readonly ?string $state;
    public readonly string  $country;
    public readonly ?string $zipCode;
    public readonly int     $roomCount;
    public readonly int     $bathroomCount;
    public readonly int     $serviceId;
    public readonly ?bool   $isEnabled;
    public readonly ?bool   $isActive;
    public readonly ?bool   $isEnable;
    public readonly ?string $pets;
    public readonly ?string $publicName;
    public readonly ?string $wifiName;
    public readonly ?string $wifiPassword;
    public readonly ?bool   $laundry;
    public readonly ?string $garbage;
    public readonly ?bool   $extraSupplies;
    public readonly ?string $createdDate;
    public readonly ?string $access;
    public readonly ?string $suppliesLocation;
    public readonly ?string $parking;
    public readonly ?string $otherNote;
    public readonly ?float  $latitude;
    public readonly ?float  $longitude;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id            = (int)($data['id'] ?? 0);
        $this->userId        = isset($data['userId']) ? (int)$data['userId'] : null;
        $this->name          = (string)($data['name'] ?? '');
        $this->nickname      = isset($data['nickName']) ? (string)$data['nickName'] : null;
        $this->apt           = isset($data['apt']) ? (string)$data['apt'] : null;
        $this->street        = isset($data['street']) ? (string)$data['street'] : null;
        $this->address       = (string)($data['address'] ?? '');
        $this->city          = (string)($data['city'] ?? '');
        $this->state         = isset($data['state']) ? (string)$data['state'] : null;
        $this->country       = (string)($data['country'] ?? '');
        $this->zipCode       = isset($data['zipCode']) ? (string)$data['zipCode'] : null;
        $this->roomCount     = (int)($data['roomCount'] ?? 0);
        $this->bathroomCount = (int)($data['bathroomCount'] ?? 0);
        $this->serviceId     = (int)($data['serviceId'] ?? 0);
        $this->isEnabled     = isset($data['isEnabled']) && $data['isEnabled'] !== null
                               ? (bool)$data['isEnabled'] : null;
        $this->isActive      = isset($data['isActive']) ? (bool)$data['isActive'] : null;
        $this->isEnable      = isset($data['isEnable']) ? (bool)$data['isEnable'] : null;
        $this->pets          = isset($data['pets']) ? (string)$data['pets'] : null;
        $this->publicName    = isset($data['publicName']) ? (string)$data['publicName'] : null;
        $this->wifiName      = isset($data['wifiName']) ? (string)$data['wifiName'] : null;
        $this->wifiPassword  = isset($data['wifiPassword']) ? (string)$data['wifiPassword'] : null;
        $this->laundry       = isset($data['laundry']) ? (bool)$data['laundry'] : null;
        $this->garbage       = isset($data['garbage']) ? (string)$data['garbage'] : null;
        $this->extraSupplies = isset($data['extraSupplies']) ? (bool)$data['extraSupplies'] : null;
        $this->createdDate   = isset($data['createdDate']) ? (string)$data['createdDate'] : null;
        $this->access        = isset($data['access']) ? (string)$data['access'] : null;
        $this->suppliesLocation = isset($data['suppliesLocation']) ? (string)$data['suppliesLocation'] : null;
        $this->parking       = isset($data['parking']) ? (string)$data['parking'] : null;
        $this->otherNote     = isset($data['otherNote']) ? (string)$data['otherNote'] : null;
        $this->latitude      = isset($data['latitude']) ? (float)$data['latitude'] : null;
        $this->longitude     = isset($data['longitude']) ? (float)$data['longitude'] : null;
        $this->raw           = $data;
    }
}
