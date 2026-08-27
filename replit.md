# Workspace

## Overview

pnpm workspace monorepo using TypeScript. Each package manages its own dependencies.

## Stack

- **Monorepo tool**: pnpm workspaces
- **Node.js version**: 24
- **Package manager**: pnpm
- **TypeScript version**: 5.9
- **API framework**: Express 5
- **Database**: PostgreSQL + Drizzle ORM
- **Validation**: Zod (`zod/v4`), `drizzle-zod`
- **API codegen**: Orval (from OpenAPI spec)
- **Build**: esbuild (CJS bundle)

## Key Commands

- `pnpm run typecheck` - full typecheck across all packages
- `pnpm run build` - typecheck + build all packages
- `pnpm --filter @workspace/api-spec run codegen` - regenerate API hooks and Zod schemas from OpenAPI spec
- `pnpm --filter @workspace/db run push` - push DB schema changes (dev only)
- `pnpm --filter @workspace/api-server run dev` - run API server locally

See the `pnpm-workspace` skill for workspace structure, TypeScript setup, and package details.

## GitHub Push Method

Python3 script using `GITHUB_PERSONAL_ACCESS_TOKEN` env var. Owner: `cleansterhq`, repo: `Cleanster-partner-api-sdk`, branch: `main`. Zapier/Make/SOAP are NOT pushed to GitHub.

## SDK Projects (all pushed to GitHub)

### Test Count Summary (verified against actual test files)

| SDK | Actual Tests | Badge | Body Text |
|---|---|---|---|
| Java | 85 | 85 ✅ | 85 ✅ |
| Python | 107 | 107 ✅ | 107 ✅ |
| TypeScript | 94 | 94 ✅ | 94 ✅ |
| Ruby | 127 | 127 ✅ | 127 ✅ |
| Go | 98 | 98 ✅ | 98 ✅ |
| PHP | 115 | 115 ✅ | 115 ✅ |
| C# | 115 | 115 ✅ | 115 ✅ |
| Swift | 174 | 174 ✅ | 174 ✅ |
| Kotlin | 174 | 174 ✅ | 174 ✅ |
| XML | 127 | 127 ✅ | 127 ✅ |
| SOAP | 122 | 122 ✅ | 122 ✅ |
| Android | 172 | 172 ✅ | 172 ✅ |
| MCP Server | 75 | - | 75 ✅ |

**Root README totals:** 1,510 SDK tests + 75 MCP = **1,585 total** (badge correct)

### API Endpoint Count: 66 (confirmed)

All SDKs cover the same 66 API endpoints across 8 resource types:
- Bookings: 19 (+updateTask, +updateSqft), Properties: 14, Users: 3, Checklists: 6 (incl. image upload), Other: 9 (+getTasks, +getSubcategories), Payment Methods: 6, Webhooks: 4, Blacklist: 3
- `GET /v1/plans` and `GET /v1/recommended-hours` gained an optional `subcatId` query param
- Root README "All 66 Endpoints" section fully documents all 66 (was 60, then 62, now 66 after adding updateTask, updateSqft, getTasks, getSubcategories)
- Two DELETE-with-body endpoints use `@HTTP` in Android/Kotlin Retrofit (correct pattern, not a bug)

### Java SDK (`java-sdk/`)

- Full Maven project targeting Java 11+
- 8 API classes: Bookings, Users, Properties, Checklists, Other, Blacklist, PaymentMethods, Webhooks
- OkHttp + Jackson transport; typed exception hierarchy
- **85 unit tests** (JUnit 5 + Mockito) - all passing
- Build: `mvn package` → 3 JARs

### PHP SDK (`php-sdk/`)

- PHP 8.1+, zero runtime dependencies - uses only built-in `ext-curl` and `ext-json`
- Same 8 API namespaces as all other SDKs
- PHP 8.1 `readonly` properties on all model classes
- Injectable `HttpClient` - enables PHPUnit mocking without real HTTP
- **115 PHPUnit 10 tests** - all passing

### Go SDK (`go-sdk/`)

- Go 1.21+, zero external runtime dependencies - uses only `net/http`, `encoding/json`, `sync`, `context`
- Generic `APIResponse[T any]` - fully typed `.Data` field
- All methods accept `context.Context` as first parameter
- Thread-safe `SetAccessToken`/`GetAccessToken` via `sync.RWMutex`
- **98 tests** using `net/http/httptest` - all passing

### Ruby SDK (`ruby-sdk/`)

- Ruby 2.7+, zero runtime gem dependencies - uses built-in `Net::HTTP` and `json`
- Idiomatic Ruby: snake_case methods, keyword arguments, model objects
- **127 RSpec unit tests** - all passing
- Published to RubyGems as `cleanster`

### TypeScript SDK (`typescript-sdk/`)

- TypeScript 5.x, Node.js 18+ (uses native `fetch` - zero HTTP dependencies)
- Fully typed: `ApiResponse<T>` generic, typed interfaces for every request/response
- **94 Jest (ts-jest) unit tests** - all passing
- Tests located in `tests/cleanster.test.ts`

### Python SDK (`python-sdk/`)

- Python 3.8+ package named `cleanster`
- `requests`-based HTTP transport; typed exception hierarchy
- **107 unit tests** (unittest + unittest.mock) - all passing

### C# SDK (`csharp-sdk/`)

- .NET 8.0 library targeting `net8.0`; zero external runtime dependencies
- `sealed record` model types; generic `ApiResponse<T>` wrapper
- All API methods async (`Task<ApiResponse<T>>`); optional `CancellationToken`
- **115 xUnit 2.7 + Moq 4.20 tests** - all passing

### Swift SDK (`swift-sdk/`)

- Swift 5.9+ / iOS 16+, Swift Package Manager
- Full async/await API using `URLSession`
- **174 tests** - all passing

### Kotlin SDK (`kotlin-sdk/`)

- Kotlin 1.9+ / JVM 11+, Gradle
- Coroutines-first with `suspend` functions
- **174 tests** - all passing

### XML SDK (`xml-sdk/`)

- Java 17+ / JAXB 4.0 + OkHttp + Gson, Maven
- **127 tests** (JUnit 5 + MockWebServer) - all passing

### SOAP SDK (`soap-sdk/`)

- Java 11+ SOAP 1.1 (document/literal) bridge over REST
- 7 test classes: CleansterSOAPClientTest (43), ServiceExtensionsTest (43), UserServiceTest (6), BlacklistServiceTest (6), PaymentMethodServiceTest (12), WebhookServiceTest (8), WebhookUtilsTest (4)
- **122 tests** (JUnit 5 + Mockito) - all passing
- **Assign Cleaner endpoint fixed**: was `cleaner-assignment` → corrected to `cleaner` in BookingService.java, both test files, and README
- NOT pushed to GitHub

### Android SDK (`android-sdk/`)

- Android API 26+ / Kotlin 1.9+, Retrofit 2 + OkHttp + Gson + Coroutines
- Retrofit 2.9.0 annotation-based interface with `suspend` functions
- **60 API endpoints** across 8 API classes (2 use `@HTTP` annotation for DELETE-with-body: `removeFromBlacklist` and `deleteICalLink` - correct Retrofit pattern)
- 8 test files covering all API namespaces (MockWebServer, no real HTTP)
- **172 tests** - all passing
- Pushed to GitHub at `android-sdk/`

## MCP Server (`mcp-server/`)

Standalone Node.js 20+ TypeScript server implementing the Model Context Protocol (MCP). Allows Claude and other AI assistants to interact with the Cleanster Partner API through natural language.

- **Transports**: HTTP/SSE via Express (default, `MCP_TRANSPORT=http`, port 8000) or stdio for Claude Desktop (`MCP_TRANSPORT=stdio`)
- **Tools**: 14 tools - list_bookings, get_booking, list_properties, get_property, list_cleaners, get_cleaner, list_services, create_booking, cancel_booking, reschedule_booking, assign_crew, update_checklist, update_task, update_sqft
- **create_booking is now aligned to the real API contract**: takes `planId`, `date`, `time`, `hours`, `roomCount`, `bathroomCount`, `extraSupplies`, `paymentMethodId` (+ optional `couponCode`/`extras`) instead of a fictional `service_type` enum. Service types are account-specific and discovered dynamically via the new `list_services` tool (`GET /v1/services`), not a fixed set.
- **Auth**: Per-connection bearer token (API key); OAuth 2.0 + PKCE seam in `src/auth/token.ts`
- **Rate limiting**: 60 req/min per token via express-rate-limit (HTTP mode only)
- **Logging**: Pino with bearer token redaction; pretty-print in dev, JSON in production
- **Tests**: **75 Vitest unit tests** - all passing (15 test files, mocked API, no real HTTP calls)
- **Run**: `cd mcp-server && npm run dev`; Health endpoint at `GET /health`
- **Workflow**: "MCP Server" workflow configured, runs on port 8000

## Zapier Integration (`zapier-app/`)

Production-ready Zapier app - NOT pushed to GitHub.

- 3 Triggers: New Booking, Booking Status Changed, New Property
- 6 Actions: Create/Cancel/Reschedule Booking, Create Property, Assign Cleaner, Send Chat Message
- 4 Searches: Find Booking, Find Property, Find Cleaner, Get Available Services
- 2 Search-or-Creates: Find or Create Booking, Find or Create Property
- Auth: `Authorization: Bearer <api-key>` (single key, simpler than SDK dual-header model)
- **Assign Cleaner endpoint fixed**: was `POST /v1/bookings/{id}/assign-cleaner` → corrected to `POST /v1/bookings/{id}/cleaner`

## Make.com Integration (`make-app/`)

Production-ready Make.com custom app - NOT pushed to GitHub.

- 18 modules total across triggers, actions, lookups, and generic API call
- Triggers: Watch New Bookings, Watch Booking Status Changed, Watch New Properties
- Actions: Full booking lifecycle, property management, cleaner assignment
- RPCs: listProperties, listCleaners, listChecklists, listServices (dynamic dropdowns)
- **Assign Cleaner endpoint fixed**: was `POST /v1/bookings/{id}/assign-cleaner` → corrected to `POST /v1/bookings/{id}/cleaner`
