# Cleanster Kotlin SDK

Kotlin SDK for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep).
Covers all 53 endpoints with Kotlin Coroutines (`suspend` functions), data classes, Gson serialization, and Gradle/Kotlin DSL.

---

## Requirements

| Tool | Version |
|---|---|
| Kotlin | 1.9+ |
| JVM / JDK | 11+ |
| Android | API 26+ |
| Gradle | 8.x |

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.cleanster:cleanster-kotlin-sdk:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.cleanster:cleanster-kotlin-sdk:1.0.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
}
```

---

## Quick Start

```kotlin
import com.cleanster.CleansterClient

// Sandbox (development & testing)
val client = CleansterClient.sandbox("your-access-key")

// Production (go-live)
val client = CleansterClient.production("your-access-key")

// Fetch and set the per-user JWT
val tokenResp = client.users.fetchAccessToken(12345)
client.setToken(tokenResp.data?.token ?: "")

// Create a booking
val booking = client.bookings.createBooking(
    CreateBookingRequest(
        date            = "2025-09-15",
        time            = "10:00",
        propertyId      = 1004,
        planId          = 2,
        hours           = 3.0,
        roomCount       = 2,
        bathroomCount   = 1,
        extraSupplies   = false,
        paymentMethodId = 55,
    )
)
println("Booking ID: ${booking.data?.id}")
```

All API calls are `suspend` functions — call them from a coroutine scope:

```kotlin
// In a ViewModel (Android):
viewModelScope.launch {
    val bookings = client.bookings.getBookings(status = "OPEN")
}

// In a coroutine test:
runTest {
    val bookings = client.bookings.getBookings()
}
```

---

## Authentication

| Header | Description |
|---|---|
| `access-key` | Your static partner key — passed at client construction |
| `token` | Per-user JWT — set via `client.setToken(token)` |

**Full auth flow:**

```kotlin
// 1. Create the user (first time only)
val user = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",
)
val userId = user.data?.id ?: error("No user ID")

// 2. Fetch their JWT
val tokenResp = client.users.fetchAccessToken(userId)
val jwt = tokenResp.data?.token ?: error("No token")

// 3. Set the token — all subsequent calls use this
client.setToken(jwt)

// 4. (Optional) Validate the token is still live
val verify = client.users.verifyJwt(jwt)
println(verify.message)  // "OK"
```

---

## Error Handling

```kotlin
import com.cleanster.CleansterError

try {
    val booking = client.bookings.getBookingDetails(99999)
} catch (e: CleansterError.Unauthorized) {
    println("Invalid access-key or token: ${e.message}")
} catch (e: CleansterError.ApiError) {
    println("API error ${e.statusCode}: ${e.message}")
} catch (e: CleansterError.NetworkError) {
    println("Network failure: ${e.message}")
} catch (e: CleansterError.DecodingError) {
    println("Bad response: ${e.message}")
}
```

---

## All Endpoints

### Users

```kotlin
// Create user
val user = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",  // optional
)

// Fetch JWT
val resp = client.users.fetchAccessToken(userId)
client.setToken(resp.data?.token ?: "")

// Verify JWT
val verify = client.users.verifyJwt("eyJhbGciOi...")
```

---

### Properties

```kotlin
// List
val all  = client.properties.listProperties()
val res  = client.properties.listProperties(serviceId = 1)

// Add
val prop = client.properties.addProperty(
    CreatePropertyRequest(
        name          = "Downtown Loft",
        address       = "123 Main St",
        city          = "Atlanta",
        country       = "US",
        roomCount     = 2,
        bathroomCount = 1,
        serviceId     = 1,
        state         = "GA",              // optional
        zip           = "30301",           // optional
        timezone      = "America/New_York",// optional
        note          = "Ring doorbell",   // optional
        latitude      = 33.749,            // optional
        longitude     = -84.388,           // optional
    )
)
val propertyId = prop.data?.id ?: 0

// Get, update, delete
val detail = client.properties.getProperty(propertyId)
client.properties.updateProperty(propertyId, updatedRequest)
client.properties.deleteProperty(propertyId)

// Enable/disable
client.properties.enableOrDisableProperty(propertyId, enabled = false)

// Cleaner management
val cleaners = client.properties.getPropertyCleaners(propertyId)
client.properties.addCleanerToProperty(propertyId, cleanerId = 789)
client.properties.removeCleanerFromProperty(propertyId, cleanerId = 789)

// iCal sync
client.properties.setICalLink(propertyId, "https://airbnb.com/calendar.ics")
val ical = client.properties.getICalLink(propertyId)
client.properties.deleteICalLink(propertyId, "https://airbnb.com/calendar.ics")

// Default checklist (updateUpcomingBookings also updates already-scheduled bookings)
client.properties.setDefaultChecklist(propertyId, checklistId = 77, updateUpcomingBookings = true)

// Additional info
client.properties.updateAdditionalInformation(propertyId, mapOf(
    "parkingInstructions" to "Use visitor lot",
    "accessCode"          to "1234#",
))
```

---

### Bookings

```kotlin
// List
val all       = client.bookings.getBookings()
val open      = client.bookings.getBookings(status = "OPEN")
val completed = client.bookings.getBookings(pageNo = 2, status = "COMPLETED")

// Create
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
        couponCode      = "20POFF",  // optional
        extras          = listOf(3, 7), // optional
    )
)
val bookingId = booking.data?.id ?: 0

// Get details
val detail = client.bookings.getBookingDetails(bookingId)

// Lifecycle
client.bookings.cancelBooking(bookingId, reason = "Customer request")
client.bookings.rescheduleBooking(bookingId, date = "2025-09-20", time = "14:00")

// Cleaner assignment
client.bookings.assignCleaner(bookingId, cleanerId = 789)
client.bookings.removeAssignedCleaner(bookingId)

// Adjust
client.bookings.adjustHours(bookingId, hours = 4.5)
client.bookings.payExpenses(bookingId, paymentMethodId = 55)

// Inspection
val report  = client.bookings.getBookingInspection(bookingId)
val details = client.bookings.getBookingInspectionDetails(bookingId)

// Override checklist for this booking only
client.bookings.assignChecklistToBooking(bookingId, checklistId = 77)

// Post-completion
client.bookings.submitFeedback(bookingId, rating = 5, comment = "Spotless!")
client.bookings.addTip(bookingId, amount = 10.0, paymentMethodId = 55)

// Chat (available ±24 hours of booking start time)
val chat = client.bookings.getChat(bookingId)
client.bookings.sendMessage(bookingId, message = "Cleaner is on the way!")
client.bookings.deleteMessage(bookingId, messageId = "msg-abc-123")
```

---

### Checklists

```kotlin
// List and get
val all       = client.checklists.listChecklists()
val checklist = client.checklists.getChecklist(77)

// Create
val created = client.checklists.createChecklist(
    name  = "Deep Clean",
    items = listOf(
        "Vacuum all carpets",
        "Mop hard floors",
        "Wipe down all surfaces",
        "Clean oven interior",
        "Scrub bathrooms",
    ),
)
val checklistId = created.data?.id ?: 0

// Update (replaces all items)
client.checklists.updateChecklist(checklistId, "Deep Clean v2", listOf("New task 1", "New task 2"))

// Delete
client.checklists.deleteChecklist(checklistId)
```

---

### Payment Methods

```kotlin
// Get Stripe Setup Intent (pass clientSecret to Stripe Android SDK)
val intent = client.paymentMethods.getSetupIntentDetails()
val clientSecret = (intent.data?.get("clientSecret") as? String)

// After Stripe Android SDK confirms and returns pm_xxx:
val method = client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")

// PayPal
val paypal = client.paymentMethods.getPayPalClientToken()

// List / manage
val methods = client.paymentMethods.getPaymentMethods()
client.paymentMethods.setDefaultPaymentMethod(55)
client.paymentMethods.deletePaymentMethod(55)
```

---

### Webhooks

```kotlin
// List
val hooks = client.webhooks.listWebhooks()

// Subscribe
val hook = client.webhooks.createWebhook(
    url   = "https://api.yourapp.com/hooks/cleanster",
    event = "booking.completed",
)

// Update / delete
client.webhooks.updateWebhook(hook.data?.id ?: 0, url = "https://api.yourapp.com/v2/hooks", event = "booking.completed")
client.webhooks.deleteWebhook(hook.data?.id ?: 0)
```

**Supported events:** `booking.created`, `booking.cleaner_assigned`, `booking.cleaner_removed`,
`booking.rescheduled`, `booking.started`, `booking.completed`, `booking.cancelled`, `booking.feedback_submitted`

---

### Blacklist

```kotlin
val blocked = client.blacklist.listBlacklistedCleaners()
client.blacklist.addToBlacklist(cleanerId = 789, reason = "Repeated late arrivals")
client.blacklist.removeFromBlacklist(cleanerId = 789)
```

---

### Reference Data

```kotlin
// Service types
val services = client.other.getServices()

// Plans for a property
val plans = client.other.getPlans(propertyId = 1004)

// Recommended hours
val hours = client.other.getRecommendedHours(propertyId = 1004, roomCount = 2, bathroomCount = 1)
println("Recommended: ${hours.data?.hours}")

// Cost estimate (show price before booking)
val estimate = client.other.getCostEstimate(
    CostEstimateRequest(
        propertyId = 1004,
        planId     = 2,
        hours      = 3.0,
        couponCode = "20POFF",
        extras     = listOf(3),
    )
)
println("Total: ${estimate.data?.total}")

// Cleaning extras
val extras = client.other.getCleaningExtras(serviceId = 1)

// Available cleaners for a time slot
val available = client.other.getAvailableCleaners(
    AvailableCleanersRequest(propertyId = 1004, date = "2025-09-15", time = "09:00")
)

// Coupon codes
val coupons = client.other.getCoupons()
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

> `75POFF` is expired and will return an error.

---

## Running Tests

```bash
cd kotlin-sdk
./gradlew test
```

All 167 tests should pass.

---

## Environments

| | URL |
|---|---|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## Support

- **API Docs:** [Postman](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **Support:** [support@cleanster.com](mailto:support@cleanster.com)
