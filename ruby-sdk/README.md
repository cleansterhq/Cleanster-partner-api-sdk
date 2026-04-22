# Cleanster Ruby SDK

<p align="center">
  <strong>Official Ruby client library for the Cleanster Partner API</strong><br>
  Manage cleaning service bookings, properties, users, checklists, payment methods, webhooks, and more.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Ruby-2.7%2B-red?logo=ruby" alt="Ruby 2.7+">
  <img src="https://img.shields.io/badge/gem-cleanster-orange?logo=rubygems" alt="RubyGems">
  <img src="https://img.shields.io/badge/RSpec-3.x-green?logo=ruby" alt="RSpec 3.x">
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
- [Model Reference](#model-reference)
- [Sandbox vs Production](#sandbox-vs-production)
- [Test Coupon Codes](#test-coupon-codes-sandbox-only)
- [Publishing the Gem](#publishing-the-gem)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)
- [Support](#support)

---

## Overview

The Cleanster Ruby SDK provides a clean, idiomatic Ruby interface for the [Cleanster Partner API](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep). It targets Ruby 2.7+ and uses Ruby's built-in `Net::HTTP` library — no external HTTP dependencies.

**What it gives you:**

- **Idiomatic Ruby** — snake_case methods and attributes throughout; keyword arguments for every request
- **Dual-layer authentication** — partner access key + per-user bearer token, sent automatically on every request
- **Typed model objects** — `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod` — no raw hash navigation
- **`ApiResponse` wrapper** — every call returns `ApiResponse` with `#status`, `#message`, and `#data`
- **Three-level exception hierarchy** — `CleansterError` → `AuthError` (401) / `ApiError` (4xx/5xx)
- **Environment switching** — one line to swap sandbox vs production
- **Zero external HTTP dependencies** — uses Ruby's built-in `Net::HTTP` with configurable timeouts
- **Fully documented** — every method has YARD doc comments for IDE integration
- **119 RSpec unit tests** — all passing; no network access required

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Ruby | ≥ 2.7.0 |
| Bundler | ≥ 2.0 (recommended) |

> The SDK has **no runtime gem dependencies**. `Net::HTTP` and `json` are part of Ruby's standard library.

---

## Installation

### Gemfile (recommended)

```ruby
gem "cleanster", "~> 1.0"
```

Then:

```bash
bundle install
```

### Direct gem install

```bash
gem install cleanster
```

### Build from Source

```bash
git clone https://github.com/cleanster/cleanster-ruby-sdk.git
cd cleanster-ruby-sdk
bundle install
gem build cleanster.gemspec
gem install cleanster-1.0.0.gem
```

---

## Authentication

The Cleanster Partner API uses **two layers of authentication** sent as HTTP headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `access-key` | Your partner key | Identifies your partner account |
| `token` | User bearer token | Authenticates the end-user |

### Step-by-Step Authentication

**Step 1 — Initialize the client with your partner access key:**

```ruby
require "cleanster"

client = Cleanster::Client.sandbox("your-partner-access-key")
```

**Step 2 — Create or look up a user. For new users:**

```ruby
response = client.users.create_user(
  email:      "jane@example.com",
  first_name: "Jane",
  last_name:  "Smith",
  phone:      "+15551234567"  # optional
)
user = response.data           # Cleanster::Models::User
puts "Created user ##{user.id}"
```

**Step 3 — Fetch the user's long-lived bearer token:**

```ruby
token_response = client.users.fetch_access_token(user.id)
user_token = token_response.data.token
```

**Step 4 — Set the token on the client** for all subsequent calls:

```ruby
client.access_token = user_token
# Every API call from this point forward includes the user token automatically
```

> **Tip:** The user token is long-lived. Store it securely in your database and call `client.access_token =` at the start of each session — no need to re-fetch each time.

---

## Quick Start

```ruby
require "cleanster"

# 1. Initialize (sandbox for dev, production for live)
client = Cleanster::Client.sandbox(ENV["CLEANSTER_API_KEY"])

# 2. Create a user
response = client.users.create_user(
  email:      "jane@example.com",
  first_name: "Jane",
  last_name:  "Smith"
)
user = response.data

# 3. Authenticate subsequent calls with the user's token
token_response = client.users.fetch_access_token(user.id)
client.access_token = token_response.data.token

# 4. Add a property
property_response = client.properties.add_property(
  name:           "Beach House",
  address:        "123 Ocean Drive",
  city:           "Miami",
  country:        "USA",
  room_count:     3,
  bathroom_count: 2,
  service_id:     1
)
property = property_response.data  # Cleanster::Models::Property

# 5. Get recommended cleaning hours for the property
client.other.get_recommended_hours(property.id, bathroom_count: 2, room_count: 3)

# 6. Estimate cost
client.other.calculate_cost(
  property_id: property.id,
  plan_id:     2,
  hours:       3,
  coupon_code: "20POFF"  # optional — sandbox test coupon
)

# 7. Create a booking
booking_response = client.bookings.create_booking(
  date:              "2025-06-15",
  time:              "10:00",
  property_id:       property.id,
  room_count:        3,
  bathroom_count:    2,
  plan_id:           2,
  hours:             3,
  extra_supplies:    false,
  payment_method_id: 10
)
booking = booking_response.data  # Cleanster::Models::Booking
puts "Created booking ##{booking.id} — status: #{booking.status}"

# 8. List all bookings
bookings_response = client.bookings.get_bookings
puts "#{bookings_response.status}: #{bookings_response.message}"
```

---

## Configuration

### Factory Methods (Recommended)

```ruby
require "cleanster"

# Sandbox — for development and testing (no real charges or cleaners)
client = Cleanster::Client.sandbox("your-access-key")

# Production — for live use (real cleaners, real charges)
client = Cleanster::Client.production("your-access-key")
```

### Builder Pattern (Custom Configuration)

```ruby
config = Cleanster::Config.builder("your-access-key")
           .sandbox             # or .production
           .open_timeout(10)   # TCP connection timeout in seconds (default: 10)
           .read_timeout(60)   # Response read timeout in seconds (default: 30)
           .build

client = Cleanster::Client.new(config)
```

### Custom Base URL

```ruby
config = Cleanster::Config.builder("your-access-key")
           .base_url("https://your-proxy.example.com/api")
           .build
```

### Direct Config Initialization

```ruby
config = Cleanster::Config.new(
  access_key:   "your-key",
  base_url:     Cleanster::SANDBOX_BASE_URL,
  open_timeout: 10,
  read_timeout: 45
)
```

### Environment Base URLs

| Environment | Base URL |
|-------------|----------|
| Sandbox | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| Production | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

---

## API Reference

Every method returns a `Cleanster::Models::ApiResponse` instance. Access the result via `.data` and the response metadata via `.status` and `.message`. See [Response Structure](#response-structure) for full details.

---

### Bookings (`client.bookings`)

The bookings API manages the full lifecycle of a cleaning appointment.

#### `get_bookings(page_no: nil, status: nil)`

Retrieve a paginated list of bookings. All keyword arguments are optional.

```ruby
# All bookings
result = client.bookings.get_bookings

# Filter by status
completed = client.bookings.get_bookings(status: "COMPLETED")

# Page 2 of open bookings
page2 = client.bookings.get_bookings(page_no: 2, status: "OPEN")
```

**Valid status values:** `"OPEN"` | `"CLEANER_ASSIGNED"` | `"COMPLETED"` | `"CANCELLED"` | `"REMOVED"`

---

#### `create_booking(request_hash)`

Schedule a new cleaning appointment.

```ruby
booking_response = client.bookings.create_booking(
  date:              "2025-06-15",   # Required — YYYY-MM-DD
  time:              "10:00",        # Required — HH:mm (24-hour)
  property_id:       1004,           # Required
  room_count:        2,              # Required
  bathroom_count:    1,              # Required
  plan_id:           5,              # Required — from get_plans
  hours:             3,              # Required — from get_recommended_hours
  extra_supplies:    false,          # Required — include cleaning supplies?
  payment_method_id: 10,            # Required
  coupon_code:       "20POFF",      # Optional
  extras:            [101, 102]     # Optional — extra service IDs
)

booking = booking_response.data     # Cleanster::Models::Booking
puts booking.id                      # Integer
puts booking.status                  # "OPEN"
puts booking.cost                    # Float
```

---

#### `get_booking_details(booking_id)`

```ruby
booking = client.bookings.get_booking_details(16926).data
puts booking.date          # "2025-06-15"
puts booking.hours         # 3
puts booking.cleaner_id    # Integer or nil
puts booking.cost          # Float
```

---

#### `cancel_booking(booking_id, reason: nil)`

```ruby
# With a cancellation reason
client.bookings.cancel_booking(16459, reason: "Changed my schedule")

# Without a reason (reason is optional)
client.bookings.cancel_booking(16459)
```

---

#### `reschedule_booking(booking_id, date:, time:)`

```ruby
client.bookings.reschedule_booking(16459, date: "2025-07-01", time: "14:00")
```

---

#### `assign_cleaner(booking_id, cleaner_id:)` / `remove_assigned_cleaner(booking_id)`

```ruby
# Assign a specific cleaner
client.bookings.assign_cleaner(16459, cleaner_id: 5)

# Remove the assigned cleaner
client.bookings.remove_assigned_cleaner(16459)
```

---

#### `adjust_hours(booking_id, hours:)`

```ruby
client.bookings.adjust_hours(16459, hours: 4.0)
```

---

#### `pay_expenses(booking_id, payment_method_id:)`

Pay outstanding expenses within 72 hours of booking completion.

```ruby
client.bookings.pay_expenses(16926, payment_method_id: 10)
```

---

#### `get_booking_inspection(booking_id)` / `get_booking_inspection_details(booking_id)`

```ruby
inspection         = client.bookings.get_booking_inspection(16926)
inspection_details = client.bookings.get_booking_inspection_details(16926)
```

---

#### `assign_checklist_to_booking(booking_id, checklist_id)`

Override the property's default checklist for this specific booking.

```ruby
client.bookings.assign_checklist_to_booking(16926, 105)
```

---

#### `submit_feedback(booking_id, rating:, comment: nil)`

Submit a star rating (1–5) and optional comment after a booking completes.

```ruby
client.bookings.submit_feedback(16926, rating: 5, comment: "Excellent — very thorough!")
client.bookings.submit_feedback(16926, rating: 4)  # comment is optional
```

---

#### `add_tip(booking_id, amount:, payment_method_id:)`

Add a tip for the cleaner (within 72 hours of booking completion).

```ruby
client.bookings.add_tip(16926, amount: 20.0, payment_method_id: 10)
```

---

#### Chat — `get_chat`, `send_message`, `delete_message`

```ruby
# Get all messages in a booking thread
chat = client.bookings.get_chat(17142)

# Send a message
client.bookings.send_message(17142, message: "Please focus on the kitchen today.")

# Delete a specific message
client.bookings.delete_message(17142, "msg-abc-123")
```

---

**Bookings API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_bookings(page_no:, status:)` | GET | `/v1/bookings` |
| `create_booking(request)` | POST | `/v1/bookings/create` |
| `get_booking_details(id)` | GET | `/v1/bookings/{id}` |
| `cancel_booking(id, reason:)` | POST | `/v1/bookings/{id}/cancel` |
| `reschedule_booking(id, date:, time:)` | POST | `/v1/bookings/{id}/reschedule` |
| `assign_cleaner(id, cleaner_id:)` | POST | `/v1/bookings/{id}/cleaner` |
| `remove_assigned_cleaner(id)` | DELETE | `/v1/bookings/{id}/cleaner` |
| `adjust_hours(id, hours:)` | POST | `/v1/bookings/{id}/hours` |
| `pay_expenses(id, payment_method_id:)` | POST | `/v1/bookings/{id}/expenses` |
| `get_booking_inspection(id)` | GET | `/v1/bookings/{id}/inspection` |
| `get_booking_inspection_details(id)` | GET | `/v1/bookings/{id}/inspection/details` |
| `assign_checklist_to_booking(id, cid)` | POST | `/v1/bookings/{id}/checklist/{cid}` |
| `submit_feedback(id, rating:, comment:)` | POST | `/v1/bookings/{id}/feedback` |
| `add_tip(id, amount:, payment_method_id:)` | POST | `/v1/bookings/{id}/tip` |
| `get_chat(id)` | GET | `/v1/bookings/{id}/chat` |
| `send_message(id, message:)` | POST | `/v1/bookings/{id}/chat` |
| `delete_message(id, message_id)` | DELETE | `/v1/bookings/{id}/chat/{message_id}` |

---

### Users (`client.users`)

#### `create_user(email:, first_name:, last_name:, phone: nil)`

```ruby
response = client.users.create_user(
  email:      "jane@example.com",
  first_name: "Jane",
  last_name:  "Smith",
  phone:      "+15551234567"  # optional
)

user = response.data    # Cleanster::Models::User
puts user.id            # Integer
puts user.email         # String
puts user.first_name    # String
```

---

#### `fetch_access_token(user_id)`

Fetch the long-lived bearer token. Store it and reuse it across sessions.

```ruby
response = client.users.fetch_access_token(user.id)
token = response.data.token    # String

# Authenticate all subsequent requests:
client.access_token = token
```

---

#### `verify_jwt(token:)`

```ruby
result = client.users.verify_jwt(token: "eyJhbGci...")
puts result.status  # 200 if valid
```

---

**Users API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `create_user(email:, first_name:, last_name:, phone:)` | POST | `/v1/user/account` |
| `fetch_access_token(user_id)` | GET | `/v1/user/access-token/{user_id}` |
| `verify_jwt(token:)` | POST | `/v1/user/verify-jwt` |

---

### Properties (`client.properties`)

#### `list_properties(service_id: nil)`

```ruby
# All properties
all_props = client.properties.list_properties

# Only residential properties (service_id = 1)
residential = client.properties.list_properties(service_id: 1)
```

---

#### `add_property(request_hash)`

```ruby
response = client.properties.add_property(
  name:           "Downtown Condo",
  address:        "456 Main St",
  city:           "Toronto",
  country:        "Canada",
  room_count:     2,
  bathroom_count: 1,
  service_id:     1
)

property = response.data    # Cleanster::Models::Property
puts property.id            # Integer
puts property.name          # String
puts property.city          # String
```

---

#### CRUD Operations

```ruby
# Get
property = client.properties.get_property(1040).data

# Update
client.properties.update_property(1040,
  name:           "Renovated Condo",
  address:        "456 Main St",
  city:           "Toronto",
  country:        "Canada",
  room_count:     3,
  bathroom_count: 1,
  service_id:     1
)

# Enable or disable
client.properties.enable_or_disable_property(1040, enabled: false)

# Delete
client.properties.delete_property(1040)
```

---

#### Property Cleaners

```ruby
# List assigned cleaners
cleaners = client.properties.get_property_cleaners(1040)

# Assign a cleaner
client.properties.assign_cleaner_to_property(1040, cleaner_id: 5)

# Unassign a cleaner
client.properties.unassign_cleaner_from_property(1040, 5)
```

---

#### iCal Calendar Integration

Sync a property's availability with Airbnb, VRBO, or any iCal-compatible calendar.

```ruby
# Add iCal link
client.properties.add_ical_link(1040, ical_link: "https://calendar.example.com/feed.ics")

# Get current iCal link
ical = client.properties.get_ical_link(1040)

# Remove iCal link
client.properties.remove_ical_link(1040, ical_link: "https://calendar.example.com/feed.ics")
```

---

#### `assign_checklist_to_property(property_id, checklist_id, update_upcoming_bookings: false)`

```ruby
# Assign and apply to all upcoming bookings at this property
client.properties.assign_checklist_to_property(1040, 105, update_upcoming_bookings: true)
```

---

**Properties API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_properties(service_id:)` | GET | `/v1/properties` |
| `add_property(request)` | POST | `/v1/properties` |
| `get_property(id)` | GET | `/v1/properties/{id}` |
| `update_property(id, request)` | PUT | `/v1/properties/{id}` |
| `update_additional_information(id, data)` | PUT | `/v1/properties/{id}/additional-information` |
| `enable_or_disable_property(id, enabled:)` | POST | `/v1/properties/{id}/enable-disable` |
| `delete_property(id)` | DELETE | `/v1/properties/{id}` |
| `get_property_cleaners(id)` | GET | `/v1/properties/{id}/cleaners` |
| `assign_cleaner_to_property(id, cleaner_id:)` | POST | `/v1/properties/{id}/cleaners` |
| `unassign_cleaner_from_property(id, cleaner_id)` | DELETE | `/v1/properties/{id}/cleaners/{cid}` |
| `add_ical_link(id, ical_link:)` | PUT | `/v1/properties/{id}/ical` |
| `get_ical_link(id)` | GET | `/v1/properties/{id}/ical` |
| `remove_ical_link(id, ical_link:)` | DELETE | `/v1/properties/{id}/ical` |
| `assign_checklist_to_property(id, cid, update_upcoming_bookings:)` | PUT | `/v1/properties/{id}/checklist/{cid}` |

---

### Checklists (`client.checklists`)

Checklists define the tasks a cleaner must complete during a booking. They can be set as property defaults or overridden per booking.

#### `list_checklists`

```ruby
all = client.checklists.list_checklists
```

---

#### `get_checklist(checklist_id)`

```ruby
checklist = client.checklists.get_checklist(105).data  # Cleanster::Models::Checklist
puts checklist.name
checklist.items.each do |item|   # Array<ChecklistItem>
  mark = item.is_completed ? "✓" : " "
  puts "[#{mark}] #{item.description}"
end
```

---

#### `create_checklist(name:, items:)`

```ruby
checklist = client.checklists.create_checklist(
  name:  "Standard Residential Clean",
  items: [
    "Vacuum all floors",
    "Mop kitchen and bathroom floors",
    "Wipe all countertops",
    "Scrub toilets, sinks, and tubs",
    "Empty all trash bins",
    "Wipe mirrors and glass surfaces"
  ]
).data
puts "Created checklist ##{checklist.id}"
```

---

#### `update_checklist(checklist_id, name:, items:)` / `delete_checklist(checklist_id)`

```ruby
# Update
client.checklists.update_checklist(105,
  name:  "Deep Clean",
  items: ["All standard tasks", "Inside oven", "Inside fridge"]
)

# Delete
client.checklists.delete_checklist(105)
```

---

**Checklists API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_checklists` | GET | `/v1/checklist` |
| `get_checklist(id)` | GET | `/v1/checklist/{id}` |
| `create_checklist(name:, items:)` | POST | `/v1/checklist` |
| `update_checklist(id, name:, items:)` | PUT | `/v1/checklist/{id}` |
| `delete_checklist(id)` | DELETE | `/v1/checklist/{id}` |

---

### Other / Utilities (`client.other`)

Reference data used when building booking flows.

#### `get_services`

Returns all available cleaning service types.

```ruby
services = client.other.get_services.data
```

---

#### `get_plans(property_id)`

Returns available booking plans for a given property.

```ruby
plans = client.other.get_plans(1004).data
```

---

#### `get_recommended_hours(property_id, bathroom_count:, room_count:)`

Returns the system-recommended cleaning duration based on property size. Use this to pre-fill the `hours` field.

```ruby
rec = client.other.get_recommended_hours(1004, bathroom_count: 2, room_count: 3)
```

---

#### `calculate_cost(request_hash)`

Calculate the estimated booking price before committing.

```ruby
estimate = client.other.calculate_cost(
  property_id: 1004,
  plan_id:     2,
  hours:       3,
  coupon_code: "20POFF"   # optional
)
```

---

#### `get_cleaning_extras(service_id)`

Returns available add-on services (inside fridge, inside oven, laundry, etc.).

```ruby
extras = client.other.get_cleaning_extras(1).data
```

---

#### `get_available_cleaners(request_hash)`

Find cleaners available for a specific property, date, and time slot.

```ruby
cleaners = client.other.get_available_cleaners(
  property_id: 1004,
  date:        "2025-06-15",
  time:        "10:00"
)
```

---

#### `get_coupons`

Returns all valid coupon codes.

```ruby
coupons = client.other.get_coupons.data
```

---

**Other API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_services` | GET | `/v1/services` |
| `get_plans(property_id)` | GET | `/v1/plans?propertyId={id}` |
| `get_recommended_hours(pid, bathroom_count:, room_count:)` | GET | `/v1/recommended-hours` |
| `calculate_cost(request)` | POST | `/v1/cost-estimate` |
| `get_cleaning_extras(service_id)` | GET | `/v1/cleaning-extras/{service_id}` |
| `get_available_cleaners(request)` | POST | `/v1/available-cleaners` |
| `get_coupons` | GET | `/v1/coupons` |

---

### Blacklist (`client.blacklist`)

Prevent specific cleaners from being auto-assigned to your bookings.

```ruby
# List all blacklisted cleaners
list = client.blacklist.list_blacklisted_cleaners.data

# Add a cleaner to the blacklist
client.blacklist.add_to_blacklist(
  cleaner_id: 7,
  reason:     "Damaged furniture during last booking"  # optional
)

# Remove a cleaner from the blacklist
client.blacklist.remove_from_blacklist(cleaner_id: 7)
```

**Blacklist API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_blacklisted_cleaners` | GET | `/v1/blacklist/cleaner` |
| `add_to_blacklist(cleaner_id:, reason:)` | POST | `/v1/blacklist/cleaner` |
| `remove_from_blacklist(cleaner_id:)` | DELETE | `/v1/blacklist/cleaner` |

---

### Payment Methods (`client.payment_methods`)

Manage Stripe and PayPal payment methods for your users.

```ruby
# Stripe — get setup intent for client-side card collection
intent = client.payment_methods.get_setup_intent_details.data
# Use intent["clientSecret"] with Stripe.js on the client side

# PayPal — get client token for PayPal button rendering
paypal = client.payment_methods.get_paypal_client_token.data

# Add a payment method (after client-side tokenization)
client.payment_methods.add_payment_method("paymentMethodId" => "pm_xxxxxxxxxxxx")

# List all saved payment methods
methods = client.payment_methods.get_payment_methods.data

# Set default payment method
client.payment_methods.set_default_payment_method(193)

# Delete a payment method
client.payment_methods.delete_payment_method(193)
```

**Payment Methods API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `get_setup_intent_details` | GET | `/v1/payment-methods/setup-intent` |
| `get_paypal_client_token` | GET | `/v1/payment-methods/paypal-client-token` |
| `add_payment_method(request)` | POST | `/v1/payment-methods` |
| `get_payment_methods` | GET | `/v1/payment-methods` |
| `delete_payment_method(id)` | DELETE | `/v1/payment-methods/{id}` |
| `set_default_payment_method(id)` | PUT | `/v1/payment-methods/{id}/default` |

---

### Webhooks (`client.webhooks`)

Receive real-time notifications when booking events occur — no polling required.

```ruby
# List all configured webhook endpoints
hooks = client.webhooks.list_webhooks.data

# Register a new webhook
hook = client.webhooks.create_webhook(
  "url"   => "https://your-app.com/webhooks/cleanster",
  "event" => "booking.status_changed"
).data

# Update a webhook
client.webhooks.update_webhook(50,
  "url"   => "https://your-app.com/v2/webhooks",
  "event" => "booking.status_changed"
)

# Delete a webhook
client.webhooks.delete_webhook(50)
```

**Webhooks API Summary**

| Method | HTTP | Endpoint |
|--------|------|----------|
| `list_webhooks` | GET | `/v1/webhooks` |
| `create_webhook(request)` | POST | `/v1/webhooks` |
| `update_webhook(id, request)` | PUT | `/v1/webhooks/{id}` |
| `delete_webhook(id)` | DELETE | `/v1/webhooks/{id}` |

---

## Error Handling

All SDK methods raise Ruby exceptions on failure. Rescue the most specific class first.

```ruby
require "cleanster"

client = Cleanster::Client.sandbox("your-key")
client.access_token = "user-token"

begin
  booking = client.bookings.get_booking_details(99999).data

rescue Cleanster::AuthError => e
  # HTTP 401 — bad access key or user token
  puts "Auth failed [#{e.status_code}]: #{e.message}"
  puts "Response body: #{e.response_body}"
  # Prompt the user to re-authenticate

rescue Cleanster::ApiError => e
  # HTTP 4xx / 5xx — API-level error (other than 401)
  puts "API error [#{e.status_code}]: #{e.message}"
  puts "Response body: #{e.response_body}"

  case e.status_code
  when 404
    puts "Resource not found."
  when 422
    puts "Validation error — check your request fields."
  when 500..599
    puts "Server error — retry after a short delay."
  end

rescue Cleanster::CleansterError => e
  # Network failure, timeout, or JSON parse error
  puts "SDK/network error: #{e.message}"

rescue => e
  # Unknown error — re-raise
  raise
end
```

### Exception Hierarchy

```
StandardError
└── Cleanster::CleansterError          ← base SDK error (network, timeout, JSON parse)
    ├── Cleanster::AuthError           ← HTTP 401 (invalid/missing credentials)
    └── Cleanster::ApiError            ← HTTP 4xx/5xx (API-level errors, not 401)
```

| Exception | When Raised | Key Attributes |
|-----------|-------------|----------------|
| `Cleanster::CleansterError` | Network error, timeout, JSON parse failure | `message` |
| `Cleanster::AuthError` | HTTP 401 | `status_code` (always `401`), `response_body` |
| `Cleanster::ApiError` | HTTP 4xx/5xx (not 401) | `status_code`, `message`, `response_body` |

---

## Response Structure

Every SDK method returns a `Cleanster::Models::ApiResponse` instance.

```ruby
class ApiResponse
  attr_reader :status   # Integer — HTTP-style status code (e.g., 200)
  attr_reader :message  # String  — Human-readable status (e.g., "OK")
  attr_reader :data     # Object  — Parsed response payload (model object or raw Hash/Array)
end
```

**Usage patterns:**

```ruby
# Full access
response = client.bookings.get_booking_details(16926)
puts response.status      # 200
puts response.message     # "OK"
booking = response.data   # Cleanster::Models::Booking

# Chain directly to data
booking = client.bookings.get_booking_details(16926).data
puts booking.id           # 16926
puts booking.status       # "COMPLETED"
puts booking.date         # "2025-06-15"
puts booking.cost         # 150.0
```

---

## Model Reference

### `Cleanster::Models::Booking`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | Booking ID |
| `status` | `String` | `"OPEN"` / `"CLEANER_ASSIGNED"` / `"COMPLETED"` / `"CANCELLED"` / `"REMOVED"` |
| `date` | `String` | Booking date (YYYY-MM-DD) |
| `time` | `String` | Start time (HH:mm) |
| `hours` | `Numeric` | Duration in hours |
| `cost` | `Numeric` | Total cost |
| `property_id` | `Integer` | Associated property ID |
| `cleaner_id` | `Integer \| nil` | Assigned cleaner (`nil` if unassigned) |
| `plan_id` | `Integer` | Booking plan ID |
| `room_count` | `Integer` | Number of rooms |
| `bathroom_count` | `Integer` | Number of bathrooms |
| `extra_supplies` | `Boolean` | Whether cleaning supplies are included |
| `payment_method_id` | `Integer` | Payment method ID |
| `raw` | `Hash` | The original parsed JSON hash |

### `Cleanster::Models::User`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | User ID |
| `email` | `String` | Email address |
| `first_name` | `String` | First name |
| `last_name` | `String` | Last name |
| `phone` | `String \| nil` | Phone number (optional) |
| `token` | `String \| nil` | Bearer token — only present after `fetch_access_token` |
| `raw` | `Hash` | The original parsed JSON hash |

### `Cleanster::Models::Property`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | Property ID |
| `name` | `String` | Property name/label |
| `address` | `String` | Street address |
| `city` | `String` | City |
| `country` | `String` | Country |
| `room_count` | `Integer` | Number of bedrooms/rooms |
| `bathroom_count` | `Integer` | Number of bathrooms |
| `service_id` | `Integer` | Service type ID |
| `is_enabled` | `Boolean \| nil` | Whether the property is active |
| `raw` | `Hash` | The original parsed JSON hash |

### `Cleanster::Models::Checklist`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | Checklist ID |
| `name` | `String` | Checklist name |
| `items` | `Array<ChecklistItem>` | Task items |
| `raw` | `Hash` | The original parsed JSON hash |

### `Cleanster::Models::ChecklistItem`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | Item ID |
| `description` | `String` | Task description |
| `is_completed` | `Boolean` | Whether the cleaner marked it complete |
| `image_url` | `String \| nil` | Proof photo URL (if uploaded by cleaner) |
| `raw` | `Hash` | The original parsed JSON hash |

### `Cleanster::Models::PaymentMethod`

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `Integer` | Payment method ID |
| `type` | `String` | `"card"` / `"paypal"` / etc. |
| `last_four` | `String \| nil` | Last 4 digits (cards only) |
| `brand` | `String \| nil` | Card brand (`"visa"`, `"mastercard"`, etc.) |
| `is_default` | `Boolean` | Whether this is the default payment method |
| `raw` | `Hash` | The original parsed JSON hash |

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

```ruby
# Development / CI
client = Cleanster::Client.sandbox(ENV["CLEANSTER_API_KEY"])

# Production
client = Cleanster::Client.production(ENV["CLEANSTER_API_KEY"])
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

Pass the code in the `coupon_code` key of `create_booking` or `calculate_cost`.

---

## Publishing the Gem

### Build

```bash
gem build cleanster.gemspec
# → cleanster-1.0.0.gem
```

### Validate

```bash
gem specification cleanster-1.0.0.gem
```

### Push to RubyGems.org

```bash
gem push cleanster-1.0.0.gem
```

You must be authenticated: `gem signin` or set the `GEM_HOST_API_KEY` environment variable.

### Version bumping

Edit `lib/cleanster/version.rb`:

```ruby
module Cleanster
  VERSION = "1.1.0".freeze
end
```

Then rebuild and push.

---

## Running Tests

The test suite contains **119 RSpec examples** — all passing. Tests stub `Cleanster::HttpClient` using RSpec `instance_double` — no network access or real API keys are needed.

### Setup

```bash
git clone https://github.com/cleanster/cleanster-ruby-sdk.git
cd cleanster-ruby-sdk
bundle install
```

### Run Tests

```bash
# Run all tests with documentation format
bundle exec rspec

# Run with progress format (dots)
bundle exec rspec --format progress

# Run a specific example group
bundle exec rspec spec/cleanster_spec.rb -e "BookingsApi"

# Run with coverage (requires simplecov)
COVERAGE=true bundle exec rspec

# Run via Rake
bundle exec rake spec
```

### Test Coverage

| Area | Examples |
|------|----------|
| `Cleanster::Config` | 9 — URL assignment, blank key rejection, builder, custom timeouts |
| `Cleanster::Config::Builder` | 5 — sandbox, production, timeouts, custom base URL |
| `Cleanster::Client` | 13 — factory methods, all 8 API namespaces, token get/set/clear |
| `BookingsApi` | 21 — all 17 methods + edge cases (optional reason, optional comment, no params) |
| `UsersApi` | 5 — create (with/without phone), fetch_access_token, verify_jwt |
| `PropertiesApi` | 15 — CRUD, enable/disable, cleaners, iCal, checklist (true/false), serviceId filter |
| `ChecklistsApi` | 5 — list, get (with typed items), create, update, delete |
| `OtherApi` | 7 — all 7 utility endpoints |
| `BlacklistApi` | 4 — list, add (with/without reason), remove |
| `PaymentMethodsApi` | 6 — all 6 methods |
| `WebhooksApi` | 4 — list, create, update, delete |
| Exceptions | 10 — status_code, message, response_body, class hierarchy, propagation |
| Models | 15 — field mapping for all 5 models + ApiResponse (hash/list/model_class) |

---

## Project Structure

```
cleanster-ruby-sdk/
├── lib/
│   ├── cleanster.rb                     ← Main require entry point
│   └── cleanster/
│       ├── version.rb                   ← VERSION constant
│       ├── exceptions.rb                ← CleansterError / AuthError / ApiError
│       ├── config.rb                    ← Config + Config::Builder
│       ├── http_client.rb               ← Net::HTTP transport layer
│       ├── client.rb                    ← CleansterClient (main entry point)
│       ├── api/
│       │   ├── bookings_api.rb          ← 17 booking methods
│       │   ├── users_api.rb             ← 3 user methods
│       │   ├── properties_api.rb        ← 14 property methods
│       │   ├── checklists_api.rb        ← 5 checklist methods
│       │   ├── other_api.rb             ← 7 utility methods
│       │   ├── blacklist_api.rb         ← 3 blacklist methods
│       │   ├── payment_methods_api.rb   ← 6 payment method methods
│       │   └── webhooks_api.rb          ← 4 webhook methods
│       └── models/
│           ├── api_response.rb          ← ApiResponse wrapper
│           ├── booking.rb               ← Booking model
│           ├── user.rb                  ← User model
│           ├── property.rb              ← Property model
│           ├── checklist.rb             ← Checklist + ChecklistItem models
│           └── payment_method.rb        ← PaymentMethod model
├── spec/
│   ├── spec_helper.rb                   ← RSpec configuration
│   └── cleanster_spec.rb                ← 119 RSpec examples
├── cleanster.gemspec                     ← Gem specification
├── Gemfile                              ← Bundler dependency file
├── Rakefile                             ← `rake spec` task
├── README.md
├── LICENSE
├── CHANGELOG.md
└── .gitignore
```

---

## Contributing

1. Fork the repository on GitHub.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and add RSpec tests.
4. Ensure all tests pass: `bundle exec rspec`
5. Submit a pull request with a clear description of your changes.

### Code Style

This project follows standard Ruby conventions:
- 2-space indentation
- `snake_case` method and variable names
- YARD doc comments on all public methods
- Keyword arguments for all public API methods

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
| GitHub Issues | https://github.com/cleanster/cleanster-ruby-sdk/issues |
| RubyGems | https://rubygems.org/gems/cleanster |

---

*Made with care for the Cleanster partner ecosystem.*
