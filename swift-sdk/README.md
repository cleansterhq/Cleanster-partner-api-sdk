# Cleanster Swift SDK

<p align="center">
  <strong>Official Swift client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Swift-5.9%2B-orange?logo=swift" alt="Swift 5.9+">
  <img src="https://img.shields.io/badge/iOS-16%2B-blue?logo=apple" alt="iOS 16+">
  <img src="https://img.shields.io/badge/macOS-13%2B-blue?logo=apple" alt="macOS 13+">
  <img src="https://img.shields.io/badge/SPM-compatible-brightgreen" alt="SPM">
  <img src="https://img.shields.io/badge/tests-170%20passing-brightgreen" alt="170 passing">
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
- [Test Coupon Codes](#test-coupon-codes)
- [Chat Window Rules](#chat-window-rules)
- [Webhook Events](#webhook-events)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## Overview

The Cleanster Swift SDK provides a modern async/await interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It uses Swift's native `URLSession` and `Codable` — zero third-party dependencies.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal calendar sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create reusable task lists and assign to bookings
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — block specific cleaners from your properties

---

## Requirements

| Tool | Version |
|---|---|
| Swift | 5.9+ |
| Xcode | 15+ |
| iOS | 16+ |
| macOS | 13+ |

---

## Installation

### Swift Package Manager (Xcode)

1. In Xcode: **File → Add Package Dependencies**
2. Enter the repository URL:
   ```
   https://github.com/cleansterhq/Cleanster-partner-api-sdk
   ```
3. Set the path to the `swift-sdk` folder.
4. Add `Cleanster` to your target.

### Swift Package Manager (Package.swift)

```swift
// Package.swift
dependencies: [
    .package(url: "https://github.com/cleansterhq/Cleanster-partner-api-sdk", from: "1.0.0"),
],
targets: [
    .target(name: "MyApp", dependencies: [
        .product(name: "Cleanster", package: "Cleanster-partner-api-sdk")
    ]),
]
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — passed at client construction |
| `token` | A per-user JWT — long-lived, obtained via `fetchAccessToken(_:)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```swift
import Cleanster

let client = CleansterClient.sandbox(accessKey: "your-access-key")

let user = try await client.users.createUser(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith",
    phone: "+14155551234"   // optional
)
let userId = user.data?.id ?? 0
```

**Step 3 — Fetch the user's JWT** (long-lived; store it):

```swift
let tokenResp = try await client.users.fetchAccessToken(userId)
let jwt = tokenResp.data?.token ?? ""
```

**Step 4 — Set the token on the client**:

```swift
client.setToken(jwt)
// All subsequent calls now include this token
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401 on a user-scoped endpoint.

---

## Quick Start

```swift
import Cleanster

// 1. Build the client
let client = CleansterClient.sandbox(accessKey: "your-access-key")

// 2. Authenticate
let tokenResp = try await client.users.fetchAccessToken(42)
client.setToken(tokenResp.data?.token ?? "")

// 3. List available services
let services = try await client.other.getServices()
print("Services:", services.data as Any)

// 4. Estimate cost before booking
let estimate = try await client.other.getCostEstimate(
    CostEstimateRequest(propertyId: 1004, planId: 2, hours: 3.0)
)
print("Total:", estimate.data?.total ?? 0)

// 5. Create a booking
let booking = try await client.bookings.createBooking(
    CreateBookingRequest(
        date: "2025-09-01",
        time: "10:00",
        propertyId: 1004,
        planId: 2,
        hours: 3.0,
        roomCount: 2,
        bathroomCount: 1,
        extraSupplies: false,
        paymentMethodId: 55,
        couponCode: "20POFF"   // optional
    )
)
print("Created booking:", booking.data?.id ?? 0)

// 6. List open bookings
let open = try await client.bookings.getBookings(status: "OPEN")
print("Open bookings:", open.data as Any)
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (development) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```swift
// Sandbox
let client = CleansterClient.sandbox(accessKey: "your-access-key")

// Production
let client = CleansterClient.production(accessKey: "your-access-key")
```

Always develop and test against **sandbox** first.

---

## Booking Flow

```
createBooking()         →   OPEN
                               │
              assignCleaner()
                               │
                               ▼
                     CLEANER_ASSIGNED
                               │
                   Cleaner starts the job
                               │
              ┌────────────────┴────────────────┐
              ▼                                 ▼
         COMPLETED                         CANCELLED
              │
    submitFeedback()
    addTip()
    payExpenses()
```

Booking status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All methods are `async throws` and return `ApiResponse<T>`.
Access `.data` for the payload, `.status` for the HTTP code, and `.message` for the description.

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

Retrieve a paginated list of bookings, optionally filtered by status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | Int | No | Page number (1-based) |
| `status` | String | No | `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |

```swift
let all       = try await client.bookings.getBookings()
let open      = try await client.bookings.getBookings(status: "OPEN")
let page2     = try await client.bookings.getBookings(pageNo: 2, status: "COMPLETED")
```

---

#### Get Booking Details
**`GET /v1/bookings/{bookingId}`**

Retrieve full details for a single booking.

```swift
let detail = try await client.bookings.getBookingDetails(16926)
print(detail.data?.id, detail.data?.status)
```

---

#### Create Booking
**`POST /v1/bookings/create`**

Schedule a new cleaning appointment.

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | Date in `YYYY-MM-DD` format |
| `time` | String | Yes | Start time in `HH:mm` (24-hour) |
| `propertyId` | Int | Yes | Property to be cleaned |
| `planId` | Int | Yes | Cleaning plan ID |
| `hours` | Double | Yes | Scheduled duration in hours |
| `roomCount` | Int | Yes | Number of rooms |
| `bathroomCount` | Int | Yes | Number of bathrooms |
| `extraSupplies` | Bool | Yes | Whether the cleaner brings supplies |
| `paymentMethodId` | Int | Yes | Payment method ID |
| `couponCode` | String | No | Discount coupon code |
| `extras` | [Int] | No | List of extra service IDs |

```swift
let booking = try await client.bookings.createBooking(
    CreateBookingRequest(
        date: "2025-09-15",
        time: "09:00",
        propertyId: 1004,
        planId: 2,
        hours: 3.0,
        roomCount: 2,
        bathroomCount: 1,
        extraSupplies: false,
        paymentMethodId: 55,
        couponCode: "20POFF",   // optional
        extras: [3, 7]          // optional
    )
)
print("Booking ID:", booking.data?.id ?? 0)
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | String | No | Cancellation reason |

```swift
try await client.bookings.cancelBooking(16926, reason: "Customer request")
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | New date in `YYYY-MM-DD` |
| `time` | String | Yes | New start time in `HH:mm` |

```swift
try await client.bookings.rescheduleBooking(16926, date: "2025-09-20", time: "14:00")
```

---

#### Assign Cleaner
**`POST /v1/bookings/{bookingId}/cleaner`**

Manually assign a specific cleaner. The cleaner must be in the property's preferred pool.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```swift
try await client.bookings.assignCleaner(16926, cleanerId: 789)
```

---

#### Remove Assigned Cleaner
**`DELETE /v1/bookings/{bookingId}/cleaner`**

Remove the currently assigned cleaner. Booking returns to `OPEN` status.

```swift
try await client.bookings.removeAssignedCleaner(16926)
```

---

#### Adjust Hours
**`POST /v1/bookings/{bookingId}/hours`**

Change the duration of a booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `hours` | Double | Yes | New duration in hours |

```swift
try await client.bookings.adjustHours(16926, hours: 4.5)
```

---

#### Pay Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

Pay outstanding balance-on-completion charges. Must be called within 72 hours of booking completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | Int | Yes | Payment method to charge |

```swift
try await client.bookings.payExpenses(16926, paymentMethodId: 55)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

Retrieve the post-booking inspection summary.

```swift
let report = try await client.bookings.getBookingInspection(16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

Retrieve detailed inspection data including photos and notes.

```swift
let details = try await client.bookings.getBookingInspectionDetails(16926)
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this specific booking only.

```swift
try await client.bookings.assignChecklistToBooking(16926, checklistId: 77)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

Submit a star rating and optional comment after a completed booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | Int | Yes | 1 (poor) to 5 (excellent) |
| `comment` | String | No | Written feedback |

```swift
try await client.bookings.submitFeedback(16926, rating: 5, comment: "Spotless work!")
```

---

#### Add Tip
**`POST /v1/bookings/{bookingId}/tip`**

Add a gratuity for the cleaner. Must be called within 72 hours of booking completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | Double | Yes | Tip amount in account currency |
| `paymentMethodId` | Int | Yes | Payment method to charge |

```swift
try await client.bookings.addTip(16926, amount: 15.00, paymentMethodId: 55)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

Retrieve all chat messages for a booking thread. See [Chat Window Rules](#chat-window-rules).

```swift
let chat = try await client.bookings.getChat(16926)
```

**Response fields per message:**

| Field | Type | Description |
|---|---|---|
| `messageId` | String | Unique message identifier |
| `senderId` | String | Sender reference (e.g. `C6`, `P3`) |
| `content` | String | Text content |
| `timestamp` | String | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `messageType` | String | `text` or `media` |
| `attachments` | Array | Media files |
| `attachments[].type` | String | `image`, `video`, or `sound` |
| `attachments[].url` | String | Direct media URL |
| `attachments[].thumbUrl` | String | Thumbnail URL (nullable) |
| `isRead` | Bool | Whether the message has been read |
| `senderType` | String | `client`, `cleaner`, `support`, or `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

| Field | Type | Required | Description |
|---|---|---|---|
| `message` | String | Yes | Text content to send |

```swift
try await client.bookings.sendMessage(16926, message: "Please bring extra supplies.")
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```swift
try await client.bookings.deleteMessage(16926, messageId: "-OLPrlE06uD8tQ8ebJZw")
```

---

### Users

#### Create User
**`POST /v1/user/account`**

Create a new user account. Each end-user in your system needs one account.

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | String | Yes | Email address |
| `firstName` | String | Yes | First name |
| `lastName` | String | Yes | Last name |
| `phone` | String | No | Phone in E.164 format (e.g. `+14155551234`) |

```swift
let user = try await client.users.createUser(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith",
    phone: "+14155551234"
)
let userId = user.data?.id ?? 0
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Retrieve the long-lived JWT for a user. Only the `access-key` header is required for this call.

```swift
let tokenResp = try await client.users.fetchAccessToken(userId)
let jwt = tokenResp.data?.token ?? ""
client.setToken(jwt)
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

Verify that a token is still valid.

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | String | Yes | JWT to validate |

```swift
let verify = try await client.users.verifyJwt("eyJhbGciOi...")
print(verify.message)   // "OK" if valid
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `serviceId` | Int | No | Filter by service type |

```swift
let all  = try await client.properties.listProperties()
let res  = try await client.properties.listProperties(serviceId: 1)
```

---

#### Add Property
**`POST /v1/properties`**

Create a new cleanable property (location).

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Property display name |
| `address` | String | Yes | Street address |
| `city` | String | Yes | City |
| `country` | String | Yes | Country code (e.g. `US`) |
| `roomCount` | Int | Yes | Number of rooms |
| `bathroomCount` | Int | Yes | Number of bathrooms |
| `serviceId` | Int | Yes | Service area ID |
| `state` | String | No | State or province |
| `zip` | String | No | Postal / ZIP code |
| `timezone` | String | No | IANA timezone (e.g. `America/New_York`) |
| `note` | String | No | Access or cleaning notes |
| `latitude` | Double | No | GPS latitude |
| `longitude` | Double | No | GPS longitude |

```swift
let prop = try await client.properties.addProperty(
    CreatePropertyRequest(
        name: "Downtown Loft",
        address: "123 Main St",
        city: "Atlanta",
        country: "US",
        roomCount: 2,
        bathroomCount: 1,
        serviceId: 1,
        state: "GA",
        zip: "30301",
        timezone: "America/New_York",
        note: "Ring doorbell",
        latitude: 33.749,
        longitude: -84.388
    )
)
let propertyId = prop.data?.id ?? 0
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```swift
let detail = try await client.properties.getProperty(propertyId)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```swift
let updated = try await client.properties.updateProperty(propertyId, request: updatedRequest)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```swift
try await client.properties.deleteProperty(propertyId)
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

Update freeform key/value fields on a property.

```swift
try await client.properties.updateAdditionalInformation(propertyId, fields: [
    "parkingInstructions": "Use visitor lot on 2nd St",
    "accessCode": "1234#",
    "petInfo": "One friendly golden retriever",
    "garbageDay": "Tuesday",
])
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

Disabled properties cannot receive new bookings.

| Field | Type | Required | Description |
|---|---|---|---|
| `enabled` | Bool | Yes | `true` to enable, `false` to disable |

```swift
try await client.properties.enableOrDisableProperty(propertyId, enabled: false)
```

---

#### Get Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

List all cleaners associated with a property.

```swift
let cleaners = try await client.properties.getPropertyCleaners(propertyId)
```

---

#### Add Cleaner to Property
**`POST /v1/properties/{propertyId}/cleaners`**

Add a cleaner to the property's preferred pool.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```swift
try await client.properties.addCleanerToProperty(propertyId, cleanerId: 789)
```

---

#### Remove Cleaner from Property
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```swift
try await client.properties.removeCleanerFromProperty(propertyId, cleanerId: 789)
```

---

#### Set iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

Add an iCal feed for calendar sync (Airbnb, VRBO, Google Calendar, etc.).

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | String | Yes | Full iCal URL |

```swift
try await client.properties.setICalLink(propertyId,
    icalLink: "https://www.airbnb.com/calendar/ical/12345.ics?s=abc")
```

---

#### Get iCal Link
**`GET /v1/properties/{propertyId}/ical`**

```swift
let ical = try await client.properties.getICalLink(propertyId)
```

---

#### Delete iCal Link
**`DELETE /v1/properties/{propertyId}/ical`**

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | String | Yes | The iCal URL to remove |

```swift
try await client.properties.deleteICalLink(propertyId,
    icalLink: "https://www.airbnb.com/calendar/ical/12345.ics?s=abc")
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

Assign a checklist as the default for all future bookings on this property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `checklistId` | Int | Yes | Checklist ID |
| `updateUpcomingBookings` | Bool | No | If `true`, also updates already-scheduled bookings |

```swift
try await client.properties.setDefaultChecklist(propertyId, checklistId: 77)

// Also update all upcoming bookings
try await client.properties.setDefaultChecklist(propertyId, checklistId: 77, updateUpcomingBookings: true)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```swift
let all = try await client.checklists.listChecklists()
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```swift
let checklist = try await client.checklists.getChecklist(77)
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Checklist name |
| `items` | [String] | Yes | Task items |

```swift
let created = try await client.checklists.createChecklist(
    name: "Deep Clean",
    items: [
        "Vacuum all carpets and rugs",
        "Mop all hard floors",
        "Wipe down all surfaces and appliances",
        "Clean interior of oven",
        "Scrub and disinfect all bathrooms",
        "Empty and wipe out all bins",
        "Dust all furniture, shelves, and ceiling fans",
    ]
)
let checklistId = created.data?.id ?? 0
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

Replaces the checklist name and items entirely.

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | New checklist name |
| `items` | [String] | Yes | Replacement task items |

```swift
try await client.checklists.updateChecklist(77,
    name: "Deep Clean v2",
    items: ["New task 1", "New task 2"]
)
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```swift
try await client.checklists.deleteChecklist(77)
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```swift
let imageData = try Data(contentsOf: URL(fileURLWithPath: "bathroom-guide.jpg"))
try await client.checklists.uploadChecklistImage(imageData: imageData, fileName: "bathroom-guide.jpg")
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

Retrieve all available service types.

```swift
let services = try await client.other.getServices()
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

Retrieve all cleaning plans available for a specific property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |

```swift
let plans = try await client.other.getPlans(propertyId: 1004)
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={id}&roomCount={n}&bathroomCount={n}`**

Get the estimated cleaning duration for a property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |
| `roomCount` | Int | Yes | Number of rooms |
| `bathroomCount` | Int | Yes | Number of bathrooms |

```swift
let rec = try await client.other.getRecommendedHours(
    propertyId: 1004,
    roomCount: 3,
    bathroomCount: 2
)
print("Recommended:", rec.data?.hours ?? 0, "hours")
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

Calculate the price before creating a booking. Safe to call without committing.

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |
| `planId` | Int | Yes | Cleaning plan ID |
| `hours` | Double | Yes | Estimated duration |
| `couponCode` | String | No | Discount coupon code |
| `extras` | [Int] | No | Extra service IDs |

```swift
let estimate = try await client.other.getCostEstimate(
    CostEstimateRequest(
        propertyId: 1004,
        planId: 2,
        hours: 3.0,
        couponCode: "20POFF",
        extras: [3]
    )
)
print("Subtotal:", estimate.data?.subtotal ?? 0)
print("Discount:", estimate.data?.discount ?? 0)
print("Total:", estimate.data?.total ?? 0)
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

Retrieve all available add-on services for a given service type.

```swift
let extras = try await client.other.getCleaningExtras(serviceId: 1)
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

Check which cleaners are available for a given property, date, and time slot.

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |
| `date` | String | Yes | Date in `YYYY-MM-DD` format |
| `time` | String | Yes | Start time in `HH:mm` |

```swift
let available = try await client.other.getAvailableCleaners(
    AvailableCleanersRequest(
        propertyId: 1004,
        date: "2025-09-15",
        time: "09:00"
    )
)
```

---

#### Get Coupons
**`GET /v1/coupons`**

Retrieve all available (non-expired) coupon codes for the account.

```swift
let coupons = try await client.other.getCoupons()
```

#### List Cleaners
**`GET /v1/cleaners`**

```swift
let cleaners = try await client.other.listCleaners(status: "active", search: "Jane")
```

#### Get Cleaner
**`GET /v1/cleaners/{cleanerId}`**

```swift
let cleaner = try await client.other.getCleaner(cleanerId: 789)
```

---

### Blacklist

#### List Blacklisted Cleaners
**`GET /v1/blacklist/cleaner`**

```swift
let blocked = try await client.blacklist.listBlacklistedCleaners()
```

---

#### Add to Blacklist
**`POST /v1/blacklist/cleaner`**

Block a cleaner from being assigned to your properties.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |
| `reason` | String | No | Reason for blocking |

```swift
try await client.blacklist.addToBlacklist(cleanerId: 789, reason: "Repeated late arrivals")
```

---

#### Remove from Blacklist
**`DELETE /v1/blacklist/cleaner`**

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```swift
try await client.blacklist.removeFromBlacklist(cleanerId: 789)
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

Retrieve a Stripe Setup Intent `clientSecret`. Pass this to the Stripe iOS SDK to collect and confirm payment method details securely.

```swift
let intent = try await client.paymentMethods.getSetupIntentDetails()
// Pass intent data to Stripe iOS SDK...
// After confirmation, Stripe returns a payment method ID (pm_xxx)
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

Retrieve a PayPal client token for the Braintree SDK.

```swift
let paypal = try await client.paymentMethods.getPayPalClientToken()
// Pass token to Braintree iOS SDK
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

Save a new payment method after collecting it via Stripe or Braintree.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | String | Yes | Stripe `pm_xxx` or Braintree nonce |

```swift
// After Stripe iOS SDK setup flow:
let method = try await client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")
print("Saved method ID:", method.data?.id ?? 0)
```

---

#### Get Payment Methods
**`GET /v1/payment-methods`**

List all saved payment methods for the current user.

```swift
let methods = try await client.paymentMethods.getPaymentMethods()
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{paymentMethodId}/default`**

```swift
try await client.paymentMethods.setDefaultPaymentMethod(55)
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{paymentMethodId}`**

```swift
try await client.paymentMethods.deletePaymentMethod(55)
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```swift
let hooks = try await client.webhooks.listWebhooks()
```

---

#### Create Webhook
**`POST /v1/webhooks`**

Subscribe to a booking lifecycle event.

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | Your HTTPS endpoint URL |
| `event` | String | Yes | Event name (see [Webhook Events](#webhook-events)) |

```swift
let hook = try await client.webhooks.createWebhook(
    url: "https://api.yourapp.com/hooks/cleanster",
    event: "booking.completed"
)
let hookId = hook.data?.id ?? 0
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | New endpoint URL |
| `event` | String | Yes | New event name |

```swift
try await client.webhooks.updateWebhook(
    hookId,
    url: "https://api.yourapp.com/v2/hooks",
    event: "booking.completed"
)
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```swift
try await client.webhooks.deleteWebhook(hookId)
```

---

## Models Reference

### `ApiResponse<T>`

Every method returns `ApiResponse<T>`:

| Property | Type | Description |
|---|---|---|
| `status` | Int | HTTP status code (200, 400, 401, 403, 404, 500) |
| `message` | String | Human-readable status description |
| `data` | T? | Response payload; `nil` on errors |

---

### `Booking`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Booking ID |
| `status` | String | `OPEN`, `CLEANER_ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `date` | String | Booking date (`YYYY-MM-DD`) |
| `time` | String | Start time (`HH:mm`) |
| `hours` | Double | Duration in hours |
| `propertyId` | Int | Property being cleaned |
| `planId` | Int | Cleaning plan ID |
| `cleanerId` | Int? | Assigned cleaner (nil if unassigned) |
| `paymentMethodId` | Int | Payment method used |

---

### `Property`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Property ID |
| `name` | String | Display name |
| `address` | String | Street address |
| `city` | String | City |
| `state` | String? | State or province |
| `zip` | String? | Postal code |
| `country` | String | Country code |
| `roomCount` | Int | Bedroom count |
| `bathroomCount` | Int | Bathroom count |
| `latitude` | Double? | GPS latitude |
| `longitude` | Double? | GPS longitude |
| `timezone` | String? | IANA timezone |
| `enabled` | Bool | Whether the property accepts bookings |

---

### `CreateBookingRequest`

| Field | Type | Required |
|---|---|---|
| `date` | String | Yes |
| `time` | String | Yes |
| `propertyId` | Int | Yes |
| `planId` | Int | Yes |
| `hours` | Double | Yes |
| `roomCount` | Int | Yes |
| `bathroomCount` | Int | Yes |
| `extraSupplies` | Bool | Yes |
| `paymentMethodId` | Int | Yes |
| `couponCode` | String? | No |
| `extras` | [Int]? | No |

---

### `CreatePropertyRequest`

| Field | Type | Required |
|---|---|---|
| `name` | String | Yes |
| `address` | String | Yes |
| `city` | String | Yes |
| `country` | String | Yes |
| `roomCount` | Int | Yes |
| `bathroomCount` | Int | Yes |
| `serviceId` | Int | Yes |
| `state` | String? | No |
| `zip` | String? | No |
| `timezone` | String? | No |
| `note` | String? | No |
| `latitude` | Double? | No |
| `longitude` | Double? | No |

---

### `CostEstimateRequest`

| Field | Type | Required |
|---|---|---|
| `propertyId` | Int | Yes |
| `planId` | Int | Yes |
| `hours` | Double | Yes |
| `couponCode` | String? | No |
| `extras` | [Int]? | No |

---

### `AvailableCleanersRequest`

| Field | Type | Required |
|---|---|---|
| `propertyId` | Int | Yes |
| `date` | String | Yes |
| `time` | String | Yes |

---

## Error Handling

```swift
do {
    let booking = try await client.bookings.getBookingDetails(99999)
    print("Status:", booking.data?.status as Any)
} catch CleansterError.unauthorized {
    print("Invalid access-key or token — refresh your credentials")
} catch CleansterError.apiError(let statusCode, let message) {
    print("API error \(statusCode): \(message)")
} catch CleansterError.networkError(let underlying) {
    print("Network failure:", underlying.localizedDescription)
} catch CleansterError.decodingError(let underlying) {
    print("Failed to decode response:", underlying.localizedDescription)
} catch {
    print("Unexpected error:", error)
}
```

| Error case | HTTP status | Common cause |
|---|---|---|
| `.unauthorized` | 401 | Missing or invalid `access-key` or `token` |
| `.apiError(statusCode, message)` | 400, 403, 404, 500 | Business rule violation, not found, server error |
| `.networkError(Error)` | — | No internet, DNS failure, timeout |
| `.decodingError(Error)` | — | Unexpected response format |

---

## Test Coupon Codes (Sandbox)

| Code | Discount |
|---|---|
| `100POFF` | 100% off |
| `50POFF` | 50% off |
| `20POFF` | 20% off |
| `200OFF` | $200 off |
| `100OFF` | $100 off |

> `75POFF` is **expired** and will return an error — use it to test coupon validation.

---

## Chat Window Rules

Chat is only available during a window around the booking start time:

| Rule | Detail |
|---|---|
| Opens | 24 hours **before** the booking start time |
| Closes | 24 hours **after** the booking start time |
| Outside window | API returns 400 |

```swift
// Only call this inside the booking's chat window
let chat = try await client.bookings.getChat(bookingId)
try await client.bookings.sendMessage(bookingId, message: "On my way!")
```

---

## Webhook Events

| Event | Trigger |
|---|---|
| `booking.created` | A new booking is scheduled |
| `booking.cleaner_assigned` | A cleaner is manually assigned |
| `booking.cleaner_removed` | The assigned cleaner is removed |
| `booking.rescheduled` | A booking date/time changes |
| `booking.started` | The cleaner marks the job as started |
| `booking.completed` | The cleaner marks the job as complete |
| `booking.cancelled` | A booking is cancelled |
| `booking.feedback_submitted` | A rating/comment is submitted |

**Example webhook payload:**

```json
{
  "event": "booking.completed",
  "bookingId": 16926,
  "propertyId": 1004,
  "cleanerId": 789,
  "timestamp": "2025-09-15T14:30:00Z"
}
```

You can create one webhook per event type. Use different `url` values to route events to different handlers.

---

## Running Tests

```bash
cd swift-sdk
swift test
```

All **170 tests** should pass.

---

## Project Structure

```
swift-sdk/
├── Package.swift
├── Sources/
│   └── Cleanster/
│       ├── CleansterClient.swift     Main entry point; sandbox/production factories
│       ├── CleansterError.swift      Typed error enum
│       ├── NetworkSession.swift      URLSession abstraction (for mocking)
│       ├── Api/
│       │   ├── BookingsApi.swift     17 booking endpoints
│       │   ├── UsersApi.swift        3 user endpoints
│       │   ├── PropertiesApi.swift   14 property endpoints
│       │   ├── ChecklistsApi.swift   5 checklist endpoints
│       │   ├── OtherApi.swift        7 reference data endpoints
│       │   ├── BlacklistApi.swift    3 blacklist endpoints
│       │   ├── PaymentMethodsApi.swift  6 payment method endpoints
│       │   └── WebhooksApi.swift     4 webhook endpoints
│       ├── Models/
│       │   ├── ApiResponse.swift     Generic response wrapper
│       │   ├── Booking.swift         Booking model + requests
│       │   ├── Property.swift        Property model + requests
│       │   ├── Checklist.swift       Checklist model
│       │   ├── User.swift            User model + auth responses
│       │   ├── PaymentMethod.swift   Payment method model
│       │   └── Other.swift           Reference data models
│       └── Helpers/
│           └── URLBuilder.swift      Query string construction
└── Tests/
    └── CleansterTests/
        ├── BookingsTests.swift
        ├── UsersTests.swift
        ├── PropertiesTests.swift
        ├── ChecklistsTests.swift
        ├── OtherTests.swift
        ├── BlacklistTests.swift
        ├── PaymentMethodsTests.swift
        └── WebhooksTests.swift
```

---

## Support

| Channel | Contact |
|---|---|
| **API Documentation** | [Postman Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep) |
| **Partner inquiries** | [partner@cleanster.com](mailto:partner@cleanster.com) |
| **General support** | [support@cleanster.com](mailto:support@cleanster.com) |
| **Bug reports** | Open an issue in this repository |

---

## License

MIT License — see [LICENSE](../LICENSE) for details.
