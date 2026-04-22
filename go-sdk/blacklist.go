package cleanster

import "context"

// BlacklistService manages the list of cleaners blocked from auto-assignment.
type BlacklistService struct {
	http *httpClient
}

// ListBlacklistedCleaners returns all cleaners currently on the blacklist.
func (s *BlacklistService) ListBlacklistedCleaners(ctx context.Context) (APIResponse[[]map[string]interface{}], error) {
	raw, err := s.http.get(ctx, "/v1/blacklist/cleaner", nil)
	if err != nil {
		return APIResponse[[]map[string]interface{}]{}, err
	}
	return decode[[]map[string]interface{}](raw)
}

// AddToBlacklist prevents a cleaner from being auto-assigned to bookings.
func (s *BlacklistService) AddToBlacklist(ctx context.Context, req BlacklistRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, "/v1/blacklist/cleaner", req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// RemoveFromBlacklist re-enables a previously blacklisted cleaner for auto-assignment.
func (s *BlacklistService) RemoveFromBlacklist(ctx context.Context, req BlacklistRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, "/v1/blacklist/cleaner", req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
