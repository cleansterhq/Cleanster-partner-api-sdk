<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** A single task item within a Checklist. */
final class ChecklistItem
{
    public readonly int     $id;
    public readonly string  $description;
    public readonly bool    $isCompleted;
    public readonly ?string $imageUrl;
    /** The original raw array returned by the API. */
    public readonly array   $raw;

    public function __construct(array $data)
    {
        $this->id          = (int)($data['id'] ?? 0);
        $this->description = (string)($data['description'] ?? '');
        $this->isCompleted = (bool)($data['isCompleted'] ?? false);
        $this->imageUrl    = isset($data['imageUrl']) && $data['imageUrl'] !== null
                             ? (string)$data['imageUrl'] : null;
        $this->raw         = $data;
    }
}
