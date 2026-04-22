package cleanster

import (
	"context"
	"fmt"
)

// UsersService manages end-user accounts and authentication tokens.
type UsersService struct {
	http *httpClient
}

// CreateUser registers a new user account under your partner.
func (s *UsersService) CreateUser(ctx context.Context, req CreateUserRequest) (APIResponse[User], error) {
	raw, err := s.http.post(ctx, "/v1/user/account", req)
	if err != nil {
		return APIResponse[User]{}, err
	}
	return decode[User](raw)
}

// FetchAccessToken fetches the long-lived bearer token for a user.
// Pass the returned token to client.SetAccessToken for subsequent requests.
func (s *UsersService) FetchAccessToken(ctx context.Context, userID int) (APIResponse[User], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/user/access-token/%d", userID), nil)
	if err != nil {
		return APIResponse[User]{}, err
	}
	return decode[User](raw)
}

// VerifyJWT checks whether a JWT token is valid and has not expired.
func (s *UsersService) VerifyJWT(ctx context.Context, req VerifyJWTRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, "/v1/user/verify-jwt", req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
