# Cleanster C# SDK

<p align="center">
  <strong>Official .NET client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/.NET-8.0%2B-blueviolet?logo=dotnet" alt=".NET 8+">
  <img src="https://img.shields.io/badge/NuGet-Cleanster-blue?logo=nuget" alt="NuGet">
  <img src="https://img.shields.io/badge/tests-109%20passing-brightgreen" alt="109 passing">
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
- [Models Reference](#models-reference)
- [Error Handling](#error-handling)
- [Cancellation and Timeouts](#cancellation-and-timeouts)
- [Dependency Injection](#dependency-injection)
- [Test Coupon Codes](#test-coupon-codes)
- [Chat Window Rules](#chat-window-rules)
- [Webhook Events](#webhook-events)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## Overview

The Cleanster C# SDK provides a fully-typed, async/await interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It uses only `System.Net.Http` and `System.Text.Json` from the .NET BCL — zero external NuGet dependencies.

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

- **.NET 8.0** or later
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

### .NET CLI

```bash
dotnet add package Cleanster
```

### NuGet Package Manager

```
Install-Package Cleanster
```

### PackageReference

```xml
<PackageReference Include="Cleanster" Version="1.0.0" />
```

### Build from Source

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/csharp-sdk
dotnet build
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, from `Users.FetchAccessTokenAsync(userId)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```csharp
using Cleanster;

var client = new CleansterClient("your-access-key");

var resp = await client.Users.CreateUserAsync(
    email:     "jane@example.com",
    firstName: "Jane",
    lastName:  "Doe",
    phone:     "+15551234567"
);
int userId = ((JsonElement)resp.Data!).GetProperty("userId").GetInt32();
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```csharp
var tokenResp = await client.Users.FetchAccessTokenAsync(userId);
string userToken = ((JsonElement)tokenResp.Data!).GetProperty("token").GetString()!;
```

**Step 4 — Build the client with both credentials**:

```csharp
var client = new CleansterClient("your-access-key", userToken);
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401.

---

## Quick Start

```csharp
using Cleanster;
using Cleanster.Models;

var client = new CleansterClient("your-access-key", "user-jwt-token");

// Get recommended cleaning hours
var hours = await client.Other.GetRecommendedHoursAsync(
    propertyId:    1004,
    bathroomCount: 2,
    roomCount:     3
);
Console.WriteLine($"Recommended hours: {hours.Data}");

// Create a booking
var booking = await client.Bookings.CreateBookingAsync(new CreateBookingRequest
{
    PropertyId      = 1004,
    Date            = "2025-09-01",
    Time            = "10:00",
    PlanId          = 2,
    RoomCount       = 3,
    BathroomCount   = 2,
    Hours           = 3.0,
    ExtraSupplies   = false,
    PaymentMethodId = 10,
    CouponCode      = "20POFF"   // optional — 20% off in sandbox
});
Console.WriteLine($"Created booking: {booking.Data}");

// List open bookings
var list = await client.Bookings.GetBookingsAsync(pageNo: 1, status: "OPEN");
Console.WriteLine($"Open bookings: {list.Data}");
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```csharp
// Sandbox (default)
var client = new CleansterClient("key", "token");

// Production
var client = new CleansterClient("key", "token", CleansterEnvironment.Production);
```

---

## Booking Flow

```
CreateBookingAsync()          →   OPEN
                                      │
     Bookings.AssignCleanerAsync()
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

All methods return `Task<ApiResponse<T>>` with:
- `.Status` — HTTP status code
- `.Message` — Human-readable result
- `.Data` — Typed response payload

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | int | Yes | Page number (1-based) |
| `status` | string? | No | `OPEN` · `CLEANER_ASSIGNED` · `COMPLETED` · `CANCELLED` · `REMOVED` |

```csharp
var resp = await client.Bookings.GetBookingsAsync(pageNo: 1, status: "OPEN");
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

```csharp
var resp = await client.Bookings.GetBookingAsync(16926);
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `PropertyId` | int | Yes | Property to clean |
| `Date` | string | Yes | `YYYY-MM-DD` |
| `Time` | string | Yes | `HH:MM` (24-hour) |
| `PlanId` | int | Yes | Cleaning plan ID |
| `RoomCount` | int | Yes | Number of rooms |
| `BathroomCount` | int | Yes | Number of bathrooms |
| `Hours` | double | Yes | Duration |
| `ExtraSupplies` | bool | Yes | Cleaner brings supplies |
| `PaymentMethodId` | int | Yes | Payment method ID |
| `CouponCode` | string? | No | Discount coupon |
| `CleaningExtras` | int[]? | No | Extra service IDs |

```csharp
var resp = await client.Bookings.CreateBookingAsync(new CreateBookingRequest
{
    PropertyId      = 1004,
    Date            = "2025-09-01",
    Time            = "10:00",
    PlanId          = 2,
    RoomCount       = 3,
    BathroomCount   = 2,
    Hours           = 3.0,
    ExtraSupplies   = false,
    PaymentMethodId = 10,
    CouponCode      = "50POFF"
});
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```csharp
await client.Bookings.AssignCleanerAsync(bookingId: 16926, cleanerId: 3);
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```csharp
await client.Bookings.RemoveCleanerAsync(16926);
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

```csharp
await client.Bookings.AdjustHoursAsync(bookingId: 16926, hours: 4.5);
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

```csharp
await client.Bookings.RescheduleBookingAsync(
    bookingId: 16926, date: "2025-09-15", time: "14:00"
);
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

```csharp
await client.Bookings.PayExpensesAsync(bookingId: 16926, paymentMethodId: 10);
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```csharp
var resp = await client.Bookings.GetInspectionAsync(16926);
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```csharp
var resp = await client.Bookings.GetInspectionDetailsAsync(16926);
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

```csharp
await client.Bookings.CancelBookingAsync(bookingId: 16926, reason: "Scheduling conflict");
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```csharp
await client.Bookings.AssignChecklistToBookingAsync(bookingId: 16926, checklistId: 105);
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | int | Yes | 1–5 stars |
| `comment` | string? | No | Written feedback |

```csharp
await client.Bookings.SubmitFeedbackAsync(
    bookingId: 16926, rating: 5, comment: "Excellent work!"
);
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

```csharp
await client.Bookings.SubmitTipAsync(
    bookingId: 16926, amount: 15.00, paymentMethodId: 10
);
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

```csharp
var resp = await client.Bookings.GetChatAsync(16926);
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
| `attachments[].type` | string | `image`, `video`, `sound` |
| `attachments[].url` | string | Media URL |
| `attachments[].thumb_url` | string | Thumbnail (nullable) |
| `is_read` | bool | Read status |
| `sender_type` | string | `client` · `cleaner` · `support` · `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

```csharp
await client.Bookings.SendMessageAsync(
    bookingId: 16926, message: "Please bring extra supplies."
);
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```csharp
await client.Bookings.DeleteMessageAsync(
    bookingId: 16926, messageId: "-OLPrlE06uD8tQ8ebJZw"
);
```

---

### Users

#### Create User
**`POST /v1/user/account`**

```csharp
var resp = await client.Users.CreateUserAsync(
    email:     "jane@example.com",
    firstName: "Jane",
    lastName:  "Doe",
    phone:     "+15551234567"
);
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Only `access-key` is required for this call.

```csharp
var resp = await client.Users.FetchAccessTokenAsync(userId: 42);
string token = ((JsonElement)resp.Data!).GetProperty("token").GetString()!;
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

```csharp
var resp = await client.Users.VerifyJwtAsync(userToken);
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```csharp
var resp = await client.Properties.ListPropertiesAsync(serviceId: 1);
```

---

#### Create Property
**`POST /v1/properties`**

```csharp
var resp = await client.Properties.CreatePropertyAsync(new CreatePropertyRequest
{
    Address   = "123 Main St",
    City      = "Chicago",
    State     = "IL",
    Zip       = "60601",
    ServiceId = 1,
});
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```csharp
var resp = await client.Properties.GetPropertyAsync(1004);
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```csharp
await client.Properties.UpdatePropertyAsync(1004, updateReq);
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```csharp
await client.Properties.UpdateAdditionalInfoAsync(1004, infoReq);
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```csharp
await client.Properties.EnableOrDisableAsync(propertyId: 1004, enabled: true);
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```csharp
await client.Properties.DeletePropertyAsync(1004);
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```csharp
var resp = await client.Properties.GetIcalAsync(1004);
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```csharp
await client.Properties.AddIcalAsync(1004,
    "https://www.airbnb.com/calendar/ical/12345.ics");
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```csharp
await client.Properties.DeleteIcalAsync(1004, new[] { 101, 102, 103 });
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```csharp
var resp = await client.Properties.ListCleanersAsync(1004);
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```csharp
await client.Properties.AddCleanerAsync(propertyId: 1004, cleanerId: 3);
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```csharp
await client.Properties.RemoveCleanerAsync(propertyId: 1004, cleanerId: 3);
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

```csharp
await client.Properties.SetDefaultChecklistAsync(
    propertyId: 1004, checklistId: 105, updateUpcomingBookings: true
);
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```csharp
var resp = await client.Checklists.ListChecklistsAsync();
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```csharp
var resp = await client.Checklists.GetChecklistAsync(105);
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `Name` | string | Yes | Display name |
| `Items` | string[] | Yes | Ordered task list |

```csharp
var resp = await client.Checklists.CreateChecklistAsync(new CreateChecklistRequest
{
    Name  = "Deep Clean",
    Items = new[]
    {
        "Vacuum all rooms",
        "Mop kitchen and bathroom floors",
        "Scrub toilets, sinks, and tubs",
        "Wipe all countertops",
        "Clean inside microwave and oven",
    }
});
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```csharp
await client.Checklists.UpdateChecklistAsync(105, new CreateChecklistRequest
{
    Name  = "Standard Clean",
    Items = new[] { "Vacuum", "Wipe surfaces", "Clean bathrooms" }
});
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```csharp
await client.Checklists.DeleteChecklistAsync(105);
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```csharp
byte[] imageBytes = await File.ReadAllBytesAsync("bathroom-guide.jpg");
await client.Checklists.UploadChecklistImageAsync(imageBytes, "bathroom-guide.jpg");
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```csharp
var resp = await client.Other.GetServicesAsync();
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```csharp
var resp = await client.Other.GetPlansAsync(1004);
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```csharp
var resp = await client.Other.GetCleaningExtrasAsync(1);
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```csharp
var resp = await client.Other.GetRecommendedHoursAsync(
    propertyId: 1004, bathroomCount: 2, roomCount: 3
);
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

```csharp
var resp = await client.Other.GetCostEstimateAsync(estimateRequest);
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```csharp
var resp = await client.Other.GetAvailableCleanersAsync(availabilityRequest);
```

---

#### Get Coupons
**`GET /v1/coupons`**

```csharp
var resp = await client.Other.GetCouponsAsync();
```

#### List Cleaners
**`GET /v1/cleaners`**

```csharp
var resp = await client.Other.ListCleanersAsync(status: "active", search: "Jane");
```

#### Get Cleaner
**`GET /v1/cleaners/{cleanerId}`**

```csharp
var resp = await client.Other.GetCleanerAsync(789);
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```csharp
var resp = await client.Blacklist.GetBlacklistAsync(pageNo: 1);
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```csharp
await client.Blacklist.AddToBlacklistAsync(cleanerId: 3);
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```csharp
await client.Blacklist.RemoveFromBlacklistAsync(cleanerId: 3);
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```csharp
var resp = await client.PaymentMethods.GetSetupIntentDetailsAsync();
// Use resp.Data["clientSecret"] to initialize Stripe.js on the frontend
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```csharp
var resp = await client.PaymentMethods.GetPayPalClientTokenAsync();
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```csharp
var resp = await client.PaymentMethods.AddPaymentMethodAsync(paymentRequest);
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```csharp
var resp = await client.PaymentMethods.ListPaymentMethodsAsync();
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```csharp
await client.PaymentMethods.DeletePaymentMethodAsync(193);
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```csharp
await client.PaymentMethods.SetDefaultAsync(193);
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```csharp
var resp = await client.Webhooks.ListWebhooksAsync();
```

---

#### Create Webhook
**`POST /v1/webhooks`**

| Field | Type | Required | Description |
|---|---|---|---|
| `Url` | string | Yes | HTTPS endpoint |
| `Event` | string | Yes | Event type |

```csharp
await client.Webhooks.CreateWebhookAsync(new WebhookRequest
{
    Url   = "https://your-server.com/hooks/cleanster",
    Event = "booking.status_changed"
});
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```csharp
await client.Webhooks.UpdateWebhookAsync(50, new WebhookRequest
{
    Url   = "https://your-server.com/hooks/cleanster-v2",
    Event = "booking.completed"
});
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```csharp
await client.Webhooks.DeleteWebhookAsync(50);
```

---

## Models Reference

### `ApiResponse<T>`

```csharp
public record ApiResponse<T>
{
    public int    Status  { get; init; }   // HTTP status code
    public string Message { get; init; }   // Result description
    public T?     Data    { get; init; }   // Typed payload
}
```

### `CreateBookingRequest`

```csharp
public class CreateBookingRequest
{
    public int      PropertyId      { get; set; }
    public string   Date            { get; set; } = "";   // YYYY-MM-DD
    public string   Time            { get; set; } = "";   // HH:MM
    public int      PlanId          { get; set; }
    public int      RoomCount       { get; set; }
    public int      BathroomCount   { get; set; }
    public double   Hours           { get; set; }
    public bool     ExtraSupplies   { get; set; }
    public int      PaymentMethodId { get; set; }
    public string?  CouponCode      { get; set; }
    public int[]?   CleaningExtras  { get; set; }
}
```

### `CreateChecklistRequest`

```csharp
public class CreateChecklistRequest
{
    public string   Name  { get; set; } = "";
    public string[] Items { get; set; } = Array.Empty<string>();
}
```

### `WebhookRequest`

```csharp
public class WebhookRequest
{
    public string Url   { get; set; } = "";
    public string Event { get; set; } = "";
}
```

### `CreatePropertyRequest`

```csharp
public class CreatePropertyRequest
{
    public string  Address   { get; set; } = "";
    public string  City      { get; set; } = "";
    public string  State     { get; set; } = "";
    public string  Zip       { get; set; } = "";
    public int     ServiceId { get; set; }
    public int?    Bedrooms  { get; set; }
    public int?    Bathrooms { get; set; }
    public string? Notes     { get; set; }
}
```

---

## Error Handling

```csharp
using Cleanster.Exceptions;

try
{
    var resp = await client.Bookings.GetBookingAsync(99999);
}
catch (CleansterApiException ex)
{
    Console.Error.WriteLine($"HTTP {ex.StatusCode}: {ex.Message}");

    if (ex.StatusCode == 401)
    {
        // Re-fetch user token and retry
    }
    else if (ex.StatusCode == 404)
    {
        Console.WriteLine("Booking not found");
    }
    else if (ex.StatusCode == 422)
    {
        Console.WriteLine($"Validation error: {ex.Message}");
    }
}
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request — malformed parameters |
| 401 | Unauthorized — invalid or missing credentials |
| 403 | Forbidden — insufficient permissions |
| 404 | Not found |
| 422 | Validation error |
| 429 | Rate limit exceeded |
| 500 | Internal server error |

---

## Cancellation and Timeouts

All `*Async` methods accept an optional `CancellationToken`:

```csharp
using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(10));

var resp = await client.Bookings.GetBookingAsync(16926, cts.Token);
```

---

## Dependency Injection

The SDK integrates seamlessly with `Microsoft.Extensions.DependencyInjection`:

```csharp
// Program.cs / Startup.cs
services.AddSingleton<CleansterClient>(sp =>
    new CleansterClient(
        accessKey: configuration["Cleanster:AccessKey"]!,
        token:     configuration["Cleanster:UserToken"]!
    )
);
```

**For testing**, inject a mock via the constructor overload that accepts `HttpClient`:

```csharp
var mockHandler = new MockHttpMessageHandler();
var httpClient  = new HttpClient(mockHandler);
var client      = new CleansterClient("test-key", "test-token", httpClient);
```

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
dotnet test
```

Expected: **109 tests passing.**

To run with verbose output:

```bash
dotnet test --logger "console;verbosity=detailed"
```

---

## Project Structure

```
csharp-sdk/
├── Cleanster.sln
├── src/
│   └── Cleanster/
│       ├── Cleanster.csproj
│       ├── CleansterClient.cs          # Entry point
│       ├── CleansterEnvironment.cs     # Sandbox / Production enum
│       ├── Models/
│       │   ├── ApiResponse.cs
│       │   ├── CreateBookingRequest.cs
│       │   ├── CreateChecklistRequest.cs
│       │   ├── CreatePropertyRequest.cs
│       │   └── WebhookRequest.cs
│       ├── Services/
│       │   ├── BookingsService.cs
│       │   ├── UsersService.cs
│       │   ├── PropertiesService.cs
│       │   ├── ChecklistsService.cs
│       │   ├── OtherService.cs
│       │   ├── BlacklistService.cs
│       │   ├── PaymentMethodsService.cs
│       │   └── WebhooksService.cs
│       ├── Http/
│       │   └── CleansterHttpClient.cs  # HttpClient wrapper
│       └── Exceptions/
│           └── CleansterApiException.cs
└── tests/
    └── Cleanster.Tests/
        ├── Cleanster.Tests.csproj
        └── CleansterClientTests.cs
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
