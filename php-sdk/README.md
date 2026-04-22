# Cleanster PHP SDK

<p align="center">
  <strong>Official PHP client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/PHP-8.1%2B-777BB4?logo=php" alt="PHP 8.1+">
  <img src="https://img.shields.io/badge/Composer-PSR--4-blue?logo=packagist" alt="Composer PSR-4">
  <img src="https://img.shields.io/badge/tests-106%20passing-brightgreen" alt="106 passing tests">
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
- [Configuration](#configuration)
- [Error Handling](#error-handling)
- [API Reference](#api-reference)
  - [Bookings](#bookings-clientbookings)
  - [Users](#users-clientusers)
  - [Properties](#properties-clientproperties)
  - [Checklists](#checklists-clientchecklists)
  - [Other / Utilities](#other--utilities-clientother)
  - [Blacklist](#blacklist-clientblacklist)
  - [Payment Methods](#payment-methods-clientpaymentmethods)
  - [Webhooks](#webhooks-clientwebhooks)
- [Response Structure](#response-structure)
- [Model Reference](#model-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster PHP SDK provides a clean, idiomatic PHP interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It targets PHP 8.1+ and uses only PHP's built-in `ext-curl` and `ext-json` extensions — zero Composer runtime dependencies.

**Feature highlights:**

| Feature | Detail |
|---------|--------|
| **Idiomatic PHP 8.1+** | Constructor promotion, readonly properties, named arguments, `null`-safe operators |
| **Zero runtime dependencies** | Only `ext-curl` and `ext-json` — universally available on every PHP host |
| **Typed model objects** | `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod` — never raw arrays |
| **PSR-4 autoloading** | Standard Composer autoloading — works with any PSR-4-compliant framework |
| **`ApiResponse` wrapper** | Consistent `$status`, `$message`, `$data` on every call |
| **Injectable `HttpClient`** | Pass a mock `HttpClient` for unit testing — no real network required |
| **Three exception types** | `CleansterException`, `AuthException` (401), `ApiException` (4xx/5xx) |
| **8 service classes** | Bookings, Users, Properties, Checklists, Other, Blacklist, PaymentMethods, Webhooks |
| **106 PHPUnit tests** | All passing; uses mocks — no API key or network access needed |

---

## Requirements

| Requirement | Minimum Version |
|-------------|----------------|
| PHP | 8.1 |
| ext-curl | Any (bundled with PHP) |
| ext-json | Any (bundled with PHP) |
| Composer | 2.x (dev install only) |

> **No Guzzle or other HTTP library is required.** The SDK uses PHP's native cURL extension, which is available in every standard PHP distribution.

---

## Installation

```bash
composer require cleanster/cleanster-php-sdk
```

Then use the SDK anywhere in your project:

```php
use Cleanster\CleansterClient;
```

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

The SDK sends both headers automatically. You set the partner key once at client creation, then call `setAccessToken()` after fetching a user's token.

### Step-by-Step Authentication

**Step 1 — Create a sandbox client with your partner access key:**

```php
use Cleanster\CleansterClient;

$client = CleansterClient::sandbox($_ENV['CLEANSTER_API_KEY']);
```

**Step 2 — Create a user:**

```php
$resp = $client->users()->createUser(
    email:     'jane@example.com',
    firstName: 'Jane',
    lastName:  'Smith',
);

$user = $resp->data;   // Cleanster\Models\User
echo "Created user #{$user->id}\n";
```

**Step 3 — Fetch the user's long-lived bearer token:**

```php
$tokenResp = $client->users()->fetchAccessToken($user->id);
$token     = $tokenResp->data->token;   // string
```

**Step 4 — Set the token on the client** for all subsequent calls:

```php
$client->setAccessToken($token);
// Every subsequent request automatically sends: token: <your-token>
```

> **Tip:** The bearer token is long-lived. Store it in your database and reuse it across requests by calling `$client->setAccessToken($storedToken)` — no need to re-fetch it each time.

---

## Quick Start

```php
<?php

require 'vendor/autoload.php';

use Cleanster\CleansterClient;

$client = CleansterClient::sandbox($_ENV['CLEANSTER_API_KEY']);

// 1. Create a user
$userResp = $client->users()->createUser('jane@example.com', 'Jane', 'Smith');
$user     = $userResp->data;

// 2. Fetch and set the bearer token
$tokenResp = $client->users()->fetchAccessToken($user->id);
$client->setAccessToken($tokenResp->data->token);

// 3. Add a property
$propResp = $client->properties()->addProperty([
    'name'          => 'Beach House',
    'address'       => '123 Ocean Drive',
    'city'          => 'Miami',
    'country'       => 'USA',
    'roomCount'     => 3,
    'bathroomCount' => 2,
    'serviceId'     => 1,
]);
$prop = $propResp->data;   // Cleanster\Models\Property

// 4. Get recommended hours for this property size
$hoursResp = $client->other()->getRecommendedHours(
    propertyId:    $prop->id,
    bathroomCount: $prop->bathroomCount,
    roomCount:     $prop->roomCount,
);

// 5. Calculate the estimated cost
$costResp = $client->other()->calculateCost([
    'propertyId' => $prop->id,
    'planId'     => 2,
    'hours'      => 3,
    'couponCode' => '20POFF',   // optional sandbox coupon
]);

// 6. Create a booking
$bookingResp = $client->bookings()->createBooking([
    'date'            => '2025-06-15',
    'time'            => '10:00',
    'propertyId'      => $prop->id,
    'roomCount'       => 3,
    'bathroomCount'   => 2,
    'planId'          => 2,
    'hours'           => 3,
    'extraSupplies'   => false,
    'paymentMethodId' => 10,
]);

$booking = $bookingResp->data;   // Cleanster\Models\Booking
echo "Created booking #{$booking->id} — status: {$booking->status}\n";

// 7. List bookings
$listResp = $client->bookings()->getBookings();
echo "Total bookings: " . count($listResp->data) . "\n";
```

---

## Configuration

### Factory Methods (Recommended)

```php
use Cleanster\CleansterClient;

// Sandbox — development and testing (no real charges or cleaners)
$client = CleansterClient::sandbox('your-access-key');

// Production — live traffic (real cleaners, real charges)
$client = CleansterClient::production('your-access-key');
```

### Custom Config

For custom timeouts or non-standard base URLs:

```php
use Cleanster\Config;
use Cleanster\CleansterClient;

$config = new Config(
    accessKey: 'your-access-key',
    baseUrl:   Config::SANDBOX_BASE_URL,   // or PRODUCTION_BASE_URL or a custom URL
    timeout:   60,                         // seconds (default: 30)
);
$client = new CleansterClient($config);
```

### Config Helpers

```php
use Cleanster\Config;

$cfg = Config::sandbox('your-access-key');
// $cfg->accessKey → 'your-access-key'
// $cfg->baseUrl   → Config::SANDBOX_BASE_URL
// $cfg->timeout   → 30

$cfg = Config::production('your-access-key');
```

### Environment Base URLs

| Environment | Constant | Base URL |
|-------------|----------|----------|
| Sandbox | `Config::SANDBOX_BASE_URL` | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `Config::PRODUCTION_BASE_URL` | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## Error Handling

All SDK methods throw typed exceptions that extend `CleansterException`. Use a `try/catch` block — either catch `CleansterException` as a single catchall, or catch the specific subtypes for fine-grained control.

### Complete Error Handling Example

```php
use Cleanster\Exceptions\AuthException;
use Cleanster\Exceptions\ApiException;
use Cleanster\Exceptions\CleansterException;

try {
    $resp = $client->bookings()->getBookingDetails(99999);
    $booking = $resp->data;

} catch (AuthException $e) {
    // HTTP 401 — invalid access key or expired user token
    echo "Auth error ({$e->statusCode}): {$e->getMessage()}\n";
    echo "Raw response: {$e->responseBody}\n";
    // Prompt user to re-authenticate

} catch (ApiException $e) {
    // HTTP 4xx/5xx — API-level error
    echo "API error ({$e->statusCode}): {$e->getMessage()}\n";
    echo "Raw response: {$e->responseBody}\n";

    match ($e->statusCode) {
        404 => print("Resource not found.\n"),
        422 => print("Validation error — check your request fields.\n"),
        default => print("Server error — consider retrying after a delay.\n"),
    };

} catch (CleansterException $e) {
    // Network failure, cURL error, timeout, or JSON parse error
    echo "SDK error: {$e->getMessage()}\n";
}
```

### Exception Hierarchy

```
\RuntimeException
└── Cleanster\Exceptions\CleansterException    ← base: network failure, cURL error, JSON parse
    ├── Cleanster\Exceptions\AuthException     ← HTTP 401: invalid or missing credentials
    └── Cleanster\Exceptions\ApiException      ← HTTP 4xx/5xx (other than 401)
```

| Exception Class | When Thrown | Key Properties |
|----------------|-------------|----------------|
| `CleansterException` | cURL failure, timeout, JSON parse error | `getMessage()` |
| `AuthException` | HTTP 401 | `$statusCode`, `$responseBody`, `getMessage()` |
| `ApiException` | HTTP 4xx/5xx (not 401) | `$statusCode`, `$responseBody`, `getMessage()` |

---

## API Reference

Every method returns an `ApiResponse` object with `$status`, `$message`, and `$data` readonly properties.
All API classes are accessible via the `$client->serviceName()` accessors.

---

### Bookings (`$client->bookings()`)

#### `getBookings(?int $pageNo, ?string $status): ApiResponse`

Retrieve a paginated list of bookings. Both parameters are optional.

```php
// All bookings (no filter)
$resp = $client->bookings()->getBookings();

// Filtered by status
$resp = $client->bookings()->getBookings(status: 'OPEN');

// Paginated
$resp = $client->bookings()->getBookings(pageNo: 2, status: 'COMPLETED');

// Iterate results
foreach ($resp->data as $booking) {   // $booking is a Booking object
    echo "#{$booking->id} on {$booking->date} at {$booking->time} — {$booking->status}\n";
}
```

**Status values:** `OPEN` | `CLEANER_ASSIGNED` | `COMPLETED` | `CANCELLED` | `REMOVED`

---

#### `createBooking(array $request): ApiResponse`

Schedule a new cleaning appointment.

```php
$resp = $client->bookings()->createBooking([
    'date'            => '2025-06-15',   // Required — YYYY-MM-DD
    'time'            => '10:00',        // Required — HH:mm (24-hour)
    'propertyId'      => 1004,           // Required — from listProperties()
    'roomCount'       => 2,              // Required
    'bathroomCount'   => 1,              // Required
    'planId'          => 5,              // Required — from getPlans()
    'hours'           => 3,              // Required — from getRecommendedHours()
    'extraSupplies'   => false,          // Required — include cleaning supplies?
    'paymentMethodId' => 10,             // Required
    'couponCode'      => '20POFF',       // Optional
    'extras'          => [101, 102],     // Optional — add-on service IDs
]);

$booking = $resp->data;   // Cleanster\Models\Booking
echo "Booking #{$booking->id} cost: \${$booking->cost}\n";
```

---

#### `getBookingDetails(int $bookingId): ApiResponse`

```php
$resp = $client->bookings()->getBookingDetails(16926);
$b    = $resp->data;   // Cleanster\Models\Booking

echo "#{$b->id} on {$b->date} at {$b->time}\n";
echo "Status: {$b->status} — Cost: \${$b->cost}\n";

if ($b->cleanerId !== null) {
    echo "Assigned cleaner: #{$b->cleanerId}\n";
}
```

---

#### `cancelBooking(int $bookingId, ?string $reason): ApiResponse`

```php
// With a reason
$client->bookings()->cancelBooking(16459, 'Schedule conflict');

// Without a reason
$client->bookings()->cancelBooking(16459);
```

---

#### `rescheduleBooking(int $bookingId, string $date, string $time): ApiResponse`

```php
$client->bookings()->rescheduleBooking(16459, '2025-07-01', '14:00');
```

---

#### `assignCleaner(int $bookingId, int $cleanerId)` / `removeAssignedCleaner(int $bookingId)`

```php
$client->bookings()->assignCleaner(16459, 5);
$client->bookings()->removeAssignedCleaner(16459);
```

---

#### `adjustHours(int $bookingId, float $hours)`

```php
$client->bookings()->adjustHours(16459, 4.0);
```

---

#### `payExpenses(int $bookingId, int $paymentMethodId)`

Pay outstanding expenses within 72 hours of booking completion.

```php
$client->bookings()->payExpenses(16926, 10);
```

---

#### `getBookingInspection` / `getBookingInspectionDetails`

```php
$resp = $client->bookings()->getBookingInspection(16926);
$resp = $client->bookings()->getBookingInspectionDetails(16926);
```

---

#### `assignChecklistToBooking(int $bookingId, int $checklistId)`

Override the property's default checklist for this specific booking only.

```php
$client->bookings()->assignChecklistToBooking(16926, 105);
```

---

#### `submitFeedback(int $bookingId, int $rating, ?string $comment)`

Submit a star rating (1–5) and optional comment.

```php
// With a comment
$client->bookings()->submitFeedback(16926, 5, 'Excellent — very thorough!');

// Without a comment
$client->bookings()->submitFeedback(16926, 4);
```

---

#### `addTip(int $bookingId, float $amount, int $paymentMethodId)`

Add a tip within 72 hours of booking completion.

```php
$client->bookings()->addTip(16926, 20.0, 10);
```

---

#### Chat: `getChat`, `sendMessage`, `deleteMessage`

```php
// Get all messages in a booking's chat thread
$chat = $client->bookings()->getChat(17142);

// Send a message
$client->bookings()->sendMessage(17142, 'Please focus on the kitchen today.');

// Delete a specific message
$client->bookings()->deleteMessage(17142, 'msg-abc-123');
```

---

**Bookings API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getBookings(?int, ?string)` | GET | `/v1/bookings` |
| `createBooking(array)` | POST | `/v1/bookings/create` |
| `getBookingDetails(int)` | GET | `/v1/bookings/{id}` |
| `cancelBooking(int, ?string)` | POST | `/v1/bookings/{id}/cancel` |
| `rescheduleBooking(int, string, string)` | POST | `/v1/bookings/{id}/reschedule` |
| `assignCleaner(int, int)` | POST | `/v1/bookings/{id}/cleaner` |
| `removeAssignedCleaner(int)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `adjustHours(int, float)` | POST | `/v1/bookings/{id}/hours` |
| `payExpenses(int, int)` | POST | `/v1/bookings/{id}/expenses` |
| `getBookingInspection(int)` | GET | `/v1/bookings/{id}/inspection` |
| `getBookingInspectionDetails(int)` | GET | `/v1/bookings/{id}/inspection/details` |
| `assignChecklistToBooking(int, int)` | POST | `/v1/bookings/{id}/checklist/{cid}` |
| `submitFeedback(int, int, ?string)` | POST | `/v1/bookings/{id}/feedback` |
| `addTip(int, float, int)` | POST | `/v1/bookings/{id}/tip` |
| `getChat(int)` | GET | `/v1/bookings/{id}/chat` |
| `sendMessage(int, string)` | POST | `/v1/bookings/{id}/chat` |
| `deleteMessage(int, string)` | DELETE | `/v1/bookings/{id}/chat/{messageId}` |

---

### Users (`$client->users()`)

#### `createUser(string $email, string $firstName, string $lastName, ?string $phone): ApiResponse`

```php
$resp = $client->users()->createUser(
    email:     'jane@example.com',
    firstName: 'Jane',
    lastName:  'Smith',
    phone:     '+15551234567',   // optional — omitted from request if null
);

$user = $resp->data;   // Cleanster\Models\User
echo "Created user #{$user->id}: {$user->email}\n";
```

---

#### `fetchAccessToken(int $userId): ApiResponse`

Fetch the long-lived bearer token. Store it and reuse it across sessions.

```php
$resp  = $client->users()->fetchAccessToken(42);
$token = $resp->data->token;   // string

// Set on client for all subsequent requests:
$client->setAccessToken($token);
```

---

#### `verifyJwt(string $token): ApiResponse`

```php
$resp = $client->users()->verifyJwt('eyJhbGci...');
```

---

**Users API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `createUser(string, string, string, ?string)` | POST | `/v1/user/account` |
| `fetchAccessToken(int)` | GET | `/v1/user/access-token/{userId}` |
| `verifyJwt(string)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`$client->properties()`)

#### `listProperties(?int $serviceId): ApiResponse`

Pass `null` to return all service types.

```php
// All properties
$resp = $client->properties()->listProperties();

// Residential only (serviceId = 1)
$resp = $client->properties()->listProperties(1);

foreach ($resp->data as $prop) {   // $prop is a Property object
    echo "#{$prop->id}: {$prop->name} — {$prop->city}\n";
}
```

---

#### `addProperty(array $request): ApiResponse`

```php
$resp = $client->properties()->addProperty([
    'name'          => 'Downtown Condo',
    'address'       => '456 Main St',
    'city'          => 'Toronto',
    'country'       => 'Canada',
    'roomCount'     => 2,
    'bathroomCount' => 1,
    'serviceId'     => 1,
]);

$prop = $resp->data;   // Cleanster\Models\Property
echo "Added property #{$prop->id}: {$prop->name}\n";
```

---

#### CRUD operations

```php
// Get a single property
$resp = $client->properties()->getProperty(1040);

// Full replace (all fields required)
$resp = $client->properties()->updateProperty(1040, [
    'name' => 'Renovated Condo', 'address' => '456 Main St',
    'city' => 'Toronto', 'country' => 'Canada',
    'roomCount' => 3, 'bathroomCount' => 1, 'serviceId' => 1,
]);

// Toggle active state
$client->properties()->enableOrDisableProperty(1040, false);   // disable
$client->properties()->enableOrDisableProperty(1040, true);    // enable

// Permanently delete
$client->properties()->deleteProperty(1040);
```

---

#### Cleaner assignment

```php
// List assigned cleaners
$cleaners = $client->properties()->getPropertyCleaners(1040);

// Assign a cleaner
$client->properties()->assignCleanerToProperty(1040, 5);

// Remove a cleaner
$client->properties()->unassignCleanerFromProperty(1040, 5);
```

---

#### iCal calendar sync

Sync property availability with Airbnb, VRBO, or any iCal-compatible platform.

```php
$feedUrl = 'https://airbnb.com/calendar/ical/xxx.ics';

// Add
$client->properties()->addICalLink(1040, $feedUrl);

// Get current link
$link = $client->properties()->getICalLink(1040);

// Remove
$client->properties()->removeICalLink(1040, $feedUrl);
```

---

#### `assignChecklistToProperty(int $propertyId, int $checklistId, bool $updateUpcomingBookings)`

```php
// Apply and update all future bookings at this property
$client->properties()->assignChecklistToProperty(1040, 105, updateUpcomingBookings: true);

// Apply without touching upcoming bookings
$client->properties()->assignChecklistToProperty(1040, 105, updateUpcomingBookings: false);
```

---

**Properties API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listProperties(?int)` | GET | `/v1/properties` |
| `addProperty(array)` | POST | `/v1/properties` |
| `getProperty(int)` | GET | `/v1/properties/{id}` |
| `updateProperty(int, array)` | PUT | `/v1/properties/{id}` |
| `updateAdditionalInformation(int, array)` | PUT | `/v1/properties/{id}/additional-information` |
| `enableOrDisableProperty(int, bool)` | POST | `/v1/properties/{id}/enable-disable` |
| `deleteProperty(int)` | DELETE | `/v1/properties/{id}` |
| `getPropertyCleaners(int)` | GET | `/v1/properties/{id}/cleaners` |
| `assignCleanerToProperty(int, int)` | POST | `/v1/properties/{id}/cleaners` |
| `unassignCleanerFromProperty(int, int)` | DELETE | `/v1/properties/{id}/cleaners/{cid}` |
| `addICalLink(int, string)` | PUT | `/v1/properties/{id}/ical` |
| `getICalLink(int)` | GET | `/v1/properties/{id}/ical` |
| `removeICalLink(int, string)` | DELETE | `/v1/properties/{id}/ical` |
| `assignChecklistToProperty(int, int, bool)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`$client->checklists()`)

Checklists define the tasks a cleaner must complete during a booking.

#### `listChecklists(): ApiResponse`

```php
$resp = $client->checklists()->listChecklists();
foreach ($resp->data as $cl) {   // $cl is a Checklist object
    $count = count($cl->items);
    echo "#{$cl->id}: {$cl->name} ({$count} items)\n";
}
```

---

#### `getChecklist(int $checklistId): ApiResponse`

```php
$resp = $client->checklists()->getChecklist(105);
$cl   = $resp->data;   // Cleanster\Models\Checklist

echo "Checklist: {$cl->name}\n";
foreach ($cl->items as $item) {   // $item is a ChecklistItem object
    $mark = $item->isCompleted ? '✓' : ' ';
    echo "[{$mark}] {$item->description}\n";
    if ($item->imageUrl !== null) {
        echo "    Proof photo: {$item->imageUrl}\n";
    }
}
```

---

#### `createChecklist(string $name, array $items): ApiResponse`

```php
$resp = $client->checklists()->createChecklist(
    name:  'Standard Residential Clean',
    items: [
        'Vacuum all floors',
        'Mop kitchen and bathroom floors',
        'Wipe all countertops',
        'Scrub toilets, sinks, and tubs',
        'Empty all trash bins',
    ],
);
echo "Created checklist #{$resp->data->id}\n";
```

---

#### `updateChecklist` / `deleteChecklist`

```php
// Update name and items (full replace)
$client->checklists()->updateChecklist(105,
    name:  'Deep Clean',
    items: ['All standard tasks', 'Inside oven', 'Inside fridge'],
);

// Delete permanently
$client->checklists()->deleteChecklist(105);
```

---

**Checklists API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listChecklists()` | GET | `/v1/checklist` |
| `getChecklist(int)` | GET | `/v1/checklist/{id}` |
| `createChecklist(string, array)` | POST | `/v1/checklist` |
| `updateChecklist(int, string, array)` | PUT | `/v1/checklist/{id}` |
| `deleteChecklist(int)` | DELETE | `/v1/checklist/{id}` |

---

### Other / Utilities (`$client->other()`)

Reference data and utility endpoints used when building booking flows.

#### `getServices(): ApiResponse`

```php
$resp = $client->other()->getServices();
```

---

#### `getPlans(int $propertyId): ApiResponse`

```php
$resp = $client->other()->getPlans(1004);
```

---

#### `getRecommendedHours(int $propertyId, int $bathroomCount, int $roomCount): ApiResponse`

Returns the system-recommended cleaning duration. Use the result to pre-fill `hours` in `createBooking()`.

```php
$resp = $client->other()->getRecommendedHours(
    propertyId:    1004,
    bathroomCount: 2,
    roomCount:     3,
);
```

---

#### `calculateCost(array $request): ApiResponse`

Preview the estimated booking price before committing.

```php
$resp = $client->other()->calculateCost([
    'propertyId' => 1004,
    'planId'     => 2,
    'hours'      => 3,
    'couponCode' => '20POFF',   // optional
]);
```

---

#### `getCleaningExtras` / `getAvailableCleaners` / `getCoupons`

```php
// Add-on services (inside fridge, laundry, etc.)
$extras = $client->other()->getCleaningExtras(serviceId: 1);

// Find available cleaners for a time slot
$cleaners = $client->other()->getAvailableCleaners([
    'propertyId' => 1004,
    'date'       => '2025-06-15',
    'time'       => '10:00',
]);

// All valid coupon codes
$coupons = $client->other()->getCoupons();
```

---

**Other API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getServices()` | GET | `/v1/services` |
| `getPlans(int)` | GET | `/v1/plans?propertyId={id}` |
| `getRecommendedHours(int, int, int)` | GET | `/v1/recommended-hours` |
| `calculateCost(array)` | POST | `/v1/cost-estimate` |
| `getCleaningExtras(int)` | GET | `/v1/cleaning-extras/{serviceId}` |
| `getAvailableCleaners(array)` | POST | `/v1/available-cleaners` |
| `getCoupons()` | GET | `/v1/coupons` |

---

### Blacklist (`$client->blacklist()`)

Prevent specific cleaners from being auto-assigned to bookings.

```php
// List all blacklisted cleaners
$list = $client->blacklist()->listBlacklistedCleaners();

// Add a cleaner (reason is optional)
$client->blacklist()->addToBlacklist(cleanerId: 7, reason: 'Damaged furniture');

// Add without a reason
$client->blacklist()->addToBlacklist(7);

// Remove a cleaner
$client->blacklist()->removeFromBlacklist(7);
```

**Blacklist API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listBlacklistedCleaners()` | GET | `/v1/blacklist/cleaner` |
| `addToBlacklist(int, ?string)` | POST | `/v1/blacklist/cleaner` |
| `removeFromBlacklist(int)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`$client->paymentMethods()`)

#### Stripe

```php
// 1. Get SetupIntent details (use clientSecret with Stripe.js client-side)
$intent = $client->paymentMethods()->getSetupIntentDetails();

// 2. After client-side tokenization, save the payment method
$client->paymentMethods()->addPaymentMethod('pm_xxxxxxxxxxxxxxxx');
```

#### PayPal

```php
// Get client token for PayPal button rendering
$token = $client->paymentMethods()->getPaypalClientToken();
```

#### Manage Saved Methods

```php
// List all payment methods
$resp = $client->paymentMethods()->getPaymentMethods();
foreach ($resp->data as $method) {   // $method is a PaymentMethod object
    $label = $method->type;
    if ($method->lastFour !== null) {
        $label .= " *{$method->lastFour} ({$method->brand})";
    }
    if ($method->isDefault) {
        $label .= ' [DEFAULT]';
    }
    echo "#{$method->id}: {$label}\n";
}

// Set as default
$client->paymentMethods()->setDefaultPaymentMethod(193);

// Delete
$client->paymentMethods()->deletePaymentMethod(193);
```

**Payment Methods API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `getSetupIntentDetails()` | GET | `/v1/payment-methods/setup-intent` |
| `getPaypalClientToken()` | GET | `/v1/payment-methods/paypal-client-token` |
| `addPaymentMethod(string)` | POST | `/v1/payment-methods` |
| `getPaymentMethods()` | GET | `/v1/payment-methods` |
| `deletePaymentMethod(int)` | DELETE | `/v1/payment-methods/{id}` |
| `setDefaultPaymentMethod(int)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`$client->webhooks()`)

Receive real-time notifications when booking events occur — no polling required.

```php
// List all webhook endpoints
$hooks = $client->webhooks()->listWebhooks();

// Register a new webhook
$client->webhooks()->createWebhook(
    url:   'https://your-app.com/webhooks/cleanster',
    event: 'booking.status_changed',
);

// Update a webhook
$client->webhooks()->updateWebhook(50,
    url:   'https://your-app.com/v2/webhooks',
    event: 'booking.status_changed',
);

// Delete a webhook
$client->webhooks()->deleteWebhook(50);
```

**Webhooks API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `listWebhooks()` | GET | `/v1/webhooks` |
| `createWebhook(string, string)` | POST | `/v1/webhooks` |
| `updateWebhook(int, string, string)` | PUT | `/v1/webhooks/{id}` |
| `deleteWebhook(int)` | DELETE | `/v1/webhooks/{id}` |

---

## Response Structure

Every SDK method returns a `Cleanster\Models\ApiResponse` object.

```php
final class ApiResponse {
    public readonly int    $status;    // HTTP-style status code (e.g., 200)
    public readonly string $message;   // Human-readable status (e.g., "OK")
    public readonly mixed  $data;      // Typed payload — model object, array of models, or raw array
}
```

**Usage examples:**

```php
// Access all three fields
$resp = $client->bookings()->getBookingDetails(16926);
echo $resp->status;   // 200
echo $resp->message;  // "OK"
$booking = $resp->data;   // Cleanster\Models\Booking

// List responses return arrays of typed model objects
$resp = $client->bookings()->getBookings();
foreach ($resp->data as $booking) {   // each is a Booking instance
    echo "{$booking->id}: {$booking->status}\n";
}

// Utility responses return raw arrays
$resp = $client->other()->getCoupons();
$coupons = $resp->data;   // array
```

---

## Model Reference

### `Booking`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | Booking ID |
| `$status` | `string` | `status` | `OPEN` / `CLEANER_ASSIGNED` / `COMPLETED` / `CANCELLED` / `REMOVED` |
| `$date` | `string` | `date` | Booking date (YYYY-MM-DD) |
| `$time` | `string` | `time` | Start time (HH:mm) |
| `$hours` | `float` | `hours` | Duration in hours |
| `$cost` | `float` | `cost` | Total cost |
| `$propertyId` | `int` | `propertyId` | Associated property |
| `$cleanerId` | `?int` | `cleanerId` | Assigned cleaner (`null` if unassigned) |
| `$planId` | `int` | `planId` | Booking plan |
| `$roomCount` | `int` | `roomCount` | Number of rooms |
| `$bathroomCount` | `int` | `bathroomCount` | Number of bathrooms |
| `$extraSupplies` | `bool` | `extraSupplies` | Cleaning supplies included |
| `$paymentMethodId` | `int` | `paymentMethodId` | Payment method |
| `$raw` | `array` | — | The complete raw array from the API |

### `User`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | User ID |
| `$email` | `string` | `email` | Email address |
| `$firstName` | `string` | `firstName` | First name |
| `$lastName` | `string` | `lastName` | Last name |
| `$phone` | `?string` | `phone` | Phone number (optional) |
| `$token` | `?string` | `token` | Bearer token — only after `fetchAccessToken()` |
| `$raw` | `array` | — | The complete raw array from the API |

### `Property`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | Property ID |
| `$name` | `string` | `name` | Property label |
| `$address` | `string` | `address` | Street address |
| `$city` | `string` | `city` | City |
| `$country` | `string` | `country` | Country |
| `$roomCount` | `int` | `roomCount` | Number of rooms |
| `$bathroomCount` | `int` | `bathroomCount` | Number of bathrooms |
| `$serviceId` | `int` | `serviceId` | Service type ID |
| `$isEnabled` | `?bool` | `isEnabled` | Active state (`null` if not returned by endpoint) |
| `$raw` | `array` | — | The complete raw array from the API |

### `Checklist`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | Checklist ID |
| `$name` | `string` | `name` | Checklist name |
| `$items` | `ChecklistItem[]` | `items` | Typed array of task items |
| `$raw` | `array` | — | The complete raw array from the API |

### `ChecklistItem`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | Item ID |
| `$description` | `string` | `description` | Task description |
| `$isCompleted` | `bool` | `isCompleted` | Marked complete by cleaner |
| `$imageUrl` | `?string` | `imageUrl` | Proof photo URL (`null` if not uploaded) |
| `$raw` | `array` | — | The complete raw array from the API |

### `PaymentMethod`

| Property | PHP Type | JSON Key | Description |
|----------|---------|----------|-------------|
| `$id` | `int` | `id` | Payment method ID |
| `$type` | `string` | `type` | `"card"` / `"paypal"` / etc. |
| `$lastFour` | `?string` | `lastFour` | Last 4 digits (cards only) |
| `$brand` | `?string` | `brand` | Card brand (`"visa"`, `"mastercard"`, etc.) |
| `$isDefault` | `bool` | `isDefault` | Default method flag |
| `$raw` | `array` | — | The complete raw array from the API |

All model classes use PHP 8.1 `readonly` properties — they cannot be modified after construction.

---

## Sandbox vs Production

| Feature | Sandbox | Production |
|---------|---------|------------|
| Real charges | No | Yes |
| Real cleaners dispatched | No | Yes |
| Coupon codes | Test codes work | Real codes only |
| Data persistence | Yes (sandbox DB) | Yes (production DB) |
| Factory method | `CleansterClient::sandbox()` | `CleansterClient::production()` |
| Config constant | `Config::SANDBOX_BASE_URL` | `Config::PRODUCTION_BASE_URL` |

```php
// Development and testing
$client = CleansterClient::sandbox($_ENV['CLEANSTER_API_KEY']);

// Production
$client = CleansterClient::production($_ENV['CLEANSTER_API_KEY']);
```

> **Always develop and test against the sandbox.** Switch to production only when you are ready to go live. The sandbox never charges real money or dispatches real cleaners.

---

## Test Coupon Codes (Sandbox Only)

These discount codes work only in the sandbox environment. Use them to test coupon flows without real charges.

| Code | Discount | Suggested Use |
|------|----------|---------------|
| `100POFF` | 100% off (free booking) | Verify zero-cost booking flow |
| `50POFF` | 50% off | Verify percentage discount calculation |
| `20POFF` | 20% off | Verify small percentage discount |
| `200OFF` | $200 flat off | Verify flat-dollar discount |
| `100OFF` | $100 flat off | Verify partial flat-dollar discount |

Pass via `couponCode` in `createBooking()` or in the `calculateCost()` request array.

---

## Running Tests

The test suite contains **106 tests** — all passing. Tests use PHPUnit 10 with mocked `HttpClient` objects — no real API calls, no API keys, and no network access required.

```bash
# Install dev dependencies (first time only)
composer install

# Run all tests
vendor/bin/phpunit

# Run with verbose testdox output (shows every test name)
vendor/bin/phpunit --testdox

# Run a specific test class
vendor/bin/phpunit --filter CleansterTest

# Run tests matching a name pattern
vendor/bin/phpunit --filter testBooking

# Run with code coverage (requires Xdebug or PCOV)
vendor/bin/phpunit --coverage-text

# Generate an HTML coverage report
vendor/bin/phpunit --coverage-html coverage/
# Then open coverage/index.html in a browser
```

### Test Coverage Areas

| Area | Tests | What's Verified |
|------|-------|-----------------|
| Config | 8 | Factory URLs, access key storage, default timeout, blank key/URL rejection |
| CleansterClient | 12 | Factory methods, all 8 service accessors, `setAccessToken` / `getAccessToken` |
| `BookingsApi` | 22 | All 17 booking methods + edge cases (with/without reason, with/without comment, all param combos) |
| `UsersApi` | 5 | Create with/without phone, `omitempty` behaviour, `fetchAccessToken` token field, `verifyJwt` |
| `PropertiesApi` | 16 | CRUD, enable/disable bool, cleaner assignment, iCal add/get/remove, checklist assignment (true/false) |
| `ChecklistsApi` | 5 | List, get with typed `ChecklistItem` array, create, update, delete |
| `OtherApi` | 7 | All 7 utility endpoints with correct query params and request bodies |
| `BlacklistApi` | 4 | List, add with reason, add without reason, remove |
| `PaymentMethodsApi` | 6 | All 6 endpoints; `getPaymentMethods` maps typed `PaymentMethod` objects |
| `WebhooksApi` | 4 | List, create, update, delete |
| Exceptions | 8 | `AuthException` properties, `ApiException` properties (custom and auto message), `CleansterException` message, inheritance chain |
| Models | 6 | All model fields, nullable property handling, `Checklist` typed items, `User.token` nullable |
| **Total** | **106** | |

### How Tests Work

Every test injects a mock `HttpClient` into the API class being tested. The mock:
- Asserts the correct HTTP method (`get`, `post`, `put`, or `delete`) is called.
- Asserts the exact path string is used.
- Asserts the exact request body or query parameters are passed.
- Returns a pre-built response array that triggers the model parsing logic.

```php
// Example: how a booking test is structured
$mockHttp = $this->createMock(HttpClient::class);
$mockHttp->expects($this->once())
         ->method('post')
         ->with('/v1/bookings/{id}/feedback', ['rating' => 5, 'comment' => 'Excellent!'])
         ->willReturn(['status' => 200, 'message' => 'OK', 'data' => []]);

(new BookingsApi($mockHttp))->submitFeedback($bookingId, 5, 'Excellent!');
```

---

## Project Structure

```
cleanster-php-sdk/
├── composer.json                    ← PSR-4, PHP 8.1+, phpunit/phpunit dev dep
├── phpunit.xml                      ← PHPUnit 10 configuration
├── src/
│   ├── Config.php                   ← Config: sandbox/production URLs, timeout
│   ├── HttpClient.php               ← cURL transport; inject mock for testing
│   ├── CleansterClient.php          ← Main entry point; 8 service accessors
│   ├── Exceptions/
│   │   ├── CleansterException.php   ← Base: network/cURL/JSON errors
│   │   ├── AuthException.php        ← HTTP 401 — $statusCode + $responseBody
│   │   └── ApiException.php         ← HTTP 4xx/5xx — $statusCode + $responseBody
│   ├── Models/
│   │   ├── ApiResponse.php          ← Wrapper: $status, $message, $data
│   │   ├── Booking.php              ← Booking model (readonly)
│   │   ├── User.php                 ← User model (readonly)
│   │   ├── Property.php             ← Property model (readonly)
│   │   ├── Checklist.php            ← Checklist model — items → ChecklistItem[]
│   │   ├── ChecklistItem.php        ← ChecklistItem model (readonly)
│   │   └── PaymentMethod.php        ← PaymentMethod model (readonly)
│   └── Api/
│       ├── BookingsApi.php          ← 17 booking methods
│       ├── UsersApi.php             ← 3 user/auth methods
│       ├── PropertiesApi.php        ← 14 property management methods
│       ├── ChecklistsApi.php        ← 5 checklist CRUD methods
│       ├── OtherApi.php             ← 7 utility/reference methods
│       ├── BlacklistApi.php         ← 3 blacklist methods
│       ├── PaymentMethodsApi.php    ← 6 payment method methods
│       └── WebhooksApi.php          ← 4 webhook methods
├── tests/
│   └── CleansterTest.php            ← 106 PHPUnit tests (all passing)
├── README.md
├── LICENSE
├── CHANGELOG.md
└── .gitignore
```

### Key Design Decisions

**Why `ext-curl` instead of Guzzle?**
Every standard PHP hosting environment includes `ext-curl` — no installation required, no version conflicts, and no Composer dependency tree to audit. The `HttpClient` class is fully injectable, so if you prefer Guzzle or a PSR-18 client, you can swap it out by extending `HttpClient`.

**Why PHP 8.1+ readonly properties?**
Readonly properties guarantee that model objects are immutable after construction — you can safely pass them through your application without worrying about mutation. They also produce a clean, self-documenting API (`$booking->status`, `$user->email`) rather than forcing callers through getter methods.

**Why injectable `HttpClient`?**
Passing `$httpClient` into the `CleansterClient` constructor (or directly into each API class) is the standard dependency-injection pattern for PHP. It makes every API method testable with PHPUnit's `createMock()` — no HTTP server, no API key, no network required. All 106 tests use this mechanism.

**Why named arguments on method signatures?**
PHP 8.1 named arguments make call sites self-documenting (`createBooking(date: '2025-06-15', time: '10:00')`) and eliminate the need to remember parameter order for calls with several arguments. They also make omitting optional parameters explicit and readable.

---

## Contributing

1. Fork the repository on GitHub.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and add PHPUnit tests in `tests/CleansterTest.php`.
4. Ensure all tests pass: `vendor/bin/phpunit`
5. Submit a pull request with a clear description.

### Code Style

- Follow PSR-12 coding standards.
- All public methods must have complete PHPDoc blocks.
- All model properties must be `readonly` and explicitly typed.
- Test every API method — each test must assert both the HTTP verb/path and the response parsing.
- No external runtime dependencies — only `ext-curl` and `ext-json`.

---

## License

This SDK is released under the [MIT License](LICENSE). You are free to use, modify, and distribute it in personal and commercial projects.

---

## Support

| Resource | Link |
|----------|------|
| API Documentation | https://documenter.getpostman.com/view/26172658/2sAYdoF7ep |
| Packagist | https://packagist.org/packages/cleanster/cleanster-php-sdk |
| Partner Support | partner@cleanster.com |
| General Support | support@cleanster.com |
| GitHub Issues | https://github.com/cleanster/cleanster-php-sdk/issues |

---

*Made with care for the Cleanster partner ecosystem.*
