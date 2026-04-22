"""
Cleanster Python SDK
====================
Official Python client for the Cleanster Partner API.

Quick start::

    from cleanster import CleansterClient

    client = CleansterClient.sandbox("your-access-key")

    # Create a user and authenticate
    user = client.users.create_user(
        email="user@example.com",
        first_name="Jane",
        last_name="Smith",
    )
    token = client.users.fetch_access_token(user.data.id).data.token
    client.set_access_token(token)

    # Create a booking
    booking = client.bookings.create_booking({
        "date": "2025-06-15",
        "time": "10:00",
        "propertyId": 1004,
        "roomCount": 2,
        "bathroomCount": 1,
        "planId": 5,
        "hours": 3.0,
        "extraSupplies": False,
        "paymentMethodId": 10,
    })
    print(booking.data.id)
"""

from .client import CleansterClient
from .config import CleansterConfig
from .exceptions import CleansterApiException, CleansterAuthException, CleansterException
from .models import ApiResponse, Booking, Checklist, PaymentMethod, Property, User

__version__ = "1.0.0"
__all__ = [
    "CleansterClient",
    "CleansterConfig",
    "CleansterException",
    "CleansterAuthException",
    "CleansterApiException",
    "ApiResponse",
    "Booking",
    "Checklist",
    "PaymentMethod",
    "Property",
    "User",
]
