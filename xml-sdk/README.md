# Cleanster Partner API — XML SDK

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java%2017-orange" alt="Java 17">
  <img src="https://img.shields.io/badge/JAXB-4.0-blue" alt="JAXB 4.0">
  <img src="https://img.shields.io/badge/Endpoints-62-brightgreen" alt="62 Endpoints">
  <img src="https://img.shields.io/badge/Tests-123%20passing-success" alt="123 Tests">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT">
</p>

The official **XML SDK** for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep).

All model classes carry full **JAXB 4.0** annotations, so every request and response object can be round-tripped to and from XML with a single call to `XmlConverter.toXml()` / `XmlConverter.fromXml()`. HTTP transport uses the REST/JSON API under the hood; the XML layer is surfaced at the SDK boundary so enterprise integrations, message queues, and XML-native toolchains can work natively with the data.

---

## Table of Contents

1. [Requirements](#requirements)
2. [Installation](#installation)
3. [Quick Start](#quick-start)
4. [Authentication](#authentication)
5. [XmlConverter Utility](#xmlconverter-utility)
6. [Response Envelope](#response-envelope)
7. [Error Handling](#error-handling)
8. [Available APIs](#available-apis)
   - [Users](#users-api)
   - [Bookings](#bookings-api)
   - [Properties](#properties-api)
   - [Checklists](#checklists-api)
   - [Payment Methods](#payment-methods-api)
   - [Webhooks](#webhooks-api)
   - [Blacklist](#blacklist-api)
   - [Other / Reference Data](#other-api)
9. [Model Reference](#model-reference)
10. [XML Schema Examples](#xml-schema-examples)
11. [All 62 Endpoints](#all-62-endpoints)
12. [Testing](#testing)
13. [Building](#building)
14. [Configuration Reference](#configuration-reference)
15. [Integration Patterns](#integration-patterns)
16. [Booking Lifecycle](#booking-lifecycle)
17. [Changelog](#changelog)

---

## Requirements

| Requirement | Version |
|---|---|
| Java | 17 or higher |
| Maven | 3.8+ |
| JAXB API | 4.0.0 |
| JAXB RI (runtime) | 4.0.3 |
| OkHttp | 4.12.0 |
| Gson | 2.10.1 |

---

## Installation

### Maven

```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-xml-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

The JAXB runtime must also be on the classpath for Java 11+:

```xml
<dependency>
  <groupId>com.sun.xml.bind</groupId>
  <artifactId>jaxb-impl</artifactId>
  <version>4.0.3</version>
</dependency>
```

---

## Quick Start

```java
// 1. Create client (targets sandbox by default)
CleansterXmlClient client = CleansterXmlClient.sandbox("your-access-key");

// 2. Create a user account
XmlApiResponse<User> userResp = client.users()
        .createUser("alice@example.com", "Alice", "Smith");
int userId = userResp.getData().getId();

// 3. Fetch a per-user JWT token
XmlApiResponse<User> tokenResp = client.users().fetchAccessToken(userId);
client.setToken(tokenResp.getData().getToken());

// 4. Create a booking and serialize it to XML
XmlApiResponse<Booking> bookingResp = client.bookings()
        .createBooking("2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55);

Booking booking = bookingResp.getData();
System.out.println("Booking ID: " + booking.getId());
System.out.println(XmlConverter.toXml(booking));
```

**Production:**
```java
CleansterXmlClient client = CleansterXmlClient.production("your-access-key");
```

---

## Authentication

Every request requires two HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — issued by Cleanster |
| `token` | Per-user JWT — obtained via `fetchAccessToken(userId)` |

**Flow:**
1. Call `POST /v1/user/account` to register a user in Cleanster.
2. Call `GET /v1/user/access-token/{userId}` to get their JWT.
3. Store the JWT and pass it via `client.setToken(token)` on all subsequent calls.
4. Verify tokens at any time with `POST /v1/user/verify-jwt`.

---

## XmlConverter Utility

```java
// Serialize any JAXB model to XML
String xml = XmlConverter.toXml(booking);

// Deserialize XML back to a model
Booking restored = XmlConverter.fromXml(xml, Booking.class);

// Detect whether a string is XML
boolean isXml = XmlConverter.isXml(xml);  // true
boolean notXml = XmlConverter.isXml("{}"); // false

// Convenience methods on the client itself
String xml2 = CleansterXmlClient.toXml(booking);
Booking b2  = CleansterXmlClient.fromXml(xml, Booking.class);
```

---

## Response Envelope

Every API method returns `XmlApiResponse<T>`:

```java
XmlApiResponse<Booking> resp = client.bookings().getBooking(bookingId);

resp.isSuccess();   // true / false
resp.getMessage();  // "OK" or error description
resp.getData();     // T — the payload
```

---

## Error Handling

```java
try {
    XmlApiResponse<Booking> resp = client.bookings().getBooking(bookingId);
    System.out.println(XmlConverter.toXml(resp.getData()));
} catch (CleansterXmlException e) {
    System.err.println("HTTP " + e.getStatusCode() + ": " + e.getMessage());
}
```

HTTP 4xx and 5xx responses throw `CleansterXmlException`. Network errors throw `CleansterXmlException` wrapping an `IOException`.

---

## Available APIs

### Users API

Create partner-managed user accounts and issue access tokens.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| POST | `/v1/user/account`                   | `createUser(email, firstName, lastName)` |
| GET  | `/v1/user/access-token/{userId}`     | `fetchAccessToken(int userId)` |
| POST | `/v1/user/verify-jwt`                | `verifyJwt(String token)` |

#### Create a user

```java
XmlApiResponse<User> resp = client.users()
        .createUser("alice@example.com", "Alice", "Smith");
int userId = resp.getData().getId();
System.out.println(XmlConverter.toXml(resp.getData()));
```

#### Fetch access token

```java
XmlApiResponse<User> resp = client.users().fetchAccessToken(userId);
String token = resp.getData().getToken();
client.setToken(token);   // store it; passed as 'token' header on every call
```

#### Verify JWT

```java
XmlApiResponse<?> resp = client.users().verifyJwt(token);
System.out.println(resp.isSuccess() ? "Valid" : "Invalid or expired");
```

---

### Bookings API

Full lifecycle management for cleaning appointments.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/bookings`                                   | `listBookings()` |
| POST   | `/v1/bookings/create`                            | `createBooking(...)` |
| GET    | `/v1/bookings/{id}`                              | `getBooking(int id)` |
| POST   | `/v1/bookings/{id}/cancel`                       | `cancelBooking(int id)` |
| POST   | `/v1/bookings/{id}/reschedule`                   | `rescheduleBooking(int id, String date, String time)` |
| POST   | `/v1/bookings/{id}/cleaner`                      | `assignCleaner(int id, int cleanerId)` |
| DELETE | `/v1/bookings/{id}/cleaner`                      | `removeAssignedCleaner(int id)` |
| POST   | `/v1/bookings/{id}/hours`                        | `adjustHours(int id, double hours)` |
| POST   | `/v1/bookings/{id}/expenses`                     | `payExpenses(int id, int paymentMethodId)` |
| GET    | `/v1/bookings/{id}/inspection`                   | `getBookingInspection(int id)` |
| GET    | `/v1/bookings/{id}/inspection/details`           | `getBookingInspectionDetails(int id)` |
| PUT    | `/v1/bookings/{id}/checklist/{checklistId}`      | `assignChecklistToBooking(int id, int checklistId)` |
| POST   | `/v1/bookings/{id}/feedback`                     | `submitFeedback(int id, int rating, String comment)` |
| POST   | `/v1/bookings/{id}/tip`                          | `addTip(int id, double amount, int paymentMethodId)` |
| GET    | `/v1/bookings/{id}/chat`                         | `getChat(int id)` |
| POST   | `/v1/bookings/{id}/chat`                         | `sendMessage(int id, String message)` |
| DELETE | `/v1/bookings/{id}/chat/{messageId}`             | `deleteMessage(int id, String messageId)` |

#### Create a booking

```java
XmlApiResponse<Booking> resp = client.bookings().createBooking(
    "2025-09-15",  // date (YYYY-MM-DD)
    "09:00",       // time (HH:mm)
    1004,          // propertyId
    2,             // planId
    3.0,           // hours
    2,             // roomCount
    1,             // bathroomCount
    false,         // extraSupplies
    55             // paymentMethodId
);
Booking b = resp.getData();
System.out.println("Booking ID: " + b.getId());
System.out.println(XmlConverter.toXml(b));
```

#### Cancel a booking

```java
// No reason
client.bookings().cancelBooking(bookingId);

// With reason
client.bookings().cancelBooking(bookingId, "Schedule conflict");
```

#### Reschedule

```java
XmlApiResponse<Booking> resp = client.bookings()
        .rescheduleBooking(bookingId, "2025-10-01", "10:00");
```

#### Assign / remove cleaner

```java
client.bookings().assignCleaner(bookingId, cleanerId);
client.bookings().removeAssignedCleaner(bookingId);
```

#### Adjust hours

```java
client.bookings().adjustHours(bookingId, 4.5);
```

#### Pay expenses

```java
client.bookings().payExpenses(bookingId, paymentMethodId);
```

#### Inspection

```java
XmlApiResponse<?> report  = client.bookings().getBookingInspection(bookingId);
XmlApiResponse<?> details = client.bookings().getBookingInspectionDetails(bookingId);
```

#### Assign checklist to booking

```java
client.bookings().assignChecklistToBooking(bookingId, checklistId);
```

#### Feedback and tip

```java
client.bookings().submitFeedback(bookingId, 5, "Fantastic job!");
client.bookings().addTip(bookingId, 20.0, paymentMethodId);
```

#### Chat

```java
XmlApiResponse<?> messages = client.bookings().getChat(bookingId);
client.bookings().sendMessage(bookingId, "On my way!");
client.bookings().deleteMessage(bookingId, messageId);
```

---

### Properties API

Manage cleaning locations (homes, offices, etc.).

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/properties`                                      | `listProperties()` |
| POST   | `/v1/properties`                                      | `createProperty(...)` |
| GET    | `/v1/properties/{id}`                                 | `getProperty(int id)` |
| PUT    | `/v1/properties/{id}`                                 | `updateProperty(int id, Map body)` |
| DELETE | `/v1/properties/{id}`                                 | `deleteProperty(int id)` |
| PUT    | `/v1/properties/{id}/additional-information`          | `updateAdditionalInformation(int id, Map data)` |
| POST   | `/v1/properties/{id}/enable-disable`                  | `enableOrDisableProperty(int id, boolean enabled)` |
| GET    | `/v1/properties/{id}/cleaners`                        | `getPropertyCleaners(int id)` |
| POST   | `/v1/properties/{id}/cleaners`                        | `assignCleanerToProperty(int id, int cleanerId)` |
| DELETE | `/v1/properties/{id}/cleaners/{cleanerId}`            | `unassignCleanerFromProperty(int id, int cleanerId)` |
| PUT    | `/v1/properties/{id}/ical`                            | `addICalLink(int id, String icalUrl)` |
| GET    | `/v1/properties/{id}/ical`                            | `getICalLink(int id)` |
| DELETE | `/v1/properties/{id}/ical`                            | `removeICalLink(int id, String icalUrl)` |
| PUT    | `/v1/properties/{id}/checklist/{checklistId}`         | `setDefaultChecklist(int id, int checklistId, boolean updateUpcoming)` |

#### Create a property

```java
XmlApiResponse<Property> resp = client.properties().createProperty(
    "Sunset Villa",   // name
    "123 Ocean Dr",   // address
    "Miami Beach",    // city
    "US",             // country
    3,                // roomCount
    2,                // bathroomCount
    1                 // serviceId
);
Property p = resp.getData();
System.out.println(XmlConverter.toXml(p));
```

#### Assign/unassign cleaners

```java
client.properties().assignCleanerToProperty(propertyId, cleanerId);
client.properties().unassignCleanerFromProperty(propertyId, cleanerId);
```

#### iCal sync

```java
client.properties().addICalLink(propertyId, "https://calendar.example.com/feed.ics");
client.properties().getICalLink(propertyId);
client.properties().removeICalLink(propertyId, "https://calendar.example.com/feed.ics");
```

#### Default checklist

```java
// Apply to upcoming bookings too
client.properties().setDefaultChecklist(propertyId, checklistId, true);
```

---

### Checklists API

Define reusable cleaning task lists.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/checklist`              | `listChecklists()` |
| GET    | `/v1/checklist/{id}`         | `getChecklist(int id)` |
| POST   | `/v1/checklist`              | `createChecklist(String name, List<String> items)` |
| PUT    | `/v1/checklist/{id}`         | `updateChecklist(int id, String name, List<String> items)` |
| DELETE | `/v1/checklist/{id}`         | `deleteChecklist(int id)` |
| POST   | `/v1/checklist/upload-image` | `uploadChecklistImage(byte[] data, String fileName)` |

#### Create a checklist

```java
List<String> items = List.of(
    "Vacuum all floors",
    "Wipe kitchen counters",
    "Clean bathrooms",
    "Empty trash bins"
);
XmlApiResponse<Checklist> resp = client.checklists()
        .createChecklist("Standard Clean", items);

System.out.println(XmlConverter.toXml(resp.getData()));
```

#### Upload an image

```java
byte[] imageBytes = Files.readAllBytes(Path.of("photo.jpg"));
client.checklists().uploadChecklistImage(imageBytes, "photo.jpg");
```

---

### Payment Methods API

Manage Stripe and PayPal payment methods.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/payment-methods/setup-intent-details`    | `getSetupIntentDetails()` |
| GET    | `/v1/payment-methods/paypal-client-token`     | `getPaypalClientToken()` |
| POST   | `/v1/payment-methods`                         | `addPaymentMethod(Map request)` |
| GET    | `/v1/payment-methods`                         | `listPaymentMethods()` |
| DELETE | `/v1/payment-methods/{id}`                    | `deletePaymentMethod(int id)` |
| PUT    | `/v1/payment-methods/{id}/default`            | `setDefaultPaymentMethod(int id)` |

#### Add a card (Stripe flow)

```java
// 1. Get SetupIntent client secret (send to your frontend)
XmlApiResponse<?> intentResp = client.paymentMethods().getSetupIntentDetails();

// 2. Collect the card client-side with Stripe.js, then confirm with the SetupIntent
//    The confirmed paymentMethodId is sent back to your server

// 3. Save the payment method
client.paymentMethods().addPaymentMethod(Map.of("paymentMethodId", "pm_stripe_xyz"));

// 4. Set as default
client.paymentMethods().setDefaultPaymentMethod(methodId);
```

#### PayPal flow

```java
// 1. Get PayPal client token (send to your frontend)
XmlApiResponse<?> ppResp = client.paymentMethods().getPaypalClientToken();

// 2. Render PayPal button, collect nonce, send nonce to server

// 3. Save
client.paymentMethods().addPaymentMethod(Map.of("nonce", "paypal-nonce-xyz"));
```

#### List and serialize

```java
List<PaymentMethod> methods = client.paymentMethods().listPaymentMethods().getData();
for (PaymentMethod pm : methods) {
    System.out.println(XmlConverter.toXml(pm));
}
```

---

### Webhooks API

Register URLs to receive real-time booking events.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/webhooks`       | `listWebhooks()` |
| POST   | `/v1/webhooks`       | `createWebhook(String url, String event)` |
| PUT    | `/v1/webhooks/{id}`  | `updateWebhook(int id, String url, String event)` |
| DELETE | `/v1/webhooks/{id}`  | `deleteWebhook(int id)` |

#### Register a webhook

```java
XmlApiResponse<Webhook> resp = client.webhooks().createWebhook(
    "https://your-server.com/hooks/cleanster",
    "booking.completed"
);
Webhook wh = resp.getData();
System.out.println("Webhook ID: " + wh.getId());
System.out.println(XmlConverter.toXml(wh));
```

#### Update and delete

```java
client.webhooks().updateWebhook(webhookId, "https://new-server.com/hook", "booking.cancelled");
client.webhooks().deleteWebhook(webhookId);
```

---

### Blacklist API

Prevent specific cleaners from being assigned to your bookings.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/v1/blacklist/cleaner` | `listBlacklist()` |
| POST   | `/v1/blacklist/cleaner` | `addToBlacklist(int cleanerId, String reason)` |
| DELETE | `/v1/blacklist/cleaner` | `removeFromBlacklist(int cleanerId)` |

#### Add to blacklist

```java
XmlApiResponse<BlacklistEntry> resp = client.blacklist()
        .addToBlacklist(cleanerId, "Repeated no-show");
System.out.println(XmlConverter.toXml(resp.getData()));
```

#### Remove from blacklist

```java
client.blacklist().removeFromBlacklist(cleanerId);
```

---

### Other API

Reference data used in booking flows.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET  | `/v1/services`                  | `getServices()` |
| GET  | `/v1/plans`                     | `getPlans(int propertyId)` |
| GET  | `/v1/recommended-hours`         | `getRecommendedHours(int propertyId, int bathrooms, int rooms)` |
| POST | `/v1/cost-estimate`             | `getCostEstimate(Map request)` |
| GET  | `/v1/cleaning-extras/{id}`      | `getCleaningExtras(int serviceId)` |
| POST | `/v1/available-cleaners`        | `getAvailableCleaners(Map request)` |
| GET  | `/v1/coupons`                   | `getCoupons()` |
| GET  | `/v1/cleaners`                  | `listCleaners()` |
| GET  | `/v1/cleaners/{id}`             | `getCleaner(int cleanerId)` |

#### Get plans for a property

```java
XmlApiResponse<?> resp = client.other().getPlans(propertyId);
```

#### Get recommended cleaning hours

```java
XmlApiResponse<?> resp = client.other().getRecommendedHours(propertyId, 2, 3);
```

#### Cost estimate

```java
XmlApiResponse<?> resp = client.other().getCostEstimate(Map.of(
    "propertyId", propertyId,
    "planId",     2,
    "hours",      3.0
));
```

#### Find available cleaners

```java
XmlApiResponse<?> resp = client.other().getAvailableCleaners(Map.of(
    "propertyId", propertyId,
    "date",       "2025-09-15",
    "time",       "10:00"
));
```

#### Valid test coupon codes

| Code | Discount |
|---|---|
| `100POFF` | 100% off |
| `50POFF`  | 50% off |
| `20POFF`  | 20% off |
| `200OFF`  | $200 fixed |
| `100OFF`  | $100 fixed |

---

## Model Reference

### Booking Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Booking identifier |
| `status` | String | `pending`, `open`, `completed`, `cancelled`, `disputed` |
| `date` | String | Service date (YYYY-MM-DD) |
| `time` | String | Start time (HH:mm) |
| `hours` | Double | Duration in hours |
| `propertyId` | Integer | Associated property |
| `planId` | Integer | Cleaning plan |
| `roomCount` | Integer | Number of rooms |
| `bathroomCount` | Integer | Number of bathrooms |
| `extraSupplies` | Boolean | Whether to bring supplies |
| `totalPrice` | Double | Total cost |
| `currency` | String | ISO 4217 code |
| `paymentMethodId` | Integer | Stored payment method |
| `notes` | String | Special instructions |
| `cancelReason` | String | Reason for cancellation |
| `cancelledAt` | String | ISO-8601 cancellation timestamp |

### Property Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Property identifier |
| `name` | String | Display name |
| `address` | String | Street address |
| `city` | String | City |
| `country` | String | Country code |
| `roomCount` | Integer | Number of bedrooms |
| `bathroomCount` | Integer | Number of bathrooms |
| `active` | Boolean | Whether property is active |

### User Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | User identifier |
| `email` | String | Email address |
| `firstName` | String | First name |
| `lastName` | String | Last name |
| `phone` | String | Phone number |
| `token` | String | JWT access token |
| `active` | Boolean | Account status |

### Checklist Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Checklist identifier |
| `name` | String | Display name |
| `description` | String | Description |
| `active` | Boolean | Active status |
| `items` | List&lt;String&gt; | Task item list |

### PaymentMethod Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Method identifier |
| `type` | String | `card` or `paypal` |
| `last4` | String | Last 4 digits (card) |
| `isDefault` | Boolean | Default method flag |

### Webhook Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Webhook identifier |
| `url` | String | HTTPS endpoint URL |
| `event` | String | Subscribed event type |
| `active` | Boolean | Active status |

### BlacklistEntry Model

| Field | Type | Description |
|---|---|---|
| `cleanerId` | Integer | Blacklisted cleaner's ID |
| `reason` | String | Reason for blacklisting |

---

## XML Schema Examples

### Booking XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<booking>
    <id>12345</id>
    <status>open</status>
    <date>2025-09-15</date>
    <time>09:00</time>
    <hours>3.0</hours>
    <propertyId>1004</propertyId>
    <planId>2</planId>
    <roomCount>2</roomCount>
    <bathroomCount>1</bathroomCount>
    <extraSupplies>false</extraSupplies>
    <totalPrice>120.0</totalPrice>
    <currency>USD</currency>
    <paymentMethodId>55</paymentMethodId>
</booking>
```

### Checklist XML (with items)

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<checklist>
    <id>7</id>
    <name>Standard Clean</name>
    <active>true</active>
    <items>
        <item>Vacuum all floors</item>
        <item>Wipe kitchen counters</item>
        <item>Clean bathrooms</item>
    </items>
</checklist>
```

### Webhook XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<webhook>
    <id>9</id>
    <url>https://your-server.com/hooks/cleanster</url>
    <event>booking.completed</event>
    <active>true</active>
</webhook>
```

---

## All 62 Endpoints

| # | Method | Path | API group |
|---|---|---|---|
| 1  | POST   | `/v1/user/account`                                | Users |
| 2  | GET    | `/v1/user/access-token/{userId}`                  | Users |
| 3  | POST   | `/v1/user/verify-jwt`                             | Users |
| 4  | GET    | `/v1/properties`                                  | Properties |
| 5  | POST   | `/v1/properties`                                  | Properties |
| 6  | GET    | `/v1/properties/{id}`                             | Properties |
| 7  | PUT    | `/v1/properties/{id}`                             | Properties |
| 8  | DELETE | `/v1/properties/{id}`                             | Properties |
| 9  | PUT    | `/v1/properties/{id}/additional-information`      | Properties |
| 10 | POST   | `/v1/properties/{id}/enable-disable`              | Properties |
| 11 | GET    | `/v1/properties/{id}/cleaners`                    | Properties |
| 12 | POST   | `/v1/properties/{id}/cleaners`                    | Properties |
| 13 | DELETE | `/v1/properties/{id}/cleaners/{cleanerId}`        | Properties |
| 14 | PUT    | `/v1/properties/{id}/ical`                        | Properties |
| 15 | GET    | `/v1/properties/{id}/ical`                        | Properties |
| 16 | DELETE | `/v1/properties/{id}/ical`                        | Properties |
| 17 | PUT    | `/v1/properties/{id}/checklist/{checklistId}`     | Properties |
| 18 | GET    | `/v1/bookings`                                    | Bookings |
| 19 | POST   | `/v1/bookings/create`                             | Bookings |
| 20 | GET    | `/v1/bookings/{id}`                               | Bookings |
| 21 | POST   | `/v1/bookings/{id}/cancel`                        | Bookings |
| 22 | POST   | `/v1/bookings/{id}/reschedule`                    | Bookings |
| 23 | POST   | `/v1/bookings/{id}/cleaner`                       | Bookings |
| 24 | DELETE | `/v1/bookings/{id}/cleaner`                       | Bookings |
| 25 | POST   | `/v1/bookings/{id}/hours`                         | Bookings |
| 26 | POST   | `/v1/bookings/{id}/expenses`                      | Bookings |
| 27 | GET    | `/v1/bookings/{id}/inspection`                    | Bookings |
| 28 | GET    | `/v1/bookings/{id}/inspection/details`            | Bookings |
| 29 | PUT    | `/v1/bookings/{id}/checklist/{checklistId}`       | Bookings |
| 30 | POST   | `/v1/bookings/{id}/feedback`                      | Bookings |
| 31 | POST   | `/v1/bookings/{id}/tip`                           | Bookings |
| 32 | GET    | `/v1/bookings/{id}/chat`                          | Bookings |
| 33 | POST   | `/v1/bookings/{id}/chat`                          | Bookings |
| 34 | DELETE | `/v1/bookings/{id}/chat/{messageId}`              | Bookings |
| 35 | GET    | `/v1/checklist`                                   | Checklists |
| 36 | POST   | `/v1/checklist`                                   | Checklists |
| 37 | GET    | `/v1/checklist/{id}`                              | Checklists |
| 38 | PUT    | `/v1/checklist/{id}`                              | Checklists |
| 39 | DELETE | `/v1/checklist/{id}`                              | Checklists |
| 40 | POST   | `/v1/checklist/upload-image`                      | Checklists |
| 41 | GET    | `/v1/payment-methods/setup-intent-details`        | Payment Methods |
| 42 | GET    | `/v1/payment-methods/paypal-client-token`         | Payment Methods |
| 43 | POST   | `/v1/payment-methods`                             | Payment Methods |
| 44 | GET    | `/v1/payment-methods`                             | Payment Methods |
| 45 | DELETE | `/v1/payment-methods/{id}`                        | Payment Methods |
| 46 | PUT    | `/v1/payment-methods/{id}/default`                | Payment Methods |
| 47 | GET    | `/v1/webhooks`                                    | Webhooks |
| 48 | POST   | `/v1/webhooks`                                    | Webhooks |
| 49 | PUT    | `/v1/webhooks/{id}`                               | Webhooks |
| 50 | DELETE | `/v1/webhooks/{id}`                               | Webhooks |
| 51 | GET    | `/v1/blacklist/cleaner`                           | Blacklist |
| 52 | POST   | `/v1/blacklist/cleaner`                           | Blacklist |
| 53 | DELETE | `/v1/blacklist/cleaner`                           | Blacklist |
| 54 | GET    | `/v1/services`                                    | Other |
| 55 | GET    | `/v1/plans`                                       | Other |
| 56 | GET    | `/v1/recommended-hours`                           | Other |
| 57 | POST   | `/v1/cost-estimate`                               | Other |
| 58 | GET    | `/v1/cleaning-extras/{serviceId}`                 | Other |
| 59 | POST   | `/v1/available-cleaners`                          | Other |
| 60 | GET    | `/v1/coupons`                                     | Other |
| 61 | GET    | `/v1/cleaners`                                    | Other |
| 62 | GET    | `/v1/cleaners/{id}`                               | Other |

---

## Testing

```bash
mvn test
```

123 tests across 8 test classes using **Mockito** (no real network calls). Each test asserts:
- The correct HTTP method is used
- The path contains `/v1/` prefix and the correct endpoint segment
- Request bodies contain expected fields
- Responses are correctly deserialized

---

## Building

```bash
mvn clean package
```

The resulting JAR is in `target/cleanster-xml-sdk-1.0.0.jar`.

---

## Configuration Reference

### Client factories

| Factory | Environment |
|---|---|
| `CleansterXmlClient.sandbox("key")` | Sandbox (default) |
| `CleansterXmlClient.production("key")` | Production |
| `CleansterXmlClient.custom(url, key, httpClient)` | Custom (for testing) |

### Base URLs

| Environment | Base URL |
|---|---|
| **Sandbox** | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

### Token management

```java
client.setToken("your-jwt-token");
String token = client.getToken();
```

### Custom timeouts

```java
OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();

CleansterXmlClient client = CleansterXmlClient.custom(
        CleansterXmlClient.SANDBOX_URL,
        "your-access-key",
        httpClient
);
```

---

## Integration Patterns

### Writing XML bookings to disk

```java
List<Booking> bookings = client.bookings().listBookings().getData();
for (Booking b : bookings) {
    String xml      = XmlConverter.toXml(b);
    String filename = "booking-" + b.getId() + ".xml";
    Files.writeString(Path.of(filename), xml, StandardCharsets.UTF_8);
}
```

### XML message queue integration

```java
// Producer
Booking booking = client.bookings().createBooking(...).getData();
String xml = XmlConverter.toXml(booking);
myQueue.send("bookings", xml);

// Consumer
String received = myQueue.receive("bookings");
Booking b = XmlConverter.fromXml(received, Booking.class);
System.out.println("Processing booking " + b.getId());
```

### SOAP/WS integration

```java
@WebService
public class CleansterBookingService {
    private final CleansterXmlClient client =
            CleansterXmlClient.production(System.getenv("CLEANSTER_ACCESS_KEY"));

    @WebMethod
    public Booking createBooking(String date, String time, int propertyId) {
        return client.bookings()
                .createBooking(date, time, propertyId, 2, 3.0, 2, 1, false, 55)
                .getData();
    }
}
```

---

## Booking Lifecycle

```
              POST /v1/bookings/create
             ─────────────────────────►  PENDING / OPEN
                                              │
                    POST /{id}/reschedule ◄───┤──► POST /{id}/cancel
                                              │
                    POST /{id}/cleaner    ◄───┤  (assign cleaner)
                                              │
                    POST /{id}/hours      ◄───┤  (adjust hours)
                                              │
                                              ▼
                                          COMPLETED
                                              │
                    POST /{id}/feedback   ◄───┤
                    POST /{id}/tip        ◄───┤
                    POST /{id}/expenses   ◄───┘
```

### Key transitions

| Action | Method | Path |
|---|---|---|
| Cancel | `cancelBooking(id)` | `POST /v1/bookings/{id}/cancel` |
| Reschedule | `rescheduleBooking(id, date, time)` | `POST /v1/bookings/{id}/reschedule` |
| Assign cleaner | `assignCleaner(id, cleanerId)` | `POST /v1/bookings/{id}/cleaner` |
| Adjust hours | `adjustHours(id, hours)` | `POST /v1/bookings/{id}/hours` |
| Pay expenses | `payExpenses(id, paymentMethodId)` | `POST /v1/bookings/{id}/expenses` |
| Submit feedback | `submitFeedback(id, rating, comment)` | `POST /v1/bookings/{id}/feedback` |
| Add tip | `addTip(id, amount, paymentMethodId)` | `POST /v1/bookings/{id}/tip` |

---

## Changelog

### v1.0.0

- Initial release
- 62 endpoints across 8 API groups, all with `/v1/` prefix
- Full JAXB 4.0 annotation on all model classes
- `XmlConverter` utility (toXml / fromXml / isXml)
- `CleansterXmlClient` with sandbox, production, and custom factories
- OkHttp 4.12.0 transport with configurable timeouts
- 123 tests with Mockito

---

## Support

| Channel | Contact |
|---|---|
| **API Documentation** | [Postman Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep) |
| **Partner inquiries** | [partner@cleanster.com](mailto:partner@cleanster.com) |
| **General support** | [support@cleanster.com](mailto:support@cleanster.com) |
| **Bug reports** | Open an issue on GitHub |

---

## License

MIT License — see [LICENSE](../LICENSE) for details.
