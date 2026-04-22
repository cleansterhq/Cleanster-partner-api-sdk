"""
Exception hierarchy for the Cleanster Python SDK.
"""


class CleansterException(Exception):
    """
    Base exception for all Cleanster SDK errors.
    Raised for network errors, timeouts, and unexpected SDK-level failures.
    """
    pass


class CleansterAuthException(CleansterException):
    """
    Raised when the API returns HTTP 401 Unauthorized.
    This indicates an invalid or missing access key or user token.
    """

    def __init__(self, message: str, response_body: str = ""):
        super().__init__(message)
        self.status_code = 401
        self.response_body = response_body


class CleansterApiException(CleansterException):
    """
    Raised when the API returns a non-2xx HTTP response (excluding 401).
    Includes the HTTP status code and raw response body for debugging.
    """

    def __init__(self, status_code: int, message: str, response_body: str = ""):
        super().__init__(message)
        self.status_code = status_code
        self.response_body = response_body
