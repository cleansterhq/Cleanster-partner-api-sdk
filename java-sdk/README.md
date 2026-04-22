# Cleanster Java SDK

<p align="center">
  <strong>Official Java client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-11%2B-blue?logo=openjdk" alt="Java 11+">
  <img src="https://img.shields.io/badge/Maven-3.6%2B-orange?logo=apache-maven" alt="Maven">
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
- [API Reference](#api-reference)
  - [Bookings](#bookings-clientbookings)
  - [Users](#users-clientusers)
  - [Properties](#properties-clientproperties)
  - [Checklists](#checklists-clientchecklists)
  - [Other / Utilities](#other--utilities-clientother)
  - [Blacklist](#blacklist-clientblacklist)
  - [Payment Methods](#payment-methods-clientpaymentmethods)
  - [Webhooks](#webhooks-clientwebhooks)
- [Error Handling](#error-handling)
- [Response Structure](#response-structure)
- [Models Reference](#models-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Running Tests](#running-tests)
- [Building from Source](#building-from-source)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster Java SDK provides a type-safe, fluent interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It handles:

- **Dual-layer authentication** — partner access key + per-user bearer tokens
- **Typed models** — all requests and responses are strongly typed Java objects
- **Typed exceptions** — distinct exception types for auth errors, API errors, and network errors
- **Environment switching** — single flag toggles between sandbox and production
- **Jackson serialization** — battle-tested JSON handling with `FAIL_ON_UNKNOWN_PROPERTIES` disabled for forward compatibility
- **OkHttp transport** — configurable timeouts, keep-alive, connection pooling

---

## Requirements

| Dependency | Minimum Version |
|-----------|----------------|
| Java (JDK) | 11 |
| Maven | 3.6 |
| OkHttp | 4.11 (included) |
| Jackson Databind | 2.15 (included) |

---

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cleanster</groupId>
    <artifactId>cleanster-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.cleanster:cleanster-java-sdk:1.0.0'
```

### Gradle (Kotlin DSL)

```kotlin
implementation("com.cleanster:cleanster-java-sdk:1.0.0")
```

### Build from Source

```bash
git clone https://github.com/cleanster/cleanster-java-sdk.git
cd cleanster-java-sdk
mvn install -DskipTests
```

The compiled JAR will be at `target/cleanster-java-sdk-1.0.0.jar`.

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** sent as HTTP headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

### How to Authenticate

**Step 1 — Set your partner access key** when creating the client. This is your static partner credential issued by Cleanster.

```java
CleansterClient client = CleansterClient.sandboxClient("your-partner-access-key");
```

**Step 2 — Create or look up a user** in your system. If they are a new user, register them:

```java
CreateUserRequest req = new CreateUserRequest();
req.setEmail("user@example.com");
req.setFirstName("Jane");
req.setLastName("Doe");
req.setPhone("+15551234567"); // optional

ApiResponse<User> response = client.users().createUser(req);
int userId = response.getData().getId();
```

**Step 3 — Fetch the user's bearer token:**

```java
ApiResponse<User> tokenResponse = client.users().fetchAccessToken(userId);
String userToken = tokenResponse.getData().getToken();
```

**Step 4 — Set the token on the client** for all subsequent calls:

```java
client.setAccessToken(userToken);
// All calls from this point forward include the user token
```

> **Note:** The user token is a long-lived token. Store it securely and reuse it on subsequent sessions to avoid re-fetching every time.

---

## Quick Start

```java
import com.cleanster.sdk.client.CleansterClient;
import com.cleanster.sdk.model.*;
import com.cleanster.sdk.exception.*;

public class CleansterDemo {
    public static void main(String[] args) {
        // 1. Initialize the client (use sandboxClient for testing)
        CleansterClient client = CleansterClient.sandboxClient("your-access-key");

        // 2. Register a new user
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setEmail("jane@example.com");
        userReq.setFirstName("Jane");
        userReq.setLastName("Smith");
        ApiResponse<User> userResp = client.users().createUser(userReq);
        int userId = userResp.getData().getId();

        // 3. Fetch and set the user's access token
        String token = client.users().fetchAccessToken(userId).getData().getToken();
        client.setAccessToken(token);

        // 4. Add a property for the user
        CreatePropertyRequest propReq = new CreatePropertyRequest();
        propReq.setName("Beach House");
        propReq.setAddress("123 Ocean Drive");
        propReq.setCity("Miami");
        propReq.setCountry("USA");
        propReq.setRoomCount(3);
        propReq.setBathroomCount(2);
        propReq.setServiceId(1);
        ApiResponse<Property> propResp = client.properties().addProperty(propReq);
        int propertyId = propResp.getData().getId();

        // 5. Check available plans and estimate cost
        ApiResponse<Object> plans = client.other().getPlans(propertyId);

        CostEstimateRequest costReq = new CostEstimateRequest();
        costReq.setPropertyId(propertyId);
        costReq.setPlanId(2);
        costReq.setHours(3.0f);
        ApiResponse<Object> estimate = client.other().calculateCost(costReq);

        // 6. Create a booking
        CreateBookingRequest bookingReq = new CreateBookingRequest();
        bookingReq.setDate("2025-06-15");
        bookingReq.setTime("10:00");
        bookingReq.setPropertyId(propertyId);
        bookingReq.setRoomCount(3);
        bookingReq.setBathroomCount(2);
        bookingReq.setPlanId(2);
        bookingReq.setHours(3.0f);
        bookingReq.setExtraSupplies(false);
        bookingReq.setPaymentMethodId(10);

        ApiResponse<Booking> bookingResp = client.bookings().createBooking(bookingReq);
        int bookingId = bookingResp.getData().getId();
        System.out.println("Created booking #" + bookingId);

        // 7. List all bookings
        ApiResponse<Object> allBookings = client.bookings().getBookings(1, null);
        System.out.println("Status: " + allBookings.getStatus());
    }
}
```

---

## Configuration

### Using Factory Methods (Recommended)

```java
// Sandbox — connects to the sandbox environment (for development & testing)
CleansterClient client = CleansterClient.sandboxClient("your-access-key");

// Production — connects to the live environment
CleansterClient client = CleansterClient.productionClient("your-access-key");
```

### Using the Builder (Custom Configuration)

```java
import com.cleanster.sdk.client.CleansterConfig;
import com.cleanster.sdk.client.CleansterClient;

CleansterConfig config = CleansterConfig.sandboxBuilder("your-access-key")
    .connectTimeoutSeconds(10)  // default: 30
    .readTimeoutSeconds(60)     // default: 30
    .writeTimeoutSeconds(30)    // default: 30
    .build();

CleansterClient client = new CleansterClient(config);
```

### Base URLs

| Environment | Base URL |
|-------------|----------|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

You can also supply a fully custom base URL:

```java
CleansterConfig config = new CleansterConfig.Builder("your-access-key")
    .baseUrl("https://your-custom-endpoint.example.com")
    .build();
```

---

## API Reference

Every API method returns an `ApiResponse<T>` wrapper (see [Response Structure](#response-structure)).

---

### Bookings (`client.bookings()`)

The booking API manages the full lifecycle of a cleaning appointment.

#### `getBookings(Integer pageNo, String status)`

Retrieve a paginated list of bookings. Both parameters are optional — pass `null` to omit.

```java
// All bookings, page 1
ApiResponse<Object> all = client.bookings().getBookings(null, null);

// Completed bookings only
ApiResponse<Object> completed = client.bookings().getBookings(1, "COMPLETED");

// Page 2 of all open bookings
ApiResponse<Object> page2Open = client.bookings().getBookings(2, "OPEN");
```

**Valid status values:** `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED`

---

#### `createBooking(CreateBookingRequest request)`

Schedule a new cleaning appointment.

```java
CreateBookingRequest req = new CreateBookingRequest();
req.setDate("2025-06-15");          // Required — format: YYYY-MM-DD
req.setTime("10:00");               // Required — format: HH:mm (24-hour)
req.setPropertyId(1004);            // Required — the property to clean
req.setRoomCount(2);                // Required
req.setBathroomCount(1);            // Required
req.setPlanId(5);                   // Required — plan from getPlans()
req.setHours(3.0f);                 // Required — recommended from getRecommendedHours()
req.setExtraSupplies(false);        // Required — whether to include cleaning supplies
req.setPaymentMethodId(10);         // Required — payment method ID

ApiResponse<Booking> response = client.bookings().createBooking(req);
Booking booking = response.getData();
System.out.println("Booking ID: " + booking.getId());
System.out.println("Status: " + booking.getStatus());
```

---

#### `getBookingDetails(int bookingId)`

Retrieve full details of a specific booking.

```java
ApiResponse<Booking> response = client.bookings().getBookingDetails(16926);
Booking booking = response.getData();
System.out.println("Assigned cleaner: " + booking.getCleanerId());
System.out.println("Total cost: " + booking.getCost());
```

---

#### `cancelBooking(int bookingId, CancelBookingRequest request)`

Cancel a booking with an optional reason.

```java
CancelBookingRequest req = new CancelBookingRequest("Changed my schedule");
client.bookings().cancelBooking(16459, req);
```

---

#### `rescheduleBooking(int bookingId, RescheduleBookingRequest request)`

Move a booking to a different date and time.

```java
RescheduleBookingRequest req = new RescheduleBookingRequest("2025-07-01", "14:00");
client.bookings().rescheduleBooking(16459, req);
```

---

#### `assignCleaner(int bookingId, CleanerAssignmentRequest request)`

Manually assign a specific cleaner to a booking.

```java
CleanerAssignmentRequest req = new CleanerAssignmentRequest(5); // cleaner ID
client.bookings().assignCleaner(16459, req);
```

---

#### `removeAssignedCleaner(int bookingId)`

Remove the currently assigned cleaner (returns booking to unassigned state).

```java
client.bookings().removeAssignedCleaner(16459);
```

---

#### `adjustHours(int bookingId, AdjustHoursRequest request)`

Change the number of hours for a booking.

```java
AdjustHoursRequest req = new AdjustHoursRequest(4.0f);
client.bookings().adjustHours(16459, req);
```

---

#### `payExpenses(int bookingId, PayExpensesRequest request)`

Pay any outstanding expenses on a completed booking. Must be called within 72 hours of completion.

```java
PayExpensesRequest req = new PayExpensesRequest();
req.setPaymentMethodId(10);
client.bookings().payExpenses(16926, req);
```

---

#### `getBookingInspection(int bookingId)` / `getBookingInspectionDetails(int bookingId)`

Retrieve the cleaner's inspection report for a completed booking.

```java
ApiResponse<Object> inspection = client.bookings().getBookingInspection(16926);
ApiResponse<Object> details = client.bookings().getBookingInspectionDetails(16926);
```

---

#### `assignChecklistToBooking(int bookingId, int checklistId)`

Attach a cleaning checklist to a specific booking.

```java
client.bookings().assignChecklistToBooking(16926, 105);
```

---

#### `submitFeedback(int bookingId, FeedbackRequest request)`

Submit a star rating and comment after a booking is completed.

```java
FeedbackRequest req = new FeedbackRequest(5, "Excellent work — very thorough!");
client.bookings().submitFeedback(16926, req);
```

**Rating:** Integer 1–5.

---

#### `addTip(int bookingId, TipRequest request)`

Add a tip for the cleaner. Must be called within 72 hours of booking completion.

```java
TipRequest req = new TipRequest(20.0f, 10); // amount, paymentMethodId
client.bookings().addTip(16926, req);
```

---

#### `getChat(int bookingId)` / `sendMessage(int bookingId, SendMessageRequest)` / `deleteMessage(int bookingId, String messageId)`

Interact with the in-booking chat thread between the partner and cleaner.

```java
// Get all messages
ApiResponse<Object> chat = client.bookings().getChat(17142);

// Send a message
SendMessageRequest msg = new SendMessageRequest("Please focus on the kitchen today.");
client.bookings().sendMessage(17142, msg);

// Delete a specific message
client.bookings().deleteMessage(17142, "msg-abc-123");
```

---

**Booking API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getBookings(pageNo, status)` | GET | `/v1/bookings` |
| `createBooking(req)` | POST | `/v1/bookings/create` |
| `getBookingDetails(id)` | GET | `/v1/bookings/{id}` |
| `cancelBooking(id, req)` | POST | `/v1/bookings/{id}/cancel` |
| `rescheduleBooking(id, req)` | POST | `/v1/bookings/{id}/reschedule` |
| `assignCleaner(id, req)` | POST | `/v1/bookings/{id}/cleaner` |
| `removeAssignedCleaner(id)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `adjustHours(id, req)` | POST | `/v1/bookings/{id}/hours` |
| `payExpenses(id, req)` | POST | `/v1/bookings/{id}/expenses` |
| `getBookingInspection(id)` | GET | `/v1/bookings/{id}/inspection` |
| `getBookingInspectionDetails(id)` | GET | `/v1/bookings/{id}/inspection/details` |
| `assignChecklistToBooking(id, cid)` | PUT | `/v1/bookings/{id}/checklist/{cid}` |
| `submitFeedback(id, req)` | POST | `/v1/bookings/{id}/feedback` |
| `addTip(id, req)` | POST | `/v1/bookings/{id}/tip` |
| `getChat(id)` | GET | `/v1/bookings/{id}/chat` |
| `sendMessage(id, req)` | POST | `/v1/bookings/{id}/chat` |
| `deleteMessage(id, msgId)` | DELETE | `/v1/bookings/{id}/chat/{msgId}` |

---

### Users (`client.users()`)

Manage end-user accounts that bookings are made under.

#### `createUser(CreateUserRequest request)`

```java
CreateUserRequest req = new CreateUserRequest();
req.setEmail("jane@example.com");    // Required
req.setFirstName("Jane");            // Required
req.setLastName("Smith");            // Required
req.setPhone("+15551234567");        // Optional

ApiResponse<User> response = client.users().createUser(req);
User user = response.getData();
System.out.println("User ID: " + user.getId());
System.out.println("Email: " + user.getEmail());
```

---

#### `fetchAccessToken(int userId)`

Fetch the long-lived bearer token for a user. Use this token in `client.setAccessToken()`.

```java
ApiResponse<User> response = client.users().fetchAccessToken(42);
String token = response.getData().getToken();
client.setAccessToken(token);
```

---

#### `verifyJwt(VerifyJwtRequest request)`

Verify that a JWT token is valid and has not expired.

```java
VerifyJwtRequest req = new VerifyJwtRequest("eyJhbGci...");
ApiResponse<Object> result = client.users().verifyJwt(req);
```

---

**Users API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `createUser(req)` | POST | `/v1/user/account` |
| `fetchAccessToken(userId)` | GET | `/v1/user/access-token/{userId}` |
| `verifyJwt(req)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`client.properties()`)

Properties represent the physical locations where cleanings take place.

#### `listProperties(Integer serviceId)`

```java
// All properties
ApiResponse<Object> all = client.properties().listProperties(null);

// Properties for a specific service type
ApiResponse<Object> residential = client.properties().listProperties(1);
```

---

#### `addProperty(CreatePropertyRequest request)`

```java
CreatePropertyRequest req = new CreatePropertyRequest();
req.setName("Downtown Condo");       // Required
req.setAddress("456 Main St");       // Required
req.setCity("Toronto");              // Required
req.setCountry("Canada");            // Required
req.setRoomCount(2);                 // Required
req.setBathroomCount(1);             // Required
req.setServiceId(1);                 // Required — from getServices()

ApiResponse<Property> response = client.properties().addProperty(req);
Property property = response.getData();
System.out.println("Property ID: " + property.getId());
```

---

#### `getProperty(int propertyId)` / `updateProperty(int propertyId, CreatePropertyRequest request)` / `deleteProperty(int propertyId)`

```java
// Get
ApiResponse<Property> prop = client.properties().getProperty(1040);

// Update
CreatePropertyRequest update = new CreatePropertyRequest();
update.setName("Updated Name");
update.setRoomCount(3);
client.properties().updateProperty(1040, update);

// Delete
client.properties().deleteProperty(1040);
```

---

#### `enableOrDisableProperty(int propertyId, EnableDisablePropertyRequest request)`

Toggle a property's active/inactive state.

```java
EnableDisablePropertyRequest req = new EnableDisablePropertyRequest();
req.setEnabled(false); // disable the property
client.properties().enableOrDisableProperty(1040, req);
```

---

#### Property Cleaners

```java
// List cleaners assigned to a property
ApiResponse<Object> cleaners = client.properties().getPropertyCleaners(1040);

// Assign a cleaner to a property
AssignCleanerToPropertyRequest req = new AssignCleanerToPropertyRequest();
req.setCleanerId(5);
client.properties().assignCleanerToProperty(1040, req);

// Unassign a cleaner
client.properties().unassignCleanerFromProperty(1040, 5);
```

---

#### iCal Calendar Integration

Sync a property's availability with an external calendar (Airbnb, VRBO, etc.).

```java
// Add iCal link
ICalRequest req = new ICalRequest("https://calendar.example.com/feed.ics");
client.properties().addICalLink(1040, req);

// Get current iCal link
ApiResponse<Object> ical = client.properties().getICalLink(1040);

// Remove iCal link
client.properties().removeICalLink(1040, req);
```

---

#### `assignChecklistToProperty(int propertyId, int checklistId, boolean updateUpcomingBookings)`

Attach a default checklist to a property. If `updateUpcomingBookings` is `true`, all future bookings for this property will automatically use this checklist.

```java
client.properties().assignChecklistToProperty(1040, 105, true);
```

---

**Properties API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listProperties(serviceId)` | GET | `/v1/properties` |
| `addProperty(req)` | POST | `/v1/properties` |
| `getProperty(id)` | GET | `/v1/properties/{id}` |
| `updateProperty(id, req)` | PUT | `/v1/properties/{id}` |
| `updateAdditionalInformation(id, data)` | PUT | `/v1/properties/{id}/additional-information` |
| `enableOrDisableProperty(id, req)` | POST | `/v1/properties/{id}/enable-disable` |
| `deleteProperty(id)` | DELETE | `/v1/properties/{id}` |
| `getPropertyCleaners(id)` | GET | `/v1/properties/{id}/cleaners` |
| `assignCleanerToProperty(id, req)` | POST | `/v1/properties/{id}/cleaners` |
| `unassignCleanerFromProperty(id, cleanerId)` | DELETE | `/v1/properties/{id}/cleaners/{cleanerId}` |
| `addICalLink(id, req)` | PUT | `/v1/properties/{id}/ical` |
| `getICalLink(id)` | GET | `/v1/properties/{id}/ical` |
| `removeICalLink(id, req)` | DELETE | `/v1/properties/{id}/ical` |
| `assignChecklistToProperty(id, cid, flag)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`client.checklists()`)

Checklists define the tasks a cleaner should complete during a booking. They can be assigned to properties (default) or individual bookings (override).

#### `listChecklists()`

```java
ApiResponse<Object> all = client.checklists().listChecklists();
```

---

#### `getChecklist(int checklistId)`

```java
ApiResponse<Checklist> checklist = client.checklists().getChecklist(105);
System.out.println("Name: " + checklist.getData().getName());
checklist.getData().getItems().forEach(item ->
    System.out.println(" - " + item.getDescription())
);
```

---

#### `createChecklist(CreateChecklistRequest request)`

```java
import java.util.Arrays;

CreateChecklistRequest req = new CreateChecklistRequest(
    "Standard Residential Clean",
    Arrays.asList(
        "Vacuum all floors",
        "Mop kitchen and bathroom floors",
        "Clean stovetop and oven exterior",
        "Wipe all countertops",
        "Scrub toilets, sinks, and tubs",
        "Empty all trash bins",
        "Wipe mirrors and glass surfaces"
    )
);
ApiResponse<Checklist> response = client.checklists().createChecklist(req);
System.out.println("Created checklist ID: " + response.getData().getId());
```

---

#### `updateChecklist(int checklistId, CreateChecklistRequest request)` / `deleteChecklist(int checklistId)`

```java
// Update
CreateChecklistRequest updated = new CreateChecklistRequest("Deep Clean", Arrays.asList("..."));
client.checklists().updateChecklist(105, updated);

// Delete
client.checklists().deleteChecklist(105);
```

---

**Checklists API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listChecklists()` | GET | `/v1/checklist` |
| `getChecklist(id)` | GET | `/v1/checklist/{id}` |
| `createChecklist(req)` | POST | `/v1/checklist` |
| `updateChecklist(id, req)` | PUT | `/v1/checklist/{id}` |
| `deleteChecklist(id)` | DELETE | `/v1/checklist/{id}` |
| `uploadImage(bytes, mime)` | POST | `/v1/checklist/upload-image` |

---

### Other / Utilities (`client.other()`)

Utility endpoints for reference data needed when building booking flows.

#### `getServices()`

Returns all available cleaning service types (e.g. Residential, Commercial, Airbnb).

```java
ApiResponse<Object> services = client.other().getServices();
```

---

#### `getPlans(int propertyId)`

Returns all available booking plans for a given property (e.g. Standard Clean, Deep Clean, Move-In/Move-Out).

```java
ApiResponse<Object> plans = client.other().getPlans(1004);
```

---

#### `getRecommendedHours(int propertyId, int bathroomCount, int roomCount)`

Returns the system's recommended number of cleaning hours based on property size. Use this to pre-fill the hours field when creating a booking.

```java
ApiResponse<Object> hours = client.other().getRecommendedHours(1004, 2, 3);
```

---

#### `calculateCost(CostEstimateRequest request)`

Calculate the estimated price for a booking before committing. Use this to show a cost preview to your users.

```java
CostEstimateRequest req = new CostEstimateRequest();
req.setPropertyId(1004);
req.setPlanId(2);
req.setHours(3.0f);
req.setCouponCode("20POFF"); // optional

ApiResponse<Object> estimate = client.other().calculateCost(req);
```

---

#### `getCleaningExtras(int serviceId)`

Returns available add-on services for a given service type (e.g., inside fridge, inside oven, laundry).

```java
ApiResponse<Object> extras = client.other().getCleaningExtras(1);
```

---

#### `getAvailableCleaners(AvailableCleanersRequest request)`

Find cleaners available for a specific property, date, and time slot.

```java
AvailableCleanersRequest req = new AvailableCleanersRequest();
req.setPropertyId(1004);
req.setDate("2025-06-15");
req.setTime("10:00");
ApiResponse<Object> cleaners = client.other().getAvailableCleaners(req);
```

---

#### `getCoupons()`

Returns all valid coupon codes available for use at booking creation.

```java
ApiResponse<Object> coupons = client.other().getCoupons();
```

---

**Other API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getServices()` | GET | `/v1/services` |
| `getPlans(propertyId)` | GET | `/v1/plans?propertyId={id}` |
| `getRecommendedHours(pId, baths, rooms)` | GET | `/v1/recommended-hours` |
| `calculateCost(req)` | POST | `/v1/cost-estimate` |
| `getCleaningExtras(serviceId)` | GET | `/v1/cleaning-extras/{serviceId}` |
| `getAvailableCleaners(req)` | POST | `/v1/available-cleaners` |
| `getCoupons()` | GET | `/v1/coupons` |

---

### Blacklist (`client.blacklist()`)

The blacklist prevents specific cleaners from being assigned to your bookings.

```java
// List all blacklisted cleaners
ApiResponse<Object> list = client.blacklist().listBlacklistedCleaners();

// Add a cleaner to the blacklist
BlacklistRequest addReq = new BlacklistRequest(7, "Damaged furniture during last booking");
client.blacklist().addToBlacklist(addReq);

// Remove a cleaner from the blacklist
BlacklistRequest removeReq = new BlacklistRequest(7, null);
client.blacklist().removeFromBlacklist(removeReq);
```

**Blacklist API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listBlacklistedCleaners()` | GET | `/v1/blacklist/cleaner` |
| `addToBlacklist(req)` | POST | `/v1/blacklist/cleaner` |
| `removeFromBlacklist(req)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`client.paymentMethods()`)

Manage user payment methods. The API supports Stripe (credit/debit card) and PayPal.

```java
// Get Stripe setup intent (use client-side to collect card details)
ApiResponse<Object> setupIntent = client.paymentMethods().getSetupIntentDetails();

// Get PayPal client token (use client-side to render PayPal button)
ApiResponse<Object> paypalToken = client.paymentMethods().getPaypalClientToken();

// Add a payment method (after client-side tokenization)
AddPaymentMethodRequest addReq = new AddPaymentMethodRequest();
addReq.setPaymentMethodId("pm_xxxxxxxxxxxx"); // Stripe payment method ID
client.paymentMethods().addPaymentMethod(addReq);

// List all saved payment methods
ApiResponse<Object> methods = client.paymentMethods().getPaymentMethods();

// Set a payment method as the default
client.paymentMethods().setDefaultPaymentMethod(193);

// Delete a payment method
client.paymentMethods().deletePaymentMethod(193);
```

**Payment Methods API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getSetupIntentDetails()` | GET | `/v1/payment-methods/setup-intent-details` |
| `getPaypalClientToken()` | GET | `/v1/payment-methods/paypal-client-token` |
| `addPaymentMethod(req)` | POST | `/v1/payment-methods` |
| `getPaymentMethods()` | GET | `/v1/payment-methods` |
| `deletePaymentMethod(id)` | DELETE | `/v1/payment-methods/{id}` |
| `setDefaultPaymentMethod(id)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`client.webhooks()`)

Webhooks allow your server to receive real-time notifications when booking events occur, eliminating the need to poll the API.

```java
// List all configured webhooks
ApiResponse<Object> list = client.webhooks().listWebhooks();

// Register a new webhook
java.util.Map<String, Object> webhookConfig = new java.util.HashMap<>();
webhookConfig.put("url", "https://your-app.com/webhooks/cleanster");
webhookConfig.put("event", "booking.status_changed");
ApiResponse<Object> created = client.webhooks().createWebhook(webhookConfig);

// Update a webhook
webhookConfig.put("url", "https://your-app.com/webhooks/cleanster-v2");
client.webhooks().updateWebhook(50, webhookConfig);

// Delete a webhook
client.webhooks().deleteWebhook(50);
```

**Webhooks API Summary Table**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listWebhooks()` | GET | `/v1/webhooks` |
| `createWebhook(req)` | POST | `/v1/webhooks` |
| `updateWebhook(id, req)` | PUT | `/v1/webhooks/{id}` |
| `deleteWebhook(id)` | DELETE | `/v1/webhooks/{id}` |

---

## Error Handling

The SDK uses a three-level exception hierarchy. Always handle the most specific exceptions first.

```java
import com.cleanster.sdk.exception.CleansterAuthException;
import com.cleanster.sdk.exception.CleansterApiException;
import com.cleanster.sdk.exception.CleansterException;

try {
    ApiResponse<Booking> booking = client.bookings().getBookingDetails(99999);

} catch (CleansterAuthException e) {
    // HTTP 401 — your access key or user token is invalid/missing
    System.err.println("Authentication failed: " + e.getMessage());
    System.err.println("Check your access key and user token.");

} catch (CleansterApiException e) {
    // HTTP 4xx / 5xx — the API returned an error response
    System.err.println("API error [HTTP " + e.getStatusCode() + "]: " + e.getMessage());
    System.err.println("Raw response: " + e.getResponseBody());

    if (e.getStatusCode() == 404) {
        System.err.println("Resource not found.");
    } else if (e.getStatusCode() == 422) {
        System.err.println("Validation error — check your request fields.");
    } else if (e.getStatusCode() >= 500) {
        System.err.println("Server error — try again later.");
    }

} catch (CleansterException e) {
    // Network failure, timeout, JSON parse error, etc.
    System.err.println("SDK/network error: " + e.getMessage());
    e.printStackTrace();
}
```

### Exception Hierarchy

```
CleansterException (base)
├── CleansterAuthException   — HTTP 401 (invalid/missing credentials)
└── CleansterApiException    — HTTP 4xx / 5xx (API-level errors)
```

| Exception | When Thrown | Key Fields |
|-----------|-------------|------------|
| `CleansterException` | Network errors, timeouts, serialization failures | `getMessage()` |
| `CleansterAuthException` | HTTP 401 — bad access key or user token | `getMessage()`, `getStatusCode()` (always 401), `getResponseBody()` |
| `CleansterApiException` | Non-2xx HTTP response other than 401 | `getMessage()`, `getStatusCode()`, `getResponseBody()` |

---

## Response Structure

All API methods return an `ApiResponse<T>` object:

```java
public class ApiResponse<T> {
    private Integer status;   // HTTP-style status code (e.g., 200)
    private String message;   // Human-readable status message
    private T data;           // The response payload (typed)
}
```

**Example — checking the response:**

```java
ApiResponse<Booking> response = client.bookings().getBookingDetails(16926);

System.out.println("Status: " + response.getStatus());    // 200
System.out.println("Message: " + response.getMessage());  // "OK"

Booking booking = response.getData();
System.out.println("Booking #" + booking.getId());
System.out.println("Status: " + booking.getStatus());
System.out.println("Hours: " + booking.getHours());
System.out.println("Cost: $" + booking.getCost());
```

---

## Models Reference

### `Booking`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Integer` | Booking ID |
| `status` | `String` | `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |
| `date` | `String` | Date of booking (YYYY-MM-DD) |
| `time` | `String` | Start time (HH:mm) |
| `hours` | `Float` | Duration in hours |
| `cost` | `Float` | Total cost |
| `propertyId` | `Integer` | Associated property ID |
| `cleanerId` | `Integer` | Assigned cleaner ID (null if unassigned) |
| `planId` | `Integer` | Booking plan ID |
| `roomCount` | `Integer` | Number of rooms |
| `bathroomCount` | `Integer` | Number of bathrooms |

### `User`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Integer` | User ID |
| `email` | `String` | Email address |
| `firstName` | `String` | First name |
| `lastName` | `String` | Last name |
| `phone` | `String` | Phone number |
| `token` | `String` | Bearer token (from `fetchAccessToken`) |

### `Property`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Integer` | Property ID |
| `name` | `String` | Property name/label |
| `address` | `String` | Street address |
| `city` | `String` | City |
| `country` | `String` | Country |
| `roomCount` | `Integer` | Number of rooms |
| `bathroomCount` | `Integer` | Number of bathrooms |

### `Checklist`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Integer` | Checklist ID |
| `name` | `String` | Checklist name |
| `items` | `List<ChecklistItem>` | Task items |

### `Checklist.ChecklistItem`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Integer` | Item ID |
| `description` | `String` | Task description |
| `isCompleted` | `Boolean` | Whether the cleaner marked it done |
| `imageUrl` | `String` | Proof photo URL (if uploaded) |

---

## Sandbox vs Production

| Feature | Sandbox | Production |
|---------|---------|------------|
| Real charges | No | Yes |
| Real cleaners | No | Yes |
| Coupon codes | Test codes work | Real codes only |
| API base URL | `partner-sandbox-dot-...` | `partner-dot-...` |
| Data persisted | Yes (sandbox DB) | Yes (production DB) |

> **Always use the sandbox environment during development.** Switch to production only when you are ready to go live.

---

## Test Coupon Codes (Sandbox Only)

Use these coupon codes in the sandbox to test discount scenarios. They do not work in production.

| Code | Discount | Use Case |
|------|----------|----------|
| `100POFF` | 100% off — free booking | Test zero-cost flows |
| `50POFF` | 50% off | Test percentage discount |
| `20POFF` | 20% off | Test small percentage discount |
| `200OFF` | $200 flat discount | Test flat-rate discount |
| `100OFF` | $100 flat discount | Test partial flat discount |

Pass the coupon code in `CostEstimateRequest.setCouponCode()` or in `CreateBookingRequest` if supported.

---

## Running Tests

The test suite contains **74 unit tests** using JUnit 5 and Mockito. No network access or API keys are required.

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=CleansterClientTest

# Run a specific test method
mvn test -Dtest=CleansterClientTest#createBooking

# Run tests with verbose output
mvn test -Dsurefire.useFile=false
```

**Test coverage includes:**
- Configuration validation (null/blank access key rejection, URL assignment, timeouts)
- All 8 API classes — correct HTTP method, URL path, and request body for every endpoint
- Query parameter construction (pageNo, status, serviceId, propertyId, etc.)
- Response deserialization and data field mapping
- Exception propagation (`CleansterAuthException`, `CleansterApiException`)
- All request and response model getters/setters

---

## Building from Source

```bash
# Clone the repository
git clone https://github.com/cleanster/cleanster-java-sdk.git
cd cleanster-java-sdk

# Compile
mvn compile

# Run tests
mvn test

# Package (produces JAR + sources JAR + Javadoc JAR)
mvn package

# Install into local Maven repository
mvn install

# Skip tests during build
mvn package -DskipTests

# Generate Javadoc only
mvn javadoc:jar
```

**Output artifacts in `target/`:**

| File | Description |
|------|-------------|
| `cleanster-java-sdk-1.0.0.jar` | Main library JAR (~85 KB) |
| `cleanster-java-sdk-1.0.0-sources.jar` | Source code JAR (~32 KB) |
| `cleanster-java-sdk-1.0.0-javadoc.jar` | Javadoc JAR (~346 KB) |

---

## Project Structure

```
cleanster-java-sdk/
├── pom.xml
├── README.md
├── LICENSE
├── CHANGELOG.md
└── src/
    ├── main/java/com/cleanster/sdk/
    │   ├── api/
    │   │   ├── BookingApi.java
    │   │   ├── UserApi.java
    │   │   ├── PropertyApi.java
    │   │   ├── ChecklistApi.java
    │   │   ├── OtherApi.java
    │   │   ├── BlacklistApi.java
    │   │   ├── PaymentMethodApi.java
    │   │   └── WebhookApi.java
    │   ├── client/
    │   │   ├── CleansterClient.java   ← Main entry point
    │   │   ├── CleansterConfig.java   ← Configuration builder
    │   │   └── HttpClient.java        ← OkHttp wrapper
    │   ├── exception/
    │   │   ├── CleansterException.java
    │   │   ├── CleansterAuthException.java
    │   │   └── CleansterApiException.java
    │   └── model/
    │       ├── ApiResponse.java
    │       ├── Booking.java
    │       ├── User.java
    │       ├── Property.java
    │       ├── Checklist.java
    │       ├── PaymentMethod.java
    │       └── ... (28 model classes total)
    └── test/java/com/cleanster/sdk/
        └── CleansterClientTest.java   ← 74 unit tests
```

---

## License

This SDK is released under the [MIT License](LICENSE). You are free to use, modify, and distribute it in personal and commercial projects.

---

## Support

| Resource | Link |
|----------|------|
| API Documentation | https://documenter.getpostman.com/view/26172658/2sAYdoF7ep |
| Partner Support | partner@cleanster.com |
| General Support | support@cleanster.com |
| GitHub Issues | https://github.com/cleanster/cleanster-java-sdk/issues |

---

*Made with care for the Cleanster partner ecosystem.*
