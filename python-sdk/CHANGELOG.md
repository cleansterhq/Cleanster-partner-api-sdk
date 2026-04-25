# Changelog

All notable changes to the Cleanster Python SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `OtherApi` — `list_cleaners` (`GET /v1/cleaners`) and `get_cleaner` (`GET /v1/cleaners/{id}`) to list all cleaners and retrieve a single cleaner by ID.

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Python SDK.
- `CleansterClient` with `sandbox()` and `production()` factory methods.
- `CleansterConfig` with builder pattern support for custom timeout and base URL.
- **BookingsApi** — full booking lifecycle: create, get, cancel, reschedule, cleaner
  assignment, hours adjustment, expenses, inspection, checklist assignment, feedback,
  tip, and chat (get/send/delete messages).
- **UsersApi** — create user, fetch access token, verify JWT.
- **PropertiesApi** — CRUD, enable/disable, cleaner assignment, iCal sync,
  checklist association.
- **ChecklistsApi** — list, get, create, update, delete checklists.
- **OtherApi** — services, plans, recommended hours, cost estimate, cleaning extras,
  available cleaners, coupons.
- **BlacklistApi** — list, add, remove blacklisted cleaners.
- **PaymentMethodsApi** — Stripe setup intent, PayPal client token, add/list/delete
  payment methods, set default.
- **WebhooksApi** — list, create, update, delete webhook endpoints.
- Typed model classes: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`,
  `PaymentMethod`, `ApiResponse`.
- Exception hierarchy: `CleansterException` → `CleansterAuthException` (HTTP 401),
  `CleansterApiException` (HTTP 4xx/5xx).
- 100+ unit tests using `unittest.mock` — no network access required.
- `requests`-based HTTP transport with session keep-alive.
- Full type annotations throughout.
- Comprehensive README with per-endpoint code examples.
