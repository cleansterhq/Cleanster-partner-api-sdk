# Cleanster PHP SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `OtherApi` — `listCleaners` (`GET /v1/cleaners`) and `getCleaner` (`GET /v1/cleaners/{id}`) to list all cleaners and retrieve a single cleaner by ID.

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster PHP SDK.
- `CleansterClient` with `sandbox()` and `production()` factory methods.
- `Config` with `sandbox()` / `production()` helpers and custom timeout support.
- `HttpClient` using PHP's built-in cURL extension — zero external HTTP dependencies.
- Full model classes: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- `ApiResponse` wrapper with `$status`, `$message`, and `$data` readonly properties.
- 8 API service classes: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- Three exception classes: `CleansterException`, `AuthException` (401), `ApiException` (4xx/5xx).
- PHP 8.1+ required (readonly properties, constructor promotion, named arguments).
- 108 PHPUnit 10 tests — all passing; zero real HTTP requests.
- PSR-4 autoloading via Composer.
