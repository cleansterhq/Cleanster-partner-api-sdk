"""
Internal HTTP transport layer — wraps the requests library and handles
authentication headers, JSON serialization, and error mapping.
"""

from typing import Any, Dict, Optional

import requests

from .config import CleansterConfig
from .exceptions import CleansterApiException, CleansterAuthException, CleansterException


class HttpClient:
    """
    Low-level HTTP client used internally by all API classes.
    Handles auth headers, timeouts, and maps HTTP error codes to SDK exceptions.
    """

    def __init__(self, config: CleansterConfig):
        self._config = config
        self._bearer_token: Optional[str] = None
        self._session = requests.Session()

    @property
    def bearer_token(self) -> Optional[str]:
        return self._bearer_token

    @bearer_token.setter
    def bearer_token(self, token: Optional[str]) -> None:
        self._bearer_token = token

    def _headers(self) -> Dict[str, str]:
        return {
            "Content-Type": "application/json",
            "access-key": self._config.access_key,
            "token": self._bearer_token or "",
        }

    def _url(self, path: str) -> str:
        return self._config.base_url + path

    def _handle_response(self, response: requests.Response) -> Dict[str, Any]:
        body = ""
        try:
            body = response.text
        except Exception:
            pass

        if response.status_code == 401:
            raise CleansterAuthException(
                "Unauthorized — invalid or missing access key or user token.",
                body,
            )
        if not response.ok:
            raise CleansterApiException(
                response.status_code,
                f"API request failed with status {response.status_code}",
                body,
            )
        try:
            return response.json()
        except Exception as exc:
            raise CleansterException(
                f"Failed to parse JSON response: {exc}"
            ) from exc

    def get(self, path: str, params: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        try:
            response = self._session.get(
                self._url(path),
                headers=self._headers(),
                params={k: v for k, v in (params or {}).items() if v is not None},
                timeout=self._config.timeout,
            )
        except requests.RequestException as exc:
            raise CleansterException(f"Network error: {exc}") from exc
        return self._handle_response(response)

    def post(self, path: str, body: Optional[Any] = None) -> Dict[str, Any]:
        try:
            response = self._session.post(
                self._url(path),
                headers=self._headers(),
                json=body,
                timeout=self._config.timeout,
            )
        except requests.RequestException as exc:
            raise CleansterException(f"Network error: {exc}") from exc
        return self._handle_response(response)

    def put(self, path: str, body: Optional[Any] = None) -> Dict[str, Any]:
        try:
            response = self._session.put(
                self._url(path),
                headers=self._headers(),
                json=body,
                timeout=self._config.timeout,
            )
        except requests.RequestException as exc:
            raise CleansterException(f"Network error: {exc}") from exc
        return self._handle_response(response)

    def post_multipart(self, path: str, image_bytes: bytes, file_name: str) -> Dict[str, Any]:
        """Upload an image via multipart/form-data POST."""
        try:
            response = self._session.post(
                self._url(path),
                headers={
                    "access-key": self._config.access_key,
                    "token": self._bearer_token or "",
                },
                files={"file": (file_name, image_bytes, "image/*")},
                timeout=self._config.timeout,
            )
        except requests.RequestException as exc:
            raise CleansterException(f"Network error: {exc}") from exc
        return self._handle_response(response)

    def delete(self, path: str, body: Optional[Any] = None) -> Dict[str, Any]:
        try:
            response = self._session.delete(
                self._url(path),
                headers=self._headers(),
                json=body,
                timeout=self._config.timeout,
            )
        except requests.RequestException as exc:
            raise CleansterException(f"Network error: {exc}") from exc
        return self._handle_response(response)
