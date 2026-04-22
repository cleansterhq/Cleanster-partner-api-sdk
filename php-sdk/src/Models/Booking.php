<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Represents a single cleaning appointment. */
final class Booking
{
    public readonly int     $id;
    public readonly string  $status;
    public readonly string  $date;
    public readonly string  $time;
    public readonly float   $hours;
    public readonly float   $cost;
    public readonly int     $propertyId;
    public readonly ?int    $cleanerId;
    public readonly int     $planId;
    public readonly int     $roomCount;
    public readonly int     $bathroomCount;
    public readonly bool    $extraSupplies;
    public readonly int     $paymentMethodId;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id              = (int)($data['id'] ?? 0);
        $this->status          = (string)($data['status'] ?? '');
        $this->date            = (string)($data['date'] ?? '');
        $this->time            = (string)($data['time'] ?? '');
        $this->hours           = (float)($data['hours'] ?? 0);
        $this->cost            = (float)($data['cost'] ?? 0);
        $this->propertyId      = (int)($data['propertyId'] ?? 0);
        $this->cleanerId       = isset($data['cleanerId']) && $data['cleanerId'] !== null
                                 ? (int)$data['cleanerId'] : null;
        $this->planId          = (int)($data['planId'] ?? 0);
        $this->roomCount       = (int)($data['roomCount'] ?? 0);
        $this->bathroomCount   = (int)($data['bathroomCount'] ?? 0);
        $this->extraSupplies   = (bool)($data['extraSupplies'] ?? false);
        $this->paymentMethodId = (int)($data['paymentMethodId'] ?? 0);
        $this->raw             = $data;
    }
}
