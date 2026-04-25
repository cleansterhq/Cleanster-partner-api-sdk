# Cleanster Go SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `OtherApi` — `ListCleaners` (`GET /v1/cleaners`) and `GetCleaner` (`GET /v1/cleaners/{id}`) to list all cleaners and retrieve a single cleaner by ID.

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Go SDK.
- `Client` with `NewSandboxClient` and `NewProductionClient` factory functions.
- `Config` and `NewSandboxConfig` / `NewProductionConfig` helpers.
- `httpClient` using Go's built-in `net/http` — zero external dependencies.
- Full model structs: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- Generic `APIResponse[T]` wrapper for all API calls.
- 8 service types: `BookingsService`, `UsersService`, `PropertiesService`, `ChecklistsService`,
  `OtherService`, `BlacklistService`, `PaymentMethodsService`, `WebhooksService`.
- Three error types: `CleansterError`, `AuthError`, `APIError` — all compatible with `errors.As`.
- Thread-safe `SetAccessToken` / `GetAccessToken` using `sync.RWMutex`.
- Context-aware API calls — every method accepts `context.Context`.
- 94 tests using `net/http/httptest` — no external test dependencies.
- Support for Go 1.21+.
