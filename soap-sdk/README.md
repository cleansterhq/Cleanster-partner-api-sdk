# Cleanster SOAP SDK

A Java SOAP SDK for the Cleanster Partner API. Provides a WSDL-defined, document/literal SOAP interface over all major Cleanster API operations — bookings, properties, cleaners, checklists, chat, and service types.

---

## Overview

| Item | Detail |
|---|---|
| Language | Java 11+ |
| Build | Maven |
| SOAP Style | Document / Literal (SOAP 1.1) |
| Auth | Bearer Token (API Key) |
| Tests | 43 (JUnit 5 + Mockito) |
| WSDL | `wsdl/cleanster.wsdl` |
| XSD Schema | `wsdl/cleanster-types.xsd` |

---

## SOAP Operations

### Bookings
| Operation | SOAPAction | Description |
|---|---|---|
| `GetBooking` | `.../GetBooking` | Retrieve a booking by ID |
| `ListBookings` | `.../ListBookings` | List bookings with optional filters |
| `CreateBooking` | `.../CreateBooking` | Create a new cleaning booking |
| `CancelBooking` | `.../CancelBooking` | Cancel a booking |
| `RescheduleBooking` | `.../RescheduleBooking` | Reschedule to a new date and time |
| `AssignCleaner` | `.../AssignCleaner` | Assign a cleaner to a booking |

### Properties
| Operation | SOAPAction | Description |
|---|---|---|
| `GetProperty` | `.../GetProperty` | Retrieve a property by ID |
| `ListProperties` | `.../ListProperties` | List all properties |
| `CreateProperty` | `.../CreateProperty` | Add a new property |

### Cleaners
| Operation | SOAPAction | Description |
|---|---|---|
| `ListCleaners` | `.../ListCleaners` | List cleaners (with optional status filter) |
| `GetCleaner` | `.../GetCleaner` | Retrieve a cleaner by ID |

### Checklists
| Operation | SOAPAction | Description |
|---|---|---|
| `ListChecklists` | `.../ListChecklists` | List all checklists |
| `GetChecklist` | `.../GetChecklist` | Retrieve a checklist by ID |
| `CreateChecklist` | `.../CreateChecklist` | Create a new checklist |
| `DeleteChecklist` | `.../DeleteChecklist` | Delete a checklist |

### Other
| Operation | SOAPAction | Description |
|---|---|---|
| `GetServices` | `.../GetServices` | List available service types |
| `GetChat` | `.../GetChat` | Get chat messages for a booking |
| `SendMessage` | `.../SendMessage` | Send a chat message on a booking |

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

// 2. Get a booking
Booking booking = client.getBooking(16459);
System.out.println(booking.getStatus());   // "scheduled"

// 3. List bookings
ListBookingsRequest req = new ListBookingsRequest()
    .setStatus("scheduled")
    .setPage(1)
    .setPerPage(20);
List<Booking> bookings = client.listBookings(req);

// 4. Create a booking
CreateBookingRequest create = new CreateBookingRequest()
    .setPropertyId(42L)
    .setScheduledAt("2025-07-15T09:00:00Z")
    .setDurationHours(3.0)
    .setServiceType("standard")
    .setNotes("Please focus on the kitchen.");
Booking newBooking = client.createBooking(create);

// 5. Cancel a booking
ApiResponse cancelled = client.cancelBooking(16459, "Customer rescheduled");
System.out.println(cancelled.isSuccess()); // true

// 6. Reschedule a booking
RescheduleBookingRequest reschedule = new RescheduleBookingRequest()
    .setBookingId(16459L)
    .setScheduledAt("2025-08-01T10:00:00Z");
Booking rescheduled = client.rescheduleBooking(reschedule);

// 7. Assign a cleaner
Booking assigned = client.assignCleaner(16459, 789);

// 8. Create a property
CreatePropertyRequest prop = new CreatePropertyRequest()
    .setAddress("456 Oak Ave")
    .setCity("Savannah")
    .setState("GA")
    .setZip("31401")
    .setName("Riverside Cottage")
    .setBedrooms(3)
    .setAccessInstructions("Key in lockbox. Code: 5523.");
Property property = client.createProperty(prop);

// 9. Upload a checklist image
byte[] imageBytes = Files.readAllBytes(Paths.get("bathroom-guide.jpg"));
ApiResponse uploaded = client.uploadChecklistImage(105, imageBytes, "bathroom-guide.jpg");

// 10. Send a chat message
ChatMessage msg = client.sendMessage(16459, "Please use eco-friendly products.");
```

---

## SOAP Envelope Examples

The `examples/` directory contains ready-to-use SOAP request envelopes for common operations:

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

### Sending with Postman

1. Set method to `POST`, URL to `https://api.cleanster.com/soap`
2. Headers: `Content-Type: text/xml`, `SOAPAction: https://api.cleanster.com/soap/GetBooking`, `Authorization: Bearer <key>`
3. Body: raw XML — paste the contents of the example file

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
    │   ├── CleansterSOAPClient.java   ← Main entry point (facade)
    │   ├── SOAPTransport.java         ← HTTP transport (REST bridge)
    │   ├── SOAPClientException.java   ← Runtime exception
    │   ├── BookingService.java        ← Booking operations
    │   ├── PropertyService.java       ← Property operations
    │   ├── CleanerService.java        ← Cleaner operations
    │   ├── ChecklistService.java      ← Checklist operations
    │   ├── OtherService.java          ← Services/chat operations
    │   └── model/
    │       ├── Booking.java
    │       ├── Property.java
    │       ├── Cleaner.java
    │       ├── Checklist.java
    │       ├── ServiceType.java
    │       ├── ChatMessage.java
    │       ├── ApiResponse.java
    │       ├── CreateBookingRequest.java
    │       ├── ListBookingsRequest.java
    │       ├── RescheduleBookingRequest.java
    │       └── CreatePropertyRequest.java
    └── test/java/com/cleanster/soap/
        └── CleansterSOAPClientTest.java   ← 43 tests
```

---

## Running Tests

```bash
cd soap-sdk
mvn test
```

Expected: **48 tests, 0 failures, 0 errors.**

To run a single test class:

```bash
mvn test -Dtest=CleansterSOAPClientTest
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

You can generate type-safe stubs using standard tooling:

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

### .NET (Add Service Reference)

In Visual Studio: **Add Service Reference → Advanced → Add Web Reference**
URL: point to your deployed WSDL location.

```bash
# Or via CLI:
dotnet-svcutil wsdl/cleanster.wsdl \
  --outputDir generated/ \
  --namespace "*,Cleanster.Soap"
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
(SOAP API)                           (HTTP layer)        (JSON responses)
```

The `SOAPTransport` handles all HTTP communication, translating SOAP-style operation calls into the appropriate REST endpoints. Models are deserialized from JSON using Jackson and presented through the SOAP-compatible Java API defined in the WSDL.

---

## License

MIT
