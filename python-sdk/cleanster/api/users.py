"""User API — manage end-user accounts and authentication tokens."""

from typing import Any, Dict, Optional

from ..http_client import HttpClient
from ..models.response import ApiResponse
from ..models.user import User


class UsersApi:
    """
    User account operations: create user, fetch access token, verify JWT.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def create_user(
        self,
        email: str,
        first_name: str,
        last_name: str,
        phone: Optional[str] = None,
    ) -> ApiResponse:
        """
        Create a new user account under your partner.

        Args:
            email:      User's email address.
            first_name: User's first name.
            last_name:  User's last name.
            phone:      User's phone number (optional).

        Returns:
            ApiResponse with data as a User object.
        """
        body: Dict[str, Any] = {
            "email": email,
            "firstName": first_name,
            "lastName": last_name,
        }
        if phone is not None:
            body["phone"] = phone
        raw = self._http.post("/v1/user/account", body=body)
        return ApiResponse.from_dict(raw, data_factory=User)

    def fetch_access_token(self, user_id: int) -> ApiResponse:
        """
        Fetch the long-lived bearer token for a user.
        Call client.set_access_token(response.data.token) after this.

        Args:
            user_id: The user ID returned from create_user.

        Returns:
            ApiResponse with data as a User object containing the token field.
        """
        raw = self._http.get(f"/v1/user/access-token/{user_id}")
        return ApiResponse.from_dict(raw, data_factory=User)

    def verify_jwt(self, token: str) -> ApiResponse:
        """
        Verify that a JWT token is valid and has not expired.

        Args:
            token: The JWT string to verify.

        Returns:
            ApiResponse with verification result.
        """
        raw = self._http.post("/v1/user/verify-jwt", body={"token": token})
        return ApiResponse.from_dict(raw)
