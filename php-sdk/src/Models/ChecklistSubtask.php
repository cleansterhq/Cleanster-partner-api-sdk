<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** A subtask within a live checklist task. */
final class ChecklistSubtask
{
    public readonly string $description;
    public readonly bool $flagRequestPhotos;
    /** @var string[] */
    public readonly array $photos;
    public readonly array $raw;

    public function __construct(array $data)
    {
        $this->description = (string)($data['description'] ?? '');
        $this->flagRequestPhotos = (bool)($data['flag_request_photos'] ?? false);
        $this->photos = array_values($data['photos'] ?? []);
        $this->raw = $data;
    }
}