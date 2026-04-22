"""User model."""

from typing import Any, Dict, Optional


class User:
    """Represents a Cleanster end-user account."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.email: Optional[str] = data.get("email")
        self.first_name: Optional[str] = data.get("firstName")
        self.last_name: Optional[str] = data.get("lastName")
        self.phone: Optional[str] = data.get("phone")
        self.token: Optional[str] = data.get("token")
        self._raw = data

    def __repr__(self) -> str:
        return f"User(id={self.id}, email={self.email!r})"
