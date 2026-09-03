"""Property model."""

from typing import Any, Dict, Optional


class Property:
    """Represents a physical property where cleanings take place."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.user_id: Optional[int] = data.get("userId")
        self.name: Optional[str] = data.get("name")
        self.nickname: Optional[str] = data.get("nickName")
        self.apt: Optional[str] = data.get("apt")
        self.street: Optional[str] = data.get("street")
        self.address: Optional[str] = data.get("address")
        self.city: Optional[str] = data.get("city")
        self.state: Optional[str] = data.get("state")
        self.country: Optional[str] = data.get("country")
        self.zip_code: Optional[str] = data.get("zipCode")
        self.room_count: Optional[int] = data.get("roomCount")
        self.bathroom_count: Optional[int] = data.get("bathroomCount")
        self.service_id: Optional[int] = data.get("serviceId")
        self.is_enabled: Optional[bool] = data.get("isEnabled")
        self.is_active: Optional[bool] = data.get("isActive")
        self.is_enable: Optional[bool] = data.get("isEnable")
        self.pets: Optional[str] = data.get("pets")
        self.public_name: Optional[str] = data.get("publicName")
        self.wifi_name: Optional[str] = data.get("wifiName")
        self.wifi_password: Optional[str] = data.get("wifiPassword")
        self.laundry: Optional[bool] = data.get("laundry")
        self.garbage: Optional[str] = data.get("garbage")
        self.extra_supplies: Optional[bool] = data.get("extraSupplies")
        self.created_date: Optional[str] = data.get("createdDate")
        self.access: Optional[str] = data.get("access")
        self.supplies_location: Optional[str] = data.get("suppliesLocation")
        self.parking: Optional[str] = data.get("parking")
        self.other_note: Optional[str] = data.get("otherNote")
        self.latitude: Optional[float] = data.get("latitude")
        self.longitude: Optional[float] = data.get("longitude")
        self._raw = data

    def __repr__(self) -> str:
        return f"Property(id={self.id}, name={self.name!r}, city={self.city!r})"
