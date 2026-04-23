# Cleanster Python SDK

<p align="center">
  <strong>Official Python client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Python-3.8%2B-blue?logo=python" alt="Python 3.8+">
  <img src="https://img.shields.io/badge/PyPI-cleanster-orange?logo=pypi" alt="PyPI">
  <img src="https://img.shields.io/badge/tests-99%20passing-brightgreen" alt="99 passing">
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

The Cleanster Python SDK provides a clean, Pythonic interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It uses only Python's built-in `urllib` — zero external dependencies.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal sync, preferred cleaner lists
- **Handle users** — create accounts and fetch authorization tokens
- **Configure checklists** — create reusable task lists and assign to bookings
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — block specific cleaners from your properties

---

## Requirements

- **Python 3.8** or later
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

```bash
pip install cleanster
```

Or install from source:

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/python-sdk
pip install -e .
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, obtained via `fetch_access_token(user_id)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```python
from cleanster import CleansterClient

client = CleansterClient(access_key="your-access-key")

resp = client.users.create_user(
    email="jane@example.com",
    first_name="Jane",
    last_name="Doe",
    phone="+15551234567"
)
user_id = resp.data["userId"]
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```python
resp = client.users.fetch_access_token(user_id)
user_token = resp.data["token"]
```

**Step 4 — Build the client with both credentials**:

```python
client = CleansterClient(
    access_key="your-access-key",
    token=user_token
)
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401 on a user endpoint.

---

## Quick Start

```python
from cleanster import CleansterClient

client = CleansterClient(access_key="your-access-key", token="user-jwt-token")

# List available services
services = client.other.get_services()
print("Services:", services.data)

# Get recommended hours
hours = client.other.get_recommended_hours(
    property_id=1004,
    bathroom_count=2,
    room_count=3
)
print("Recommended hours:", hours.data)

# Create a booking
booking = client.bookings.create_booking(
    property_id=1004,
    date="2025-09-01",
    time="10:00",
    plan_id=2,
    room_count=3,
    bathroom_count=2,
    hours=3.0,
    extra_supplies=False,
    payment_method_id=10,
    coupon_code="20POFF"  # optional — 20% off in sandbox
)
print("Created booking:", booking.data["id"])

# List open bookings
bookings = client.bookings.list_bookings(page_no=1, status="OPEN")
print("Open bookings:", len(bookings.data["bookings"]))
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```python
# Sandbox (default)
client = CleansterClient(access_key="key", token="token")

# Production
client = CleansterClient(
    access_key="key",
    token="token",
    environment="production"
)
```

---

## Booking Flow

```
create_booking()        →   OPEN
                               │
        assign_cleaner()
                               │
                               ▼
                     CLEANER_ASSIGNED
                               │
                   Cleaner starts the job
                               │
                  ┌────────────┴────────────┐
                  ▼                         ▼
           COMPLETED                  CANCELLED
```

Booking status values: `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED`

---

## API Reference

All methods return an `ApiResponse` with:
- `.status` — HTTP status code
- `.message` — Human-readable result
- `.data` — Response payload (dict or list)

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

Retrieve a paginated list of bookings, optionally filtered by status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `page_no` | int | Yes | Page number (1-based) |
| `status` | str | No | `OPEN`, `CLEANER_ASSIGNED`, `COMPLETED`, `CANCELLED`, `REMOVED` |

```python
resp = client.bookings.list_bookings(page_no=1, status="OPEN")
for booking in resp.data["bookings"]:
    print(booking["id"], booking["status"])
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

Retrieve full details for a single booking.

```python
resp = client.bookings.get_booking(16926)
print(resp.data["status"], "on", resp.data["date"])
```

---

#### Create Booking
**`POST /v1/bookings/create`**

Schedule a new cleaning appointment.

| Field | Type | Required | Description |
|---|---|---|---|
| `property_id` | int | Yes | Property to be cleaned |
| `date` | str | Yes | Date in `YYYY-MM-DD` format |
| `time` | str | Yes | Start time in `HH:MM` (24-hour) |
| `plan_id` | int | Yes | Cleaning plan ID |
| `room_count` | int | Yes | Number of rooms |
| `bathroom_count` | int | Yes | Number of bathrooms |
| `hours` | float | Yes | Scheduled duration in hours |
| `extra_supplies` | bool | Yes | Whether the cleaner brings supplies |
| `payment_method_id` | int | Yes | Payment method ID |
| `coupon_code` | str | No | Discount coupon code |
| `cleaning_extras` | list | No | List of extra service IDs |

```python
resp = client.bookings.create_booking(
    property_id=1004,
    date="2025-09-01",
    time="10:00",
    plan_id=2,
    room_count=3,
    bathroom_count=2,
    hours=3.0,
    extra_supplies=False,
    payment_method_id=10,
    coupon_code="20POFF"
)
print("Booking ID:", resp.data["id"])
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```python
resp = client.bookings.assign_cleaner(booking_id=16926, cleaner_id=3)
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```python
resp = client.bookings.remove_cleaner(booking_id=16926)
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

| Field | Type | Required | Description |
|---|---|---|---|
| `hours` | float | Yes | New duration in hours |

```python
resp = client.bookings.adjust_hours(booking_id=16926, hours=4.5)
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

| Field | Type | Required | Description |
|---|---|---|---|
| `date` | str | Yes | New date `YYYY-MM-DD` |
| `time` | str | Yes | New time `HH:MM` |

```python
resp = client.bookings.reschedule_booking(
    booking_id=16926, date="2025-09-15", time="14:00"
)
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

| Field | Type | Required | Description |
|---|---|---|---|
| `payment_method_id` | int | Yes | Payment method to charge |

```python
resp = client.bookings.pay_expenses(booking_id=16926, payment_method_id=10)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```python
resp = client.bookings.get_inspection(booking_id=16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```python
resp = client.bookings.get_inspection_details(booking_id=16926)
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

| Field | Type | Required | Description |
|---|---|---|---|
| `reason` | str | No | Cancellation reason |

```python
resp = client.bookings.cancel_booking(booking_id=16926, reason="Scheduling conflict")
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```python
resp = client.bookings.assign_checklist_to_booking(
    booking_id=16926, checklist_id=105
)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

| Field | Type | Required | Description |
|---|---|---|---|
| `rating` | int | Yes | 1 (poor) to 5 (excellent) |
| `comment` | str | No | Written feedback |

```python
resp = client.bookings.submit_feedback(
    booking_id=16926, rating=5, comment="Excellent work!"
)
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | float | Yes | Tip amount in USD |
| `payment_method_id` | int | Yes | Payment method to charge |

```python
resp = client.bookings.submit_tip(
    booking_id=16926, amount=15.00, payment_method_id=10
)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

See [Chat Window Rules](#chat-window-rules) for availability.

```python
resp = client.bookings.get_chat(booking_id=16926)
for msg in resp.data["messages"]:
    print(f"[{msg['sender_type']}] {msg['content']}")
```

**Response `messages[]` fields:**

| Field | Type | Description |
|---|---|---|
| `message_id` | str | Unique message identifier |
| `sender_id` | str | Sender reference key (e.g. `C6`, `P3`) |
| `content` | str | Text content (empty for media messages) |
| `timestamp` | str | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `message_type` | str | `text` or `media` |
| `attachments` | list | Media attachments |
| `attachments[].type` | str | `image`, `video`, or `sound` |
| `attachments[].url` | str | Direct media URL |
| `attachments[].thumb_url` | str | Thumbnail URL (nullable) |
| `is_read` | bool | Read status |
| `sender_type` | str | `client`, `cleaner`, `support`, `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

| Field | Type | Required | Description |
|---|---|---|---|
| `message` | str | Yes | Text content |

```python
resp = client.bookings.send_message(
    booking_id=16926, message="Please bring extra cleaning supplies."
)
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```python
resp = client.bookings.delete_message(
    booking_id=16926, message_id="-OLPrlE06uD8tQ8ebJZw"
)
```

---

### Users

#### Create User
**`POST /v1/user/account`**

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | str | Yes | Email address |
| `first_name` | str | Yes | First name |
| `last_name` | str | Yes | Last name |
| `phone` | str | Yes | Phone in E.164 format |

```python
resp = client.users.create_user(
    email="jane@example.com",
    first_name="Jane",
    last_name="Doe",
    phone="+15551234567"
)
user_id = resp.data["userId"]
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Retrieve the long-lived JWT for a user. Only `access-key` is required for this call.

```python
resp = client.users.fetch_access_token(user_id=42)
token = resp.data["token"]
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | str | Yes | JWT to validate |

```python
resp = client.users.verify_jwt(token=user_token)
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```python
resp = client.properties.list_properties(service_id=1)
```

---

#### Create Property
**`POST /v1/properties`**

| Field | Type | Required | Description |
|---|---|---|---|
| `address` | str | Yes | Street address |
| `city` | str | Yes | City |
| `state` | str | Yes | State or province |
| `zip` | str | Yes | Postal / ZIP code |
| `service_id` | int | Yes | Service area ID |
| `bedrooms` | int | No | Bedroom count |
| `bathrooms` | int | No | Bathroom count |
| `notes` | str | No | Access or cleaning notes |

```python
resp = client.properties.create_property(
    address="123 Main St",
    city="Chicago",
    state="IL",
    zip_code="60601",
    service_id=1
)
property_id = resp.data["id"]
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```python
resp = client.properties.get_property(property_id=1004)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```python
resp = client.properties.update_property(
    property_id=1004,
    address="456 Elm Street",
    city="Chicago"
)
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```python
resp = client.properties.update_additional_info(
    property_id=1004,
    gate_code="1234",
    pet_info="One friendly dog"
)
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```python
resp = client.properties.enable_or_disable(property_id=1004, enabled=True)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```python
resp = client.properties.delete_property(property_id=1004)
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```python
resp = client.properties.get_ical(property_id=1004)
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```python
resp = client.properties.add_ical(
    property_id=1004,
    url="https://www.airbnb.com/calendar/ical/12345.ics?s=abc"
)
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```python
resp = client.properties.delete_ical(
    property_id=1004,
    event_ids=[101, 102, 103]
)
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```python
resp = client.properties.list_cleaners(property_id=1004)
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```python
resp = client.properties.add_cleaner(property_id=1004, cleaner_id=3)
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```python
resp = client.properties.remove_cleaner(property_id=1004, cleaner_id=3)
```

---

#### Assign Checklist to Property
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={true|false}`**

```python
resp = client.properties.set_default_checklist(
    property_id=1004,
    checklist_id=105,
    update_upcoming_bookings=True
)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```python
resp = client.checklists.list_checklists()
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```python
resp = client.checklists.get_checklist(checklist_id=105)
for item in resp.data["items"]:
    print(item["task"])
```

---

#### Create Checklist
**`POST /v1/checklist`**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | str | Yes | Checklist display name |
| `items` | list[str] | Yes | Ordered task descriptions |

```python
resp = client.checklists.create_checklist(
    name="Deep Clean",
    items=[
        "Vacuum all rooms",
        "Mop kitchen and bathroom floors",
        "Scrub toilets, sinks, and tubs",
        "Wipe all countertops",
        "Clean inside microwave and oven"
    ]
)
print("Checklist ID:", resp.data["id"])
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```python
resp = client.checklists.update_checklist(
    checklist_id=105,
    name="Standard Clean",
    items=["Vacuum", "Wipe surfaces", "Clean bathrooms"]
)
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```python
resp = client.checklists.delete_checklist(checklist_id=105)
```

---

#### Upload Checklist Image
**`POST /v1/checklist/upload-image`**

```python
with open("bathroom-guide.jpg", "rb") as f:
    image_data = f.read()

resp = client.checklists.upload_image(
    image_data=image_data,
    mime_type="image/jpeg"
)
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```python
resp = client.other.get_services()
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```python
resp = client.other.get_plans(property_id=1004)
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```python
resp = client.other.get_cleaning_extras(service_id=1)
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```python
resp = client.other.get_recommended_hours(
    property_id=1004,
    bathroom_count=2,
    room_count=3
)
print("Recommended hours:", resp.data["hours"])
```

---

#### Calculate Cost Estimate
**`POST /v1/cost-estimate`**

```python
resp = client.other.get_cost_estimate(estimate_request)
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```python
resp = client.other.get_available_cleaners(availability_request)
```

---

#### Get Coupons
**`GET /v1/coupons`**

```python
resp = client.other.get_coupons()
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```python
resp = client.blacklist.get_blacklist(page_no=1)
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```python
resp = client.blacklist.add_to_blacklist(cleaner_id=3)
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```python
resp = client.blacklist.remove_from_blacklist(cleaner_id=3)
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```python
resp = client.payment_methods.get_setup_intent_details()
client_secret = resp.data["clientSecret"]
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```python
resp = client.payment_methods.get_paypal_client_token()
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```python
resp = client.payment_methods.add_payment_method(payment_request)
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```python
resp = client.payment_methods.list_payment_methods()
for pm in resp.data:
    print(pm["type"], pm.get("last4", ""))
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```python
resp = client.payment_methods.delete_payment_method(payment_method_id=193)
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```python
resp = client.payment_methods.set_default(payment_method_id=193)
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```python
resp = client.webhooks.list_webhooks()
```

---

#### Create Webhook
**`POST /v1/webhooks`**

| Field | Type | Required | Description |
|---|---|---|---|
| `url` | str | Yes | HTTPS endpoint URL |
| `event` | str | Yes | Event type to subscribe to |

```python
resp = client.webhooks.create_webhook(
    url="https://your-server.com/hooks/cleanster",
    event="booking.status_changed"
)
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```python
resp = client.webhooks.update_webhook(
    webhook_id=50,
    url="https://your-server.com/hooks/cleanster-v2",
    event="booking.completed"
)
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```python
resp = client.webhooks.delete_webhook(webhook_id=50)
```

---

## Models Reference

### `ApiResponse`

| Attribute | Type | Description |
|---|---|---|
| `status` | int | HTTP status code |
| `message` | str | Result description |
| `data` | dict / list | Response payload |

### `Booking` (dict fields)

| Field | Type | Description |
|---|---|---|
| `id` | int | Unique booking ID |
| `status` | str | `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED` |
| `date` | str | `YYYY-MM-DD` |
| `time` | str | `HH:MM` |
| `hours` | float | Duration in hours |
| `cost` | float | Total cost in USD |
| `propertyId` | int | Property ID |
| `planId` | int | Plan ID |
| `roomCount` | int | Rooms |
| `bathroomCount` | int | Bathrooms |
| `extraSupplies` | bool | Whether cleaner brings supplies |
| `paymentMethodId` | int | Payment method charged |
| `cleaner` | dict | Assigned cleaner object (nullable) |

### `Cleaner` (dict fields)

| Field | Type | Description |
|---|---|---|
| `id` | int | Cleaner ID |
| `name` | str | Full name |
| `email` | str | Email address |
| `phone` | str | Phone number |
| `profileUrl` | str | Profile picture URL |
| `rating` | float | Average rating (1.0–5.0) |

### `Checklist` (dict fields)

| Field | Type | Description |
|---|---|---|
| `id` | int | Checklist ID |
| `name` | str | Display name |
| `items` | list | Array of checklist items |
| `items[].id` | int | Task ID |
| `items[].task` | str | Task description |
| `items[].order` | int | Display order |

### `PaymentMethod` (dict fields)

| Field | Type | Description |
|---|---|---|
| `id` | int | Payment method ID |
| `type` | str | `card` or `paypal` |
| `last4` | str | Last 4 card digits |
| `brand` | str | Card brand |
| `isDefault` | bool | Whether this is the default |

---

## Error Handling

```python
from cleanster.exceptions import CleansterApiError

try:
    resp = client.bookings.get_booking(99999)
except CleansterApiError as e:
    print(f"HTTP {e.status_code}: {e.message}")
    if e.status_code == 401:
        # Re-fetch user token and retry
        pass
    elif e.status_code == 404:
        print("Booking not found")
    elif e.status_code == 422:
        print("Validation error:", e.message)
```

| HTTP Status | Meaning |
|---|---|
| 400 | Bad request — malformed parameters |
| 401 | Unauthorized — invalid or missing credentials |
| 403 | Forbidden — insufficient permissions |
| 404 | Not found |
| 422 | Unprocessable entity — validation failed |
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
pip install pytest
pytest tests/ -v
```

Expected: **99 tests passing.**

---

## Project Structure

```
python-sdk/
├── setup.py
├── cleanster/
│   ├── __init__.py           # CleansterClient export
│   ├── client.py             # Main client class
│   ├── http_client.py        # urllib-based HTTP layer
│   ├── exceptions.py         # CleansterApiError
│   ├── models/
│   │   ├── api_response.py
│   │   ├── booking.py
│   │   ├── checklist.py
│   │   └── payment_method.py
│   └── api/
│       ├── bookings.py
│       ├── users.py
│       ├── properties.py
│       ├── checklists.py
│       ├── other.py
│       ├── blacklist.py
│       ├── payment_methods.py
│       └── webhooks.py
└── tests/
    └── test_cleanster_sdk.py
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
