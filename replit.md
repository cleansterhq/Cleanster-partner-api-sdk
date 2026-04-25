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

- `pnpm run typecheck` — full typecheck across all packages
- `pnpm run build` — typecheck + build all packages
- `pnpm --filter @workspace/api-spec run codegen` — regenerate API hooks and Zod schemas from OpenAPI spec
- `pnpm --filter @workspace/db run push` — push DB schema changes (dev only)
- `pnpm --filter @workspace/api-server run dev` — run API server locally

See the `pnpm-workspace` skill for workspace structure, TypeScript setup, and package details.

## SDK Projects

### Java SDK (`java-sdk/`)

- Full Maven project targeting Java 11+
- 8 API classes: Bookings, Users, Properties, Checklists, Other, Blacklist, PaymentMethods, Webhooks
- OkHttp + Jackson transport; typed exception hierarchy
- 74 unit tests (JUnit 5 + Mockito) — all passing
- Build: `mvn package` → 3 JARs
- Detailed README with per-endpoint examples and model tables

### PHP SDK (`php-sdk/`)

- PHP 8.1+, zero runtime dependencies — uses only built-in `ext-curl` and `ext-json`
- Same 8 API namespaces as all other SDKs (Bookings/Users/Properties/Checklists/Other/Blacklist/PaymentMethods/Webhooks)
- PHP 8.1 `readonly` properties on all model classes: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`
- `ApiResponse` wrapper: `$status`, `$message`, `$data` readonly properties on every call
- Injectable `HttpClient` constructor parameter — enables PHPUnit mocking without real HTTP
- `CleansterClient::sandbox($key)` / `::production($key)` factory methods
- `$client->bookings()`, `->users()`, `->properties()`, `->checklists()`, `->other()`, `->blacklist()`, `->paymentMethods()`, `->webhooks()` accessors
- Exception hierarchy: `CleansterException` → `AuthException` (401, `$statusCode` + `$responseBody`) / `ApiException` (4xx/5xx)
- 106 PHPUnit 10 tests — all passing; uses `createMock(HttpClient::class)`; no real network access
- Packaging: `composer.json` (PSR-4, PHP 8.1+), `phpunit.xml`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with named-argument PHP code examples, model property tables, exception handling, test coverage breakdown, design decision rationale

### Go SDK (`go-sdk/`)

- Go 1.21+, zero external runtime dependencies — uses only `net/http`, `encoding/json`, `sync`, `context`
- Same 8 API namespaces as all other SDKs (Bookings/Users/Properties/Checklists/Other/Blacklist/PaymentMethods/Webhooks)
- Generic `APIResponse[T any]` — fully typed `.Data` field, no interface casting required
- Typed model structs: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`; pointer types for nullable fields (`*int`, `*string`)
- All methods accept `context.Context` as first parameter
- Thread-safe `SetAccessToken`/`GetAccessToken` via `sync.RWMutex`
- Exception hierarchy: `CleansterError` → `AuthError` (401) / `APIError` (4xx/5xx) — all `errors.As`-compatible
- 92 tests using `net/http/httptest` — all passing; no network access required
- Packaging: `go.mod` (module `github.com/cleanster/cleanster-go-sdk`, Go 1.21), `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with context-aware Go code examples, model field tables, error handling patterns, and full API summary tables

### Ruby SDK (`ruby-sdk/`)

- Ruby 2.7+, zero runtime gem dependencies — uses built-in `Net::HTTP` and `json`
- Same 8 API namespaces as Java/Python/TypeScript SDKs
- Idiomatic Ruby: snake_case methods, keyword arguments, model objects (not raw hashes)
- `ApiResponse` wrapper with `#status`, `#message`, `#data`; model classes for Booking, User, Property, Checklist, ChecklistItem, PaymentMethod
- Exception hierarchy: `CleansterError` → `AuthError` (401) / `ApiError` (4xx/5xx)
- 119 RSpec unit tests — all passing; no network access required
- Published to RubyGems as `cleanster`; `gem build cleanster.gemspec` produces the gem
- Packaging: `cleanster.gemspec`, `Gemfile`, `Rakefile`, `.rspec`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with per-endpoint Ruby keyword-argument examples and full model attribute tables

### TypeScript SDK (`typescript-sdk/`)

- TypeScript 5.x, Node.js 18+ (uses native `fetch` — zero HTTP dependencies)
- Same 8 API namespaces as Java/Python SDKs
- Fully typed: `ApiResponse<T>` generic, typed interfaces for every request/response
- Exception classes with correct `instanceof` support
- 85 Jest (ts-jest) unit tests — all passing
- Published to npm as `cleanster`; `npm run build` compiles to `dist/` with `.d.ts` files
- Packaging: `package.json`, `tsconfig.json`, `jest.config.js`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with per-endpoint TypeScript examples and full type reference table

### Python SDK (`python-sdk/`)

- Python 3.8+ package named `cleanster`
- Same 8 API namespaces as Java SDK
- `requests`-based HTTP transport; typed exception hierarchy
- 99 unit tests (unittest + unittest.mock) — all passing
- Packaging: `setup.py`, `pyproject.toml`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with per-endpoint examples and model tables
- Install: `pip install cleanster`

### C# SDK (`csharp-sdk/`)

- .NET 8.0 library targeting `net8.0`; zero external runtime dependencies (only `System.Net.Http` + `System.Text.Json`)
- Same 8 API service classes as all other SDKs: `BookingsApi` (17 methods), `UsersApi` (3), `PropertiesApi` (14), `ChecklistsApi` (5), `OtherApi` (7), `BlacklistApi` (3), `PaymentMethodsApi` (6), `WebhooksApi` (4)
- `sealed record` model types with `[JsonPropertyName]` and `init`-only properties: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`
- Generic `ApiResponse<T>` sealed record wrapper: `(int Status, string Message, T Data)`
- `CleansterClient.Sandbox(key)` / `.Production(key)` static factories; `SetAccessToken(token)` / `GetAccessToken()`
- `ICleansterHttpClient` public interface — inject `Mock<ICleansterHttpClient>` for unit tests; `[InternalsVisibleTo("Cleanster.Tests")]` exposes internal API constructors to the test assembly
- Exception hierarchy: `CleansterException` (base) → `AuthException` (401) / `ApiException` (4xx/5xx), both with `StatusCode` + `ResponseBody`
- All API methods async (`Task<ApiResponse<T>>`); every method accepts optional `CancellationToken`
- `JsonHelper` (internal): `ParseSingle<T>`, `ParseList<T>`, `ParseRaw` shared across all API classes
- 107 xUnit 2.7 + Moq 4.20 tests — all passing; `MockBehavior.Strict` ensures exact path/verb/body verification; zero real HTTP calls
- Packaging: `Cleanster.sln`, `Cleanster.csproj`, `Cleanster.Tests.csproj`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README: dual-auth step-by-step, all API methods with C# named-argument examples, full model property tables, exception hierarchy, DI/ASP.NET Core patterns, design decision rationale, test coverage breakdown

## MCP Server (`mcp-server/`)

Standalone Node.js 20+ TypeScript server implementing the Model Context Protocol (MCP). Allows Claude and other AI assistants to interact with the Cleanster Partner API through natural language.

- **Transports**: HTTP/SSE via Express (default, `MCP_TRANSPORT=http`, port 8000) or stdio for Claude Desktop direct connection (`MCP_TRANSPORT=stdio`)
- **Tools**: 11 tools — list_bookings, get_booking, list_properties, get_property, list_cleaners, get_payout_records, create_booking, cancel_booking, reschedule_booking, assign_crew, update_checklist
- **Auth**: Per-connection bearer token (API key); OAuth 2.0 + PKCE seam in `src/auth/token.ts`
- **Rate limiting**: 60 req/min per token via express-rate-limit (HTTP mode only)
- **Logging**: Pino with bearer token redaction; pretty-print in dev, JSON in production
- **Tests**: 51 Vitest unit tests — all passing (mocked API, no real HTTP calls)
- **Run**: `cd mcp-server && npm run dev`; Health endpoint at `GET /health`
- **GitHub**: Pushed to `cleansterhq/Cleanster-partner-api-sdk` at `mcp-server/`
- **Workflow**: "MCP Server" workflow configured, auto-starts
