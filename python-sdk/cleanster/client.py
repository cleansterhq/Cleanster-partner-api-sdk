"""
CleansterClient — the main entry point for the Cleanster Python SDK.
"""

from typing import Optional

from .api.blacklist import BlacklistApi
from .api.bookings import BookingsApi
from .api.checklists import ChecklistsApi
from .api.other import OtherApi
from .api.payment_methods import PaymentMethodsApi
from .api.properties import PropertiesApi
from .api.users import UsersApi
from .api.webhooks import WebhooksApi
from .config import CleansterConfig
from .http_client import HttpClient


class CleansterClient:
    """
    Main entry point for the Cleanster Partner API SDK.

    Usage::

        # Sandbox (for development)
        client = CleansterClient.sandbox("your-access-key")

        # Production (for live use)
        client = CleansterClient.production("your-access-key")

        # Create a user
        response = client.users.create_user(
            email="user@example.com",
            first_name="Jane",
            last_name="Smith",
        )
        user_id = response.data.id

        # Authenticate as that user
        token = client.users.fetch_access_token(user_id).data.token
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
        print(f"Booking ID: {booking.data.id}")
    """

    def __init__(self, config: CleansterConfig):
        self._http = HttpClient(config)
        self.bookings = BookingsApi(self._http)
        self.users = UsersApi(self._http)
        self.properties = PropertiesApi(self._http)
        self.checklists = ChecklistsApi(self._http)
        self.other = OtherApi(self._http)
        self.blacklist = BlacklistApi(self._http)
        self.payment_methods = PaymentMethodsApi(self._http)
        self.webhooks = WebhooksApi(self._http)

    @classmethod
    def sandbox(cls, access_key: str) -> "CleansterClient":
        """
        Create a client connected to the **sandbox** environment.
        Use this for all development and testing.

        Args:
            access_key: Your Cleanster partner access key.

        Returns:
            CleansterClient pointed at sandbox.
        """
        return cls(CleansterConfig.sandbox(access_key))

    @classmethod
    def production(cls, access_key: str) -> "CleansterClient":
        """
        Create a client connected to the **production** environment.
        Real charges will be applied and real cleaners dispatched.

        Args:
            access_key: Your Cleanster partner access key.

        Returns:
            CleansterClient pointed at production.
        """
        return cls(CleansterConfig.production(access_key))

    def set_access_token(self, token: Optional[str]) -> None:
        """
        Set the user-level bearer token for authenticated requests.
        Obtain this by calling ``client.users.fetch_access_token(user_id)``.

        Args:
            token: The bearer token string, or None to clear.
        """
        self._http.bearer_token = token

    def get_access_token(self) -> Optional[str]:
        """Return the currently set user bearer token, or None if not set."""
        return self._http.bearer_token
