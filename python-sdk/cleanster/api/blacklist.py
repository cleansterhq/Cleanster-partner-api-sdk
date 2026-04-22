"""Blacklist API — prevent specific cleaners from being assigned to your bookings."""

from typing import Optional

from ..http_client import HttpClient
from ..models.response import ApiResponse


class BlacklistApi:
    """
    Blacklist management: list, add, and remove cleaners from the blacklist.
    Blacklisted cleaners will not be auto-assigned to any of your bookings.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def list_blacklisted_cleaners(self) -> ApiResponse:
        """Return all cleaners currently on the blacklist."""
        raw = self._http.get("/v1/blacklist/cleaner")
        return ApiResponse.from_dict(raw)

    def add_to_blacklist(
        self, cleaner_id: int, reason: Optional[str] = None
    ) -> ApiResponse:
        """
        Add a cleaner to the blacklist.

        Args:
            cleaner_id: The cleaner's user ID.
            reason:     Optional reason for blacklisting.

        Returns:
            ApiResponse.
        """
        body = {"cleanerId": cleaner_id}
        if reason is not None:
            body["reason"] = reason
        raw = self._http.post("/v1/blacklist/cleaner", body=body)
        return ApiResponse.from_dict(raw)

    def remove_from_blacklist(self, cleaner_id: int) -> ApiResponse:
        """
        Remove a cleaner from the blacklist.

        Args:
            cleaner_id: The cleaner's user ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(
            "/v1/blacklist/cleaner", body={"cleanerId": cleaner_id}
        )
        return ApiResponse.from_dict(raw)
