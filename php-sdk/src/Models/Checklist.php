<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** A checklist returned by the live checklist API. */
final class Checklist
{
    public readonly int    $id;
    public readonly bool   $isDefault;
    public readonly bool   $disabled;
    public readonly string $title;
    public readonly string $type;
    public readonly int    $totalTasks;
    public readonly int    $totalSubTasks;
    /** @var ChecklistTask[] */
    public readonly array  $tasks;
    /** Legacy response accessor. */
    public readonly string $name;
    /** @var ChecklistItem[] */
    public readonly array  $items;
    /** The original raw array returned by the API. */
    public readonly array  $raw;

    public function __construct(array $data)
    {
        $this->id            = (int)($data['id'] ?? 0);
        $this->isDefault     = (bool)($data['is_default'] ?? false);
        $this->disabled      = (bool)($data['disabled'] ?? false);
        $this->title         = (string)($data['title'] ?? '');
        $this->type          = (string)($data['type'] ?? '');
        $this->totalTasks    = (int)($data['totalTasks'] ?? 0);
        $this->totalSubTasks = (int)($data['totalSubTasks'] ?? 0);
        $this->tasks = array_map(
            fn(array $task) => new ChecklistTask($task),
            $data['tasks'] ?? []
        );
        $this->name  = (string)($data['name'] ?? $this->title);
        $this->items = array_map(
            fn(array $item) => new ChecklistItem($item),
            $data['items'] ?? []
        );
        $this->raw   = $data;
    }
}
