"""Webhooks API — manage real-time event notifications."""

from typing import Any, Dict

from ..http_client import HttpClient
from ..models.response import ApiResponse


class WebhooksApi:
    """
    Webhook management: list, create, update, and delete webhook endpoints.
    Webhooks send real-time POST requests to your server when booking events occur.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def list_webhooks(self) -> ApiResponse:
        """Return all configured webhook endpoints."""
        raw = self._http.get("/v1/webhooks")
        return ApiResponse.from_dict(raw)

    def create_webhook(self, request: Dict[str, Any]) -> ApiResponse:
        """
        Register a new webhook endpoint.

        Args:
            request: Dict with keys: url (your HTTPS endpoint), event (event type).

        Returns:
            ApiResponse with data as the created webhook dict.
        """
        raw = self._http.post("/v1/webhooks", body=request)
        return ApiResponse.from_dict(raw)

    def update_webhook(self, webhook_id: int, request: Dict[str, Any]) -> ApiResponse:
        """
        Update an existing webhook endpoint configuration.

        Args:
            webhook_id: The webhook ID.
            request:    Dict with updated fields (url, event, etc.).

        Returns:
            ApiResponse.
        """
        raw = self._http.put(f"/v1/webhooks/{webhook_id}", body=request)
        return ApiResponse.from_dict(raw)

    def delete_webhook(self, webhook_id: int) -> ApiResponse:
        """
        Delete a webhook endpoint.

        Args:
            webhook_id: The webhook ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(f"/v1/webhooks/{webhook_id}")
        return ApiResponse.from_dict(raw)
