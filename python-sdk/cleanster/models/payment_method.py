"""PaymentMethod model."""

from typing import Any, Dict, Optional


class PaymentMethod:
    """Represents a saved payment method (card or PayPal)."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.type: Optional[str] = data.get("type")
        self.last_four: Optional[str] = data.get("lastFour")
        self.brand: Optional[str] = data.get("brand")
        self.is_default: Optional[bool] = data.get("isDefault")
        self._raw = data

    def __repr__(self) -> str:
        return (
            f"PaymentMethod(id={self.id}, type={self.type!r}, "
            f"last_four={self.last_four!r})"
        )
