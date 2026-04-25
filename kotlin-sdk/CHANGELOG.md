# Cleanster Kotlin SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Kotlin SDK.
- `CleansterClient` with `sandbox()` and `production()` companion-object factory methods.
- `CleansterConfig` with custom base URL and timeout support.
- `HttpEngine` using OkHttp 4.12 + Gson 2.10 for HTTP transport.
- Full Kotlin data-class models: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- `ApiResponse<T>` generic wrapper — statically typed `data` field on every response.
- 8 API service classes: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- All API methods are `suspend` functions — fully compatible with Kotlin Coroutines.
- `CleansterError` sealed class hierarchy: `Unauthorized` (401), `ApiError(statusCode, body)` (4xx/5xx).
- Gradle (Kotlin DSL) build — compatible with JVM 11+ and Android.
- 166 unit tests using in-memory `MockHttpEngine` — all passing; no network access required.
- Kotlin 1.9+ required; JVM 11+ target.
