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
    """A named collection of cleaning tasks."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.name: Optional[str] = data.get("name")
        raw_items = data.get("items") or []
        self.items: List[ChecklistItem] = [
            ChecklistItem(item) if isinstance(item, dict) else item
            for item in raw_items
        ]
        self._raw = data

    def __repr__(self) -> str:
        return f"Checklist(id={self.id}, name={self.name!r}, items={len(self.items)})"
