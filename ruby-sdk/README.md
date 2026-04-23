# Cleanster Ruby SDK

<p align="center">
  <strong>Official Ruby client library for the Cleanster Partner API</strong><br>
  Automate residential and commercial cleaning operations — bookings, properties, cleaners, checklists, payments, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Ruby-2.7%2B-red?logo=ruby" alt="Ruby 2.7+">
  <img src="https://img.shields.io/badge/gem-cleanster-orange?logo=rubygems" alt="RubyGems">
  <img src="https://img.shields.io/badge/tests-119%20passing-brightgreen" alt="119 passing">
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

The Cleanster Ruby SDK provides a clean, idiomatic Ruby interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It uses Ruby's built-in `Net::HTTP` — zero external dependencies.

Use it to:
- **Create and manage bookings** — schedule, reschedule, cancel, adjust hours
- **Manage properties** — CRUD, iCal sync, preferred cleaner lists
- **Handle users** — create accounts and manage authorization tokens
- **Configure checklists** — create task lists and assign to bookings
- **Process payments** — Stripe and PayPal support
- **Receive webhooks** — subscribe to booking lifecycle events
- **Blacklist cleaners** — prevent specific cleaners from being assigned

---

## Requirements

- **Ruby 2.7** or later
- A Cleanster Partner account — contact [partner@cleanster.com](mailto:partner@cleanster.com) for access

---

## Installation

Add to your `Gemfile`:

```ruby
gem 'cleanster'
```

Then:

```bash
bundle install
```

Or install directly:

```bash
gem install cleanster
```

Install from source:

```bash
git clone https://github.com/cleansterhq/Cleanster-partner-api-sdk.git
cd Cleanster-partner-api-sdk/ruby-sdk
bundle install
```

---

## Authentication

Every request requires two credentials sent as HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key from Cleanster |
| `token` | A per-user JWT — long-lived, from `users.fetch_access_token(user_id)` |

### 4-Step Setup

**Step 1 — Contact Cleanster** to receive your `access-key`.

**Step 2 — Create a user account** (one-time per end-user):

```ruby
require 'cleanster'

client = Cleanster::Client.new(access_key: 'your-access-key')

resp = client.users.create_user(
  email: 'jane@example.com',
  first_name: 'Jane',
  last_name: 'Doe',
  phone: '+15551234567'
)
user_id = resp.data['userId']
```

**Step 3 — Fetch the user's access token** (store it; it is long-lived):

```ruby
resp = client.users.fetch_access_token(user_id)
user_token = resp.data['token']
```

**Step 4 — Build the client with both credentials**:

```ruby
client = Cleanster::Client.new(
  access_key: 'your-access-key',
  token: user_token
)
```

> **Token lifecycle:** Only refresh when the API returns HTTP 401.

---

## Quick Start

```ruby
require 'cleanster'

client = Cleanster::Client.new(
  access_key: 'your-access-key',
  token: 'user-jwt-token'
)

# Get recommended cleaning hours
hours = client.other.get_recommended_hours(
  property_id: 1004,
  bathroom_count: 2,
  room_count: 3
)
puts "Recommended hours: #{hours.data}"

# Create a booking
booking = client.bookings.create_booking(
  property_id: 1004,
  date: '2025-09-01',
  time: '10:00',
  plan_id: 2,
  room_count: 3,
  bathroom_count: 2,
  hours: 3.0,
  extra_supplies: false,
  payment_method_id: 10,
  coupon_code: '20POFF'   # optional — 20% off in sandbox
)
puts "Created booking: #{booking.data['id']}"

# List open bookings
list = client.bookings.list_bookings(page_no: 1, status: 'OPEN')
puts "Open bookings: #{list.data['bookings'].size}"
```

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** (default) | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

```ruby
# Sandbox (default)
client = Cleanster::Client.new(access_key: 'key', token: 'token')

# Production
client = Cleanster::Client.new(
  access_key: 'key',
  token: 'token',
  environment: :production
)
```

---

## Booking Flow

```
create_booking         →   OPEN
                               │
     bookings.assign_cleaner
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

All methods return a `Cleanster::ApiResponse` with:
- `.status` — HTTP status code
- `.message` — Human-readable result
- `.data` — Response payload (Hash or Array)

---

### Bookings

#### List Bookings
**`GET /v1/bookings?pageNo={pageNo}&status={status}`**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `page_no` | Integer | Yes | Page number (1-based) |
| `status` | String | No | `OPEN` · `CLEANER_ASSIGNED` · `COMPLETED` · `CANCELLED` · `REMOVED` |

```ruby
resp = client.bookings.list_bookings(page_no: 1, status: 'OPEN')
resp.data['bookings'].each { |b| puts "#{b['id']} - #{b['status']}" }
```

---

#### Get Booking
**`GET /v1/bookings/{bookingId}`**

```ruby
resp = client.bookings.get_booking(16926)
puts "#{resp.data['status']} on #{resp.data['date']}"
```

---

#### Create Booking
**`POST /v1/bookings/create`**

| Field | Type | Required | Description |
|---|---|---|---|
| `property_id` | Integer | Yes | Property to clean |
| `date` | String | Yes | `YYYY-MM-DD` |
| `time` | String | Yes | `HH:MM` (24-hour) |
| `plan_id` | Integer | Yes | Cleaning plan ID |
| `room_count` | Integer | Yes | Number of rooms |
| `bathroom_count` | Integer | Yes | Number of bathrooms |
| `hours` | Float | Yes | Scheduled duration |
| `extra_supplies` | Boolean | Yes | Cleaner brings supplies |
| `payment_method_id` | Integer | Yes | Payment method ID |
| `coupon_code` | String | No | Discount coupon |
| `cleaning_extras` | Array | No | Extra service IDs |

```ruby
resp = client.bookings.create_booking(
  property_id: 1004,
  date: '2025-09-01',
  time: '10:00',
  plan_id: 2,
  room_count: 3,
  bathroom_count: 2,
  hours: 3.0,
  extra_supplies: false,
  payment_method_id: 10,
  coupon_code: '50POFF'
)
puts "Booking ID: #{resp.data['id']}"
```

---

#### Assign Cleaner to Booking
**`POST /v1/bookings/{bookingId}/cleaner`**

```ruby
client.bookings.assign_cleaner(booking_id: 16926, cleaner_id: 3)
```

---

#### Remove Cleaner from Booking
**`DELETE /v1/bookings/{bookingId}/cleaner`**

```ruby
client.bookings.remove_cleaner(booking_id: 16926)
```

---

#### Adjust Booking Hours
**`POST /v1/bookings/{bookingId}/hours`**

```ruby
client.bookings.adjust_hours(booking_id: 16926, hours: 4.5)
```

---

#### Reschedule Booking
**`POST /v1/bookings/{bookingId}/reschedule`**

```ruby
client.bookings.reschedule_booking(
  booking_id: 16926, date: '2025-09-15', time: '14:00'
)
```

---

#### Pay Booking Expenses
**`POST /v1/bookings/{bookingId}/expenses`**

```ruby
client.bookings.pay_expenses(booking_id: 16926, payment_method_id: 10)
```

---

#### Get Booking Inspection
**`GET /v1/bookings/{bookingId}/inspection`**

```ruby
resp = client.bookings.get_inspection(booking_id: 16926)
```

---

#### Get Booking Inspection Details
**`GET /v1/bookings/{bookingId}/inspection/details`**

```ruby
resp = client.bookings.get_inspection_details(booking_id: 16926)
```

---

#### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**

```ruby
client.bookings.cancel_booking(booking_id: 16926, reason: 'Scheduling conflict')
```

---

#### Assign Checklist to Booking
**`PUT /v1/bookings/{bookingId}/checklist/{checklistId}`**

Override the property's default checklist for this booking only.

```ruby
client.bookings.assign_checklist_to_booking(16926, 105)
```

---

#### Submit Feedback
**`POST /v1/bookings/{bookingId}/feedback`**

```ruby
client.bookings.submit_feedback(
  booking_id: 16926, rating: 5, comment: 'Excellent work!'
)
```

---

#### Submit Tip
**`POST /v1/bookings/{bookingId}/tip`**

```ruby
client.bookings.submit_tip(
  booking_id: 16926, amount: 15.00, payment_method_id: 10
)
```

---

#### Get Chat Messages
**`GET /v1/bookings/{bookingId}/chat`**

```ruby
resp = client.bookings.get_chat(booking_id: 16926)
resp.data['messages'].each do |msg|
  puts "[#{msg['sender_type']}] #{msg['content']}"
end
```

**`messages[]` fields:**

| Field | Type | Description |
|---|---|---|
| `message_id` | String | Unique ID |
| `sender_id` | String | Reference key (e.g. `C6`, `P3`) |
| `content` | String | Text (empty for media) |
| `timestamp` | String | `DD MMM YYYY, HH:MM AM/PM` (GMT) |
| `message_type` | String | `text` or `media` |
| `attachments` | Array | Media items |
| `attachments[].type` | String | `image`, `video`, `sound` |
| `attachments[].url` | String | Media URL |
| `attachments[].thumb_url` | String | Thumbnail (nullable) |
| `is_read` | Boolean | Read status |
| `sender_type` | String | `client` · `cleaner` · `support` · `bot` |

---

#### Send Chat Message
**`POST /v1/bookings/{bookingId}/chat`**

```ruby
client.bookings.send_message(
  booking_id: 16926, message: 'Please bring extra supplies.'
)
```

---

#### Delete Chat Message
**`DELETE /v1/bookings/{bookingId}/chat/{messageId}`**

```ruby
client.bookings.delete_message(
  booking_id: 16926, message_id: '-OLPrlE06uD8tQ8ebJZw'
)
```

---

### Users

#### Create User
**`POST /v1/user/account`**

```ruby
resp = client.users.create_user(
  email: 'jane@example.com',
  first_name: 'Jane',
  last_name: 'Doe',
  phone: '+15551234567'
)
user_id = resp.data['userId']
```

---

#### Fetch Access Token
**`GET /v1/user/access-token/{userId}`**

Only `access-key` is required for this call.

```ruby
resp = client.users.fetch_access_token(42)
token = resp.data['token']
```

---

#### Verify JWT
**`POST /v1/user/verify-jwt`**

```ruby
resp = client.users.verify_jwt(user_token)
```

---

### Properties

#### List Properties
**`GET /v1/properties?serviceId={serviceId}`**

```ruby
resp = client.properties.list_properties(service_id: 1)
```

---

#### Create Property
**`POST /v1/properties`**

```ruby
resp = client.properties.create_property(
  address: '123 Main St',
  city: 'Chicago',
  state: 'IL',
  zip: '60601',
  service_id: 1
)
```

---

#### Get Property
**`GET /v1/properties/{propertyId}`**

```ruby
resp = client.properties.get_property(1004)
```

---

#### Update Property
**`PUT /v1/properties/{propertyId}`**

```ruby
client.properties.update_property(1004, address: '456 Elm St')
```

---

#### Update Additional Information
**`PUT /v1/properties/{propertyId}/additional-information`**

```ruby
client.properties.update_additional_info(
  1004,
  gate_code: '1234',
  pet_info: 'One friendly dog'
)
```

---

#### Enable or Disable Property
**`POST /v1/properties/{propertyId}/enable-disable`**

```ruby
client.properties.enable_or_disable(1004, enabled: true)
```

---

#### Delete Property
**`DELETE /v1/properties/{propertyId}`**

```ruby
client.properties.delete_property(1004)
```

---

#### Get iCal Links
**`GET /v1/properties/{propertyId}/ical`**

```ruby
resp = client.properties.get_ical(1004)
```

---

#### Add iCal Link
**`PUT /v1/properties/{propertyId}/ical`**

```ruby
client.properties.add_ical(
  1004, 'https://www.airbnb.com/calendar/ical/12345.ics'
)
```

---

#### Delete iCal Events
**`DELETE /v1/properties/{propertyId}/ical`**

```ruby
client.properties.delete_ical(1004, event_ids: [101, 102])
```

---

#### List Property Cleaners
**`GET /v1/properties/{propertyId}/cleaners`**

```ruby
resp = client.properties.list_cleaners(1004)
```

---

#### Add Preferred Cleaner
**`POST /v1/properties/{propertyId}/cleaners`**

```ruby
client.properties.add_cleaner(1004, cleaner_id: 3)
```

---

#### Remove Preferred Cleaner
**`DELETE /v1/properties/{propertyId}/cleaners/{cleanerId}`**

```ruby
client.properties.remove_cleaner(1004, 3)
```

---

#### Assign Checklist to Property
**`PUT /v1/properties/{propertyId}/checklist/{checklistId}?updateUpcomingBookings={bool}`**

```ruby
client.properties.set_default_checklist(
  1004, 105, update_upcoming_bookings: true
)
```

---

### Checklists

#### List Checklists
**`GET /v1/checklist`**

```ruby
resp = client.checklists.list_checklists
```

---

#### Get Checklist
**`GET /v1/checklist/{checklistId}`**

```ruby
resp = client.checklists.get_checklist(105)
resp.data['items'].each { |item| puts item['task'] }
```

---

#### Create Checklist
**`POST /v1/checklist`**

```ruby
resp = client.checklists.create_checklist(
  name: 'Deep Clean',
  items: [
    'Vacuum all rooms',
    'Mop kitchen and bathroom floors',
    'Scrub toilets, sinks, and tubs',
    'Wipe all countertops',
    'Clean inside microwave and oven'
  ]
)
puts "Checklist ID: #{resp.data['id']}"
```

---

#### Update Checklist
**`PUT /v1/checklist/{checklistId}`**

```ruby
client.checklists.update_checklist(
  105,
  name: 'Standard Clean',
  items: ['Vacuum', 'Wipe surfaces', 'Clean bathrooms']
)
```

---

#### Delete Checklist
**`DELETE /v1/checklist/{checklistId}`**

```ruby
client.checklists.delete_checklist(105)
```

---

#### Upload Checklist Image
**`POST /v1/checklist/upload-image`**

```ruby
image_data = File.read('bathroom-guide.jpg', mode: 'rb')
client.checklists.upload_image(image_data, 'image/jpeg')
```

---

### Other / Reference Data

#### Get Services
**`GET /v1/services`**

```ruby
resp = client.other.get_services
```

---

#### Get Plans
**`GET /v1/plans?propertyId={propertyId}`**

```ruby
resp = client.other.get_plans(property_id: 1004)
```

---

#### Get Cleaning Extras
**`GET /v1/cleaning-extras/{serviceId}`**

```ruby
resp = client.other.get_cleaning_extras(service_id: 1)
```

---

#### Get Recommended Hours
**`GET /v1/recommended-hours?propertyId={n}&bathroomCount={n}&roomCount={n}`**

```ruby
resp = client.other.get_recommended_hours(
  property_id: 1004,
  bathroom_count: 2,
  room_count: 3
)
```

---

#### Calculate Cost Estimate
**`POST /v1/cost-estimate`**

```ruby
resp = client.other.get_cost_estimate(estimate_request)
```

---

#### Get Available Cleaners
**`POST /v1/available-cleaners`**

```ruby
resp = client.other.get_available_cleaners(availability_request)
```

---

#### Get Coupons
**`GET /v1/coupons`**

```ruby
resp = client.other.get_coupons
```

---

### Blacklist

#### Get Blacklisted Cleaners
**`GET /v1/blacklist/cleaner?pageNo={pageNo}`**

```ruby
resp = client.blacklist.get_blacklist(page_no: 1)
```

---

#### Add Cleaner to Blacklist
**`POST /v1/blacklist/cleaner`**

```ruby
client.blacklist.add_to_blacklist(cleaner_id: 3)
```

---

#### Remove Cleaner from Blacklist
**`DELETE /v1/blacklist/cleaner`**

```ruby
client.blacklist.remove_from_blacklist(cleaner_id: 3)
```

---

### Payment Methods

#### Get Stripe Setup Intent Details
**`GET /v1/payment-methods/setup-intent-details`**

```ruby
resp = client.payment_methods.get_setup_intent_details
client_secret = resp.data['clientSecret']
```

---

#### Get PayPal Client Token
**`GET /v1/payment-methods/paypal-client-token`**

```ruby
resp = client.payment_methods.get_paypal_client_token
```

---

#### Add Payment Method
**`POST /v1/payment-methods`**

```ruby
resp = client.payment_methods.add_payment_method(payment_request)
```

---

#### List Payment Methods
**`GET /v1/payment-methods`**

```ruby
resp = client.payment_methods.list_payment_methods
resp.data.each { |pm| puts "#{pm['type']} #{pm['last4']}" }
```

---

#### Delete Payment Method
**`DELETE /v1/payment-methods/{id}`**

```ruby
client.payment_methods.delete_payment_method(193)
```

---

#### Set Default Payment Method
**`PUT /v1/payment-methods/{id}/default`**

```ruby
client.payment_methods.set_default(193)
```

---

### Webhooks

#### List Webhooks
**`GET /v1/webhooks`**

```ruby
resp = client.webhooks.list_webhooks
```

---

#### Create Webhook
**`POST /v1/webhooks`**

```ruby
client.webhooks.create_webhook(
  url: 'https://your-server.com/hooks/cleanster',
  event: 'booking.status_changed'
)
```

---

#### Update Webhook
**`PUT /v1/webhooks/{webhookId}`**

```ruby
client.webhooks.update_webhook(
  50,
  url: 'https://your-server.com/hooks/cleanster-v2',
  event: 'booking.completed'
)
```

---

#### Delete Webhook
**`DELETE /v1/webhooks/{webhookId}`**

```ruby
client.webhooks.delete_webhook(50)
```

---

## Models Reference

### `ApiResponse`

| Attribute | Type | Description |
|---|---|---|
| `status` | Integer | HTTP status code |
| `message` | String | Result description |
| `data` | Hash / Array | Response payload |

### Booking (Hash keys)

| Key | Type | Description |
|---|---|---|
| `id` | Integer | Unique booking ID |
| `status` | String | `OPEN` · `CLEANER_ASSIGNED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` · `REMOVED` |
| `date` | String | `YYYY-MM-DD` |
| `time` | String | `HH:MM` |
| `hours` | Float | Duration |
| `cost` | Float | Total cost USD |
| `propertyId` | Integer | Property |
| `planId` | Integer | Plan |
| `roomCount` | Integer | Rooms |
| `bathroomCount` | Integer | Bathrooms |
| `extraSupplies` | Boolean | Supplies included |
| `paymentMethodId` | Integer | Payment method |
| `cleaner` | Hash | Assigned cleaner (nullable) |

### Cleaner (Hash keys)

| Key | Type | Description |
|---|---|---|
| `id` | Integer | Cleaner ID |
| `name` | String | Full name |
| `email` | String | Email |
| `phone` | String | Phone |
| `profileUrl` | String | Profile picture |
| `rating` | Float | Average rating (1–5) |

### Checklist (Hash keys)

| Key | Type | Description |
|---|---|---|
| `id` | Integer | Checklist ID |
| `name` | String | Display name |
| `items` | Array | Task items |
| `items[].id` | Integer | Task ID |
| `items[].task` | String | Task description |
| `items[].order` | Integer | Sort order |

### PaymentMethod (Hash keys)

| Key | Type | Description |
|---|---|---|
| `id` | Integer | ID |
| `type` | String | `card` or `paypal` |
| `last4` | String | Last 4 card digits |
| `brand` | String | Card brand |
| `isDefault` | Boolean | Default method |

---

## Error Handling

```ruby
begin
  resp = client.bookings.get_booking(99999)
rescue Cleanster::ApiError => e
  puts "HTTP #{e.status_code}: #{e.message}"
  case e.status_code
  when 401
    # Re-fetch user token and retry
  when 404
    puts "Booking not found"
  when 422
    puts "Validation error: #{e.message}"
  end
end
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
bundle exec rspec
```

Expected: **119 examples, 0 failures.**

---

## Project Structure

```
ruby-sdk/
├── cleanster.gemspec
├── Gemfile
├── lib/
│   └── cleanster/
│       ├── client.rb             # Cleanster::Client
│       ├── http_client.rb        # Net::HTTP wrapper
│       ├── api_response.rb       # ApiResponse struct
│       ├── error.rb              # Cleanster::ApiError
│       └── api/
│           ├── bookings_api.rb
│           ├── users_api.rb
│           ├── properties_api.rb
│           ├── checklists_api.rb
│           ├── other_api.rb
│           ├── blacklist_api.rb
│           ├── payment_methods_api.rb
│           └── webhooks_api.rb
└── spec/
    └── cleanster_spec.rb
```

---

## License

MIT License. See [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Cleanster Partner API Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
