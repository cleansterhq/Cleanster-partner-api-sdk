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

### Python SDK (`python-sdk/`)

- Python 3.8+ package named `cleanster`
- Same 8 API namespaces as Java SDK
- `requests`-based HTTP transport; typed exception hierarchy
- 99 unit tests (unittest + unittest.mock) — all passing
- Packaging: `setup.py`, `pyproject.toml`, `LICENSE`, `CHANGELOG.md`, `.gitignore`
- Detailed README with per-endpoint examples and model tables
- Install: `pip install cleanster`
