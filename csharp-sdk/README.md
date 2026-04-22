# Cleanster C# SDK

[![NuGet](https://img.shields.io/nuget/v/Cleanster.svg)](https://www.nuget.org/packages/Cleanster)
[![Tests](https://img.shields.io/badge/tests-107%20passing-brightgreen)]()
[![.NET](https://img.shields.io/badge/.NET-8.0%2B-blueviolet)](https://dotnet.microsoft.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

The official .NET SDK for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep).

**Zero external runtime dependencies** — uses only `System.Net.Http` and `System.Text.Json` from the .NET BCL. Fully async, nullable-reference-type safe, and designed for straightforward dependency injection in both production and tests.

---

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Authentication](#authentication)
  - [Step 1 — Your Partner Access Key](#step-1--your-partner-access-key)
  - [Step 2 — Create a User](#step-2--create-a-user)
  - [Step 3 — Fetch the Bearer Token](#step-3--fetch-the-bearer-token)
  - [Step 4 — Attach the Token to the Client](#step-4--attach-the-token-to-the-client)
- [Quick Start](#quick-start)
- [Environments](#environments)
- [API Reference](#api-reference)
  - [Bookings](#bookings)
  - [Users](#users)
  - [Properties](#properties)
  - [Checklists](#checklists)
  - [Other (Reference Data)](#other-reference-data)
  - [Blacklist](#blacklist)
  - [Payment Methods](#payment-methods)
  - [Webhooks](#webhooks)
- [Models](#models)
- [Error Handling](#error-handling)
- [Cancellation and Timeouts](#cancellation-and-timeouts)
- [Testing / Dependency Injection](#testing--dependency-injection)
- [Project Structure](#project-structure)
- [Running the Tests](#running-the-tests)

---

## Requirements

- .NET 8.0 or later
- A Cleanster Partner account ([contact Cleanster](https://cleanster.com) for access)

---

## Installation

```bash
dotnet add package Cleanster
```

Or add directly to your `.csproj`:

```xml
<PackageReference Include="Cleanster" Version="1.0.0" />
```

---

## Authentication

The Cleanster API uses **dual-header authentication** — every request must include:

| Header       | Value                                      |
|--------------|--------------------------------------------|
| `access-key` | Your static partner access key             |
| `token`      | A per-user JWT bearer token                |

### Step 1 — Your Partner Access Key

Store your access key in an environment variable or secret manager:

```bash
# .env / shell
CLEANSTER_API_KEY=pk_live_your_partner_key_here
```

```csharp
var apiKey = Environment.GetEnvironmentVariable("CLEANSTER_API_KEY")!;
```

### Step 2 — Create a User

A user account must exist before any booking-related calls can be made:

```csharp
var client = CleansterClient.Sandbox(apiKey);

var userResp = await client.Users.CreateUserAsync(
    email:     "jane@example.com",
    firstName: "Jane",
    lastName:  "Smith",
    phone:     "+15551234567");  // phone is optional

Console.WriteLine($"Created user: {userResp.Data.Id}");
```

### Step 3 — Fetch the Bearer Token

Exchange the user ID for a long-lived JWT token:

```csharp
var tokenResp = await client.Users.FetchAccessTokenAsync(userResp.Data.Id);
string bearerToken = tokenResp.Data.Token!;
```

### Step 4 — Attach the Token to the Client

One call sets the `token` header on every subsequent request:

```csharp
client.SetAccessToken(bearerToken);
```

The token is long-lived. Persist it alongside the user record and reuse it across sessions — there is no need to re-fetch on every request. Retrieve it at any time:

```csharp
string currentToken = client.GetAccessToken();
```

---

## Quick Start

```csharp
using Cleanster;
using Cleanster.Exceptions;

// 1. Create the client
var client = CleansterClient.Sandbox(
    Environment.GetEnvironmentVariable("CLEANSTER_API_KEY")!);

// 2. Register a user and get their bearer token
var user  = await client.Users.CreateUserAsync("jane@example.com", "Jane", "Smith");
var token = await client.Users.FetchAccessTokenAsync(user.Data.Id);
client.SetAccessToken(token.Data.Token!);

// 3. Add a property
var property = await client.Properties.AddPropertyAsync(
    name:          "Beachside Apartment",
    address:       "100 Ocean Drive",
    city:          "Miami",
    country:       "USA",
    roomCount:     2,
    bathroomCount: 1,
    serviceId:     1);

// 4. Calculate cost and pick a plan
var hours = await client.Other.GetRecommendedHoursAsync(
    property.Data.Id, bathroomCount: 1, roomCount: 2);
var cost = await client.Other.CalculateCostAsync(
    property.Data.Id, planId: 2, hours: 3.0, couponCode: "20POFF");

// 5. Add a payment method (paymentMethodId comes from Stripe.js on the client)
await client.PaymentMethods.AddPaymentMethodAsync("pm_test_xxx");
var methods = await client.PaymentMethods.GetPaymentMethodsAsync();
int methodId = methods.Data[0].Id;

// 6. Book a cleaning
try
{
    var booking = await client.Bookings.CreateBookingAsync(
        date:            "2025-08-15",
        time:            "10:00",
        propertyId:      property.Data.Id,
        roomCount:       2,
        bathroomCount:   1,
        planId:          2,
        hours:           3.0,
        extraSupplies:   false,
        paymentMethodId: methodId,
        couponCode:      "20POFF");

    Console.WriteLine($"Booked! ID={booking.Data.Id}, Cost=${booking.Data.Cost}");
}
catch (AuthException ex)
{
    Console.Error.WriteLine($"Auth failed: {ex.Message}");
}
catch (ApiException ex)
{
    Console.Error.WriteLine($"API error {ex.StatusCode}: {ex.ResponseBody}");
}
catch (CleansterException ex)
{
    Console.Error.WriteLine($"Network error: {ex.Message}");
}
```

---

## Environments

| Factory                           | Base URL                                                                                    | Use for                                          |
|-----------------------------------|---------------------------------------------------------------------------------------------|--------------------------------------------------|
| `CleansterClient.Sandbox(key)`    | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public`               | Development and integration testing — no real charges or real cleaners |
| `CleansterClient.Production(key)` | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public`                       | Live traffic                                     |

**Sandbox coupon codes** (for testing price calculations): `100POFF`, `50POFF`, `20POFF`, `200OFF`, `100OFF`.

To point at a custom or proxied endpoint:

```csharp
var cfg    = new CleansterConfig(apiKey, "https://my-proxy.example.com/public", timeout: TimeSpan.FromSeconds(60));
var client = new CleansterClient(cfg);
```

---

## API Reference

All methods are `async` and return `Task<ApiResponse<T>>`.
`ApiResponse<T>` is a sealed record: `(int Status, string Message, T Data)`.

### Bookings

```csharp
CleansterClient client = ...;
```

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `GetBookingsAsync(pageNo?, status?)` | GET | `/v1/bookings` | `List<Booking>` |
| `CreateBookingAsync(date, time, propertyId, roomCount, bathroomCount, planId, hours, extraSupplies, paymentMethodId, couponCode?, extras?)` | POST | `/v1/bookings/create` | `Booking` |
| `GetBookingDetailsAsync(bookingId)` | GET | `/v1/bookings/{id}` | `Booking` |
| `CancelBookingAsync(bookingId, reason?)` | POST | `/v1/bookings/{id}/cancel` | `JsonElement` |
| `RescheduleBookingAsync(bookingId, date, time)` | POST | `/v1/bookings/{id}/reschedule` | `JsonElement` |
| `AssignCleanerAsync(bookingId, cleanerId)` | POST | `/v1/bookings/{id}/cleaner` | `JsonElement` |
| `RemoveAssignedCleanerAsync(bookingId)` | DELETE | `/v1/bookings/{id}/cleaner` | `JsonElement` |
| `AdjustHoursAsync(bookingId, hours)` | POST | `/v1/bookings/{id}/hours` | `JsonElement` |
| `PayExpensesAsync(bookingId, paymentMethodId)` | POST | `/v1/bookings/{id}/expenses` | `JsonElement` |
| `GetBookingInspectionAsync(bookingId)` | GET | `/v1/bookings/{id}/inspection` | `JsonElement` |
| `GetBookingInspectionDetailsAsync(bookingId)` | GET | `/v1/bookings/{id}/inspection/details` | `JsonElement` |
| `AssignChecklistToBookingAsync(bookingId, checklistId)` | PUT | `/v1/bookings/{id}/checklist/{checklistId}` | `JsonElement` |
| `SubmitFeedbackAsync(bookingId, rating, comment?)` | POST | `/v1/bookings/{id}/feedback` | `JsonElement` |
| `AddTipAsync(bookingId, amount, paymentMethodId)` | POST | `/v1/bookings/{id}/tip` | `JsonElement` |
| `GetChatAsync(bookingId)` | GET | `/v1/bookings/{id}/chat` | `JsonElement` |
| `SendMessageAsync(bookingId, message)` | POST | `/v1/bookings/{id}/chat` | `JsonElement` |
| `DeleteMessageAsync(bookingId, messageId)` | DELETE | `/v1/bookings/{id}/chat/{messageId}` | `JsonElement` |

**Examples:**

```csharp
// List open bookings on page 2
var list = await client.Bookings.GetBookingsAsync(pageNo: 2, status: "OPEN");
foreach (var b in list.Data)
    Console.WriteLine($"#{b.Id} — {b.Status} — {b.Date} {b.Time}");

// Create a booking with add-on services
var booking = await client.Bookings.CreateBookingAsync(
    date:            "2025-08-15",
    time:            "09:00",
    propertyId:      1040,
    roomCount:       3,
    bathroomCount:   2,
    planId:          2,
    hours:           4.0,
    extraSupplies:   true,
    paymentMethodId: 193,
    couponCode:      "50POFF",
    extras:          new[] { 1, 3 });

// Cancel with a reason
await client.Bookings.CancelBookingAsync(booking.Data.Id, reason: "Guest checked out early");

// Reschedule
await client.Bookings.RescheduleBookingAsync(booking.Data.Id, date: "2025-09-01", time: "11:00");

// Submit 5-star feedback
await client.Bookings.SubmitFeedbackAsync(booking.Data.Id, rating: 5, comment: "Spotless!");

// Tip the cleaner $20
await client.Bookings.AddTipAsync(booking.Data.Id, amount: 20.0, paymentMethodId: 193);

// Chat
await client.Bookings.SendMessageAsync(booking.Data.Id, "Please focus on the kitchen.");
var chat = await client.Bookings.GetChatAsync(booking.Data.Id);
```

---

### Users

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `CreateUserAsync(email, firstName, lastName, phone?)` | POST | `/v1/user/account` | `User` |
| `FetchAccessTokenAsync(userId)` | GET | `/v1/user/access-token/{id}` | `User` |
| `VerifyJwtAsync(token)` | POST | `/v1/user/verify-jwt` | `JsonElement` |

**Examples:**

```csharp
// Register a user
var user = await client.Users.CreateUserAsync(
    email:     "john@example.com",
    firstName: "John",
    lastName:  "Doe",
    phone:     "+15559876543");  // omit phone — it's optional

// Fetch and store the bearer token
var tokenResp = await client.Users.FetchAccessTokenAsync(user.Data.Id);
string jwt = tokenResp.Data.Token!;
client.SetAccessToken(jwt);

// Verify a JWT before trusting it
var verifyResult = await client.Users.VerifyJwtAsync(jwt);
```

---

### Properties

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `ListPropertiesAsync(serviceId?)` | GET | `/v1/properties` | `List<Property>` |
| `AddPropertyAsync(name, address, city, country, roomCount, bathroomCount, serviceId)` | POST | `/v1/properties` | `Property` |
| `GetPropertyAsync(propertyId)` | GET | `/v1/properties/{id}` | `Property` |
| `UpdatePropertyAsync(propertyId, name, address, city, country, roomCount, bathroomCount, serviceId)` | PUT | `/v1/properties/{id}` | `Property` |
| `UpdateAdditionalInformationAsync(propertyId, data)` | PUT | `/v1/properties/{id}/additional-information` | `JsonElement` |
| `EnableOrDisablePropertyAsync(propertyId, enabled)` | POST | `/v1/properties/{id}/enable-disable` | `JsonElement` |
| `DeletePropertyAsync(propertyId)` | DELETE | `/v1/properties/{id}` | `JsonElement` |
| `GetPropertyCleanersAsync(propertyId)` | GET | `/v1/properties/{id}/cleaners` | `JsonElement` |
| `AssignCleanerToPropertyAsync(propertyId, cleanerId)` | POST | `/v1/properties/{id}/cleaners` | `JsonElement` |
| `UnassignCleanerFromPropertyAsync(propertyId, cleanerId)` | DELETE | `/v1/properties/{id}/cleaners/{cleanerId}` | `JsonElement` |
| `AddICalLinkAsync(propertyId, icalLink)` | PUT | `/v1/properties/{id}/ical` | `JsonElement` |
| `GetICalLinkAsync(propertyId)` | GET | `/v1/properties/{id}/ical` | `JsonElement` |
| `RemoveICalLinkAsync(propertyId, icalLink)` | DELETE | `/v1/properties/{id}/ical` | `JsonElement` |
| `AssignChecklistToPropertyAsync(propertyId, checklistId, updateUpcomingBookings?)` | PUT | `/v1/properties/{id}/checklist/{checklistId}` | `JsonElement` |

**Examples:**

```csharp
// Add a property
var prop = await client.Properties.AddPropertyAsync(
    name:          "Penthouse Suite",
    address:       "1 Luxury Ave",
    city:          "New York",
    country:       "USA",
    roomCount:     4,
    bathroomCount: 3,
    serviceId:     1);

// Update additional freeform information
await client.Properties.UpdateAdditionalInformationAsync(
    prop.Data.Id,
    new { parkingCode = "P42", wifiPassword = "guest123" });

// Disable a property (temporarily remove from booking flow)
await client.Properties.EnableOrDisablePropertyAsync(prop.Data.Id, enabled: false);

// Set a default cleaner pool
await client.Properties.AssignCleanerToPropertyAsync(prop.Data.Id, cleanerId: 7);

// Sync availability via iCal (e.g., Airbnb calendar)
await client.Properties.AddICalLinkAsync(prop.Data.Id, "https://www.airbnb.com/calendar/ical/xxx.ics");

// Set the default checklist and push it to all upcoming bookings at this property
await client.Properties.AssignChecklistToPropertyAsync(
    prop.Data.Id, checklistId: 105, updateUpcomingBookings: true);
```

---

### Checklists

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `ListChecklistsAsync()` | GET | `/v1/checklist` | `List<Checklist>` |
| `GetChecklistAsync(checklistId)` | GET | `/v1/checklist/{id}` | `Checklist` |
| `CreateChecklistAsync(name, items)` | POST | `/v1/checklist` | `Checklist` |
| `UpdateChecklistAsync(checklistId, name, items)` | PUT | `/v1/checklist/{id}` | `Checklist` |
| `DeleteChecklistAsync(checklistId)` | DELETE | `/v1/checklist/{id}` | `JsonElement` |
| `UploadChecklistImageAsync(imageBytes, mimeType)` | POST | `/v1/checklist/upload-image` | `JsonElement` |

**Examples:**

```csharp
// Create a checklist
var cl = await client.Checklists.CreateChecklistAsync(
    name:  "Deep Clean",
    items: ["Vacuum all rooms", "Mop kitchen floor", "Scrub bathroom tiles", "Wipe all surfaces"]);

// Retrieve it with typed ChecklistItem objects
var fetched = await client.Checklists.GetChecklistAsync(cl.Data.Id);
foreach (var item in fetched.Data.Items)
    Console.WriteLine($"[{(item.IsCompleted ? "X" : " ")}] {item.Description}");

// Update the checklist
await client.Checklists.UpdateChecklistAsync(
    cl.Data.Id,
    name:  "Standard Clean",
    items: ["Vacuum", "Mop", "Wipe surfaces"]);
```

---

### Other (Reference Data)

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `GetServicesAsync()` | GET | `/v1/services` | `JsonElement` |
| `GetPlansAsync(propertyId)` | GET | `/v1/plans` | `JsonElement` |
| `GetRecommendedHoursAsync(propertyId, bathroomCount, roomCount)` | GET | `/v1/recommended-hours` | `JsonElement` |
| `CalculateCostAsync(propertyId, planId, hours, couponCode?)` | POST | `/v1/cost-estimate` | `JsonElement` |
| `GetCleaningExtrasAsync(serviceId)` | GET | `/v1/cleaning-extras/{serviceId}` | `JsonElement` |
| `GetAvailableCleanersAsync(propertyId, date, time)` | POST | `/v1/available-cleaners` | `JsonElement` |
| `GetCouponsAsync()` | GET | `/v1/coupons` | `JsonElement` |

**Examples:**

```csharp
// Look up available plans for a property
var plans = await client.Other.GetPlansAsync(propertyId: 1040);

// Get the system-recommended hours for a 3-bed/2-bath property
var hours = await client.Other.GetRecommendedHoursAsync(
    propertyId: 1040, bathroomCount: 2, roomCount: 3);

// Preview the cost with a coupon
var estimate = await client.Other.CalculateCostAsync(
    propertyId: 1040, planId: 2, hours: 3.0, couponCode: "100OFF");

// Find cleaners available on a specific date
var cleaners = await client.Other.GetAvailableCleanersAsync(
    propertyId: 1040, date: "2025-09-01", time: "10:00");

// List valid coupon codes
var coupons = await client.Other.GetCouponsAsync();
```

---

### Blacklist

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `ListBlacklistedCleanersAsync()` | GET | `/v1/blacklist/cleaner` | `JsonElement` |
| `AddToBlacklistAsync(cleanerId, reason?)` | POST | `/v1/blacklist/cleaner` | `JsonElement` |
| `RemoveFromBlacklistAsync(cleanerId)` | DELETE | `/v1/blacklist/cleaner` | `JsonElement` |

**Examples:**

```csharp
// Prevent a cleaner from auto-assignment (with optional reason)
await client.Blacklist.AddToBlacklistAsync(cleanerId: 7, reason: "Repeated lateness");

// View who is currently blocked
var blocked = await client.Blacklist.ListBlacklistedCleanersAsync();

// Lift the block
await client.Blacklist.RemoveFromBlacklistAsync(cleanerId: 7);
```

---

### Payment Methods

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `GetSetupIntentDetailsAsync()` | GET | `/v1/payment-methods/setup-intent-details` | `JsonElement` |
| `GetPaypalClientTokenAsync()` | GET | `/v1/payment-methods/paypal-client-token` | `JsonElement` |
| `AddPaymentMethodAsync(paymentMethodId)` | POST | `/v1/payment-methods` | `JsonElement` |
| `GetPaymentMethodsAsync()` | GET | `/v1/payment-methods` | `List<PaymentMethod>` |
| `DeletePaymentMethodAsync(paymentMethodId)` | DELETE | `/v1/payment-methods/{id}` | `JsonElement` |
| `SetDefaultPaymentMethodAsync(paymentMethodId)` | PUT | `/v1/payment-methods/{id}/default` | `JsonElement` |

**Stripe card flow:**

```csharp
// 1. Fetch setup intent — return clientSecret to your frontend
var intent = await client.PaymentMethods.GetSetupIntentDetailsAsync();
// 2. Front-end: use Stripe.js to collect the card and confirm the SetupIntent
// 3. Back-end: register the resulting payment method ID
await client.PaymentMethods.AddPaymentMethodAsync("pm_xxx_from_stripe_js");

// List all saved methods
var methods = await client.PaymentMethods.GetPaymentMethodsAsync();
foreach (var pm in methods.Data)
    Console.WriteLine($"#{pm.Id} {pm.Brand} *{pm.LastFour} — default: {pm.IsDefault}");

// Change the default
await client.PaymentMethods.SetDefaultPaymentMethodAsync(methods.Data[1].Id);

// Remove a card
await client.PaymentMethods.DeletePaymentMethodAsync(methods.Data[0].Id);
```

**PayPal flow:**

```csharp
// 1. Fetch client token — pass to @paypal/react-paypal-js on the frontend
var pp = await client.PaymentMethods.GetPaypalClientTokenAsync();
// 2. User completes PayPal checkout; your frontend receives the paymentMethodId
// 3. Register it server-side
await client.PaymentMethods.AddPaymentMethodAsync("paypal-method-id");
```

---

### Webhooks

| Method | HTTP | Endpoint | Returns |
|--------|------|----------|---------|
| `ListWebhooksAsync()` | GET | `/v1/webhooks` | `JsonElement` |
| `CreateWebhookAsync(url, eventType)` | POST | `/v1/webhooks` | `JsonElement` |
| `UpdateWebhookAsync(webhookId, url, eventType)` | PUT | `/v1/webhooks/{id}` | `JsonElement` |
| `DeleteWebhookAsync(webhookId)` | DELETE | `/v1/webhooks/{id}` | `JsonElement` |

**Examples:**

```csharp
// Register a webhook endpoint
var hook = await client.Webhooks.CreateWebhookAsync(
    url:       "https://app.example.com/hooks/cleanster",
    eventType: "booking.status_changed");

// Update the URL after a domain migration
await client.Webhooks.UpdateWebhookAsync(
    webhookId: 50,
    url:       "https://app-v2.example.com/hooks/cleanster",
    eventType: "booking.status_changed");

// View all configured hooks
var hooks = await client.Webhooks.ListWebhooksAsync();

// Remove a webhook
await client.Webhooks.DeleteWebhookAsync(webhookId: 50);
```

---

## Models

### `ApiResponse<T>`

```csharp
public sealed record ApiResponse<T>(int Status, string Message, T Data);
```

| Property  | Type     | Description                                      |
|-----------|----------|--------------------------------------------------|
| `Status`  | `int`    | HTTP-style status code (always 200 on success)   |
| `Message` | `string` | Human-readable status string (e.g., `"OK"`)      |
| `Data`    | `T`      | Typed payload — model, list, or `JsonElement`    |

---

### `Booking`

| Property          | Type     | JSON Key            | Notes                                          |
|-------------------|----------|---------------------|------------------------------------------------|
| `Id`              | `int`    | `id`                | Booking identifier                             |
| `Status`          | `string` | `status`            | `OPEN` / `CLEANER_ASSIGNED` / `COMPLETED` / `CANCELLED` / `REMOVED` |
| `Date`            | `string` | `date`              | `YYYY-MM-DD`                                   |
| `Time`            | `string` | `time`              | `HH:mm` (24-hour)                              |
| `Hours`           | `double` | `hours`             | Cleaning duration                              |
| `Cost`            | `double` | `cost`              | Total amount charged                           |
| `PropertyId`      | `int`    | `propertyId`        |                                                |
| `CleanerId`       | `int?`   | `cleanerId`         | `null` when not yet assigned                   |
| `PlanId`          | `int`    | `planId`            |                                                |
| `RoomCount`       | `int`    | `roomCount`         |                                                |
| `BathroomCount`   | `int`    | `bathroomCount`     |                                                |
| `ExtraSupplies`   | `bool`   | `extraSupplies`     |                                                |
| `PaymentMethodId` | `int`    | `paymentMethodId`   |                                                |

---

### `User`

| Property    | Type      | JSON Key      | Notes                                             |
|-------------|-----------|---------------|---------------------------------------------------|
| `Id`        | `int`     | `id`          |                                                   |
| `Email`     | `string`  | `email`       |                                                   |
| `FirstName` | `string`  | `firstName`   |                                                   |
| `LastName`  | `string`  | `lastName`    |                                                   |
| `Phone`     | `string?` | `phone`       | `null` if not provided at registration            |
| `Token`     | `string?` | `token`       | JWT bearer — only present after `FetchAccessTokenAsync` |

---

### `Property`

| Property       | Type     | JSON Key        | Notes                                   |
|----------------|----------|-----------------|-----------------------------------------|
| `Id`           | `int`    | `id`            |                                         |
| `Name`         | `string` | `name`          |                                         |
| `Address`      | `string` | `address`       |                                         |
| `City`         | `string` | `city`          |                                         |
| `Country`      | `string` | `country`       |                                         |
| `RoomCount`    | `int`    | `roomCount`     |                                         |
| `BathroomCount`| `int`    | `bathroomCount` |                                         |
| `ServiceId`    | `int`    | `serviceId`     |                                         |
| `IsEnabled`    | `bool?`  | `isEnabled`     | `null` when not returned by this endpoint |

---

### `Checklist`

| Property | Type                 | JSON Key | Notes |
|----------|----------------------|----------|-------|
| `Id`     | `int`                | `id`     |       |
| `Name`   | `string`             | `name`   |       |
| `Items`  | `List<ChecklistItem>`| `items`  | Fully typed — never `JsonElement[]` |

### `ChecklistItem`

| Property      | Type      | JSON Key      | Notes                                              |
|---------------|-----------|---------------|----------------------------------------------------|
| `Id`          | `int`     | `id`          |                                                    |
| `Description` | `string`  | `description` |                                                    |
| `IsCompleted` | `bool`    | `isCompleted` |                                                    |
| `ImageUrl`    | `string?` | `imageUrl`    | Proof photo URL — `null` until the cleaner uploads |

---

### `PaymentMethod`

| Property    | Type      | JSON Key    | Notes                                              |
|-------------|-----------|-------------|----------------------------------------------------|
| `Id`        | `int`     | `id`        |                                                    |
| `Type`      | `string`  | `type`      | `"card"` or `"paypal"`                             |
| `LastFour`  | `string?` | `lastFour`  | Last 4 digits — `null` for non-card methods        |
| `Brand`     | `string?` | `brand`     | `"visa"`, `"mastercard"`, etc. — `null` for PayPal |
| `IsDefault` | `bool`    | `isDefault` |                                                    |

---

## Error Handling

All SDK exceptions extend `CleansterException`:

```
CleansterException          (base — network errors, JSON parse failures, timeouts)
├── AuthException           (HTTP 401 — invalid or missing access key or bearer token)
└── ApiException            (HTTP 4xx/5xx other than 401)
```

| Exception             | When thrown                          | Extra properties                    |
|-----------------------|--------------------------------------|-------------------------------------|
| `CleansterException`  | Network failure, timeout, JSON error | —                                   |
| `AuthException`       | HTTP 401                             | `StatusCode` (always 401), `ResponseBody` |
| `ApiException`        | HTTP 4xx or 5xx (not 401)            | `StatusCode`, `ResponseBody`        |

**Recommended pattern:**

```csharp
try
{
    var bookings = await client.Bookings.GetBookingsAsync();
    // ...
}
catch (AuthException ex)
{
    // Refresh the token and retry, or redirect the user to re-authenticate
    Console.Error.WriteLine($"[401] {ex.ResponseBody}");
}
catch (ApiException ex)
{
    Console.Error.WriteLine(ex.StatusCode switch
    {
        404 => $"Resource not found — {ex.ResponseBody}",
        422 => $"Validation error — {ex.ResponseBody}",
        500 => $"Server error — please retry",
        _   => $"API error {ex.StatusCode} — {ex.ResponseBody}",
    });
}
catch (CleansterException ex)
{
    // Transient network error — safe to retry with backoff
    Console.Error.WriteLine($"Network error: {ex.Message}");
}
```

---

## Cancellation and Timeouts

Every API method accepts an optional `CancellationToken`:

```csharp
// Global timeout via CleansterConfig
var cfg    = new CleansterConfig(apiKey, CleansterConfig.SandboxBaseUrl, TimeSpan.FromSeconds(15));
var client = new CleansterClient(cfg);

// Per-request deadline with CancellationTokenSource
using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
var bookings  = await client.Bookings.GetBookingsAsync(ct: cts.Token);

// ASP.NET Core — pass the request's cancellation token
public async Task<IActionResult> GetBookings(CancellationToken ct)
{
    var bookings = await _cleansterClient.Bookings.GetBookingsAsync(ct: ct);
    return Ok(bookings.Data);
}
```

---

## Testing / Dependency Injection

Inject `ICleansterHttpClient` to mock the transport layer without touching the network. This is the intended unit-testing pattern.

```csharp
using Cleanster;
using Cleanster.Api;
using Moq;
using Xunit;
using System.Text.Json;

public class MyServiceTests
{
    private static JsonElement MakeResponse(string dataJson)
    {
        var json = $$"""{"status":200,"message":"OK","data":{{dataJson}}}""";
        using var doc = JsonDocument.Parse(json);
        return doc.RootElement.Clone();
    }

    [Fact]
    public async Task GetBookings_ReturnsTypedList()
    {
        // Arrange
        var mock = new Mock<ICleansterHttpClient>();
        mock.Setup(h => h.GetAsync("/v1/bookings", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse(@"[{""id"":1,""status"":""OPEN"",""date"":""2025-06-15"",""time"":""10:00"",""hours"":3.0,""cost"":150.0,""propertyId"":1004,""cleanerId"":null,""planId"":2,""roomCount"":2,""bathroomCount"":1,""extraSupplies"":false,""paymentMethodId"":10}]"));

        var api = new BookingsApi(mock.Object);

        // Act
        var resp = await api.GetBookingsAsync();

        // Assert
        Assert.Equal(200, resp.Status);
        Assert.Single(resp.Data);
        Assert.Equal("OPEN", resp.Data[0].Status);
        Assert.Null(resp.Data[0].CleanerId);

        mock.VerifyAll();
    }

    [Fact]
    public async Task AuthException_IsThrownOn401()
    {
        var mock = new Mock<ICleansterHttpClient>();
        mock.Setup(h => h.GetAsync(It.IsAny<string>(), It.IsAny<IDictionary<string, string>?>(), It.IsAny<CancellationToken>()))
            .ThrowsAsync(new Cleanster.Exceptions.AuthException(401, "Unauthorized"));

        var api = new BookingsApi(mock.Object);

        await Assert.ThrowsAsync<Cleanster.Exceptions.AuthException>(
            () => api.GetBookingsAsync());
    }
}
```

**ASP.NET Core DI registration:**

```csharp
// Program.cs
builder.Services.AddSingleton<CleansterConfig>(_ =>
    CleansterConfig.Production(builder.Configuration["Cleanster:ApiKey"]!));

builder.Services.AddSingleton<ICleansterHttpClient, CleansterHttpClient>();
builder.Services.AddSingleton<CleansterClient>();
```

---

## Project Structure

```
csharp-sdk/
├── src/
│   └── Cleanster/
│       ├── Cleanster.csproj          # Library project — net8.0, zero external deps
│       ├── CleansterClient.cs        # Main entry point with static factory methods
│       ├── CleansterConfig.cs        # Config with Sandbox/Production URL constants
│       ├── CleansterHttpClient.cs    # HttpClient transport (internal)
│       ├── ICleansterHttpClient.cs   # Transport interface — implement to mock or extend
│       ├── JsonHelper.cs             # ParseSingle / ParseList / ParseRaw (internal)
│       ├── Exceptions/
│       │   ├── CleansterException.cs # Base exception
│       │   ├── AuthException.cs      # HTTP 401
│       │   └── ApiException.cs       # HTTP 4xx / 5xx
│       ├── Models/
│       │   ├── ApiResponse.cs        # ApiResponse<T> sealed record
│       │   ├── Booking.cs
│       │   ├── User.cs
│       │   ├── Property.cs
│       │   ├── Checklist.cs
│       │   ├── ChecklistItem.cs
│       │   └── PaymentMethod.cs
│       └── Api/
│           ├── BookingsApi.cs        # 17 methods
│           ├── UsersApi.cs           # 3 methods
│           ├── PropertiesApi.cs      # 14 methods
│           ├── ChecklistsApi.cs      # 5 methods
│           ├── OtherApi.cs           # 7 methods
│           ├── BlacklistApi.cs       # 3 methods
│           ├── PaymentMethodsApi.cs  # 6 methods
│           └── WebhooksApi.cs        # 4 methods
├── tests/
│   └── Cleanster.Tests/
│       ├── Cleanster.Tests.csproj    # xUnit + Moq
│       └── CleansterTests.cs         # 107 tests — all passing
├── Cleanster.sln
├── LICENSE                           # MIT
├── CHANGELOG.md
└── .gitignore
```

---

## Running the Tests

```bash
# Clone and restore
git clone https://github.com/cleanster/cleanster-csharp-sdk.git
cd cleanster-csharp-sdk
dotnet restore

# Run all 107 tests
dotnet test

# Verbose output
dotnet test -v normal

# Coverage report (requires coverlet)
dotnet test --collect:"XPlat Code Coverage"
dotnet tool install -g dotnet-reportgenerator-globaltool
reportgenerator -reports:**/coverage.cobertura.xml -targetdir:coverage/
```

**Test coverage breakdown:**

| Area                 | Tests |
|----------------------|-------|
| `CleansterConfig`    | 8     |
| `CleansterClient`    | 12    |
| `BookingsApi`        | 22    |
| `UsersApi`           | 5     |
| `PropertiesApi`      | 16    |
| `ChecklistsApi`      | 5     |
| `OtherApi`           | 7     |
| `BlacklistApi`       | 4     |
| `PaymentMethodsApi`  | 6     |
| `WebhooksApi`        | 4     |
| Exceptions           | 8     |
| Models               | 7     |
| Cancellation/errors  | 3     |
| **Total**            | **107** |

Every test uses `Mock<ICleansterHttpClient>` and `MockBehavior.Strict` — zero real HTTP calls, deterministic results, and precise verification that each method sends requests to the correct path with the correct HTTP verb.

---

## Design Decisions

**Why `System.Net.Http` and `System.Text.Json` only?**
Zero external runtime dependencies means no `Newtonsoft.Json`, no `Polly`, no NuGet conflicts. The SDK works in any .NET 8+ project without version negotiation.

**Why sealed records for models?**
`sealed record` types are immutable by default (`init`-only properties), structurally equatable (great for tests), and serialize cleanly with `System.Text.Json`. They model API responses — which should not be mutated — perfectly.

**Why an `ICleansterHttpClient` interface?**
Decoupling the transport enables any caller to inject a mock (as every test in this SDK does), a Polly-wrapped retry client, a logging decorator, or any custom `HttpClient` configuration without touching the SDK source.

**Why `internal` constructors on API service classes?**
Users should always go through `CleansterClient`, which keeps the API surface clean and ensures the shared token and config are applied consistently. Test assemblies get access via `[InternalsVisibleTo("Cleanster.Tests")]`.

**Why `CancellationToken` on every method?**
ASP.NET Core passes a per-request `CancellationToken` that fires when the client disconnects. Accepting it on every method means the SDK can participate in cooperative cancellation without any extra plumbing.

---

## License

MIT — see [LICENSE](LICENSE).
