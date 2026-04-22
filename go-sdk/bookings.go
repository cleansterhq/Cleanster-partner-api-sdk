package cleanster

import (
	"context"
	"fmt"
	"net/url"
	"strconv"
)

// BookingsService manages the full lifecycle of cleaning appointments.
type BookingsService struct {
	http *httpClient
}

// GetBookings retrieves a paginated list of bookings.
// All filter parameters are optional.
func (s *BookingsService) GetBookings(ctx context.Context, params GetBookingsParams) (APIResponse[[]Booking], error) {
	q := url.Values{}
	if params.PageNo != nil {
		q.Set("pageNo", strconv.Itoa(*params.PageNo))
	}
	if params.Status != "" {
		q.Set("status", params.Status)
	}
	raw, err := s.http.get(ctx, "/v1/bookings", q)
	if err != nil {
		return APIResponse[[]Booking]{}, err
	}
	return decode[[]Booking](raw)
}

// CreateBooking schedules a new cleaning appointment.
func (s *BookingsService) CreateBooking(ctx context.Context, req CreateBookingRequest) (APIResponse[Booking], error) {
	raw, err := s.http.post(ctx, "/v1/bookings/create", req)
	if err != nil {
		return APIResponse[Booking]{}, err
	}
	return decode[Booking](raw)
}

// GetBookingDetails returns full details of a specific booking.
func (s *BookingsService) GetBookingDetails(ctx context.Context, bookingID int) (APIResponse[Booking], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/bookings/%d", bookingID), nil)
	if err != nil {
		return APIResponse[Booking]{}, err
	}
	return decode[Booking](raw)
}

// CancelBooking cancels a booking. Provide an optional reason in the request.
func (s *BookingsService) CancelBooking(ctx context.Context, bookingID int, req CancelBookingRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/cancel", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// RescheduleBooking moves a booking to a new date and time.
func (s *BookingsService) RescheduleBooking(ctx context.Context, bookingID int, req RescheduleBookingRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/reschedule", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AssignCleaner manually assigns a specific cleaner to a booking.
func (s *BookingsService) AssignCleaner(ctx context.Context, bookingID int, req AssignCleanerRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/cleaner", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// RemoveAssignedCleaner removes the currently assigned cleaner from a booking.
func (s *BookingsService) RemoveAssignedCleaner(ctx context.Context, bookingID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/bookings/%d/cleaner", bookingID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AdjustHours updates the number of hours for a booking.
func (s *BookingsService) AdjustHours(ctx context.Context, bookingID int, req AdjustHoursRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/hours", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// PayExpenses pays outstanding expenses for a completed booking (within 72h of completion).
func (s *BookingsService) PayExpenses(ctx context.Context, bookingID int, req PayExpensesRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/expenses", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetBookingInspection returns the inspection report for a completed booking.
func (s *BookingsService) GetBookingInspection(ctx context.Context, bookingID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/bookings/%d/inspection", bookingID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetBookingInspectionDetails returns detailed inspection information for a completed booking.
func (s *BookingsService) GetBookingInspectionDetails(ctx context.Context, bookingID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/bookings/%d/inspection/details", bookingID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AssignChecklistToBooking attaches a checklist to a specific booking,
// overriding the property's default checklist for this appointment only.
func (s *BookingsService) AssignChecklistToBooking(ctx context.Context, bookingID, checklistID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/bookings/%d/checklist/%d", bookingID, checklistID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// SubmitFeedback submits a star rating (1–5) and optional comment after a booking completes.
func (s *BookingsService) SubmitFeedback(ctx context.Context, bookingID int, req FeedbackRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/feedback", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AddTip adds a tip for the cleaner (within 72 hours of booking completion).
func (s *BookingsService) AddTip(ctx context.Context, bookingID int, req TipRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/tip", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetChat retrieves all chat messages in a booking thread.
func (s *BookingsService) GetChat(ctx context.Context, bookingID int) (APIResponse[[]map[string]interface{}], error) {
	raw, err := s.http.get(ctx, fmt.Sprintf("/v1/bookings/%d/chat", bookingID), nil)
	if err != nil {
		return APIResponse[[]map[string]interface{}]{}, err
	}
	return decode[[]map[string]interface{}](raw)
}

// SendMessage posts a chat message to a booking thread.
func (s *BookingsService) SendMessage(ctx context.Context, bookingID int, req SendMessageRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, fmt.Sprintf("/v1/bookings/%d/chat", bookingID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// DeleteMessage deletes a specific chat message from a booking thread.
func (s *BookingsService) DeleteMessage(ctx context.Context, bookingID int, messageID string) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/bookings/%d/chat/%s", bookingID, messageID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
