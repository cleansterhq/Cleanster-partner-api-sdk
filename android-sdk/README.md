# Cleanster Android SDK (Retrofit)

<p align="center">
  <strong>Official Android client library for the Cleanster Partner API</strong><br>
  Built on Retrofit 2 + OkHttp + Gson — the most widely used Android HTTP stack
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-API%2026%2B-brightgreen?logo=android" alt="Android API 26+">
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-purple?logo=kotlin" alt="Kotlin 1.9+">
  <img src="https://img.shields.io/badge/Retrofit-2.9-blue?logo=square" alt="Retrofit 2.9">
  <img src="https://img.shields.io/badge/OkHttp-4.12-orange" alt="OkHttp 4.12">
  <img src="https://img.shields.io/badge/tests-164%20passing-brightgreen" alt="164 passing">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT License">
  <img src="https://img.shields.io/badge/API-Cleanster%20Partner-brightgreen" alt="Cleanster Partner API">
</p>

---

## Table of Contents

- [Overview](#overview)
- [Why Retrofit?](#why-retrofit)
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
- [Advanced Usage](#advanced-usage)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## Overview

The Cleanster Android SDK wraps the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep) in a type-safe, coroutines-ready Retrofit 2 client. All 60 API endpoints across 8 resource types are covered with full Kotlin data-class request/response models.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal calendar sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create reusable task lists and assign to bookings
- **Process payments** — Stripe Setup Intent + PayPal Braintree flows
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — block specific cleaners from your properties

---

## Why Retrofit?

Retrofit is the most widely adopted HTTP client library in the Android ecosystem:

| Library | Downloads / Month | Stars on GitHub |
|---|---|---|
| **Retrofit** (Square) | 20M+ | 42,000+ |
| OkHttp only | bundled | — |
| Ktor | 3M+ | 12,000+ |
| Volley | legacy | 3,300+ |

Retrofit's annotation-based interface contracts (`@GET`, `@POST`, `@Body`, `@Path`, etc.) pair naturally with Kotlin `suspend` functions, giving you compile-time safety and zero-boilerplate network code. OkHttp provides the underlying connection pooling, TLS, and interceptor chain.

---

## Requirements

| Component | Version |
|---|---|
| Android | API 26+ (Android 8.0 Oreo) |
| Kotlin | 1.9+ |
| Retrofit | 2.9.0 |
| OkHttp | 4.12.0 |
| Gson | 2.10.1 |
| Coroutines | 1.7.3+ |
| JVM | 11+ |

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.cleanster:cleanster-android-sdk:1.0.0")

    // Retrofit stack
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Optional: for testing
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

### Gradle (Groovy)

```groovy
// app/build.gradle
dependencies {
    implementation 'com.cleanster:cleanster-android-sdk:1.0.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### AndroidManifest.xml — Internet Permission

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    ...
</manifest>
```

> Without this permission the app will throw `java.net.UnknownHostException` on API calls.

### ProGuard / R8 Rules

If you use ProGuard or R8, add:

```proguard
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson models
-keep class com.cleanster.android.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
```

---

## Authentication

Every API call requires two HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — passed at client construction |
| `token` | A per-user JWT — obtained via `users.fetchAccessToken(userId)` |

The SDK injects both headers automatically via an `OkHttp` `Interceptor` — you never set headers manually.

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```kotlin
import com.cleanster.android.CleansterClient

val client = CleansterClient.sandbox("your-access-key")

val userResp = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",   // optional
)
val userId = userResp.data?.id ?: error("User creation failed")
// Save userId in your database
```

**Step 3 — Fetch the user's JWT** (long-lived; store it securely):

```kotlin
val tokenResp = client.users.fetchAccessToken(userId)
val jwt = tokenResp.data?.token ?: error("No token returned")
// Save jwt securely (encrypted SharedPreferences, Keystore, etc.)
```

**Step 4 — Set the token on the client**:

```kotlin
client.setToken(jwt)
// All subsequent calls automatically include this JWT
```

> **Token lifecycle:** Tokens are long-lived. Only refresh when you receive HTTP 401 on a user-scoped endpoint.

---

## Quick Start

```kotlin
import com.cleanster.android.CleansterClient
import com.cleanster.android.model.*
import kotlinx.coroutines.launch

// In a ViewModel or coroutine scope:
val client = CleansterClient.sandbox("your-access-key")

viewModelScope.launch {
    // 1. Authenticate
    val tokenResp = client.users.fetchAccessToken(userId = 42)
    client.setToken(tokenResp.data?.token ?: return@launch)

    // 2. Browse available services
    val services = client.other.getServices()
    println("Services: ${services.data}")

    // 3. Get a price estimate before booking
    val estimate = client.other.getCostEstimate(
        CostEstimateRequest(
            propertyId = 1004,
            planId     = 2,
            hours      = 3.0,
            couponCode = "20POFF",
        )
    )
    println("Total: ${estimate.data?.total}")

    // 4. Create the booking
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
            couponCode      = "20POFF",
        )
    )
    println("Booking ID: ${booking.data?.id}")

    // 5. List upcoming bookings
    val openBookings = client.bookings.getBookings(status = "OPEN")
    println("Open bookings: ${openBookings.data?.size}")
}
```

All API methods are `suspend` functions. Call them from:
- `viewModelScope.launch { }` — recommended for Android ViewModels
- `lifecycleScope.launch { }` — for Activities and Fragments
- `runBlocking { }` — for unit tests or scripts

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (development) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```kotlin
// Sandbox — always start here
val client = CleansterClient.sandbox("your-access-key")

// Production — switch only after sandbox testing is complete
val client = CleansterClient.production("your-access-key")

// Custom base URL — for tests
val client = CleansterClient.custom("your-access-key", mockWebServer.url("/").toString())
```

---

## Booking Flow

```
createBooking()         →   OPEN
                               │
             assignCleaner()  (or auto-dispatched)
                               │
                               ▼
                     CLEANER_ASSIGNED
                               │
                    Cleaner checks in
                               │
                               ▼
                          IN_PROGRESS
                               │
                   Cleaner marks complete
                    ┌──────────┴──────────┐
                    ▼                     ▼
               COMPLETED             CANCELLED
                    │
     ┌──────────────┼──────────────┐
     ▼              ▼              ▼
addTip()     payExpenses()   submitFeedback()
```

Booking status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All methods are `suspend` functions returning `ApiResponse<T>`.

| Property | Type | Description |
|---|---|---|
| `.status` | Int | HTTP status code (200, 400, 401, 403, 404, 500) |
| `.message` | String? | Human-readable description |
| `.data` | T? | Response payload; `null` on errors |

---

### Bookings

#### List Bookings
**`GET /v1/bookings`**

Retrieve a paginated list of bookings, optionally filtered by status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | Int? | No | Page number (1-based) |
| `status` | String? | No | Filter by booking status |

```kotlin
val all  = client.bookings.getBookings()
val open = client.bookings.getBookings(status = "OPEN")
val page2 = client.bookings.getBookings(pageNo = 2, status = "COMPLETED")
```

---

#### Get Booking Details
**`GET /v1/bookings/{bookingId}`**

```kotlin
val booking = client.bookings.getBookingDetails(16926)
println("${booking.data?.id} — ${booking.data?.status}")
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | Date in `YYYY-MM-DD` |
| `time` | String | Yes | Start time in `HH:mm` (24-hour) |
| `propertyId` | Int | Yes | Property to be cleaned |
| `planId` | Int | Yes | Cleaning plan ID |
| `hours` | Double | Yes | Duration in hours |
| `roomCount` | Int | Yes | Number of rooms |
| `bathroomCount` | Int | Yes | Number of bathrooms |
| `extraSupplies` | Boolean | Yes | Cleaner brings supplies |
| `paymentMethodId` | Int | Yes | Payment method ID |
| `couponCode` | String? | No | Discount coupon |
| `extras` | List\<Int\>? | No | Extra service IDs |

```kotlin
val resp = client.bookings.createBooking(
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
        couponCode      = "20POFF",
        extras          = listOf(3, 7),
    )
)
val bookingId = resp.data?.id ?: 0
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | String? | No | Cancellation reason |

```kotlin
client.bookings.cancelBooking(16926, reason = "Customer changed plans")
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | New date in `YYYY-MM-DD` |
| `time` | String | Yes | New time in `HH:mm` |

```kotlin
client.bookings.rescheduleBooking(16926, date = "2025-09-20", time = "14:00")
```

---

#### Assign Cleaner
**`POST /v1/bookings/{bookingId}/cleaner`**

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```kotlin
client.bookings.assignCleaner(16926, cleanerId = 789)
```

---

#### Remove Assigned Cleaner
**`DELETE /v1/bookings/{bookingId}/cleaner`**

Removes the currently assigned cleaner. Booking returns to `OPEN` status.

```kotlin
client.bookings.removeAssignedCleaner(16926)
```

---

#### Adjust Hours
**`POST /v1/bookings/{bookingId}/hours`**

| Field | Type | Required | Description |
|---|---|---|---|
| `hours` | Double | Yes | New duration in hours |

```kotlin
client.bookings.adjustHours(16926, hours = 4.5)
```

---

#### Pay Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

Pay outstanding balance-on-completion charges. Must be called within 72 hours of completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | Int | Yes | Payment method to charge |

```kotlin
client.bookings.payExpenses(16926, paymentMethodId = 55)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```kotlin
val report = client.bookings.getBookingInspection(16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```kotlin
val details = client.bookings.getBookingInspectionDetails(16926)
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```kotlin
client.bookings.assignChecklistToBooking(16926, checklistId = 77)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | Int | Yes | 1 (poor) to 5 (excellent) |
| `comment` | String? | No | Written feedback |

```kotlin
client.bookings.submitFeedback(16926, rating = 5, comment = "Spotless!")
```

---

#### Add Tip
**`POST /v1/bookings/{bookingId}/tip`**

Must be called within 72 hours of booking completion.

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | Double | Yes | Tip amount in account currency |
| `paymentMethodId` | Int | Yes | Payment method to charge |

```kotlin
client.bookings.addTip(16926, amount = 15.0, paymentMethodId = 55)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

Only accessible within 24 hours before and after the booking start time. See [Chat Window Rules](#chat-window-rules).

```kotlin
val messages = client.bookings.getChat(16926)
messages.data?.forEach { msg ->
    println("[${msg.senderType}] ${msg.content}")
}
```

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

| Field | Type | Required | Description |
|---|---|---|---|
| `message` | String | Yes | Text to send |

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

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | String | Yes | Email address |
| `firstName` | String | Yes | First name |
| `lastName` | String | Yes | Last name |
| `phone` | String? | No | Phone in E.164 format |

```kotlin
val resp = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",
)
val userId = resp.data?.id ?: error("User creation failed")
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Only the `access-key` header is required for this call.

```kotlin
val resp = client.users.fetchAccessToken(userId)
client.setToken(resp.data?.token ?: "")
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | String | Yes | JWT to validate |

```kotlin
val resp = client.users.verifyJwt("eyJhbGciOi...")
println(resp.message)  // "OK" if valid
```

---

### Properties

#### List Properties
**`GET /v1/properties`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `serviceId` | Int? | No | Filter by service type |

```kotlin
val all = client.properties.listProperties()
val residential = client.properties.listProperties(serviceId = 1)
```

---

#### Add Property
**`POST /v1/properties`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Display name |
| `address` | String | Yes | Street address |
| `city` | String | Yes | City |
| `country` | String | Yes | Country code (`US`, `GB`, etc.) |
| `roomCount` | Int | Yes | Bedroom count |
| `bathroomCount` | Int | Yes | Bathroom count |
| `serviceId` | Int | Yes | Service area ID |
| `state` | String? | No | State / province |
| `zip` | String? | No | Postal / ZIP code |
| `timezone` | String? | No | IANA timezone |
| `note` | String? | No | Access notes |
| `latitude` | Double? | No | GPS latitude |
| `longitude` | Double? | No | GPS longitude |

```kotlin
val resp = client.properties.addProperty(
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
val propertyId = resp.data?.id ?: 0
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```kotlin
val property = client.properties.getProperty(1004)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```kotlin
val updated = client.properties.updateProperty(1004, updatedRequest)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```kotlin
client.properties.deleteProperty(1004)
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```kotlin
client.properties.updateAdditionalInformation(
    propertyId = 1004,
    info = mapOf(
        "parkingInstructions" to "Use visitor lot on 2nd St",
        "accessCode"          to "1234#",
        "petInfo"             to "One golden retriever — friendly",
        "garbageDay"          to "Tuesday",
    )
)
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

| Field | Type | Required | Description |
|---|---|---|---|
| `enabled` | Boolean | Yes | `true` to enable, `false` to disable |

```kotlin
// Disable — no new bookings accepted
client.properties.enableOrDisableProperty(1004, enabled = false)

// Re-enable
client.properties.enableOrDisableProperty(1004, enabled = true)
```

---

#### Get Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```kotlin
val cleaners = client.properties.getPropertyCleaners(1004)
cleaners.data?.forEach { println("${it.firstName} ${it.lastName}") }
```

---

#### Add Cleaner to Property
**`POST /v1/properties/{propertyId}/cleaners`**

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | Int | Yes | Cleaner user ID |

```kotlin
client.properties.addCleanerToProperty(1004, cleanerId = 789)
```

---

#### Remove Cleaner from Property
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```kotlin
client.properties.removeCleanerFromProperty(1004, cleanerId = 789)
```

---

#### Set iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

Add an iCal feed for automatic calendar sync (Airbnb, VRBO, Google Calendar, etc.).

```kotlin
client.properties.setICalLink(
    propertyId = 1004,
    icalLink   = "https://www.airbnb.com/calendar/ical/12345.ics?s=abc",
)
```

---

#### Get iCal Link
**`GET /v1/properties/{propertyId}/ical`**

```kotlin
val ical = client.properties.getICalLink(1004)
```

---

#### Delete iCal Link
**`DELETE /v1/properties/{propertyId}/ical`**

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | String | Yes | The iCal URL to remove |

```kotlin
client.properties.deleteICalLink(
    propertyId = 1004,
    icalLink   = "https://www.airbnb.com/calendar/ical/12345.ics?s=abc",
)
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `checklistId` | Int | Yes | Checklist ID |
| `updateUpcomingBookings` | Boolean? | No | Also update already-scheduled bookings |

```kotlin
// Set default for future bookings only
client.properties.setDefaultChecklist(1004, checklistId = 77)

// Also update all upcoming bookings
client.properties.setDefaultChecklist(1004, checklistId = 77, updateUpcomingBookings = true)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```kotlin
val checklists = client.checklists.listChecklists()
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```kotlin
val checklist = client.checklists.getChecklist(77)
println("${checklist.data?.name}: ${checklist.data?.items}")
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Checklist name |
| `items` | List\<String\> | Yes | Task items |

```kotlin
val resp = client.checklists.createChecklist(
    name  = "Deep Clean",
    items = listOf(
        "Vacuum all carpets and rugs",
        "Mop all hard floors",
        "Wipe down all surfaces and appliances",
        "Clean interior of oven",
        "Scrub and disinfect all bathrooms",
        "Empty and wipe all bins",
        "Dust furniture, shelves, and ceiling fans",
    )
)
val checklistId = resp.data?.id ?: 0
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

Replaces the name and all items entirely.

```kotlin
client.checklists.updateChecklist(77, "Deep Clean v2", listOf("Updated task 1", "Updated task 2"))
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```kotlin
client.checklists.deleteChecklist(77)
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```kotlin
val services = client.other.getServices()
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |

```kotlin
val plans = client.other.getPlans(propertyId = 1004)
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours`**

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
println("Recommended hours: ${rec.data}")
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

Safe to call without committing to a booking. Use before `createBooking` to show pricing.

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |
| `planId` | Int | Yes | Cleaning plan ID |
| `hours` | Double | Yes | Estimated duration |
| `couponCode` | String? | No | Discount coupon |
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

```kotlin
val extras = client.other.getCleaningExtras(serviceId = 1)
// e.g. [{ id: 3, name: "Oven Cleaning", price: 25 }, ...]
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | Int | Yes | The property ID |
| `date` | String | Yes | Date in `YYYY-MM-DD` |
| `time` | String | Yes | Start time in `HH:mm` |

```kotlin
val available = client.other.getAvailableCleaners(
    AvailableCleanersRequest(
        propertyId = 1004,
        date       = "2025-09-15",
        time       = "09:00",
    )
)
available.data?.forEach { println("${it.firstName} ${it.lastName} ⭐ ${it.rating}") }
```

---

#### Get Coupons
**`GET /v1/coupons`**

```kotlin
val coupons = client.other.getCoupons()
coupons.data?.forEach { println("${it.code}: ${it.discount}") }
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

Returns a Stripe Setup Intent `clientSecret`. Pass this to the Stripe Android SDK to collect payment details.

```kotlin
val intent = client.paymentMethods.getSetupIntentDetails()
// Pass intent.data to Stripe Android SDK confirmSetup()
// After confirmation, you receive a payment method ID (pm_xxx)
```

**Stripe Android SDK integration:**

```kotlin
// After receiving the client secret:
val paymentSheet = PaymentSheet(this) { result ->
    when (result) {
        is PaymentSheetResult.Completed -> {
            // Save pm_xxx via addPaymentMethod
            viewModelScope.launch {
                client.paymentMethods.addPaymentMethod("pm_xxx_from_stripe")
            }
        }
        else -> { /* handle error */ }
    }
}
paymentSheet.presentWithSetupIntent(
    setupIntentClientSecret = intent.data?.clientSecret ?: ""
)
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```kotlin
val paypal = client.paymentMethods.getPayPalClientToken()
// Pass token to Braintree Drop-In UI
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

Save a payment method after collecting it via Stripe or Braintree.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | String | Yes | Stripe `pm_xxx` or Braintree nonce |

```kotlin
// After Stripe Android SDK confirms card:
val method = client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")
println("Saved ID: ${method.data?.id}")
```

---

#### Get Payment Methods
**`GET /v1/payment-methods`**

```kotlin
val methods = client.paymentMethods.getPaymentMethods()
methods.data?.forEach { pm ->
    println("${pm.brand} ****${pm.last4} ${if (pm.isDefault) "(default)" else ""}")
}
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{paymentMethodId}/default`**

```kotlin
client.paymentMethods.setDefaultPaymentMethod(paymentMethodId = 55)
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{paymentMethodId}`**

```kotlin
client.paymentMethods.deletePaymentMethod(paymentMethodId = 55)
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

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | Your HTTPS endpoint |
| `event` | String | Yes | Event name (see [Webhook Events](#webhook-events)) |

```kotlin
val hook = client.webhooks.createWebhook(
    url   = "https://api.yourapp.com/hooks/cleanster",
    event = "booking.completed",
)
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```kotlin
client.webhooks.updateWebhook(
    webhookId = 1,
    url       = "https://api.yourapp.com/v2/hooks",
    event     = "booking.completed",
)
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```kotlin
client.webhooks.deleteWebhook(webhookId = 1)
```

---

## Models Reference

### `ApiResponse<T>`

| Property | Type | Description |
|---|---|---|
| `status` | Int | HTTP status code |
| `message` | String? | Human-readable status text |
| `data` | T? | Response payload; null on errors |

---

### `Booking`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Booking ID |
| `status` | String | Current booking status |
| `date` | String | `YYYY-MM-DD` |
| `time` | String | `HH:mm` |
| `hours` | Double | Duration in hours |
| `propertyId` | Int | Property being cleaned |
| `planId` | Int | Cleaning plan ID |
| `cleanerId` | Int? | Assigned cleaner (null if unassigned) |
| `paymentMethodId` | Int | Payment method used |
| `roomCount` | Int | Number of rooms |
| `bathroomCount` | Int | Number of bathrooms |
| `couponCode` | String? | Applied coupon code |

---

### `Property`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Property ID |
| `name` | String | Display name |
| `address` | String | Street address |
| `city` | String | City |
| `state` | String? | State/province |
| `zip` | String? | Postal code |
| `country` | String | Country code |
| `roomCount` | Int | Bedrooms |
| `bathroomCount` | Int | Bathrooms |
| `serviceId` | Int | Service area |
| `latitude` | Double? | GPS latitude |
| `longitude` | Double? | GPS longitude |
| `timezone` | String? | IANA timezone |
| `isEnabled` | Boolean | Accepts bookings |
| `note` | String? | Access notes |

---

### `Checklist`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Checklist ID |
| `name` | String | Checklist name |
| `items` | List\<String\> | Task items |

---

### `PaymentMethod`

| Field | Type | Description |
|---|---|---|
| `id` | Int | Saved method ID |
| `brand` | String? | Card brand (Visa, Mastercard, etc.) |
| `last4` | String? | Last 4 digits |
| `isDefault` | Boolean | Whether this is the default method |
| `type` | String? | `card` or `paypal` |

---

### `ChatMessage`

| Field | Type | Description |
|---|---|---|
| `messageId` | String | Unique identifier |
| `senderId` | String | Sender reference |
| `content` | String | Text content |
| `timestamp` | String | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `messageType` | String | `text` or `media` |
| `isRead` | Boolean | Read status |
| `senderType` | String | `client`, `cleaner`, `support`, or `bot` |

---

### `CostEstimate`

| Field | Type | Description |
|---|---|---|
| `subtotal` | Double | Base price before discounts |
| `discount` | Double | Amount discounted |
| `total` | Double | Final charge amount |

---

## Error Handling

```kotlin
import com.cleanster.android.CleansterError

try {
    val booking = client.bookings.getBookingDetails(99999)
} catch (e: CleansterError.Unauthorized) {
    // HTTP 401 — invalid access-key or token
    showError("Please log in again.")
} catch (e: CleansterError.ApiError) {
    // HTTP 400, 403, 404, 500
    showError("Error ${e.statusCode}: ${e.message}")
} catch (e: CleansterError.NetworkError) {
    // No internet or connection timeout
    showError("Check your internet connection.")
} catch (e: CleansterError.DecodingError) {
    // Unexpected response format
    showError("Something went wrong. Please try again.")
}
```

| Error class | HTTP status | Common cause |
|---|---|---|
| `CleansterError.Unauthorized` | 401 | Missing or invalid `access-key` or `token` |
| `CleansterError.ApiError` | 400, 403, 404, 500 | Business rule violation, not found, server error |
| `CleansterError.NetworkError` | — | No internet, DNS failure, timeout |
| `CleansterError.DecodingError` | — | Unexpected response format |

**ViewModel pattern with error handling:**

```kotlin
class BookingViewModel(
    private val client: CleansterClient,
) : ViewModel() {
    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadBookings() {
        viewModelScope.launch {
            try {
                val resp = client.bookings.getBookings(status = "OPEN")
                _bookings.value = resp.data ?: emptyList()
            } catch (e: CleansterError.Unauthorized) {
                _error.value = "Session expired. Please log in again."
            } catch (e: CleansterError.NetworkError) {
                _error.value = "No internet connection."
            } catch (e: CleansterError.ApiError) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
}
```

---

## Test Coupon Codes (Sandbox)

| Code | Discount |
|---|---|
| `100POFF` | 100% off |
| `50POFF` | 50% off |
| `20POFF` | 20% off |
| `200OFF` | $200 off |
| `100OFF` | $100 off |

> `75POFF` is **expired** and will return an error — use it to test coupon validation flows.

---

## Chat Window Rules

Chat is only available during a window around the booking:

| Rule | Detail |
|---|---|
| Opens | 24 hours **before** the booking start time |
| Closes | 24 hours **after** the booking start time |
| Outside window | API returns 400 |

```kotlin
// Check before attempting to call chat APIs
viewModelScope.launch {
    try {
        val messages = client.bookings.getChat(bookingId)
        displayMessages(messages.data ?: emptyList())
    } catch (e: CleansterError.ApiError) {
        if (e.statusCode == 400) {
            showInfo("Chat is not available for this booking yet.")
        }
    }
}
```

---

## Webhook Events

| Event | Trigger |
|---|---|
| `booking.created` | New booking is scheduled |
| `booking.cleaner_assigned` | A cleaner is confirmed |
| `booking.cleaner_removed` | Assigned cleaner is removed |
| `booking.rescheduled` | Date or time changes |
| `booking.started` | Cleaner checks in |
| `booking.completed` | Job finished |
| `booking.cancelled` | Booking is cancelled |
| `booking.feedback_submitted` | Rating is submitted |

**Register a webhook:**

```kotlin
// One webhook per event type
client.webhooks.createWebhook(
    url   = "https://api.yourapp.com/hooks/cleanster",
    event = "booking.completed",
)

client.webhooks.createWebhook(
    url   = "https://api.yourapp.com/hooks/cleanster",
    event = "booking.cleaner_assigned",
)
```

**Example payload received at your endpoint:**

```json
{
  "event": "booking.completed",
  "bookingId": 16926,
  "propertyId": 1004,
  "cleanerId": 789,
  "timestamp": "2025-09-15T14:30:00Z"
}
```

Your server must return HTTP `200` within 5 seconds. Timed-out deliveries are retried with exponential backoff.

---

## Advanced Usage

### Enable OkHttp Logging (Development)

```kotlin
// Enable full request/response logging in sandbox
val client = CleansterClient.sandbox("your-access-key", enableLogging = true)
// Disable in production (CleansterClient.production never logs)
```

> **Warning:** `enableLogging = true` writes full request/response bodies to Logcat, including your `access-key` and JWT tokens. Never enable in production builds.

### Dependency Injection with Hilt

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCleansterClient(): CleansterClient {
        return if (BuildConfig.DEBUG) {
            CleansterClient.sandbox(BuildConfig.CLEANSTER_ACCESS_KEY)
        } else {
            CleansterClient.production(BuildConfig.CLEANSTER_ACCESS_KEY)
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cleansterClient: CleansterClient,
) : ViewModel() {
    fun loadBookings() = viewModelScope.launch {
        val resp = cleansterClient.bookings.getBookings()
        // ...
    }
}
```

### Dependency Injection with Koin

```kotlin
// di/NetworkModule.kt
val networkModule = module {
    single {
        if (BuildConfig.DEBUG) {
            CleansterClient.sandbox(BuildConfig.CLEANSTER_ACCESS_KEY)
        } else {
            CleansterClient.production(BuildConfig.CLEANSTER_ACCESS_KEY)
        }
    }
}

// Usage in ViewModel:
class HomeViewModel(
    private val client: CleansterClient,
) : ViewModel() {
    init {
        viewModelScope.launch {
            val bookings = client.bookings.getBookings()
        }
    }
}
```

### Storing the JWT Securely (Android Keystore)

```kotlin
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val prefs = EncryptedSharedPreferences.create(
    context,
    "cleanster_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
)

// Save token
prefs.edit().putString("cleanster_jwt", jwt).apply()

// Restore token on app launch
val jwt = prefs.getString("cleanster_jwt", null)
if (jwt != null) client.setToken(jwt)
```

### Testing with MockWebServer

The SDK's `CleansterClient.custom()` factory accepts a custom base URL, making it easy to inject a `MockWebServer` in tests:

```kotlin
class MyBookingTest {
    private val server = MockWebServer()
    private val client = CleansterClient.custom("test-key", server.url("/").toString())

    @Before fun setUp() {
        server.start()
        client.setToken("test-token")
    }

    @After fun tearDown() { server.shutdown() }

    @Test fun `createBooking sends POST`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody("""{"status":200,"message":"OK","data":{"id":1,"status":"OPEN"}}""")
                .addHeader("Content-Type", "application/json")
        )
        val resp = client.bookings.createBooking(
            CreateBookingRequest("2025-09-15","09:00",1004,2,3.0,2,1,false,55)
        )
        assertEquals("POST", server.takeRequest().method)
        assertEquals(1, resp.data?.id)
    }
}
```

---

## Running Tests

```bash
cd android-sdk
./gradlew test
```

All **164 tests** should pass.

To run a specific test class:

```bash
./gradlew test --tests "com.cleanster.android.BookingsTest"
```

To run with verbose output:

```bash
./gradlew test --info
```

**Test distribution:**

| File | Tests | Coverage |
|---|---|---|
| `BookingsTest.kt` | 47 | 17 endpoints + auth headers |
| `UsersTest.kt` | 18 | 3 endpoints + token management |
| `PropertiesTest.kt` | 26 | 14 endpoints |
| `ChecklistsTest.kt` | 18 | 5 endpoints |
| `OtherTest.kt` | 21 | 7 reference data endpoints |
| `BlacklistTest.kt` | 9 | 3 endpoints |
| `PaymentMethodsTest.kt` | 14 | 6 endpoints |
| `WebhooksTest.kt` | 11 | 4 endpoints + 8 event types |
| **Total** | **164** | **60 endpoints** |

---

## Project Structure

```
android-sdk/
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/kotlin/com/cleanster/android/
│   │   ├── CleansterClient.kt            Main entry point; sandbox/production/custom factories
│   │   ├── CleansterError.kt             Typed error hierarchy
│   │   ├── AuthInterceptor.kt            OkHttp interceptor for access-key + token headers
│   │   ├── api/
│   │   │   ├── BookingsApi.kt            Retrofit service + wrapper (17 endpoints)
│   │   │   ├── UsersApi.kt               Retrofit service + wrapper (3 endpoints) + wrap()
│   │   │   ├── PropertiesApi.kt          Retrofit service + wrapper (14 endpoints)
│   │   │   ├── ChecklistsApi.kt          Retrofit service + wrapper (5 endpoints)
│   │   │   ├── OtherApi.kt               Retrofit service + wrapper (7 endpoints)
│   │   │   ├── BlacklistApi.kt           Retrofit service + wrapper (3 endpoints)
│   │   │   ├── PaymentMethodsApi.kt      Retrofit service + wrapper (6 endpoints)
│   │   │   └── WebhooksApi.kt            Retrofit service + wrapper (4 endpoints)
│   │   └── model/
│   │       ├── ApiResponse.kt            Generic ApiResponse<T> wrapper
│   │       ├── Models.kt                 Booking, Property, Checklist, PaymentMethod, ...
│   │       └── Requests.kt               CreateBookingRequest, CreatePropertyRequest, ...
│   └── test/kotlin/com/cleanster/android/
│       ├── BookingsTest.kt               47 tests — MockWebServer
│       ├── UsersTest.kt                  18 tests — MockWebServer
│       ├── PropertiesTest.kt             26 tests — MockWebServer
│       ├── ChecklistsTest.kt             18 tests — MockWebServer
│       ├── OtherTest.kt                  21 tests — MockWebServer
│       ├── BlacklistTest.kt              9  tests — MockWebServer
│       ├── PaymentMethodsTest.kt         14 tests — MockWebServer
│       └── WebhooksTest.kt               11 tests — MockWebServer
└── README.md                             This file
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
