# Cleanster Java SDK

Official Java SDK for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep) — manage cleaning service bookings, properties, users, checklists, payment methods, and more.

## Requirements

- Java 11+
- Maven 3.6+

## Installation

### Maven

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cleanster</groupId>
    <artifactId>cleanster-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Build from source

```bash
git clone https://github.com/cleanster/cleanster-java-sdk.git
cd cleanster-java-sdk
mvn install
```

## Quick Start

```java
import com.cleanster.sdk.client.CleansterClient;
import com.cleanster.sdk.model.*;

// 1. Create the client (sandbox for testing)
CleansterClient client = CleansterClient.sandboxClient("your-access-key");

// 2. Create a user and fetch their access token
CreateUserRequest userReq = new CreateUserRequest();
userReq.setEmail("partner@example.com");
userReq.setFirstName("Jane");
userReq.setLastName("Smith");
ApiResponse<User> user = client.users().createUser(userReq);

String token = client.users()
    .fetchAccessToken(user.getData().getId())
    .getData()
    .getToken();
client.setAccessToken(token);

// 3. List available services
ApiResponse<Object> services = client.other().getServices();

// 4. Create a booking
CreateBookingRequest booking = new CreateBookingRequest();
booking.setDate("2025-06-15");
booking.setTime("10:00");
booking.setPropertyId(1004);
booking.setRoomCount(2);
booking.setBathroomCount(1);
booking.setPlanId(5);
booking.setHours(3.0f);
booking.setExtraSupplies(false);
booking.setPaymentMethodId(10);

ApiResponse<Booking> created = client.bookings().createBooking(booking);
System.out.println("Booking ID: " + created.getData().getId());
```

## Configuration

```java
import com.cleanster.sdk.client.CleansterConfig;
import com.cleanster.sdk.client.CleansterClient;

// Sandbox (default for testing)
CleansterClient sandboxClient = CleansterClient.sandboxClient("your-access-key");

// Production
CleansterClient prodClient = CleansterClient.productionClient("your-access-key");

// Custom configuration
CleansterConfig config = CleansterConfig.sandboxBuilder("your-access-key")
    .connectTimeoutSeconds(10)
    .readTimeoutSeconds(30)
    .writeTimeoutSeconds(30)
    .build();
CleansterClient client = new CleansterClient(config);
```

## Authentication

The API uses two layers of authentication:

1. **Access Key** (`access-key` header) — your partner-level API key. Set once when creating the client.
2. **Bearer Token** (`Authorization` header) — a user-level token. Obtain it via `users().fetchAccessToken()` and set it with `client.setAccessToken(token)`.

```java
// After creating/fetching a user:
String token = client.users().fetchAccessToken(userId).getData().getToken();
client.setAccessToken(token);
// All subsequent calls will include this token
```

## API Reference

### Bookings (`client.bookings()`)

| Method | Description |
|--------|-------------|
| `getBookings(pageNo, status)` | List upcoming/past bookings |
| `createBooking(request)` | Schedule a new booking |
| `getBookingDetails(bookingId)` | Get booking details |
| `cancelBooking(bookingId, request)` | Cancel a booking |
| `rescheduleBooking(bookingId, request)` | Reschedule to new date/time |
| `assignCleaner(bookingId, request)` | Assign a cleaner |
| `removeAssignedCleaner(bookingId)` | Remove assigned cleaner |
| `adjustHours(bookingId, request)` | Change booking hours |
| `payExpenses(bookingId, request)` | Pay expenses (up to 72h after completion) |
| `getBookingInspection(bookingId)` | Get inspection report |
| `getBookingInspectionDetails(bookingId)` | Get detailed inspection info |
| `assignChecklistToBooking(bookingId, checklistId)` | Assign a checklist |
| `submitFeedback(bookingId, request)` | Submit rating/feedback |
| `addTip(bookingId, request)` | Add a tip (within 72h after completion) |
| `getChat(bookingId)` | Get chat messages |
| `sendMessage(bookingId, request)` | Send a chat message |
| `deleteMessage(bookingId, messageId)` | Delete a chat message |

### Users (`client.users()`)

| Method | Description |
|--------|-------------|
| `createUser(request)` | Create a new user account |
| `fetchAccessToken(userId)` | Get user's long-lived access token |
| `verifyJwt(request)` | Verify a JWT token |

### Properties (`client.properties()`)

| Method | Description |
|--------|-------------|
| `listProperties(serviceId)` | List all properties |
| `addProperty(request)` | Add a new property |
| `getProperty(propertyId)` | Get property details |
| `updateProperty(propertyId, request)` | Update a property |
| `updateAdditionalInformation(propertyId, data)` | Update property extras |
| `enableOrDisableProperty(propertyId, request)` | Enable/disable a property |
| `deleteProperty(propertyId)` | Delete a property |
| `getPropertyCleaners(propertyId)` | Get assigned cleaners |
| `assignCleanerToProperty(propertyId, request)` | Assign cleaner to property |
| `unassignCleanerFromProperty(propertyId, cleanerId)` | Remove cleaner from property |
| `addICalLink(propertyId, request)` | Add iCal calendar sync |
| `getICalLink(propertyId)` | Get iCal URL |
| `removeICalLink(propertyId, request)` | Remove iCal link |
| `assignChecklistToProperty(propertyId, checklistId, updateUpcoming)` | Assign checklist |

### Checklists (`client.checklists()`)

| Method | Description |
|--------|-------------|
| `listChecklists()` | List all checklists |
| `getChecklist(checklistId)` | Get checklist by ID |
| `createChecklist(request)` | Create a new checklist |
| `updateChecklist(checklistId, request)` | Update a checklist |
| `deleteChecklist(checklistId)` | Delete a checklist |

### Other (`client.other()`)

| Method | Description |
|--------|-------------|
| `getServices()` | View available cleaning services |
| `getPlans(propertyId)` | Get booking plans for a property |
| `getRecommendedHours(propertyId, bathrooms, rooms)` | Get recommended cleaning hours |
| `calculateCost(request)` | Calculate booking cost estimate |
| `getCleaningExtras(serviceId)` | Get available add-on services |
| `getAvailableCleaners(request)` | Find available cleaners for a slot |
| `getCoupons()` | List available coupon codes |

### Blacklist (`client.blacklist()`)

| Method | Description |
|--------|-------------|
| `listBlacklistedCleaners()` | List blacklisted cleaners |
| `addToBlacklist(request)` | Blacklist a cleaner |
| `removeFromBlacklist(request)` | Remove from blacklist |

### Payment Methods (`client.paymentMethods()`)

| Method | Description |
|--------|-------------|
| `getSetupIntentDetails()` | Get Stripe setup intent |
| `getPaypalClientToken()` | Get PayPal client token |
| `addPaymentMethod(request)` | Add a payment method |
| `getPaymentMethods()` | List all payment methods |
| `deletePaymentMethod(id)` | Delete a payment method |
| `setDefaultPaymentMethod(id)` | Set default payment method |

### Webhooks (`client.webhooks()`)

| Method | Description |
|--------|-------------|
| `listWebhooks()` | List configured webhooks |
| `createWebhook(request)` | Create a webhook endpoint |
| `updateWebhook(webhookId, request)` | Update a webhook |
| `deleteWebhook(webhookId)` | Delete a webhook |

## Error Handling

The SDK throws typed exceptions:

```java
import com.cleanster.sdk.exception.*;

try {
    ApiResponse<Booking> booking = client.bookings().getBookingDetails(99999);
} catch (CleansterAuthException e) {
    // 401 - Invalid or missing access key/token
    System.out.println("Auth failed: " + e.getMessage());
} catch (CleansterApiException e) {
    // Non-2xx response (404, 422, 500, etc.)
    System.out.println("API error " + e.getStatusCode() + ": " + e.getMessage());
    System.out.println("Response: " + e.getResponseBody());
} catch (CleansterException e) {
    // Network errors, serialization issues, etc.
    System.out.println("SDK error: " + e.getMessage());
}
```

## Test Coupon Codes (Sandbox)

| Code | Discount |
|------|----------|
| `100POFF` | 100% Off |
| `50POFF` | 50% Off |
| `20POFF` | 20% Off |
| `200OFF` | $200 Off |
| `100OFF` | $100 Off |

## Running Tests

```bash
mvn test
```

## Building

```bash
mvn package          # Compile + test + create JAR
mvn package -DskipTests  # Skip tests
mvn javadoc:jar      # Generate Javadoc JAR
```

## License

MIT License — see [LICENSE](LICENSE) for details.

## Support

- API Documentation: https://documenter.getpostman.com/view/26172658/2sAYdoF7ep
- Partner support: partner@cleanster.com
- General support: support@cleanster.com
