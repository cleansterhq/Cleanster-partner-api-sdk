# Cleanster — Make.com Integration

A production-ready custom app for [Make.com](https://make.com) (formerly Integromat) that connects the Cleanster Partner API to 1,000+ apps. Automate bookings, properties, cleaners, and notifications with powerful multi-step scenarios.

---

## What's Included

### Triggers (Watch for new events)
| Module | Description |
|---|---|
| **Watch New Bookings** | Fires when a new booking is created |
| **Watch Booking Status Changed** | Fires when a booking changes status (with optional status filter) |
| **Watch New Properties** | Fires when a new property is added |

### Actions (Bookings)
| Module | Description |
|---|---|
| **Create a Booking** | Creates a new cleaning booking |
| **Cancel a Booking** | Cancels an existing booking |
| **Reschedule a Booking** | Moves a booking to a new date and time |
| **Assign Cleaner to Booking** | Assigns a cleaner to an existing booking |
| **Send Chat Message** | Sends a message in the booking chat thread |

### Actions (Properties)
| Module | Description |
|---|---|
| **Create a Property** | Adds a new property to your account |
| **Update a Property** | Updates an existing property's details |

### Search / Lookup Modules
| Module | Description |
|---|---|
| **Get a Booking** | Retrieves a single booking by ID |
| **Search Bookings** | Finds bookings by status, property, or date range |
| **Get a Property** | Retrieves a single property by ID |
| **Search Properties** | Searches properties by name or address |
| **Get a Cleaner** | Retrieves a single cleaner by ID |
| **Search Cleaners** | Searches cleaners by name, email, or status |
| **Get Available Services** | Lists all available cleaning service types |
| **Make an API Call** | Generic authenticated call to any Cleanster endpoint |

---

## File Structure

```
make-app/
├── app.json                  ← App metadata (name, version, description)
├── base.json                 ← Base URL and auth headers
│
├── connections/
│   └── apikey.json           ← API key authentication
│
├── modules/
│   ├── watchNewBookings.json
│   ├── watchBookingStatusChanged.json
│   ├── watchNewProperties.json
│   ├── createBooking.json
│   ├── cancelBooking.json
│   ├── rescheduleBooking.json
│   ├── assignCleaner.json
│   ├── sendMessage.json
│   ├── createProperty.json
│   ├── updateProperty.json
│   ├── getBooking.json
│   ├── searchBooking.json
│   ├── getProperty.json
│   ├── searchProperty.json
│   ├── getCleaner.json
│   ├── searchCleaner.json
│   ├── getServices.json
│   └── makeAPICall.json
│
├── rpcs/
│   ├── listProperties.json   ← Dynamic property dropdown
│   ├── listCleaners.json     ← Dynamic cleaner dropdown
│   ├── listChecklists.json   ← Dynamic checklist dropdown
│   └── listServices.json     ← Dynamic service type dropdown
│
└── groups/
    ├── bookings.json
    ├── properties.json
    ├── cleaners.json
    └── other.json
```

---

## Deploying to Make.com

### Option 1: Make App Builder (Recommended)

1. Log in to [Make.com](https://make.com) and go to **Your Apps → New App**
2. Fill in the app name `Cleanster` and description from `app.json`
3. Set the **Base URL** to `https://api.cleanster.com`
4. Under **Connections**, create an `apikey` connection using `connections/apikey.json`
5. For each file in `modules/`, click **New Module** and paste the JSON
6. For each file in `rpcs/`, click **New RPC** and paste the JSON
7. **Save** and click **Test** to verify the connection

### Option 2: Make CLI (Advanced)

If you have access to the Make SDK CLI:

```bash
cd make-app
npm install -g @make-app-sdk/cli
make-sdk login
make-sdk deploy
```

### Option 3: Import via JSON

Make.com supports importing full app definitions via their App Builder API. Each module JSON can be imported directly through the UI under the **Import** option in the module editor.

---

## Authentication

This app uses **API Key** authentication.

When a user connects to Cleanster in Make:
1. They are asked for their **API Key** (found in Cleanster dashboard → Settings → API)
2. The key is validated against `GET /v1/services`
3. Every subsequent API call includes the header: `Authorization: Bearer <api_key>`

---

## Module Details

### Trigger Behavior (Polling)

All three trigger modules use **polling** — Make checks for new items at regular intervals. The polling frequency depends on your Make plan:
- Free/Core: every 15 minutes
- Pro+: every 1 minute

Each trigger uses a **cursor** (the record's `created_at` or `updated_at` field) so Make only processes genuinely new records on each poll.

### Dynamic Dropdowns (RPCs)

The following fields automatically populate from your live Cleanster data:
- **Property** dropdown → calls `/v1/properties` (100 results)
- **Cleaner** dropdown → calls `/v1/cleaners?status=active` (100 results)
- **Checklist** dropdown → calls `/v1/checklists` (100 results)
- **Service Type** dropdown → uses static options (standard, deep, move_in_out, turnover, post_construction)

### Make an API Call Module

The **Make an API Call** module lets you reach any Cleanster endpoint not covered by the dedicated modules. It automatically handles Bearer token auth — just provide:
- **Method**: GET / POST / PUT / PATCH / DELETE
- **Path**: The endpoint path (e.g. `v1/bookings/123/checklist`)
- **Body**: Raw JSON for POST/PUT/PATCH requests

---

## Scenario Examples

### 1. New Booking → Slack Notification
```
[Watch New Bookings] → [Slack: Send a Message]
"New cleaning booked at {{property_address}}, {{property_city}} on {{scheduled_at}}"
```

### 2. Booking Completed → Log to Google Sheets
```
[Watch Booking Status Changed] (status: completed)
→ [Google Sheets: Add a Row]
Columns: Booking ID | Property | Cleaner | Completion Time | Duration
```

### 3. Airtable Record → Create Booking
```
[Airtable: Watch Records]
→ [Cleanster: Search Properties] (by address from Airtable)
→ [Cleanster: Create a Booking]
```

### 4. New Booking → Retrieve Property → Send Access Instructions via Email
```
[Watch New Bookings]
→ [Get a Property] (using property_id from trigger)
→ [Gmail: Send an Email]
Subject: "Access instructions for your upcoming cleaning"
Body: {{access_instructions}}
```

### 5. HubSpot Deal Created → Create Property + Book Cleaning
```
[HubSpot: Watch Deals]
→ [Router]
  ├── [Cleanster: Create a Property] (from deal address fields)
  └── [Cleanster: Create a Booking] (using new property ID)
```

### 6. Typeform Response → Create Property → Create Booking
```
[Typeform: Watch Responses]
→ [Cleanster: Create a Property]
→ [Cleanster: Create a Booking] (property_id = {{1.id}})
→ [Gmail: Send Email] confirmation to customer
```

### 7. Booking Cancelled → Notify via SMS (Twilio)
```
[Watch Booking Status Changed] (status: cancelled)
→ [Cleanster: Get a Booking] (for full details)
→ [Twilio: Send an SMS]
"Your cleaning for {{property_address}} on {{scheduled_at}} has been cancelled."
```

### 8. Daily Schedule → Search Bookings → Google Calendar Events
```
[Schedule: Every day at 8am]
→ [Cleanster: Search Bookings] (from_date: today, to_date: today)
→ [Iterator]
→ [Google Calendar: Create an Event] (for each booking)
```

---

## IML (Integromat Markup Language) Notes

Make.com modules use IML for dynamic values. Key expressions used in this app:

| Expression | Description |
|---|---|
| `{{parameters.field_name}}` | References a user-provided input field |
| `{{ifempty(body.data, body)}}` | Uses `body.data` if present, otherwise the raw `body` |
| `{{item.field}}` | References a field while iterating a response array |
| `{{rpc('listProperties')}}` | Calls a Remote Procedure for dynamic dropdown data |
| `{{connection.apiKey}}` | References the authenticated user's API key |
| `{{statusCode}}` | HTTP status code (used in connection validation) |

---

## Adding Webhook Support (Instant Triggers)

The current triggers use polling. To upgrade to instant (real-time) triggers when Cleanster adds webhook support:

In any trigger module JSON, change `typeId: 4` to `typeId: 1` and add:

```json
"webhook": {
  "url": "/v1/webhooks",
  "method": "POST",
  "body": {
    "mode": "json",
    "content": [
      { "name": "event", "value": "booking.created" },
      { "name": "url", "value": "{{webhook.url}}" }
    ]
  }
},
"webhookDetach": {
  "url": "/v1/webhooks/{{webhook.id}}",
  "method": "DELETE"
}
```

---

## API Reference

| Method | Endpoint | Used By |
|---|---|---|
| `GET` | `/v1/bookings` | Watch New Bookings, Watch Status Changed, Search Bookings |
| `GET` | `/v1/bookings/:id` | Get a Booking |
| `POST` | `/v1/bookings` | Create a Booking |
| `POST` | `/v1/bookings/:id/cancel` | Cancel a Booking |
| `POST` | `/v1/bookings/:id/reschedule` | Reschedule a Booking |
| `POST` | `/v1/bookings/:id/cleaner` | Assign Cleaner to Booking |
| `POST` | `/v1/bookings/:id/chat` | Send Chat Message |
| `GET` | `/v1/properties` | Watch New Properties, Search Properties, listProperties RPC |
| `GET` | `/v1/properties/:id` | Get a Property |
| `POST` | `/v1/properties` | Create a Property |
| `PUT` | `/v1/properties/:id` | Update a Property |
| `GET` | `/v1/cleaners` | Search Cleaners, listCleaners RPC |
| `GET` | `/v1/cleaners/:id` | Get a Cleaner |
| `GET` | `/v1/services` | Get Available Services, auth validation |
| `GET` | `/v1/checklists` | listChecklists RPC |

---

## License

MIT
