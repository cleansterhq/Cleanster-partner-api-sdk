# Cleanster Java SDK

<p align="center">
  <strong>Official Java client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-11%2B-blue?logo=openjdk" alt="Java 11+">
  <img src="https://img.shields.io/badge/Maven-3.6%2B-orange?logo=apache-maven" alt="Maven">
  <img src="https://img.shields.io/badge/tests-74%20passing-brightgreen" alt="74 passing">
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

The Cleanster Java SDK provides a type-safe, fluent interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). Use it to:

- **Create and manage bookings** — schedule cleanings, reschedule, cancel, adjust hours
- **Manage properties** — create locations, manage iCal integrations, assign preferred cleaners
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create, assign, and manage cleaning task lists
- **Process payments** — attach Stripe or PayPal payment methods, set defaults
- **Receive webhooks** — subscribe to real-time booking lifecycle events
- **Blacklist cleaners** — block specific cleaners from your properties

---

## Requirements

- **Java 11** or later
- **Maven 3.6+** or **Gradle 7+**
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

### Maven

```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle (Groovy DSL)

```groovy
implementation 'com.cleanster:cleanster-sdk:1.0.0'
```

### Gradle (Kotlin DSL)

```kotlin
implementation("com.cleanster:cleanster-sdk:1.0.0")
```

### Build from Source

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/java-sdk
mvn install -DskipTests
```

---

## Authentication

Every request to the Cleanster API requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key. Issued by Cleanster — contact [partner@cleanster.com](mailto:partner@cleanster.com). |
| `token` | A per-user JWT. Long-lived; obtained via the `/v1/user/access-token/{userId}` endpoint. |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```java
CleansterClient client = new CleansterClient("your-access-key");

ApiResponse<Object> resp = client.users().createUser(
    "jane@example.com",   // email
    "Jane",               // first name
    "Doe",                // last name
    "+15551234567"        // phone
);
// resp.getData() contains userId and account details
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```java
int userId = 42; // from the createUser response
ApiResponse<Object> tokenResp = client.users().fetchAccessToken(userId);
Map<?, ?> data = (Map<?, ?>) tokenResp.getData();
String userToken = (String) data.get("token");
```

**Step 4 — Build the client with both credentials**:

```java
CleansterClient client = new CleansterClient("your-access-key", userToken);
// All subsequent calls automatically include both headers
```

> **Token lifecycle:** Tokens are long-lived. Only refresh when the API returns HTTP 401 on a user endpoint.

---

## Quick Start

```java
import com.cleanster.sdk.client.CleansterClient;
import com.cleanster.sdk.model.*;
import java.util.Arrays;

public class QuickStart {
    public static void main(String[] args) throws Exception {
        // 1. Initialize client
        CleansterClient client = new CleansterClient("your-access-key", "user-jwt-token");

        // 2. List available services
        ApiResponse<Object> services = client.other().getServices();
        System.out.println("Services: " + services.getData());

        // 3. Get recommended cleaning hours
        ApiResponse<Object> hours = client.other().getRecommendedHours(1004, 2, 3);
        System.out.println("Recommended hours: " + hours.getData());

        // 4. Create a booking
        CreateBookingRequest req = new CreateBookingRequest();
        req.setPropertyId(1004);
        req.setDate("2025-09-01");
        req.setTime("10:00");
        req.setPlanId(2);
        req.setRoomCount(3);
        req.setBathroomCount(2);
        req.setHours(3.0);
        req.setExtraSupplies(false);
        req.setPaymentMethodId(10);
        req.setCouponCode("20POFF"); // optional — 20% off in sandbox

        ApiResponse<Booking> booking = client.bookings().createBooking(req);
        System.out.println("Created booking: " + booking.getData().getId());

        // 5. List open bookings
        GetBookingsParams params = new GetBookingsParams();
        params.setPageNo(1);
        params.setStatus("OPEN");
        ApiResponse<BookingList> list = client.bookings().getBookings(params);
        System.out.println("Open bookings: " + list.getData().getBookings().size());
    }
}
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```java
// Sandbox (default)
CleansterClient client = new CleansterClient("key", "token");

// Explicit sandbox
CleansterClient client = new CleansterClient("key", "token",
    CleansterClient.Environment.SANDBOX);

// Production
CleansterClient client = new CleansterClient("key", "token",
    CleansterClient.Environment.PRODUCTION);
```

---

## Booking Flow

```
POST /v1/bookings/create  ──►  OPEN
                                 │
        POST /v1/bookings/{id}/cleaner
                                 │
                                 ▼
                       CLEANER_ASSIGNED
                                 │
                    Cleaner starts the job
                                 │
                    ┌────────────┴────────────┐
                    ▼                         ▼
             COMPLETED                  CANCELLED
```

Booking status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All SDK methods return `ApiResponse<T>` with:
- `getStatus()` — HTTP status code (int)
- `getMessage()` — Human-readable result string
- `getData()` — Typed response payload

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

Retrieve a paginated list of bookings, optionally filtered by status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | int | Yes | Page number (1-based) |
| `status` | String | No | Filter: `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |

```java
GetBookingsParams params = new GetBookingsParams();
params.setPageNo(1);
params.setStatus("OPEN"); // optional

ApiResponse<BookingList> resp = client.bookings().getBookings(params);
List<Booking> bookings = resp.getData().getBookings();
int totalPages = resp.getData().getTotalPages();
```

**Response fields:**

| Field | Type | Description |
|---|---|---|
| `data.bookings` | `List<Booking>` | Array of booking objects |
| `data.totalPages` | int | Total number of pages |
| `data.currentPage` | int | Current page number |

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

Retrieve full details for a single booking.

```java
ApiResponse<Booking> resp = client.bookings().getBooking(16926);
Booking b = resp.getData();
System.out.println(b.getStatus() + " on " + b.getDate());
```

---

#### Create Booking
**`POST /v1/bookings/create`**

Schedule a new cleaning appointment.

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | int | Yes | Property to be cleaned |
| `date` | String | Yes | Date in `YYYY-MM-DD` format |
| `time` | String | Yes | Start time in `HH:MM` (24-hour) |
| `planId` | int | Yes | Cleaning plan ID (from `GET /v1/plans`) |
| `roomCount` | int | Yes | Number of rooms |
| `bathroomCount` | int | Yes | Number of bathrooms |
| `hours` | double | Yes | Scheduled duration in hours |
| `extraSupplies` | boolean | Yes | Whether to supply cleaning materials |
| `paymentMethodId` | int | Yes | Payment method to charge |
| `couponCode` | String | No | Discount coupon code |
| `cleaningExtras` | List | No | List of extra service IDs |

```java
CreateBookingRequest req = new CreateBookingRequest();
req.setPropertyId(1004);
req.setDate("2025-09-01");
req.setTime("10:00");
req.setPlanId(2);
req.setRoomCount(3);
req.setBathroomCount(2);
req.setHours(3.0);
req.setExtraSupplies(false);
req.setPaymentMethodId(10);
req.setCouponCode("20POFF"); // optional

ApiResponse<Booking> resp = client.bookings().createBooking(req);
System.out.println("Booking ID: " + resp.getData().getId());
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

Assign a specific cleaner to an open booking.

```java
ApiResponse<Object> resp = client.bookings().assignCleaner(16926, cleanerId);
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

Unassign the current cleaner from a booking.

```java
ApiResponse<Object> resp = client.bookings().removeCleaner(16926);
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

Update the scheduled duration of a booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `hours` | double | Yes | New duration in hours |

```java
ApiResponse<Object> resp = client.bookings().adjustHours(16926, 4.5);
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

Move a booking to a different date and/or time.

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | String | Yes | New date in `YYYY-MM-DD` format |
| `time` | String | Yes | New time in `HH:MM` (24-hour) |

```java
ApiResponse<Object> resp = client.bookings().rescheduleBooking(16926, "2025-09-15", "14:00");
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

Pay any outstanding expenses for a booking using a saved payment method.

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | int | Yes | ID of the payment method to charge |

```java
ApiResponse<Object> resp = client.bookings().payExpenses(16926, 10);
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

Retrieve the inspection summary for a completed booking.

```java
ApiResponse<Object> resp = client.bookings().getInspection(16926);
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

Retrieve detailed inspection results including photos and checklist completion status.

```java
ApiResponse<Object> resp = client.bookings().getInspectionDetails(16926);
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

Cancel a booking and optionally provide a reason.

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | String | No | Cancellation reason for records |

```java
ApiResponse<Object> resp = client.bookings().cancelBooking(16926, "Scheduling conflict");
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this specific booking only.

```java
ApiResponse<Object> resp = client.bookings().assignChecklistToBooking(16926, 105);
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

Submit a rating and optional comment for a completed booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | int | Yes | Rating from 1 (poor) to 5 (excellent) |
| `comment` | String | No | Written feedback |

```java
ApiResponse<Object> resp = client.bookings().submitFeedback(16926, 5, "Excellent work!");
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

Add a monetary tip for the cleaner after a completed booking.

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | double | Yes | Tip amount in USD |
| `paymentMethodId` | int | Yes | Payment method to charge |

```java
ApiResponse<Object> resp = client.bookings().submitTip(16926, 15.00, 10);
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

Retrieve the chat thread for a booking. See [Chat Window Rules](#chat-window-rules) for availability.

```java
ApiResponse<Object> resp = client.bookings().getChat(16926);
Map<?, ?> data = (Map<?, ?>) resp.getData();
List<?> messages = (List<?>) data.get("messages");
```

**Response structure:**

| Field | Type | Description |
|---|---|---|
| `data.messages[].message_id` | String | Unique message ID |
| `data.messages[].sender_id` | String | Sender reference (e.g. `C6`, `P3`) |
| `data.messages[].content` | String | Text content (empty for media messages) |
| `data.messages[].timestamp` | String | Send time — format: `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `data.messages[].message_type` | String | `text` or `media` |
| `data.messages[].attachments[]` | Array | Media attachments (see below) |
| `data.messages[].attachments[].type` | String | `image`, `video`, or `sound` |
| `data.messages[].attachments[].url` | String | Direct URL to the media file |
| `data.messages[].attachments[].thumb_url` | String | Thumbnail URL (nullable) |
| `data.messages[].is_read` | boolean | Whether the message has been read |
| `data.messages[].sender_type` | String | `client`, `cleaner`, `support`, or `bot` |
| `data.reference.sender` | Object | Map of `sender_id` → sender details |
| `data.reference.sender.name` | String | Sender display name |
| `data.reference.sender.profile_url` | String | Profile picture URL |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

Send a text message in the booking chat thread.

| Field | Type | Required | Description |
|---|---|---|---|
| `message` | String | Yes | Message text content |

```java
ApiResponse<Object> resp = client.bookings().sendMessage(16926, "Please bring extra cleaning supplies.");
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

Delete a specific message from the chat thread.

```java
ApiResponse<Object> resp = client.bookings().deleteMessage(16926, "-OLPrlE06uD8tQ8ebJZw");
```

---

### Users

#### Create User
**`POST /v1/user/account`**

Register a new user account. Call this once per end-user of your platform.

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | String | Yes | User's email address |
| `firstName` | String | Yes | First name |
| `lastName` | String | Yes | Last name |
| `phone` | String | Yes | Phone number in E.164 format |

```java
ApiResponse<Object> resp = client.users().createUser(
    "jane@example.com", "Jane", "Doe", "+15551234567"
);
Map<?, ?> data = (Map<?, ?>) resp.getData();
int userId = ((Number) data.get("userId")).intValue();
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Retrieve the long-lived JWT for an existing user. Store and reuse this token; only request a new one when the API returns 401.

```java
ApiResponse<Object> resp = client.users().fetchAccessToken(42);
Map<?, ?> data = (Map<?, ?>) resp.getData();
String token = (String) data.get("token");
```

> **Headers for this endpoint:** Only `access-key` is required — no user `token`.

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

Check whether a user's JWT is still valid.

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | String | Yes | The JWT to validate |

```java
ApiResponse<Object> resp = client.users().verifyJwt(userToken);
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

List all properties for a specific service area.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `serviceId` | int | Yes | Service area identifier |

```java
ApiResponse<Object> resp = client.properties().listProperties(1);
```

---

#### Create Property
**`POST /v1/properties`**

Register a new property (cleaning location).

| Field | Type | Required | Description |
|---|---|---|---|
| `address` | String | Yes | Street address |
| `city` | String | Yes | City |
| `state` | String | Yes | State / province |
| `zip` | String | Yes | Postal / ZIP code |
| `serviceId` | int | Yes | Service area ID |
| `bedrooms` | int | No | Number of bedrooms |
| `bathrooms` | int | No | Number of bathrooms |
| `notes` | String | No | Access or cleaning notes |

```java
CreatePropertyRequest req = new CreatePropertyRequest();
req.setAddress("123 Main St");
req.setCity("Chicago");
req.setState("IL");
req.setZip("60601");
req.setServiceId(1);

ApiResponse<Object> resp = client.properties().createProperty(req);
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

Retrieve full details for a single property.

```java
ApiResponse<Object> resp = client.properties().getProperty(1004);
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

Update property details such as address or room count.

```java
UpdatePropertyRequest req = new UpdatePropertyRequest();
req.setAddress("456 Elm Street");
req.setCity("Chicago");
ApiResponse<Object> resp = client.properties().updateProperty(1004, req);
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

Update supplementary details like gate codes, pet information, or access instructions.

```java
ApiResponse<Object> resp = client.properties().updateAdditionalInfo(1004, additionalInfoRequest);
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

Toggle a property's active status. Disabled properties cannot receive new bookings.

| Field | Type | Required | Description |
|---|---|---|---|
| `enabled` | boolean | Yes | `true` to enable, `false` to disable |

```java
ApiResponse<Object> resp = client.properties().enableOrDisable(1004, true);
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

Permanently remove a property and all associated data.

```java
ApiResponse<Object> resp = client.properties().deleteProperty(1004);
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

Retrieve all iCal calendar subscriptions attached to a property.

```java
ApiResponse<Object> resp = client.properties().getIcal(1004);
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

Subscribe a property to an external iCal calendar (e.g. Airbnb, VRBO) to block off dates automatically.

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | Full iCal feed URL (`.ics`) |

```java
ApiResponse<Object> resp = client.properties().addIcal(1004,
    "https://www.airbnb.com/calendar/ical/12345.ics?s=abc");
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

Remove specific iCal events (by event ID) from a property's calendar.

```java
ApiResponse<Object> resp = client.properties().deleteIcal(1004,
    Arrays.asList(101, 102, 103));
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

List all preferred cleaners assigned to a property.

```java
ApiResponse<Object> resp = client.properties().listCleaners(1004);
```

---

#### Add Preferred Cleaner to Property
**`POST /v1/properties/{propertyId}/cleaners`**

Add a cleaner to the preferred list for a property.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | int | Yes | Cleaner's user ID |

```java
ApiResponse<Object> resp = client.properties().addCleaner(1004, 3);
```

---

#### Remove Preferred Cleaner from Property
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

Remove a cleaner from the preferred list.

```java
ApiResponse<Object> resp = client.properties().removeCleaner(1004, 3);
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={true|false}`**

Set the default checklist for all future bookings at a property. Optionally apply it to upcoming (already scheduled) bookings too.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `updateUpcomingBookings` | boolean | No | Also apply to future scheduled bookings (default: `false`) |

```java
ApiResponse<Object> resp = client.properties()
    .setDefaultChecklist(1004, 105, true);
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

Return all checklists associated with your partner account.

```java
ApiResponse<Object> resp = client.checklists().listChecklists();
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

Return a specific checklist including all its task items in order.

```java
ApiResponse<Checklist> resp = client.checklists().getChecklist(105);
Checklist cl = resp.getData();
cl.getItems().forEach(item -> System.out.println(item.getTask()));
```

---

#### Create Checklist
**`POST /v1/checklist`**

Create a new checklist with a name and ordered task items.

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | Display name for the checklist |
| `items` | List\<String\> | Yes | Ordered list of task descriptions |

```java
CreateChecklistRequest req = new CreateChecklistRequest();
req.setName("Deep Clean");
req.setItems(Arrays.asList(
    "Vacuum all rooms",
    "Mop kitchen and bathroom floors",
    "Scrub toilets, sinks, and tubs",
    "Wipe all countertops and surfaces",
    "Clean inside microwave and oven",
    "Wipe exterior of all appliances"
));

ApiResponse<Checklist> resp = client.checklists().createChecklist(req);
System.out.println("Created checklist ID: " + resp.getData().getId());
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

Replace the name and all task items of an existing checklist.

```java
CreateChecklistRequest req = new CreateChecklistRequest();
req.setName("Standard Clean");
req.setItems(Arrays.asList("Vacuum", "Wipe surfaces", "Clean bathrooms"));

ApiResponse<Checklist> resp = client.checklists().updateChecklist(105, req);
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

Permanently delete a checklist. Any bookings or properties referencing this checklist will revert to no checklist.

```java
ApiResponse<Object> resp = client.checklists().deleteChecklist(105);
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```java
byte[] imageBytes = Files.readAllBytes(Paths.get("bathroom-guide.jpg"));
ApiResponse<Object> resp = client.checklists().uploadChecklistImage(105, imageBytes, "bathroom-guide.jpg");
```

---

### Other / Reference Data

These endpoints provide data needed to build a booking flow.

#### Get Services
**`GET /v1/services`**

Return all available service types (e.g. Residential, Commercial, Airbnb).

```java
ApiResponse<Object> resp = client.other().getServices();
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

Return available cleaning plans for a specific property.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | int | Yes | The property to get plans for |

```java
ApiResponse<Object> resp = client.other().getPlans(1004);
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

Return optional add-on services available for a given service type.

```java
ApiResponse<Object> resp = client.other().getCleaningExtras(1);
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={propertyId}&bathroomCount={n}&roomCount={n}`**

Calculate the recommended cleaning duration based on property size.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | int | Yes | The property being cleaned |
| `bathroomCount` | int | Yes | Number of bathrooms |
| `roomCount` | int | Yes | Number of rooms |

```java
ApiResponse<Object> resp = client.other().getRecommendedHours(1004, 2, 3);
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

Get a price estimate before creating a booking.

```java
ApiResponse<Object> resp = client.other().getCostEstimate(estimateRequest);
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

Find cleaners available for a specific date, time, and location.

```java
ApiResponse<Object> resp = client.other().getAvailableCleaners(availabilityRequest);
```

---

#### Get Coupons
**`GET /v1/coupons`**

Return all active coupon codes for your partner account.

```java
ApiResponse<Object> resp = client.other().getCoupons();
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

Retrieve a paginated list of cleaners your account has blacklisted.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | int | Yes | Page number (1-based) |

```java
ApiResponse<Object> resp = client.blacklist().getBlacklist(1);
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

Prevent a specific cleaner from being assigned to your properties.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | int | Yes | The cleaner's user ID |

```java
ApiResponse<Object> resp = client.blacklist().addToBlacklist(3);
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

Remove a cleaner from the blacklist, allowing them to be assigned again.

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | int | Yes | The cleaner's user ID |

```java
ApiResponse<Object> resp = client.blacklist().removeFromBlacklist(3);
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

Retrieve a Stripe `SetupIntent` client secret. Use this on your frontend to initialize Stripe's `confirmCardSetup` flow before saving a card.

```java
ApiResponse<Object> resp = client.paymentMethods().getSetupIntentDetails();
Map<?, ?> data = (Map<?, ?>) resp.getData();
String clientSecret = (String) data.get("clientSecret");
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

Retrieve a PayPal client token for initializing PayPal Hosted Fields or Smart Payment Buttons.

```java
ApiResponse<Object> resp = client.paymentMethods().getPayPalClientToken();
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

Save a new Stripe or PayPal payment method to the user's account.

```java
ApiResponse<Object> resp = client.paymentMethods().addPaymentMethod(paymentRequest);
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

List all saved payment methods for the current user.

```java
ApiResponse<Object> resp = client.paymentMethods().listPaymentMethods();
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

Remove a saved payment method.

```java
ApiResponse<Object> resp = client.paymentMethods().deletePaymentMethod(193);
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

Mark a payment method as the default for future charges.

```java
ApiResponse<Object> resp = client.paymentMethods().setDefault(193);
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

Return all webhook subscriptions for your partner account.

```java
ApiResponse<Object> resp = client.webhooks().listWebhooks();
```

---

#### Create Webhook
**`POST /v1/webhooks`**

Register a URL to receive booking event notifications.

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | String | Yes | HTTPS endpoint to receive POST payloads |
| `event` | String | Yes | Event type to subscribe to (see [Webhook Events](#webhook-events)) |

```java
WebhookRequest req = new WebhookRequest();
req.setUrl("https://your-server.com/hooks/cleanster");
req.setEvent("booking.status_changed");

ApiResponse<Object> resp = client.webhooks().createWebhook(req);
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

Update an existing webhook's URL or event subscription.

```java
WebhookRequest req = new WebhookRequest();
req.setUrl("https://your-server.com/hooks/cleanster-v2");
req.setEvent("booking.completed");

ApiResponse<Object> resp = client.webhooks().updateWebhook(50, req);
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

Remove a webhook subscription.

```java
ApiResponse<Object> resp = client.webhooks().deleteWebhook(50);
```

---

## Models Reference

### `Booking`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique booking ID |
| `status` | String | `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED` |
| `date` | String | Booking date (`YYYY-MM-DD`) |
| `time` | String | Booking start time (`HH:MM`) |
| `hours` | double | Scheduled duration in hours |
| `cost` | double | Total cost in USD |
| `propertyId` | int | ID of the property being cleaned |
| `planId` | int | Cleaning plan ID |
| `roomCount` | int | Number of rooms included |
| `bathroomCount` | int | Number of bathrooms included |
| `extraSupplies` | boolean | Whether the cleaner brings supplies |
| `paymentMethodId` | int | Payment method charged |
| `cleaner` | `Cleaner` | Assigned cleaner (null if unassigned) |

### `Cleaner`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique cleaner ID |
| `name` | String | Full display name |
| `email` | String | Email address |
| `phone` | String | Phone number |
| `profileUrl` | String | Profile picture URL |
| `rating` | double | Average rating (1.0–5.0) |

### `Property`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique property ID |
| `address` | String | Street address |
| `city` | String | City |
| `state` | String | State or province |
| `zip` | String | Postal / ZIP code |
| `serviceId` | int | Service area identifier |
| `enabled` | boolean | Whether the property accepts new bookings |
| `bedrooms` | int | Bedroom count |
| `bathrooms` | int | Bathroom count |

### `Checklist`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique checklist ID |
| `name` | String | Display name |
| `items` | `List<ChecklistItem>` | Ordered list of cleaning tasks |

### `ChecklistItem`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique task ID |
| `task` | String | Task description |
| `order` | int | Display/execution order |
| `isCompleted` | boolean | Whether the cleaner marked this done |

### `PaymentMethod`

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique payment method ID |
| `type` | String | `card` or `paypal` |
| `last4` | String | Last 4 digits of the card (cards only) |
| `brand` | String | Card brand: `Visa`, `Mastercard`, `Amex`, etc. |
| `expiryMonth` | int | Card expiry month (cards only) |
| `expiryYear` | int | Card expiry year (cards only) |
| `isDefault` | boolean | Whether this is the default payment method |

### `ApiResponse<T>`

| Field | Type | Description |
|---|---|---|
| `status` | int | HTTP status code (200 = success) |
| `message` | String | Human-readable result description |
| `data` | T | Typed response payload |

---

## Error Handling

All API errors throw `CleansterApiException`:

```java
try {
    ApiResponse<Booking> resp = client.bookings().getBooking(99999);
} catch (CleansterApiException e) {
    System.err.println("HTTP " + e.getStatusCode() + ": " + e.getMessage());
    // Handle specific codes:
    switch (e.getStatusCode()) {
        case 401:
            // Re-fetch the user token and retry
            break;
        case 404:
            // Resource does not exist
            break;
        case 422:
            // Validation failed — check the message for details
            break;
    }
}
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request — malformed parameters |
| 401 | Unauthorized — invalid or missing `access-key` / `token` |
| 403 | Forbidden — your account lacks permission |
| 404 | Not found — resource does not exist |
| 422 | Unprocessable entity — validation error |
| 429 | Too many requests — rate limit exceeded |
| 500 | Internal server error |

---

## Test Coupon Codes

Use these codes in the **sandbox** environment when creating bookings:

| Code | Discount | Status |
|---|---|---|
| `100POFF` | 100% off | Active |
| `50POFF` | 50% off | Active |
| `20POFF` | 20% off | Active |
| `200OFF` | $200 off | Active |
| `100OFF` | $100 off | Active |
| `75POFF` | 75% off | **Expired** |

```java
req.setCouponCode("50POFF");
```

---

## Chat Window Rules

The chat feature between clients and cleaners has the following availability rules:

| Booking State | Chat Available |
|---|---|
| `OPEN` — within 24 hours of scheduled start | Yes |
| `COMPLETED` — within 24 hours of completion | Yes |
| `IN_PROGRESS` (hanging state) | Yes — **no time restriction** |
| `CANCELLED` | No |
| `OPEN` or `COMPLETED` — older than 24 hours | No |

A **hanging state** means the cleaner has started the job but it has not been marked completed or cancelled.

---

## Webhook Events

When creating a webhook, subscribe to one of these events:

| Event | Fired when |
|---|---|
| `booking.status_changed` | A booking transitions to any new status |
| `booking.cleaner_assigned` | A cleaner is assigned to a booking |
| `booking.cancelled` | A booking is cancelled |
| `booking.completed` | A booking is marked as completed |

Payloads are sent as `POST` requests to your webhook URL with a JSON body containing the event type and relevant booking data.

---

## Running Tests

```bash
mvn test
```

Expected: **74 tests, 0 failures, 0 errors.**

To run a specific test class:

```bash
mvn test -Dtest=BookingApiTest
```

---

## Project Structure

```
java-sdk/
├── pom.xml
└── src/
    ├── main/java/com/cleanster/sdk/
    │   ├── client/
    │   │   └── CleansterClient.java      # Main entry point
    │   ├── api/
    │   │   ├── BookingApi.java
    │   │   ├── UserApi.java
    │   │   ├── PropertyApi.java
    │   │   ├── ChecklistApi.java
    │   │   ├── OtherApi.java
    │   │   ├── BlacklistApi.java
    │   │   ├── PaymentMethodApi.java
    │   │   └── WebhookApi.java
    │   ├── http/
    │   │   └── CleansterHttpClient.java  # HTTP layer (java.net.http)
    │   └── model/
    │       ├── Booking.java
    │       ├── Cleaner.java
    │       ├── Property.java
    │       ├── Checklist.java
    │       ├── ChecklistItem.java
    │       ├── PaymentMethod.java
    │       ├── ApiResponse.java
    │       └── ...
    └── test/java/com/cleanster/sdk/
        └── CleansterSdkTest.java
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
