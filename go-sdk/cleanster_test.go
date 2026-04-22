package cleanster_test

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	cleanster "github.com/cleanster/cleanster-go-sdk"
)

// ---------------------------------------------------------------------------
// Test helpers
// ---------------------------------------------------------------------------

func writeJSON(w http.ResponseWriter, v interface{}) {
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(v); err != nil {
		panic(err)
	}
}

func okResponse(data interface{}) map[string]interface{} {
	return map[string]interface{}{
		"status":  200,
		"message": "OK",
		"data":    data,
	}
}

func newTestClient(t *testing.T, handler http.HandlerFunc) (*cleanster.Client, func()) {
	t.Helper()
	srv := httptest.NewServer(handler)
	cfg := cleanster.Config{
		AccessKey: "test-access-key",
		BaseURL:   srv.URL,
		Timeout:   5 * time.Second,
	}
	client, err := cleanster.NewClient(cfg)
	if err != nil {
		t.Fatalf("failed to create test client: %v", err)
	}
	return client, srv.Close
}

func mustPageNo(n int) *int { return &n }

// ---------------------------------------------------------------------------
// Config
// ---------------------------------------------------------------------------

func TestNewSandboxConfig(t *testing.T) {
	cfg := cleanster.NewSandboxConfig("my-key")
	if cfg.BaseURL != cleanster.SandboxBaseURL {
		t.Errorf("expected sandbox URL, got %s", cfg.BaseURL)
	}
	if cfg.AccessKey != "my-key" {
		t.Errorf("expected my-key, got %s", cfg.AccessKey)
	}
}

func TestNewProductionConfig(t *testing.T) {
	cfg := cleanster.NewProductionConfig("my-key")
	if cfg.BaseURL != cleanster.ProductionBaseURL {
		t.Errorf("expected production URL, got %s", cfg.BaseURL)
	}
}

func TestDefaultTimeout(t *testing.T) {
	if cleanster.DefaultTimeout <= 0 {
		t.Error("DefaultTimeout must be positive")
	}
}

func TestNewClient_BlankAccessKey(t *testing.T) {
	_, err := cleanster.NewClient(cleanster.Config{AccessKey: "", BaseURL: "https://example.com"})
	if err == nil {
		t.Fatal("expected error for blank AccessKey")
	}
}

func TestNewClient_WhitespaceAccessKey(t *testing.T) {
	_, err := cleanster.NewClient(cleanster.Config{AccessKey: "   ", BaseURL: "https://example.com"})
	if err == nil {
		t.Fatal("expected error for whitespace-only AccessKey")
	}
}

func TestNewClient_BlankBaseURL(t *testing.T) {
	_, err := cleanster.NewClient(cleanster.Config{AccessKey: "key", BaseURL: ""})
	if err == nil {
		t.Fatal("expected error for blank BaseURL")
	}
}

func TestNewSandboxClient(t *testing.T) {
	_, err := cleanster.NewSandboxClient("my-key")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
}

func TestNewProductionClient(t *testing.T) {
	_, err := cleanster.NewProductionClient("my-key")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
}

// ---------------------------------------------------------------------------
// Client — services and token management
// ---------------------------------------------------------------------------

func TestClient_AllServicesExposed(t *testing.T) {
	client, cleanup := newTestClient(t, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {}))
	defer cleanup()

	if client.Bookings == nil {
		t.Error("Bookings service is nil")
	}
	if client.Users == nil {
		t.Error("Users service is nil")
	}
	if client.Properties == nil {
		t.Error("Properties service is nil")
	}
	if client.Checklists == nil {
		t.Error("Checklists service is nil")
	}
	if client.Other == nil {
		t.Error("Other service is nil")
	}
	if client.Blacklist == nil {
		t.Error("Blacklist service is nil")
	}
	if client.PaymentMethods == nil {
		t.Error("PaymentMethods service is nil")
	}
	if client.Webhooks == nil {
		t.Error("Webhooks service is nil")
	}
}

func TestClient_AccessToken_DefaultEmpty(t *testing.T) {
	client, cleanup := newTestClient(t, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {}))
	defer cleanup()
	if client.GetAccessToken() != "" {
		t.Error("expected empty access token by default")
	}
}

func TestClient_SetAndGetAccessToken(t *testing.T) {
	client, cleanup := newTestClient(t, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {}))
	defer cleanup()
	client.SetAccessToken("bearer-xyz")
	if client.GetAccessToken() != "bearer-xyz" {
		t.Errorf("expected bearer-xyz, got %s", client.GetAccessToken())
	}
}

func TestClient_ClearAccessToken(t *testing.T) {
	client, cleanup := newTestClient(t, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {}))
	defer cleanup()
	client.SetAccessToken("tok")
	client.SetAccessToken("")
	if client.GetAccessToken() != "" {
		t.Error("expected empty token after clearing")
	}
}

func TestClient_AuthHeadersSent(t *testing.T) {
	var gotAccessKey, gotToken string
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		gotAccessKey = r.Header.Get("access-key")
		gotToken = r.Header.Get("token")
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	client.SetAccessToken("user-bearer-token")
	_, _ = client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	if gotAccessKey != "test-access-key" {
		t.Errorf("expected access-key=test-access-key, got %s", gotAccessKey)
	}
	if gotToken != "user-bearer-token" {
		t.Errorf("expected token=user-bearer-token, got %s", gotToken)
	}
}

// ---------------------------------------------------------------------------
// BookingsService
// ---------------------------------------------------------------------------

func TestBookings_GetBookings_NoParams(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings" {
			t.Errorf("expected path /v1/bookings, got %s", r.URL.Path)
		}
		if r.Method != http.MethodGet {
			t.Errorf("expected GET, got %s", r.Method)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Status != 200 {
		t.Errorf("expected status 200, got %d", resp.Status)
	}
}

func TestBookings_GetBookings_WithStatus(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Query().Get("status") != "OPEN" {
			t.Errorf("expected status=OPEN, got %s", r.URL.Query().Get("status"))
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{Status: "OPEN"})
}

func TestBookings_GetBookings_WithPageNo(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Query().Get("pageNo") != "2" {
			t.Errorf("expected pageNo=2, got %s", r.URL.Query().Get("pageNo"))
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{PageNo: mustPageNo(2)})
}

func TestBookings_CreateBooking(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/create" {
			t.Errorf("expected /v1/bookings/create, got %s", r.URL.Path)
		}
		if r.Method != http.MethodPost {
			t.Errorf("expected POST, got %s", r.Method)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["date"] != "2025-06-15" {
			t.Errorf("expected date=2025-06-15, got %v", body["date"])
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1, "status": "OPEN", "date": "2025-06-15", "time": "10:00", "hours": 3.0, "cost": 150.0, "propertyId": 1004, "planId": 2, "roomCount": 2, "bathroomCount": 1, "extraSupplies": false, "paymentMethodId": 10}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Bookings.CreateBooking(context.Background(), cleanster.CreateBookingRequest{
		Date: "2025-06-15", Time: "10:00", PropertyID: 1004,
		RoomCount: 2, BathroomCount: 1, PlanID: 2, Hours: 3,
		ExtraSupplies: false, PaymentMethodID: 10,
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.Status != "OPEN" {
		t.Errorf("expected status OPEN, got %s", resp.Data.Status)
	}
}

func TestBookings_GetBookingDetails(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926" {
			t.Errorf("expected /v1/bookings/16926, got %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 16926, "status": "COMPLETED", "date": "2025-06-15", "time": "10:00", "hours": 3.0, "cost": 150.0, "propertyId": 1004, "planId": 2, "roomCount": 2, "bathroomCount": 1, "extraSupplies": false, "paymentMethodId": 10}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Bookings.GetBookingDetails(context.Background(), 16926)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.ID != 16926 {
		t.Errorf("expected ID 16926, got %d", resp.Data.ID)
	}
}

func TestBookings_CancelBooking_WithReason(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16459/cancel" {
			t.Errorf("expected /v1/bookings/16459/cancel, got %s", r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["reason"] != "Changed plans" {
			t.Errorf("expected reason=Changed plans, got %v", body["reason"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.CancelBooking(context.Background(), 16459, cleanster.CancelBookingRequest{Reason: "Changed plans"})
}

func TestBookings_CancelBooking_WithoutReason(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if _, ok := body["reason"]; ok {
			t.Error("reason field should be omitted when empty")
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.CancelBooking(context.Background(), 16459, cleanster.CancelBookingRequest{})
}

func TestBookings_RescheduleBooking(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16459/reschedule" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["date"] != "2025-07-01" || body["time"] != "14:00" {
			t.Errorf("wrong body: %v", body)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.RescheduleBooking(context.Background(), 16459, cleanster.RescheduleBookingRequest{Date: "2025-07-01", Time: "14:00"})
}

func TestBookings_AssignCleaner(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16459/cleaner" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["cleanerId"] != float64(5) {
			t.Errorf("expected cleanerId=5, got %v", body["cleanerId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.AssignCleaner(context.Background(), 16459, cleanster.AssignCleanerRequest{CleanerID: 5})
}

func TestBookings_RemoveAssignedCleaner(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodDelete || r.URL.Path != "/v1/bookings/16459/cleaner" {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.RemoveAssignedCleaner(context.Background(), 16459)
}

func TestBookings_AdjustHours(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["hours"] != float64(4) {
			t.Errorf("expected hours=4, got %v", body["hours"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.AdjustHours(context.Background(), 16459, cleanster.AdjustHoursRequest{Hours: 4})
}

func TestBookings_PayExpenses(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/expenses" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["paymentMethodId"] != float64(10) {
			t.Errorf("expected paymentMethodId=10, got %v", body["paymentMethodId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.PayExpenses(context.Background(), 16926, cleanster.PayExpensesRequest{PaymentMethodID: 10})
}

func TestBookings_GetBookingInspection(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/inspection" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.GetBookingInspection(context.Background(), 16926)
}

func TestBookings_GetBookingInspectionDetails(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/inspection/details" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.GetBookingInspectionDetails(context.Background(), 16926)
}

func TestBookings_AssignChecklistToBooking(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/checklist/105" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.AssignChecklistToBooking(context.Background(), 16926, 105)
}

func TestBookings_SubmitFeedback(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/feedback" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["rating"] != float64(5) {
			t.Errorf("expected rating=5, got %v", body["rating"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.SubmitFeedback(context.Background(), 16926, cleanster.FeedbackRequest{Rating: 5, Comment: "Great!"})
}

func TestBookings_SubmitFeedback_OmitsEmptyComment(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if _, ok := body["comment"]; ok {
			t.Error("comment field should be omitted when empty")
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.SubmitFeedback(context.Background(), 16926, cleanster.FeedbackRequest{Rating: 4})
}

func TestBookings_AddTip(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/16926/tip" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["amount"] != float64(20) {
			t.Errorf("expected amount=20, got %v", body["amount"])
		}
		if body["paymentMethodId"] != float64(10) {
			t.Errorf("expected paymentMethodId=10, got %v", body["paymentMethodId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.AddTip(context.Background(), 16926, cleanster.TipRequest{Amount: 20, PaymentMethodID: 10})
}

func TestBookings_GetChat(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/17142/chat" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.GetChat(context.Background(), 17142)
}

func TestBookings_SendMessage(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/17142/chat" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["message"] != "Please bring extra towels." {
			t.Errorf("wrong message: %v", body["message"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.SendMessage(context.Background(), 17142, cleanster.SendMessageRequest{Message: "Please bring extra towels."})
}

func TestBookings_DeleteMessage(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/bookings/17142/chat/msg-abc-123" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Bookings.DeleteMessage(context.Background(), 17142, "msg-abc-123")
}

// ---------------------------------------------------------------------------
// UsersService
// ---------------------------------------------------------------------------

func TestUsers_CreateUser(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/user/account" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["email"] != "jane@example.com" {
			t.Errorf("expected email=jane@example.com, got %v", body["email"])
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 42, "email": "jane@example.com", "firstName": "Jane", "lastName": "Smith"}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Users.CreateUser(context.Background(), cleanster.CreateUserRequest{
		Email: "jane@example.com", FirstName: "Jane", LastName: "Smith",
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.Email != "jane@example.com" {
		t.Errorf("expected email=jane@example.com, got %s", resp.Data.Email)
	}
}

func TestUsers_CreateUser_WithPhone(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["phone"] != "+15551234567" {
			t.Errorf("expected phone, got %v", body["phone"])
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1, "email": "x@y.com", "firstName": "X", "lastName": "Y"}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Users.CreateUser(context.Background(), cleanster.CreateUserRequest{
		Email: "x@y.com", FirstName: "X", LastName: "Y", Phone: "+15551234567",
	})
}

func TestUsers_CreateUser_OmitsEmptyPhone(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if _, ok := body["phone"]; ok {
			t.Error("phone should be omitted when empty")
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1, "email": "x@y.com", "firstName": "X", "lastName": "Y"}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Users.CreateUser(context.Background(), cleanster.CreateUserRequest{Email: "x@y.com", FirstName: "X", LastName: "Y"})
}

func TestUsers_FetchAccessToken(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/user/access-token/42" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		token := "bearer-jwt-abc"
		writeJSON(w, okResponse(map[string]interface{}{"id": 42, "email": "j@x.com", "firstName": "J", "lastName": "X", "token": token}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Users.FetchAccessToken(context.Background(), 42)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.Token == nil || *resp.Data.Token != "bearer-jwt-abc" {
		t.Errorf("expected token bearer-jwt-abc")
	}
}

func TestUsers_VerifyJWT(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/user/verify-jwt" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["token"] != "eyJhbGci..." {
			t.Errorf("wrong token: %v", body["token"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Users.VerifyJWT(context.Background(), cleanster.VerifyJWTRequest{Token: "eyJhbGci..."})
}

// ---------------------------------------------------------------------------
// PropertiesService
// ---------------------------------------------------------------------------

func TestProperties_ListProperties_NoFilter(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		if r.URL.Query().Get("serviceId") != "" {
			t.Error("serviceId should not be set when not filtering")
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.ListProperties(context.Background(), 0)
}

func TestProperties_ListProperties_WithServiceID(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Query().Get("serviceId") != "1" {
			t.Errorf("expected serviceId=1, got %s", r.URL.Query().Get("serviceId"))
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.ListProperties(context.Background(), 1)
}

func TestProperties_AddProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1040, "name": "Beach House", "address": "123 St", "city": "Miami", "country": "USA", "roomCount": 3, "bathroomCount": 2, "serviceId": 1}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Properties.AddProperty(context.Background(), cleanster.CreatePropertyRequest{
		Name: "Beach House", Address: "123 St", City: "Miami", Country: "USA",
		RoomCount: 3, BathroomCount: 2, ServiceID: 1,
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.ID != 1040 {
		t.Errorf("expected ID=1040, got %d", resp.Data.ID)
	}
}

func TestProperties_GetProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1040, "name": "Condo", "address": "456 Ave", "city": "NYC", "country": "USA", "roomCount": 2, "bathroomCount": 1, "serviceId": 1}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Properties.GetProperty(context.Background(), 1040)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.City != "NYC" {
		t.Errorf("expected city=NYC, got %s", resp.Data.City)
	}
}

func TestProperties_UpdateProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040" || r.Method != http.MethodPut {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 1040, "name": "Updated", "address": "456 Ave", "city": "NYC", "country": "USA", "roomCount": 2, "bathroomCount": 1, "serviceId": 1}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.UpdateProperty(context.Background(), 1040, cleanster.CreatePropertyRequest{
		Name: "Updated", Address: "456 Ave", City: "NYC", Country: "USA", RoomCount: 2, BathroomCount: 1, ServiceID: 1,
	})
}

func TestProperties_EnableOrDisableProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/enable-disable" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["enabled"] != false {
			t.Errorf("expected enabled=false, got %v", body["enabled"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.EnableOrDisableProperty(context.Background(), 1040, cleanster.EnableDisablePropertyRequest{Enabled: false})
}

func TestProperties_DeleteProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.DeleteProperty(context.Background(), 1040)
}

func TestProperties_GetPropertyCleaners(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/cleaners" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.GetPropertyCleaners(context.Background(), 1040)
}

func TestProperties_AssignCleanerToProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/cleaners" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["cleanerId"] != float64(5) {
			t.Errorf("expected cleanerId=5, got %v", body["cleanerId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.AssignCleanerToProperty(context.Background(), 1040, cleanster.AssignCleanerToPropertyRequest{CleanerID: 5})
}

func TestProperties_UnassignCleanerFromProperty(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/cleaners/5" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.UnassignCleanerFromProperty(context.Background(), 1040, 5)
}

func TestProperties_AddICalLink(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/ical" || r.Method != http.MethodPut {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["icalLink"] != "https://calendar.example.com/feed.ics" {
			t.Errorf("wrong icalLink: %v", body["icalLink"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.AddICalLink(context.Background(), 1040, cleanster.ICalRequest{ICalLink: "https://calendar.example.com/feed.ics"})
}

func TestProperties_GetICalLink(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/properties/1040/ical" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.GetICalLink(context.Background(), 1040)
}

func TestProperties_RemoveICalLink(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodDelete {
			t.Errorf("expected DELETE, got %s", r.Method)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.RemoveICalLink(context.Background(), 1040, cleanster.ICalRequest{ICalLink: "https://calendar.example.com/feed.ics"})
}

func TestProperties_AssignChecklistToProperty_True(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if !strings.Contains(r.URL.String(), "updateUpcomingBookings=true") {
			t.Errorf("expected updateUpcomingBookings=true in URL, got %s", r.URL.String())
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.AssignChecklistToProperty(context.Background(), 1040, 105, true)
}

func TestProperties_AssignChecklistToProperty_False(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if !strings.Contains(r.URL.String(), "updateUpcomingBookings=false") {
			t.Errorf("expected updateUpcomingBookings=false in URL, got %s", r.URL.String())
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Properties.AssignChecklistToProperty(context.Background(), 1040, 105, false)
}

// ---------------------------------------------------------------------------
// ChecklistsService
// ---------------------------------------------------------------------------

func TestChecklists_ListChecklists(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/checklist" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Checklists.ListChecklists(context.Background())
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Status != 200 {
		t.Errorf("expected status 200, got %d", resp.Status)
	}
}

func TestChecklists_GetChecklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/checklist/105" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{
			"id": 105, "name": "Standard",
			"items": []interface{}{map[string]interface{}{"id": 1, "description": "Vacuum floors", "isCompleted": false}},
		}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Checklists.GetChecklist(context.Background(), 105)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.ID != 105 {
		t.Errorf("expected ID=105, got %d", resp.Data.ID)
	}
	if len(resp.Data.Items) != 1 {
		t.Errorf("expected 1 item, got %d", len(resp.Data.Items))
	}
	if resp.Data.Items[0].Description != "Vacuum floors" {
		t.Errorf("expected Vacuum floors, got %s", resp.Data.Items[0].Description)
	}
}

func TestChecklists_CreateChecklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/checklist" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["name"] != "Deep Clean" {
			t.Errorf("expected name=Deep Clean, got %v", body["name"])
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 105, "name": "Deep Clean", "items": []interface{}{}}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Checklists.CreateChecklist(context.Background(), cleanster.CreateChecklistRequest{
		Name: "Deep Clean", Items: []string{"Mop floors", "Wipe counters"},
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.Name != "Deep Clean" {
		t.Errorf("expected name=Deep Clean, got %s", resp.Data.Name)
	}
}

func TestChecklists_UpdateChecklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/checklist/105" || r.Method != http.MethodPut {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{"id": 105, "name": "Updated", "items": []interface{}{}}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Checklists.UpdateChecklist(context.Background(), 105, cleanster.CreateChecklistRequest{Name: "Updated", Items: []string{"New task"}})
}

func TestChecklists_DeleteChecklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/checklist/105" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Checklists.DeleteChecklist(context.Background(), 105)
}

// ---------------------------------------------------------------------------
// OtherService
// ---------------------------------------------------------------------------

func TestOther_GetServices(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/services" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetServices(context.Background())
}

func TestOther_GetPlans(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/plans" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		if r.URL.Query().Get("propertyId") != "1004" {
			t.Errorf("expected propertyId=1004, got %s", r.URL.Query().Get("propertyId"))
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetPlans(context.Background(), 1004)
}

func TestOther_GetRecommendedHours(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		q := r.URL.Query()
		if q.Get("propertyId") != "1004" || q.Get("bathroomCount") != "2" || q.Get("roomCount") != "3" {
			t.Errorf("wrong query params: %v", q)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetRecommendedHours(context.Background(), 1004, 2, 3)
}

func TestOther_CalculateCost(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/cost-estimate" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["couponCode"] != "20POFF" {
			t.Errorf("expected couponCode=20POFF, got %v", body["couponCode"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.CalculateCost(context.Background(), cleanster.CostEstimateRequest{PropertyID: 1004, PlanID: 2, Hours: 3, CouponCode: "20POFF"})
}

func TestOther_GetCleaningExtras(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/cleaning-extras/1" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetCleaningExtras(context.Background(), 1)
}

func TestOther_GetAvailableCleaners(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/available-cleaners" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["date"] != "2025-06-15" {
			t.Errorf("expected date=2025-06-15, got %v", body["date"])
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetAvailableCleaners(context.Background(), cleanster.AvailableCleanersRequest{PropertyID: 1004, Date: "2025-06-15", Time: "10:00"})
}

func TestOther_GetCoupons(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/coupons" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Other.GetCoupons(context.Background())
}

// ---------------------------------------------------------------------------
// BlacklistService
// ---------------------------------------------------------------------------

func TestBlacklist_ListBlacklistedCleaners(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/blacklist/cleaner" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Blacklist.ListBlacklistedCleaners(context.Background())
}

func TestBlacklist_AddToBlacklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/blacklist/cleaner" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["cleanerId"] != float64(7) {
			t.Errorf("expected cleanerId=7, got %v", body["cleanerId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Blacklist.AddToBlacklist(context.Background(), cleanster.BlacklistRequest{CleanerID: 7, Reason: "Damage"})
}

func TestBlacklist_AddToBlacklist_OmitsEmptyReason(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if _, ok := body["reason"]; ok {
			t.Error("reason should be omitted when empty")
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Blacklist.AddToBlacklist(context.Background(), cleanster.BlacklistRequest{CleanerID: 7})
}

func TestBlacklist_RemoveFromBlacklist(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/blacklist/cleaner" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["cleanerId"] != float64(7) {
			t.Errorf("expected cleanerId=7, got %v", body["cleanerId"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Blacklist.RemoveFromBlacklist(context.Background(), cleanster.BlacklistRequest{CleanerID: 7})
}

// ---------------------------------------------------------------------------
// PaymentMethodsService
// ---------------------------------------------------------------------------

func TestPaymentMethods_GetSetupIntentDetails(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods/setup-intent" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.GetSetupIntentDetails(context.Background())
}

func TestPaymentMethods_GetPaypalClientToken(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods/paypal-client-token" {
			t.Errorf("wrong path: %s", r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.GetPaypalClientToken(context.Background())
}

func TestPaymentMethods_AddPaymentMethod(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.AddPaymentMethod(context.Background(), cleanster.AddPaymentMethodRequest{PaymentMethodID: "pm_xxx"})
}

func TestPaymentMethods_GetPaymentMethods(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.GetPaymentMethods(context.Background())
}

func TestPaymentMethods_DeletePaymentMethod(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods/193" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.DeletePaymentMethod(context.Background(), 193)
}

func TestPaymentMethods_SetDefaultPaymentMethod(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/payment-methods/193/default" || r.Method != http.MethodPut {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.PaymentMethods.SetDefaultPaymentMethod(context.Background(), 193)
}

// ---------------------------------------------------------------------------
// WebhooksService
// ---------------------------------------------------------------------------

func TestWebhooks_ListWebhooks(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/webhooks" || r.Method != http.MethodGet {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse([]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Webhooks.ListWebhooks(context.Background())
}

func TestWebhooks_CreateWebhook(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/webhooks" || r.Method != http.MethodPost {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		var body map[string]interface{}
		json.NewDecoder(r.Body).Decode(&body)
		if body["url"] != "https://example.com/hooks" {
			t.Errorf("expected url=https://example.com/hooks, got %v", body["url"])
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Webhooks.CreateWebhook(context.Background(), cleanster.WebhookRequest{URL: "https://example.com/hooks", Event: "booking.status_changed"})
}

func TestWebhooks_UpdateWebhook(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/webhooks/50" || r.Method != http.MethodPut {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Webhooks.UpdateWebhook(context.Background(), 50, cleanster.WebhookRequest{URL: "https://example.com/v2/hooks", Event: "booking.status_changed"})
}

func TestWebhooks_DeleteWebhook(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/v1/webhooks/50" || r.Method != http.MethodDelete {
			t.Errorf("wrong method/path: %s %s", r.Method, r.URL.Path)
		}
		writeJSON(w, okResponse(map[string]interface{}{}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, _ = client.Webhooks.DeleteWebhook(context.Background(), 50)
}

// ---------------------------------------------------------------------------
// Error handling
// ---------------------------------------------------------------------------

func TestError_AuthError_On401(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusUnauthorized)
		fmt.Fprint(w, `{"message":"Unauthorized"}`)
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, err := client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	if err == nil {
		t.Fatal("expected error, got nil")
	}
	var authErr *cleanster.AuthError
	if !errors.As(err, &authErr) {
		t.Fatalf("expected *cleanster.AuthError, got %T", err)
	}
	if authErr.StatusCode != 401 {
		t.Errorf("expected StatusCode=401, got %d", authErr.StatusCode)
	}
	if authErr.ResponseBody == "" {
		t.Error("expected non-empty ResponseBody")
	}
}

func TestError_APIError_On404(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusNotFound)
		fmt.Fprint(w, `{"message":"Not found"}`)
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, err := client.Bookings.GetBookingDetails(context.Background(), 99999)
	if err == nil {
		t.Fatal("expected error, got nil")
	}
	var apiErr *cleanster.APIError
	if !errors.As(err, &apiErr) {
		t.Fatalf("expected *cleanster.APIError, got %T", err)
	}
	if apiErr.StatusCode != 404 {
		t.Errorf("expected StatusCode=404, got %d", apiErr.StatusCode)
	}
}

func TestError_APIError_On422(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusUnprocessableEntity)
		fmt.Fprint(w, `{"message":"Validation error"}`)
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, err := client.Bookings.CreateBooking(context.Background(), cleanster.CreateBookingRequest{})
	var apiErr *cleanster.APIError
	if !errors.As(err, &apiErr) {
		t.Fatalf("expected *cleanster.APIError, got %T: %v", err, err)
	}
	if apiErr.StatusCode != 422 {
		t.Errorf("expected StatusCode=422, got %d", apiErr.StatusCode)
	}
}

func TestError_APIError_On500(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusInternalServerError)
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, err := client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	var apiErr *cleanster.APIError
	if !errors.As(err, &apiErr) {
		t.Fatalf("expected *cleanster.APIError, got %T", err)
	}
	if apiErr.StatusCode != 500 {
		t.Errorf("expected StatusCode=500, got %d", apiErr.StatusCode)
	}
}

func TestError_CleansterError_ErrorString(t *testing.T) {
	err := &cleanster.CleansterError{Message: "network timeout"}
	if err.Error() != "cleanster: network timeout" {
		t.Errorf("unexpected error string: %s", err.Error())
	}
}

func TestError_AuthError_ErrorString(t *testing.T) {
	err := &cleanster.AuthError{StatusCode: 401, ResponseBody: "body"}
	if !strings.Contains(err.Error(), "401") {
		t.Errorf("expected 401 in error string, got: %s", err.Error())
	}
}

func TestError_APIError_ErrorString(t *testing.T) {
	err := &cleanster.APIError{StatusCode: 422, Message: "Validation failed"}
	if !strings.Contains(err.Error(), "422") || !strings.Contains(err.Error(), "Validation failed") {
		t.Errorf("unexpected error string: %s", err.Error())
	}
}

func TestError_APIError_ResponseBody(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprint(w, `{"error":"bad input"}`)
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	_, err := client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	var apiErr *cleanster.APIError
	errors.As(err, &apiErr)
	if apiErr == nil || apiErr.ResponseBody == "" {
		t.Error("expected non-empty ResponseBody in APIError")
	}
}

// ---------------------------------------------------------------------------
// Models
// ---------------------------------------------------------------------------

func TestModel_Booking_NullableFields(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, okResponse(map[string]interface{}{
			"id": 1, "status": "OPEN", "date": "2025-06-15", "time": "10:00",
			"hours": 3.0, "cost": 150.0, "propertyId": 1004, "cleanerId": nil,
			"planId": 2, "roomCount": 2, "bathroomCount": 1,
			"extraSupplies": false, "paymentMethodId": 10,
		}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Bookings.GetBookingDetails(context.Background(), 1)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.CleanerID != nil {
		t.Errorf("expected CleanerID to be nil, got %v", resp.Data.CleanerID)
	}
}

func TestModel_User_TokenField(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, okResponse(map[string]interface{}{
			"id": 42, "email": "x@y.com", "firstName": "X", "lastName": "Y", "token": "jwt-token",
		}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Users.FetchAccessToken(context.Background(), 42)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Data.Token == nil || *resp.Data.Token != "jwt-token" {
		t.Error("expected Token=jwt-token")
	}
}

func TestModel_Checklist_Items(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, okResponse(map[string]interface{}{
			"id": 1, "name": "Test",
			"items": []interface{}{
				map[string]interface{}{"id": 10, "description": "Mop", "isCompleted": true},
				map[string]interface{}{"id": 11, "description": "Vacuum", "isCompleted": false},
			},
		}))
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Checklists.GetChecklist(context.Background(), 1)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if len(resp.Data.Items) != 2 {
		t.Errorf("expected 2 items, got %d", len(resp.Data.Items))
	}
	if !resp.Data.Items[0].IsCompleted {
		t.Error("expected first item IsCompleted=true")
	}
}

func TestAPIResponse_MessageField(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, map[string]interface{}{"status": 200, "message": "OK", "data": []interface{}{}})
	})
	client, cleanup := newTestClient(t, handler)
	defer cleanup()
	resp, err := client.Bookings.GetBookings(context.Background(), cleanster.GetBookingsParams{})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if resp.Message != "OK" {
		t.Errorf("expected message=OK, got %s", resp.Message)
	}
}
