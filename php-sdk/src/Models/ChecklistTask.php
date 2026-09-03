<?php

declare(strict_types=1);

namespace Cleanster\Models;

/** A task within a live checklist response. */
final class ChecklistTask
{
    public readonly ?string $imageName;
    public readonly string $title;
    public readonly int $totalSubtasks;
    /** @var ChecklistSubtask[] */
    public readonly array $subtasks;
    public readonly array $raw;

    public function __construct(array $data)
    {
        $this->imageName = isset($data['image_name']) && $data['image_name'] !== null
            ? (string)$data['image_name'] : null;
        $this->title = (string)($data['title'] ?? '');
        $this->totalSubtasks = (int)($data['totalSubtasks'] ?? 0);
        $this->subtasks = array_map(
            fn(array $subtask) => new ChecklistSubtask($subtask),
            $data['subtasks'] ?? []
        );
        $this->raw = $data;
    }
}