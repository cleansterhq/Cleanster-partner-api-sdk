# Cleanster Partner API — Official SDKs

<p align="center">
  <strong>Multi-language SDKs for the Cleanster Partner API</strong><br>
  Integrate professional cleaning service automation into your platform.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/API-Cleanster%20Partner-brightgreen" alt="Cleanster Partner API">
  <img src="https://img.shields.io/badge/SDKs-12%20Languages-blue" alt="12 Languages">
  <img src="https://img.shields.io/badge/MCP%20Server-Claude%20%7C%20AI-blueviolet" alt="MCP Server">
  <img src="https://img.shields.io/badge/Endpoints-62-orange" alt="62 Endpoints">
  <img src="https://img.shields.io/badge/Tests-1500%20passing-success" alt="1500 Tests Passing">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT License">
</p>

---

## Table of Contents

- [Available SDKs](#available-sdks)
- [What the API Does](#what-the-api-does)
- [Environments](#environments)
- [Authentication](#authentication)
- [Installation & Quick Start](#installation--quick-start)
  - [Java](#java)
  - [Python](#python)
  - [TypeScript / Node.js](#typescript--nodejs)
  - [Ruby](#ruby)
  - [Go](#go)
  - [PHP](#php)
  - [C# / .NET](#c--net)
  - [Swift](#swift)
  - [Kotlin](#kotlin)
  - [XML (JAXB)](#xml)
  - [SOAP](#soap)
  - [Android (Retrofit)](#android)
  - [MCP Server (Claude / AI)](#mcp-server)
- [Standard Response Format](#standard-response-format)
- [Error Handling](#error-handling)
- [All 62 Endpoints](#all-62-endpoints)
  - [Users](#users-api)
  - [Properties](#properties-api)
  - [Bookings](#bookings-api)
  - [Checklists](#checklists-api)
  - [Payment Methods](#payment-methods-api)
  - [Webhooks](#webhooks-api)
  - [Blacklist](#blacklist-api)
  - [Reference Data](#reference-data-api)
- [Integration Workflow](#integration-workflow)
- [Booking Lifecycle](#booking-lifecycle)
- [Chat Window Rules](#chat-window-rules)
- [Webhook Events](#webhook-events)
- [Test Coupon Codes](#test-coupon-codes)
- [Repository Structure](#repository-structure)
- [Support](#support)

---

## Available SDKs

| Language | Folder | Tests Passing | Min Version | Package Manager |
|---|---|---|---|---|
| [Java](#java) | [`java-sdk/`](./java-sdk) | 78 | Java 11+ | Maven / Gradle |
| [Python](#python) | [`python-sdk/`](./python-sdk) | 103 | Python 3.8+ | pip |
| [TypeScript / Node.js](#typescript--nodejs) | [`typescript-sdk/`](./typescript-sdk) | 89 | Node.js 18+ | npm |
| [Ruby](#ruby) | [`ruby-sdk/`](./ruby-sdk) | 123 | Ruby 2.7+ | gem |
| [Go](#go) | [`go-sdk/`](./go-sdk) | 96 | Go 1.21+ | go get |
| [PHP](#php) | [`php-sdk/`](./php-sdk) | 110 | PHP 8.1+ | Composer |
| [C# / .NET](#c--net) | [`csharp-sdk/`](./csharp-sdk) | 111 | .NET 8.0+ | NuGet |
| [Swift](#swift) | [`swift-sdk/`](./swift-sdk) | 170 | Swift 5.9+ / iOS 16+ | Swift Package Manager |
| [Kotlin](#kotlin) | [`kotlin-sdk/`](./kotlin-sdk) | 170 | Kotlin 1.9+ / JVM 11+ | Gradle |
| [XML (JAXB)](#xml) | [`xml-sdk/`](./xml-sdk) | 164 | Java 17+ / JAXB 4.0 | Maven |
| [SOAP](#soap) | [`soap-sdk/`](./soap-sdk) | 118 | Java 11+ | Maven |
| [Android (Retrofit)](#android) | [`android-sdk/`](./android-sdk) | 168 | Android API 26+ / Kotlin 1.9+ | Gradle |

**1,500 tests passing across all SDKs.**

### AI / Agentic Integration

| Integration | Folder | Tests | Runtime | Description |
|---|---|---|---|---|
| [MCP Server](#mcp-server) | [`mcp-server/`](./mcp-server) | 67 | Node.js 20+ | Model Context Protocol server for Claude and AI assistants |

---

## What the API Does

The Cleanster Partner API is a white-label backend for cleaning service platforms. It lets you embed fully-managed cleaning operations — booking scheduling, cleaner dispatch, payment processing, chat, inspections, and webhooks — into your own product without building any of that infrastructure yourself.

**Core capabilities:**

| Capability | Description |
|---|---|
| **User management** | Create end-user accounts and issue authenticated API tokens |
| **Property management** | Register cleaning locations, sync iCal calendars, manage property-level cleaners |
| **Booking lifecycle** | Schedule, reschedule, cancel, assign cleaners, adjust hours |
| **Post-booking actions** | Pay expenses, add tips, submit feedback, view inspection reports |
| **In-booking chat** | Send and delete messages during the service window |
| **Checklists** | Create reusable task lists and attach them per-booking or per-property |
| **Payment methods** | Attach Stripe cards and PayPal accounts to user profiles |
| **Webhooks** | Receive real-time events for every stage of a booking |
| **Blacklist** | Prevent specific cleaners from being assigned to your properties |
| **Reference data** | Fetch service types, plans, pricing, extras, available cleaners |

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

All SDKs target **sandbox** by default. You explicitly select production at client construction time.

**Sandbox behaviour:**
- All bookings and users are isolated from production data
- Payments are simulated — no real charges
- Use the [test coupon codes](#test-coupon-codes) to validate discount logic
- Webhooks fire in real time against sandbox events

---

## Authentication

Every request to the API requires **two** HTTP headers:

| Header | Type | Description |
|---|---|---|
| `access-key` | Static string | Your partner key — issued by Cleanster, never changes |
| `token` | JWT string | A per-user bearer token — fetched per user via the Users API |

**Flow:**
1. Cleanster issues you a single `access-key` for your platform.
2. When a user signs up in your platform, call `POST /v1/user/account` to create their Cleanster account.
3. Call `GET /v1/user/access-token/{userId}` to get a long-lived JWT for that user.
4. Store the JWT securely in your backend. Pass it as the `token` header on all subsequent calls made on behalf of that user.
5. The JWT can be validated at any time with `POST /v1/user/verify-jwt`.

Contact [partner@cleanster.com](mailto:partner@cleanster.com) to obtain your `access-key`.

---

## Installation & Quick Start

### Java

**Maven:**
```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'com.cleanster:cleanster-sdk:1.0.0'
```

**Sandbox client:**
```java
import com.cleanster.sdk.CleansterClient;
import com.cleanster.sdk.model.*;

CleansterClient client = CleansterClient.sandbox("your-access-key");
client.setToken("user-jwt-token"); // set per-user token

// Create a booking
CreateBookingRequest req = new CreateBookingRequest();
req.setDate("2025-09-15");
req.setTime("10:00");
req.setPropertyId(1004);
req.setPlanId(2);
req.setHours(3.0);
req.setRoomCount(2);
req.setBathroomCount(1);
req.setExtraSupplies(false);
req.setPaymentMethodId(55);

ApiResponse<Booking> resp = client.bookings().createBooking(req);
System.out.println("Booking ID: " + resp.getData().getId());
```

**Production client:**
```java
CleansterClient client = CleansterClient.production("your-access-key");
```

[Full Java documentation →](./java-sdk/README.md)

---

### Python

```bash
pip install cleanster
```

**Sandbox client:**
```python
from cleanster import CleansterClient

client = CleansterClient.sandbox(access_key="your-access-key")
client.token = "user-jwt-token"  # set per-user token

# Create a booking
response = client.bookings.create_booking({
    "date": "2025-09-15",
    "time": "10:00",
    "propertyId": 1004,
    "planId": 2,
    "hours": 3.0,
    "roomCount": 2,
    "bathroomCount": 1,
    "extraSupplies": False,
    "paymentMethodId": 55,
})
print(f"Booking ID: {response.data['id']}")
```

**Production client:**
```python
client = CleansterClient.production(access_key="your-access-key")
```

[Full Python documentation →](./python-sdk/README.md)

---

### TypeScript / Node.js

```bash
npm install cleanster
# or
yarn add cleanster
```

**Sandbox client:**
```typescript
import { CleansterClient } from 'cleanster';

const client = CleansterClient.sandbox({ accessKey: 'your-access-key' });
client.setToken('user-jwt-token');  // set per-user token

// Create a booking
const response = await client.bookings.createBooking({
  date: '2025-09-15',
  time: '10:00',
  propertyId: 1004,
  planId: 2,
  hours: 3.0,
  roomCount: 2,
  bathroomCount: 1,
  extraSupplies: false,
  paymentMethodId: 55,
});
console.log('Booking ID:', response.data.id);
```

**Production client:**
```typescript
const client = CleansterClient.production({ accessKey: 'your-access-key' });
```

[Full TypeScript documentation →](./typescript-sdk/README.md)

---

### Ruby

**Gemfile:**
```ruby
gem 'cleanster'
```

```bash
bundle install
```

**Sandbox client:**
```ruby
require 'cleanster'

client = Cleanster::Client.sandbox(access_key: 'your-access-key')
client.token = 'user-jwt-token'  # set per-user token

# Create a booking
response = client.bookings.create_booking(
  date: '2025-09-15',
  time: '10:00',
  property_id: 1004,
  plan_id: 2,
  hours: 3.0,
  room_count: 2,
  bathroom_count: 1,
  extra_supplies: false,
  payment_method_id: 55
)
puts "Booking ID: #{response.data['id']}"
```

**Production client:**
```ruby
client = Cleanster::Client.production(access_key: 'your-access-key')
```

[Full Ruby documentation →](./ruby-sdk/README.md)

---

### Go

```bash
go get github.com/cleanster/cleanster-go-sdk
```

**Sandbox client:**
```go
package main

import (
    "context"
    "fmt"
    cleanster "github.com/cleanster/cleanster-go-sdk"
)

func main() {
    client := cleanster.NewSandboxClient("your-access-key")
    client.SetToken("user-jwt-token")  // set per-user token

    ctx := context.Background()

    // Create a booking
    resp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{
        Date:            "2025-09-15",
        Time:            "10:00",
        PropertyID:      1004,
        PlanID:          2,
        Hours:           3.0,
        RoomCount:       2,
        BathroomCount:   1,
        ExtraSupplies:   false,
        PaymentMethodID: 55,
    })
    if err != nil {
        panic(err)
    }
    fmt.Println("Booking ID:", resp.Data.ID)
}
```

**Production client:**
```go
client := cleanster.NewProductionClient("your-access-key")
```

[Full Go documentation →](./go-sdk/README.md)

---

### PHP

```bash
composer require cleanster/cleanster-php-sdk
```

**Sandbox client:**
```php
<?php
require 'vendor/autoload.php';

use Cleanster\CleansterClient;

$client = CleansterClient::sandbox('your-access-key');
$client->setToken('user-jwt-token');  // set per-user token

// Create a booking
$response = $client->bookings()->createBooking([
    'date'            => '2025-09-15',
    'time'            => '10:00',
    'propertyId'      => 1004,
    'planId'          => 2,
    'hours'           => 3.0,
    'roomCount'       => 2,
    'bathroomCount'   => 1,
    'extraSupplies'   => false,
    'paymentMethodId' => 55,
]);
echo "Booking ID: " . $response->data->id;
```

**Production client:**
```php
$client = CleansterClient::production('your-access-key');
```

[Full PHP documentation →](./php-sdk/README.md)

---

### C# / .NET

```bash
dotnet add package Cleanster
```

**Sandbox client:**
```csharp
using Cleanster;

var client = CleansterClient.Sandbox("your-access-key");
client.SetToken("user-jwt-token");  // set per-user token

// Create a booking
var response = await client.Bookings.CreateBookingAsync(
    date:            "2025-09-15",
    time:            "10:00",
    propertyId:      1004,
    planId:          2,
    hours:           3.0,
    roomCount:       2,
    bathroomCount:   1,
    extraSupplies:   false,
    paymentMethodId: 55
);
Console.WriteLine($"Booking ID: {response.Data?.GetProperty("id")}");
```

**Production client:**
```csharp
var client = CleansterClient.Production("your-access-key");
```

[Full C# documentation →](./csharp-sdk/README.md)

---

### Swift

**Package.swift:**
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

Or in Xcode: **File → Add Package Dependencies** and enter the repository URL.

**Sandbox client:**
```swift
import Cleanster

let client = CleansterClient.sandbox(accessKey: "your-access-key")

// Fetch and set the per-user token
let tokenResp = try await client.users.fetchAccessToken(12345)
client.setToken(tokenResp.data?.token ?? "")

// Create a booking
let response = try await client.bookings.createBooking(
    CreateBookingRequest(
        date: "2025-09-15",
        time: "10:00",
        propertyId: 1004,
        planId: 2,
        hours: 3.0,
        roomCount: 2,
        bathroomCount: 1,
        extraSupplies: false,
        paymentMethodId: 55
    )
)
print("Booking ID:", response.data?.id ?? 0)
```

**Production client:**
```swift
let client = CleansterClient.production(accessKey: "your-access-key")
```

[Full Swift documentation →](./swift-sdk/README.md)

---

### Kotlin

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.cleanster:cleanster-kotlin-sdk:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

**Sandbox client:**
```kotlin
import com.cleanster.CleansterClient

val client = CleansterClient.sandbox("your-access-key")

// Fetch and set the per-user token
val tokenResp = client.users.fetchAccessToken(12345)
client.setToken(tokenResp.data?.token ?: "")

// Create a booking
val response = client.bookings.createBooking(
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
println("Booking ID: ${response.data?.id}")
```

**Production client:**
```kotlin
val client = CleansterClient.production("your-access-key")
```

[Full Kotlin documentation →](./kotlin-sdk/README.md)

---

### XML

The XML SDK targets enterprise and integration-heavy Java environments where XML is the
preferred data format.  Every model class carries full **JAXB 4.0** annotations so any
response object can be serialised to XML with a single call to `XmlConverter.toXml()`.

**Maven:**
```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-xml-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
<dependency>
  <groupId>jakarta.xml.bind</groupId>
  <artifactId>jakarta.xml.bind-api</artifactId>
  <version>4.0.0</version>
</dependency>
<dependency>
  <groupId>com.sun.xml.bind</groupId>
  <artifactId>jaxb-impl</artifactId>
  <version>4.0.3</version>
  <scope>runtime</scope>
</dependency>
```

**Quick start:**
```java
import com.cleanster.xml.client.CleansterXmlClient;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.model.*;

// Create a sandbox client
CleansterXmlClient client = CleansterXmlClient.sandbox("your-access-key");

// Authenticate
XmlApiResponse<User> tokenResp = client.users().fetchAccessToken(userId);
client.setToken(tokenResp.getData().getToken());

// Create a booking
Booking booking = client.bookings().createBooking(
    "2025-09-15", "09:00", 1004, 2, 3.0, 2, 1, false, 55
).getData();

// Serialise to XML
String xml = XmlConverter.toXml(booking);
System.out.println(xml);
// <?xml version="1.0" encoding="UTF-8"?>
// <booking>
//   <id>12345</id>
//   <status>pending</status>
//   ...
// </booking>

// Deserialise from XML
Booking restored = XmlConverter.fromXml(xml, Booking.class);
```

**Production client:**
```java
CleansterXmlClient client = CleansterXmlClient.production("your-access-key");
```

[Full XML documentation →](./xml-sdk/README.md)

---

### SOAP

The SOAP SDK exposes all 62 Cleanster Partner API operations through a document/literal SOAP 1.1 interface backed by a WSDL and XSD schema. Ideal for enterprise systems that require SOAP-based integration. Internally it bridges to the Cleanster REST API via `SOAPTransport`.

**Maven:**
```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-soap-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Quick start:**
```java
import com.cleanster.soap.CleansterSOAPClient;
import com.cleanster.soap.model.*;

CleansterSOAPClient client = new CleansterSOAPClient("your-api-key");

// Bookings
Booking booking = client.getBooking(16459);
List<Booking> all = client.listBookings(new ListBookingsRequest().setStatus("scheduled"));

// Properties
Property property = client.createProperty(new CreatePropertyRequest()
    .setAddress("456 Oak Ave").setCity("Atlanta").setState("GA").setZip("30301"));
client.addICalLink(property.getId(), "https://airbnb.com/calendar.ics");

// Checklists
Checklist checklist = client.createChecklist("Deep Clean", List.of("Oven", "Bathrooms"));
client.uploadChecklistImage(checklist.getId(), imageBytes, "guide.jpg");

// Users & auth
User user = client.createUser(new CreateUserRequest().setEmail("alice@example.com"));
client.verifyJwt(client.fetchAccessToken(user.getId()).getAccessToken());

// Payment methods
PaymentMethod pm = client.addPaymentMethod("pm_test_123");
client.setDefaultPaymentMethod(pm.getId());

// Webhooks
Webhook hook = client.createWebhook("https://myapp.com/hook", "booking.completed");

// Blacklist
client.addToBlacklist(789L, "Repeated no-shows");
```

**Sending raw SOAP with cURL:**
```bash
curl -X POST https://api.cleanster.com/soap \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: https://api.cleanster.com/soap/GetBooking" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d @soap-sdk/examples/GetBooking.xml
```

[Full SOAP documentation →](./soap-sdk/README.md)

---

### Android

The Android SDK wraps the Cleanster Partner API in a type-safe, coroutines-ready [Retrofit 2](https://square.github.io/retrofit/) client. All 62 API endpoints across 8 resource types are covered with full Kotlin data-class request/response models.

**Gradle (Kotlin DSL):**
```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.cleanster:cleanster-android-sdk:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

**Quick start:**
```kotlin
import com.cleanster.android.CleansterClient
import com.cleanster.android.CleansterConfig

val client = CleansterClient(CleansterConfig(apiKey = "YOUR_API_KEY"))

// Bookings
val booking = client.bookings.getBooking(16459)
val list = client.bookings.listBookings(status = "scheduled")

// Properties
val property = client.properties.createProperty(
    address = "456 Oak Ave", city = "Atlanta", state = "GA", zip = "30301"
)

// Users
val user = client.users.createUser(email = "alice@example.com")

// Webhooks
val hook = client.webhooks.createWebhook(
    url = "https://myapp.com/hook", event = "booking.completed"
)
```

**Run tests:**
```bash
./gradlew test        # 168 tests, all should pass
```

[Full Android documentation →](./android-sdk/README.md)

---

## MCP Server

Connect Claude (or any MCP-compatible AI assistant) directly to the Cleanster Partner API. The MCP server exposes 11 tools covering bookings, properties, cleaners, payouts, and checklists — all controlled by natural language.

### Quick Start

**Install dependencies:**

```bash
cd mcp-server
npm install
cp .env.example .env   # fill in CLEANSTER_API_BASE_URL
```

**Run (HTTP/SSE mode — for remote or shared access):**

```bash
MCP_SERVER_PORT=8000 npm run dev
# Health: http://localhost:8000/health
# SSE:    http://localhost:8000/sse  (Authorization: Bearer <key>)
```

**Claude Desktop (stdio mode — for local direct connection):**

1. Build: `npm run build`
2. Add to `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "cleanster": {
      "command": "node",
      "args": ["/path/to/mcp-server/dist/index.js"],
      "env": {
        "MCP_TRANSPORT": "stdio",
        "CLEANSTER_API_KEY": "your-api-key",
        "CLEANSTER_API_BASE_URL": "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public"
      }
    }
  }
}
```

3. Restart Claude Desktop — you'll see Cleanster tools available.

**Example conversation with Claude:**

> "List the upcoming bookings for property prop_42 this month"

> "Create a booking for property prop_99, house cleaning, July 15th at 10am. Notes: focus on kitchen."

> "Cancel booking bk_001 — guest checked out early."

### Available Tools

| Tool | Type | Description |
|---|---|---|
| `list_bookings` | Read | List bookings (filter by property, status, date) |
| `get_booking` | Read | Get full booking details |
| `list_properties` | Read | List properties by type |
| `get_property` | Read | Get property details + cleaners + iCal |
| `list_cleaners` | Read | Find available cleaners by region/date |
| `get_payout_records` | Read | Get payout records for a date range |
| `create_booking` | Write | Schedule a new cleaning |
| `cancel_booking` | Write | Cancel a booking |
| `reschedule_booking` | Write | Move a booking to a new time |
| `assign_crew` | Write | Assign cleaners to a booking |
| `update_checklist` | Write | Set checklist items for a booking |

[Full MCP server documentation →](./mcp-server/README.md)

---

## Standard Response Format

Every API response — success or failure — uses the same envelope:

```json
{
  "status":  200,
  "message": "OK",
  "data":    { ... }
}
```

| Field | Type | Description |
|---|---|---|
| `status` | integer | HTTP-style status code (200, 400, 401, 403, 404, 500) |
| `message` | string | Human-readable status description |
| `data` | object or array | The response payload; `null` on errors |

In all SDKs, this is surfaced as an `ApiResponse<T>` / `APIResponse[T]` object. Access `.data` for the payload, `.status` for the code, and `.message` for the text.

---

## Error Handling

| HTTP Status | Meaning | Common Causes |
|---|---|---|
| `400 Bad Request` | Invalid input | Missing required fields, wrong data types, business rule violation |
| `401 Unauthorized` | Auth failure | Missing or invalid `access-key` or `token` |
| `403 Forbidden` | Permission denied | Token is valid but the resource doesn't belong to this user |
| `404 Not Found` | Resource missing | Wrong booking ID, property ID, etc. |
| `500 Internal Server Error` | Server error | Unexpected server-side failure |

**Java:**
```java
try {
    ApiResponse<Booking> resp = client.bookings().getBookingDetails(99999);
} catch (ApiException e) {
    System.err.println("Status: " + e.getStatusCode());
    System.err.println("Message: " + e.getMessage());
} catch (AuthException e) {
    System.err.println("Authentication failed: " + e.getMessage());
}
```

**Python:**
```python
from cleanster.exceptions import ApiException, AuthException

try:
    resp = client.bookings.get_booking_details(99999)
except AuthException as e:
    print(f"Auth error: {e}")
except ApiException as e:
    print(f"API error {e.status_code}: {e.message}")
```

**TypeScript:**
```typescript
import { ApiException, AuthException } from 'cleanster';

try {
  const resp = await client.bookings.getBookingDetails(99999);
} catch (err) {
  if (err instanceof AuthException) {
    console.error('Auth failed:', err.message);
  } else if (err instanceof ApiException) {
    console.error(`API error ${err.statusCode}:`, err.message);
  }
}
```

**Go:**
```go
resp, err := client.Bookings.GetBookingDetails(ctx, 99999)
if err != nil {
    var apiErr *cleanster.CleansterError
    if errors.As(err, &apiErr) {
        fmt.Println("Status:", apiErr.StatusCode, "Message:", apiErr.Message)
    }
}
```

**C#:**
```csharp
try {
    var resp = await client.Bookings.GetBookingDetailsAsync(99999);
} catch (ApiException ex) {
    Console.WriteLine($"Error {ex.StatusCode}: {ex.Message}");
} catch (AuthException ex) {
    Console.WriteLine($"Auth failed: {ex.Message}");
}
```

**Swift:**
```swift
do {
    let resp = try await client.bookings.getBookingDetails(99999)
} catch CleansterError.unauthorized(let msg) {
    print("Auth failed: \(msg)")
} catch CleansterError.apiError(let code, let msg) {
    print("API error \(code): \(msg)")
} catch CleansterError.networkError(let msg) {
    print("Network error: \(msg)")
}
```

**Kotlin:**
```kotlin
import com.cleanster.CleansterError

try {
    val resp = client.bookings.getBookingDetails(99999)
} catch (e: CleansterError.Unauthorized) {
    println("Auth failed: ${e.message}")
} catch (e: CleansterError.ApiError) {
    println("API error ${e.statusCode}: ${e.message}")
} catch (e: CleansterError.NetworkError) {
    println("Network error: ${e.message}")
}
```

---

## All 62 Endpoints

---

### Users API

Manage end-user accounts and issue authentication tokens.

---

#### `POST /v1/user/account` — Create User

Create a new user account on behalf of one of your customers.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | string | yes | User's email address |
| `firstName` | string | yes | First name |
| `lastName` | string | yes | Last name |
| `phone` | string | no | Phone number (E.164 format recommended) |

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `id` | integer | Cleanster user ID — save this |
| `email` | string | Email address |
| `firstName` | string | First name |
| `lastName` | string | Last name |
| `phone` | string | Phone number |

**Examples:**

```java
// Java
CreateUserRequest req = new CreateUserRequest();
req.setEmail("alice@example.com");
req.setFirstName("Alice");
req.setLastName("Smith");
req.setPhone("+14155551234");
ApiResponse<User> resp = client.users().createUser(req);
int userId = resp.getData().getId(); // store this
```

```python
# Python
resp = client.users.create_user(
    email="alice@example.com",
    first_name="Alice",
    last_name="Smith",
    phone="+14155551234"
)
user_id = resp.data["id"]
```

```typescript
// TypeScript
const resp = await client.users.createUser({
  email: 'alice@example.com',
  firstName: 'Alice',
  lastName: 'Smith',
  phone: '+14155551234',
});
const userId = resp.data.id;
```

```go
// Go
resp, err := client.Users.CreateUser(ctx, cleanster.CreateUserRequest{
    Email:     "alice@example.com",
    FirstName: "Alice",
    LastName:  "Smith",
    Phone:     "+14155551234",
})
userID := resp.Data.ID
```

```csharp
// C#
var resp = await client.Users.CreateUserAsync(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith",
    phone: "+14155551234"
);
```

```swift
// Swift
let resp = try await client.users.createUser(
    email: "alice@example.com",
    firstName: "Alice",
    lastName: "Smith",
    phone: "+14155551234"
)
let userId = resp.data?.id ?? 0
```

```kotlin
// Kotlin
val resp = client.users.createUser(
    email     = "alice@example.com",
    firstName = "Alice",
    lastName  = "Smith",
    phone     = "+14155551234",
)
val userId = resp.data?.id ?: 0
```

---

#### `GET /v1/user/access-token/{userId}` — Fetch User Token

Retrieve the long-lived JWT for a specific user. Store it securely and pass it as the `token` header on all calls made on behalf of this user.

**Path parameter:** `userId` — integer user ID from Create User.

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `token` | string | Long-lived JWT for this user |

**Examples:**

```java
ApiResponse<User> resp = client.users().fetchAccessToken(12345);
String jwt = resp.getData().getToken();
client.setToken(jwt);
```

```python
resp = client.users.fetch_access_token(12345)
client.token = resp.data["token"]
```

```typescript
const resp = await client.users.fetchAccessToken(12345);
client.setToken(resp.data.token);
```

```go
resp, err := client.Users.FetchAccessToken(ctx, 12345)
client.SetToken(resp.Data["token"].(string))
```

```csharp
var resp = await client.Users.FetchAccessTokenAsync(12345);
client.SetToken(resp.Data.GetProperty("token").GetString()!);
```

```swift
// Swift
let resp = try await client.users.fetchAccessToken(userId: 12345)
client.setToken(resp.data?.token ?? "")
```

```kotlin
// Kotlin
val resp = client.users.fetchAccessToken(12345)
client.setToken(resp.data?.token ?: "")
```

---

#### `POST /v1/user/verify-jwt` — Verify JWT

Check whether a JWT is valid and has not expired.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | string | yes | The JWT to validate |

**Examples:**

```python
resp = client.users.verify_jwt("eyJhbGciOi...")
print(resp.message)  # "OK" if valid
```

```typescript
const resp = await client.users.verifyJwt({ token: 'eyJhbGciOi...' });
```

```swift
// Swift
let resp = try await client.users.verifyJwt(token: "eyJhbGciOi...")
print(resp.message ?? "")  // "OK" if valid
```

```kotlin
// Kotlin
val resp = client.users.verifyJwt("eyJhbGciOi...")
println(resp.message)   // "OK" if valid
```

---

### Properties API

Manage the physical locations where cleaning services are delivered.

---

#### `GET /v1/properties` — List Properties

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `serviceId` | integer | no | Filter by service type. Omit to return all. |

**Response `data`:** Array of property objects.

| Field | Type | Description |
|---|---|---|
| `id` | integer | Property ID |
| `name` | string | Display name |
| `address` | string | Street address |
| `city` | string | City |
| `country` | string | Country |
| `roomCount` | integer | Number of rooms |
| `bathroomCount` | integer | Number of bathrooms |
| `serviceId` | integer | Associated service type |
| `isEnabled` | boolean | Whether the property is active |

**Examples:**

```java
ApiResponse<Object> resp = client.properties().listProperties(null);
// Filter by service:
ApiResponse<Object> filtered = client.properties().listProperties(1);
```

```python
resp = client.properties.list_properties()
resp = client.properties.list_properties(service_id=1)
```

```typescript
const resp = await client.properties.listProperties();
const filtered = await client.properties.listProperties(1);
```

---

#### `POST /v1/properties` — Add Property

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | yes | Display name |
| `address` | string | yes | Street address |
| `city` | string | yes | City |
| `country` | string | yes | Country code or name |
| `roomCount` | integer | yes | Number of rooms |
| `bathroomCount` | integer | yes | Number of bathrooms |
| `serviceId` | integer | yes | Service type ID (from `GET /v1/services`) |
| `state` | string | no | State / province |
| `zip` | string | no | Postal / ZIP code |
| `timezone` | string | no | IANA timezone (e.g. `America/New_York`) |
| `note` | string | no | Internal note visible to assigned cleaners |
| `latitude` | number | no | GPS latitude |
| `longitude` | number | no | GPS longitude |

**Examples:**

```java
CreatePropertyRequest req = new CreatePropertyRequest();
req.setName("Downtown Loft");
req.setAddress("123 Main St");
req.setCity("Atlanta");
req.setCountry("US");
req.setState("GA");
req.setZip("30301");
req.setRoomCount(2);
req.setBathroomCount(1);
req.setServiceId(1);
req.setTimezone("America/New_York");
ApiResponse<Property> resp = client.properties().addProperty(req);
int propId = resp.getData().getId();
```

```python
resp = client.properties.add_property({
    "name": "Downtown Loft",
    "address": "123 Main St",
    "city": "Atlanta",
    "country": "US",
    "state": "GA",
    "zip": "30301",
    "roomCount": 2,
    "bathroomCount": 1,
    "serviceId": 1,
    "timezone": "America/New_York",
})
property_id = resp.data["id"]
```

```typescript
const resp = await client.properties.addProperty({
  name: 'Downtown Loft',
  address: '123 Main St',
  city: 'Atlanta',
  country: 'US',
  roomCount: 2,
  bathroomCount: 1,
  serviceId: 1,
});
```

```go
lat := 33.749
lon := -84.388
resp, err := client.Properties.AddProperty(ctx, cleanster.CreatePropertyRequest{
    Name:          "Downtown Loft",
    Address:       "123 Main St",
    City:          "Atlanta",
    Country:       "US",
    State:         "GA",
    Zip:           "30301",
    RoomCount:     2,
    BathroomCount: 1,
    ServiceID:     1,
    Timezone:      "America/New_York",
    Latitude:      &lat,
    Longitude:     &lon,
})
```

```csharp
var resp = await client.Properties.AddPropertyAsync(
    name: "Downtown Loft",
    address: "123 Main St",
    city: "Atlanta",
    country: "US",
    roomCount: 2,
    bathroomCount: 1,
    serviceId: 1,
    state: "GA",
    zip: "30301",
    timezone: "America/New_York"
);
```

---

#### `GET /v1/properties/{propertyId}` — Get Property
#### `PUT /v1/properties/{propertyId}` — Update Property
#### `DELETE /v1/properties/{propertyId}` — Delete Property

Update and delete use the same fields as Add Property. Delete is permanent.

```python
# Get
resp = client.properties.get_property(1004)

# Update
resp = client.properties.update_property(1004, {
    "name": "Uptown Suite",
    "address": "456 Peach St",
    "city": "Atlanta",
    "country": "US",
    "roomCount": 3,
    "bathroomCount": 2,
    "serviceId": 1,
})

# Delete
resp = client.properties.delete_property(1004)
```

---

#### `PUT /v1/properties/{propertyId}/additional-information` — Update Additional Information

Update freeform supplemental fields on a property.

```typescript
await client.properties.updateAdditionalInformation(1004, {
  parkingInstructions: 'Use the visitor lot on the left.',
  accessCode: '1234#',
});
```

---

#### `POST /v1/properties/{propertyId}/enable-disable` — Toggle Property

Enable or disable a property. Disabled properties cannot receive new bookings.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `enabled` | boolean | yes | `true` to enable, `false` to disable |

```java
client.properties().enableOrDisableProperty(1004, new EnableDisableRequest(false));
```

```python
client.properties.enable_or_disable_property(1004, enabled=False)
```

---

#### `GET /v1/properties/{propertyId}/cleaners` — List Property Cleaners

Returns the pool of cleaners associated with this property.

#### `POST /v1/properties/{propertyId}/cleaners` — Add Cleaner to Property

Add a cleaner to a property's preferred pool.

**Request body:** `{ "cleanerId": 789 }`

#### `DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}` — Remove Cleaner from Property

```python
# List
resp = client.properties.get_property_cleaners(1004)

# Add
resp = client.properties.add_cleaner_to_property(1004, cleaner_id=789)

# Remove
resp = client.properties.remove_cleaner_from_property(1004, cleaner_id=789)
```

---

#### `PUT /v1/properties/{propertyId}/ical` — Set iCal Link

Sync a property's booking calendar with an external iCal feed (e.g. Airbnb, VRBO).

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `icalLink` | string | yes | Full HTTPS URL of the iCal feed |

#### `GET /v1/properties/{propertyId}/ical` — Get iCal Link
#### `DELETE /v1/properties/{propertyId}/ical` — Remove iCal Link

```python
# Set
client.properties.set_ical_link(1004, ical_link="https://www.airbnb.com/calendar/ical/abc123.ics")

# Get
resp = client.properties.get_ical_link(1004)

# Remove
client.properties.delete_ical_link(1004, ical_link="https://www.airbnb.com/...")
```

---

#### `PUT /v1/properties/{propertyId}/checklist/{checklistId}` — Set Default Checklist

Assign a checklist as the default for all future bookings on this property.

**Query parameter:** `updateUpcomingBookings` — boolean. Set to `true` to also update already-scheduled (not yet completed) bookings.

```java
client.properties().setDefaultChecklist(1004, 77, true);
```

```python
client.properties.set_default_checklist(1004, checklist_id=77, update_upcoming_bookings=True)
```

```typescript
await client.properties.setDefaultChecklist(1004, 77, true);
```

---

### Bookings API

Manage the full lifecycle of cleaning appointments.

---

#### `GET /v1/bookings` — List Bookings

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | integer | no | Page number, 1-based. Defaults to 1. |
| `status` | string | no | Filter: `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |

**Response `data`:** Array of booking objects.

| Field | Type | Description |
|---|---|---|
| `id` | integer | Booking ID |
| `status` | string | Current status |
| `date` | string | Service date (`YYYY-MM-DD`) |
| `time` | string | Start time (`HH:mm`) |
| `hours` | number | Duration in hours |
| `cost` | number | Total cost in account currency |
| `propertyId` | integer | Associated property |
| `cleanerId` | integer or null | Assigned cleaner's user ID |
| `planId` | integer | Plan used for this booking |
| `roomCount` | integer | Rooms cleaned |
| `bathroomCount` | integer | Bathrooms cleaned |
| `extraSupplies` | boolean | Whether supplies are included |
| `paymentMethodId` | integer | Payment method charged |

**Examples:**

```java
ApiResponse<Object> all   = client.bookings().getBookings(1, null);
ApiResponse<Object> open  = client.bookings().getBookings(1, "OPEN");
ApiResponse<Object> done  = client.bookings().getBookings(1, "COMPLETED");
```

```python
all_bookings  = client.bookings.get_bookings()
open_bookings = client.bookings.get_bookings(status="OPEN")
page2         = client.bookings.get_bookings(page_no=2, status="COMPLETED")
```

```typescript
const all   = await client.bookings.getBookings();
const open  = await client.bookings.getBookings(1, 'OPEN');
const done  = await client.bookings.getBookings(1, 'COMPLETED');
```

```go
all,  err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{})
open, err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{Status: "OPEN"})
pageNo := 2
p2,   err := client.Bookings.GetBookings(ctx, cleanster.GetBookingsParams{PageNo: &pageNo})
```

```csharp
var all  = await client.Bookings.GetBookingsAsync();
var open = await client.Bookings.GetBookingsAsync(status: "OPEN");
var p2   = await client.Bookings.GetBookingsAsync(pageNo: 2, status: "COMPLETED");
```

```swift
// Swift
let all  = try await client.bookings.getBookings()
let open = try await client.bookings.getBookings(status: "OPEN")
let p2   = try await client.bookings.getBookings(pageNo: 2, status: "COMPLETED")
```

```kotlin
// Kotlin
val all  = client.bookings.getBookings()
val open = client.bookings.getBookings(status = "OPEN")
val p2   = client.bookings.getBookings(pageNo = 2, status = "COMPLETED")
```

---

#### `POST /v1/bookings/create` — Create Booking

Schedule a new cleaning appointment.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | string | yes | Service date in `YYYY-MM-DD` format |
| `time` | string | yes | Start time in `HH:mm` 24-hour format |
| `propertyId` | integer | yes | ID of the property to clean |
| `planId` | integer | yes | Cleaning plan ID (from `GET /v1/plans`) |
| `hours` | number | yes | Duration (from `GET /v1/recommended-hours`) |
| `roomCount` | integer | yes | Number of rooms |
| `bathroomCount` | integer | yes | Number of bathrooms |
| `extraSupplies` | boolean | yes | `true` to add cleaning supplies |
| `paymentMethodId` | integer | yes | Saved payment method ID |
| `couponCode` | string | no | Discount coupon code |
| `extras` | integer[] | no | Array of extra service IDs (from `GET /v1/cleaning-extras`) |

**Examples:**

```java
CreateBookingRequest req = new CreateBookingRequest();
req.setDate("2025-09-15");
req.setTime("09:00");
req.setPropertyId(1004);
req.setPlanId(2);
req.setHours(3.0);
req.setRoomCount(2);
req.setBathroomCount(1);
req.setExtraSupplies(false);
req.setPaymentMethodId(55);
req.setCouponCode("20POFF");
req.setExtras(List.of(3, 7));   // oven cleaning + inside fridge
ApiResponse<Booking> resp = client.bookings().createBooking(req);
```

```python
resp = client.bookings.create_booking({
    "date": "2025-09-15",
    "time": "09:00",
    "propertyId": 1004,
    "planId": 2,
    "hours": 3.0,
    "roomCount": 2,
    "bathroomCount": 1,
    "extraSupplies": False,
    "paymentMethodId": 55,
    "couponCode": "20POFF",
    "extras": [3, 7],
})
booking_id = resp.data["id"]
```

```typescript
const resp = await client.bookings.createBooking({
  date: '2025-09-15',
  time: '09:00',
  propertyId: 1004,
  planId: 2,
  hours: 3.0,
  roomCount: 2,
  bathroomCount: 1,
  extraSupplies: false,
  paymentMethodId: 55,
  couponCode: '20POFF',
  extras: [3, 7],
});
```

```ruby
resp = client.bookings.create_booking(
  date: '2025-09-15',
  time: '09:00',
  property_id: 1004,
  plan_id: 2,
  hours: 3.0,
  room_count: 2,
  bathroom_count: 1,
  extra_supplies: false,
  payment_method_id: 55,
  coupon_code: '20POFF'
)
```

```swift
// Swift
let resp = try await client.bookings.createBooking(
    date:            "2025-09-15",
    time:            "09:00",
    propertyId:      1004,
    planId:          2,
    hours:           3.0,
    roomCount:       2,
    bathroomCount:   1,
    extraSupplies:   false,
    paymentMethodId: 55,
    couponCode:      "20POFF",
    extras:          [3, 7]
)
let bookingId = resp.data?.id ?? 0
```

```kotlin
// Kotlin
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

#### `GET /v1/bookings/{bookingId}` — Get Booking Details

Returns the full booking object. Same fields as listed in [List Bookings](#get-v1bookings--list-bookings).

```python
resp = client.bookings.get_booking_details(16459)
print(resp.data["status"])  # "CLEANER_ASSIGNED"
```

---

#### `POST /v1/bookings/{bookingId}/cancel` — Cancel Booking

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | string | no | Cancellation reason (shown to cleaner) |

```java
CancelBookingRequest req = new CancelBookingRequest();
req.setReason("Customer rescheduled");
client.bookings().cancelBooking(16459, req);
```

```python
client.bookings.cancel_booking(16459, reason="Customer rescheduled")
```

---

#### `POST /v1/bookings/{bookingId}/reschedule` — Reschedule Booking

Move the booking to a new date and time.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | string | yes | New date (`YYYY-MM-DD`) |
| `time` | string | yes | New start time (`HH:mm`) |

```typescript
await client.bookings.rescheduleBooking(16459, {
  date: '2025-09-20',
  time: '14:00',
});
```

```go
_, err = client.Bookings.RescheduleBooking(ctx, 16459, cleanster.RescheduleBookingRequest{
    Date: "2025-09-20",
    Time: "14:00",
})
```

---

#### `POST /v1/bookings/{bookingId}/cleaner` — Assign Cleaner

Manually assign a specific cleaner to a booking. The cleaner must be in the property's cleaner pool.

**Request body:** `{ "cleanerId": 789 }`

#### `DELETE /v1/bookings/{bookingId}/cleaner` — Remove Assigned Cleaner

Unassign the current cleaner; the booking returns to `OPEN` status.

```python
# Assign
client.bookings.assign_cleaner(16459, cleaner_id=789)

# Unassign
client.bookings.remove_assigned_cleaner(16459)
```

---

#### `POST /v1/bookings/{bookingId}/hours` — Adjust Hours

Change the duration of a booking.

**Request body:** `{ "hours": 4.5 }`

```csharp
await client.Bookings.AdjustHoursAsync(16459, hours: 4.5);
```

---

#### `POST /v1/bookings/{bookingId}/expenses` — Pay Expenses

Pay outstanding balance-on-completion charges. Can be called before the booking completes and up to 72 hours after.

**Request body:** `{ "paymentMethodId": 55 }`

```java
PayExpensesRequest req = new PayExpensesRequest();
req.setPaymentMethodId(55);
client.bookings().payExpenses(16459, req);
```

---

#### `GET /v1/bookings/{bookingId}/inspection` — Get Inspection Report
#### `GET /v1/bookings/{bookingId}/inspection/details` — Get Detailed Inspection

Retrieve photos, notes, and scores from the post-booking inspection.

```python
report  = client.bookings.get_booking_inspection(16459)
details = client.bookings.get_booking_inspection_details(16459)
```

---

#### `PUT /v1/bookings/{bookingId}/checklist/{checklistId}` — Assign Checklist to Booking

Override the property's default checklist for this specific booking only.

```typescript
await client.bookings.assignChecklistToBooking(16459, 77);
```

```go
_, err = client.Bookings.AssignChecklistToBooking(ctx, 16459, 77)
```

---

#### `POST /v1/bookings/{bookingId}/feedback` — Submit Feedback

Submit a star rating and optional written comment after a booking completes.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | integer | yes | Star rating, 1–5 |
| `comment` | string | no | Written feedback |

```python
client.bookings.submit_feedback(16459, rating=5, comment="Spotless — great job!")
```

```csharp
await client.Bookings.SubmitFeedbackAsync(16459, rating: 5, comment: "Spotless!");
```

---

#### `POST /v1/bookings/{bookingId}/tip` — Add Tip

Add a gratuity for the cleaner. Must be called within 72 hours of booking completion.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | number | yes | Tip amount in account currency |
| `paymentMethodId` | integer | yes | Payment method to charge |

```typescript
await client.bookings.addTip(16459, {
  amount: 10.00,
  paymentMethodId: 55,
});
```

---

#### `GET /v1/bookings/{bookingId}/chat` — Get Chat Messages

Retrieve the chat thread for a booking. Chat is available within ±24 hours of the booking start time. For bookings in an indefinitely-hanging state, there is no time restriction.

**Response `data`:** Array of message objects.

| Field | Type | Description |
|---|---|---|
| `id` | string | Message ID |
| `message` | string | Message text |
| `sentBy` | string | `PARTNER`, `CLEANER`, or `SYSTEM` |
| `sentAt` | string | ISO 8601 timestamp |
| `isDeleted` | boolean | Whether this message has been deleted |

#### `POST /v1/bookings/{bookingId}/chat` — Send Chat Message

**Request body:** `{ "message": "Your cleaner is on the way!" }`

#### `DELETE /v1/bookings/{bookingId}/chat/{messageId}` — Delete Chat Message

```java
// Get
ApiResponse<Object> chat = client.bookings().getChat(17142);

// Send
SendMessageRequest msg = new SendMessageRequest();
msg.setMessage("Your cleaner is 10 minutes away.");
client.bookings().sendMessage(17142, msg);

// Delete
client.bookings().deleteMessage(17142, "msg-abc-123");
```

```python
# Get
chat = client.bookings.get_chat(17142)

# Send
client.bookings.send_message(17142, message="Your cleaner is 10 minutes away.")

# Delete
client.bookings.delete_message(17142, "msg-abc-123")
```

---

### Checklists API

Create and manage reusable cleaning task lists that can be assigned per-property or per-booking.

---

#### `GET /v1/checklist` — List Checklists

Returns all checklists in the partner account.

#### `GET /v1/checklist/{checklistId}` — Get Checklist

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `id` | integer | Checklist ID |
| `name` | string | Checklist name |
| `items` | array | List of `ChecklistItem` objects |

**`ChecklistItem` fields:**

| Field | Type | Description |
|---|---|---|
| `id` | integer | Item ID |
| `description` | string | Task description shown to cleaner |
| `isCompleted` | boolean | Whether the cleaner marked it done |
| `imageUrl` | string or null | Photo proof URL (if uploaded by cleaner) |

---

#### `POST /v1/checklist` — Create Checklist

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | yes | Checklist name |
| `items` | string[] | yes | Array of task description strings |

**Examples:**

```java
CreateChecklistRequest req = new CreateChecklistRequest();
req.setName("Standard Deep Clean");
req.setItems(List.of(
    "Vacuum all carpets",
    "Mop hard floors",
    "Wipe down all surfaces",
    "Clean oven interior",
    "Scrub bathrooms"
));
ApiResponse<Checklist> resp = client.checklists().createChecklist(req);
int checklistId = resp.getData().getId();
```

```python
resp = client.checklists.create_checklist(
    name="Standard Deep Clean",
    items=[
        "Vacuum all carpets",
        "Mop hard floors",
        "Wipe down all surfaces",
        "Clean oven interior",
        "Scrub bathrooms",
    ]
)
checklist_id = resp.data["id"]
```

```typescript
const resp = await client.checklists.createChecklist({
  name: 'Standard Deep Clean',
  items: [
    'Vacuum all carpets',
    'Mop hard floors',
    'Wipe down all surfaces',
    'Clean oven interior',
    'Scrub bathrooms',
  ],
});
```

```go
resp, err := client.Checklists.CreateChecklist(ctx, cleanster.CreateChecklistRequest{
    Name:  "Standard Deep Clean",
    Items: []string{"Vacuum all carpets", "Mop hard floors"},
})
```

```ruby
resp = client.checklists.create_checklist(
  name: 'Standard Deep Clean',
  items: ['Vacuum all carpets', 'Mop hard floors', 'Scrub bathrooms']
)
```

---

#### `PUT /v1/checklist/{checklistId}` — Update Checklist

Same body as Create Checklist. Replaces the entire checklist.

#### `DELETE /v1/checklist/{checklistId}` — Delete Checklist

```python
# Update
client.checklists.update_checklist(77, name="Deep Clean v2", items=["Vacuum", "Mop", "Polish"])

# Delete
client.checklists.delete_checklist(77)
```

#### `POST /v1/checklist/{checklistId}/upload` — Upload Checklist Image

Upload an image to associate with a checklist. The image is sent as `multipart/form-data` in the `image` field.

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `checklistId` | integer | The checklist ID |

**Request body:** `multipart/form-data` with an `image` field containing the image bytes.

**Examples:**

```python
# Python
with open("photo.jpg", "rb") as f:
    client.checklists.upload_checklist_image(77, f.read(), file_name="photo.jpg")
```

```typescript
// TypeScript
const imageBytes = fs.readFileSync('photo.jpg');
await client.checklists.uploadChecklistImage(77, imageBytes, 'photo.jpg');
```

```go
// Go
imageData, _ := os.ReadFile("photo.jpg")
client.Checklists.UploadChecklistImage(ctx, 77, imageData, "photo.jpg")
```

```kotlin
// Kotlin / Android
val imageBytes = File("photo.jpg").readBytes()
client.checklists.uploadChecklistImage(77, imageBytes, "photo.jpg")
```

---

### Payment Methods API

Manage Stripe card and PayPal payment methods for a user. Payment methods are attached to the user whose `token` is set on the client.

---

#### `GET /v1/payment-methods/setup-intent-details` — Get Stripe Setup Intent

Returns the Stripe client secret needed to tokenize a card in your frontend using Stripe.js.

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `clientSecret` | string | Stripe Setup Intent client secret |

**Flow:**
1. Call this endpoint from your server.
2. Pass the `clientSecret` to your frontend.
3. Use `stripe.confirmCardSetup(clientSecret, ...)` in Stripe.js.
4. On success, Stripe.js returns a `paymentMethod.id` string (e.g. `pm_xxxxx`).
5. Pass that string to `POST /v1/payment-methods`.

```typescript
// Server-side: get the secret
const { data } = await client.paymentMethods.getSetupIntentDetails();
const clientSecret = data.clientSecret;

// After Stripe.js confirms card setup on the frontend:
await client.paymentMethods.addPaymentMethod('pm_xxxxx');
```

```python
resp = client.payment_methods.get_setup_intent_details()
client_secret = resp.data["clientSecret"]

# After Stripe.js returns the payment method ID:
client.payment_methods.add_payment_method({"paymentMethodId": "pm_xxxxx"})
```

---

#### `GET /v1/payment-methods/paypal-client-token` — Get PayPal Client Token

Returns a one-time token for the Braintree/PayPal JS SDK to authorize PayPal Vault.

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `clientToken` | string | PayPal Braintree client token |

---

#### `POST /v1/payment-methods` — Save Payment Method

Save a tokenized payment method to the user's profile.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `paymentMethodId` | string | yes | Stripe `pm_xxx` token or PayPal nonce from client SDK |

```java
AddPaymentMethodRequest req = new AddPaymentMethodRequest();
req.setPaymentMethodId("pm_1OjvDE2eZvKYlo2C3hzIwdpz");
client.paymentMethods().addPaymentMethod(req);
```

```go
resp, err := client.PaymentMethods.AddPaymentMethod(ctx, cleanster.AddPaymentMethodRequest{
    PaymentMethodID: "pm_1OjvDE2eZvKYlo2C3hzIwdpz",
})
```

---

#### `GET /v1/payment-methods` — List Payment Methods

Returns all saved payment methods for the current user.

**Response `data`:** Array of payment method objects.

| Field | Type | Description |
|---|---|---|
| `id` | integer | Internal payment method ID (used for booking and tipping) |
| `type` | string | `card` or `paypal` |
| `lastFour` | string | Last 4 digits (cards only) |
| `brand` | string | `visa`, `mastercard`, `amex`, etc. |
| `isDefault` | boolean | Whether this is the default method |

---

#### `PUT /v1/payment-methods/{paymentMethodId}/default` — Set Default Payment Method

Mark a payment method as the default for this user.

#### `DELETE /v1/payment-methods/{paymentMethodId}` — Delete Payment Method

```python
# List
methods = client.payment_methods.get_payment_methods()

# Set default
client.payment_methods.set_default_payment_method(55)

# Delete
client.payment_methods.delete_payment_method(55)
```

```csharp
// List
var methods = await client.PaymentMethods.GetPaymentMethodsAsync();

// Set default
await client.PaymentMethods.SetDefaultPaymentMethodAsync(55);

// Delete
await client.PaymentMethods.DeletePaymentMethodAsync(55);
```

---

### Webhooks API

Subscribe to real-time events for booking lifecycle changes. When a subscribed event fires, Cleanster sends an HTTP POST to your endpoint with a JSON payload.

---

#### `GET /v1/webhooks` — List Webhooks

Returns all registered webhook endpoints.

#### `POST /v1/webhooks` — Create Webhook

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | string | yes | HTTPS URL to receive the POST |
| `event` | string | yes | Event name to subscribe to (see [Webhook Events](#webhook-events)) |

#### `PUT /v1/webhooks/{webhookId}` — Update Webhook

Same body as Create Webhook.

#### `DELETE /v1/webhooks/{webhookId}` — Delete Webhook

**Examples:**

```java
CreateWebhookRequest req = new CreateWebhookRequest();
req.setUrl("https://api.yourplatform.com/hooks/cleanster");
req.setEvent("booking.completed");
client.webhooks().createWebhook(req);
```

```python
client.webhooks.create_webhook({
    "url": "https://api.yourplatform.com/hooks/cleanster",
    "event": "booking.completed"
})
```

```typescript
await client.webhooks.createWebhook({
  url: 'https://api.yourplatform.com/hooks/cleanster',
  event: 'booking.completed',
});
```

```go
resp, err := client.Webhooks.CreateWebhook(ctx, cleanster.CreateWebhookRequest{
    URL:   "https://api.yourplatform.com/hooks/cleanster",
    Event: "booking.completed",
})
```

```csharp
await client.Webhooks.CreateWebhookAsync(
    url:       "https://api.yourplatform.com/hooks/cleanster",
    eventType: "booking.completed"
);
```

---

### Blacklist API

Prevent specific cleaners from being assigned to any of your bookings. Blacklisted cleaners are skipped during both manual and automatic assignment.

---

#### `GET /v1/blacklist/cleaner` — List Blacklisted Cleaners
#### `POST /v1/blacklist/cleaner` — Add to Blacklist

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `cleanerId` | integer | yes | The cleaner's user ID |
| `reason` | string | no | Internal reason for blacklisting |

#### `DELETE /v1/blacklist/cleaner` — Remove from Blacklist

**Request body:** `{ "cleanerId": 789 }`

**Examples:**

```java
// Add
BlacklistRequest req = new BlacklistRequest();
req.setCleanerId(789);
req.setReason("Repeated late arrivals");
client.blacklist().addToBlacklist(req);

// Remove
BlacklistRequest removeReq = new BlacklistRequest();
removeReq.setCleanerId(789);
client.blacklist().removeFromBlacklist(removeReq);
```

```python
# Add
client.blacklist.add_to_blacklist(cleaner_id=789, reason="Repeated late arrivals")

# List
resp = client.blacklist.list_blacklisted_cleaners()

# Remove
client.blacklist.remove_from_blacklist(cleaner_id=789)
```

```typescript
await client.blacklist.addToBlacklist({ cleanerId: 789, reason: 'Repeated late arrivals' });
await client.blacklist.removeFromBlacklist({ cleanerId: 789 });
```

---

### Reference Data API

Fetch the lookup data required to build booking flows — service types, plans, pricing estimates, cleaning extras, and available cleaners.

---

#### `GET /v1/services` — Get Service Types

Returns all cleaning service types available on your partner account (e.g., Residential, Airbnb, Office).

```python
services = client.other.get_services()
# Each item: { "id": 1, "name": "Residential" }
```

---

#### `GET /v1/plans` — Get Plans

Returns available booking plans (e.g., Regular, Deep Clean, Move-In/Out) for a given property.

**Query parameter:** `propertyId` — integer, required.

```typescript
const plans = await client.other.getPlans(1004);
// Each plan: { "id": 2, "name": "Regular Clean", "pricePerHour": 35 }
```

---

#### `GET /v1/recommended-hours` — Get Recommended Hours

Get the system-recommended number of cleaning hours based on property size. Use this value as the `hours` field when creating a booking.

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `propertyId` | integer | yes | Property ID |
| `roomCount` | integer | yes | Number of rooms |
| `bathroomCount` | integer | yes | Number of bathrooms |

```java
ApiResponse<Object> resp = client.other().getRecommendedHours(1004, 1, 2);
// resp.getData() -> { "hours": 3.0 }
```

```python
resp = client.other.get_recommended_hours(
    property_id=1004, room_count=2, bathroom_count=1
)
recommended_hours = resp.data["hours"]
```

```go
resp, err := client.Other.GetRecommendedHours(ctx, 1004, 1, 2)
```

---

#### `POST /v1/cost-estimate` — Get Cost Estimate

Calculate the total price for a potential booking before creating it.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | integer | yes | Property ID |
| `planId` | integer | yes | Plan ID |
| `hours` | number | yes | Duration in hours |
| `couponCode` | string | no | Discount coupon to apply |
| `extras` | integer[] | no | Extra service IDs |

**Response `data`:**

| Field | Type | Description |
|---|---|---|
| `total` | number | Total price in account currency |
| `discount` | number | Discount applied (0 if no coupon) |
| `subtotal` | number | Price before discount |

```python
resp = client.other.get_cost_estimate({
    "propertyId": 1004,
    "planId": 2,
    "hours": 3.0,
    "couponCode": "20POFF",
    "extras": [3],
})
print(f"Total: ${resp.data['total']}")
```

```typescript
const estimate = await client.other.getCostEstimate({
  propertyId: 1004,
  planId: 2,
  hours: 3.0,
  couponCode: '20POFF',
});
console.log('Total:', estimate.data.total);
```

---

#### `GET /v1/cleaning-extras/{serviceId}` — Get Cleaning Extras

Returns available add-on services for a given service type (e.g., inside oven, inside fridge, laundry).

```java
ApiResponse<Object> extras = client.other().getCleaningExtras(1);
// Each extra: { "id": 3, "name": "Oven Cleaning", "price": 25 }
```

---

#### `POST /v1/available-cleaners` — Get Available Cleaners

Find cleaners available for a specific property, date, and time slot. Use the results to offer manual cleaner selection to your users.

**Request body:**

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | integer | yes | Property ID |
| `date` | string | yes | Desired date (`YYYY-MM-DD`) |
| `time` | string | yes | Desired start time (`HH:mm`) |

```python
resp = client.other.get_available_cleaners({
    "propertyId": 1004,
    "date": "2025-09-15",
    "time": "09:00",
})
for cleaner in resp.data:
    print(cleaner["id"], cleaner["firstName"], cleaner["lastName"])
```

```typescript
const resp = await client.other.getAvailableCleaners({
  propertyId: 1004,
  date: '2025-09-15',
  time: '09:00',
});
```

---

#### `GET /v1/coupons` — Get Coupon Codes

Returns all valid coupon codes available for use at booking creation.

```python
resp = client.other.get_coupons()
for coupon in resp.data:
    print(coupon["code"], coupon["discount"])
```

---

## Booking Lifecycle

```
createBooking()         →  OPEN
                              │
               assignCleaner()  (or auto-dispatched)
                              │
                              ▼
                    CLEANER_ASSIGNED
                              │
                   Cleaner starts the job
                              │
                              ▼
                         IN_PROGRESS
                              │
                Cleaner marks complete
                    ┌─────────┴──────────┐
                    ▼                    ▼
               COMPLETED            CANCELLED
                    │
     ┌──────────────┼──────────────┐
     ▼              ▼              ▼
addTip()     payExpenses()   submitFeedback()
```

**Booking status values:**

| Status | Description |
|---|---|
| `OPEN` | Booking created, no cleaner yet |
| `CLEANER_ASSIGNED` | A cleaner has been confirmed |
| `IN_PROGRESS` | Cleaner has checked in; job underway |
| `COMPLETED` | Job finished successfully |
| `CANCELLED` | Booking was cancelled |
| `REMOVED` | Booking removed from active view |

---

## Chat Window Rules

The chat thread for a booking is only accessible during a time window around the booking:

| Rule | Detail |
|---|---|
| Chat opens | 24 hours **before** the booking start time |
| Chat closes | 24 hours **after** the booking start time |
| Outside window | API returns `400 Bad Request` |

```python
# Python — only call inside the window
chat = client.bookings.get_chat(booking_id)
client.bookings.send_message(booking_id, message="Please bring extra supplies.")
```

```swift
// Swift
let messages = try await client.bookings.getChat(bookingId: 16926)
try await client.bookings.sendMessage(bookingId: 16926, message: "On my way!")
```

```kotlin
// Kotlin
val messages = client.bookings.getChat(16926)
client.bookings.sendMessage(16926, message = "On my way!")
```

**Chat message fields:**

| Field | Type | Description |
|---|---|---|
| `messageId` | string | Unique message identifier |
| `senderId` | string | Sender reference (e.g. `C6`, `P3`) |
| `content` | string | Text content |
| `timestamp` | string | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `messageType` | string | `text` or `media` |
| `attachments` | array | Media files (images, video, audio) |
| `attachments[].type` | string | `image`, `video`, or `sound` |
| `attachments[].url` | string | Direct media URL |
| `attachments[].thumbUrl` | string | Thumbnail URL (nullable) |
| `isRead` | boolean | Whether the message has been read |
| `senderType` | string | `client`, `cleaner`, `support`, or `bot` |

---

## Integration Workflow

Here is the recommended end-to-end flow for a new user booking their first cleaning:

```
1. [Your backend] POST /v1/user/account          → create Cleanster user → save userId
2. [Your backend] GET  /v1/user/access-token/{userId}  → save JWT token
3. [Your backend] GET  /v1/services               → show service type picker
4. [Your backend] POST /v1/properties             → create property for user
5. [Your backend] GET  /v1/payment-methods/setup-intent-details → get Stripe client secret
6. [Your frontend] Stripe.js confirmCardSetup()   → tokenize card → get pm_xxx
7. [Your backend] POST /v1/payment-methods        → save tokenized card
8. [Your backend] GET  /v1/plans?propertyId=X     → show plan picker
9. [Your backend] GET  /v1/recommended-hours      → pre-fill hours field
10.[Your backend] POST /v1/cost-estimate          → show price preview
11.[Your backend] POST /v1/bookings/create        → create booking → save bookingId
12.[Webhook]      booking.cleaner_assigned        → notify user cleaner is confirmed
13.[Webhook]      booking.completed               → trigger tip / feedback prompts
```

---

## Webhook Events

Register webhooks via `POST /v1/webhooks` to receive real-time notifications.

| Event | Fires When |
|---|---|
| `booking.created` | A new booking is successfully scheduled |
| `booking.cleaner_assigned` | A cleaner is assigned to a booking |
| `booking.cleaner_removed` | The assigned cleaner is removed |
| `booking.rescheduled` | A booking is moved to a new date/time |
| `booking.started` | The cleaner checks in and the service begins |
| `booking.completed` | The service is marked as completed |
| `booking.cancelled` | A booking is cancelled |
| `booking.feedback_submitted` | A rating is submitted for a completed booking |

**Webhook payload structure:**

```json
{
  "event": "booking.completed",
  "bookingId": 16459,
  "propertyId": 1004,
  "timestamp": "2025-09-15T13:45:00Z",
  "data": { }
}
```

Your endpoint should return HTTP `200` within 5 seconds to acknowledge receipt. Failed deliveries are retried with exponential backoff.

---

## Test Coupon Codes

Use these in the **sandbox** environment to test discount logic. These codes are not valid in production.

| Code | Discount Type | Value |
|---|---|---|
| `100POFF` | Percentage | 100% off — makes booking free |
| `50POFF` | Percentage | 50% off |
| `20POFF` | Percentage | 20% off |
| `200OFF` | Fixed amount | $200 off |
| `100OFF` | Fixed amount | $100 off |

> ⚠️ `75POFF` is **expired** and will return an error if used.

**Testing a coupon via cost estimate:**
```python
resp = client.other.get_cost_estimate({
    "propertyId": 1004,
    "planId": 2,
    "hours": 3.0,
    "couponCode": "50POFF",
})
print(resp.data["discount"])   # 50% of subtotal
print(resp.data["total"])      # subtotal minus discount
```

---

## Repository Structure

```
Cleanster-partner-api-sdk/
│
├── swift-sdk/
│   ├── Package.swift
│   ├── Sources/Cleanster/
│   │   ├── Api/          BookingsApi.swift, PropertiesApi.swift, UsersApi.swift, ...
│   │   ├── Models/       Models.swift, Requests.swift, ApiResponse.swift
│   │   ├── Helpers/      AnyCodable.swift
│   │   ├── CleansterClient.swift
│   │   ├── CleansterError.swift
│   │   └── NetworkSession.swift
│   ├── Tests/CleansterTests/   170 unit tests
│   └── README.md               Full Swift SDK documentation
│
├── java-sdk/
│   ├── src/main/java/com/cleanster/sdk/
│   │   ├── api/          BookingApi, PropertyApi, UserApi, ChecklistApi, ...
│   │   ├── client/       HttpClient, CleansterClient
│   │   └── model/        Booking, Property, User, Checklist, ...
│   ├── src/test/java/    78 unit tests
│   ├── pom.xml
│   └── README.md         Full Java SDK documentation
│
├── python-sdk/
│   ├── cleanster/
│   │   ├── api/          bookings.py, properties.py, users.py, ...
│   │   ├── models/       booking.py, property.py, response.py, ...
│   │   └── client.py
│   ├── tests/            103 unit tests
│   ├── pyproject.toml
│   └── README.md         Full Python SDK documentation
│
├── typescript-sdk/
│   ├── src/
│   │   ├── api/          bookings.ts, properties.ts, users.ts, ...
│   │   ├── models/       booking.ts, property.ts, response.ts, ...
│   │   └── client.ts
│   ├── tests/            89 unit tests
│   ├── package.json
│   └── README.md         Full TypeScript SDK documentation
│
├── ruby-sdk/
│   ├── lib/cleanster/
│   │   ├── api/          bookings_api.rb, properties_api.rb, ...
│   │   └── models/       booking.rb, property.rb, ...
│   ├── spec/             123 unit tests
│   ├── cleanster.gemspec
│   └── README.md         Full Ruby SDK documentation
│
├── go-sdk/
│   ├── bookings.go       Bookings service
│   ├── properties.go     Properties service
│   ├── models.go         All request/response types
│   ├── client.go         CleansterClient
│   ├── cleanster_test.go 96 unit tests
│   ├── go.mod
│   └── README.md         Full Go SDK documentation
│
├── php-sdk/
│   ├── src/
│   │   ├── Api/          BookingsApi.php, PropertiesApi.php, ...
│   │   └── Models/       Booking.php, Property.php, ...
│   ├── tests/            110 unit tests
│   ├── composer.json
│   └── README.md         Full PHP SDK documentation
│
├── csharp-sdk/
│   ├── src/Cleanster/
│   │   ├── Api/          BookingsApi.cs, PropertiesApi.cs, ...
│   │   └── Models/       Booking.cs, Property.cs, ...
│   ├── tests/            111 unit tests
│   ├── Cleanster.sln
│   └── README.md         Full C# SDK documentation
│
├── kotlin-sdk/
│   ├── src/main/kotlin/com/cleanster/
│   │   ├── api/          BookingsApi.kt, PropertiesApi.kt, UsersApi.kt, ...
│   │   ├── model/        Models.kt, Requests.kt, ApiResponse.kt
│   │   ├── CleansterClient.kt
│   │   ├── CleansterError.kt
│   │   └── HttpEngine.kt
│   ├── src/test/kotlin/  170 unit tests
│   ├── build.gradle.kts
│   └── README.md         Full Kotlin SDK documentation
│
├── xml-sdk/                  Java 17 + JAXB 4.0 + OkHttp + Gson
│   ├── src/main/java/com/cleanster/xml/
│   │   ├── api/          BookingsXmlApi.java, PropertiesXmlApi.java, UsersXmlApi.java, ...
│   │   ├── model/        Booking.java, Property.java, User.java, ...  (all JAXB-annotated)
│   │   └── client/       CleansterXmlClient.java, XmlConverter.java, XmlHttpClient.java
│   ├── src/test/java/    164 unit tests (JUnit 5 + MockWebServer)
│   ├── pom.xml
│   └── README.md         Full XML SDK documentation
│
├── soap-sdk/                 Java 11+ SOAP 1.1 (document/literal) bridge over REST
│   ├── wsdl/
│   │   ├── cleanster.wsdl    Full WSDL — 62 operations across 9 services
│   │   └── cleanster-types.xsd  XML Schema for all types
│   ├── examples/             Ready-to-use SOAP envelope XML files
│   ├── src/main/java/com/cleanster/soap/
│   │   ├── CleansterSOAPClient.java  Main facade (62 operations)
│   │   ├── SOAPTransport.java        HTTP/REST bridge
│   │   ├── BookingService.java       17 booking operations
│   │   ├── PropertyService.java      14 property operations
│   │   ├── CleanerService.java       2 cleaner operations
│   │   ├── ChecklistService.java     6 checklist operations
│   │   ├── OtherService.java         7 utility operations
│   │   ├── UserService.java          3 user operations
│   │   ├── BlacklistService.java     3 blacklist operations
│   │   ├── PaymentMethodService.java 6 payment method operations
│   │   ├── WebhookService.java       4 webhook operations
│   │   └── model/            Booking, Property, User, Webhook, PaymentMethod, ...
│   ├── src/test/java/        118 unit tests (JUnit 5 + Mockito)
│   ├── pom.xml
│   └── README.md             Full SOAP SDK documentation
│
├── android-sdk/              Android (Retrofit 2 + OkHttp + Gson + Coroutines)
│   ├── src/main/kotlin/com/cleanster/android/
│   │   ├── CleansterClient.kt      Main client entry point
│   │   ├── CleansterConfig.kt      Configuration (apiKey, baseUrl, timeout)
│   │   ├── CleansterApi.kt         Retrofit interface (all 62 endpoints)
│   │   ├── api/                    BookingsApi, PropertiesApi, UsersApi, ...
│   │   └── model/                  Data classes for all request/response types
│   ├── src/test/kotlin/com/cleanster/android/
│   │   ├── BookingsTest.kt         47 booking tests (MockWebServer)
│   │   ├── PropertiesTest.kt       Property API tests
│   │   ├── UsersTest.kt            User API tests
│   │   ├── ChecklistsTest.kt       Checklist API tests
│   │   ├── OtherTest.kt            Reference data & cost estimate tests
│   │   ├── BlacklistTest.kt        Blacklist API tests
│   │   ├── PaymentMethodsTest.kt   Payment method tests
│   │   └── WebhooksTest.kt         Webhook tests
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── README.md                   Full Android SDK documentation
│
├── mcp-server/               MCP server — TypeScript/Node.js 20+, two transports
│   ├── src/
│   │   ├── api/
│   │   │   ├── cleanster.ts  Cleanster REST API client (axios)
│   │   │   └── endpoints.ts  Endpoint constants
│   │   ├── auth/
│   │   │   ├── token.ts      Bearer token validation + OAuth seam (TODO)
│   │   │   └── middleware.ts Express auth middleware
│   │   ├── tools/            11 tool files (one per MCP tool)
│   │   │   ├── list_bookings.ts, get_booking.ts, create_booking.ts, ...
│   │   │   └── update_checklist.ts
│   │   ├── server.ts         McpServer factory + tool registration loop
│   │   └── index.ts          Entry point — stdio or HTTP/SSE transport
│   ├── tests/                67 unit tests (Vitest, mocked API)
│   ├── .env.example
│   ├── package.json
│   ├── tsconfig.json
│   └── README.md             Full MCP server documentation
│
└── README.md             This file
```

Each SDK is self-contained with its own dependency management, build configuration, test suite, and comprehensive README documenting every endpoint.

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

MIT License — see [LICENSE](LICENSE) for details.
