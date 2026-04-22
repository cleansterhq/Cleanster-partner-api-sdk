<?php

declare(strict_types=1);

namespace Cleanster\Models;

/**
 * Standard response wrapper returned by every SDK method.
 *
 * @property int    $status  HTTP-style status code (e.g., 200).
 * @property string $message Human-readable status (e.g., "OK").
 * @property mixed  $data    Typed payload — a model object, array of models, or raw array.
 */
final class ApiResponse
{
    public function __construct(
        public readonly int    $status,
        public readonly string $message,
        public readonly mixed  $data,
    ) {}
}
