# Cleanster Partner API — Official SDKs

<p align="center">
  <strong>Multi-language SDKs for the Cleanster Partner API</strong><br>
  Integrate cleaning service automation into your platform in minutes.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/API-Cleanster%20Partner-brightgreen" alt="Cleanster Partner API">
  <img src="https://img.shields.io/badge/SDKs-7%20Languages-blue" alt="7 Languages">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="MIT License">
  <img src="https://img.shields.io/badge/Endpoints-53-orange" alt="53 Endpoints">
</p>

---

## Available SDKs

| Language | Folder | Tests | Min Version | Package |
|---|---|---|---|---|
| [Java](#java) | [`java-sdk/`](./java-sdk) | 74 passing | Java 11+ | Maven / Gradle |
| [Python](#python) | [`python-sdk/`](./python-sdk) | 99 passing | Python 3.8+ | pip |
| [TypeScript / Node.js](#typescript) | [`typescript-sdk/`](./typescript-sdk) | 85 passing | Node.js 18+ | npm |
| [Ruby](#ruby) | [`ruby-sdk/`](./ruby-sdk) | 119 passing | Ruby 2.7+ | gem |
| [Go](#go) | [`go-sdk/`](./go-sdk) | 92 passing | Go 1.21+ | go get |
| [PHP](#php) | [`php-sdk/`](./php-sdk) | 106 passing | PHP 8.1+ | Composer |
| [C# / .NET](#c--net) | [`csharp-sdk/`](./csharp-sdk) | 107 passing | .NET 8.0+ | NuGet |

---

## Overview

The Cleanster Partner API gives your platform full control over cleaning service operations:

- **Bookings** — create, reschedule, cancel, adjust hours, assign cleaners
- **Properties** — manage locations, iCal calendar sync, preferred cleaner lists
- **Users** — create accounts and manage per-user authentication tokens
- **Checklists** — build reusable task lists and assign them to bookings
- **Payments** — attach Stripe and PayPal payment methods
- **Webhooks** — subscribe to real-time booking lifecycle events
- **Blacklist** — block specific cleaners from your properties
- **Reference Data** — services, plans, cleaning extras, cost estimates, available cleaners

---

## API Endpoints

53 endpoints across 8 groups:

| Group | Endpoints |
|---|---|
| Bookings | 17 |
| Properties | 14 |
| Other / Reference Data | 7 |
| Payment Methods | 6 |
| Checklists | 6 |
| Webhooks | 4 |
| Blacklist | 3 |
| Users | 3 |

Full documentation: [Cleanster Partner API on Postman](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)

---

## Authentication

Every request requires two HTTP headers:

| Header | Description |
|---|---|
| `access-key` | Your static partner key — issued by Cleanster |
| `token` | A per-user long-lived JWT — fetched via `GET /v1/user/access-token/{userId}` |

Contact [partner@cleanster.com](mailto:partner@cleanster.com) to get your access key.

---

## Environments

| Environment | Base URL |
|---|---|
| **Sandbox** | `https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public` |
| **Production** | `https://partner-dot-official-tidyio-project.ue.r.appspot.com/public` |

All SDKs default to the **sandbox** environment. Switch to production when going live.

---

## Quick Install

### Java

```xml
<dependency>
  <groupId>com.cleanster</groupId>
  <artifactId>cleanster-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

```java
CleansterClient client = new CleansterClient("your-access-key", "user-token");
ApiResponse<Booking> booking = client.bookings().createBooking(req);
```

[Full Java documentation →](./java-sdk/README.md)

---

### Python

```bash
pip install cleanster
```

```python
from cleanster import CleansterClient
client = CleansterClient(access_key="your-access-key", token="user-token")
booking = client.bookings.create_booking(property_id=1004, date="2025-09-01", ...)
```

[Full Python documentation →](./python-sdk/README.md)

---

### TypeScript

```bash
npm install cleanster
```

```typescript
import { CleansterClient } from 'cleanster';
const client = new CleansterClient({ accessKey: 'your-access-key', token: 'user-token' });
const booking = await client.bookings.createBooking({ propertyId: 1004, date: '2025-09-01', ... });
```

[Full TypeScript documentation →](./typescript-sdk/README.md)

---

### Ruby

```ruby
gem 'cleanster'
```

```ruby
client = Cleanster::Client.new(access_key: 'your-access-key', token: 'user-token')
booking = client.bookings.create_booking(property_id: 1004, date: '2025-09-01', ...)
```

[Full Ruby documentation →](./ruby-sdk/README.md)

---

### Go

```bash
go get github.com/cleanster/cleanster-go-sdk
```

```go
client := cleanster.NewClient("your-access-key", "user-token")
resp, err := client.Bookings.CreateBooking(ctx, cleanster.CreateBookingRequest{...})
```

[Full Go documentation →](./go-sdk/README.md)

---

### PHP

```bash
composer require cleanster/cleanster-php-sdk
```

```php
$client = new CleansterClient('your-access-key', 'user-token');
$booking = $client->bookings()->createBooking([...]);
```

[Full PHP documentation →](./php-sdk/README.md)

---

### C# / .NET

```bash
dotnet add package Cleanster
```

```csharp
var client = new CleansterClient("your-access-key", "user-token");
var booking = await client.Bookings.CreateBookingAsync(new CreateBookingRequest { ... });
```

[Full C# documentation →](./csharp-sdk/README.md)

---

## Test Coupon Codes (Sandbox)

| Code | Discount |
|---|---|
| `100POFF` | 100% off |
| `50POFF` | 50% off |
| `20POFF` | 20% off |
| `200OFF` | $200 off |
| `100OFF` | $100 off |

---

## Repository Structure

```
Cleanster-partner-api-sdk/
├── java-sdk/            Java 11+ SDK (Maven / Gradle)
├── python-sdk/          Python 3.8+ SDK (pip)
├── typescript-sdk/      TypeScript / Node.js 18+ SDK (npm)
├── ruby-sdk/            Ruby 2.7+ SDK (gem)
├── go-sdk/              Go 1.21+ SDK (go get)
├── php-sdk/             PHP 8.1+ SDK (Composer)
└── csharp-sdk/          .NET 8.0+ SDK (NuGet)
```

Each SDK folder contains:
- Full source code
- Comprehensive `README.md` with every endpoint documented
- Complete test suite

---

## License

MIT License — see [LICENSE](LICENSE) for details.

---

## Support

- **API Documentation:** [Postman Docs](https://documenter.getpostman.com/view/26172658/2sAYdoF7ep)
- **Partner inquiries:** [partner@cleanster.com](mailto:partner@cleanster.com)
- **General support:** [support@cleanster.com](mailto:support@cleanster.com)
