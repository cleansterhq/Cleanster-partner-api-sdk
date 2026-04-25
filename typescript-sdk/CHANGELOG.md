# Changelog

All notable changes to the Cleanster TypeScript SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `OtherApi` — `listCleaners` (`GET /v1/cleaners`) and `getCleaner` (`GET /v1/cleaners/{id}`) to list all cleaners and retrieve a single cleaner by ID.

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster TypeScript SDK.
- `CleansterClient` with `sandbox()` and `production()` static factory methods.
- `CleansterConfig` with fluent builder pattern supporting custom base URL and timeout.
- **BookingsApi** — full booking lifecycle: create, get, cancel, reschedule, cleaner
  assignment, hours adjustment, expenses, inspection, checklist assignment, feedback,
  tip, and chat (get/send/delete messages).
- **UsersApi** — create user, fetch access token, verify JWT.
- **PropertiesApi** — CRUD, enable/disable, cleaner assignment, iCal sync,
  additional information, and checklist association.
- **ChecklistsApi** — list, get, create, update, delete checklists.
- **OtherApi** — services, plans, recommended hours, cost estimate, cleaning extras,
  available cleaners, coupons.
- **BlacklistApi** — list, add, remove blacklisted cleaners.
- **PaymentMethodsApi** — Stripe setup intent, PayPal client token, add/list/delete
  payment methods, set default.
- **WebhooksApi** — list, create, update, delete webhook endpoints.
- Full TypeScript interfaces for all request/response types.
- Typed model interfaces: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`,
  `PaymentMethod`, `ApiResponse<T>`.
- Generic `ApiResponse<T>` wrapper — statically typed `data` field on every response.
- Exception classes: `CleansterException`, `CleansterAuthException` (HTTP 401),
  `CleansterApiException` (HTTP 4xx/5xx).
- Native `fetch`-based HTTP transport (Node 18+) with `AbortController` timeout support.
- 87 Jest unit tests — no network access required.
- Full TypeScript declaration files (`.d.ts`) included in published package.
