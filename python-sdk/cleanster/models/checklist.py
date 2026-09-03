"""Checklist models."""

from typing import Any, Dict, List, Optional


class ChecklistItem:
    """A single task item within a checklist."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.description: Optional[str] = data.get("description")
        self.is_completed: Optional[bool] = data.get("isCompleted")
        self.image_url: Optional[str] = data.get("imageUrl")
        self._raw = data

    def __repr__(self) -> str:
        return f"ChecklistItem(id={self.id}, description={self.description!r})"


class Checklist:
    """A checklist returned by the live checklist API."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.is_default: Optional[bool] = data.get("is_default")
        self.disabled: Optional[bool] = data.get("disabled")
        self.title: Optional[str] = data.get("title")
        self.type: Optional[str] = data.get("type")
        self.total_tasks: Optional[int] = data.get("totalTasks")
        self.total_sub_tasks: Optional[int] = data.get("totalSubTasks")
        raw_tasks = data.get("tasks") or []
        self.tasks: List[ChecklistTask] = [
            ChecklistTask(task) if isinstance(task, dict) else task
            for task in raw_tasks
        ]

        # Legacy response accessors retained for existing integrations.
        self.name: Optional[str] = data.get("name", self.title)
        raw_items = data.get("items") or []
        self.items: List[ChecklistItem] = [
            ChecklistItem(item) if isinstance(item, dict) else item
            for item in raw_items
        ]
        self._raw = data

    def __repr__(self) -> str:
        return f"Checklist(id={self.id}, title={self.title!r}, tasks={len(self.tasks)})"


class ChecklistSubtask:
    """A subtask within a checklist task."""

    def __init__(self, data: Dict[str, Any]):
        self.description: Optional[str] = data.get("description")
        self.flag_request_photos: Optional[bool] = data.get("flag_request_photos")
        self.photos: List[str] = list(data.get("photos") or [])
        self._raw = data


class ChecklistTask:
    """A task within a checklist."""

    def __init__(self, data: Dict[str, Any]):
        self.image_name: Optional[str] = data.get("image_name")
        self.title: Optional[str] = data.get("title")
        self.total_subtasks: Optional[int] = data.get("totalSubtasks")
        raw_subtasks = data.get("subtasks") or []
        self.subtasks: List[ChecklistSubtask] = [
            ChecklistSubtask(subtask) if isinstance(subtask, dict) else subtask
            for subtask in raw_subtasks
        ]
        self._raw = data
