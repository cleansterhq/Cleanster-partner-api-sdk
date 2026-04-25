package cleanster

import (
        "context"
        "fmt"
        "net/url"
        "strconv"
)

// OtherService provides utility endpoints used when building booking flows.
type OtherService struct {
        http *httpClient
}

// GetServices returns all available cleaning service types.
func (s *OtherService) GetServices(ctx context.Context) (APIResponse[[]map[string]interface{}], error) {
        raw, err := s.http.get(ctx, "/v1/services", nil)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// GetPlans returns all available booking plans for a given property.
func (s *OtherService) GetPlans(ctx context.Context, propertyID int) (APIResponse[[]map[string]interface{}], error) {
        q := url.Values{}
        q.Set("propertyId", strconv.Itoa(propertyID))
        raw, err := s.http.get(ctx, "/v1/plans", q)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// GetRecommendedHours returns the system-recommended number of cleaning hours
// based on the property size. Use the result to pre-fill the Hours field in CreateBookingRequest.
func (s *OtherService) GetRecommendedHours(ctx context.Context, propertyID, bathroomCount, roomCount int) (APIResponse[map[string]interface{}], error) {
        q := url.Values{}
        q.Set("propertyId", strconv.Itoa(propertyID))
        q.Set("bathroomCount", strconv.Itoa(bathroomCount))
        q.Set("roomCount", strconv.Itoa(roomCount))
        raw, err := s.http.get(ctx, "/v1/recommended-hours", q)
        if err != nil {
                return APIResponse[map[string]interface{}]{}, err
        }
        return decode[map[string]interface{}](raw)
}

// GetCostEstimate calculates the estimated price for a potential booking.
// Use this to show a cost preview before committing.
func (s *OtherService) GetCostEstimate(ctx context.Context, req CostEstimateRequest) (APIResponse[map[string]interface{}], error) {
        raw, err := s.http.post(ctx, "/v1/cost-estimate", req)
        if err != nil {
                return APIResponse[map[string]interface{}]{}, err
        }
        return decode[map[string]interface{}](raw)
}

// GetCleaningExtras returns available add-on services for a given service type
// (e.g., inside fridge, inside oven, laundry).
func (s *OtherService) GetCleaningExtras(ctx context.Context, serviceID int) (APIResponse[[]map[string]interface{}], error) {
        raw, err := s.http.get(ctx, fmt.Sprintf("/v1/cleaning-extras/%d", serviceID), nil)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// GetAvailableCleaners finds cleaners available for a specific property, date, and time slot.
func (s *OtherService) GetAvailableCleaners(ctx context.Context, req AvailableCleanersRequest) (APIResponse[[]map[string]interface{}], error) {
        raw, err := s.http.post(ctx, "/v1/available-cleaners", req)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// GetCoupons returns all valid coupon codes available at booking creation.
func (s *OtherService) GetCoupons(ctx context.Context) (APIResponse[[]map[string]interface{}], error) {
        raw, err := s.http.get(ctx, "/v1/coupons", nil)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// ListCleaners returns a list of cleaners. Pass empty strings to omit filters.
func (s *OtherService) ListCleaners(ctx context.Context, status, search string) (APIResponse[[]map[string]interface{}], error) {
        q := url.Values{}
        if status != "" {
                q.Set("status", status)
        }
        if search != "" {
                q.Set("search", search)
        }
        var qp url.Values
        if len(q) > 0 {
                qp = q
        }
        raw, err := s.http.get(ctx, "/v1/cleaners", qp)
        if err != nil {
                return APIResponse[[]map[string]interface{}]{}, err
        }
        return decode[[]map[string]interface{}](raw)
}

// GetCleaner retrieves a single cleaner by their ID.
func (s *OtherService) GetCleaner(ctx context.Context, cleanerID int) (APIResponse[map[string]interface{}], error) {
        raw, err := s.http.get(ctx, "/v1/cleaners/"+strconv.Itoa(cleanerID), nil)
        if err != nil {
                return APIResponse[map[string]interface{}]{}, err
        }
        return decode[map[string]interface{}](raw)
}
