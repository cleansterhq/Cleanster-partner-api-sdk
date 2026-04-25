# Cleanster Go SDK

<p align="center">
  <strong>Official Go client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Go-1.21%2B-00ADD8?logo=go" alt="Go 1.21+">
  <img src="https://img.shields.io/badge/tests-94%20passing-brightgreen" alt="94 passing">
  <img src="https://img.shields.io/badge/dependencies-zero-brightgreen" alt="Zero dependencies">
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
- [Environments](#environments)
- [Booking Flow](#booking-flow)
- [API Reference](#api-reference)
  - [Bookings](#bookings)
  - [Users](#users)
  - [Properties](#properties)
  - [Checklists](#checklists)
  - [Other / Reference Data](#other--reference-data)
  - [Blacklist](#blacklist)
  - [Payment Methods](#payment-methods)
  - [Webhooks](#webhooks)
- [Type Reference](#type-reference)
- [Error Handling](#error-handling)
- [Test Coupon Codes](#test-coupon-codes)
- [Chat Window Rules](#chat-window-rules)
- [Webhook Events](#webhook-events)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## Overview

The Cleanster Go SDK provides a clean, idiomatic Go interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It targets Go 1.21+ and uses only Go's standard library — zero external dependencies.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal calendar sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create task lists and assign to bookings
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — prevent specific cleaners from being assigned

---

## Requirements

- **Go 1.21** or later
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

```bash
go get github.com/cleanster/cleanster-go-sdk
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, from `Users.FetchAccessToken(ctx, userID)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```go
import (
    cleanster "github.com/cleanster/cleanster-go-sdk"
    "context"
)

client := cleanster.NewClient("your-access-key", "")

resp, err := client.Users.CreateUser(context.Background(), cleanster.CreateUserRequest{
    Email:     "jane@example.com",
    FirstName: "Jane",
    LastName:  "Doe",
    Phone:     "+15551234567",
})
if err != nil {
    log.Fatal(err)
}
userID := int(resp.Data["userId"].(float64))
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```go
tokenResp, err := client.Users.FetchAccessToken(context.Background(), userID)
if err != nil {
    log.Fatal(err)
}
userToken := tokenResp.Data["token"].(string)
```

**Step 4 — Build the client with both credentials**:

```go
client := cleanster.NewClient("your-access-key", userToken)
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401.

---

## Quick Start

```go
package main

import (
    cleanster "github.com/cleanster/cleanster-go-sdk"
    "context"
    "fmt"
    "log"
)

func main() {
    ctx := context.Background()
    client := cleanster.NewClient("your-access-key", "user-jwt-token")

    // Get recommended cleaning hours
    hoursResp, err := client.Other.GetRecommendedHours(ctx, cleanster.RecommendedHoursParams{
        PropertyID:     1004,
        BathroomCount:  2,
        RoomCount:      3,
    })
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("Recommended hours:", hoursResp.Data)

    // Create a booking
    bookingResp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{
        PropertyID:      1004,
        Date:            "2025-09-01",
        Time:            "10:00",
        PlanID:          2,
        RoomCount:       3,
        BathroomCount:   2,
        Hours:           3.0,
        ExtraSupplies:   false,
        PaymentMethodID: 10,
        CouponCode:      "20POFF", // optional — 20% off in sandbox
    })
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("Created booking:", bookingResp.Data)

    // List open bookings
    listResp, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{
        PageNo: cleanster.PageNo(1),
        Status: "OPEN",
    })
    if err != nil {
        log.Fatal(err)
    }
    fmt.Printf("Open bookings: %d\n", len(listResp.Data.Bookings))
}
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```go
// Sandbox (default)
client := cleanster.NewClient("key", "token")

// Production
client := cleanster.NewClientWithOptions(cleanster.ClientOptions{
    AccessKey:   "key",
    Token:       "token",
    Environment: cleanster.Production,
})
```

---

## Booking Flow

```
CreateBooking()          →   OPEN
                                 │
     Bookings.AssignCleaner()
                                 │
                                 ▼
                       CLEANER_ASSIGNED
                                 │
                    Cleaner starts the job
                                 │
                  ┌──────────────┴──────────────┐
                  ▼                             ▼
             COMPLETED                     CANCELLED
```

Status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All methods return `(APIResponse[T], error)`.

`APIResponse[T]` has:
- `.Status` — HTTP status code
- `.Message` — Human-readable result
- `.Data` — Typed payload

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

| Field | Type | Required | Description |
|---|---|---|---|
| `PageNo` | `*int` | Yes | Page number (use `cleanster.PageNo(n)`) |
| `Status` | string | No | `OPEN` · `CLEANER_ASSIGNED` · `COMPLETED` · `CANCELLED` · `REMOVED` |

```go
resp, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{
    PageNo: cleanster.PageNo(1),
    Status: "OPEN",
})
for _, b := range resp.Data.Bookings {
    fmt.Println(b.ID, b.Status)
}
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

```go
resp, err := client.Bookings.GetBooking(ctx, 16926)
fmt.Println(resp.Data.Status, "on", resp.Data.Date)
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `PropertyID` | int | Yes | Property to clean |
| `Date` | string | Yes | `YYYY-MM-DD` |
| `Time` | string | Yes | `HH:MM` (24-hour) |
| `PlanID` | int | Yes | Cleaning plan ID |
| `RoomCount` | int | Yes | Number of rooms |
| `BathroomCount` | int | Yes | Number of bathrooms |
| `Hours` | float64 | Yes | Duration |
| `ExtraSupplies` | bool | Yes | Cleaner brings supplies |
| `PaymentMethodID` | int | Yes | Payment method ID |
| `CouponCode` | string | No | Discount coupon |
| `CleaningExtras` | []int | No | Extra service IDs |

```go
resp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{
    PropertyID:      1004,
    Date:            "2025-09-01",
    Time:            "10:00",
    PlanID:          2,
    RoomCount:       3,
    BathroomCount:   2,
    Hours:           3.0,
    ExtraSupplies:   false,
    PaymentMethodID: 10,
    CouponCode:      "50POFF",
})
fmt.Println("Booking data:", resp.Data)
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```go
_, err = client.Bookings.AssignCleaner(ctx, 16926, cleanerID)
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```go
_, err = client.Bookings.RemoveCleaner(ctx, 16926)
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

```go
_, err = client.Bookings.AdjustHours(ctx, 16926, 4.5)
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

```go
_, err = client.Bookings.RescheduleBooking(ctx, 16926, "2025-09-15", "14:00")
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

```go
_, err = client.Bookings.PayExpenses(ctx, 16926, paymentMethodID)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```go
resp, err := client.Bookings.GetInspection(ctx, 16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```go
resp, err := client.Bookings.GetInspectionDetails(ctx, 16926)
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

```go
_, err = client.Bookings.CancelBooking(ctx, 16926, "Scheduling conflict")
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```go
_, err = client.Bookings.AssignChecklistToBooking(ctx, 16926, 105)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

```go
_, err = client.Bookings.SubmitFeedback(ctx, 16926, 5, "Excellent work!")
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

```go
_, err = client.Bookings.AddTip(ctx, 16926, 15.00, paymentMethodID)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

```go
resp, err := client.Bookings.GetChat(ctx, 16926)
```

**`data.messages[]` fields:**

| Field | Type | Description |
|---|---|---|
| `message_id` | string | Unique ID |
| `sender_id` | string | Reference key (e.g. `C6`, `P3`) |
| `content` | string | Text (empty for media) |
| `timestamp` | string | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `message_type` | string | `text` or `media` |
| `attachments` | array | Media items |
| `is_read` | bool | Read status |
| `sender_type` | string | `client` · `cleaner` · `support` · `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

```go
_, err = client.Bookings.SendMessage(ctx, 16926, "Please bring extra supplies.")
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```go
_, err = client.Bookings.DeleteMessage(ctx, 16926, "-OLPrlE06uD8tQ8ebJZw")
```

---

### Users

#### Create User
**`POST /v1/user/account`**

```go
resp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{
    Email:     "jane@example.com",
    FirstName: "Jane",
    LastName:  "Doe",
    Phone:     "+15551234567",
})
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

```go
resp, err := client.Users.FetchAccessToken(ctx, 42)
token := resp.Data["token"].(string)
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

```go
resp, err := client.Users.VerifyJWT(ctx, userToken)
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```go
resp, err := client.Properties.ListProperties(ctx, serviceID)
```

---

#### Create Property
**`POST /v1/properties`**

```go
resp, err := client.Properties.CreateProperty(ctx, cleanster.CreatePropertyRequest{
    Address:   "123 Main St",
    City:      "Chicago",
    State:     "IL",
    Zip:       "60601",
    ServiceID: 1,
})
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```go
resp, err := client.Properties.GetProperty(ctx, 1004)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```go
resp, err := client.Properties.UpdateProperty(ctx, 1004, updateReq)
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```go
resp, err := client.Properties.UpdateAdditionalInfo(ctx, 1004, infoReq)
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```go
_, err = client.Properties.EnableOrDisable(ctx, 1004, true)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```go
_, err = client.Properties.DeleteProperty(ctx, 1004)
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```go
resp, err := client.Properties.GetIcal(ctx, 1004)
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```go
_, err = client.Properties.AddIcal(ctx, 1004, "https://airbnb.com/calendar/ical/12345.ics")
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```go
_, err = client.Properties.DeleteIcal(ctx, 1004, []int{101, 102, 103})
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```go
resp, err := client.Properties.ListCleaners(ctx, 1004)
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```go
_, err = client.Properties.AddCleaner(ctx, 1004, cleanerID)
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```go
_, err = client.Properties.RemoveCleaner(ctx, 1004, cleanerID)
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

```go
_, err = client.Properties.SetDefaultChecklist(ctx, 1004, 105, true)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```go
resp, err := client.Checklists.ListChecklists(ctx)
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```go
resp, err := client.Checklists.GetChecklist(ctx, 105)
for _, item := range resp.Data.Items {
    fmt.Println(item.Task)
}
```

---

#### Create Checklist
**`POST /v1/checklist`**

```go
resp, err := client.Checklists.CreateChecklist(ctx, cleanster.CreateChecklistRequest{
    Name: "Deep Clean",
    Items: []string{
        "Vacuum all rooms",
        "Mop kitchen and bathroom floors",
        "Scrub toilets, sinks, and tubs",
        "Wipe all countertops",
        "Clean inside microwave and oven",
    },
})
fmt.Println("Checklist ID:", resp.Data.ID)
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```go
_, err = client.Checklists.UpdateChecklist(ctx, 105, cleanster.CreateChecklistRequest{
    Name:  "Standard Clean",
    Items: []string{"Vacuum", "Wipe surfaces", "Clean bathrooms"},
})
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```go
_, err = client.Checklists.DeleteChecklist(ctx, 105)
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```go
data, _ := os.ReadFile("bathroom-guide.jpg")
_, err = client.Checklists.UploadChecklistImage(ctx, 105, data, "bathroom-guide.jpg")
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```go
resp, err := client.Other.GetServices(ctx)
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```go
resp, err := client.Other.GetPlans(ctx, 1004)
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```go
resp, err := client.Other.GetCleaningExtras(ctx, 1)
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```go
resp, err := client.Other.GetRecommendedHours(ctx, cleanster.RecommendedHoursParams{
    PropertyID:    1004,
    BathroomCount: 2,
    RoomCount:     3,
})
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

```go
resp, err := client.Other.GetCostEstimate(ctx, estimateReq)
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```go
resp, err := client.Other.GetAvailableCleaners(ctx, availabilityReq)
```

---

#### Get Coupons
**`GET /v1/coupons`**

```go
resp, err := client.Other.GetCoupons(ctx)
```

#### List Cleaners
**`GET /v1/cleaners`**

```go
resp, err := client.Other.ListCleaners(ctx, "active", "Jane")
```

#### Get Cleaner
**`GET /v1/cleaners/{cleanerId}`**

```go
resp, err := client.Other.GetCleaner(ctx, 789)
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```go
resp, err := client.Blacklist.GetBlacklist(ctx, 1)
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```go
_, err = client.Blacklist.AddToBlacklist(ctx, cleanerID)
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```go
_, err = client.Blacklist.RemoveFromBlacklist(ctx, cleanerID)
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```go
resp, err := client.PaymentMethods.GetSetupIntentDetails(ctx)
clientSecret := resp.Data["clientSecret"].(string)
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```go
resp, err := client.PaymentMethods.GetPayPalClientToken(ctx)
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```go
resp, err := client.PaymentMethods.AddPaymentMethod(ctx, paymentReq)
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```go
resp, err := client.PaymentMethods.ListPaymentMethods(ctx)
for _, pm := range resp.Data {
    fmt.Println(pm.Type, pm.Last4)
}
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```go
_, err = client.PaymentMethods.DeletePaymentMethod(ctx, 193)
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```go
_, err = client.PaymentMethods.SetDefaultPaymentMethod(ctx, 193)
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```go
resp, err := client.Webhooks.ListWebhooks(ctx)
```

---

#### Create Webhook
**`POST /v1/webhooks`**

```go
_, err = client.Webhooks.CreateWebhook(ctx, cleanster.WebhookRequest{
    URL:   "https://your-server.com/hooks/cleanster",
    Event: "booking.status_changed",
})
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```go
_, err = client.Webhooks.UpdateWebhook(ctx, 50, cleanster.WebhookRequest{
    URL:   "https://your-server.com/hooks/cleanster-v2",
    Event: "booking.completed",
})
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```go
_, err = client.Webhooks.DeleteWebhook(ctx, 50)
```

---

## Type Reference

### `APIResponse[T]`

```go
type APIResponse[T any] struct {
    Status  int    `json:"status"`
    Message string `json:"message"`
    Data    T      `json:"data"`
}
```

### `Booking`

```go
type Booking struct {
    ID              int     `json:"id"`
    Status          string  `json:"status"`
    Date            string  `json:"date"`
    Time            string  `json:"time"`
    Hours           float64 `json:"hours"`
    Cost            float64 `json:"cost"`
    PropertyID      int     `json:"propertyId"`
    PlanID          int     `json:"planId"`
    RoomCount       int     `json:"roomCount"`
    BathroomCount   int     `json:"bathroomCount"`
    ExtraSupplies   bool    `json:"extraSupplies"`
    PaymentMethodID int     `json:"paymentMethodId"`
    Cleaner         *Cleaner `json:"cleaner"`
}
```

### `Checklist`

```go
type Checklist struct {
    ID    int             `json:"id"`
    Name  string          `json:"name"`
    Items []ChecklistItem `json:"items"`
}

type ChecklistItem struct {
    ID          int    `json:"id"`
    Task        string `json:"task"`
    Order       int    `json:"order"`
    IsCompleted bool   `json:"isCompleted"`
}
```

### `PaymentMethod`

```go
type PaymentMethod struct {
    ID          int    `json:"id"`
    Type        string `json:"type"`
    Last4       string `json:"last4"`
    Brand       string `json:"brand"`
    ExpiryMonth int    `json:"expiryMonth"`
    ExpiryYear  int    `json:"expiryYear"`
    IsDefault   bool   `json:"isDefault"`
}
```

---

## Error Handling

```go
resp, err := client.Bookings.GetBooking(ctx, 99999)
if err != nil {
    var apiErr *cleanster.APIError
    if errors.As(err, &apiErr) {
        fmt.Printf("HTTP %d: %s\n", apiErr.StatusCode, apiErr.Message)
        if apiErr.StatusCode == 401 {
            // Re-fetch user token and retry
        }
    }
    log.Fatal(err)
}
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request |
| 401 | Unauthorized — invalid credentials |
| 403 | Forbidden |
| 404 | Not found |
| 422 | Validation error |
| 429 | Rate limit exceeded |
| 500 | Server error |

---

## Test Coupon Codes

Use in the **sandbox** environment only:

| Code | Discount | Status |
|---|---|---|
| `100POFF` | 100% off | Active |
| `50POFF` | 50% off | Active |
| `20POFF` | 20% off | Active |
| `200OFF` | $200 off | Active |
| `100OFF` | $100 off | Active |
| `75POFF` | 75% off | **Expired** |

---

## Chat Window Rules

| Booking State | Chat Available |
|---|---|
| `OPEN` — within 24 h of start | Yes |
| `COMPLETED` — within 24 h of completion | Yes |
| `IN_PROGRESS` (hanging state) | Yes — **no time restriction** |
| `CANCELLED` | No |
| Older than 24 h | No |

---

## Webhook Events

| Event | Description |
|---|---|
| `booking.status_changed` | Any status transition |
| `booking.cleaner_assigned` | Cleaner assigned |
| `booking.cancelled` | Booking cancelled |
| `booking.completed` | Booking completed |

---

## Running Tests

```bash
go test ./... -v
```

Expected: **94 tests passing.**

---

## Project Structure

```
go-sdk/
├── go.mod
├── client.go              # NewClient, ClientOptions
├── bookings.go            # BookingsService
├── users.go               # UsersService
├── properties.go          # PropertiesService
├── checklists.go          # ChecklistsService
├── other.go               # OtherService
├── blacklist.go           # BlacklistService
├── payment_methods.go     # PaymentMethodsService
├── webhooks.go            # WebhooksService
├── models.go              # Booking, Checklist, PaymentMethod, etc.
├── http.go                # Internal HTTP client (net/http)
└── cleanster_test.go      # All 92 tests
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
