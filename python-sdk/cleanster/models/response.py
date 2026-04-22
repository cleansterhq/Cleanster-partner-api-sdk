"""Generic API response wrapper."""

from typing import Any, Dict, Generic, Optional, TypeVar

T = TypeVar("T")


class ApiResponse(Generic[T]):
    """
    Wraps every Cleanster API response.

    Attributes:
        status:  HTTP-style status code (e.g., 200).
        message: Human-readable status message (e.g., "OK").
        data:    The typed response payload, or None for void responses.
    """

    def __init__(self, status: Optional[int], message: Optional[str], data: T):
        self.status = status
        self.message = message
        self.data = data

    @classmethod
    def from_dict(cls, raw: Dict[str, Any], data_factory=None) -> "ApiResponse":
        raw_data = raw.get("data")
        if data_factory is not None and isinstance(raw_data, dict):
            data = data_factory(raw_data)
        elif data_factory is not None and isinstance(raw_data, list):
            data = [data_factory(item) if isinstance(item, dict) else item for item in raw_data]
        else:
            data = raw_data
        return cls(
            status=raw.get("status"),
            message=raw.get("message"),
            data=data,
        )

    def __repr__(self) -> str:
        return f"ApiResponse(status={self.status}, message={self.message!r})"
