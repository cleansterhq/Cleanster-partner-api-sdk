# Cleanster Swift SDK

Swift SDK for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep).
Covers all 53 endpoints with async/await, Codable models, and Swift Package Manager support.

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

## Quick Start

```swift
import Cleanster

// Sandbox (development)
let client = CleansterClient.sandbox(accessKey: "your-access-key")

// Production (go-live)
let client = CleansterClient.production(accessKey: "your-access-key")

// Set the per-user JWT (required before all user-scoped calls)
let tokenResponse = try await client.users.fetchAccessToken(12345)
client.setToken(tokenResponse.data?.token ?? "")

// Create a booking
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
        paymentMethodId: 55
    )
)
print("Booking ID:", booking.data?.id ?? 0)
```

---

## Authentication

Every request requires two headers — set once per client instance:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — passed at client construction |
| `token` | Per-user JWT — set via `client.setToken(_:)` |

**Full auth flow:**

```swift
// 1. Create the user (first time only)
let user = try await client.users.createUser(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith"
)
let userId = user.data?.id ?? 0

// 2. Fetch their JWT
let tokenResp = try await client.users.fetchAccessToken(userId)
let jwt = tokenResp.data?.token ?? ""

// 3. Set the token on the client (all subsequent calls use this)
client.setToken(jwt)

// 4. (Optional) verify the token is still valid
let verify = try await client.users.verifyJwt(jwt)
print(verify.message)  // "OK"
```

---

## Error Handling

```swift
do {
    let booking = try await client.bookings.getBookingDetails(99999)
} catch CleansterError.unauthorized {
    print("Invalid access-key or token")
} catch CleansterError.apiError(let statusCode, let message) {
    print("API error \(statusCode): \(message)")
} catch CleansterError.networkError(let underlying) {
    print("Network failure:", underlying.localizedDescription)
} catch {
    print("Unexpected error:", error)
}
```

---

## All Endpoints

### Users

```swift
// Create a user
let user = try await client.users.createUser(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith",
    phone: "+14155551234"   // optional
)

// Fetch user JWT
let resp = try await client.users.fetchAccessToken(userId)
client.setToken(resp.data?.token ?? "")

// Verify JWT
let verify = try await client.users.verifyJwt("eyJhbGciOi...")
```

---

### Properties

```swift
// List all properties
let all = try await client.properties.listProperties()

// Filter by service type
let residential = try await client.properties.listProperties(serviceId: 1)

// Add a property
let prop = try await client.properties.addProperty(
    CreatePropertyRequest(
        name: "Downtown Loft",
        address: "123 Main St",
        city: "Atlanta",
        country: "US",
        roomCount: 2,
        bathroomCount: 1,
        serviceId: 1,
        state: "GA",             // optional
        zip: "30301",            // optional
        timezone: "America/New_York",  // optional
        note: "Ring doorbell",   // optional
        latitude: 33.749,        // optional
        longitude: -84.388       // optional
    )
)
let propertyId = prop.data?.id ?? 0

// Get, update, delete
let detail  = try await client.properties.getProperty(propertyId)
let updated = try await client.properties.updateProperty(propertyId, request: ...)
let deleted = try await client.properties.deleteProperty(propertyId)

// Enable / disable
try await client.properties.enableOrDisableProperty(propertyId, enabled: false)

// Manage property cleaners
let cleaners = try await client.properties.getPropertyCleaners(propertyId)
try await client.properties.addCleanerToProperty(propertyId, cleanerId: 789)
try await client.properties.removeCleanerFromProperty(propertyId, cleanerId: 789)

// iCal sync
try await client.properties.setICalLink(propertyId, icalLink: "https://airbnb.com/cal.ics")
let ical = try await client.properties.getICalLink(propertyId)
try await client.properties.deleteICalLink(propertyId, icalLink: "https://airbnb.com/cal.ics")

// Set default checklist (updateUpcomingBookings also updates scheduled bookings)
try await client.properties.setDefaultChecklist(propertyId, checklistId: 77, updateUpcomingBookings: true)

// Update additional info
try await client.properties.updateAdditionalInformation(propertyId, fields: [
    "parkingInstructions": "Use visitor lot",
    "accessCode": "1234#",
])
```

---

### Bookings

```swift
// List all bookings
let all       = try await client.bookings.getBookings()
let open      = try await client.bookings.getBookings(status: "OPEN")
let completed = try await client.bookings.getBookings(pageNo: 2, status: "COMPLETED")

// Create a booking
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
        couponCode: "20POFF",  // optional
        extras: [3, 7]         // optional extra service IDs
    )
)
let bookingId = booking.data?.id ?? 0

// Get details
let detail = try await client.bookings.getBookingDetails(bookingId)

// Cancel
try await client.bookings.cancelBooking(bookingId, reason: "Customer request")

// Reschedule
try await client.bookings.rescheduleBooking(bookingId, date: "2025-09-20", time: "14:00")

// Cleaner assignment
try await client.bookings.assignCleaner(bookingId, cleanerId: 789)
try await client.bookings.removeAssignedCleaner(bookingId)

// Adjust hours
try await client.bookings.adjustHours(bookingId, hours: 4.5)

// Pay expenses
try await client.bookings.payExpenses(bookingId, paymentMethodId: 55)

// Inspection reports
let report  = try await client.bookings.getBookingInspection(bookingId)
let details = try await client.bookings.getBookingInspectionDetails(bookingId)

// Override checklist for this booking only
try await client.bookings.assignChecklistToBooking(bookingId, checklistId: 77)

// Feedback and tip (post-completion)
try await client.bookings.submitFeedback(bookingId, rating: 5, comment: "Spotless!")
try await client.bookings.addTip(bookingId, amount: 10.0, paymentMethodId: 55)

// Chat (available ±24 hours of booking start)
let chat = try await client.bookings.getChat(bookingId)
try await client.bookings.sendMessage(bookingId, message: "Cleaner is on the way!")
try await client.bookings.deleteMessage(bookingId, messageId: "msg-abc-123")
```

---

### Checklists

```swift
// List and get
let all       = try await client.checklists.listChecklists()
let checklist = try await client.checklists.getChecklist(77)

// Create
let created = try await client.checklists.createChecklist(
    name: "Deep Clean",
    items: [
        "Vacuum all carpets",
        "Mop hard floors",
        "Wipe down all surfaces",
        "Clean oven interior",
        "Scrub bathrooms",
    ]
)
let checklistId = created.data?.id ?? 0

// Update (replaces items entirely)
try await client.checklists.updateChecklist(checklistId, name: "Deep Clean v2", items: ["..."])

// Delete
try await client.checklists.deleteChecklist(checklistId)
```

---

### Payment Methods

```swift
// Get Stripe Setup Intent (pass clientSecret to Stripe iOS SDK)
let intent = try await client.paymentMethods.getSetupIntentDetails()
let clientSecret = (intent.data?.value as? [String: Any])?["clientSecret"] as? String

// After Stripe iOS SDK confirms the setup and returns pm_xxx:
let method = try await client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")

// Get PayPal client token
let paypal = try await client.paymentMethods.getPayPalClientToken()

// List saved methods
let methods = try await client.paymentMethods.getPaymentMethods()

// Set default
try await client.paymentMethods.setDefaultPaymentMethod(55)

// Delete
try await client.paymentMethods.deletePaymentMethod(55)
```

---

### Webhooks

```swift
// List
let hooks = try await client.webhooks.listWebhooks()

// Create — subscribe to an event
let hook = try await client.webhooks.createWebhook(
    url: "https://api.yourapp.com/hooks/cleanster",
    event: "booking.completed"
)
let hookId = hook.data?.id ?? 0

// Update
try await client.webhooks.updateWebhook(hookId, url: "https://api.yourapp.com/v2/hooks", event: "booking.completed")

// Delete
try await client.webhooks.deleteWebhook(hookId)
```

**Supported events:** `booking.created`, `booking.cleaner_assigned`, `booking.cleaner_removed`,
`booking.rescheduled`, `booking.started`, `booking.completed`, `booking.cancelled`, `booking.feedback_submitted`

---

### Blacklist

```swift
// List blacklisted cleaners
let blocked = try await client.blacklist.listBlacklistedCleaners()

// Block a cleaner
try await client.blacklist.addToBlacklist(cleanerId: 789, reason: "Repeated late arrivals")

// Unblock a cleaner
try await client.blacklist.removeFromBlacklist(cleanerId: 789)
```

---

### Reference Data

```swift
// Service types
let services = try await client.other.getServices()

// Plans for a property
let plans = try await client.other.getPlans(propertyId: 1004)

// Recommended hours
let hours = try await client.other.getRecommendedHours(
    propertyId: 1004,
    roomCount: 2,
    bathroomCount: 1
)
print("Recommended hours:", hours.data?.hours ?? 0)

// Cost estimate (show price before booking)
let estimate = try await client.other.getCostEstimate(
    CostEstimateRequest(
        propertyId: 1004,
        planId: 2,
        hours: 3.0,
        couponCode: "20POFF",
        extras: [3]
    )
)
print("Total:", estimate.data?.total ?? 0)

// Cleaning extras (add-on services)
let extras = try await client.other.getCleaningExtras(serviceId: 1)

// Available cleaners for a time slot
let available = try await client.other.getAvailableCleaners(
    AvailableCleanersRequest(propertyId: 1004, date: "2025-09-15", time: "09:00")
)

// Valid coupon codes
let coupons = try await client.other.getCoupons()
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

> `75POFF` is expired — it will return an error.

---

## Running Tests

```bash
cd swift-sdk
swift test
```

All 120 tests should pass.

---

## Environments

| | URL |
|---|---|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```swift
let sandbox    = CleansterClient.sandbox(accessKey: "key")
let production = CleansterClient.production(accessKey: "key")
```

---

## Support

- **API Docs:** [Postman](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **Support:** [support@cleanster.com](mailto:support@cleanster.com)
