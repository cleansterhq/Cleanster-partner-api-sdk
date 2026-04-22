"""Booking model."""

from typing import Any, Dict, Optional


class Booking:
    """Represents a single cleaning booking."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.status: Optional[str] = data.get("status")
        self.date: Optional[str] = data.get("date")
        self.time: Optional[str] = data.get("time")
        self.hours: Optional[float] = data.get("hours")
        self.cost: Optional[float] = data.get("cost")
        self.property_id: Optional[int] = data.get("propertyId")
        self.cleaner_id: Optional[int] = data.get("cleanerId")
        self.plan_id: Optional[int] = data.get("planId")
        self.room_count: Optional[int] = data.get("roomCount")
        self.bathroom_count: Optional[int] = data.get("bathroomCount")
        self.extra_supplies: Optional[bool] = data.get("extraSupplies")
        self.payment_method_id: Optional[int] = data.get("paymentMethodId")
        self.posted_by: Optional[int] = data.get("postedBy")
        self._raw = data

    def __repr__(self) -> str:
        return f"Booking(id={self.id}, status={self.status!r}, date={self.date!r})"
