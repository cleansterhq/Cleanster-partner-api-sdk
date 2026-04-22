<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** Represents a saved Stripe card or PayPal payment method. */
final class PaymentMethod
{
    public readonly int     $id;
    public readonly string  $type;
    public readonly ?string $lastFour;
    public readonly ?string $brand;
    public readonly bool    $isDefault;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id        = (int)($data['id'] ?? 0);
        $this->type      = (string)($data['type'] ?? '');
        $this->lastFour  = isset($data['lastFour']) && $data['lastFour'] !== null
                           ? (string)$data['lastFour'] : null;
        $this->brand     = isset($data['brand']) && $data['brand'] !== null
                           ? (string)$data['brand'] : null;
        $this->isDefault = (bool)($data['isDefault'] ?? false);
        $this->raw       = $data;
    }
}
