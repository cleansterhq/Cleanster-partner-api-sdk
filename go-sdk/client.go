package cleanster

import "fmt"

// Client is the main entry point for the Cleanster Partner API SDK.
//
// Create a client using NewSandboxClient (development) or NewProductionClient (live).
// After creating a user, call SetAccessToken to authenticate per-user requests.
//
//	client, err := cleanster.NewSandboxClient("your-access-key")
//	if err != nil { log.Fatal(err) }
//
//	resp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{...})
//	client.SetAccessToken(*resp.Data.Token)
type Client struct {
	// Bookings manages the full lifecycle of cleaning appointments.
	Bookings *BookingsService

	// Users manages end-user accounts and authentication tokens.
	Users *UsersService

	// Properties manages physical cleaning locations.
	Properties *PropertiesService

	// Checklists manages cleaning task lists.
	Checklists *ChecklistsService

	// Other provides utility endpoints: services, plans, cost estimates, and more.
	Other *OtherService

	// Blacklist manages the list of cleaners blocked from auto-assignment.
	Blacklist *BlacklistService

	// PaymentMethods manages Stripe and PayPal payment methods.
	PaymentMethods *PaymentMethodsService

	// Webhooks manages real-time event notification endpoints.
	Webhooks *WebhooksService

	http *httpClient
}

// NewSandboxClient creates a Client configured for the sandbox environment.
// Use this for development and testing — no real charges or cleaners.
func NewSandboxClient(accessKey string) (*Client, error) {
	return NewClient(NewSandboxConfig(accessKey))
}

// NewProductionClient creates a Client configured for the production environment.
// Use this for live traffic — real cleaners will be dispatched and charges applied.
func NewProductionClient(accessKey string) (*Client, error) {
	return NewClient(NewProductionConfig(accessKey))
}

// NewClient creates a Client from an explicit Config.
// Useful when you need custom timeouts or a non-standard base URL.
func NewClient(cfg Config) (*Client, error) {
	cfg = cfg.withDefaults()
	if err := cfg.validate(); err != nil {
		return nil, fmt.Errorf("cleanster: invalid config: %w", err)
	}
	h := newHTTPClient(cfg)
	return &Client{
		Bookings:       &BookingsService{http: h},
		Users:          &UsersService{http: h},
		Properties:     &PropertiesService{http: h},
		Checklists:     &ChecklistsService{http: h},
		Other:          &OtherService{http: h},
		Blacklist:      &BlacklistService{http: h},
		PaymentMethods: &PaymentMethodsService{http: h},
		Webhooks:       &WebhooksService{http: h},
		http:           h,
	}, nil
}

// SetAccessToken sets the user bearer token sent as the "token" header on every request.
// Call this after fetching a user's access token via Users.FetchAccessToken.
// Safe to call concurrently.
func (c *Client) SetAccessToken(token string) {
	c.http.setToken(token)
}

// GetAccessToken returns the currently active user bearer token, or "" if not set.
// Safe to call concurrently.
func (c *Client) GetAccessToken() string {
	return c.http.getToken()
}
