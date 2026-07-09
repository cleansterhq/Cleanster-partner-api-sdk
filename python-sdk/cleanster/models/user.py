"""User models."""

from typing import Any, Dict, Optional


class User:
    """Represents a Cleanster end-user account (e.g. from GET /v1/user/access-token/{id})."""

    def __init__(self, data: Dict[str, Any]):
        self.id: Optional[int] = data.get("id")
        self.email: Optional[str] = data.get("email")
        self.first_name: Optional[str] = data.get("firstName")
        self.last_name: Optional[str] = data.get("lastName")
        self.phone: Optional[str] = data.get("phone")
        self.token: Optional[str] = data.get("token")
        self._raw = data

    def __repr__(self) -> str:
        return f"User(id={self.id}, email={self.email!r})"


class CreateUserResponse:
    """
    Response from POST /v1/user/account.

    Confirmed against the live sandbox API: creating a user does NOT return a full
    user profile - only the new Cleanster user ID and a per-user JWT. ``access_token``
    is already prefixed with ``"Bearer "``; use :attr:`access_token_without_prefix` if
    your HTTP layer adds that prefix itself.
    """

    def __init__(self, data: Dict[str, Any]):
        self.user_id: Optional[int] = data.get("userId")
        self.access_token: Optional[str] = data.get("accessToken")
        self._raw = data

    @property
    def access_token_without_prefix(self) -> Optional[str]:
        if self.access_token is None:
            return None
        prefix = "Bearer "
        return self.access_token[len(prefix):] if self.access_token.startswith(prefix) else self.access_token

    def __repr__(self) -> str:
        return f"CreateUserResponse(user_id={self.user_id})"
