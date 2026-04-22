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
