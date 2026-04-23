# Cleanster PHP SDK

<p align="center">
  <strong>Official PHP client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/PHP-8.1%2B-777BB4?logo=php" alt="PHP 8.1+">
  <img src="https://img.shields.io/badge/Composer-PSR--4-blue?logo=packagist" alt="PSR-4">
  <img src="https://img.shields.io/badge/tests-106%20passing-brightgreen" alt="106 passing">
  <img src="https://img.shields.io/badge/dependencies-zero%20runtime-brightgreen" alt="Zero runtime dependencies">
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

The Cleanster PHP SDK provides a clean, idiomatic PHP interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It requires only PHP's built-in `ext-curl` and `ext-json` extensions — zero Composer runtime dependencies.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal calendar sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create task lists and assign to bookings
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — prevent specific cleaners from being assigned

---

## Requirements

- **PHP 8.1** or later
- Extensions: `ext-curl`, `ext-json`
- **Composer** (for installation)
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

```bash
composer require cleanster/cleanster-php-sdk
```

Install from source:

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/php-sdk
composer install
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, from `$client->users()->fetchAccessToken($userId)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```php
use Cleanster\CleansterClient;

$client = new CleansterClient('your-access-key');

$resp = $client->users()->createUser(
    'jane@example.com',  // email
    'Jane',              // first name
    'Doe',               // last name
    '+15551234567'       // phone
);
$userId = $resp->getData()['userId'];
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```php
$tokenResp = $client->users()->fetchAccessToken($userId);
$userToken = $tokenResp->getData()['token'];
```

**Step 4 — Build the client with both credentials**:

```php
$client = new CleansterClient('your-access-key', $userToken);
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401.

---

## Quick Start

```php
use Cleanster\CleansterClient;

$client = new CleansterClient('your-access-key', 'user-jwt-token');

// Get recommended cleaning hours
$hours = $client->other()->getRecommendedHours(1004, 2, 3);
echo "Recommended hours: " . json_encode($hours->getData()) . "\n";

// Create a booking
$booking = $client->bookings()->createBooking([
    'propertyId'      => 1004,
    'date'            => '2025-09-01',
    'time'            => '10:00',
    'planId'          => 2,
    'roomCount'       => 3,
    'bathroomCount'   => 2,
    'hours'           => 3.0,
    'extraSupplies'   => false,
    'paymentMethodId' => 10,
    'couponCode'      => '20POFF',  // optional — 20% off in sandbox
]);
echo "Created booking: " . $booking->getData()['id'] . "\n";

// List open bookings
$list = $client->bookings()->listBookings(1, 'OPEN');
echo "Open bookings: " . count($list->getData()['bookings']) . "\n";
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```php
// Sandbox (default)
$client = new CleansterClient('key', 'token');

// Production
$client = new CleansterClient('key', 'token', 'production');
```

---

## Booking Flow

```
createBooking()          →   OPEN
                                 │
     bookings()->assignCleaner()
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

All methods return a `Cleanster\ApiResponse` with:
- `->getStatus()` — HTTP status code
- `->getMessage()` — Human-readable result
- `->getData()` — Response payload (array)

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `$pageNo` | int | Yes | Page number (1-based) |
| `$status` | string | No | `OPEN` · `CLEANER_ASSIGNED` · `COMPLETED` · `CANCELLED` · `REMOVED` |

```php
$resp = $client->bookings()->listBookings(1, 'OPEN');
foreach ($resp->getData()['bookings'] as $booking) {
    echo $booking['id'] . ' - ' . $booking['status'] . "\n";
}
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

```php
$resp = $client->bookings()->getBooking(16926);
echo $resp->getData()['status'] . ' on ' . $resp->getData()['date'] . "\n";
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `propertyId` | int | Yes | Property to clean |
| `date` | string | Yes | `YYYY-MM-DD` |
| `time` | string | Yes | `HH:MM` (24-hour) |
| `planId` | int | Yes | Cleaning plan ID |
| `roomCount` | int | Yes | Number of rooms |
| `bathroomCount` | int | Yes | Number of bathrooms |
| `hours` | float | Yes | Duration |
| `extraSupplies` | bool | Yes | Cleaner brings supplies |
| `paymentMethodId` | int | Yes | Payment method ID |
| `couponCode` | string | No | Discount coupon |
| `cleaningExtras` | array | No | Extra service IDs |

```php
$resp = $client->bookings()->createBooking([
    'propertyId'      => 1004,
    'date'            => '2025-09-01',
    'time'            => '10:00',
    'planId'          => 2,
    'roomCount'       => 3,
    'bathroomCount'   => 2,
    'hours'           => 3.0,
    'extraSupplies'   => false,
    'paymentMethodId' => 10,
    'couponCode'      => '50POFF',
]);
echo "Booking ID: " . $resp->getData()['id'] . "\n";
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```php
$client->bookings()->assignCleaner(16926, $cleanerId);
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```php
$client->bookings()->removeCleaner(16926);
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

```php
$client->bookings()->adjustHours(16926, 4.5);
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

```php
$client->bookings()->rescheduleBooking(16926, '2025-09-15', '14:00');
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

```php
$client->bookings()->payExpenses(16926, $paymentMethodId);
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```php
$resp = $client->bookings()->getInspection(16926);
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```php
$resp = $client->bookings()->getInspectionDetails(16926);
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

```php
$client->bookings()->cancelBooking(16926, 'Scheduling conflict');
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```php
$client->bookings()->assignChecklistToBooking(16926, 105);
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

```php
$client->bookings()->submitFeedback(16926, 5, 'Excellent work!');
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

```php
$client->bookings()->submitTip(16926, 15.00, $paymentMethodId);
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

```php
$resp = $client->bookings()->getChat(16926);
foreach ($resp->getData()['messages'] as $msg) {
    echo "[{$msg['sender_type']}] {$msg['content']}\n";
}
```

**`messages[]` fields:**

| Field | Type | Description |
|---|---|---|
| `message_id` | string | Unique ID |
| `sender_id` | string | Reference key (e.g. `C6`, `P3`) |
| `content` | string | Text content (empty for media) |
| `timestamp` | string | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `message_type` | string | `text` or `media` |
| `attachments` | array | Media items |
| `attachments[].type` | string | `image`, `video`, `sound` |
| `attachments[].url` | string | Media URL |
| `attachments[].thumb_url` | string | Thumbnail (nullable) |
| `is_read` | bool | Read status |
| `sender_type` | string | `client` · `cleaner` · `support` · `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

```php
$client->bookings()->sendMessage(16926, 'Please bring extra supplies.');
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```php
$client->bookings()->deleteMessage(16926, '-OLPrlE06uD8tQ8ebJZw');
```

---

### Users

#### Create User
**`POST /v1/user/account`**

```php
$resp = $client->users()->createUser(
    'jane@example.com', 'Jane', 'Doe', '+15551234567'
);
$userId = $resp->getData()['userId'];
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Only `access-key` is required for this call.

```php
$resp = $client->users()->fetchAccessToken(42);
$token = $resp->getData()['token'];
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

```php
$resp = $client->users()->verifyJwt($userToken);
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```php
$resp = $client->properties()->listProperties(1);
```

---

#### Create Property
**`POST /v1/properties`**

```php
$resp = $client->properties()->createProperty([
    'address'   => '123 Main St',
    'city'      => 'Chicago',
    'state'     => 'IL',
    'zip'       => '60601',
    'serviceId' => 1,
]);
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```php
$resp = $client->properties()->getProperty(1004);
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```php
$client->properties()->updateProperty(1004, ['address' => '456 Elm St']);
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```php
$client->properties()->updateAdditionalInfo(1004, [
    'gateCode' => '1234',
    'petInfo'  => 'One friendly dog',
]);
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```php
$client->properties()->enableOrDisable(1004, true);
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```php
$client->properties()->deleteProperty(1004);
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```php
$resp = $client->properties()->getIcal(1004);
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```php
$client->properties()->addIcal(
    1004, 'https://www.airbnb.com/calendar/ical/12345.ics'
);
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```php
$client->properties()->deleteIcal(1004, [101, 102, 103]);
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```php
$resp = $client->properties()->listCleaners(1004);
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```php
$client->properties()->addCleaner(1004, $cleanerId);
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```php
$client->properties()->removeCleaner(1004, $cleanerId);
```

---

#### Set Default Checklist
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

```php
$client->properties()->setDefaultChecklist(1004, 105, true);
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```php
$resp = $client->checklists()->listChecklists();
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```php
$resp = $client->checklists()->getChecklist(105);
foreach ($resp->getData()['items'] as $item) {
    echo $item['task'] . "\n";
}
```

---

#### Create Checklist
**`POST /v1/checklist`**

```php
$resp = $client->checklists()->createChecklist('Deep Clean', [
    'Vacuum all rooms',
    'Mop kitchen and bathroom floors',
    'Scrub toilets, sinks, and tubs',
    'Wipe all countertops',
    'Clean inside microwave and oven',
]);
echo "Checklist ID: " . $resp->getData()['id'] . "\n";
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```php
$client->checklists()->updateChecklist(
    105,
    'Standard Clean',
    ['Vacuum', 'Wipe surfaces', 'Clean bathrooms']
);
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```php
$client->checklists()->deleteChecklist(105);
```

---

#### Upload Checklist Image
**`POST /v1/checklist/upload-image`**

```php
$imageData = file_get_contents('bathroom-guide.jpg');
$client->checklists()->uploadImage($imageData, 'image/jpeg');
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```php
$resp = $client->other()->getServices();
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```php
$resp = $client->other()->getPlans(1004);
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```php
$resp = $client->other()->getCleaningExtras(1);
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```php
$resp = $client->other()->getRecommendedHours(1004, 2, 3);
echo "Recommended hours: " . $resp->getData()['hours'] . "\n";
```

---

#### Get Cost Estimate
**`POST /v1/cost-estimate`**

```php
$resp = $client->other()->getCostEstimate($estimateRequest);
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```php
$resp = $client->other()->getAvailableCleaners($availabilityRequest);
```

---

#### Get Coupons
**`GET /v1/coupons`**

```php
$resp = $client->other()->getCoupons();
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```php
$resp = $client->blacklist()->getBlacklist(1);
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```php
$client->blacklist()->addToBlacklist($cleanerId);
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```php
$client->blacklist()->removeFromBlacklist($cleanerId);
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```php
$resp = $client->paymentMethods()->getSetupIntentDetails();
$clientSecret = $resp->getData()['clientSecret'];
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```php
$resp = $client->paymentMethods()->getPayPalClientToken();
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```php
$resp = $client->paymentMethods()->addPaymentMethod($paymentRequest);
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```php
$resp = $client->paymentMethods()->listPaymentMethods();
foreach ($resp->getData() as $pm) {
    echo $pm['type'] . ' ' . ($pm['last4'] ?? '') . "\n";
}
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```php
$client->paymentMethods()->deletePaymentMethod(193);
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```php
$client->paymentMethods()->setDefault(193);
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```php
$resp = $client->webhooks()->listWebhooks();
```

---

#### Create Webhook
**`POST /v1/webhooks`**

```php
$client->webhooks()->createWebhook(
    'https://your-server.com/hooks/cleanster',
    'booking.status_changed'
);
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```php
$client->webhooks()->updateWebhook(
    50,
    'https://your-server.com/hooks/cleanster-v2',
    'booking.completed'
);
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```php
$client->webhooks()->deleteWebhook(50);
```

---

## Models Reference

### `ApiResponse`

| Method | Type | Description |
|---|---|---|
| `getStatus()` | int | HTTP status code |
| `getMessage()` | string | Result description |
| `getData()` | array | Response payload |

### Booking (array keys)

| Key | Type | Description |
|---|---|---|
| `id` | int | Unique booking ID |
| `status` | string | `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED` |
| `date` | string | `YYYY-MM-DD` |
| `time` | string | `HH:MM` |
| `hours` | float | Duration |
| `cost` | float | Total cost USD |
| `propertyId` | int | Property ID |
| `planId` | int | Plan ID |
| `roomCount` | int | Rooms |
| `bathroomCount` | int | Bathrooms |
| `extraSupplies` | bool | Supplies included |
| `paymentMethodId` | int | Payment method charged |
| `cleaner` | array\|null | Assigned cleaner |

### Cleaner (array keys)

| Key | Type | Description |
|---|---|---|
| `id` | int | Cleaner ID |
| `name` | string | Full name |
| `email` | string | Email |
| `phone` | string | Phone |
| `profileUrl` | string | Profile picture |
| `rating` | float | Average rating (1–5) |

### Checklist (array keys)

| Key | Type | Description |
|---|---|---|
| `id` | int | Checklist ID |
| `name` | string | Display name |
| `items` | array | Task items |
| `items[].id` | int | Task ID |
| `items[].task` | string | Task description |
| `items[].order` | int | Sort order |
| `items[].isCompleted` | bool | Completion status |

### PaymentMethod (array keys)

| Key | Type | Description |
|---|---|---|
| `id` | int | ID |
| `type` | string | `card` or `paypal` |
| `last4` | string | Last 4 digits |
| `brand` | string | Card brand |
| `isDefault` | bool | Default method |

---

## Error Handling

```php
use Cleanster\Exceptions\CleansterApiException;

try {
    $resp = $client->bookings()->getBooking(99999);
} catch (CleansterApiException $e) {
    echo "HTTP " . $e->getCode() . ": " . $e->getMessage() . "\n";
    if ($e->getCode() === 401) {
        // Re-fetch user token and retry
    } elseif ($e->getCode() === 404) {
        echo "Booking not found\n";
    } elseif ($e->getCode() === 422) {
        echo "Validation error: " . $e->getMessage() . "\n";
    }
}
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request |
| 401 | Unauthorized — invalid credentials |
| 403 | Forbidden |
| 404 | Not found |
| 422 | Validation error |
| 429 | Rate limit exceeded |
| 500 | Server error |

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
composer install
./vendor/bin/phpunit tests/
```

Expected: **106 tests, 0 failures.**

---

## Project Structure

```
php-sdk/
├── composer.json
├── src/
│   ├── CleansterClient.php          # Main entry point
│   ├── ApiResponse.php
│   ├── HttpClient.php               # cURL wrapper
│   ├── Exceptions/
│   │   └── CleansterApiException.php
│   └── Api/
│       ├── BookingsApi.php
│       ├── UsersApi.php
│       ├── PropertiesApi.php
│       ├── ChecklistsApi.php
│       ├── OtherApi.php
│       ├── BlacklistApi.php
│       ├── PaymentMethodsApi.php
│       └── WebhooksApi.php
└── tests/
    └── CleansterSdkTest.php
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
