"""Other / Utilities API — services, plans, cost, extras, cleaners, coupons."""

from typing import Any, Dict, Optional

from ..http_client import HttpClient
from ..models.response import ApiResponse


class OtherApi:
    """
    Utility endpoints for reference data used in booking flows:
    services, plans, recommended hours, cost estimates, cleaning extras,
    available cleaners, and coupons.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def get_services(self) -> ApiResponse:
        """Return all available cleaning service types (e.g. Residential, Airbnb)."""
        raw = self._http.get("/v1/services")
        return ApiResponse.from_dict(raw)

    def get_plans(self, property_id: int) -> ApiResponse:
        """
        Return available booking plans for a given property.

        Args:
            property_id: The property ID.

        Returns:
            ApiResponse with data as a list of plan dicts.
        """
        raw = self._http.get("/v1/plans", params={"propertyId": property_id})
        return ApiResponse.from_dict(raw)

    def get_recommended_hours(
        self, property_id: int, bathroom_count: int, room_count: int
    ) -> ApiResponse:
        """
        Get the system-recommended cleaning hours based on property size.

        Args:
            property_id:     The property ID.
            bathroom_count:  Number of bathrooms.
            room_count:      Number of rooms/bedrooms.

        Returns:
            ApiResponse with data containing the recommended hours.
        """
        raw = self._http.get(
            "/v1/recommended-hours",
            params={
                "propertyId": property_id,
                "bathroomCount": bathroom_count,
                "roomCount": room_count,
            },
        )
        return ApiResponse.from_dict(raw)

    def get_cost_estimate(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Calculate the estimated cost for a potential booking.

        Args:
            request: Dict with keys: propertyId, planId, hours,
                     and optionally couponCode, extras, etc.

        Returns:
            ApiResponse with data as a cost breakdown dict.
        """
        raw = self._http.post("/v1/cost-estimate", body=request)
        return ApiResponse.from_dict(raw)

    def get_cleaning_extras(self, service_id: int) -> ApiResponse:
        """
        Get available add-on services for a given service type.

        Args:
            service_id: The service type ID.

        Returns:
            ApiResponse with data as a list of extra dicts.
        """
        raw = self._http.get(f"/v1/cleaning-extras/{service_id}")
        return ApiResponse.from_dict(raw)

    def get_available_cleaners(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Find cleaners available for a specific property, date, and time.

        Args:
            request: Dict with keys: propertyId, date, time.

        Returns:
            ApiResponse with data as a list of available cleaner dicts.
        """
        raw = self._http.post("/v1/available-cleaners", body=request)
        return ApiResponse.from_dict(raw)

    def get_coupons(self) -> ApiResponse:
        """Return all valid coupon codes available for use."""
        raw = self._http.get("/v1/coupons")
        return ApiResponse.from_dict(raw)
