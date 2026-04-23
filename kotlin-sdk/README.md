# Cleanster Kotlin SDK

<p align="center">
  <strong>Official Kotlin client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-purple?logo=kotlin" alt="Kotlin 1.9+">
  <img src="https://img.shields.io/badge/JVM-11%2B-blue?logo=openjdk" alt="JVM 11+">
  <img src="https://img.shields.io/badge/Android-API%2026%2B-brightgreen?logo=android" alt="Android 26+">
  <img src="https://img.shields.io/badge/Coroutines-1.7%2B-orange" alt="Coroutines">
  <img src="https://img.shields.io/badge/tests-164%20passing-brightgreen" alt="164 passing">
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

The Cleanster Kotlin SDK provides a modern coroutines-based (`suspend` functions) interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It is backed by OkHttp and Gson, and is compatible with both JVM server applications and Android.

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
| Kotlin | 1.9+ |
| JVM / JDK | 11+ |
| Android | API 26+ (Android 8.0) |
| Gradle | 8.x |

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.cleanster:cleanster-kotlin-sdk:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### Gradle (Groovy)

```groovy
// build.gradle
dependencies {
    implementation 'com.cleanster:cleanster-kotlin-sdk:1.0.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

### Maven

```xml
<dependency>
    <groupId>com.cleanster</groupId>
    <artifactId>cleanster-kotlin-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Android — add internet permission

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — passed at client construction |
| `token` | A per-user JWT — long-lived, obtained via `users.fetchAccessToken(userId)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```kotlin
import com.cleanster.CleansterClient

val client = CleansterClient.sandbox("your-access-key")

val user = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",   // optional
)
val userId = user.data?.id ?: error("User creation failed")
```

**Step 3 — Fetch the user's JWT** (long-lived; store it):

```kotlin
val tokenResp = client.users.fetchAccessToken(userId)
val jwt = tokenResp.data?.token ?: error("No token returned")
```

**Step 4 — Set the token on the client**:

```kotlin
client.setToken(jwt)
// All subsequent calls now include this JWT
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401 on a user-scoped endpoint.

---

## Quick Start

```kotlin
import com.cleanster.CleansterClient
import com.cleanster.model.*

// 1. Build the client
val client = CleansterClient.sandbox("your-access-key")

// 2. Authenticate
val tokenResp = client.users.fetchAccessToken(42)
client.setToken(tokenResp.data?.token ?: "")

// 3. List available services
val services = client.other.getServices()
println("Services: ${services.data}")

// 4. Estimate cost before booking
val estimate = client.other.getCostEstimate(
    CostEstimateRequest(propertyId = 1004, planId = 2, hours = 3.0)
)
println("Total: ${estimate.data?.total}")

// 5. Create a booking
val booking = client.bookings.createBooking(
    CreateBookingRequest(
        date            = "2025-09-01",
        time            = "10:00",
        propertyId      = 1004,
        planId          = 2,
        hours           = 3.0,
        roomCount       = 2,
        bathroomCount   = 1,
        extraSupplies   = false,
        paymentMethodId = 55,
        couponCode      = "20POFF",   // optional
    )
)
println("Created booking: ${booking.data?.id}")

// 6. List open bookings
val open = client.bookings.getBookings(status = "OPEN")
println("Open bookings: ${open.data}")
```

All API calls are `suspend` functions — call them from a coroutine scope:

```kotlin
// Android ViewModel
viewModelScope.launch {
    val bookings = client.bookings.getBookings(status = "OPEN")
}

// Kotlin/JVM with runBlocking
runBlocking {
    val bookings = client.bookings.getBookings()
}

// In tests
runTest {
    val bookings = client.bookings.getBookings()
}
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (development) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```kotlin
// Sandbox
val client = CleansterClient.sandbox("your-access-key")

// Production
val client = CleansterClient.production("your-access-key")
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

All methods are `suspend` functions that return `ApiResponse<T>`.
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

```kotlin
val all       = client.bookings.getBookings()
val open      = client.bookings.getBookings(status = "OPEN")
val completed = client.bookings.getBookings(pageNo = 2, status = "COMPLETED")
```

---

#### Get Booking Details
**`GET /v1/bookings/{bookingId}`**

Retrieve full details for a single booking.

```kotlin
val detail = client.bookings.getBookingDetails(16926)
println("${detail.data?.id} — ${detail.data?.status}")
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
| `extraSupplies` | Boolean | Yes | Whether the cleaner brings supplies |
| `paymentMethodId` | Int | Yes | Payment method ID |
| `couponCode` | String? | No | Discount coupon code |
| `extras` | List\<Int\>? | No | List of extra service IDs |

```kotlin
val booking = client.bookings.createBooking(
    CreateBookingRequest(
        date            = "2025-09-15",
        time            = "09:00",
        propertyId      = 1004,
        planId          = 2,
        hours           = 3.0,
        roomCount       = 2,
        bathroomCount   = 1,
        extraSupplies   = false,
        paymentMethodId = 55,
        couponCode      = "20POFF",   // optional
        extras          = listOf(3, 7), // optional
    )
)
println("Booking ID: ${booking.data?.id}")
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | String? | No | Cancellation reason |

```kotlin
client.bookings.cancelBooking(16926, reason = "Customer request")
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | New date in `YYYY-MM-DD` |
| `time` | String | Yes | New start time in `HH:mm` |

```kotlin
client.bookings.rescheduleBooking(16926, date = "2025-09-20", time = "14:00")
```

---

#### Assign Cleaner
**`POST /v1/bookings/{bookingId}/cleaner`**

Manually assign a specific cleaner. The cleaner must be in the property's preferred pool.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```kotlin
client.bookings.assignCleaner(16926, cleanerId = 789)
```

---

#### Remove Assigned Cleaner
**`DELETE /v1/bookings/{bookingId}/cleaner`**

Remove the currently assigned cleaner. Booking returns to `OPEN` status.

```kotlin
client.bookings.removeAssignedCleaner(16926)
```

---

#### Adjust Hours
**`POST /v1/bookings/{bookingId}/hours`**

Change the duration of a booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `hours` | Double | Yes | New duration in hours |

```kotlin
client.bookings.adjustHours(16926, hours = 4.5)
```

---

#### Pay Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

Pay outstanding balance-on-completion charges. Must be called within 72 hours of booking completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | Int | Yes | Payment method to charge |

```kotlin
client.bookings.payExpenses(16926, paymentMethodId = 55)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

Retrieve the post-booking inspection summary.

```kotlin
val report = client.bookings.getBookingInspection(16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

Retrieve detailed inspection data including photos and notes.

```kotlin
val details = client.bookings.getBookingInspectionDetails(16926)
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this specific booking only.

```kotlin
client.bookings.assignChecklistToBooking(16926, checklistId = 77)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

Submit a star rating and optional comment after a completed booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | Int | Yes | 1 (poor) to 5 (excellent) |
| `comment` | String? | No | Written feedback |

```kotlin
client.bookings.submitFeedback(16926, rating = 5, comment = "Spotless work!")
```

---

#### Add Tip
**`POST /v1/bookings/{bookingId}/tip`**

Add a gratuity for the cleaner. Must be called within 72 hours of booking completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | Double | Yes | Tip amount in account currency |
| `paymentMethodId` | Int | Yes | Payment method to charge |

```kotlin
client.bookings.addTip(16926, amount = 15.00, paymentMethodId = 55)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

Retrieve all chat messages for a booking thread. See [Chat Window Rules](#chat-window-rules).

```kotlin
val chat = client.bookings.getChat(16926)
```

**Response fields per message:**

| Field | Type | Description |
|---|---|---|
| `messageId` | String | Unique message identifier |
| `senderId` | String | Sender reference (e.g. `C6`, `P3`) |
| `content` | String | Text content |
| `timestamp` | String | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `messageType` | String | `text` or `media` |
| `attachments` | List | Media files |
| `attachments[].type` | String | `image`, `video`, or `sound` |
| `attachments[].url` | String | Direct media URL |
| `attachments[].thumbUrl` | String | Thumbnail URL (nullable) |
| `isRead` | Boolean | Whether the message has been read |
| `senderType` | String | `client`, `cleaner`, `support`, or `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

| Field | Type | Required | Description |
|---|---|---|---|
| `message` | String | Yes | Text content to send |

```kotlin
client.bookings.sendMessage(16926, message = "Please bring extra supplies.")
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```kotlin
client.bookings.deleteMessage(16926, messageId = "-OLPrlE06uD8tQ8ebJZw")
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
| `phone` | String? | No | Phone in E.164 format (e.g. `+14155551234`) |

```kotlin
val user = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",
)
val userId = user.data?.id ?: 0
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Retrieve the long-lived JWT for a user. Only the `access-key` header is required for this call.

```kotlin
val tokenResp = client.users.fetchAccessToken(userId)
val jwt = tokenResp.data?.token ?: ""
client.setToken(jwt)
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

Verify that a token is still valid.

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | String | Yes | JWT to validate |

```kotlin
val verify = client.users.verifyJwt("eyJhbGciOi...")
println(verify.message)   // "OK" if valid
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `serviceId` | Int | No | Filter by service type |

```kotlin
val all  = client.properties.listProperties()
val res  = client.properties.listProperties(serviceId = 1)
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
| `state` | String? | No | State or province |
| `zip` | String? | No | Postal / ZIP code |
| `timezone` | String? | No | IANA timezone (e.g. `America/New_York`) |
| `note` | String? | No | Access or cleaning notes |
| `latitude` | Double? | No | GPS latitude |
| `longitude` | Double? | No | GPS longitude |

```kotlin
val prop = client.properties.addProperty(
    CreatePropertyRequest(
        name          = "Downtown Loft",
        address       = "123 Main St",
        city          = "Atlanta",
        country       = "US",
        roomCount     = 2,
        bathroomCount = 1,
        serviceId     = 1,
        state         = "GA",
        zip           = "30301",
        timezone      = "America/New_York",
        note          = "Ring doorbell",
        latitude      = 33.749,
        longitude     = -84.388,
    )
)
val propertyId = prop.data?.id ?: 0
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```kotlin
val detail = client.properties.getProperty(propertyId)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```kotlin
val updated = client.properties.updateProperty(propertyId, updatedRequest)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```kotlin
client.properties.deleteProperty(propertyId)
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

Update freeform key/value fields on a property.

```kotlin
client.properties.updateAdditionalInformation(propertyId, mapOf(
    "parkingInstructions" to "Use visitor lot on 2nd St",
    "accessCode"          to "1234#",
    "petInfo"             to "One friendly golden retriever",
    "garbageDay"          to "Tuesday",
))
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

Disabled properties cannot receive new bookings.

| Field | Type | Required | Description |
|---|---|---|---|
| `enabled` | Boolean | Yes | `true` to enable, `false` to disable |

```kotlin
client.properties.enableOrDisableProperty(propertyId, enabled = false)
```

---

#### Get Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

List all cleaners associated with a property.

```kotlin
val cleaners = client.properties.getPropertyCleaners(propertyId)
```

---

#### Add Cleaner to Property
**`POST /v1/properties/{propertyId}/cleaners`**

Add a cleaner to the property's preferred pool.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```kotlin
client.properties.addCleanerToProperty(propertyId, cleanerId = 789)
```

---

#### Remove Cleaner from Property
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```kotlin
client.properties.removeCleanerFromProperty(propertyId, cleanerId = 789)
```

---

#### Set iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

Add an iCal feed for calendar sync (Airbnb, VRBO, Google Calendar, etc.).

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | String | Yes | Full iCal URL |

```kotlin
client.properties.setICalLink(propertyId,
    "https://www.airbnb.com/calendar/ical/12345.ics?s=abc")
```

---

#### Get iCal Link
**`GET /v1/properties/{propertyId}/ical`**

```kotlin
val ical = client.properties.getICalLink(propertyId)
```

---

#### Delete iCal Link
**`DELETE /v1/properties/{propertyId}/ical`**

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | String | Yes | The iCal URL to remove |

```kotlin
client.properties.deleteICalLink(propertyId,
    "https://www.airbnb.com/calendar/ical/12345.ics?s=abc")
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

Assign a checklist as the default for all future bookings on this property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `checklistId` | Int | Yes | Checklist ID |
| `updateUpcomingBookings` | Boolean | No | If `true`, also updates already-scheduled bookings |

```kotlin
client.properties.setDefaultChecklist(propertyId, checklistId = 77)

// Also update all upcoming bookings
client.properties.setDefaultChecklist(propertyId, checklistId = 77, updateUpcomingBookings = true)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```kotlin
val all = client.checklists.listChecklists()
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```kotlin
val checklist = client.checklists.getChecklist(77)
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Checklist name |
| `items` | List\<String\> | Yes | Task items |

```kotlin
val created = client.checklists.createChecklist(
    name  = "Deep Clean",
    items = listOf(
        "Vacuum all carpets and rugs",
        "Mop all hard floors",
        "Wipe down all surfaces and appliances",
        "Clean interior of oven",
        "Scrub and disinfect all bathrooms",
        "Empty and wipe out all bins",
        "Dust all furniture, shelves, and ceiling fans",
    )
)
val checklistId = created.data?.id ?: 0
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

Replaces the checklist name and items entirely.

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | New checklist name |
| `items` | List\<String\> | Yes | Replacement task items |

```kotlin
client.checklists.updateChecklist(77, "Deep Clean v2", listOf("New task 1", "New task 2"))
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```kotlin
client.checklists.deleteChecklist(77)
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```kotlin
val imageBytes = File("bathroom-guide.jpg").readBytes()
client.checklists.uploadChecklistImage(77, imageBytes, "bathroom-guide.jpg")
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

Retrieve all available service types.

```kotlin
val services = client.other.getServices()
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

Retrieve all cleaning plans available for a specific property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |

```kotlin
val plans = client.other.getPlans(propertyId = 1004)
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

```kotlin
val rec = client.other.getRecommendedHours(
    propertyId    = 1004,
    roomCount     = 3,
    bathroomCount = 2,
)
println("Recommended: ${rec.data?.hours} hours")
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
| `couponCode` | String? | No | Discount coupon code |
| `extras` | List\<Int\>? | No | Extra service IDs |

```kotlin
val estimate = client.other.getCostEstimate(
    CostEstimateRequest(
        propertyId = 1004,
        planId     = 2,
        hours      = 3.0,
        couponCode = "20POFF",
        extras     = listOf(3),
    )
)
println("Subtotal: ${estimate.data?.subtotal}")
println("Discount: ${estimate.data?.discount}")
println("Total:    ${estimate.data?.total}")
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

Retrieve all available add-on services for a given service type.

```kotlin
val extras = client.other.getCleaningExtras(serviceId = 1)
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

```kotlin
val available = client.other.getAvailableCleaners(
    AvailableCleanersRequest(
        propertyId = 1004,
        date       = "2025-09-15",
        time       = "09:00",
    )
)
```

---

#### Get Coupons
**`GET /v1/coupons`**

Retrieve all available (non-expired) coupon codes for the account.

```kotlin
val coupons = client.other.getCoupons()
```

---

### Blacklist

#### List Blacklisted Cleaners
**`GET /v1/blacklist/cleaner`**

```kotlin
val blocked = client.blacklist.listBlacklistedCleaners()
```

---

#### Add to Blacklist
**`POST /v1/blacklist/cleaner`**

Block a cleaner from being assigned to your properties.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |
| `reason` | String? | No | Reason for blocking |

```kotlin
client.blacklist.addToBlacklist(cleanerId = 789, reason = "Repeated late arrivals")
```

---

#### Remove from Blacklist
**`DELETE /v1/blacklist/cleaner`**

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```kotlin
client.blacklist.removeFromBlacklist(cleanerId = 789)
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

Retrieve a Stripe Setup Intent `clientSecret`. Pass this to the Stripe Android SDK to collect and confirm payment method details securely.

```kotlin
val intent = client.paymentMethods.getSetupIntentDetails()
// Pass intent data to Stripe Android SDK
// After confirmation, Stripe returns a payment method ID (pm_xxx)
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

Retrieve a PayPal client token for the Braintree SDK.

```kotlin
val paypal = client.paymentMethods.getPayPalClientToken()
// Pass token to Braintree Android SDK
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

Save a new payment method after collecting it via Stripe or Braintree.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | String | Yes | Stripe `pm_xxx` or Braintree nonce |

```kotlin
// After Stripe Android SDK setup flow:
val method = client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")
println("Saved method ID: ${method.data?.id}")
```

---

#### Get Payment Methods
**`GET /v1/payment-methods`**

List all saved payment methods for the current user.

```kotlin
val methods = client.paymentMethods.getPaymentMethods()
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{paymentMethodId}/default`**

```kotlin
client.paymentMethods.setDefaultPaymentMethod(55)
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{paymentMethodId}`**

```kotlin
client.paymentMethods.deletePaymentMethod(55)
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```kotlin
val hooks = client.webhooks.listWebhooks()
```

---

#### Create Webhook
**`POST /v1/webhooks`**

Subscribe to a booking lifecycle event.

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | Your HTTPS endpoint URL |
| `event` | String | Yes | Event name (see [Webhook Events](#webhook-events)) |

```kotlin
val hook = client.webhooks.createWebhook(
    url   = "https://api.yourapp.com/hooks/cleanster",
    event = "booking.completed",
)
val hookId = hook.data?.id ?: 0
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | New endpoint URL |
| `event` | String | Yes | New event name |

```kotlin
client.webhooks.updateWebhook(
    hookId,
    url   = "https://api.yourapp.com/v2/hooks",
    event = "booking.completed",
)
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```kotlin
client.webhooks.deleteWebhook(hookId)
```

---

## Models Reference

### `ApiResponse<T>`

Every method returns `ApiResponse<T>`:

| Property | Type | Description |
|---|---|---|
| `status` | Int | HTTP status code (200, 400, 401, 403, 404, 500) |
| `message` | String | Human-readable status description |
| `data` | T? | Response payload; `null` on errors |

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
| `cleanerId` | Int? | Assigned cleaner (null if unassigned) |
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
| `enabled` | Boolean | Whether the property accepts bookings |

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
| `extraSupplies` | Boolean | Yes |
| `paymentMethodId` | Int | Yes |
| `couponCode` | String? | No |
| `extras` | List\<Int\>? | No |

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
| `extras` | List\<Int\>? | No |

---

### `AvailableCleanersRequest`

| Field | Type | Required |
|---|---|---|
| `propertyId` | Int | Yes |
| `date` | String | Yes |
| `time` | String | Yes |

---

## Error Handling

```kotlin
import com.cleanster.CleansterError

try {
    val booking = client.bookings.getBookingDetails(99999)
    println("Status: ${booking.data?.status}")
} catch (e: CleansterError.Unauthorized) {
    println("Invalid access-key or token: ${e.message}")
} catch (e: CleansterError.ApiError) {
    println("API error ${e.statusCode}: ${e.message}")
} catch (e: CleansterError.NetworkError) {
    println("Network failure: ${e.message}")
} catch (e: CleansterError.DecodingError) {
    println("Failed to decode response: ${e.message}")
}
```

| Error class | HTTP status | Common cause |
|---|---|---|
| `CleansterError.Unauthorized` | 401 | Missing or invalid `access-key` or `token` |
| `CleansterError.ApiError` | 400, 403, 404, 500 | Business rule violation, not found, server error |
| `CleansterError.NetworkError` | — | No internet, DNS failure, timeout |
| `CleansterError.DecodingError` | — | Unexpected response format |

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

```kotlin
// Only call this inside the booking's chat window
val chat = client.bookings.getChat(bookingId)
client.bookings.sendMessage(bookingId, message = "On my way!")
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
cd kotlin-sdk
./gradlew test
```

All **164 tests** should pass.

To run a single test class:

```bash
./gradlew test --tests "com.cleanster.BookingsTest"
```

---

## Project Structure

```
kotlin-sdk/
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/kotlin/com/cleanster/
│   │   ├── CleansterClient.kt         Main entry point; sandbox/production factories
│   │   ├── CleansterError.kt          Typed error hierarchy
│   │   ├── HttpEngine.kt              OkHttp engine + HttpEngine interface
│   │   ├── api/
│   │   │   ├── BookingsApi.kt         17 booking endpoints
│   │   │   ├── UsersApi.kt            3 user endpoints
│   │   │   ├── PropertiesApi.kt       14 property endpoints
│   │   │   ├── ChecklistsApi.kt       5 checklist endpoints
│   │   │   ├── OtherApi.kt            7 reference data endpoints
│   │   │   ├── BlacklistApi.kt        3 blacklist endpoints
│   │   │   ├── PaymentMethodsApi.kt   6 payment method endpoints
│   │   │   └── WebhooksApi.kt         4 webhook endpoints
│   │   └── model/
│   │       ├── Models.kt              Booking, Property, Checklist, PaymentMethod, ...
│   │       ├── Requests.kt            CreateBookingRequest, CreatePropertyRequest, ...
│   │       └── ApiResponse.kt         Generic ApiResponse<T> wrapper
│   └── test/kotlin/com/cleanster/
│       ├── MockHttpEngine.kt          In-memory mock engine for testing
│       ├── BookingsTest.kt            44 booking tests
│       ├── UsersTest.kt               19 user tests
│       ├── PropertiesTest.kt          33 property tests
│       ├── ChecklistsTest.kt          15 checklist tests
│       ├── OtherTest.kt               21 reference data tests
│       ├── BlacklistTest.kt           8 blacklist tests
│       ├── PaymentMethodsTest.kt      16 payment method tests
│       └── WebhooksTest.kt            11 webhook tests
└── README.md                          This file
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
