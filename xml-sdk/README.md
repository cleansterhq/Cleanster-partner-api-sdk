# Cleanster Partner API — XML SDK

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java%2017-orange" alt="Java 17">
  <img src="https://img.shields.io/badge/JAXB-4.0-blue" alt="JAXB 4.0">
  <img src="https://img.shields.io/badge/Endpoints-59-brightgreen" alt="59 Endpoints">
  <img src="https://img.shields.io/badge/Tests-164%20passing-success" alt="164 Tests">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT">
</p>

The official **XML SDK** for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep).

All model classes carry full **JAXB 4.0** annotations, so every request and response object can be
round-tripped to and from XML with a single call to `XmlConverter.toXml()` /
`XmlConverter.fromXml()`.  HTTP transport still uses the REST/JSON API under the hood; the XML
layer is surfaced at the SDK boundary so that enterprise integrations, message queues, and
XML-native toolchains can work natively with the data.

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
   - [Other](#other-api)
9. [Model Reference](#model-reference)
   - [Booking](#booking-model)
   - [Property](#property-model)
   - [User](#user-model)
   - [Checklist](#checklist-model)
   - [PaymentMethod](#paymentmethod-model)
   - [Webhook](#webhook-model)
   - [BlacklistEntry](#blacklistentry-model)
   - [Plan](#plan-model)
   - [Coupon](#coupon-model)
10. [XML Schema Examples](#xml-schema-examples)
11. [All 59 Endpoints](#all-59-endpoints)
12. [Testing](#testing)
13. [Building](#building)
14. [Configuration Reference](#configuration-reference)
15. [Integration Patterns](#integration-patterns)
16. [Booking Lifecycle](#booking-lifecycle)
17. [Chat Window Rules](#chat-window-rules)
18. [Changelog](#changelog)

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

The SDK does **not** require Android; for Android use the dedicated `android-sdk/` module in this
repository.

---

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-xml-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

The JAXB runtime must also be on the classpath for Java 11+:

```xml
<dependencies>
  <!-- Cleanster XML SDK -->
  <dependency>
    <groupId>com.cleanster</groupId>
    <artifactId>cleanster-xml-sdk</artifactId>
    <version>1.0.0</version>
  </dependency>

  <!-- JAXB API (needed on Java 11+ where JAXB was removed from the JDK) -->
  <dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>4.0.0</version>
  </dependency>

  <!-- JAXB Reference Implementation -->
  <dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>4.0.3</version>
    <scope>runtime</scope>
  </dependency>
</dependencies>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.cleanster:cleanster-xml-sdk:1.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.3")
}
```

### Building from Source

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/xml-sdk
mvn clean install -DskipTests
```

---

## Quick Start

```java
import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.*;

// 1. Create a sandbox client
CleansterXmlClient client = CleansterXmlClient.sandbox("your-access-key");

// 2. Fetch an auth token for a user
XmlApiResponse<User> tokenResp = client.users().fetchAccessToken(userId);
client.setToken(tokenResp.getData().getToken());

// 3. Create a booking
XmlApiResponse<Booking> resp = client.bookings().createBooking(
    "2025-09-15",   // date
    "09:00",        // time
    1004,           // propertyId
    2,              // planId
    3.0,            // hours
    2,              // roomCount
    1,              // bathroomCount
    false,          // extraSupplies
    55              // paymentMethodId
);

Booking booking = resp.getData();
System.out.println("Created booking ID: " + booking.getId());

// 4. Serialise to XML
String xml = XmlConverter.toXml(booking);
System.out.println(xml);
// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
// <booking>
//   <id>12345</id>
//   <status>pending</status>
//   <date>2025-09-15</date>
//   <time>09:00</time>
//   ...
// </booking>

// 5. Deserialise from XML back to a Booking
Booking restored = XmlConverter.fromXml(xml, Booking.class);
System.out.println(restored.getStatus());
```

---

## Authentication

Every API call to Cleanster requires two headers:

| Header | Description |
|---|---|
| `access-key` | Your partner access key — does not expire |
| `token` | Per-user session token — obtained via `users().fetchAccessToken(userId)` |

The SDK injects both headers automatically once you have called `client.setToken(...)`.

### Two-step auth pattern

```java
// Step 1: create client with your access key
CleansterXmlClient client = CleansterXmlClient.sandbox("your-access-key");

// Step 2: fetch a user token (only the access-key header is sent at this point)
XmlApiResponse<User> tokenResp = client.users().fetchAccessToken(userId);
if (!tokenResp.isSuccess()) {
    throw new RuntimeException("Token fetch failed: " + tokenResp.getMessage());
}

// Step 3: store the token — all subsequent calls will include it automatically
client.setToken(tokenResp.getData().getToken());

// From here every call includes both access-key and token headers
XmlApiResponse<List<Booking>> bookings = client.bookings().listBookings();
```

### Production vs. Sandbox

```java
// Sandbox (default)
CleansterXmlClient sandbox = CleansterXmlClient.sandbox("your-access-key");

// Production
CleansterXmlClient production = CleansterXmlClient.production("your-access-key");
```

| Environment | Base URL |
|---|---|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## XmlConverter Utility

The `XmlConverter` class is the bridge between Java objects and XML strings.

### Marshal an object to XML

```java
Booking b = new Booking();
b.setId(42);
b.setStatus("confirmed");
b.setDate("2025-09-15");
b.setTime("09:00");
b.setTotalPrice(120.0);
b.setCurrency("USD");

String xml = XmlConverter.toXml(b);
System.out.println(xml);
```

Output:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<booking>
    <id>42</id>
    <status>confirmed</status>
    <date>2025-09-15</date>
    <time>09:00</time>
    <totalPrice>120.0</totalPrice>
    <currency>USD</currency>
</booking>
```

### Unmarshal XML to an object

```java
String xml = """
    <?xml version="1.0" encoding="UTF-8"?>
    <booking>
        <id>42</id>
        <status>confirmed</status>
    </booking>
    """;

Booking b = XmlConverter.fromXml(xml, Booking.class);
System.out.println(b.getId());     // 42
System.out.println(b.getStatus()); // confirmed
```

### Check if a string is XML

```java
XmlConverter.isXml("<?xml version=\"1.0\"?><root/>"); // true
XmlConverter.isXml("<booking><id>1</id></booking>");   // true
XmlConverter.isXml("plain text");                      // false
XmlConverter.isXml(null);                              // false
```

### Convenience wrappers on the client

The client exposes static delegates so you can write everything through one import:

```java
String xml     = CleansterXmlClient.toXml(booking);
Booking b      = CleansterXmlClient.fromXml(xml, Booking.class);
```

---

## Response Envelope

Every endpoint returns an `XmlApiResponse<T>` wrapper:

```java
public class XmlApiResponse<T> {
    boolean success;
    String  message;
    T       data;
}
```

JSON on the wire:

```json
{
  "success": true,
  "message": "Booking created",
  "data": { "id": 12345, "status": "pending", ... }
}
```

The same envelope serialised to XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<response>
    <success>true</success>
    <message>Booking created</message>
    <data>
        <id>12345</id>
        <status>pending</status>
    </data>
</response>
```

Always check `response.isSuccess()` before calling `response.getData()`.

---

## Error Handling

Failed HTTP responses throw `CleansterXmlException`:

```java
try {
    XmlApiResponse<Booking> resp = client.bookings().getBooking(9999);
} catch (CleansterXmlException e) {
    System.out.println("HTTP status : " + e.getHttpStatus());  // e.g. 404
    System.out.println("Message     : " + e.getMessage());
    System.out.println("Raw body    : " + e.getRawBody());
}
```

XML conversion errors (malformed XML, unknown element) also throw `CleansterXmlException`:

```java
try {
    Booking b = XmlConverter.fromXml("<not-valid", Booking.class);
} catch (CleansterXmlException e) {
    System.out.println("XML parse error: " + e.getMessage());
}
```

### HTTP status codes

| Status | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 400 | Bad request — check your request parameters |
| 401 | Unauthorized — missing or invalid `access-key` / `token` |
| 403 | Forbidden — insufficient permissions |
| 404 | Not found |
| 422 | Unprocessable entity — validation error |
| 429 | Rate limited |
| 500 | Server error |

---

## Available APIs

### Users API

Authenticate users and manage their profiles.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| POST | `/users/{userId}/token` | `fetchAccessToken(int userId)` |
| GET  | `/users/{userId}` | `getUserProfile(int userId)` |
| PUT  | `/users/{userId}` | `updateUserProfile(int userId, ...)` |

#### Fetch access token

```java
XmlApiResponse<User> resp = client.users().fetchAccessToken(userId);

if (resp.isSuccess()) {
    String token = resp.getData().getToken();
    client.setToken(token);
    System.out.println("Authenticated. Token: " + token);
} else {
    System.out.println("Auth failed: " + resp.getMessage());
}
```

#### Get user profile

```java
XmlApiResponse<User> resp = client.users().getUserProfile(userId);
User user = resp.getData();

System.out.println(user.getFirstName() + " " + user.getLastName());
System.out.println(user.getEmail());

// Serialise to XML
System.out.println(XmlConverter.toXml(user));
```

#### Update user profile

```java
XmlApiResponse<User> resp = client.users().updateUserProfile(
    userId,
    "Alice",          // firstName  (null = no change)
    "Wonderland",     // lastName   (null = no change)
    "+1-555-0100"     // phone      (null = no change)
);

System.out.println("Updated: " + resp.isSuccess());
```

---

### Bookings API

Full lifecycle management for cleaning bookings.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/bookings`                        | `listBookings()` |
| GET    | `/bookings/{id}`                   | `getBooking(int id)` |
| POST   | `/bookings`                        | `createBooking(...)` |
| PUT    | `/bookings/{id}`                   | `updateBooking(int id, Map body)` |
| DELETE | `/bookings/{id}`                   | `cancelBooking(int id)` |
| POST   | `/bookings/{id}/cancel`            | `cancelBooking(int id, String reason)` |
| POST   | `/bookings/{id}/reschedule`        | `rescheduleBooking(int id, String date, String time)` |
| POST   | `/bookings/{id}/confirm`           | `confirmBooking(int id)` |
| POST   | `/bookings/{id}/complete`          | `completeBooking(int id)` |
| POST   | `/bookings/{id}/dispute`           | `disputeBooking(int id, String reason)` |
| POST   | `/bookings/{id}/tip`               | `addTip(int id, double amount)` |
| GET    | `/bookings/{id}/receipt`           | `getReceipt(int id)` |
| POST   | `/bookings/{id}/review`            | `leaveReview(int id, int rating, String comment)` |
| GET    | `/bookings/upcoming`               | `listUpcomingBookings()` |
| GET    | `/bookings/past`                   | `listPastBookings()` |
| POST   | `/bookings/{id}/apply-coupon`      | `applyCoupon(int id, String code)` |
| POST   | `/bookings/{id}/notify-cleaner`    | `notifyCleaner(int id, String message)` |

#### List bookings

```java
XmlApiResponse<List<Booking>> resp = client.bookings().listBookings();

for (Booking b : resp.getData()) {
    System.out.printf("ID=%-5d  status=%-12s  date=%s%n",
            b.getId(), b.getStatus(), b.getDate());
}
```

#### Get booking

```java
XmlApiResponse<Booking> resp = client.bookings().getBooking(bookingId);
Booking b = resp.getData();

// Print XML representation
System.out.println(XmlConverter.toXml(b));
```

#### Create booking — typed parameters

```java
XmlApiResponse<Booking> resp = client.bookings().createBooking(
    "2025-09-15",   // date       (YYYY-MM-DD)
    "09:00",        // time       (HH:mm)
    1004,           // propertyId
    2,              // planId
    3.0,            // hours
    2,              // roomCount
    1,              // bathroomCount
    false,          // extraSupplies
    55              // paymentMethodId
);

Booking created = resp.getData();
System.out.println("Booking " + created.getId() + " → " + created.getStatus());
```

#### Create booking — map body

```java
Map<String, Object> body = new LinkedHashMap<>();
body.put("date",            "2025-09-15");
body.put("time",            "09:00");
body.put("propertyId",      1004);
body.put("planId",          2);
body.put("hours",           3.0);
body.put("roomCount",       2);
body.put("bathroomCount",   1);
body.put("extraSupplies",   false);
body.put("paymentMethodId", 55);

XmlApiResponse<Booking> resp = client.bookings().createBooking(body);
```

#### Update booking

```java
Map<String, Object> updates = new HashMap<>();
updates.put("notes", "Please bring eco-friendly supplies");
updates.put("hours", 4.0);

XmlApiResponse<Booking> resp = client.bookings().updateBooking(bookingId, updates);
```

#### Cancel booking

```java
// Simple cancel (DELETE)
client.bookings().cancelBooking(bookingId);

// Cancel with reason
client.bookings().cancelBooking(bookingId, "Schedule conflict");
```

#### Reschedule booking

```java
XmlApiResponse<Booking> resp = client.bookings()
        .rescheduleBooking(bookingId, "2025-10-01", "10:00");

Booking rescheduled = resp.getData();
System.out.println("New date: " + rescheduled.getDate()
        + " at " + rescheduled.getTime());
```

#### Apply coupon

```java
// Supported coupon codes: 100POFF, 50POFF, 20POFF, 200OFF, 100OFF
XmlApiResponse<Booking> resp = client.bookings().applyCoupon(bookingId, "100POFF");
System.out.println("New total: " + resp.getData().getTotalPrice());
```

#### Add tip

```java
XmlApiResponse<Booking> resp = client.bookings().addTip(bookingId, 20.0);
```

#### Leave review

```java
XmlApiResponse<Booking> resp = client.bookings()
        .leaveReview(bookingId, 5, "Fantastic job — spotless!");
```

#### Dispute booking

```java
XmlApiResponse<Booking> resp = client.bookings()
        .disputeBooking(bookingId, "Missed several areas in the kitchen");
```

#### Upcoming and past bookings

```java
List<Booking> upcoming = client.bookings().listUpcomingBookings().getData();
List<Booking> past     = client.bookings().listPastBookings().getData();
```

---

### Properties API

Manage the cleaning locations associated with a user account.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/properties`                        | `listProperties()` |
| GET    | `/properties/{id}`                   | `getProperty(int id)` |
| POST   | `/properties`                        | `createProperty(...)` |
| PUT    | `/properties/{id}`                   | `updateProperty(int id, Map body)` |
| DELETE | `/properties/{id}`                   | `deleteProperty(int id)` |
| GET    | `/properties/{id}/bookings`          | `getPropertyBookings(int id)` |
| GET    | `/properties/{id}/checklists`        | `getPropertyChecklists(int id)` |
| POST   | `/properties/{id}/archive`           | `archiveProperty(int id)` |
| POST   | `/properties/{id}/restore`           | `restoreProperty(int id)` |
| GET    | `/properties/active`                 | `listActiveProperties()` |
| GET    | `/properties/archived`               | `listArchivedProperties()` |
| POST   | `/properties/{id}/duplicate`         | `duplicateProperty(int id)` |
| GET    | `/properties/{id}/access-info`       | `getAccessInfo(int id)` |
| PUT    | `/properties/{id}/access-info`       | `updateAccessInfo(int id, String instructions)` |

#### Create property

```java
XmlApiResponse<Property> resp = client.properties().createProperty(
    "Sunset Villa",   // name
    "123 Ocean Dr",   // address
    "Miami Beach",    // city
    "FL",             // state
    "33139",          // zipCode
    "US",             // country
    3,                // roomCount
    2                 // bathroomCount
);

Property p = resp.getData();
System.out.println("Property ID: " + p.getId());

// XML output
System.out.println(XmlConverter.toXml(p));
```

#### Archive / restore

```java
client.properties().archiveProperty(propertyId);
client.properties().restoreProperty(propertyId);
```

#### Duplicate property

```java
XmlApiResponse<Property> copy = client.properties().duplicateProperty(propertyId);
System.out.println("New property: " + copy.getData().getId());
```

#### Access instructions

```java
// Read
XmlApiResponse<?> info = client.properties().getAccessInfo(propertyId);

// Update
client.properties().updateAccessInfo(propertyId, "Key under the front mat. Code 1234.");
```

---

### Checklists API

Define per-property cleaning checklists.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/checklists`       | `listChecklists()` |
| GET    | `/checklists/{id}`  | `getChecklist(int id)` |
| POST   | `/checklists`       | `createChecklist(String name, String desc, List<String> items, Integer propertyId)` |
| PUT    | `/checklists/{id}`  | `updateChecklist(int id, Map body)` |
| DELETE | `/checklists/{id}`  | `deleteChecklist(int id)` |

#### Create checklist

```java
List<String> items = List.of(
    "Vacuum all floors",
    "Wipe kitchen counters",
    "Clean both bathrooms",
    "Empty trash bins",
    "Change bed linens"
);

XmlApiResponse<Checklist> resp = client.checklists()
        .createChecklist("Standard Clean", "Regular weekly clean", items, propertyId);

Checklist c = resp.getData();
System.out.println(XmlConverter.toXml(c));
```

XML output includes the items collection:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<checklist>
    <id>7</id>
    <name>Standard Clean</name>
    <description>Regular weekly clean</description>
    <active>true</active>
    <items>
        <item>Vacuum all floors</item>
        <item>Wipe kitchen counters</item>
        <item>Clean both bathrooms</item>
        <item>Empty trash bins</item>
        <item>Change bed linens</item>
    </items>
</checklist>
```

---

### Payment Methods API

Add and manage saved payment cards.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/payment-methods`              | `listPaymentMethods()` |
| GET    | `/payment-methods/{id}`         | `getPaymentMethod(int id)` |
| POST   | `/payment-methods`              | `addPaymentMethod(String token, String holder)` |
| PUT    | `/payment-methods/{id}`         | `updatePaymentMethod(int id, Map body)` |
| DELETE | `/payment-methods/{id}`         | `deletePaymentMethod(int id)` |
| POST   | `/payment-methods/{id}/default` | `setDefaultPaymentMethod(int id)` |

#### Add and set default

```java
// Tokenise the card via Stripe.js on the frontend first
XmlApiResponse<PaymentMethod> resp = client.paymentMethods()
        .addPaymentMethod("tok_stripe_xyz", "Jane Doe");

int methodId = resp.getData().getId();

// Set as default
client.paymentMethods().setDefaultPaymentMethod(methodId);
```

#### List and serialise

```java
List<PaymentMethod> methods = client.paymentMethods().listPaymentMethods().getData();

for (PaymentMethod pm : methods) {
    System.out.println(XmlConverter.toXml(pm));
}
```

---

### Webhooks API

Register URLs to receive real-time event notifications.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/webhooks`       | `listWebhooks()` |
| GET    | `/webhooks/{id}`  | `getWebhook(int id)` |
| POST   | `/webhooks`       | `createWebhook(String url, List<String> events)` |
| DELETE | `/webhooks/{id}`  | `deleteWebhook(int id)` |

#### Supported events

| Event | Fired when |
|---|---|
| `booking.created`   | A new booking is made |
| `booking.confirmed` | A booking is confirmed |
| `booking.completed` | A cleaning is marked complete |
| `booking.cancelled` | A booking is cancelled |
| `booking.disputed`  | A dispute is opened |
| `payment.success`   | A payment succeeds |
| `payment.failed`    | A payment fails |

#### Register a webhook

```java
XmlApiResponse<Webhook> resp = client.webhooks().createWebhook(
    "https://your-server.com/hooks/cleanster",
    List.of("booking.created", "booking.completed", "booking.cancelled")
);

Webhook wh = resp.getData();
System.out.println("Webhook ID: " + wh.getId());
System.out.println("Secret:     " + wh.getSecret());
System.out.println(XmlConverter.toXml(wh));
```

---

### Blacklist API

Prevent specific cleaners from being assigned to bookings.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET    | `/blacklist`            | `listBlacklist()` |
| POST   | `/blacklist`            | `addToBlacklist(int userId, String reason)` |
| DELETE | `/blacklist/{userId}`   | `removeFromBlacklist(int userId)` |

#### Add to blacklist

```java
XmlApiResponse<BlacklistEntry> resp = client.blacklist()
        .addToBlacklist(cleanerId, "Repeated no-show");

BlacklistEntry entry = resp.getData();
System.out.println(XmlConverter.toXml(entry));
```

---

### Other API

Utility endpoints: plans, coupons, extras, chat rules, timeslots, and configuration.

#### Endpoints

| Method | Path | SDK method |
|---|---|---|
| GET  | `/plans`              | `listPlans()` |
| GET  | `/plans/{id}`         | `getPlan(int id)` |
| POST | `/coupons/validate`   | `validateCoupon(String code)` |
| GET  | `/extras`             | `listExtras()` |
| GET  | `/chat/rules`         | `getChatRules()` |
| GET  | `/timeslots`          | `getTimeslots(String date, Integer propertyId)` |
| GET  | `/config`             | `getConfig()` |

#### List plans and serialise

```java
List<Plan> plans = client.other().listPlans().getData();

for (Plan p : plans) {
    System.out.printf("%-20s  base=$%.2f  hourly=$%.2f%n",
            p.getName(), p.getBasePrice(), p.getHourlyRate());
    System.out.println(XmlConverter.toXml(p));
}
```

#### Validate coupon

```java
// Test coupon codes: 100POFF (100% off), 50POFF, 20POFF, 200OFF (fixed $200), 100OFF
XmlApiResponse<Coupon> resp = client.other().validateCoupon("100POFF");

if (resp.isSuccess()) {
    Coupon c = resp.getData();
    System.out.printf("Coupon %s: %s %.1f%n",
            c.getCode(), c.getType(), c.getValue());
} else {
    System.out.println("Invalid or expired coupon");
}
```

#### Get available timeslots

```java
XmlApiResponse<?> resp = client.other().getTimeslots("2025-09-15", propertyId);
System.out.println(resp.getData());
```

---

## Model Reference

### Booking Model

```java
Booking b = new Booking();
b.setId(12345);
b.setStatus("confirmed");      // pending | confirmed | completed | cancelled | disputed
b.setDate("2025-09-15");       // YYYY-MM-DD
b.setTime("09:00");            // HH:mm
b.setHours(3.0);
b.setPropertyId(1004);
b.setPropertyName("Sunset Villa");
b.setPlanId(2);
b.setPlanName("Standard Clean");
b.setRoomCount(2);
b.setBathroomCount(1);
b.setExtraSupplies(false);
b.setTotalPrice(120.0);
b.setCurrency("USD");
b.setPaymentMethodId(55);
b.setNotes("Ring doorbell on arrival");
b.setCreatedAt("2025-08-01T10:00:00Z");
b.setUpdatedAt("2025-08-01T10:00:00Z");
b.setCancelledAt(null);
b.setCancelReason(null);
```

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Booking identifier |
| `status` | String | `pending`, `confirmed`, `completed`, `cancelled`, `disputed` |
| `date` | String | Service date (YYYY-MM-DD) |
| `time` | String | Start time (HH:mm) |
| `hours` | Double | Duration in hours |
| `propertyId` | Integer | Associated property |
| `planId` | Integer | Cleaning plan |
| `roomCount` | Integer | Number of rooms |
| `bathroomCount` | Integer | Number of bathrooms |
| `extraSupplies` | Boolean | Whether to bring extra supplies |
| `totalPrice` | Double | Total cost |
| `currency` | String | ISO 4217 currency code |
| `paymentMethodId` | Integer | Stored payment method |
| `notes` | String | Special instructions |
| `cancelReason` | String | Reason for cancellation |

---

### Property Model

```java
Property p = new Property();
p.setId(1004);
p.setName("Sunset Villa");
p.setAddress("123 Ocean Drive");
p.setCity("Miami Beach");
p.setState("FL");
p.setZipCode("33139");
p.setCountry("US");
p.setRoomCount(3);
p.setBathroomCount(2);
p.setSquareFootage(1800.0);
p.setPropertyType("house");   // house | apartment | office | other
p.setActive(true);
p.setNotes("Has a pool — avoid slippery tiles");
p.setAccessInstructions("Key under the welcome mat");
```

---

### User Model

```java
User u = new User();
u.setId(7);
u.setEmail("partner@example.com");
u.setFirstName("Alice");
u.setLastName("Smith");
u.setPhone("+1-555-0100");
u.setToken("eyJ...");          // JWT session token
u.setRole("partner");
u.setActive(true);
```

---

### Checklist Model

```java
Checklist c = new Checklist();
c.setId(3);
c.setName("Move-Out Deep Clean");
c.setDescription("Full deep clean for end-of-tenancy inspection");
c.setPropertyId(1004);
c.setActive(true);
c.setItems(List.of(
    "Clean inside oven",
    "Degrease extractor fan",
    "Scrub tile grout",
    "Wash all windows"
));
```

XML serialisation with a list collection:

```xml
<checklist>
    <id>3</id>
    <name>Move-Out Deep Clean</name>
    <items>
        <item>Clean inside oven</item>
        <item>Degrease extractor fan</item>
        <item>Scrub tile grout</item>
        <item>Wash all windows</item>
    </items>
</checklist>
```

---

### PaymentMethod Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Method identifier |
| `type` | String | `card` |
| `brand` | String | `Visa`, `Mastercard`, `Amex`, `Discover` |
| `last4` | String | Last 4 digits |
| `expMonth` | Integer | Expiry month (1–12) |
| `expYear` | Integer | Expiry year (4-digit) |
| `isDefault` | Boolean | Whether this is the default method |
| `holderName` | String | Cardholder name |

---

### Webhook Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Webhook identifier |
| `url` | String | HTTPS endpoint URL |
| `active` | Boolean | Whether the webhook is active |
| `events` | List<String> | Subscribed event types |
| `secret` | String | HMAC-SHA256 signing secret |

---

### BlacklistEntry Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Entry identifier |
| `userId` | Integer | Blacklisted user's ID |
| `reason` | String | Reason for blacklisting |
| `createdAt` | String | ISO-8601 timestamp |

---

### Plan Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Plan identifier |
| `name` | String | e.g. Standard, Deep Clean, Move-In/Out |
| `description` | String | Plan description |
| `basePrice` | Double | Starting price |
| `hourlyRate` | Double | Additional hourly rate |
| `currency` | String | ISO 4217 |
| `active` | Boolean | Whether the plan is bookable |

---

### Coupon Model

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Coupon identifier |
| `code` | String | Coupon code (e.g. `100POFF`) |
| `type` | String | `percent` or `fixed` |
| `value` | Double | Discount amount/percentage |
| `active` | Boolean | Whether the coupon is valid |
| `expiresAt` | String | ISO-8601 expiry |
| `usageLimit` | Integer | Maximum redemptions |
| `usageCount` | Integer | Current redemption count |

---

## XML Schema Examples

### Full Booking XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<booking>
    <id>12345</id>
    <status>confirmed</status>
    <date>2025-09-15</date>
    <time>09:00</time>
    <hours>3.0</hours>
    <propertyId>1004</propertyId>
    <propertyName>Sunset Villa</propertyName>
    <planId>2</planId>
    <planName>Standard Clean</planName>
    <roomCount>2</roomCount>
    <bathroomCount>1</bathroomCount>
    <extraSupplies>false</extraSupplies>
    <totalPrice>120.0</totalPrice>
    <currency>USD</currency>
    <paymentMethodId>55</paymentMethodId>
    <notes>Ring doorbell on arrival</notes>
    <createdAt>2025-08-01T10:00:00Z</createdAt>
    <updatedAt>2025-08-01T10:00:00Z</updatedAt>
</booking>
```

### Full Property XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<property>
    <id>1004</id>
    <name>Sunset Villa</name>
    <address>123 Ocean Drive</address>
    <city>Miami Beach</city>
    <state>FL</state>
    <zipCode>33139</zipCode>
    <country>US</country>
    <roomCount>3</roomCount>
    <bathroomCount>2</bathroomCount>
    <squareFootage>1800.0</squareFootage>
    <propertyType>house</propertyType>
    <active>true</active>
    <accessInstructions>Key under welcome mat</accessInstructions>
</property>
```

### Webhook with events XML

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<webhook>
    <id>9</id>
    <url>https://your-server.com/hooks/cleanster</url>
    <active>true</active>
    <events>
        <event>booking.created</event>
        <event>booking.completed</event>
        <event>booking.cancelled</event>
    </events>
    <secret>whsec_abc123xyz</secret>
    <createdAt>2025-01-01T00:00:00Z</createdAt>
</webhook>
```

---

## All 59 Endpoints

### Users (3)

| # | Method | Path | Description |
|---|---|---|---|
| 1 | POST | `/users/{userId}/token` | Fetch access token |
| 2 | GET  | `/users/{userId}` | Get user profile |
| 3 | PUT  | `/users/{userId}` | Update user profile |

### Bookings (17)

| # | Method | Path | Description |
|---|---|---|---|
| 4  | GET    | `/bookings` | List all bookings |
| 5  | GET    | `/bookings/{id}` | Get booking |
| 6  | POST   | `/bookings` | Create booking |
| 7  | PUT    | `/bookings/{id}` | Update booking |
| 8  | DELETE | `/bookings/{id}` | Cancel booking |
| 9  | POST   | `/bookings/{id}/cancel` | Cancel with reason |
| 10 | POST   | `/bookings/{id}/reschedule` | Reschedule booking |
| 11 | POST   | `/bookings/{id}/confirm` | Confirm booking |
| 12 | POST   | `/bookings/{id}/complete` | Mark complete |
| 13 | POST   | `/bookings/{id}/dispute` | Dispute booking |
| 14 | POST   | `/bookings/{id}/tip` | Add tip |
| 15 | GET    | `/bookings/{id}/receipt` | Get receipt |
| 16 | POST   | `/bookings/{id}/review` | Leave review |
| 17 | GET    | `/bookings/upcoming` | Upcoming bookings |
| 18 | GET    | `/bookings/past` | Past bookings |
| 19 | POST   | `/bookings/{id}/apply-coupon` | Apply coupon code |
| 20 | POST   | `/bookings/{id}/notify-cleaner` | Notify cleaner |

### Properties (14)

| # | Method | Path | Description |
|---|---|---|---|
| 21 | GET    | `/properties` | List properties |
| 22 | GET    | `/properties/{id}` | Get property |
| 23 | POST   | `/properties` | Create property |
| 24 | PUT    | `/properties/{id}` | Update property |
| 25 | DELETE | `/properties/{id}` | Delete property |
| 26 | GET    | `/properties/{id}/bookings` | Property bookings |
| 27 | GET    | `/properties/{id}/checklists` | Property checklists |
| 28 | POST   | `/properties/{id}/archive` | Archive property |
| 29 | POST   | `/properties/{id}/restore` | Restore property |
| 30 | GET    | `/properties/active` | Active properties |
| 31 | GET    | `/properties/archived` | Archived properties |
| 32 | POST   | `/properties/{id}/duplicate` | Duplicate property |
| 33 | GET    | `/properties/{id}/access-info` | Get access instructions |
| 34 | PUT    | `/properties/{id}/access-info` | Update access instructions |

### Checklists (5)

| # | Method | Path | Description |
|---|---|---|---|
| 35 | GET    | `/checklists` | List checklists |
| 36 | GET    | `/checklists/{id}` | Get checklist |
| 37 | POST   | `/checklists` | Create checklist |
| 38 | PUT    | `/checklists/{id}` | Update checklist |
| 39 | DELETE | `/checklists/{id}` | Delete checklist |

### Payment Methods (6)

| # | Method | Path | Description |
|---|---|---|---|
| 40 | GET    | `/payment-methods` | List payment methods |
| 41 | GET    | `/payment-methods/{id}` | Get payment method |
| 42 | POST   | `/payment-methods` | Add payment method |
| 43 | PUT    | `/payment-methods/{id}` | Update payment method |
| 44 | DELETE | `/payment-methods/{id}` | Delete payment method |
| 45 | POST   | `/payment-methods/{id}/default` | Set as default |

### Webhooks (4)

| # | Method | Path | Description |
|---|---|---|---|
| 46 | GET    | `/webhooks` | List webhooks |
| 47 | GET    | `/webhooks/{id}` | Get webhook |
| 48 | POST   | `/webhooks` | Create webhook |
| 49 | DELETE | `/webhooks/{id}` | Delete webhook |

### Blacklist (3)

| # | Method | Path | Description |
|---|---|---|---|
| 50 | GET    | `/blacklist` | List blacklisted users |
| 51 | POST   | `/blacklist` | Add user to blacklist |
| 52 | DELETE | `/blacklist/{userId}` | Remove from blacklist |

### Other (7)

| # | Method | Path | Description |
|---|---|---|---|
| 53 | GET  | `/plans` | List cleaning plans |
| 54 | GET  | `/plans/{id}` | Get plan |
| 55 | POST | `/coupons/validate` | Validate coupon code |
| 56 | GET  | `/extras` | List available extras |
| 57 | GET  | `/chat/rules` | Chat window rules |
| 58 | GET  | `/timeslots` | Available time slots |
| 59 | GET  | `/config` | Partner configuration |

---

## Testing

The test suite covers **164 tests** across 8 test classes.  Each test uses
OkHttp `MockWebServer` to intercept HTTP calls locally — no real API credentials are needed.

### Run all tests

```bash
mvn test
```

### Test breakdown

| Test class | Tests | What it covers |
|---|---|---|
| `BookingsTest` | 47 | All 17 booking endpoints + Booking XML round-trip |
| `PropertiesTest` | 26 | All 14 property endpoints + Property XML round-trip |
| `OtherTest` | 21 | All 7 other endpoints + Plan/Coupon XML round-trip |
| `ChecklistsTest` | 18 | All 5 checklist endpoints + Checklist XML round-trip |
| `UsersTest` | 18 | All 3 user endpoints + User XML round-trip + `XmlConverter` utility |
| `PaymentMethodsTest` | 14 | All 6 payment method endpoints + PaymentMethod XML round-trip |
| `WebhooksTest` | 11 | All 4 webhook endpoints + Webhook XML round-trip |
| `BlacklistTest` | 9 | All 3 blacklist endpoints + BlacklistEntry XML round-trip |
| **Total** | **164** | |

### What the tests verify

For every endpoint test:

1. **HTTP method** is correct (GET / POST / PUT / DELETE / PATCH)
2. **URL path** is correct (e.g. `/bookings/10/reschedule`)
3. **Request body** contains expected fields
4. **Response** is correctly deserialized from JSON to typed objects

For every XML test:

1. `XmlConverter.toXml(obj)` produces valid XML with the expected element names
2. `XmlConverter.fromXml(xml, Class)` correctly round-trips all fields
3. Collections (`List<String>`) are correctly wrapped in parent elements

### Run a specific test class

```bash
mvn test -Dtest=BookingsTest
mvn test -Dtest=PropertiesTest
mvn test -Dtest=UsersTest
```

### Inspect test output

```bash
mvn test -pl xml-sdk 2>&1 | grep -E "Tests run|FAIL|ERROR"
```

---

## Building

### Build and run tests

```bash
mvn clean test
```

### Build without tests

```bash
mvn clean package -DskipTests
```

### Build fat JAR (with all dependencies)

Add the Maven Shade plugin to `pom.xml` and run:

```bash
mvn clean package -Pshade
```

### Check for dependency updates

```bash
mvn versions:display-dependency-updates
```

---

## Configuration Reference

### Client factories

| Factory method | Description |
|---|---|
| `CleansterXmlClient.sandbox("key")` | Targets the sandbox environment |
| `CleansterXmlClient.production("key")` | Targets the production environment |
| `CleansterXmlClient.custom(url, key, httpClient)` | Custom base URL + OkHttp client (for testing) |

### Token management

```java
client.setToken("your-session-token");  // persist the token
String token = client.getToken();       // retrieve the stored token
```

### Timeouts

The default connection and read timeout is 30 seconds.  To customise, create an
`OkHttpClient` manually and pass it to the `custom()` factory:

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

### XmlConverter defaults

`XmlConverter.toXml()` always produces:
- UTF-8 encoding
- Formatted (indented) output
- Standard JAXB XML prolog: `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>`

---

## Integration Patterns

### Writing XML bookings to disk

```java
XmlApiResponse<List<Booking>> resp = client.bookings().listBookings();

for (Booking b : resp.getData()) {
    String xml      = XmlConverter.toXml(b);
    String filename = "booking-" + b.getId() + ".xml";
    Files.writeString(Path.of(filename), xml, StandardCharsets.UTF_8);
    System.out.println("Wrote " + filename);
}
```

### XML message queue integration

```java
// Producer: serialize to XML and put on queue
Booking booking = client.bookings().createBooking(...).getData();
String xml = XmlConverter.toXml(booking);
myMessageQueue.send("bookings", xml);

// Consumer: receive XML and deserialise
String received = myMessageQueue.receive("bookings");
Booking b = XmlConverter.fromXml(received, Booking.class);
System.out.println("Processing booking " + b.getId());
```

### Transforming to XSLT

```java
String xml = XmlConverter.toXml(booking);

// Use any standard JAXP transformer
TransformerFactory factory = TransformerFactory.newInstance();
Transformer transformer = factory.newTransformer(
        new StreamSource(new File("booking-template.xslt")));
transformer.transform(
        new StreamSource(new StringReader(xml)),
        new StreamResult(System.out));
```

### SOAP/WS integration

All Cleanster models are standard JAXB-annotated POJOs, making them compatible with any
`jakarta.xml.ws` or `javax.jws` binding:

```java
@WebService
public class CleansterBookingService {

    private final CleansterXmlClient client =
            CleansterXmlClient.production(System.getenv("CLEANSTER_ACCESS_KEY"));

    @WebMethod
    public Booking createBooking(String date, String time, int propertyId) {
        XmlApiResponse<Booking> resp = client.bookings()
                .createBooking(date, time, propertyId, 2, 3.0, 2, 1, false, 55);
        return resp.getData();
    }
}
```

---

## Booking Lifecycle

Cleanster bookings move through a well-defined state machine:

```
                ┌─────────────┐
   createBooking│             │
  ──────────────►   PENDING   │
                │             │
                └──────┬──────┘
                       │ confirmBooking()
                       ▼
                ┌─────────────┐
                │  CONFIRMED  │─────────────┐
                └──────┬──────┘             │ disputeBooking()
                       │ completeBooking()  │
                       ▼                    ▼
                ┌─────────────┐      ┌─────────────┐
                │  COMPLETED  │      │  DISPUTED   │
                └─────────────┘      └─────────────┘

 Any state → CANCELLED via cancelBooking() or cancelBooking(id, reason)
 CONFIRMED  → rescheduled via rescheduleBooking(id, date, time)
 COMPLETED  → review via leaveReview(), tip via addTip()
```

### State transition methods

| From state | Action | Method |
|---|---|---|
| PENDING | Confirm the booking | `confirmBooking(id)` |
| PENDING / CONFIRMED | Cancel | `cancelBooking(id)` |
| CONFIRMED | Reschedule | `rescheduleBooking(id, date, time)` |
| CONFIRMED | Mark complete | `completeBooking(id)` |
| CONFIRMED | Dispute | `disputeBooking(id, reason)` |
| COMPLETED | Leave review | `leaveReview(id, rating, comment)` |
| COMPLETED | Add tip | `addTip(id, amount)` |
| CONFIRMED | Apply coupon | `applyCoupon(id, code)` |

---

## Chat Window Rules

The `/chat/rules` endpoint (`getChatRules()`) returns the partner's configured chat window
behaviour:

```java
XmlApiResponse<?> resp = client.other().getChatRules();
System.out.println(resp.getData());
```

Rules typically include:
- **maxMessages** — maximum messages per conversation
- **messageWindowHours** — hours after booking when messaging is open
- **allowAttachments** — whether file attachments are permitted
- **autoCloseAfterHours** — hours of inactivity before thread is closed

These rules apply to the in-app messaging thread between the partner customer and the
assigned cleaner.

---

## Changelog

### v1.0.0

- Initial release
- 59 endpoints across 8 API groups
- Full JAXB 4.0 annotation on all model classes
- `XmlConverter` utility (toXml / fromXml / isXml)
- `CleansterXmlClient` with sandbox, production, and custom factories
- OkHttp 4.12.0 transport with configurable timeouts
- 164 tests with MockWebServer

---

## Support

| Channel | Contact |
|---|---|
| **API Documentation** | [Postman Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep) |
| **Partner inquiries** | [partner@cleanster.com](mailto:partner@cleanster.com) |
| **General support**   | [support@cleanster.com](mailto:support@cleanster.com) |
| **Bug reports**       | Open an issue on GitHub |

---

## License

MIT License — see [LICENSE](../LICENSE) for details.
