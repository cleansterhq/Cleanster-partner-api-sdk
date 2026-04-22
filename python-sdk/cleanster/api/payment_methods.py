"""Payment Methods API — manage Stripe and PayPal payment methods."""

from typing import Any, Dict

from ..http_client import HttpClient
from ..models.response import ApiResponse


class PaymentMethodsApi:
    """
    Payment method management: Stripe setup intent, PayPal client token,
    add/list/delete payment methods, and set the default.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def get_setup_intent_details(self) -> ApiResponse:
        """
        Get Stripe SetupIntent details for collecting card information client-side.
        Use the returned client_secret with Stripe.js or the Stripe mobile SDK.

        Returns:
            ApiResponse with data containing the Stripe setup intent.
        """
        raw = self._http.get("/v1/payment-methods/setup-intent")
        return ApiResponse.from_dict(raw)

    def get_paypal_client_token(self) -> ApiResponse:
        """
        Get a PayPal client token for rendering the PayPal button client-side.

        Returns:
            ApiResponse with data containing the PayPal client token.
        """
        raw = self._http.get("/v1/payment-methods/paypal-client-token")
        return ApiResponse.from_dict(raw)

    def add_payment_method(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Save a new payment method after client-side tokenization.

        Args:
            request: Dict with paymentMethodId (Stripe token) or
                     PayPal nonce, depending on the payment type.

        Returns:
            ApiResponse.
        """
        raw = self._http.post("/v1/payment-methods", body=request)
        return ApiResponse.from_dict(raw)

    def get_payment_methods(self) -> ApiResponse:
        """Return all saved payment methods for the current user."""
        raw = self._http.get("/v1/payment-methods")
        return ApiResponse.from_dict(raw)

    def delete_payment_method(self, payment_method_id: int) -> ApiResponse:
        """
        Delete a saved payment method.

        Args:
            payment_method_id: The payment method ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(f"/v1/payment-methods/{payment_method_id}")
        return ApiResponse.from_dict(raw)

    def set_default_payment_method(self, payment_method_id: int) -> ApiResponse:
        """
        Set a payment method as the default for future bookings.

        Args:
            payment_method_id: The payment method ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.put(f"/v1/payment-methods/{payment_method_id}/default")
        return ApiResponse.from_dict(raw)
