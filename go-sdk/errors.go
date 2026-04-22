package cleanster

import "fmt"

// CleansterError is the base error type for the Cleanster SDK.
// Returned for network failures, timeouts, JSON parse errors, and other SDK-level issues.
type CleansterError struct {
	Message string
}

func (e *CleansterError) Error() string {
	return fmt.Sprintf("cleanster: %s", e.Message)
}

// AuthError is returned when the API responds with HTTP 401 Unauthorized.
// This indicates an invalid or missing access key or user bearer token.
type AuthError struct {
	// StatusCode is always 401.
	StatusCode int

	// ResponseBody is the raw response body returned by the API.
	ResponseBody string
}

func (e *AuthError) Error() string {
	return fmt.Sprintf("cleanster: authentication failed (HTTP %d)", e.StatusCode)
}

// APIError is returned when the API responds with a non-2xx HTTP status (other than 401).
// Inspect StatusCode to determine whether the error is a client (4xx) or server (5xx) error.
type APIError struct {
	// StatusCode is the HTTP status code (e.g., 404, 422, 500).
	StatusCode int

	// Message is a human-readable description of the error.
	Message string

	// ResponseBody is the raw response body returned by the API.
	ResponseBody string
}

func (e *APIError) Error() string {
	return fmt.Sprintf("cleanster: API error (HTTP %d): %s", e.StatusCode, e.Message)
}
