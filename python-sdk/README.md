# Cleanster Python SDK

<p align="center">
  <strong>Official Python client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Python-3.8%2B-blue?logo=python" alt="Python 3.8+">
  <img src="https://img.shields.io/badge/PyPI-cleanster-orange?logo=pypi" alt="PyPI">
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
  - [Payment Methods](#payment-methods-clientpayment_methods)
  - [Webhooks](#webhooks-clientwebhooks)
- [Error Handling](#error-handling)
- [Response Structure](#response-structure)
- [Models Reference](#models-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster Python SDK provides a clean, Pythonic interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It handles:

- **Dual-layer authentication** — partner access key + per-user bearer tokens, both sent automatically on every request
- **Typed models** — response data is mapped to Python objects (`Booking`, `User`, `Property`, etc.)
- **Typed exceptions** — distinct exception types for 401 auth errors, API errors, and network failures
- **Environment switching** — one line to toggle between sandbox and production
- **`requests`-based transport** — battle-tested HTTP with session keep-alive and configurable timeout
- **Full type annotations** — works seamlessly with mypy, Pyright, and IDE autocompletion

---

## Requirements

| Dependency | Minimum Version |
|-----------|----------------|
| Python | 3.8 |
| requests | 2.28 |

---

## Installation

### From PyPI (Recommended)

```bash
pip install cleanster
```

### With pip + version pin

```bash
pip install cleanster==1.0.0
```

### Add to `requirements.txt`

```
cleanster>=1.0.0
```

### Add to `pyproject.toml`

```toml
[project]
dependencies = [
    "cleanster>=1.0.0",
]
```

### Build from Source

```bash
git clone https://github.com/cleanster/cleanster-python-sdk.git
cd cleanster-python-sdk
pip install -e .
```

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** passed as HTTP headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

### Step-by-Step Authentication

**Step 1 — Initialize the client with your partner access key:**

```python
from cleanster import CleansterClient

client = CleansterClient.sandbox("your-partner-access-key")
```

**Step 2 — Create or look up a user.** If they're new, register them:

```python
response = client.users.create_user(
    email="user@example.com",
    first_name="Jane",
    last_name="Doe",
    phone="+15551234567",  # optional
)
user_id = response.data.id
print(f"Created user #{user_id}")
```

**Step 3 — Fetch the user's long-lived bearer token:**

```python
token_response = client.users.fetch_access_token(user_id)
user_token = token_response.data.token
```

**Step 4 — Set the token on the client** for all subsequent calls:

```python
client.set_access_token(user_token)
# All calls from this point include the user token automatically
```

> **Tip:** The user token is long-lived. Store it securely (e.g. in your database) and call `set_access_token()` on each session to avoid re-fetching every time.

---

## Quick Start

```python
from cleanster import CleansterClient

# 1. Initialize (sandbox for development, production for live)
client = CleansterClient.sandbox("your-access-key")

# 2. Register a new user
user_resp = client.users.create_user(
    email="jane@example.com",
    first_name="Jane",
    last_name="Smith",
)
user_id = user_resp.data.id

# 3. Fetch and set the user access token
token = client.users.fetch_access_token(user_id).data.token
client.set_access_token(token)

# 4. Add a property
property_resp = client.properties.add_property({
    "name": "Beach House",
    "address": "123 Ocean Drive",
    "city": "Miami",
    "country": "USA",
    "roomCount": 3,
    "bathroomCount": 2,
    "serviceId": 1,
})
property_id = property_resp.data.id

# 5. Check recommended hours and estimate cost
hours_resp = client.other.get_recommended_hours(property_id, bathroom_count=2, room_count=3)

estimate = client.other.calculate_cost({
    "propertyId": property_id,
    "planId": 2,
    "hours": 3.0,
    "couponCode": "20POFF",  # optional
})

# 6. Create a booking
booking_resp = client.bookings.create_booking({
    "date": "2025-06-15",
    "time": "10:00",
    "propertyId": property_id,
    "roomCount": 3,
    "bathroomCount": 2,
    "planId": 2,
    "hours": 3.0,
    "extraSupplies": False,
    "paymentMethodId": 10,
})
booking_id = booking_resp.data.id
print(f"Created booking #{booking_id}, status: {booking_resp.data.status}")

# 7. List all bookings
all_bookings = client.bookings.get_bookings()
print(f"API status: {all_bookings.status}, message: {all_bookings.message}")
```

---

## Configuration

### Factory Methods (Recommended)

```python
# Sandbox — for development and testing (no real charges)
client = CleansterClient.sandbox("your-access-key")

# Production — for live use (real cleaners, real charges)
client = CleansterClient.production("your-access-key")
```

### Builder Pattern (Custom Configuration)

```python
from cleanster import CleansterConfig, CleansterClient

config = (
    CleansterConfig.builder("your-access-key")
    .sandbox()               # or .production()
    .timeout(60)             # request timeout in seconds (default: 30)
    .build()
)
client = CleansterClient(config)
```

### Custom Base URL

```python
config = (
    CleansterConfig.builder("your-access-key")
    .base_url("https://your-proxy.example.com")
    .build()
)
```

### Base URLs

| Environment | Base URL |
|-------------|----------|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## API Reference

Every API method returns an `ApiResponse` object. See [Response Structure](#response-structure) for details.

---

### Bookings (`client.bookings`)

The booking API manages the full lifecycle of a cleaning appointment.

#### `get_bookings(page_no=None, status=None)`

Retrieve a paginated list of bookings.

```python
# All bookings (no filters)
result = client.bookings.get_bookings()

# Filter by status
completed = client.bookings.get_bookings(status="COMPLETED")

# Paginate
page2 = client.bookings.get_bookings(page_no=2, status="OPEN")
```

**Valid status values:** `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED`

---

#### `create_booking(request: dict)`

Schedule a new cleaning appointment.

```python
booking = client.bookings.create_booking({
    "date": "2025-06-15",          # Required — YYYY-MM-DD
    "time": "10:00",               # Required — HH:mm (24-hour)
    "propertyId": 1004,            # Required
    "roomCount": 2,                # Required
    "bathroomCount": 1,            # Required
    "planId": 5,                   # Required — from get_plans()
    "hours": 3.0,                  # Required — from get_recommended_hours()
    "extraSupplies": False,        # Required — include cleaning supplies?
    "paymentMethodId": 10,         # Required
})
print(f"Booking #{booking.data.id} — {booking.data.status}")
```

---

#### `get_booking_details(booking_id: int)`

```python
result = client.bookings.get_booking_details(16926)
b = result.data
print(f"Date: {b.date}, Hours: {b.hours}, Cost: ${b.cost}")
print(f"Cleaner ID: {b.cleaner_id}")
```

---

#### `cancel_booking(booking_id: int, reason: str = None)`

```python
client.bookings.cancel_booking(16459, reason="Changed my schedule")
```

---

#### `reschedule_booking(booking_id: int, date: str, time: str)`

```python
client.bookings.reschedule_booking(16459, "2025-07-01", "14:00")
```

---

#### `assign_cleaner(booking_id: int, cleaner_id: int)`

```python
client.bookings.assign_cleaner(16459, cleaner_id=5)
```

---

#### `remove_assigned_cleaner(booking_id: int)`

```python
client.bookings.remove_assigned_cleaner(16459)
```

---

#### `adjust_hours(booking_id: int, hours: float)`

```python
client.bookings.adjust_hours(16459, 4.0)
```

---

#### `pay_expenses(booking_id: int, payment_method_id: int)`

Pay outstanding expenses within 72 hours of booking completion.

```python
client.bookings.pay_expenses(16926, payment_method_id=10)
```

---

#### `get_booking_inspection(booking_id)` / `get_booking_inspection_details(booking_id)`

```python
inspection = client.bookings.get_booking_inspection(16926)
details = client.bookings.get_booking_inspection_details(16926)
```

---

#### `assign_checklist_to_booking(booking_id: int, checklist_id: int)`

Override the property's default checklist for this specific booking.

```python
client.bookings.assign_checklist_to_booking(16926, checklist_id=105)
```

---

#### `submit_feedback(booking_id: int, rating: int, comment: str = None)`

```python
client.bookings.submit_feedback(16926, rating=5, comment="Excellent — very thorough!")
```

**Rating:** Integer 1–5.

---

#### `add_tip(booking_id: int, amount: float, payment_method_id: int)`

Add a tip within 72 hours of booking completion.

```python
client.bookings.add_tip(16926, amount=20.0, payment_method_id=10)
```

---

#### Chat Methods

```python
# Get all messages in a booking thread
chat = client.bookings.get_chat(17142)

# Send a message
client.bookings.send_message(17142, "Please focus on the kitchen today.")

# Delete a message
client.bookings.delete_message(17142, "msg-abc-123")
```

---

**Bookings API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_bookings(page_no, status)` | GET | `/v1/bookings` |
| `create_booking(req)` | POST | `/v1/bookings/create` |
| `get_booking_details(id)` | GET | `/v1/bookings/{id}` |
| `cancel_booking(id, reason)` | POST | `/v1/bookings/{id}/cancel` |
| `reschedule_booking(id, date, time)` | POST | `/v1/bookings/{id}/reschedule` |
| `assign_cleaner(id, cleaner_id)` | POST | `/v1/bookings/{id}/cleaner` |
| `remove_assigned_cleaner(id)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `adjust_hours(id, hours)` | POST | `/v1/bookings/{id}/hours` |
| `pay_expenses(id, pm_id)` | POST | `/v1/bookings/{id}/expenses` |
| `get_booking_inspection(id)` | GET | `/v1/bookings/{id}/inspection` |
| `get_booking_inspection_details(id)` | GET | `/v1/bookings/{id}/inspection/details` |
| `assign_checklist_to_booking(id, cid)` | POST | `/v1/bookings/{id}/checklist/{cid}` |
| `submit_feedback(id, rating, comment)` | POST | `/v1/bookings/{id}/feedback` |
| `add_tip(id, amount, pm_id)` | POST | `/v1/bookings/{id}/tip` |
| `get_chat(id)` | GET | `/v1/bookings/{id}/chat` |
| `send_message(id, message)` | POST | `/v1/bookings/{id}/chat` |
| `delete_message(id, msg_id)` | DELETE | `/v1/bookings/{id}/chat/{msg_id}` |

---

### Users (`client.users`)

#### `create_user(email, first_name, last_name, phone=None)`

```python
result = client.users.create_user(
    email="jane@example.com",
    first_name="Jane",
    last_name="Smith",
    phone="+15551234567",  # optional
)
user = result.data
print(f"User #{user.id} — {user.email}")
```

---

#### `fetch_access_token(user_id: int)`

```python
result = client.users.fetch_access_token(42)
token = result.data.token
client.set_access_token(token)
```

---

#### `verify_jwt(token: str)`

```python
result = client.users.verify_jwt("eyJhbGci...")
print(result.status)  # 200 if valid
```

---

**Users API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `create_user(email, first_name, last_name, phone)` | POST | `/v1/user/account` |
| `fetch_access_token(user_id)` | GET | `/v1/user/access-token/{user_id}` |
| `verify_jwt(token)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`client.properties`)

#### `list_properties(service_id=None)`

```python
# All properties
all_props = client.properties.list_properties()

# Filter by service type
residential = client.properties.list_properties(service_id=1)
```

---

#### `add_property(request: dict)`

```python
result = client.properties.add_property({
    "name": "Downtown Condo",
    "address": "456 Main St",
    "city": "Toronto",
    "country": "Canada",
    "roomCount": 2,
    "bathroomCount": 1,
    "serviceId": 1,
})
property_id = result.data.id
```

---

#### CRUD Operations

```python
# Get
prop = client.properties.get_property(1040)
print(prop.data.name, prop.data.city)

# Update
client.properties.update_property(1040, {"name": "New Name", "roomCount": 3})

# Enable / Disable
client.properties.enable_or_disable_property(1040, enabled=False)

# Delete
client.properties.delete_property(1040)
```

---

#### Property Cleaners

```python
# List assigned cleaners
cleaners = client.properties.get_property_cleaners(1040)

# Assign a cleaner
client.properties.assign_cleaner_to_property(1040, cleaner_id=5)

# Unassign a cleaner
client.properties.unassign_cleaner_from_property(1040, cleaner_id=5)
```

---

#### iCal Calendar Integration

Sync property availability with Airbnb, VRBO, or any iCal-compatible calendar.

```python
# Add iCal link
client.properties.add_ical_link(1040, "https://calendar.example.com/feed.ics")

# Get current iCal link
ical = client.properties.get_ical_link(1040)

# Remove iCal link
client.properties.remove_ical_link(1040, "https://calendar.example.com/feed.ics")
```

---

#### `assign_checklist_to_property(property_id, checklist_id, update_upcoming_bookings=False)`

Set the default checklist for all future bookings at this property.

```python
# Assign and apply to all upcoming bookings
client.properties.assign_checklist_to_property(1040, 105, update_upcoming_bookings=True)
```

---

**Properties API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_properties(service_id)` | GET | `/v1/properties` |
| `add_property(req)` | POST | `/v1/properties` |
| `get_property(id)` | GET | `/v1/properties/{id}` |
| `update_property(id, req)` | PUT | `/v1/properties/{id}` |
| `update_additional_information(id, data)` | PUT | `/v1/properties/{id}/additional-information` |
| `enable_or_disable_property(id, enabled)` | POST | `/v1/properties/{id}/enable-disable` |
| `delete_property(id)` | DELETE | `/v1/properties/{id}` |
| `get_property_cleaners(id)` | GET | `/v1/properties/{id}/cleaners` |
| `assign_cleaner_to_property(id, cleaner_id)` | POST | `/v1/properties/{id}/cleaners` |
| `unassign_cleaner_from_property(id, cleaner_id)` | DELETE | `/v1/properties/{id}/cleaners/{cid}` |
| `add_ical_link(id, url)` | PUT | `/v1/properties/{id}/ical` |
| `get_ical_link(id)` | GET | `/v1/properties/{id}/ical` |
| `remove_ical_link(id, url)` | DELETE | `/v1/properties/{id}/ical` |
| `assign_checklist_to_property(id, cid, flag)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`client.checklists`)

Checklists define the cleaning tasks a cleaner should complete. They can be assigned to properties (as defaults) or to individual bookings (as overrides).

#### `list_checklists()`

```python
result = client.checklists.list_checklists()
```

---

#### `get_checklist(checklist_id: int)`

```python
result = client.checklists.get_checklist(105)
checklist = result.data
print(f"Checklist: {checklist.name}")
for item in checklist.items:
    status = "✓" if item.is_completed else "○"
    print(f"  {status} {item.description}")
```

---

#### `create_checklist(name: str, items: list[str])`

```python
result = client.checklists.create_checklist(
    name="Standard Residential Clean",
    items=[
        "Vacuum all floors",
        "Mop kitchen and bathroom floors",
        "Wipe all countertops",
        "Scrub toilets, sinks, and tubs",
        "Empty all trash bins",
        "Wipe mirrors and glass surfaces",
    ],
)
print(f"Created checklist #{result.data.id}")
```

---

#### `update_checklist(checklist_id, name, items)` / `delete_checklist(checklist_id)`

```python
# Update
client.checklists.update_checklist(105, "Deep Clean", ["Task A", "Task B"])

# Delete
client.checklists.delete_checklist(105)
```

---

**Checklists API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_checklists()` | GET | `/v1/checklist` |
| `get_checklist(id)` | GET | `/v1/checklist/{id}` |
| `create_checklist(name, items)` | POST | `/v1/checklist` |
| `update_checklist(id, name, items)` | PUT | `/v1/checklist/{id}` |
| `delete_checklist(id)` | DELETE | `/v1/checklist/{id}` |

---

### Other / Utilities (`client.other`)

Reference data endpoints used when building booking flows.

#### `get_services()`

```python
services = client.other.get_services()
```

---

#### `get_plans(property_id: int)`

```python
plans = client.other.get_plans(1004)
```

---

#### `get_recommended_hours(property_id, bathroom_count, room_count)`

```python
result = client.other.get_recommended_hours(1004, bathroom_count=2, room_count=3)
```

---

#### `calculate_cost(request: dict)`

Show users a price preview before booking.

```python
estimate = client.other.calculate_cost({
    "propertyId": 1004,
    "planId": 2,
    "hours": 3.0,
    "couponCode": "20POFF",  # optional
})
```

---

#### `get_cleaning_extras(service_id: int)`

```python
extras = client.other.get_cleaning_extras(service_id=1)
```

---

#### `get_available_cleaners(request: dict)`

```python
available = client.other.get_available_cleaners({
    "propertyId": 1004,
    "date": "2025-06-15",
    "time": "10:00",
})
```

---

#### `get_coupons()`

```python
coupons = client.other.get_coupons()
```

---

**Other API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_services()` | GET | `/v1/services` |
| `get_plans(property_id)` | GET | `/v1/plans?propertyId={id}` |
| `get_recommended_hours(pId, baths, rooms)` | GET | `/v1/recommended-hours` |
| `calculate_cost(req)` | POST | `/v1/cost-estimate` |
| `get_cleaning_extras(service_id)` | GET | `/v1/cleaning-extras/{serviceId}` |
| `get_available_cleaners(req)` | POST | `/v1/available-cleaners` |
| `get_coupons()` | GET | `/v1/coupons` |

---

### Blacklist (`client.blacklist`)

Prevent specific cleaners from being assigned to your bookings.

```python
# List all blacklisted cleaners
blacklisted = client.blacklist.list_blacklisted_cleaners()

# Add to blacklist
client.blacklist.add_to_blacklist(cleaner_id=7, reason="Damaged furniture")

# Remove from blacklist
client.blacklist.remove_from_blacklist(cleaner_id=7)
```

**Blacklist API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_blacklisted_cleaners()` | GET | `/v1/blacklist/cleaner` |
| `add_to_blacklist(cleaner_id, reason)` | POST | `/v1/blacklist/cleaner` |
| `remove_from_blacklist(cleaner_id)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`client.payment_methods`)

Manage Stripe and PayPal payment methods for your users.

```python
# Stripe — get setup intent for client-side card collection
intent = client.payment_methods.get_setup_intent_details()
# Use intent.data["clientSecret"] with Stripe.js

# PayPal — get client token for PayPal button rendering
paypal = client.payment_methods.get_paypal_client_token()

# Add a payment method (after client-side tokenization)
client.payment_methods.add_payment_method({"paymentMethodId": "pm_xxxxxxxxxxxx"})

# List saved payment methods
methods = client.payment_methods.get_payment_methods()

# Set a default
client.payment_methods.set_default_payment_method(193)

# Delete
client.payment_methods.delete_payment_method(193)
```

**Payment Methods API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_setup_intent_details()` | GET | `/v1/payment-methods/setup-intent` |
| `get_paypal_client_token()` | GET | `/v1/payment-methods/paypal-client-token` |
| `add_payment_method(req)` | POST | `/v1/payment-methods` |
| `get_payment_methods()` | GET | `/v1/payment-methods` |
| `delete_payment_method(id)` | DELETE | `/v1/payment-methods/{id}` |
| `set_default_payment_method(id)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`client.webhooks`)

Receive real-time event notifications on your server.

```python
# List configured webhooks
webhooks = client.webhooks.list_webhooks()

# Register a new webhook
result = client.webhooks.create_webhook({
    "url": "https://your-app.com/webhooks/cleanster",
    "event": "booking.status_changed",
})

# Update
client.webhooks.update_webhook(50, {"url": "https://your-app.com/v2/webhooks"})

# Delete
client.webhooks.delete_webhook(50)
```

**Webhooks API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_webhooks()` | GET | `/v1/webhooks` |
| `create_webhook(req)` | POST | `/v1/webhooks` |
| `update_webhook(id, req)` | PUT | `/v1/webhooks/{id}` |
| `delete_webhook(id)` | DELETE | `/v1/webhooks/{id}` |

---

## Error Handling

The SDK raises typed exceptions. Always handle the most specific type first.

```python
from cleanster import CleansterClient
from cleanster.exceptions import (
    CleansterAuthException,
    CleansterApiException,
    CleansterException,
)

client = CleansterClient.sandbox("your-key")
client.set_access_token("user-token")

try:
    result = client.bookings.get_booking_details(99999)

except CleansterAuthException as e:
    # HTTP 401 — bad access key or user token
    print(f"Auth error: {e}")
    print(f"Status: {e.status_code}")           # always 401
    print(f"Response body: {e.response_body}")

except CleansterApiException as e:
    # HTTP 4xx or 5xx — API-level error
    print(f"API error [{e.status_code}]: {e}")
    print(f"Response body: {e.response_body}")

    if e.status_code == 404:
        print("Resource not found.")
    elif e.status_code == 422:
        print("Validation error — check your request fields.")
    elif e.status_code >= 500:
        print("Server error — try again later.")

except CleansterException as e:
    # Network failure, timeout, JSON parse error
    print(f"SDK error: {e}")
```

### Exception Hierarchy

```
CleansterException (base)        — network errors, timeouts, serialization
├── CleansterAuthException       — HTTP 401 (invalid/missing credentials)
└── CleansterApiException        — HTTP 4xx / 5xx (API errors)
```

| Exception | Raised When | Key Attributes |
|-----------|-------------|----------------|
| `CleansterException` | Network error, timeout, JSON parse failure | `str(e)` |
| `CleansterAuthException` | HTTP 401 | `status_code` (always 401), `response_body` |
| `CleansterApiException` | HTTP 4xx/5xx (not 401) | `status_code`, `response_body` |

---

## Response Structure

All API methods return an `ApiResponse` object:

```python
class ApiResponse:
    status: int        # HTTP-style status code (e.g., 200)
    message: str       # Human-readable message (e.g., "OK")
    data: Any          # Typed response payload (Booking, User, list, dict, etc.)
```

**Example — accessing the response:**

```python
result = client.bookings.get_booking_details(16926)

print(result.status)    # 200
print(result.message)   # "OK"

booking = result.data   # Booking object
print(booking.id)
print(booking.status)
print(booking.hours)
print(booking.cost)
```

**List responses** return `data` as a Python list:

```python
result = client.bookings.get_bookings()
# result.data is a list of raw dicts (or typed objects, depending on the endpoint)
```

---

## Models Reference

### `Booking`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | Booking ID |
| `status` | `str` | `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |
| `date` | `str` | Booking date (YYYY-MM-DD) |
| `time` | `str` | Start time (HH:mm) |
| `hours` | `float` | Duration in hours |
| `cost` | `float` | Total cost |
| `property_id` | `int` | Associated property ID |
| `cleaner_id` | `int` | Assigned cleaner ID (`None` if unassigned) |
| `plan_id` | `int` | Booking plan ID |
| `room_count` | `int` | Number of rooms |
| `bathroom_count` | `int` | Number of bathrooms |
| `extra_supplies` | `bool` | Whether cleaning supplies are included |

### `User`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | User ID |
| `email` | `str` | Email address |
| `first_name` | `str` | First name |
| `last_name` | `str` | Last name |
| `phone` | `str` | Phone number |
| `token` | `str` | Bearer token (from `fetch_access_token`) |

### `Property`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | Property ID |
| `name` | `str` | Property name/label |
| `address` | `str` | Street address |
| `city` | `str` | City |
| `country` | `str` | Country |
| `room_count` | `int` | Number of rooms |
| `bathroom_count` | `int` | Number of bathrooms |
| `service_id` | `int` | Service type ID |
| `is_enabled` | `bool` | Whether the property is active |

### `Checklist`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | Checklist ID |
| `name` | `str` | Checklist name |
| `items` | `list[ChecklistItem]` | Task items |

### `ChecklistItem`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | Item ID |
| `description` | `str` | Task description |
| `is_completed` | `bool` | Whether the cleaner marked it complete |
| `image_url` | `str` | Proof photo URL (if uploaded) |

### `PaymentMethod`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `int` | Payment method ID |
| `type` | `str` | `card`, `paypal`, etc. |
| `last_four` | `str` | Last 4 digits (cards only) |
| `brand` | `str` | Card brand (e.g. `visa`, `mastercard`) |
| `is_default` | `bool` | Whether this is the default payment method |

---

## Sandbox vs Production

| Feature | Sandbox | Production |
|---------|---------|------------|
| Real charges | No | Yes |
| Real cleaners | No | Yes |
| Coupon codes | Test codes work | Real codes only |
| Data persistence | Yes (sandbox DB) | Yes (production DB) |

> **Always develop against the sandbox environment.** Switch to production only when ready to go live.

---

## Test Coupon Codes (Sandbox Only)

| Code | Discount | Use Case |
|------|----------|----------|
| `100POFF` | 100% off (free booking) | Test zero-cost flows |
| `50POFF` | 50% off | Test percentage discount |
| `20POFF` | 20% off | Test small percentage discount |
| `200OFF` | $200 flat discount | Test flat-rate discount |
| `100OFF` | $100 flat discount | Test partial flat discount |

Pass the coupon code in your `calculate_cost` or `create_booking` request dict.

---

## Running Tests

The test suite contains **100+ unit tests** using Python's built-in `unittest` and `unittest.mock`. No network access or API keys are needed.

### Setup

```bash
# Clone the repo
git clone https://github.com/cleanster/cleanster-python-sdk.git
cd cleanster-python-sdk

# Install dev dependencies
pip install -e ".[dev]"
```

### Run Tests

```bash
# Run all tests
python -m pytest

# Run with verbose output
python -m pytest -v

# Run a specific test file
python -m pytest tests/test_cleanster_sdk.py

# Run a specific test class
python -m pytest tests/test_cleanster_sdk.py::TestBookingsApi

# Run a specific test method
python -m pytest tests/test_cleanster_sdk.py::TestBookingsApi::test_create_booking

# Run with coverage report
python -m pytest --cov=cleanster --cov-report=term-missing

# Run with the standard unittest runner (no extra dependencies)
python -m unittest discover -s tests -v
```

**Test coverage includes:**
- Configuration validation (blank/None access key, URL assignment, custom timeout)
- All 8 API classes — correct HTTP method, path, and request body for every endpoint
- Query parameter construction (page_no, status, service_id, property_id, etc.)
- Optional parameter handling (phone, reason, comment — absent from request when None)
- Response parsing and model field mapping
- Exception hierarchy and propagation

---

## Project Structure

```
cleanster-python-sdk/
├── cleanster/
│   ├── __init__.py          ← Public API surface
│   ├── client.py            ← CleansterClient (main entry point)
│   ├── config.py            ← CleansterConfig + builder
│   ├── exceptions.py        ← Exception hierarchy
│   ├── http_client.py       ← requests wrapper + auth headers
│   ├── api/
│   │   ├── __init__.py
│   │   ├── bookings.py      ← BookingsApi
│   │   ├── users.py         ← UsersApi
│   │   ├── properties.py    ← PropertiesApi
│   │   ├── checklists.py    ← ChecklistsApi
│   │   ├── other.py         ← OtherApi
│   │   ├── blacklist.py     ← BlacklistApi
│   │   ├── payment_methods.py ← PaymentMethodsApi
│   │   └── webhooks.py      ← WebhooksApi
│   └── models/
│       ├── __init__.py
│       ├── booking.py
│       ├── checklist.py
│       ├── payment_method.py
│       ├── property.py
│       ├── response.py      ← ApiResponse
│       └── user.py
├── tests/
│   ├── __init__.py
│   └── test_cleanster_sdk.py  ← 100+ unit tests
├── setup.py
├── pyproject.toml
├── README.md
├── LICENSE
└── CHANGELOG.md
```

---

## Contributing

1. Fork the repository on GitHub.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and add tests.
4. Run the test suite: `python -m pytest`
5. Submit a pull request.

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
| GitHub Issues | https://github.com/cleanster/cleanster-python-sdk/issues |
| PyPI | https://pypi.org/project/cleanster/ |

---

*Made with care for the Cleanster partner ecosystem.*
