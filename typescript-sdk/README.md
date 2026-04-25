# Cleanster TypeScript SDK

<p align="center">
  <strong>Official TypeScript client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/TypeScript-5.x-blue?logo=typescript" alt="TypeScript 5">
  <img src="https://img.shields.io/badge/Node.js-18%2B-green?logo=node.js" alt="Node.js 18+">
  <img src="https://img.shields.io/badge/tests-89%20passing-brightgreen" alt="89 passing">
  <img src="https://img.shields.io/badge/dependencies-zero-brightgreen" alt="Zero dependencies">
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
- [Type Reference](#type-reference)
- [Error Handling](#error-handling)
- [Test Coupon Codes](#test-coupon-codes)
- [Chat Window Rules](#chat-window-rules)
- [Webhook Events](#webhook-events)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## Overview

The Cleanster TypeScript SDK provides a fully-typed, async/await interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It works in Node.js 18+ with zero external runtime dependencies (uses the native `fetch` API).

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal calendar sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create task lists and assign to bookings or properties
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — prevent specific cleaners from being assigned

---

## Requirements

- **Node.js 18+** (for native `fetch`)
- **TypeScript 4.7+** (or plain JavaScript)
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

```bash
npm install cleanster
# or
yarn add cleanster
# or
pnpm add cleanster
```

Install from source:

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/typescript-sdk
npm install && npm run build
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, from `users.fetchAccessToken(userId)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```typescript
import { CleansterClient } from 'cleanster';

const client = new CleansterClient({ accessKey: 'your-access-key' });

const resp = await client.users.createUser({
  email: 'jane@example.com',
  firstName: 'Jane',
  lastName: 'Doe',
  phone: '+15551234567',
});
const userId = resp.data.userId;
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```typescript
const tokenResp = await client.users.fetchAccessToken(userId);
const userToken = tokenResp.data.token;
```

**Step 4 — Build the client with both credentials**:

```typescript
const client = new CleansterClient({
  accessKey: 'your-access-key',
  token: userToken,
});
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401.

---

## Quick Start

```typescript
import { CleansterClient } from 'cleanster';

const client = new CleansterClient({
  accessKey: 'your-access-key',
  token: 'user-jwt-token',
});

// Get recommended cleaning duration
const hours = await client.other.getRecommendedHours({
  propertyId: 1004,
  bathroomCount: 2,
  roomCount: 3,
});
console.log('Recommended hours:', hours.data);

// Create a booking
const booking = await client.bookings.createBooking({
  propertyId: 1004,
  date: '2025-09-01',
  time: '10:00',
  planId: 2,
  roomCount: 3,
  bathroomCount: 2,
  hours: 3,
  extraSupplies: false,
  paymentMethodId: 10,
  couponCode: '20POFF', // optional — 20% off in sandbox
});
console.log('Created booking ID:', booking.data.id);

// List open bookings
const list = await client.bookings.getBookings({ pageNo: 1, status: 'OPEN' });
console.log('Open bookings:', list.data.bookings.length);
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```typescript
// Sandbox (default)
const client = new CleansterClient({ accessKey: 'key', token: 'token' });

// Production
const client = new CleansterClient({
  accessKey: 'key',
  token: 'token',
  baseUrl: 'https://partner-dot-official-tidyio-project.ue.r.appspot.com/public',
});
```

---

## Booking Flow

```
createBooking()        →   OPEN
                               │
     bookings.assignCleaner()
                               │
                               ▼
                     CLEANER_ASSIGNED
                               │
                  Cleaner starts the job
                               │
                ┌──────────────┴──────────────┐
                ▼                             ▼
           COMPLETED                     CANCELLED
```

Status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All methods return `Promise<ApiResponse<T>>` with:
- `.status` — HTTP status code
- `.message` — Human-readable result
- `.data` — Typed response payload

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `pageNo` | number | Yes | Page number (1-based) |
| `status` | string | No | `OPEN` · `CLEANER_ASSIGNED` · `COMPLETED` · `CANCELLED` · `REMOVED` |

```typescript
const resp = await client.bookings.getBookings({ pageNo: 1, status: 'OPEN' });
resp.data.bookings.forEach(b => console.log(b.id, b.status));
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

```typescript
const resp = await client.bookings.getBooking(16926);
console.log(resp.data.status, 'on', resp.data.date);
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | number | Yes | Property to clean |
| `date` | string | Yes | `YYYY-MM-DD` |
| `time` | string | Yes | `HH:MM` (24-hour) |
| `planId` | number | Yes | Cleaning plan ID |
| `roomCount` | number | Yes | Number of rooms |
| `bathroomCount` | number | Yes | Number of bathrooms |
| `hours` | number | Yes | Scheduled duration |
| `extraSupplies` | boolean | Yes | Cleaner brings supplies |
| `paymentMethodId` | number | Yes | Payment method ID |
| `couponCode` | string | No | Discount coupon |
| `cleaningExtras` | number[] | No | Extra service IDs |

```typescript
const resp = await client.bookings.createBooking({
  propertyId: 1004,
  date: '2025-09-01',
  time: '10:00',
  planId: 2,
  roomCount: 3,
  bathroomCount: 2,
  hours: 3,
  extraSupplies: false,
  paymentMethodId: 10,
  couponCode: '50POFF',
});
console.log('Booking ID:', resp.data.id);
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```typescript
await client.bookings.assignCleaner(16926, cleanerId);
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```typescript
await client.bookings.removeCleaner(16926);
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

```typescript
await client.bookings.adjustHours(16926, 4.5);
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

```typescript
await client.bookings.rescheduleBooking(16926, {
  date: '2025-09-15',
  time: '14:00',
});
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

```typescript
await client.bookings.payExpenses(16926, paymentMethodId);
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```typescript
const resp = await client.bookings.getInspection(16926);
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```typescript
const resp = await client.bookings.getInspectionDetails(16926);
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

```typescript
await client.bookings.cancelBooking(16926, 'Scheduling conflict');
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```typescript
await client.bookings.assignChecklistToBooking(16926, 105);
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | number | Yes | 1–5 stars |
| `comment` | string | No | Written feedback |

```typescript
await client.bookings.submitFeedback(16926, { rating: 5, comment: 'Excellent!' });
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

```typescript
await client.bookings.submitTip(16926, { amount: 15.00, paymentMethodId: 10 });
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

```typescript
const resp = await client.bookings.getChat(16926);
for (const msg of resp.data.messages) {
  console.log(`[${msg.sender_type}] ${msg.content}`);
}
```

**`data.messages[]` fields:**

| Field | Type | Description |
|---|---|---|
| `message_id` | string | Unique message ID |
| `sender_id` | string | Sender key (e.g. `C6`, `P3`) |
| `content` | string | Text content |
| `timestamp` | string | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `message_type` | string | `text` or `media` |
| `attachments` | array | Media items |
| `attachments[].type` | string | `image`, `video`, `sound` |
| `attachments[].url` | string | Media URL |
| `attachments[].thumb_url` | string | Thumbnail URL (nullable) |
| `is_read` | boolean | Read status |
| `sender_type` | string | `client` · `cleaner` · `support` · `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

```typescript
await client.bookings.sendMessage(16926, 'Please bring extra supplies.');
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```typescript
await client.bookings.deleteMessage(16926, '-OLPrlE06uD8tQ8ebJZw');
```

---

### Users

#### Create User
**`POST /v1/user/account`**

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | string | Yes | Email address |
| `firstName` | string | Yes | First name |
| `lastName` | string | Yes | Last name |
| `phone` | string | Yes | E.164 format |

```typescript
const resp = await client.users.createUser({
  email: 'jane@example.com',
  firstName: 'Jane',
  lastName: 'Doe',
  phone: '+15551234567',
});
const userId: number = resp.data.userId;
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

```typescript
const resp = await client.users.fetchAccessToken(42);
const token: string = resp.data.token;
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

```typescript
const resp = await client.users.verifyJwt(userToken);
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```typescript
const resp = await client.properties.listProperties(1);
```

---

#### Create Property
**`POST /v1/properties`**

```typescript
const resp = await client.properties.createProperty({
  address: '123 Main St',
  city: 'Chicago',
  state: 'IL',
  zip: '60601',
  serviceId: 1,
});
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```typescript
const resp = await client.properties.getProperty(1004);
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```typescript
await client.properties.updateProperty(1004, { address: '456 Elm St' });
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```typescript
await client.properties.updateAdditionalInfo(1004, {
  gateCode: '1234',
  petInfo: 'One friendly dog',
});
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```typescript
await client.properties.enableOrDisable(1004, true);
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```typescript
await client.properties.deleteProperty(1004);
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```typescript
const resp = await client.properties.getIcal(1004);
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```typescript
await client.properties.addIcal(1004, 'https://airbnb.com/calendar/ical/12345.ics');
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```typescript
await client.properties.deleteIcal(1004, [101, 102, 103]);
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```typescript
const resp = await client.properties.listCleaners(1004);
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```typescript
await client.properties.addCleaner(1004, cleanerId);
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```typescript
await client.properties.removeCleaner(1004, cleanerId);
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

```typescript
await client.properties.setDefaultChecklist(1004, 105, true);
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```typescript
const resp = await client.checklists.listChecklists();
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```typescript
const resp = await client.checklists.getChecklist(105);
resp.data.items.forEach(item => console.log(item.task));
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | string | Yes | Display name |
| `items` | string[] | Yes | Ordered task descriptions |

```typescript
const resp = await client.checklists.createChecklist({
  name: 'Deep Clean',
  items: [
    'Vacuum all rooms',
    'Mop kitchen and bathroom floors',
    'Scrub toilets, sinks, and tubs',
    'Wipe all countertops',
    'Clean inside microwave and oven',
  ],
});
console.log('Checklist ID:', resp.data.id);
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```typescript
await client.checklists.updateChecklist(105, {
  name: 'Standard Clean',
  items: ['Vacuum', 'Wipe surfaces', 'Clean bathrooms'],
});
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```typescript
await client.checklists.deleteChecklist(105);
```

---

#### Upload Checklist Image
**`POST /v1/checklist/{checklistId}/upload`**

Upload an image for a checklist. The image is sent as `multipart/form-data` in the `image` form field.

```typescript
const imageData = fs.readFileSync('bathroom-guide.jpg');
await client.checklists.uploadChecklistImage(imageData, 'bathroom-guide.jpg');
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```typescript
const resp = await client.other.getServices();
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```typescript
const resp = await client.other.getPlans(1004);
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```typescript
const resp = await client.other.getCleaningExtras(1);
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```typescript
const resp = await client.other.getRecommendedHours({
  propertyId: 1004,
  bathroomCount: 2,
  roomCount: 3,
});
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

```typescript
const resp = await client.other.getCostEstimate(estimateRequest);
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```typescript
const resp = await client.other.getAvailableCleaners(availabilityRequest);
```

---

#### Get Coupons
**`GET /v1/coupons`**

```typescript
const resp = await client.other.getCoupons();
```

#### List Cleaners
**`GET /v1/cleaners`**

```typescript
const resp = await client.other.listCleaners({ status: "active", search: "Jane" });
```

#### Get Cleaner
**`GET /v1/cleaners/{cleanerId}`**

```typescript
const resp = await client.other.getCleaner(789);
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```typescript
const resp = await client.blacklist.getBlacklist(1);
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```typescript
await client.blacklist.addToBlacklist(cleanerId);
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```typescript
await client.blacklist.removeFromBlacklist(cleanerId);
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```typescript
const resp = await client.paymentMethods.getSetupIntentDetails();
const clientSecret = resp.data.clientSecret;
// Use clientSecret with Stripe.js confirmCardSetup
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```typescript
const resp = await client.paymentMethods.getPayPalClientToken();
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```typescript
await client.paymentMethods.addPaymentMethod(paymentRequest);
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```typescript
const resp = await client.paymentMethods.listPaymentMethods();
resp.data.forEach(pm => console.log(pm.type, pm.last4));
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```typescript
await client.paymentMethods.deletePaymentMethod(193);
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```typescript
await client.paymentMethods.setDefault(193);
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```typescript
const resp = await client.webhooks.listWebhooks();
```

---

#### Create Webhook
**`POST /v1/webhooks`**

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | string | Yes | HTTPS endpoint |
| `event` | string | Yes | Event type |

```typescript
await client.webhooks.createWebhook({
  url: 'https://your-server.com/hooks/cleanster',
  event: 'booking.status_changed',
});
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```typescript
await client.webhooks.updateWebhook(50, {
  url: 'https://your-server.com/hooks/cleanster-v2',
  event: 'booking.completed',
});
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```typescript
await client.webhooks.deleteWebhook(50);
```

---

## Type Reference

### `ApiResponse<T>`

```typescript
interface ApiResponse<T> {
  status: number;   // HTTP status code
  message: string;  // Result description
  data: T;          // Typed payload
}
```

### `Booking`

```typescript
interface Booking {
  id: number;
  status: 'OPEN' | 'CLEANER_ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'REMOVED';
  date: string;           // YYYY-MM-DD
  time: string;           // HH:MM
  hours: number;
  cost: number;
  propertyId: number;
  planId: number;
  roomCount: number;
  bathroomCount: number;
  extraSupplies: boolean;
  paymentMethodId: number;
  cleaner?: Cleaner;
}
```

### `Cleaner`

```typescript
interface Cleaner {
  id: number;
  name: string;
  email: string;
  phone: string;
  profileUrl: string;
  rating: number; // 1.0 – 5.0
}
```

### `Checklist`

```typescript
interface Checklist {
  id: number;
  name: string;
  items: ChecklistItem[];
}

interface ChecklistItem {
  id: number;
  task: string;
  order: number;
  isCompleted: boolean;
}
```

### `PaymentMethod`

```typescript
interface PaymentMethod {
  id: number;
  type: 'card' | 'paypal';
  last4?: string;
  brand?: string;
  expiryMonth?: number;
  expiryYear?: number;
  isDefault: boolean;
}
```

---

## Error Handling

```typescript
import { CleansterApiError } from 'cleanster';

try {
  const resp = await client.bookings.getBooking(99999);
} catch (err) {
  if (err instanceof CleansterApiError) {
    console.error(`HTTP ${err.statusCode}: ${err.message}`);
    if (err.statusCode === 401) {
      // Re-fetch user token and retry
    }
  }
}
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request — malformed parameters |
| 401 | Unauthorized — invalid or missing credentials |
| 403 | Forbidden — insufficient permissions |
| 404 | Not found |
| 422 | Validation error |
| 429 | Rate limit exceeded |
| 500 | Internal server error |

---

## Test Coupon Codes

Use in the **sandbox** environment only:

| Code | Discount | Status |
|---|---|---|
| `100POFF` | 100% off | Active |
| `50POFF` | 50% off | Active |
| `20POFF` | 20% off | Active |
| `200OFF` | $200 off | Active |
| `100OFF` | $100 off | Active |
| `75POFF` | 75% off | **Expired** |

---

## Chat Window Rules

| Booking State | Chat Available |
|---|---|
| `OPEN` — within 24 h of start | Yes |
| `COMPLETED` — within 24 h of completion | Yes |
| `IN_PROGRESS` (hanging state) | Yes — **no time restriction** |
| `CANCELLED` | No |
| Older than 24 h | No |

---

## Webhook Events

| Event | Description |
|---|---|
| `booking.status_changed` | Any status transition |
| `booking.cleaner_assigned` | Cleaner assigned |
| `booking.cancelled` | Booking cancelled |
| `booking.completed` | Booking completed |

---

## Running Tests

```bash
npm test
```

Expected: **89 tests passing.**

---

## Project Structure

```
typescript-sdk/
├── package.json
├── tsconfig.json
├── src/
│   ├── index.ts               # Public exports
│   ├── client.ts              # CleansterClient
│   ├── http.ts                # fetch-based HTTP layer
│   ├── errors.ts              # CleansterApiError
│   ├── api/
│   │   ├── bookings.ts
│   │   ├── users.ts
│   │   ├── properties.ts
│   │   ├── checklists.ts
│   │   ├── other.ts
│   │   ├── blacklist.ts
│   │   ├── payment-methods.ts
│   │   └── webhooks.ts
│   └── models/
│       ├── booking.ts
│       ├── checklist.ts
│       └── payment-method.ts
└── tests/
    └── cleanster.test.ts
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
