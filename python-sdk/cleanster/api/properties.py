"""Properties API — CRUD for cleaning locations, plus cleaners, iCal, and checklists."""

from typing import Any, Dict, Optional

from ..http_client import HttpClient
from ..models.property import Property
from ..models.response import ApiResponse


class PropertiesApi:
    """
    Property management: create, read, update, delete, enable/disable,
    cleaner assignment, iCal calendar sync, and checklist association.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def list_properties(self, service_id: Optional[int] = None) -> ApiResponse:
        """
        List all properties. Optionally filter by service type.

        Args:
            service_id: Filter by service type ID. None returns all properties.

        Returns:
            ApiResponse with data as a list of property dicts.
        """
        params = {"serviceId": service_id} if service_id is not None else None
        raw = self._http.get("/v1/properties", params=params)
        return ApiResponse.from_dict(raw)

    def add_property(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Add a new property.

        Args:
            request: Dict with keys: name, address, city, country,
                     roomCount, bathroomCount, serviceId.

        Returns:
            ApiResponse with data as a Property object.
        """
        raw = self._http.post("/v1/properties", body=request)
        return ApiResponse.from_dict(raw, data_factory=Property)

    def get_property(self, property_id: int) -> ApiResponse:
        """Get details of a specific property."""
        raw = self._http.get(f"/v1/properties/{property_id}")
        return ApiResponse.from_dict(raw, data_factory=Property)

    def update_property(self, property_id: int, request: Dict[str, Any]) -> ApiResponse:
        """Update an existing property's details."""
        raw = self._http.put(f"/v1/properties/{property_id}", body=request)
        return ApiResponse.from_dict(raw, data_factory=Property)

    def update_additional_information(
        self, property_id: int, data: Dict[str, Any]
    ) -> ApiResponse:
        """Update additional/supplemental information fields for a property."""
        raw = self._http.put(
            f"/v1/properties/{property_id}/additional-information", body=data
        )
        return ApiResponse.from_dict(raw)

    def enable_or_disable_property(
        self, property_id: int, enabled: bool
    ) -> ApiResponse:
        """
        Toggle a property's active/inactive state.

        Args:
            property_id: The property ID.
            enabled:     True to enable, False to disable.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/properties/{property_id}/enable-disable",
            body={"enabled": enabled},
        )
        return ApiResponse.from_dict(raw)

    def delete_property(self, property_id: int) -> ApiResponse:
        """Permanently delete a property."""
        raw = self._http.delete(f"/v1/properties/{property_id}")
        return ApiResponse.from_dict(raw)

    def get_property_cleaners(self, property_id: int) -> ApiResponse:
        """Get the list of cleaners assigned to a property."""
        raw = self._http.get(f"/v1/properties/{property_id}/cleaners")
        return ApiResponse.from_dict(raw)

    def assign_cleaner_to_property(
        self, property_id: int, cleaner_id: int
    ) -> ApiResponse:
        """Assign a cleaner to a property's default cleaner pool."""
        raw = self._http.post(
            f"/v1/properties/{property_id}/cleaners",
            body={"cleanerId": cleaner_id},
        )
        return ApiResponse.from_dict(raw)

    def unassign_cleaner_from_property(
        self, property_id: int, cleaner_id: int
    ) -> ApiResponse:
        """Remove a cleaner from a property's default cleaner pool."""
        raw = self._http.delete(
            f"/v1/properties/{property_id}/cleaners/{cleaner_id}"
        )
        return ApiResponse.from_dict(raw)

    def add_ical_link(self, property_id: int, ical_url: str) -> ApiResponse:
        """
        Add an iCal calendar link to a property for availability syncing.

        Args:
            property_id: The property ID.
            ical_url:    The iCal feed URL (e.g. from Airbnb, VRBO).

        Returns:
            ApiResponse.
        """
        raw = self._http.put(
            f"/v1/properties/{property_id}/ical",
            body={"icalLink": ical_url},
        )
        return ApiResponse.from_dict(raw)

    def get_ical_link(self, property_id: int) -> ApiResponse:
        """Retrieve the current iCal link for a property."""
        raw = self._http.get(f"/v1/properties/{property_id}/ical")
        return ApiResponse.from_dict(raw)

    def remove_ical_link(self, property_id: int, ical_url: str) -> ApiResponse:
        """Remove the iCal calendar link from a property."""
        raw = self._http.delete(
            f"/v1/properties/{property_id}/ical",
            body={"icalLink": ical_url},
        )
        return ApiResponse.from_dict(raw)

    def set_default_checklist(
        self,
        property_id: int,
        checklist_id: int,
        update_upcoming_bookings: bool = False,
    ) -> ApiResponse:
        """
        Set a default checklist for a property.

        Args:
            property_id:               The property ID.
            checklist_id:              The checklist ID.
            update_upcoming_bookings:  If True, apply to all upcoming bookings too.

        Returns:
            ApiResponse.
        """
        path = (
            f"/v1/properties/{property_id}/checklist/{checklist_id}"
            f"?updateUpcomingBookings={str(update_upcoming_bookings).lower()}"
        )
        raw = self._http.put(path)
        return ApiResponse.from_dict(raw)
