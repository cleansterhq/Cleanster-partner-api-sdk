# Cleanster Partner API — Zapier Integration

A production-ready Zapier app for the [Cleanster Partner API](https://api.cleanster.com). This lets you connect Cleanster to 5,000+ apps through Zapier — automating bookings, properties, cleaners, and chat with tools like Airtable, Google Sheets, Slack, Notion, HubSpot, and more.

---

## What's Included

### Triggers (When this happens…)
| Trigger | Description |
|---|---|
| **New Booking** | Fires when a new booking is created |
| **Booking Status Changed** | Fires when a booking changes status (scheduled → in_progress → completed) |
| **New Property** | Fires when a new property is added |

### Actions (Do this…)
| Action | Description |
|---|---|
| **Create Booking** | Creates a new cleaning booking |
| **Cancel Booking** | Cancels an existing booking |
| **Reschedule Booking** | Moves a booking to a new date and time |
| **Create Property** | Adds a new property to your account |
| **Assign Cleaner to Booking** | Assigns a specific cleaner to a booking |
| **Send Chat Message** | Sends a message on a booking chat thread |

### Searches (Find…)
| Search | Description |
|---|---|
| **Find Booking** | Looks up a booking by ID or filters (status, property) |
| **Find Property** | Looks up a property by ID or address |
| **Find Cleaner** | Looks up a cleaner by ID or name |
| **Get Available Services** | Lists all available cleaning service types |

### Search or Create (Find or make…)
- **Find or Create Booking** — finds an existing booking or creates a new one
- **Find or Create Property** — finds an existing property or creates a new one

---

## Prerequisites

- Node.js 18 or higher
- A Cleanster Partner API key (find it in your Cleanster dashboard under **Settings → API**)
- A Zapier account (free or paid)
- Zapier CLI: `npm install -g zapier-platform-cli`

---

## Quick Start

### 1. Install dependencies

```bash
cd zapier-app
npm install
```

### 2. Log in to Zapier

```bash
zapier login
```

### 3. Register the app (first time only)

```bash
zapier register "Cleanster Partner API"
```

This will create your app on Zapier's developer platform and update `.zapierapprc` with your app ID.

### 4. Push the app to Zapier

```bash
zapier push
```

### 5. Test in Zapier

After pushing, go to [zapier.com](https://zapier.com), create a new Zap, and search for **Cleanster Partner API** to find your app.

---

## Authentication

This app uses **API Key** authentication (Bearer token).

When a user connects to Cleanster in Zapier, they will be asked for their **API Key**. The key is validated against `GET /v1/services`.

In your code, the API key is automatically added to every request as:
```
Authorization: Bearer <api_key>
```

---

## Development

### Project structure

```
zapier-app/
├── index.js              ← App entry point (registers all modules)
├── authentication.js     ← API key auth setup
├── package.json
│
├── triggers/
│   ├── new_booking.js
│   ├── booking_status_changed.js
│   └── new_property.js
│
├── creates/
│   ├── create_booking.js
│   ├── cancel_booking.js
│   ├── reschedule_booking.js
│   ├── create_property.js
│   ├── assign_cleaner.js
│   └── send_message.js
│
└── searches/
    ├── find_booking.js
    ├── find_property.js
    ├── find_cleaner.js
    └── get_services.js
```

### Validate the app definition

```bash
zapier validate
```

### Run tests

```bash
npm test
```

### Test a single trigger/action/search

```bash
zapier test
```

### View logs from a pushed app

```bash
zapier logs
```

---

## Common Zap Examples

### 1. New Booking → Slack Notification
**Trigger:** Cleanster — New Booking  
**Action:** Slack — Send Channel Message  
> "New cleaning booked for {{property__address}} on {{scheduled_at}}"

---

### 2. Booking Completed → Google Sheets Row
**Trigger:** Cleanster — Booking Status Changed (filter: `completed`)  
**Action:** Google Sheets — Create Spreadsheet Row  
> Logs booking ID, property address, cleaner name, and completion time

---

### 3. Airtable Record → Create Booking in Cleanster
**Trigger:** Airtable — New Record  
**Action:** Cleanster — Create Booking  
> Automatically books cleanings when a property row is added to Airtable

---

### 4. New Booking → Find Property → Send Slack DM with Access Instructions
**Trigger:** Cleanster — New Booking  
**Search:** Cleanster — Find Property (using `property__id`)  
**Action:** Slack — Send Direct Message  
> Sends the property access instructions to the dispatch manager

---

### 5. HubSpot Contact Created → Create Property in Cleanster
**Trigger:** HubSpot — New Contact  
**Action:** Cleanster — Create Property  
> Auto-populates Cleanster with new property owners from your CRM

---

### 6. Typeform Submission → Create Booking
**Trigger:** Typeform — New Entry  
**Search:** Cleanster — Find Property (by address from form)  
**Action:** Cleanster — Create Booking  
> Self-service booking form that creates bookings directly in Cleanster

---

### 7. Booking Completed → Send Thank-You Email via Gmail
**Trigger:** Cleanster — Booking Status Changed (filter: `completed`)  
**Action:** Gmail — Send Email  
> Automatically sends a thank-you email to the property owner

---

## Adding Webhooks (Future Enhancement)

The current implementation uses **polling** (checking for new data every 15 minutes on free Zapier plans, every 1–2 minutes on paid plans). For real-time triggers, the Cleanster API supports webhooks.

To convert a polling trigger to a webhook trigger:

```js
// In any trigger file, change:
operation: {
  type: "polling",
  perform: getBookings,
}

// To:
operation: {
  type: "hook",
  performSubscribe: subscribeHook,   // POST /v1/webhooks
  performUnsubscribe: unsubscribeHook, // DELETE /v1/webhooks/:id
  perform: parseWebhookPayload,
}
```

See the [Zapier Platform Docs — REST Hooks](https://github.com/zapier/zapier-platform/blob/main/packages/cli/README.md#rest-hooks) for details.

---

## Environment Variables

No environment variables are needed. Authentication is entirely user-provided through Zapier's auth flow.

For local testing, you can create a `.env` file:

```env
CLEANSTER_API_KEY=your_api_key_here
```

And reference it in tests:
```js
const TEST_API_KEY = process.env.CLEANSTER_API_KEY;
```

---

## Publishing to Zapier App Directory

To make this integration available to all Zapier users (not just your own account):

1. Push a stable version: `zapier push`
2. Promote to production: `zapier promote <version>`
3. Submit for review via the [Zapier Developer Platform](https://developer.zapier.com)

Zapier reviews take 1–4 weeks. During review, ensure:
- All triggers have working `sample` data
- All actions have clear `helpText` on every field
- Authentication test endpoint returns a 200
- App description follows Zapier's style guidelines

---

## API Reference

This app covers the following Cleanster API endpoints:

| Method | Endpoint | Used By |
|---|---|---|
| `GET` | `/v1/bookings` | Triggers: New Booking, Booking Status Changed; Search: Find Booking |
| `GET` | `/v1/bookings/:id` | Search: Find Booking (by ID) |
| `POST` | `/v1/bookings` | Action: Create Booking |
| `POST` | `/v1/bookings/:id/cancel` | Action: Cancel Booking |
| `POST` | `/v1/bookings/:id/reschedule` | Action: Reschedule Booking |
| `POST` | `/v1/bookings/:id/cleaner` | Action: Assign Cleaner |
| `POST` | `/v1/bookings/:id/chat` | Action: Send Message |
| `GET` | `/v1/properties` | Trigger: New Property; Search: Find Property |
| `GET` | `/v1/properties/:id` | Search: Find Property (by ID) |
| `POST` | `/v1/properties` | Action: Create Property |
| `GET` | `/v1/cleaners` | Search: Find Cleaner |
| `GET` | `/v1/cleaners/:id` | Search: Find Cleaner (by ID) |
| `GET` | `/v1/services` | Auth test; Search: Get Services |

---

## License

MIT
