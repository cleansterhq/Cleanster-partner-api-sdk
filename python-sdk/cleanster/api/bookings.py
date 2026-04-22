"""Booking API — manage the full lifecycle of cleaning appointments."""

from typing import Any, Dict, Optional

from ..http_client import HttpClient
from ..models.booking import Booking
from ..models.response import ApiResponse


class BookingsApi:
    """
    All booking-related operations: list, create, cancel, reschedule,
    cleaner assignment, hours, expenses, inspection, feedback, tip, and chat.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def get_bookings(
        self,
        page_no: Optional[int] = None,
        status: Optional[str] = None,
    ) -> ApiResponse:
        """
        List upcoming and past bookings.

        Args:
            page_no: Page number (1-based). None defaults to page 1.
            status:  Filter by booking status. One of: OPEN, CLEANER_ASSIGNED,
                     COMPLETED, CANCELLED, REMOVED. None returns all statuses.

        Returns:
            ApiResponse with data as a list of booking dicts.
        """
        params: Dict[str, Any] = {}
        if page_no is not None:
            params["pageNo"] = page_no
        if status is not None:
            params["status"] = status
        raw = self._http.get("/v1/bookings", params=params or None)
        return ApiResponse.from_dict(raw)

    def create_booking(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Schedule a new cleaning booking.

        Args:
            request: Dict with keys: date, time, propertyId, roomCount,
                     bathroomCount, planId, hours, extraSupplies, paymentMethodId.

        Returns:
            ApiResponse with data as the created Booking object.
        """
        raw = self._http.post("/v1/bookings/create", body=request)
        return ApiResponse.from_dict(raw, data_factory=Booking)

    def get_booking_details(self, booking_id: int) -> ApiResponse:
        """
        Get full details of a specific booking.

        Args:
            booking_id: The booking ID.

        Returns:
            ApiResponse with data as a Booking object.
        """
        raw = self._http.get(f"/v1/bookings/{booking_id}")
        return ApiResponse.from_dict(raw, data_factory=Booking)

    def cancel_booking(self, booking_id: int, reason: Optional[str] = None) -> ApiResponse:
        """
        Cancel a booking.

        Args:
            booking_id: The booking ID.
            reason:     Optional cancellation reason.

        Returns:
            ApiResponse.
        """
        body = {"reason": reason} if reason else {}
        raw = self._http.post(f"/v1/bookings/{booking_id}/cancel", body=body)
        return ApiResponse.from_dict(raw)

    def reschedule_booking(self, booking_id: int, date: str, time: str) -> ApiResponse:
        """
        Reschedule a booking to a new date and time.

        Args:
            booking_id: The booking ID.
            date:       New date in YYYY-MM-DD format.
            time:       New time in HH:mm format (24-hour).

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/reschedule",
            body={"date": date, "time": time},
        )
        return ApiResponse.from_dict(raw)

    def assign_cleaner(self, booking_id: int, cleaner_id: int) -> ApiResponse:
        """
        Assign a specific cleaner to a booking.

        Args:
            booking_id:  The booking ID.
            cleaner_id:  The cleaner's user ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/cleaner",
            body={"cleanerId": cleaner_id},
        )
        return ApiResponse.from_dict(raw)

    def remove_assigned_cleaner(self, booking_id: int) -> ApiResponse:
        """
        Remove the currently assigned cleaner from a booking.

        Args:
            booking_id: The booking ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(f"/v1/bookings/{booking_id}/cleaner")
        return ApiResponse.from_dict(raw)

    def adjust_hours(self, booking_id: int, hours: float) -> ApiResponse:
        """
        Change the duration (hours) of a booking.

        Args:
            booking_id: The booking ID.
            hours:      New number of hours.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/hours",
            body={"hours": hours},
        )
        return ApiResponse.from_dict(raw)

    def pay_expenses(self, booking_id: int, payment_method_id: int) -> ApiResponse:
        """
        Pay outstanding expenses for a completed booking.
        Must be called within 72 hours of booking completion.

        Args:
            booking_id:         The booking ID.
            payment_method_id:  ID of the payment method to charge.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/expenses",
            body={"paymentMethodId": payment_method_id},
        )
        return ApiResponse.from_dict(raw)

    def get_booking_inspection(self, booking_id: int) -> ApiResponse:
        """Get the inspection report for a completed booking."""
        raw = self._http.get(f"/v1/bookings/{booking_id}/inspection")
        return ApiResponse.from_dict(raw)

    def get_booking_inspection_details(self, booking_id: int) -> ApiResponse:
        """Get detailed inspection information for a completed booking."""
        raw = self._http.get(f"/v1/bookings/{booking_id}/inspection/details")
        return ApiResponse.from_dict(raw)

    def assign_checklist_to_booking(self, booking_id: int, checklist_id: int) -> ApiResponse:
        """
        Attach a cleaning checklist to a specific booking (overrides property default).

        Args:
            booking_id:    The booking ID.
            checklist_id:  The checklist ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/checklist/{checklist_id}"
        )
        return ApiResponse.from_dict(raw)

    def submit_feedback(
        self, booking_id: int, rating: int, comment: Optional[str] = None
    ) -> ApiResponse:
        """
        Submit a star rating and optional comment after a booking completes.

        Args:
            booking_id: The booking ID.
            rating:     Integer 1–5.
            comment:    Optional written feedback.

        Returns:
            ApiResponse.
        """
        body: Dict[str, Any] = {"rating": rating}
        if comment is not None:
            body["comment"] = comment
        raw = self._http.post(f"/v1/bookings/{booking_id}/feedback", body=body)
        return ApiResponse.from_dict(raw)

    def add_tip(
        self, booking_id: int, amount: float, payment_method_id: int
    ) -> ApiResponse:
        """
        Add a tip for the cleaner. Must be called within 72 hours of completion.

        Args:
            booking_id:         The booking ID.
            amount:             Tip amount in the account currency.
            payment_method_id:  ID of the payment method to charge.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/tip",
            body={"amount": amount, "paymentMethodId": payment_method_id},
        )
        return ApiResponse.from_dict(raw)

    def get_chat(self, booking_id: int) -> ApiResponse:
        """Retrieve all chat messages for a booking thread."""
        raw = self._http.get(f"/v1/bookings/{booking_id}/chat")
        return ApiResponse.from_dict(raw)

    def send_message(self, booking_id: int, message: str) -> ApiResponse:
        """
        Post a chat message in a booking thread.

        Args:
            booking_id: The booking ID.
            message:    Message text to send.

        Returns:
            ApiResponse.
        """
        raw = self._http.post(
            f"/v1/bookings/{booking_id}/chat",
            body={"message": message},
        )
        return ApiResponse.from_dict(raw)

    def delete_message(self, booking_id: int, message_id: str) -> ApiResponse:
        """
        Delete a specific chat message.

        Args:
            booking_id:  The booking ID.
            message_id:  The message ID string.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(f"/v1/bookings/{booking_id}/chat/{message_id}")
        return ApiResponse.from_dict(raw)
