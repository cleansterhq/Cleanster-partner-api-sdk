# Cleanster Go SDK

<p align="center">
  <strong>Official Go client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Go-1.21%2B-00ADD8?logo=go" alt="Go 1.21+">
  <img src="https://img.shields.io/badge/go.dev-reference-blue?logo=go" alt="pkg.go.dev">
  <img src="https://img.shields.io/badge/tests-92%20passing-brightgreen" alt="92 passing tests">
  <img src="https://img.shields.io/badge/dependencies-none-brightgreen" alt="Zero dependencies">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT License">
  <img src="https://img.shields.io/badge/API-Cleanster%20Partner-brightgreen" alt="Cleanster Partner API">
</p>

---

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Authentication](#authentication)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Error Handling](#error-handling)
- [API Reference](#api-reference)
  - [Bookings](#bookings-clientbookings)
  - [Users](#users-clientusers)
  - [Properties](#properties-clientproperties)
  - [Checklists](#checklists-clientchecklists)
  - [Other / Utilities](#other--utilities-clientother)
  - [Blacklist](#blacklist-clientblacklist)
  - [Payment Methods](#payment-methods-clientpaymentmethods)
  - [Webhooks](#webhooks-clientwebhooks)
- [Response Structure](#response-structure)
- [Model Reference](#model-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster Go SDK provides a clean, idiomatic Go interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It targets Go 1.21+ and uses only Go's standard library — no external HTTP or JSON dependencies.

**Feature highlights:**

| Feature | Detail |
|---------|--------|
| **Idiomatic Go** | Struct-based requests, typed responses, `errors.As`-compatible errors |
| **Zero external dependencies** | Uses only `net/http`, `encoding/json`, `sync`, `context` from the standard library |
| **Generics** | `APIResponse[T]` typed wrapper — no type assertions or interface casting |
| **Context-aware** | Every API method accepts `context.Context` for cancellation and timeout |
| **Thread-safe auth** | `SetAccessToken` / `GetAccessToken` guarded by `sync.RWMutex` |
| **Three error types** | `CleansterError`, `AuthError` (401), `APIError` (4xx/5xx) — all work with `errors.As` |
| **8 service types** | Bookings, Users, Properties, Checklists, Other, Blacklist, PaymentMethods, Webhooks |
| **92 tests** | All passing; uses `net/http/httptest` — no network access or API keys required |

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Go | ≥ 1.21 |

> Go 1.21 is required for the `APIResponse[T any]` generic type. The SDK has **zero external runtime dependencies**.

---

## Installation

```bash
go get github.com/cleanster/cleanster-go-sdk
```

Then import in your Go file:

```go
import cleanster "github.com/cleanster/cleanster-go-sdk"
```

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** sent as HTTP headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

The SDK handles both headers automatically. You supply the partner key once at client creation, then set the user token after fetching it.

### Step-by-Step Authentication

**Step 1 — Create the client with your partner access key:**

```go
import (
    "log"
    cleanster "github.com/cleanster/cleanster-go-sdk"
)

client, err := cleanster.NewSandboxClient(os.Getenv("CLEANSTER_API_KEY"))
if err != nil {
    log.Fatal(err)
}
```

**Step 2 — Create or look up a user. For new users:**

```go
ctx := context.Background()

userResp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{
    Email:     "jane@example.com",
    FirstName: "Jane",
    LastName:  "Smith",
})
if err != nil {
    log.Fatal(err)
}
user := userResp.Data   // cleanster.User
fmt.Printf("Created user #%d\n", user.ID)
```

**Step 3 — Fetch the user's long-lived bearer token:**

```go
tokenResp, err := client.Users.FetchAccessToken(ctx, user.ID)
if err != nil {
    log.Fatal(err)
}
token := *tokenResp.Data.Token   // string
```

**Step 4 — Set the token on the client** for all subsequent calls:

```go
client.SetAccessToken(token)
// Every subsequent API call automatically includes this token
```

> **Tip:** The bearer token is long-lived. Store it in your database and reuse it across sessions by calling `client.SetAccessToken(storedToken)` — no need to re-fetch it each time.

---

## Quick Start

```go
package main

import (
    "context"
    "fmt"
    "log"
    "os"

    cleanster "github.com/cleanster/cleanster-go-sdk"
)

func main() {
    ctx := context.Background()

    // 1. Create a sandbox client
    client, err := cleanster.NewSandboxClient(os.Getenv("CLEANSTER_API_KEY"))
    if err != nil {
        log.Fatal(err)
    }

    // 2. Create a user
    userResp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{
        Email:     "jane@example.com",
        FirstName: "Jane",
        LastName:  "Smith",
    })
    if err != nil {
        log.Fatal(err)
    }

    // 3. Fetch and set the user token
    tokenResp, err := client.Users.FetchAccessToken(ctx, userResp.Data.ID)
    if err != nil {
        log.Fatal(err)
    }
    client.SetAccessToken(*tokenResp.Data.Token)

    // 4. Add a property
    propResp, err := client.Properties.AddProperty(ctx, cleanster.CreatePropertyRequest{
        Name:          "Beach House",
        Address:       "123 Ocean Drive",
        City:          "Miami",
        Country:       "USA",
        RoomCount:     3,
        BathroomCount: 2,
        ServiceID:     1,
    })
    if err != nil {
        log.Fatal(err)
    }
    prop := propResp.Data   // cleanster.Property

    // 5. Check recommended hours
    client.Other.GetRecommendedHours(ctx, prop.ID, prop.BathroomCount, prop.RoomCount)

    // 6. Estimate cost
    client.Other.CalculateCost(ctx, cleanster.CostEstimateRequest{
        PropertyID: prop.ID,
        PlanID:     2,
        Hours:      3,
        CouponCode: "20POFF",   // optional sandbox coupon
    })

    // 7. Create a booking
    bookingResp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{
        Date:            "2025-06-15",
        Time:            "10:00",
        PropertyID:      prop.ID,
        RoomCount:       3,
        BathroomCount:   2,
        PlanID:          2,
        Hours:           3,
        ExtraSupplies:   false,
        PaymentMethodID: 10,
    })
    if err != nil {
        log.Fatal(err)
    }
    fmt.Printf("Created booking #%d — status: %s\n", bookingResp.Data.ID, bookingResp.Data.Status)

    // 8. List all bookings
    listResp, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{})
    if err != nil {
        log.Fatal(err)
    }
    fmt.Printf("Found %d bookings\n", len(listResp.Data))
}
```

---

## Configuration

### Factory Functions (Recommended)

```go
// Sandbox — development and testing (no real charges or cleaners)
client, err := cleanster.NewSandboxClient("your-access-key")

// Production — live traffic (real cleaners, real charges)
client, err := cleanster.NewProductionClient("your-access-key")
```

### Custom Config

For custom timeouts, proxies, or non-standard base URLs:

```go
cfg := cleanster.Config{
    AccessKey: "your-access-key",
    BaseURL:   cleanster.SandboxBaseURL,   // or ProductionBaseURL or custom
    Timeout:   45 * time.Second,           // default is 30s
}
client, err := cleanster.NewClient(cfg)
```

### Helper Config Constructors

```go
// Returns a Config struct (not a Client — lets you customize before creating)
sandboxCfg    := cleanster.NewSandboxConfig("your-access-key")
productionCfg := cleanster.NewProductionConfig("your-access-key")

// Modify any fields before creating the client
sandboxCfg.Timeout = 60 * time.Second
client, err := cleanster.NewClient(sandboxCfg)
```

### Environment Base URLs

| Environment | Base URL |
|-------------|----------|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## Error Handling

All SDK methods return `(value, error)`. The error is always one of the three SDK error types, all compatible with `errors.As`.

### Complete Error Handling Example

```go
import "errors"

bookingResp, err := client.Bookings.GetBookingDetails(ctx, 99999)
if err != nil {
    var authErr *cleanster.AuthError
    var apiErr  *cleanster.APIError
    var sdkErr  *cleanster.CleansterError

    switch {
    case errors.As(err, &authErr):
        // HTTP 401 — bad access key or expired user token
        fmt.Printf("Auth error (%d): %v\n", authErr.StatusCode, authErr)
        fmt.Printf("Response body: %s\n", authErr.ResponseBody)
        // Prompt the user to re-authenticate

    case errors.As(err, &apiErr):
        // HTTP 4xx/5xx — API-level error
        fmt.Printf("API error (%d): %v\n", apiErr.StatusCode, apiErr)
        fmt.Printf("Response body: %s\n", apiErr.ResponseBody)

        switch apiErr.StatusCode {
        case 404:
            fmt.Println("Resource not found.")
        case 422:
            fmt.Println("Validation error — check request fields.")
        default:
            fmt.Printf("Server error — consider retrying.\n")
        }

    case errors.As(err, &sdkErr):
        // Network failure, timeout, or JSON parse error
        fmt.Printf("SDK error: %v\n", sdkErr)

    default:
        fmt.Printf("Unknown error: %v\n", err)
    }
    return
}

// Success — use bookingResp.Data
fmt.Printf("Booking #%d is %s\n", bookingResp.Data.ID, bookingResp.Data.Status)
```

### Exception Hierarchy

```
error (interface)
└── *cleanster.CleansterError        ← base: network failure, timeout, JSON parse error
    ├── *cleanster.AuthError         ← HTTP 401 (invalid or missing credentials)
    └── *cleanster.APIError          ← HTTP 4xx/5xx (API-level errors, other than 401)
```

| Type | When Returned | Key Fields |
|------|---------------|------------|
| `*cleanster.CleansterError` | Network error, timeout, JSON parse failure | `Message string` |
| `*cleanster.AuthError` | HTTP 401 | `StatusCode int`, `ResponseBody string` |
| `*cleanster.APIError` | HTTP 4xx/5xx (not 401) | `StatusCode int`, `Message string`, `ResponseBody string` |

---

## API Reference

Every method accepts `context.Context` as its first parameter and returns an `APIResponse[T]` value plus an `error`. All request structs use standard Go field names (PascalCase with JSON tags).

---

### Bookings (`client.Bookings`)

#### `GetBookings(ctx, GetBookingsParams) → APIResponse[[]Booking]`

Retrieve a paginated list of bookings. All filter parameters are optional.

```go
// All bookings (no filter)
resp, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{})

// Filter by status
page := 2
resp, err = client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{
    Status: "OPEN",
    PageNo: &page,   // *int — nil for the first page
})
```

**`GetBookingsParams` fields:**

| Field | Type | Description |
|-------|------|-------------|
| `PageNo` | `*int` | Page number (1-based); `nil` = first page |
| `Status` | `string` | `"OPEN"` \| `"CLEANER_ASSIGNED"` \| `"COMPLETED"` \| `"CANCELLED"` \| `"REMOVED"` |

---

#### `CreateBooking(ctx, CreateBookingRequest) → APIResponse[Booking]`

Schedule a new cleaning appointment.

```go
resp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{
    Date:            "2025-06-15",   // Required — YYYY-MM-DD
    Time:            "10:00",        // Required — HH:mm (24-hour)
    PropertyID:      1004,           // Required
    RoomCount:       2,              // Required
    BathroomCount:   1,              // Required
    PlanID:          5,              // Required — from GetPlans
    Hours:           3,              // Required — from GetRecommendedHours
    ExtraSupplies:   false,          // Required — include cleaning supplies?
    PaymentMethodID: 10,             // Required
    CouponCode:      "20POFF",       // Optional
    Extras:          []int{101, 102},// Optional — add-on service IDs
})

booking := resp.Data   // cleanster.Booking
fmt.Println(booking.ID, booking.Status, booking.Cost)
```

---

#### `GetBookingDetails(ctx, bookingID int) → APIResponse[Booking]`

```go
resp, err := client.Bookings.GetBookingDetails(ctx, 16926)
b := resp.Data
fmt.Printf("Booking #%d on %s at %s — status: %s — cost: %.2f\n",
    b.ID, b.Date, b.Time, b.Status, b.Cost)
if b.CleanerID != nil {
    fmt.Printf("Assigned cleaner: #%d\n", *b.CleanerID)
}
```

---

#### `CancelBooking(ctx, bookingID int, CancelBookingRequest) → APIResponse[map[string]interface{}]`

```go
// With a reason
_, err = client.Bookings.CancelBooking(ctx, 16459, cleanster.CancelBookingRequest{
    Reason: "Schedule conflict",
})

// Without a reason (Reason is omitted from JSON automatically)
_, err = client.Bookings.CancelBooking(ctx, 16459, cleanster.CancelBookingRequest{})
```

---

#### `RescheduleBooking(ctx, bookingID int, RescheduleBookingRequest) → APIResponse[map[string]interface{}]`

```go
_, err = client.Bookings.RescheduleBooking(ctx, 16459, cleanster.RescheduleBookingRequest{
    Date: "2025-07-01",
    Time: "14:00",
})
```

---

#### `AssignCleaner(ctx, bookingID int, AssignCleanerRequest)` / `RemoveAssignedCleaner(ctx, bookingID int)`

```go
// Assign a specific cleaner
_, err = client.Bookings.AssignCleaner(ctx, 16459, cleanster.AssignCleanerRequest{CleanerID: 5})

// Remove the assigned cleaner
_, err = client.Bookings.RemoveAssignedCleaner(ctx, 16459)
```

---

#### `AdjustHours(ctx, bookingID int, AdjustHoursRequest)`

```go
_, err = client.Bookings.AdjustHours(ctx, 16459, cleanster.AdjustHoursRequest{Hours: 4.0})
```

---

#### `PayExpenses(ctx, bookingID int, PayExpensesRequest)`

Pay outstanding expenses within 72 hours of booking completion.

```go
_, err = client.Bookings.PayExpenses(ctx, 16926, cleanster.PayExpensesRequest{PaymentMethodID: 10})
```

---

#### `GetBookingInspection` / `GetBookingInspectionDetails`

```go
resp, err := client.Bookings.GetBookingInspection(ctx, 16926)
resp, err  = client.Bookings.GetBookingInspectionDetails(ctx, 16926)
```

---

#### `AssignChecklistToBooking(ctx, bookingID, checklistID int)`

Override the property's default checklist for this specific booking only.

```go
_, err = client.Bookings.AssignChecklistToBooking(ctx, 16926, 105)
```

---

#### `SubmitFeedback(ctx, bookingID int, FeedbackRequest)`

Submit a star rating (1–5) and optional comment.

```go
// With a comment
_, err = client.Bookings.SubmitFeedback(ctx, 16926, cleanster.FeedbackRequest{
    Rating:  5,
    Comment: "Excellent — very thorough!",
})

// Without a comment (Comment is omitted from JSON automatically)
_, err = client.Bookings.SubmitFeedback(ctx, 16926, cleanster.FeedbackRequest{Rating: 4})
```

---

#### `AddTip(ctx, bookingID int, TipRequest)`

Add a tip within 72 hours of booking completion.

```go
_, err = client.Bookings.AddTip(ctx, 16926, cleanster.TipRequest{
    Amount:          20.0,
    PaymentMethodID: 10,
})
```

---

#### Chat: `GetChat`, `SendMessage`, `DeleteMessage`

```go
// Get all messages in a booking's chat thread
chat, err := client.Bookings.GetChat(ctx, 17142)

// Send a message
_, err = client.Bookings.SendMessage(ctx, 17142, cleanster.SendMessageRequest{
    Message: "Please focus on the kitchen today.",
})

// Delete a specific message
_, err = client.Bookings.DeleteMessage(ctx, 17142, "msg-abc-123")
```

---

**Bookings API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `GetBookings(ctx, params)` | GET | `/v1/bookings` |
| `CreateBooking(ctx, req)` | POST | `/v1/bookings/create` |
| `GetBookingDetails(ctx, id)` | GET | `/v1/bookings/{id}` |
| `CancelBooking(ctx, id, req)` | POST | `/v1/bookings/{id}/cancel` |
| `RescheduleBooking(ctx, id, req)` | POST | `/v1/bookings/{id}/reschedule` |
| `AssignCleaner(ctx, id, req)` | POST | `/v1/bookings/{id}/cleaner` |
| `RemoveAssignedCleaner(ctx, id)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `AdjustHours(ctx, id, req)` | POST | `/v1/bookings/{id}/hours` |
| `PayExpenses(ctx, id, req)` | POST | `/v1/bookings/{id}/expenses` |
| `GetBookingInspection(ctx, id)` | GET | `/v1/bookings/{id}/inspection` |
| `GetBookingInspectionDetails(ctx, id)` | GET | `/v1/bookings/{id}/inspection/details` |
| `AssignChecklistToBooking(ctx, id, cid)` | POST | `/v1/bookings/{id}/checklist/{cid}` |
| `SubmitFeedback(ctx, id, req)` | POST | `/v1/bookings/{id}/feedback` |
| `AddTip(ctx, id, req)` | POST | `/v1/bookings/{id}/tip` |
| `GetChat(ctx, id)` | GET | `/v1/bookings/{id}/chat` |
| `SendMessage(ctx, id, req)` | POST | `/v1/bookings/{id}/chat` |
| `DeleteMessage(ctx, id, messageID)` | DELETE | `/v1/bookings/{id}/chat/{messageID}` |

---

### Users (`client.Users`)

#### `CreateUser(ctx, CreateUserRequest) → APIResponse[User]`

```go
resp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{
    Email:     "jane@example.com",
    FirstName: "Jane",
    LastName:  "Smith",
    Phone:     "+15551234567",  // Optional — omitted from JSON if empty
})

user := resp.Data   // cleanster.User
fmt.Println(user.ID, user.Email)
```

---

#### `FetchAccessToken(ctx, userID int) → APIResponse[User]`

Fetch the long-lived bearer token. Store it and reuse it across sessions.

```go
resp, err := client.Users.FetchAccessToken(ctx, 42)
if err != nil {
    log.Fatal(err)
}
token := *resp.Data.Token   // string

// Authenticate all subsequent requests:
client.SetAccessToken(token)
```

---

#### `VerifyJWT(ctx, VerifyJWTRequest) → APIResponse[map[string]interface{}]`

```go
resp, err := client.Users.VerifyJWT(ctx, cleanster.VerifyJWTRequest{Token: "eyJhbGci..."})
```

---

**Users API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `CreateUser(ctx, req)` | POST | `/v1/user/account` |
| `FetchAccessToken(ctx, userID)` | GET | `/v1/user/access-token/{userID}` |
| `VerifyJWT(ctx, req)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`client.Properties`)

#### `ListProperties(ctx, serviceID int) → APIResponse[[]Property]`

Pass `serviceID = 0` to return all service types.

```go
// All properties
resp, err := client.Properties.ListProperties(ctx, 0)

// Residential only (serviceID = 1)
resp, err = client.Properties.ListProperties(ctx, 1)
```

---

#### `AddProperty(ctx, CreatePropertyRequest) → APIResponse[Property]`

```go
resp, err := client.Properties.AddProperty(ctx, cleanster.CreatePropertyRequest{
    Name:          "Downtown Condo",
    Address:       "456 Main St",
    City:          "Toronto",
    Country:       "Canada",
    RoomCount:     2,
    BathroomCount: 1,
    ServiceID:     1,
})
prop := resp.Data   // cleanster.Property
fmt.Println(prop.ID, prop.Name, prop.City)
```

---

#### CRUD operations

```go
// Get
prop, err := client.Properties.GetProperty(ctx, 1040)

// Update (full replace)
_, err = client.Properties.UpdateProperty(ctx, 1040, cleanster.CreatePropertyRequest{
    Name: "Renovated Condo", Address: "456 Main St",
    City: "Toronto", Country: "Canada",
    RoomCount: 3, BathroomCount: 1, ServiceID: 1,
})

// Enable or disable
_, err = client.Properties.EnableOrDisableProperty(ctx, 1040,
    cleanster.EnableDisablePropertyRequest{Enabled: false})

// Delete permanently
_, err = client.Properties.DeleteProperty(ctx, 1040)
```

---

#### Cleaner assignment

```go
// List assigned cleaners
cleaners, err := client.Properties.GetPropertyCleaners(ctx, 1040)

// Assign a cleaner
_, err = client.Properties.AssignCleanerToProperty(ctx, 1040,
    cleanster.AssignCleanerToPropertyRequest{CleanerID: 5})

// Unassign
_, err = client.Properties.UnassignCleanerFromProperty(ctx, 1040, 5)
```

---

#### iCal calendar sync

Sync property availability with Airbnb, VRBO, or any iCal-compatible platform.

```go
feedURL := "https://airbnb.com/calendar/ical/xxx.ics"

// Add
_, err = client.Properties.AddICalLink(ctx, 1040, cleanster.ICalRequest{ICalLink: feedURL})

// Get current link
link, err := client.Properties.GetICalLink(ctx, 1040)

// Remove
_, err = client.Properties.RemoveICalLink(ctx, 1040, cleanster.ICalRequest{ICalLink: feedURL})
```

---

#### `AssignChecklistToProperty(ctx, propertyID, checklistID int, updateUpcomingBookings bool)`

```go
// Apply to property and update all future bookings at this property
_, err = client.Properties.AssignChecklistToProperty(ctx, 1040, 105, true)

// Apply to property without affecting upcoming bookings
_, err = client.Properties.AssignChecklistToProperty(ctx, 1040, 105, false)
```

---

**Properties API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `ListProperties(ctx, serviceID)` | GET | `/v1/properties` |
| `AddProperty(ctx, req)` | POST | `/v1/properties` |
| `GetProperty(ctx, id)` | GET | `/v1/properties/{id}` |
| `UpdateProperty(ctx, id, req)` | PUT | `/v1/properties/{id}` |
| `UpdateAdditionalInformation(ctx, id, data)` | PUT | `/v1/properties/{id}/additional-information` |
| `EnableOrDisableProperty(ctx, id, req)` | POST | `/v1/properties/{id}/enable-disable` |
| `DeleteProperty(ctx, id)` | DELETE | `/v1/properties/{id}` |
| `GetPropertyCleaners(ctx, id)` | GET | `/v1/properties/{id}/cleaners` |
| `AssignCleanerToProperty(ctx, id, req)` | POST | `/v1/properties/{id}/cleaners` |
| `UnassignCleanerFromProperty(ctx, id, cleanerID)` | DELETE | `/v1/properties/{id}/cleaners/{cid}` |
| `AddICalLink(ctx, id, req)` | PUT | `/v1/properties/{id}/ical` |
| `GetICalLink(ctx, id)` | GET | `/v1/properties/{id}/ical` |
| `RemoveICalLink(ctx, id, req)` | DELETE | `/v1/properties/{id}/ical` |
| `AssignChecklistToProperty(ctx, id, cid, updateUpcoming)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`client.Checklists`)

Checklists define the tasks a cleaner must complete during a booking. They can be set as property defaults or overridden per booking.

#### `ListChecklists(ctx) → APIResponse[[]Checklist]`

```go
resp, err := client.Checklists.ListChecklists(ctx)
for _, cl := range resp.Data {
    fmt.Printf("Checklist #%d: %s (%d items)\n", cl.ID, cl.Name, len(cl.Items))
}
```

---

#### `GetChecklist(ctx, checklistID int) → APIResponse[Checklist]`

```go
resp, err := client.Checklists.GetChecklist(ctx, 105)
cl := resp.Data   // cleanster.Checklist
for _, item := range cl.Items {
    mark := " "
    if item.IsCompleted {
        mark = "✓"
    }
    fmt.Printf("[%s] %s\n", mark, item.Description)
}
```

---

#### `CreateChecklist(ctx, CreateChecklistRequest) → APIResponse[Checklist]`

```go
resp, err := client.Checklists.CreateChecklist(ctx, cleanster.CreateChecklistRequest{
    Name: "Standard Residential Clean",
    Items: []string{
        "Vacuum all floors",
        "Mop kitchen and bathroom floors",
        "Wipe all countertops",
        "Scrub toilets, sinks, and tubs",
        "Empty all trash bins",
    },
})
fmt.Printf("Created checklist #%d\n", resp.Data.ID)
```

---

#### `UpdateChecklist` / `DeleteChecklist`

```go
// Update
_, err = client.Checklists.UpdateChecklist(ctx, 105, cleanster.CreateChecklistRequest{
    Name:  "Deep Clean",
    Items: []string{"All standard tasks", "Inside oven", "Inside fridge"},
})

// Delete
_, err = client.Checklists.DeleteChecklist(ctx, 105)
```

---

**Checklists API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `ListChecklists(ctx)` | GET | `/v1/checklist` |
| `GetChecklist(ctx, id)` | GET | `/v1/checklist/{id}` |
| `CreateChecklist(ctx, req)` | POST | `/v1/checklist` |
| `UpdateChecklist(ctx, id, req)` | PUT | `/v1/checklist/{id}` |
| `DeleteChecklist(ctx, id)` | DELETE | `/v1/checklist/{id}` |

---

### Other / Utilities (`client.Other`)

Reference data and utility endpoints used when building booking flows.

#### `GetServices(ctx) → APIResponse[[]map[string]interface{}]`

```go
resp, err := client.Other.GetServices(ctx)
```

---

#### `GetPlans(ctx, propertyID int) → APIResponse[[]map[string]interface{}]`

```go
resp, err := client.Other.GetPlans(ctx, 1004)
```

---

#### `GetRecommendedHours(ctx, propertyID, bathroomCount, roomCount int) → APIResponse[map[string]interface{}]`

Returns the system-recommended cleaning duration based on property size. Use the result to pre-fill `Hours` in `CreateBookingRequest`.

```go
resp, err := client.Other.GetRecommendedHours(ctx, 1004, 2, 3)
```

---

#### `CalculateCost(ctx, CostEstimateRequest) → APIResponse[map[string]interface{}]`

Preview the estimated booking price before committing.

```go
resp, err := client.Other.CalculateCost(ctx, cleanster.CostEstimateRequest{
    PropertyID: 1004,
    PlanID:     2,
    Hours:      3,
    CouponCode: "20POFF",   // optional
})
```

---

#### `GetCleaningExtras(ctx, serviceID int)` / `GetAvailableCleaners(ctx, req)` / `GetCoupons(ctx)`

```go
// Add-on services (inside fridge, laundry, etc.)
extras, err := client.Other.GetCleaningExtras(ctx, 1)

// Find available cleaners for a time slot
cleaners, err := client.Other.GetAvailableCleaners(ctx, cleanster.AvailableCleanersRequest{
    PropertyID: 1004,
    Date:       "2025-06-15",
    Time:       "10:00",
})

// All valid coupon codes
coupons, err := client.Other.GetCoupons(ctx)
```

---

**Other API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `GetServices(ctx)` | GET | `/v1/services` |
| `GetPlans(ctx, propertyID)` | GET | `/v1/plans?propertyId={id}` |
| `GetRecommendedHours(ctx, pid, bath, rooms)` | GET | `/v1/recommended-hours` |
| `CalculateCost(ctx, req)` | POST | `/v1/cost-estimate` |
| `GetCleaningExtras(ctx, serviceID)` | GET | `/v1/cleaning-extras/{serviceID}` |
| `GetAvailableCleaners(ctx, req)` | POST | `/v1/available-cleaners` |
| `GetCoupons(ctx)` | GET | `/v1/coupons` |

---

### Blacklist (`client.Blacklist`)

Prevent specific cleaners from being auto-assigned to bookings.

```go
// List all blacklisted cleaners
list, err := client.Blacklist.ListBlacklistedCleaners(ctx)

// Add a cleaner (reason is optional — omitted from JSON if empty)
_, err = client.Blacklist.AddToBlacklist(ctx, cleanster.BlacklistRequest{
    CleanerID: 7,
    Reason:    "Damaged furniture",   // optional
})

// Remove a cleaner
_, err = client.Blacklist.RemoveFromBlacklist(ctx, cleanster.BlacklistRequest{CleanerID: 7})
```

**Blacklist API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `ListBlacklistedCleaners(ctx)` | GET | `/v1/blacklist/cleaner` |
| `AddToBlacklist(ctx, req)` | POST | `/v1/blacklist/cleaner` |
| `RemoveFromBlacklist(ctx, req)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`client.PaymentMethods`)

#### Stripe

```go
// 1. Get SetupIntent details (use clientSecret with Stripe.js client-side)
intent, err := client.PaymentMethods.GetSetupIntentDetails(ctx)

// 2. After client-side tokenization, save the payment method
_, err = client.PaymentMethods.AddPaymentMethod(ctx, cleanster.AddPaymentMethodRequest{
    PaymentMethodID: "pm_xxxxxxxxxxxxxxxx",
})
```

#### PayPal

```go
// Get client token for PayPal button rendering
token, err := client.PaymentMethods.GetPaypalClientToken(ctx)
```

#### Manage Saved Methods

```go
// List all payment methods
methods, err := client.PaymentMethods.GetPaymentMethods(ctx)
for _, m := range methods.Data {
    fmt.Printf("#%d %s", m.ID, m.Type)
    if m.LastFour != nil {
        fmt.Printf(" *%s (%s)", *m.LastFour, *m.Brand)
    }
    if m.IsDefault {
        fmt.Print(" [DEFAULT]")
    }
    fmt.Println()
}

// Set as default
_, err = client.PaymentMethods.SetDefaultPaymentMethod(ctx, 193)

// Delete
_, err = client.PaymentMethods.DeletePaymentMethod(ctx, 193)
```

**Payment Methods API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `GetSetupIntentDetails(ctx)` | GET | `/v1/payment-methods/setup-intent` |
| `GetPaypalClientToken(ctx)` | GET | `/v1/payment-methods/paypal-client-token` |
| `AddPaymentMethod(ctx, req)` | POST | `/v1/payment-methods` |
| `GetPaymentMethods(ctx)` | GET | `/v1/payment-methods` |
| `DeletePaymentMethod(ctx, id)` | DELETE | `/v1/payment-methods/{id}` |
| `SetDefaultPaymentMethod(ctx, id)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`client.Webhooks`)

Receive real-time notifications when booking events occur — no polling required.

```go
// List all webhook endpoints
hooks, err := client.Webhooks.ListWebhooks(ctx)

// Register a new webhook
_, err = client.Webhooks.CreateWebhook(ctx, cleanster.WebhookRequest{
    URL:   "https://your-app.com/webhooks/cleanster",
    Event: "booking.status_changed",
})

// Update a webhook
_, err = client.Webhooks.UpdateWebhook(ctx, 50, cleanster.WebhookRequest{
    URL:   "https://your-app.com/v2/webhooks",
    Event: "booking.status_changed",
})

// Delete a webhook
_, err = client.Webhooks.DeleteWebhook(ctx, 50)
```

**Webhooks API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `ListWebhooks(ctx)` | GET | `/v1/webhooks` |
| `CreateWebhook(ctx, req)` | POST | `/v1/webhooks` |
| `UpdateWebhook(ctx, id, req)` | PUT | `/v1/webhooks/{id}` |
| `DeleteWebhook(ctx, id)` | DELETE | `/v1/webhooks/{id}` |

---

## Response Structure

Every SDK method returns an `APIResponse[T]` value where `T` is the specific data type.

```go
// Generic definition
type APIResponse[T any] struct {
    Status  int    // HTTP-style status code (e.g., 200)
    Message string // Human-readable status (e.g., "OK")
    Data    T      // Typed payload
}
```

**Usage examples:**

```go
// Access all fields
resp, err := client.Bookings.GetBookingDetails(ctx, 16926)
if err != nil { /* handle */ }
fmt.Println(resp.Status)   // 200
fmt.Println(resp.Message)  // "OK"
booking := resp.Data       // cleanster.Booking (fully typed)

// Chain directly to Data
booking, err := func() (cleanster.Booking, error) {
    resp, err := client.Bookings.GetBookingDetails(ctx, 16926)
    return resp.Data, err
}()

// Slice result
resp, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{})
for _, b := range resp.Data {   // []cleanster.Booking
    fmt.Println(b.ID, b.Status)
}
```

---

## Model Reference

### `Booking`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | Booking ID |
| `Status` | `string` | `status` | `"OPEN"` / `"CLEANER_ASSIGNED"` / `"COMPLETED"` / `"CANCELLED"` / `"REMOVED"` |
| `Date` | `string` | `date` | Booking date (YYYY-MM-DD) |
| `Time` | `string` | `time` | Start time (HH:mm) |
| `Hours` | `float64` | `hours` | Duration in hours |
| `Cost` | `float64` | `cost` | Total cost |
| `PropertyID` | `int` | `propertyId` | Associated property |
| `CleanerID` | `*int` | `cleanerId` | Assigned cleaner (`nil` if unassigned) |
| `PlanID` | `int` | `planId` | Booking plan |
| `RoomCount` | `int` | `roomCount` | Number of rooms |
| `BathroomCount` | `int` | `bathroomCount` | Number of bathrooms |
| `ExtraSupplies` | `bool` | `extraSupplies` | Cleaning supplies included |
| `PaymentMethodID` | `int` | `paymentMethodId` | Payment method |

### `User`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | User ID |
| `Email` | `string` | `email` | Email address |
| `FirstName` | `string` | `firstName` | First name |
| `LastName` | `string` | `lastName` | Last name |
| `Phone` | `*string` | `phone` | Phone number (optional) |
| `Token` | `*string` | `token` | Bearer token — present only after `FetchAccessToken` |

### `Property`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | Property ID |
| `Name` | `string` | `name` | Property label |
| `Address` | `string` | `address` | Street address |
| `City` | `string` | `city` | City |
| `Country` | `string` | `country` | Country |
| `RoomCount` | `int` | `roomCount` | Number of rooms |
| `BathroomCount` | `int` | `bathroomCount` | Number of bathrooms |
| `ServiceID` | `int` | `serviceId` | Service type ID |
| `IsEnabled` | `*bool` | `isEnabled` | Active state (`nil` if not returned) |

### `Checklist`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | Checklist ID |
| `Name` | `string` | `name` | Checklist name |
| `Items` | `[]ChecklistItem` | `items` | Task items |

### `ChecklistItem`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | Item ID |
| `Description` | `string` | `description` | Task description |
| `IsCompleted` | `bool` | `isCompleted` | Marked complete by cleaner |
| `ImageURL` | `*string` | `imageUrl` | Proof photo URL (if uploaded) |

### `PaymentMethod`

| Field | Go Type | JSON Key | Description |
|-------|---------|----------|-------------|
| `ID` | `int` | `id` | Payment method ID |
| `Type` | `string` | `type` | `"card"` / `"paypal"` / etc. |
| `LastFour` | `*string` | `lastFour` | Last 4 digits (cards only) |
| `Brand` | `*string` | `brand` | Card brand (`"visa"`, `"mastercard"`, etc.) |
| `IsDefault` | `bool` | `isDefault` | Whether this is the default method |

### Request Types

| Struct | Used By | Key Fields |
|--------|---------|------------|
| `CreateBookingRequest` | `CreateBooking` | `Date`, `Time`, `PropertyID`, `PlanID`, `Hours`, `PaymentMethodID` |
| `CancelBookingRequest` | `CancelBooking` | `Reason` (optional — `omitempty`) |
| `RescheduleBookingRequest` | `RescheduleBooking` | `Date`, `Time` |
| `AssignCleanerRequest` | `AssignCleaner` | `CleanerID` |
| `AdjustHoursRequest` | `AdjustHours` | `Hours` |
| `PayExpensesRequest` | `PayExpenses` | `PaymentMethodID` |
| `FeedbackRequest` | `SubmitFeedback` | `Rating` (1–5), `Comment` (optional — `omitempty`) |
| `TipRequest` | `AddTip` | `Amount`, `PaymentMethodID` |
| `SendMessageRequest` | `SendMessage` | `Message` |
| `CreateUserRequest` | `CreateUser` | `Email`, `FirstName`, `LastName`, `Phone` (optional) |
| `VerifyJWTRequest` | `VerifyJWT` | `Token` |
| `CreatePropertyRequest` | `AddProperty`, `UpdateProperty` | `Name`, `Address`, `City`, `Country`, `RoomCount`, `BathroomCount`, `ServiceID` |
| `EnableDisablePropertyRequest` | `EnableOrDisableProperty` | `Enabled` |
| `AssignCleanerToPropertyRequest` | `AssignCleanerToProperty` | `CleanerID` |
| `ICalRequest` | `AddICalLink`, `RemoveICalLink` | `ICalLink` |
| `CreateChecklistRequest` | `CreateChecklist`, `UpdateChecklist` | `Name`, `Items []string` |
| `CostEstimateRequest` | `CalculateCost` | `PropertyID`, `PlanID`, `Hours`, `CouponCode` (optional) |
| `AvailableCleanersRequest` | `GetAvailableCleaners` | `PropertyID`, `Date`, `Time` |
| `BlacklistRequest` | `AddToBlacklist`, `RemoveFromBlacklist` | `CleanerID`, `Reason` (optional) |
| `AddPaymentMethodRequest` | `AddPaymentMethod` | `PaymentMethodID` |
| `WebhookRequest` | `CreateWebhook`, `UpdateWebhook` | `URL`, `Event` |

---

## Sandbox vs Production

| Feature | Sandbox | Production |
|---------|---------|------------|
| Real charges | No | Yes |
| Real cleaners dispatched | No | Yes |
| Coupon codes | Test codes work | Real codes only |
| Data persistence | Yes (sandbox DB) | Yes (production DB) |
| Factory function | `NewSandboxClient` | `NewProductionClient` |
| Constant | `SandboxBaseURL` | `ProductionBaseURL` |

```go
// Development / CI
client, err := cleanster.NewSandboxClient(os.Getenv("CLEANSTER_API_KEY"))

// Production
client, err := cleanster.NewProductionClient(os.Getenv("CLEANSTER_API_KEY"))
```

> **Always develop and test against the sandbox.** Switch to production only when you're ready to go live.

---

## Test Coupon Codes (Sandbox Only)

These codes work only in the sandbox environment. Use them to test discount flows without real charges.

| Code | Discount | Suggested Test |
|------|----------|----------------|
| `100POFF` | 100% off (free booking) | Verify zero-cost booking flow |
| `50POFF` | 50% off | Verify percentage discount calculation |
| `20POFF` | 20% off | Verify small percentage discount |
| `200OFF` | $200 flat off | Verify flat discount |
| `100OFF` | $100 flat off | Verify partial flat discount |

Pass via `CouponCode` in `CreateBookingRequest` or `CostEstimateRequest`.

---

## Running Tests

The test suite contains **92 tests** — all passing. Tests use `net/http/httptest` to spin up a local test server — no network access, real API keys, or external dependencies required.

```bash
# Clone the repo
git clone https://github.com/cleanster/cleanster-go-sdk.git
cd cleanster-go-sdk

# Run all tests
go test ./...

# Run with verbose output (shows each test name)
go test -v ./...

# Run a specific test by name
go test -v -run TestBookings_CreateBooking ./...

# Run tests matching a prefix
go test -v -run TestBookings_ ./...

# Run with race detector
go test -race ./...

# Run with coverage
go test -cover ./...

# Generate HTML coverage report
go test -coverprofile=coverage.out ./...
go tool cover -html=coverage.out -o coverage.html
```

### Test Coverage Areas

| Area | Tests | What's Verified |
|------|-------|-----------------|
| Config | 9 | Factory functions, blank key rejection, timeout defaults |
| Client | 6 | All 8 services exposed, token get/set/clear, auth headers sent |
| `BookingsService` | 22 | All 17 methods + edge cases (no reason, no comment, no params, body field validation) |
| `UsersService` | 5 | Create with/without phone, token field, verify JWT |
| `PropertiesService` | 15 | CRUD, enable/disable, cleaners, iCal, checklist (true/false), service ID filter |
| `ChecklistsService` | 5 | List, get (with typed items), create, update, delete |
| `OtherService` | 7 | All 7 utility endpoints |
| `BlacklistService` | 4 | List, add (with/without reason), remove |
| `PaymentMethodsService` | 6 | All 6 endpoints |
| `WebhooksService` | 4 | List, create, update, delete |
| Error types | 8 | `AuthError` on 401, `APIError` on 404/422/500, `CleansterError` string, `ResponseBody` |
| Models | 4 | Nullable fields, User.Token pointer, Checklist.Items array, APIResponse.Message |
| **Total** | **95** | |

---

## Project Structure

```
cleanster-go-sdk/
├── go.mod                  ← Module: github.com/cleanster/cleanster-go-sdk (Go 1.21)
├── config.go               ← Config, NewSandboxConfig, NewProductionConfig, constants
├── errors.go               ← CleansterError, AuthError, APIError
├── models.go               ← APIResponse[T], rawAPIResponse, all model and request structs
├── http_client.go          ← net/http transport: get/post/put/delete, auth header injection
├── client.go               ← Client struct, NewSandboxClient, NewProductionClient, NewClient
├── bookings.go             ← BookingsService (17 methods)
├── users.go                ← UsersService (3 methods)
├── properties.go           ← PropertiesService (14 methods)
├── checklists.go           ← ChecklistsService (5 methods)
├── other.go                ← OtherService (7 methods)
├── blacklist.go            ← BlacklistService (3 methods)
├── payment_methods.go      ← PaymentMethodsService (6 methods)
├── webhooks.go             ← WebhooksService (4 methods)
├── cleanster_test.go       ← 92 tests (package cleanster_test, net/http/httptest)
├── README.md
├── LICENSE
├── CHANGELOG.md
└── .gitignore
```

### Key Design Decisions

**Why `net/http` only?**
Zero external dependencies means no dependency conflicts, no security advisories from transitive deps, and a simpler `go.sum`. The standard library HTTP client is production-grade.

**Why generics for `APIResponse[T]`?**
Generics (Go 1.18+) eliminate type assertions. Callers get a fully typed `.Data` field without any casting. Requires Go 1.21+ (matching Cleanster's recommended baseline).

**Why pointers for optional fields (`*int`, `*string`)?**
Go's zero values (`0`, `""`) are semantically meaningful in the API (e.g., `cleanerID = 0` vs. `cleanerID = nil`). Pointers precisely represent nullable/optional API fields.

**Why `context.Context` on every method?**
Standard Go practice — allows callers to cancel in-flight requests, apply per-request deadlines, and propagate trace IDs through middleware.

---

## Contributing

1. Fork the repository on GitHub.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and add tests in `cleanster_test.go`.
4. Ensure all tests pass: `go test ./...`
5. Check formatting: `gofmt -l .`
6. Submit a pull request with a clear description.

### Code Style

- Follow standard Go conventions (`gofmt`, `go vet`)
- All public types and methods must have doc comments (`// TypeName ...`)
- Use `context.Context` as the first parameter for all API-calling methods
- Use pointer types for all nullable/optional model fields
- Tests live in the `cleanster_test` package (external test package)
- No external dependencies in production code

---

## License

This SDK is released under the [MIT License](LICENSE). You are free to use, modify, and distribute it in personal and commercial projects.

---

## Support

| Resource | Link |
|----------|------|
| API Documentation | https://documenter.getpostman.com/view/26172658/2sAYdoF7ep |
| Go Package Reference | https://pkg.go.dev/github.com/cleanster/cleanster-go-sdk |
| Partner Support | partner@cleanster.com |
| General Support | support@cleanster.com |
| GitHub Issues | https://github.com/cleanster/cleanster-go-sdk/issues |

---

*Made with care for the Cleanster partner ecosystem.*
