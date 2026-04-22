<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** A named collection of cleaning task items. */
final class Checklist
{
    public readonly int    $id;
    public readonly string $name;
    /** @var ChecklistItem[] */
    public readonly array  $items;
    /** The original raw array returned by the API. */
    public readonly array  $raw;

    public function __construct(array $data)
    {
        $this->id    = (int)($data['id'] ?? 0);
        $this->name  = (string)($data['name'] ?? '');
        $this->items = array_map(
            fn(array $item) => new ChecklistItem($item),
            $data['items'] ?? []
        );
        $this->raw   = $data;
    }
}
