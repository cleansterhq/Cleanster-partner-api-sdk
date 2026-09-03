---
name: Checklist wire contract
description: Live response and create/update structure for reusable checklists.
---

Reusable checklists are hierarchical. Create and update bodies use `title` and
`tasks`. Each task uses `image_name`, `title`, and `subtasks`; each subtask uses
`description`, `flag_request_photos`, and optional `photos`.

List and get responses additionally include `is_default`, `disabled`, `type`,
`totalTasks`, `totalSubTasks`, and task-level `totalSubtasks`.

**Why:** The public Postman contract uses this structure. The previous
`name`/`items` payload is a legacy SDK shape and is not the live create/update
wire contract.

**How to apply:** Preserve legacy response aliases only for compatibility.
New create/update serialization must emit `title` and structured `tasks`, with
the exact snake-case keys above. Do not require or emit response-only counters
when creating a checklist.