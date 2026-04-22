# Cleanster TypeScript SDK

<p align="center">
  <strong>Official TypeScript client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/TypeScript-5.x-blue?logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/Node.js-18%2B-green?logo=node.js" alt="Node.js 18+">
  <img src="https://img.shields.io/badge/npm-cleanster-orange?logo=npm" alt="npm">
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
- [Type Reference](#type-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Building & Publishing](#building--publishing)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster TypeScript SDK provides a fully-typed, async/await-based interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It works in Node.js 18+ and any bundled TypeScript application.

**What it gives you:**

- **100% TypeScript** — every request, response, and error is fully typed; zero `any` in your application code
- **Generic `ApiResponse<T>`** — the `data` field is statically typed for every API call
- **Dual-layer authentication** — partner access key + per-user bearer tokens, sent automatically on every request
- **Typed exceptions** — distinct classes for 401 auth errors, other API errors, and network failures
- **Environment switching** — one line to toggle between sandbox and production
- **Native `fetch` transport** — uses the built-in `fetch` API (Node 18+) with `AbortController` timeout support; zero external HTTP dependencies
- **Full JSDoc** — every method has hover documentation in your IDE
- **Tree-shakeable** — all API classes are individually importable

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Node.js | ≥ 18.0 |
| TypeScript | ≥ 4.7 (for `exactOptionalPropertyTypes` support) |

> **Why Node 18?** The SDK uses the native `fetch` API and `AbortController` introduced in Node 18. No `node-fetch` polyfill is needed.

---

## Installation

### npm

```bash
npm install cleanster
```

### yarn

```bash
yarn add cleanster
```

### pnpm

```bash
pnpm add cleanster
```

### Version pin

```bash
npm install cleanster@1.0.0
```

### Build from Source

```bash
git clone https://github.com/cleanster/cleanster-typescript-sdk.git
cd cleanster-typescript-sdk
npm install
npm run build
```

The compiled output lands in `dist/` with full `.d.ts` declaration files.

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** sent as HTTP headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

### Step-by-Step Authentication

**Step 1 — Initialize the client with your partner access key:**

```typescript
import { CleansterClient } from "cleanster";

const client = CleansterClient.sandbox("your-partner-access-key");
```

**Step 2 — Create or retrieve a user.** For new users, register them via the API:

```typescript
const { data: user } = await client.users.createUser({
  email: "user@example.com",
  firstName: "Jane",
  lastName: "Doe",
  phone: "+15551234567", // optional
});
console.log(`Created user #${user.id}`);
```

**Step 3 — Fetch the user's long-lived bearer token:**

```typescript
const { data: tokenUser } = await client.users.fetchAccessToken(user.id);
const userToken = tokenUser.token!;
```

**Step 4 — Set the token on the client** for all subsequent calls:

```typescript
client.setAccessToken(userToken);
// Every API call from this point forward includes the user token automatically
```

> **Tip:** The user token is long-lived. Store it securely in your database and call `setAccessToken()` at the start of each session — no need to re-fetch each time.

---

## Quick Start

```typescript
import { CleansterClient } from "cleanster";

async function main() {
  // 1. Initialize (sandbox for dev, production for live)
  const client = CleansterClient.sandbox("your-access-key");

  // 2. Create a user
  const { data: user } = await client.users.createUser({
    email: "jane@example.com",
    firstName: "Jane",
    lastName: "Smith",
  });

  // 3. Fetch and set the user access token
  const { data: tokenUser } = await client.users.fetchAccessToken(user.id);
  client.setAccessToken(tokenUser.token!);

  // 4. Add a property
  const { data: property } = await client.properties.addProperty({
    name: "Beach House",
    address: "123 Ocean Drive",
    city: "Miami",
    country: "USA",
    roomCount: 3,
    bathroomCount: 2,
    serviceId: 1,
  });

  // 5. Check recommended hours and estimate cost
  await client.other.getRecommendedHours(property.id, 2, 3);
  await client.other.calculateCost({
    propertyId: property.id,
    planId: 2,
    hours: 3,
    couponCode: "20POFF", // optional — sandbox test coupon
  });

  // 6. Create a booking
  const { data: booking } = await client.bookings.createBooking({
    date: "2025-06-15",
    time: "10:00",
    propertyId: property.id,
    roomCount: 3,
    bathroomCount: 2,
    planId: 2,
    hours: 3,
    extraSupplies: false,
    paymentMethodId: 10,
  });

  console.log(`Created booking #${booking.id} — status: ${booking.status}`);

  // 7. List all bookings
  const { status, message } = await client.bookings.getBookings();
  console.log(`${status}: ${message}`);
}

main().catch(console.error);
```

---

## Configuration

### Factory Methods (Recommended)

```typescript
import { CleansterClient } from "cleanster";

// Sandbox — for development and testing (no real charges or cleaners)
const client = CleansterClient.sandbox("your-access-key");

// Production — for live use (real cleaners, real charges)
const client = CleansterClient.production("your-access-key");
```

### Builder Pattern (Custom Configuration)

```typescript
import { CleansterClient, CleansterConfig } from "cleanster";

const config = CleansterConfig.builder("your-access-key")
  .sandbox()            // or .production()
  .timeoutMs(60_000)   // request timeout in milliseconds (default: 30,000)
  .build();

const client = new CleansterClient(config);
```

### Custom Base URL

```typescript
const config = CleansterConfig.builder("your-access-key")
  .baseUrl("https://your-proxy.example.com/api")
  .build();
```

### Environment Base URLs

| Environment | Base URL |
|-------------|----------|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## API Reference

Every method returns a `Promise<ApiResponse<T>>`. Destructure `{ data }` for the typed payload, or check `{ status, message }` for the response metadata. See [Response Structure](#response-structure) for full details.

---

### Bookings (`client.bookings`)

The booking API manages the full lifecycle of a cleaning appointment.

#### `getBookings(pageNo?, status?)`

Retrieve a paginated list of bookings. All parameters are optional.

```typescript
// All bookings
const result = await client.bookings.getBookings();

// Filter by status
const completed = await client.bookings.getBookings(undefined, "COMPLETED");

// Page 2 of open bookings
const page2 = await client.bookings.getBookings(2, "OPEN");
```

**Valid status values:** `"OPEN"` | `"CLEANER_ASSIGNED"` | `"COMPLETED"` | `"CANCELLED"` | `"REMOVED"`

---

#### `createBooking(request: CreateBookingRequest)`

Schedule a new cleaning appointment.

```typescript
const { data: booking } = await client.bookings.createBooking({
  date: "2025-06-15",          // Required — YYYY-MM-DD
  time: "10:00",               // Required — HH:mm (24-hour)
  propertyId: 1004,            // Required
  roomCount: 2,                // Required
  bathroomCount: 1,            // Required
  planId: 5,                   // Required — from getPlans()
  hours: 3,                    // Required — from getRecommendedHours()
  extraSupplies: false,        // Required — include cleaning supplies?
  paymentMethodId: 10,         // Required
  couponCode: "20POFF",        // Optional
  extras: [101, 102],          // Optional — extra service IDs
});

console.log(booking.id);       // number
console.log(booking.status);  // "OPEN"
console.log(booking.cost);    // number
```

---

#### `getBookingDetails(bookingId: number)`

```typescript
const { data: booking } = await client.bookings.getBookingDetails(16926);
console.log(booking.date);         // "2025-06-15"
console.log(booking.hours);        // 3
console.log(booking.cleanerId);    // number | null
console.log(booking.cost);         // number
```

---

#### `cancelBooking(bookingId, request?)`

```typescript
// With a cancellation reason
await client.bookings.cancelBooking(16459, { reason: "Changed my schedule" });

// Without a reason (reason is optional)
await client.bookings.cancelBooking(16459);
```

---

#### `rescheduleBooking(bookingId, request)`

```typescript
await client.bookings.rescheduleBooking(16459, {
  date: "2025-07-01",
  time: "14:00",
});
```

---

#### `assignCleaner(bookingId, request)` / `removeAssignedCleaner(bookingId)`

```typescript
// Assign
await client.bookings.assignCleaner(16459, { cleanerId: 5 });

// Remove
await client.bookings.removeAssignedCleaner(16459);
```

---

#### `adjustHours(bookingId, request)`

```typescript
await client.bookings.adjustHours(16459, { hours: 4.0 });
```

---

#### `payExpenses(bookingId, request)`

Pay outstanding expenses within 72 hours of booking completion.

```typescript
await client.bookings.payExpenses(16926, { paymentMethodId: 10 });
```

---

#### `getBookingInspection(bookingId)` / `getBookingInspectionDetails(bookingId)`

```typescript
const inspection = await client.bookings.getBookingInspection(16926);
const details = await client.bookings.getBookingInspectionDetails(16926);
```

---

#### `assignChecklistToBooking(bookingId, checklistId)`

Override the property's default checklist for this specific booking.

```typescript
await client.bookings.assignChecklistToBooking(16926, 105);
```

---

#### `submitFeedback(bookingId, request)`

Submit a star rating (1–5) and optional comment after a booking completes.

```typescript
await client.bookings.submitFeedback(16926, {
  rating: 5,
  comment: "Excellent — very thorough!",
});
```

---

#### `addTip(bookingId, request)`

Add a tip for the cleaner (within 72 hours of booking completion).

```typescript
await client.bookings.addTip(16926, {
  amount: 20.0,
  paymentMethodId: 10,
});
```

---

#### Chat — `getChat`, `sendMessage`, `deleteMessage`

```typescript
// Get all messages in a thread
const chat = await client.bookings.getChat(17142);

// Send a message
await client.bookings.sendMessage(17142, { message: "Please focus on the kitchen today." });

// Delete a message
await client.bookings.deleteMessage(17142, "msg-abc-123");
```

---

**Bookings API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getBookings(pageNo?, status?)` | GET | `/v1/bookings` |
| `createBooking(req)` | POST | `/v1/bookings/create` |
| `getBookingDetails(id)` | GET | `/v1/bookings/{id}` |
| `cancelBooking(id, req?)` | POST | `/v1/bookings/{id}/cancel` |
| `rescheduleBooking(id, req)` | POST | `/v1/bookings/{id}/reschedule` |
| `assignCleaner(id, req)` | POST | `/v1/bookings/{id}/cleaner` |
| `removeAssignedCleaner(id)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `adjustHours(id, req)` | POST | `/v1/bookings/{id}/hours` |
| `payExpenses(id, req)` | POST | `/v1/bookings/{id}/expenses` |
| `getBookingInspection(id)` | GET | `/v1/bookings/{id}/inspection` |
| `getBookingInspectionDetails(id)` | GET | `/v1/bookings/{id}/inspection/details` |
| `assignChecklistToBooking(id, cid)` | POST | `/v1/bookings/{id}/checklist/{cid}` |
| `submitFeedback(id, req)` | POST | `/v1/bookings/{id}/feedback` |
| `addTip(id, req)` | POST | `/v1/bookings/{id}/tip` |
| `getChat(id)` | GET | `/v1/bookings/{id}/chat` |
| `sendMessage(id, req)` | POST | `/v1/bookings/{id}/chat` |
| `deleteMessage(id, msgId)` | DELETE | `/v1/bookings/{id}/chat/{msgId}` |

---

### Users (`client.users`)

#### `createUser(request: CreateUserRequest)`

```typescript
const { data: user } = await client.users.createUser({
  email: "jane@example.com",    // Required
  firstName: "Jane",            // Required
  lastName: "Smith",            // Required
  phone: "+15551234567",        // Optional
});

// TypeScript knows: user is User
console.log(user.id);          // number
console.log(user.email);       // string
console.log(user.firstName);   // string
```

---

#### `fetchAccessToken(userId: number)`

Fetch the long-lived bearer token. Store it in your database and reuse it across sessions.

```typescript
const { data: tokenUser } = await client.users.fetchAccessToken(user.id);
const token: string = tokenUser.token!;

// Authenticate all subsequent requests:
client.setAccessToken(token);
```

---

#### `verifyJwt(request: VerifyJwtRequest)`

```typescript
const result = await client.users.verifyJwt({ token: "eyJhbGci..." });
console.log(result.status); // 200 if valid
```

---

**Users API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `createUser(req)` | POST | `/v1/user/account` |
| `fetchAccessToken(userId)` | GET | `/v1/user/access-token/{userId}` |
| `verifyJwt(req)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`client.properties`)

#### `listProperties(serviceId?)`

```typescript
// All properties
const all = await client.properties.listProperties();

// Only residential properties (serviceId = 1)
const residential = await client.properties.listProperties(1);
```

---

#### `addProperty(request: CreatePropertyRequest)`

```typescript
const { data: property } = await client.properties.addProperty({
  name: "Downtown Condo",
  address: "456 Main St",
  city: "Toronto",
  country: "Canada",
  roomCount: 2,
  bathroomCount: 1,
  serviceId: 1,
});

// TypeScript knows: property is Property
console.log(property.id);     // number
console.log(property.name);   // string
```

---

#### CRUD Operations

```typescript
// Get
const { data: prop } = await client.properties.getProperty(1040);
console.log(prop.city); // string

// Update
await client.properties.updateProperty(1040, {
  name: "Renovated Condo",
  address: "456 Main St",
  city: "Toronto",
  country: "Canada",
  roomCount: 3,
  bathroomCount: 1,
  serviceId: 1,
});

// Enable / Disable
await client.properties.enableOrDisableProperty(1040, { enabled: false });

// Delete
await client.properties.deleteProperty(1040);
```

---

#### Property Cleaners

```typescript
// List assigned cleaners
const cleaners = await client.properties.getPropertyCleaners(1040);

// Assign a cleaner
await client.properties.assignCleanerToProperty(1040, { cleanerId: 5 });

// Unassign a cleaner
await client.properties.unassignCleanerFromProperty(1040, 5);
```

---

#### iCal Calendar Integration

Sync a property's availability with Airbnb, VRBO, or any iCal-compatible calendar.

```typescript
// Add iCal link
await client.properties.addICalLink(1040, {
  icalLink: "https://calendar.example.com/feed.ics",
});

// Get current iCal link
const ical = await client.properties.getICalLink(1040);

// Remove iCal link
await client.properties.removeICalLink(1040, {
  icalLink: "https://calendar.example.com/feed.ics",
});
```

---

#### `assignChecklistToProperty(propertyId, checklistId, updateUpcomingBookings?)`

```typescript
// Assign and apply to all upcoming bookings at this property
await client.properties.assignChecklistToProperty(1040, 105, true);
```

---

**Properties API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listProperties(serviceId?)` | GET | `/v1/properties` |
| `addProperty(req)` | POST | `/v1/properties` |
| `getProperty(id)` | GET | `/v1/properties/{id}` |
| `updateProperty(id, req)` | PUT | `/v1/properties/{id}` |
| `updateAdditionalInformation(id, data)` | PUT | `/v1/properties/{id}/additional-information` |
| `enableOrDisableProperty(id, req)` | POST | `/v1/properties/{id}/enable-disable` |
| `deleteProperty(id)` | DELETE | `/v1/properties/{id}` |
| `getPropertyCleaners(id)` | GET | `/v1/properties/{id}/cleaners` |
| `assignCleanerToProperty(id, req)` | POST | `/v1/properties/{id}/cleaners` |
| `unassignCleanerFromProperty(id, cleanerId)` | DELETE | `/v1/properties/{id}/cleaners/{cid}` |
| `addICalLink(id, req)` | PUT | `/v1/properties/{id}/ical` |
| `getICalLink(id)` | GET | `/v1/properties/{id}/ical` |
| `removeICalLink(id, req)` | DELETE | `/v1/properties/{id}/ical` |
| `assignChecklistToProperty(id, cid, flag?)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`client.checklists`)

Checklists define the tasks a cleaner should complete during a booking. They can be set as property defaults or overridden per booking.

#### `listChecklists()`

```typescript
const all = await client.checklists.listChecklists();
```

---

#### `getChecklist(checklistId: number)`

```typescript
const { data: checklist } = await client.checklists.getChecklist(105);

// TypeScript knows: checklist is Checklist
console.log(checklist.name); // string
checklist.items.forEach(item => {
  // TypeScript knows: item is ChecklistItem
  console.log(`[${item.isCompleted ? "✓" : " "}] ${item.description}`);
});
```

---

#### `createChecklist(request: CreateChecklistRequest)`

```typescript
const { data: checklist } = await client.checklists.createChecklist({
  name: "Standard Residential Clean",
  items: [
    "Vacuum all floors",
    "Mop kitchen and bathroom floors",
    "Wipe all countertops",
    "Scrub toilets, sinks, and tubs",
    "Empty all trash bins",
    "Wipe mirrors and glass surfaces",
  ],
});
console.log(`Created checklist #${checklist.id}`);
```

---

#### `updateChecklist(checklistId, request)` / `deleteChecklist(checklistId)`

```typescript
// Update
await client.checklists.updateChecklist(105, {
  name: "Deep Clean",
  items: ["All standard tasks", "Inside oven", "Inside fridge"],
});

// Delete
await client.checklists.deleteChecklist(105);
```

---

**Checklists API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listChecklists()` | GET | `/v1/checklist` |
| `getChecklist(id)` | GET | `/v1/checklist/{id}` |
| `createChecklist(req)` | POST | `/v1/checklist` |
| `updateChecklist(id, req)` | PUT | `/v1/checklist/{id}` |
| `deleteChecklist(id)` | DELETE | `/v1/checklist/{id}` |

---

### Other / Utilities (`client.other`)

Reference data endpoints used when building booking flows.

#### `getServices()`

Returns all available cleaning service types.

```typescript
const { data: services } = await client.other.getServices();
```

---

#### `getPlans(propertyId: number)`

Returns all available booking plans for a given property (Standard, Deep Clean, Move-In/Move-Out, etc.).

```typescript
const { data: plans } = await client.other.getPlans(1004);
```

---

#### `getRecommendedHours(propertyId, bathroomCount, roomCount)`

Returns the system-recommended number of cleaning hours based on property size. Use this to pre-fill the `hours` field when creating a booking.

```typescript
const { data: rec } = await client.other.getRecommendedHours(1004, 2, 3);
```

---

#### `calculateCost(request: CostEstimateRequest)`

Calculate the estimated price for a booking before committing. Use this to show a cost preview.

```typescript
const estimate = await client.other.calculateCost({
  propertyId: 1004,
  planId: 2,
  hours: 3,
  couponCode: "20POFF",  // optional
  extras: [101],         // optional
});
```

---

#### `getCleaningExtras(serviceId: number)`

Returns available add-on services for a given service type (inside fridge, inside oven, laundry, etc.).

```typescript
const extras = await client.other.getCleaningExtras(1);
```

---

#### `getAvailableCleaners(request: AvailableCleanersRequest)`

Find cleaners available for a specific property, date, and time slot.

```typescript
const cleaners = await client.other.getAvailableCleaners({
  propertyId: 1004,
  date: "2025-06-15",
  time: "10:00",
});
```

---

#### `getCoupons()`

Returns all valid coupon codes available for use at booking creation.

```typescript
const coupons = await client.other.getCoupons();
```

---

**Other API Summary**

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

### Blacklist (`client.blacklist`)

Prevent specific cleaners from being auto-assigned to your bookings.

```typescript
// List all blacklisted cleaners
const { data: list } = await client.blacklist.listBlacklistedCleaners();

// Add a cleaner to the blacklist
await client.blacklist.addToBlacklist({
  cleanerId: 7,
  reason: "Damaged furniture during last booking",
});

// Remove a cleaner from the blacklist
await client.blacklist.removeFromBlacklist({ cleanerId: 7 });
```

**Blacklist API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listBlacklistedCleaners()` | GET | `/v1/blacklist/cleaner` |
| `addToBlacklist(req)` | POST | `/v1/blacklist/cleaner` |
| `removeFromBlacklist(req)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`client.paymentMethods`)

Manage Stripe and PayPal payment methods for your users.

```typescript
// Stripe — get setup intent for client-side card collection
const { data: intent } = await client.paymentMethods.getSetupIntentDetails();
// Use intent.clientSecret with Stripe.js

// PayPal — get client token for PayPal button rendering
const { data: paypal } = await client.paymentMethods.getPaypalClientToken();

// Add a payment method (after client-side tokenization)
await client.paymentMethods.addPaymentMethod({
  paymentMethodId: "pm_xxxxxxxxxxxx", // Stripe token
});

// List all saved payment methods
const methods = await client.paymentMethods.getPaymentMethods();

// Set default payment method
await client.paymentMethods.setDefaultPaymentMethod(193);

// Delete a payment method
await client.paymentMethods.deletePaymentMethod(193);
```

**Payment Methods API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getSetupIntentDetails()` | GET | `/v1/payment-methods/setup-intent` |
| `getPaypalClientToken()` | GET | `/v1/payment-methods/paypal-client-token` |
| `addPaymentMethod(req)` | POST | `/v1/payment-methods` |
| `getPaymentMethods()` | GET | `/v1/payment-methods` |
| `deletePaymentMethod(id)` | DELETE | `/v1/payment-methods/{id}` |
| `setDefaultPaymentMethod(id)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`client.webhooks`)

Receive real-time notifications when booking events occur — no polling required.

```typescript
// List all configured webhook endpoints
const { data: hooks } = await client.webhooks.listWebhooks();

// Register a new webhook
const { data: hook } = await client.webhooks.createWebhook({
  url: "https://your-app.com/webhooks/cleanster",
  event: "booking.status_changed",
});

// Update a webhook
await client.webhooks.updateWebhook(50, {
  url: "https://your-app.com/v2/webhooks",
  event: "booking.status_changed",
});

// Delete a webhook
await client.webhooks.deleteWebhook(50);
```

**Webhooks API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listWebhooks()` | GET | `/v1/webhooks` |
| `createWebhook(req)` | POST | `/v1/webhooks` |
| `updateWebhook(id, req)` | PUT | `/v1/webhooks/{id}` |
| `deleteWebhook(id)` | DELETE | `/v1/webhooks/{id}` |

---

## Error Handling

All SDK methods are async and throw typed errors. Catch the most specific type first.

```typescript
import {
  CleansterClient,
  CleansterAuthException,
  CleansterApiException,
  CleansterException,
} from "cleanster";

const client = CleansterClient.sandbox("your-key");
client.setAccessToken("user-token");

try {
  const { data: booking } = await client.bookings.getBookingDetails(99999);

} catch (err) {
  if (err instanceof CleansterAuthException) {
    // HTTP 401 — bad access key or user token
    console.error(`Auth failed [${err.statusCode}]: ${err.message}`);
    console.error("Response body:", err.responseBody);
    // Prompt the user to re-authenticate

  } else if (err instanceof CleansterApiException) {
    // HTTP 4xx / 5xx — API-level error
    console.error(`API error [${err.statusCode}]: ${err.message}`);
    console.error("Response body:", err.responseBody);

    if (err.statusCode === 404) {
      console.error("Resource not found.");
    } else if (err.statusCode === 422) {
      console.error("Validation error — check your request fields.");
    } else if (err.statusCode >= 500) {
      console.error("Server error — retry after a short delay.");
    }

  } else if (err instanceof CleansterException) {
    // Network failure, timeout, or JSON parse error
    console.error("SDK/network error:", err.message);

  } else {
    throw err; // Unknown error — re-throw
  }
}
```

### Exception Hierarchy

```
CleansterException          ← extends Error
├── CleansterAuthException  ← HTTP 401 (invalid/missing credentials)
└── CleansterApiException   ← HTTP 4xx / 5xx (API-level errors)
```

| Exception | When Thrown | Key Properties |
|-----------|-------------|----------------|
| `CleansterException` | Network error, timeout, JSON parse failure | `message` |
| `CleansterAuthException` | HTTP 401 | `statusCode` (always `401`), `responseBody` |
| `CleansterApiException` | HTTP 4xx/5xx (not 401) | `statusCode`, `responseBody` |

All exceptions correctly implement `instanceof` checks (using `Object.setPrototypeOf` to fix TypeScript class extension in CommonJS).

---

## Response Structure

Every SDK method returns `Promise<ApiResponse<T>>`, where `T` is statically typed.

```typescript
interface ApiResponse<T> {
  status: number;   // HTTP-style code (e.g., 200)
  message: string;  // Human-readable status (e.g., "OK")
  data: T;          // Typed response payload
}
```

**Destructuring pattern:**

```typescript
// Full destructure
const { status, message, data: booking } = await client.bookings.getBookingDetails(16926);
console.log(status);          // 200
console.log(message);         // "OK"
console.log(booking.id);      // number — TypeScript knows the type!
console.log(booking.status);  // "OPEN" | "CLEANER_ASSIGNED" | "COMPLETED" | ...
console.log(booking.hours);   // number
console.log(booking.cost);    // number
```

**TypeScript auto-completion:**
Because `data` is fully typed, your IDE gives you auto-complete on every field — `booking.id`, `user.token`, `checklist.items[0].description`, etc.

---

## Type Reference

All types are re-exported from the root `"cleanster"` package:

```typescript
import type {
  // Response wrapper
  ApiResponse,

  // Domain models
  Booking,
  User,
  Property,
  Checklist,
  ChecklistItem,
  PaymentMethod,

  // Booking request types
  CreateBookingRequest,
  CancelBookingRequest,
  RescheduleBookingRequest,
  AdjustHoursRequest,
  AssignCleanerRequest,
  PayExpensesRequest,
  FeedbackRequest,
  TipRequest,
  SendMessageRequest,

  // User request types
  CreateUserRequest,
  VerifyJwtRequest,

  // Property request types
  CreatePropertyRequest,
  EnableDisablePropertyRequest,
  AssignCleanerToPropertyRequest,
  ICalRequest,

  // Checklist request types
  CreateChecklistRequest,

  // Other request types
  CostEstimateRequest,
  AvailableCleanersRequest,

  // Blacklist request types
  BlacklistRequest,

  // Webhook request types
  WebhookRequest,
} from "cleanster";
```

### `Booking`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | Booking ID |
| `status` | `"OPEN" \| "CLEANER_ASSIGNED" \| "COMPLETED" \| "CANCELLED" \| "REMOVED"` | Current status |
| `date` | `string` | Booking date (YYYY-MM-DD) |
| `time` | `string` | Start time (HH:mm) |
| `hours` | `number` | Duration in hours |
| `cost` | `number` | Total cost |
| `propertyId` | `number` | Associated property ID |
| `cleanerId` | `number \| null` | Assigned cleaner ID (`null` if unassigned) |
| `planId` | `number` | Booking plan ID |
| `roomCount` | `number` | Number of rooms |
| `bathroomCount` | `number` | Number of bathrooms |
| `extraSupplies` | `boolean` | Whether cleaning supplies are included |
| `paymentMethodId` | `number` | Payment method ID used for billing |

### `User`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | User ID |
| `email` | `string` | Email address |
| `firstName` | `string` | First name |
| `lastName` | `string` | Last name |
| `phone` | `string?` | Phone number (optional) |
| `token` | `string?` | Bearer token — only present after `fetchAccessToken()` |

### `Property`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | Property ID |
| `name` | `string` | Property name/label |
| `address` | `string` | Street address |
| `city` | `string` | City |
| `country` | `string` | Country |
| `roomCount` | `number` | Number of bedrooms/rooms |
| `bathroomCount` | `number` | Number of bathrooms |
| `serviceId` | `number` | Service type ID |
| `isEnabled` | `boolean?` | Whether the property is active |

### `Checklist`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | Checklist ID |
| `name` | `string` | Checklist name |
| `items` | `ChecklistItem[]` | Task items |

### `ChecklistItem`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | Item ID |
| `description` | `string` | Task description |
| `isCompleted` | `boolean` | Whether the cleaner marked it complete |
| `imageUrl` | `string?` | Proof photo URL (if uploaded by cleaner) |

### `PaymentMethod`

| Field | Type | Description |
|-------|------|-------------|
| `id` | `number` | Payment method ID |
| `type` | `"card" \| "paypal" \| string` | Payment method type |
| `lastFour` | `string?` | Last 4 digits (cards only) |
| `brand` | `string?` | Card brand (e.g. `"visa"`, `"mastercard"`) |
| `isDefault` | `boolean` | Whether this is the default payment method |

---

## Sandbox vs Production

| Feature | Sandbox | Production |
|---------|---------|------------|
| Real charges | No | Yes |
| Real cleaners dispatched | No | Yes |
| Coupon codes | Test codes work | Real codes only |
| Data persistence | Yes (sandbox DB) | Yes (production DB) |
| API base URL | `partner-sandbox-dot-...` | `partner-dot-...` |

> **Always develop and test against the sandbox environment.** Switch to production only when you are ready to go live.

```typescript
// Development / CI
const client = CleansterClient.sandbox(process.env.CLEANSTER_API_KEY!);

// Production
const client = CleansterClient.production(process.env.CLEANSTER_API_KEY!);
```

---

## Test Coupon Codes (Sandbox Only)

These codes work only in the sandbox environment. Use them to test discount flows without real charges.

| Code | Discount | Suggested Test Scenario |
|------|----------|------------------------|
| `100POFF` | 100% off (free booking) | Verify zero-cost booking flow |
| `50POFF` | 50% off | Verify percentage discount calculation |
| `20POFF` | 20% off | Verify small percentage discount |
| `200OFF` | $200 flat discount | Verify flat-rate discount |
| `100OFF` | $100 flat discount | Verify partial flat discount |

Pass the code in the `couponCode` field of `CreateBookingRequest` or `CostEstimateRequest`.

---

## Building & Publishing

### Development Build

```bash
# Type-check only (no output)
npm run typecheck

# Compile to dist/
npm run build

# Watch mode
npm run build:watch
```

### Publish to npm

```bash
# This runs typecheck → tests → build → publish
npm publish
```

The `prepublishOnly` script ensures nothing broken is ever published.

**Output in `dist/`:**

| File | Description |
|------|-------------|
| `index.js` | Compiled CommonJS bundle |
| `index.d.ts` | TypeScript declaration file |
| `**/*.js.map` | Source maps |
| `**/*.d.ts.map` | Declaration maps (for "Go to source" in IDEs) |

---

## Running Tests

The test suite contains **85 unit tests** using Jest and `ts-jest`. All tests mock the `HttpClient` using `jest.fn()` — no network access or API keys are needed.

### Setup

```bash
git clone https://github.com/cleanster/cleanster-typescript-sdk.git
cd cleanster-typescript-sdk
npm install
```

### Run Tests

```bash
# Run all tests
npm test

# Run with verbose output
npm test -- --verbose

# Run a specific test file
npm test -- tests/cleanster.test.ts

# Run tests matching a name pattern
npm test -- --testNamePattern="BookingsApi"

# Run with coverage report
npm run test:coverage
```

### Coverage Report

```bash
npm run test:coverage
# → Writes HTML report to coverage/lcov-report/index.html
```

**Test coverage includes:**

| Area | Tests |
|------|-------|
| `CleansterConfig` | 10 — URL assignment, blank key rejection, builder, custom timeout |
| `CleansterClient` | 6 — factory methods, API namespace types, token get/set/clear |
| `BookingsApi` | 19 — all 17 endpoints + edge cases (optional params, empty bodies) |
| `UsersApi` | 4 — create, phone field, fetchAccessToken, verifyJwt |
| `PropertiesApi` | 12 — CRUD, enable/disable, cleaners, iCal, checklist, serviceId filter |
| `ChecklistsApi` | 5 — list, get (with typed items), create, update, delete |
| `OtherApi` | 7 — all 7 utility endpoints |
| `BlacklistApi` | 3 — list, add, remove |
| `PaymentMethodsApi` | 6 — all 6 methods |
| `WebhooksApi` | 4 — list, create, update, delete |
| `Exceptions` | 9 — statusCode, message, instanceof, name, propagation |

---

## Project Structure

```
cleanster-typescript-sdk/
├── src/
│   ├── index.ts              ← Public API surface (all exports)
│   ├── client.ts             ← CleansterClient (main entry point)
│   ├── config.ts             ← CleansterConfig + CleansterConfigBuilder
│   ├── exceptions.ts         ← CleansterException hierarchy
│   ├── http-client.ts        ← fetch wrapper with auth headers + timeout
│   ├── api/
│   │   ├── bookings.ts       ← BookingsApi (17 methods)
│   │   ├── users.ts          ← UsersApi (3 methods)
│   │   ├── properties.ts     ← PropertiesApi (14 methods)
│   │   ├── checklists.ts     ← ChecklistsApi (5 methods)
│   │   ├── other.ts          ← OtherApi (7 methods)
│   │   ├── blacklist.ts      ← BlacklistApi (3 methods)
│   │   ├── payment-methods.ts ← PaymentMethodsApi (6 methods)
│   │   └── webhooks.ts       ← WebhooksApi (4 methods)
│   └── models/
│       ├── index.ts           ← Re-exports all model types
│       ├── booking.ts         ← Booking + all booking request types
│       ├── checklist.ts       ← Checklist, ChecklistItem, CreateChecklistRequest
│       ├── payment-method.ts  ← PaymentMethod, AddPaymentMethodRequest
│       ├── property.ts        ← Property + all property request types
│       ├── response.ts        ← ApiResponse<T>
│       └── user.ts            ← User, CreateUserRequest, VerifyJwtRequest
├── tests/
│   └── cleanster.test.ts     ← 85 Jest unit tests
├── dist/                     ← Compiled output (after npm run build)
├── package.json
├── tsconfig.json
├── jest.config.js
├── README.md
├── LICENSE
└── CHANGELOG.md
```

---

## Contributing

1. Fork the repository on GitHub.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and add tests.
4. Ensure all tests pass: `npm test`
5. Ensure no TypeScript errors: `npm run typecheck`
6. Submit a pull request.

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
| GitHub Issues | https://github.com/cleanster/cleanster-typescript-sdk/issues |
| npm Package | https://www.npmjs.com/package/cleanster |

---

*Made with care for the Cleanster partner ecosystem.*
