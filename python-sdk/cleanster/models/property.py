"""Property model."""

from typing import Any, Dict, Optional


class Property:
    """Represents a physical property where cleanings take place."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.name: Optional[str] = data.get("name")
        self.address: Optional[str] = data.get("address")
        self.city: Optional[str] = data.get("city")
        self.country: Optional[str] = data.get("country")
        self.room_count: Optional[int] = data.get("roomCount")
        self.bathroom_count: Optional[int] = data.get("bathroomCount")
        self.service_id: Optional[int] = data.get("serviceId")
        self.is_enabled: Optional[bool] = data.get("isEnabled")
        self._raw = data

    def __repr__(self) -> str:
        return f"Property(id={self.id}, name={self.name!r}, city={self.city!r})"
