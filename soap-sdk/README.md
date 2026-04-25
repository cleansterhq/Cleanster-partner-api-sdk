# Cleanster SOAP SDK

A Java SOAP SDK for the Cleanster Partner API. Provides a WSDL-defined, document/literal SOAP interface over all Cleanster Partner API endpoints — bookings, properties, cleaners, checklists, users, blacklist, payment methods, webhooks, and more.

---

## Overview

| Item | Detail |
|---|---|
| Language | Java 11+ |
| Build | Maven |
| SOAP Style | Document / Literal (SOAP 1.1) |
| Auth | Bearer Token (API Key) |
| Tests | 118 (JUnit 5 + Mockito) |
| Total Operations | 62 |
| WSDL | `wsdl/cleanster.wsdl` |
| XSD Schema | `wsdl/cleanster-types.xsd` |

---

## SOAP Operations

### Bookings (17 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `GetBooking` | GET | `/v1/bookings/{id}` | Retrieve a booking by ID |
| `ListBookings` | GET | `/v1/bookings` | List bookings with optional filters |
| `CreateBooking` | POST | `/v1/bookings/create` | Create a new cleaning booking |
| `CancelBooking` | POST | `/v1/bookings/{id}/cancel` | Cancel a booking |
| `RescheduleBooking` | POST | `/v1/bookings/{id}/reschedule` | Reschedule to a new date/time |
| `AssignCleaner` | POST | `/v1/bookings/{id}/cleaner` | Assign a cleaner to a booking |
| `RemoveAssignedCleaner` | DELETE | `/v1/bookings/{id}/cleaner` | Remove the assigned cleaner |
| `AdjustHours` | POST | `/v1/bookings/{id}/hours` | Adjust booking duration |
| `PayExpenses` | POST | `/v1/bookings/{id}/expenses` | Pay booking expenses |
| `GetBookingInspection` | GET | `/v1/bookings/{id}/inspection` | Get inspection report |
| `GetBookingInspectionDetails` | GET | `/v1/bookings/{id}/inspection/details` | Get detailed inspection |
| `AssignChecklistToBooking` | PUT | `/v1/bookings/{id}/checklist/{checklistId}` | Assign a checklist to a booking |
| `SubmitFeedback` | POST | `/v1/bookings/{id}/feedback` | Submit a rating and comment |
| `AddTip` | POST | `/v1/bookings/{id}/tip` | Add a tip to a booking |
| `GetChat` | GET | `/v1/bookings/{id}/chat` | Get chat messages for a booking |
| `SendMessage` | POST | `/v1/bookings/{id}/chat` | Send a chat message |
| `DeleteMessage` | DELETE | `/v1/bookings/{id}/chat/{messageId}` | Delete a chat message |

### Properties (14 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `GetProperty` | GET | `/v1/properties/{id}` | Retrieve a property by ID |
| `ListProperties` | GET | `/v1/properties` | List all properties |
| `CreateProperty` | POST | `/v1/properties` | Add a new property |
| `UpdateProperty` | PUT | `/v1/properties/{id}` | Update a property |
| `UpdateAdditionalInformation` | PUT | `/v1/properties/{id}/additional-information` | Update property notes/extras |
| `EnableOrDisableProperty` | POST | `/v1/properties/{id}/enable-disable` | Enable or disable a property |
| `DeleteProperty` | DELETE | `/v1/properties/{id}` | Delete a property |
| `GetPropertyCleaners` | GET | `/v1/properties/{id}/cleaners` | List cleaners for a property |
| `AssignCleanerToProperty` | POST | `/v1/properties/{id}/cleaners` | Assign a cleaner to a property |
| `UnassignCleanerFromProperty` | DELETE | `/v1/properties/{id}/cleaners/{cleanerId}` | Remove a cleaner from a property |
| `AddICalLink` | POST | `/v1/properties/{id}/ical` | Add an iCal link |
| `GetICalLink` | GET | `/v1/properties/{id}/ical` | Get existing iCal link |
| `RemoveICalLink` | DELETE | `/v1/properties/{id}/ical` | Remove iCal link |
| `SetDefaultChecklist` | PUT | `/v1/properties/{id}/checklist` | Set the default checklist for a property |

### Cleaners (2 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `ListCleaners` | GET | `/v1/cleaners` | List cleaners (with optional status filter) |
| `GetCleaner` | GET | `/v1/cleaners/{id}` | Retrieve a cleaner by ID |

### Checklists (6 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `ListChecklists` | GET | `/v1/checklists` | List all checklists |
| `GetChecklist` | GET | `/v1/checklists/{id}` | Retrieve a checklist by ID |
| `CreateChecklist` | POST | `/v1/checklists` | Create a new checklist |
| `UpdateChecklist` | PUT | `/v1/checklists/{id}` | Update a checklist |
| `DeleteChecklist` | DELETE | `/v1/checklists/{id}` | Delete a checklist |
| `UploadChecklistImage` | POST | `/v1/checklist/upload-image` | Upload an image to a checklist item |

### Other / Utilities (7 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `GetServices` | GET | `/v1/other/services` | List available service types |
| `GetPlans` | GET | `/v1/other/plans` | Get available cleaning plans |
| `GetRecommendedHours` | GET | `/v1/other/recommended-hours` | Get recommended cleaning hours |
| `GetCostEstimate` | POST | `/v1/other/calculate-cost` | Calculate cost estimate |
| `GetCleaningExtras` | GET | `/v1/other/cleaning-extras` | List available cleaning extras |
| `GetAvailableCleaners` | POST | `/v1/other/available-cleaners` | Find cleaners available at a given time |
| `GetCoupons` | GET | `/v1/other/coupons` | List available coupons |

### Users (3 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `CreateUser` | POST | `/v1/user/account` | Create a new partner user |
| `FetchAccessToken` | GET | `/v1/user/access-token/{id}` | Get a user's access token |
| `VerifyJwt` | POST | `/v1/user/verify-jwt` | Verify a JWT token |

### Blacklist (3 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `ListBlacklist` | GET | `/v1/blacklist` | List all blacklisted cleaners |
| `AddToBlacklist` | POST | `/v1/blacklist` | Add a cleaner to the blacklist |
| `RemoveFromBlacklist` | DELETE | `/v1/blacklist/{cleanerId}` | Remove a cleaner from the blacklist |

### Payment Methods (6 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `GetSetupIntentDetails` | GET | `/v1/payment-methods/setup-intent` | Get Stripe setup intent |
| `GetPaypalClientToken` | GET | `/v1/payment-methods/paypal-token` | Get PayPal client token |
| `AddPaymentMethod` | POST | `/v1/payment-methods` | Add a payment method |
| `GetPaymentMethods` | GET | `/v1/payment-methods` | List all payment methods |
| `DeletePaymentMethod` | DELETE | `/v1/payment-methods/{id}` | Delete a payment method |
| `SetDefaultPaymentMethod` | PUT | `/v1/payment-methods/{id}/default` | Set the default payment method |

### Webhooks (4 operations)
| Operation | HTTP Method | Path | Description |
|---|---|---|---|
| `ListWebhooks` | GET | `/v1/webhooks` | List all registered webhooks |
| `CreateWebhook` | POST | `/v1/webhooks` | Register a new webhook |
| `UpdateWebhook` | PUT | `/v1/webhooks/{id}` | Update a webhook URL/event |
| `DeleteWebhook` | DELETE | `/v1/webhooks/{id}` | Delete a webhook |

All SOAPAction URIs use the base prefix `https://api.cleanster.com/soap/`.

---

## Installation

### Maven

```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-soap-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Build from source

```bash
cd soap-sdk
mvn clean install
```

---

## Quick Start

```java
import com.cleanster.soap.CleansterSOAPClient;
import com.cleanster.soap.model.*;

// 1. Create the client
CleansterSOAPClient client = new CleansterSOAPClient("your-api-key");

// ── Bookings ────────────────────────────────────────────────────────────────

// Get a booking
Booking booking = client.getBooking(16459);
System.out.println(booking.getStatus());   // "scheduled"

// List bookings
ListBookingsRequest req = new ListBookingsRequest()
    .setStatus("scheduled")
    .setPage(1)
    .setPerPage(20);
List<Booking> bookings = client.listBookings(req);

// Create a booking
CreateBookingRequest create = new CreateBookingRequest()
    .setPropertyId(42L)
    .setScheduledAt("2025-07-15T09:00:00Z")
    .setDurationHours(3.0)
    .setServiceType("standard")
    .setNotes("Please focus on the kitchen.");
Booking newBooking = client.createBooking(create);

// Cancel a booking
ApiResponse cancelled = client.cancelBooking(16459, "Customer rescheduled");

// Reschedule a booking
RescheduleBookingRequest reschedule = new RescheduleBookingRequest()
    .setBookingId(16459L)
    .setScheduledAt("2025-08-01T10:00:00Z");
Booking rescheduled = client.rescheduleBooking(reschedule);

// Assign / remove cleaner
Booking assigned = client.assignCleaner(16459, 789);
ApiResponse removed = client.removeAssignedCleaner(16459);

// Adjust hours, pay expenses, feedback, tip
client.adjustHours(16459, 0.5);
client.payExpenses(16459, 10L);
client.submitFeedback(16459, 5, "Excellent work!");
client.addTip(16459, 20.0, 10L);

// Inspection
JsonNode inspection = client.getBookingInspection(16459);

// Chat
List<ChatMessage> chat = client.getChat(16459);
ChatMessage msg = client.sendMessage(16459, "Please use eco-friendly products.");
client.deleteMessage(16459, msg.getId());

// ── Properties ──────────────────────────────────────────────────────────────

CreatePropertyRequest prop = new CreatePropertyRequest()
    .setAddress("456 Oak Ave").setCity("Savannah").setState("GA").setZip("31401")
    .setName("Riverside Cottage").setBedrooms(3)
    .setAccessInstructions("Key in lockbox. Code: 5523.");
Property property = client.createProperty(prop);

client.addICalLink(property.getId(), "https://airbnb.com/calendar.ics");
client.assignCleanerToProperty(property.getId(), 789L);
client.setDefaultChecklist(property.getId(), 105L, true);
client.deleteProperty(property.getId());

// ── Checklists ──────────────────────────────────────────────────────────────

Checklist checklist = client.createChecklist("Deep Clean", List.of("Oven", "Refrigerator", "Bathrooms"));
client.updateChecklist(checklist.getId(), "Standard Clean", null);

// Upload image
byte[] imageBytes = Files.readAllBytes(Paths.get("bathroom-guide.jpg"));
client.uploadChecklistImage(imageBytes, "bathroom-guide.jpg");

// ── Users ────────────────────────────────────────────────────────────────────

CreateUserRequest user = new CreateUserRequest()
    .setName("Alice").setEmail("alice@example.com").setPassword("secret");
User created = client.createUser(user);
User withToken = client.fetchAccessToken(created.getId());
client.verifyJwt(withToken.getAccessToken());

// ── Blacklist ────────────────────────────────────────────────────────────────

client.addToBlacklist(789L, "Repeated no-shows");
List<BlacklistEntry> blocked = client.listBlacklist();
client.removeFromBlacklist(789L);

// ── Payment Methods ──────────────────────────────────────────────────────────

JsonNode intent = client.getSetupIntentDetails();
PaymentMethod pm = client.addPaymentMethod("pm_test_123");
List<PaymentMethod> methods = client.getPaymentMethods();
client.setDefaultPaymentMethod(pm.getId());
client.deletePaymentMethod(pm.getId());

// ── Webhooks ─────────────────────────────────────────────────────────────────

Webhook webhook = client.createWebhook("https://myapp.com/hook", "booking.completed");
List<Webhook> hooks = client.listWebhooks();
client.updateWebhook(webhook.getId(), "https://myapp.com/hook-v2", "booking.cancelled");
client.deleteWebhook(webhook.getId());
```

---

## SOAP Envelope Examples

The `examples/` directory contains ready-to-use SOAP request envelopes:

| File | Operation |
|---|---|
| `GetBooking.xml` | Retrieve a booking |
| `CreateBooking.xml` | Create a new booking |
| `CancelBooking.xml` | Cancel a booking |
| `CreateProperty.xml` | Add a property |
| `SendMessage.xml` | Send a chat message |

### Sending with cURL

```bash
curl -X POST https://api.cleanster.com/soap \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: https://api.cleanster.com/soap/GetBooking" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d @examples/GetBooking.xml
```

---

## Project Structure

```
soap-sdk/
├── pom.xml
├── wsdl/
│   ├── cleanster.wsdl              ← Full WSDL (SOAP 1.1, document/literal)
│   └── cleanster-types.xsd         ← XML Schema for all request/response types
├── examples/
│   ├── GetBooking.xml
│   ├── CreateBooking.xml
│   ├── CancelBooking.xml
│   ├── CreateProperty.xml
│   └── SendMessage.xml
└── src/
    ├── main/java/com/cleanster/soap/
    │   ├── CleansterSOAPClient.java     ← Main entry point (62 operations)
    │   ├── SOAPTransport.java           ← HTTP transport (REST bridge)
    │   ├── SOAPClientException.java     ← Runtime exception
    │   ├── BookingService.java          ← 17 booking operations
    │   ├── PropertyService.java         ← 14 property operations
    │   ├── CleanerService.java          ← 2 cleaner operations
    │   ├── ChecklistService.java        ← 6 checklist operations
    │   ├── OtherService.java            ← 7 utility operations
    │   ├── UserService.java             ← 3 user operations
    │   ├── BlacklistService.java        ← 3 blacklist operations
    │   ├── PaymentMethodService.java    ← 6 payment method operations
    │   ├── WebhookService.java          ← 4 webhook operations
    │   └── model/
    │       ├── Booking.java
    │       ├── Property.java
    │       ├── Cleaner.java
    │       ├── Checklist.java
    │       ├── ServiceType.java
    │       ├── ChatMessage.java
    │       ├── ApiResponse.java
    │       ├── User.java
    │       ├── BlacklistEntry.java
    │       ├── PaymentMethod.java
    │       ├── Webhook.java
    │       ├── CreateBookingRequest.java
    │       ├── ListBookingsRequest.java
    │       ├── RescheduleBookingRequest.java
    │       ├── CreatePropertyRequest.java
    │       └── CreateUserRequest.java
    └── test/java/com/cleanster/soap/
        ├── CleansterSOAPClientTest.java    ← 43 tests (core operations)
        ├── ServiceExtensionsTest.java      ← 43 tests (extended methods)
        ├── UserServiceTest.java            ← 6 tests
        ├── BlacklistServiceTest.java       ← 6 tests
        ├── PaymentMethodServiceTest.java   ← 12 tests
        └── WebhookServiceTest.java         ← 8 tests
```

---

## Running Tests

```bash
cd soap-sdk
mvn test
```

Expected: **118 tests, 0 failures, 0 errors.**

To run a single test class:

```bash
mvn test -Dtest=CleansterSOAPClientTest
mvn test -Dtest=UserServiceTest
mvn test -Dtest=WebhookServiceTest
```

---

## WSDL & XSD Details

### Service endpoint

```
https://api.cleanster.com/soap
```

### Namespaces

| Prefix | URI |
|---|---|
| `tns` | `https://api.cleanster.com/soap` |
| `types` | `https://api.cleanster.com/soap/types` |
| `soapenv` | `http://schemas.xmlsoap.org/soap/envelope/` |

### Binding style

**Document / Literal** — the standard style for modern SOAP services.
Each operation uses a single wrapper element matching the WSDL message name.

### Authentication

Authentication is handled via the HTTP `Authorization` header, not the SOAP header:
```
Authorization: Bearer <your-api-key>
```

---

## Generating Client Code from the WSDL

### wsimport (JDK built-in)

```bash
wsimport -keep -verbose \
  -p com.cleanster.soap.generated \
  -s src/main/java \
  wsdl/cleanster.wsdl
```

### Apache CXF

```bash
wsdl2java -d src/main/java \
  -p com.cleanster.soap.generated \
  wsdl/cleanster.wsdl
```

### Python (zeep)

```python
from zeep import Client

client = Client("path/to/cleanster.wsdl")
result = client.service.GetBooking(bookingId=16459)
```

---

## Error Handling

All errors throw `SOAPClientException` (unchecked):

```java
try {
    Booking booking = client.getBooking(99999);
} catch (SOAPClientException e) {
    System.err.println("HTTP status: " + e.getHttpStatus()); // e.g. 404
    System.err.println("Message: " + e.getMessage());
}
```

HTTP status codes:
| Code | Meaning |
|---|---|
| 400 | Bad Request — check required fields |
| 401 | Unauthorized — invalid or missing API key |
| 404 | Not Found — booking/property/cleaner ID does not exist |
| 422 | Unprocessable Entity — validation error |
| 429 | Too Many Requests — rate limit exceeded |
| 500 | Server Error — contact Cleanster support |

---

## Architecture Note

This SDK acts as a **SOAP-to-REST bridge**:

```
Your code  →  CleansterSOAPClient  →  SOAPTransport  →  Cleanster REST API
(SOAP API)     (62 operations)        (HTTP layer)        (JSON responses)
```

The `SOAPTransport` handles all HTTP communication, translating SOAP-style operation calls into the appropriate REST endpoints. Models are deserialized from JSON using Jackson and presented through the SOAP-compatible Java API defined in the WSDL.

---

## License

MIT
