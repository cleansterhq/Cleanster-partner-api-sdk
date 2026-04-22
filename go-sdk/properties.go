package cleanster

import (
	"context"
	"fmt"
	"net/url"
	"strconv"
)

// PropertiesService manages physical cleaning locations.
type PropertiesService struct {
	http *httpClient
}

// ListProperties returns all properties, optionally filtered by service type.
// Pass serviceID = 0 to return all service types.
func (s *PropertiesService) ListProperties(ctx context.Context, serviceID int) (APIResponse[[]Property], error) {
	q := url.Values{}
	if serviceID != 0 {
		q.Set("serviceId", strconv.Itoa(serviceID))
	}
	raw, err := s.http.get(ctx, "/v1/properties", q)
	if err != nil {
		return APIResponse[[]Property]{}, err
	}
	return decode[[]Property](raw)
}

// AddProperty adds a new property to the partner account.
func (s *PropertiesService) AddProperty(ctx context.Context, req CreatePropertyRequest) (APIResponse[Property], error) {
	raw, err := s.http.post(ctx, "/v1/properties", req)
	if err != nil {
		return APIResponse[Property]{}, err
	}
	return decode[Property](raw)
}

// GetProperty returns details of a specific property.
func (s *PropertiesService) GetProperty(ctx context.Context, propertyID int) (APIResponse[Property], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/properties/%d", propertyID), nil)
	if err != nil {
		return APIResponse[Property]{}, err
	}
	return decode[Property](raw)
}

// UpdateProperty replaces all fields of an existing property.
func (s *PropertiesService) UpdateProperty(ctx context.Context, propertyID int, req CreatePropertyRequest) (APIResponse[Property], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/properties/%d", propertyID), req)
	if err != nil {
		return APIResponse[Property]{}, err
	}
	return decode[Property](raw)
}

// UpdateAdditionalInformation updates freeform additional information for a property.
func (s *PropertiesService) UpdateAdditionalInformation(ctx context.Context, propertyID int, data map[string]interface{}) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/properties/%d/additional-information", propertyID), data)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// EnableOrDisableProperty toggles a property's active state.
func (s *PropertiesService) EnableOrDisableProperty(ctx context.Context, propertyID int, req EnableDisablePropertyRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/properties/%d/enable-disable", propertyID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// DeleteProperty permanently deletes a property.
func (s *PropertiesService) DeleteProperty(ctx context.Context, propertyID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/properties/%d", propertyID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetPropertyCleaners returns all cleaners assigned to a property.
func (s *PropertiesService) GetPropertyCleaners(ctx context.Context, propertyID int) (APIResponse[[]map[string]interface{}], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/properties/%d/cleaners", propertyID), nil)
	if err != nil {
		return APIResponse[[]map[string]interface{}]{}, err
	}
	return decode[[]map[string]interface{}](raw)
}

// AssignCleanerToProperty adds a cleaner to a property's default cleaner pool.
func (s *PropertiesService) AssignCleanerToProperty(ctx context.Context, propertyID int, req AssignCleanerToPropertyRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/properties/%d/cleaners", propertyID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// UnassignCleanerFromProperty removes a cleaner from a property's default cleaner pool.
func (s *PropertiesService) UnassignCleanerFromProperty(ctx context.Context, propertyID, cleanerID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/properties/%d/cleaners/%d", propertyID, cleanerID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AddICalLink sets an iCal feed URL on a property for calendar availability syncing.
func (s *PropertiesService) AddICalLink(ctx context.Context, propertyID int, req ICalRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/properties/%d/ical", propertyID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetICalLink returns the current iCal feed URL for a property.
func (s *PropertiesService) GetICalLink(ctx context.Context, propertyID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/properties/%d/ical", propertyID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// RemoveICalLink removes the iCal feed URL from a property.
func (s *PropertiesService) RemoveICalLink(ctx context.Context, propertyID int, req ICalRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/properties/%d/ical", propertyID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AssignChecklistToProperty sets the default checklist for a property.
// If updateUpcomingBookings is true, the checklist is also applied to all future bookings at this property.
func (s *PropertiesService) AssignChecklistToProperty(ctx context.Context, propertyID, checklistID int, updateUpcomingBookings bool) (APIResponse[map[string]interface{}], error) {
	path := fmt.Sprintf("/v1/properties/%d/checklist/%d?updateUpcomingBookings=%v", propertyID, checklistID, updateUpcomingBookings)
	raw, err := s.http.put(ctx, path, nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
