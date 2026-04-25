# Cleanster C# SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster .NET SDK.
- `CleansterClient` with `Sandbox()` and `Production()` static factory methods.
- `CleansterConfig` with `Sandbox()` / `Production()` helpers and custom timeout support.
- `CleansterHttpClient` using `System.Net.Http.HttpClient` and `System.Text.Json` — zero external dependencies.
- `ICleansterHttpClient` interface for custom transport injection and unit testing.
- `ApiResponse<T>` generic record wrapper for all API responses.
- Typed model records: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- 8 API service classes: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- Three exception classes: `CleansterException`, `AuthException` (401), `ApiException` (4xx/5xx).
- Full async/await throughout — every API method returns `Task<ApiResponse<T>>`.
- `CancellationToken` on every API method for deadline and cancellation support.
- 109 xUnit tests with Moq — all passing; zero real HTTP requests.
- NuGet packaging: `Cleanster` package ID, MIT license.
