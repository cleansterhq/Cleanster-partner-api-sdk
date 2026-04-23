package cleanster

import "encoding/json"

// APIResponse is the standard response wrapper returned by every SDK method.
// T is the type of the Data field (e.g., Booking, []Booking, User, etc.).
type APIResponse[T any] struct {
        // Status is an HTTP-style status code (e.g., 200).
        Status int `json:"status"`

        // Message is a human-readable status message (e.g., "OK").
        Message string `json:"message"`

        // Data is the typed response payload.
        Data T `json:"data"`
}

// rawAPIResponse is used internally to parse the outer envelope before
// deserializing the Data field into the appropriate model type.
type rawAPIResponse struct {
        Status  int             `json:"status"`
        Message string          `json:"message"`
        Data    json.RawMessage `json:"data"`
}

// decode unmarshals a rawAPIResponse.Data into a typed APIResponse[T].
func decode[T any](raw rawAPIResponse) (APIResponse[T], error) {
        var data T
        if err := json.Unmarshal(raw.Data, &data); err != nil {
                return APIResponse[T]{}, &CleansterError{
                        Message: "failed to parse response data: " + err.Error(),
                }
        }
        return APIResponse[T]{Status: raw.Status, Message: raw.Message, Data: data}, nil
}

// ---------------------------------------------------------------------------
// Booking
// ---------------------------------------------------------------------------

// Booking represents a single cleaning appointment.
type Booking struct {
        ID              int      `json:"id"`
        Status          string   `json:"status"`
        Date            string   `json:"date"`
        Time            string   `json:"time"`
        Hours           float64  `json:"hours"`
        Cost            float64  `json:"cost"`
        PropertyID      int      `json:"propertyId"`
        CleanerID       *int     `json:"cleanerId"`
        PlanID          int      `json:"planId"`
        RoomCount       int      `json:"roomCount"`
        BathroomCount   int      `json:"bathroomCount"`
        ExtraSupplies   bool     `json:"extraSupplies"`
        PaymentMethodID int      `json:"paymentMethodId"`
        PostedBy        *string  `json:"postedBy"`
}

// CreateBookingRequest contains all fields required to schedule a new booking.
type CreateBookingRequest struct {
        Date            string  `json:"date"`
        Time            string  `json:"time"`
        PropertyID      int     `json:"propertyId"`
        RoomCount       int     `json:"roomCount"`
        BathroomCount   int     `json:"bathroomCount"`
        PlanID          int     `json:"planId"`
        Hours           float64 `json:"hours"`
        ExtraSupplies   bool    `json:"extraSupplies"`
        PaymentMethodID int     `json:"paymentMethodId"`
        CouponCode      string  `json:"couponCode,omitempty"`
        Extras          []int   `json:"extras,omitempty"`
}

// CancelBookingRequest holds an optional cancellation reason.
type CancelBookingRequest struct {
        Reason string `json:"reason,omitempty"`
}

// RescheduleBookingRequest holds the new date and time for a rescheduled booking.
type RescheduleBookingRequest struct {
        Date string `json:"date"`
        Time string `json:"time"`
}

// AssignCleanerRequest holds the cleaner ID to assign.
type AssignCleanerRequest struct {
        CleanerID int `json:"cleanerId"`
}

// AdjustHoursRequest holds the updated number of hours for a booking.
type AdjustHoursRequest struct {
        Hours float64 `json:"hours"`
}

// PayExpensesRequest holds the payment method to use for outstanding expenses.
type PayExpensesRequest struct {
        PaymentMethodID int `json:"paymentMethodId"`
}

// FeedbackRequest holds a star rating and optional comment for a completed booking.
type FeedbackRequest struct {
        Rating  int    `json:"rating"`
        Comment string `json:"comment,omitempty"`
}

// TipRequest holds the tip amount and payment method for a completed booking.
type TipRequest struct {
        Amount          float64 `json:"amount"`
        PaymentMethodID int     `json:"paymentMethodId"`
}

// SendMessageRequest holds a chat message to post in a booking thread.
type SendMessageRequest struct {
        Message string `json:"message"`
}

// GetBookingsParams holds optional filters for listing bookings.
type GetBookingsParams struct {
        // PageNo is the page number (1-based). Pass nil for the first page.
        PageNo *int

        // Status filters results by booking status.
        // Valid values: "OPEN", "CLEANER_ASSIGNED", "COMPLETED", "CANCELLED", "REMOVED"
        Status string
}

// ---------------------------------------------------------------------------
// User
// ---------------------------------------------------------------------------

// User represents a Cleanster end-user account.
type User struct {
        ID        int     `json:"id"`
        Email     string  `json:"email"`
        FirstName string  `json:"firstName"`
        LastName  string  `json:"lastName"`
        Phone     *string `json:"phone"`
        Token     *string `json:"token"`
}

// CreateUserRequest holds fields needed to register a new user.
type CreateUserRequest struct {
        Email     string `json:"email"`
        FirstName string `json:"firstName"`
        LastName  string `json:"lastName"`
        Phone     string `json:"phone,omitempty"`
}

// VerifyJWTRequest holds the JWT token to verify.
type VerifyJWTRequest struct {
        Token string `json:"token"`
}

// ---------------------------------------------------------------------------
// Property
// ---------------------------------------------------------------------------

// Property represents a physical location where cleanings take place.
type Property struct {
        ID            int    `json:"id"`
        Name          string `json:"name"`
        Address       string `json:"address"`
        City          string `json:"city"`
        Country       string `json:"country"`
        RoomCount     int    `json:"roomCount"`
        BathroomCount int    `json:"bathroomCount"`
        ServiceID     int    `json:"serviceId"`
        IsEnabled     *bool  `json:"isEnabled"`
}

// CreatePropertyRequest holds required and optional fields for creating or updating a property.
type CreatePropertyRequest struct {
        Name          string   `json:"name"`
        Address       string   `json:"address"`
        City          string   `json:"city"`
        Country       string   `json:"country"`
        RoomCount     int      `json:"roomCount"`
        BathroomCount int      `json:"bathroomCount"`
        ServiceID     int      `json:"serviceId"`
        State         string   `json:"state,omitempty"`
        Zip           string   `json:"zip,omitempty"`
        Timezone      string   `json:"timezone,omitempty"`
        Note          string   `json:"note,omitempty"`
        Latitude      *float64 `json:"latitude,omitempty"`
        Longitude     *float64 `json:"longitude,omitempty"`
}

// EnableDisablePropertyRequest holds the enabled flag for toggling a property.
type EnableDisablePropertyRequest struct {
        Enabled bool `json:"enabled"`
}

// AssignCleanerToPropertyRequest holds the cleaner to assign to a property.
type AssignCleanerToPropertyRequest struct {
        CleanerID int `json:"cleanerId"`
}

// ICalRequest holds an iCal feed URL for a property.
type ICalRequest struct {
        ICalLink string `json:"icalLink"`
}

// ---------------------------------------------------------------------------
// Checklist
// ---------------------------------------------------------------------------

// ChecklistItem is a single task within a checklist.
type ChecklistItem struct {
        ID          int     `json:"id"`
        Description string  `json:"description"`
        IsCompleted bool    `json:"isCompleted"`
        ImageURL    *string `json:"imageUrl"`
}

// Checklist is a named collection of cleaning task items.
type Checklist struct {
        ID    int             `json:"id"`
        Name  string          `json:"name"`
        Items []ChecklistItem `json:"items"`
}

// CreateChecklistRequest holds the name and items for a new checklist.
type CreateChecklistRequest struct {
        Name  string   `json:"name"`
        Items []string `json:"items"`
}

// ---------------------------------------------------------------------------
// Payment Method
// ---------------------------------------------------------------------------

// PaymentMethod represents a saved Stripe card or PayPal account.
type PaymentMethod struct {
        ID        int     `json:"id"`
        Type      string  `json:"type"`
        LastFour  *string `json:"lastFour"`
        Brand     *string `json:"brand"`
        IsDefault bool    `json:"isDefault"`
}

// AddPaymentMethodRequest holds the tokenized payment method identifier.
type AddPaymentMethodRequest struct {
        PaymentMethodID string `json:"paymentMethodId"`
}

// ---------------------------------------------------------------------------
// Other / Utility
// ---------------------------------------------------------------------------

// CostEstimateRequest holds fields needed to calculate an estimated booking cost.
type CostEstimateRequest struct {
        PropertyID  int     `json:"propertyId"`
        PlanID      int     `json:"planId"`
        Hours       float64 `json:"hours"`
        CouponCode  string  `json:"couponCode,omitempty"`
        Extras      []int   `json:"extras,omitempty"`
}

// AvailableCleanersRequest holds search criteria for finding available cleaners.
type AvailableCleanersRequest struct {
        PropertyID int    `json:"propertyId"`
        Date       string `json:"date"`
        Time       string `json:"time"`
}

// ---------------------------------------------------------------------------
// Blacklist
// ---------------------------------------------------------------------------

// BlacklistRequest holds the cleaner to add to or remove from the blacklist.
type BlacklistRequest struct {
        CleanerID int    `json:"cleanerId"`
        Reason    string `json:"reason,omitempty"`
}

// ---------------------------------------------------------------------------
// Webhook
// ---------------------------------------------------------------------------

// WebhookRequest holds the URL and event type for creating or updating a webhook.
type WebhookRequest struct {
        URL   string `json:"url"`
        Event string `json:"event"`
}
