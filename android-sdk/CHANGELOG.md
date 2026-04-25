# Cleanster Android SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Android SDK.
- `CleansterClient` with `sandbox()` and `production()` companion-object factory methods.
- `CleansterConfig` with custom base URL, API key, and timeout support.
- `CleansterApi` — Retrofit 2.9 interface annotating all 59 endpoints across 8 resource types.
- `AuthInterceptor` — OkHttp interceptor attaching `Authorization: Bearer` header to every request.
- Full Kotlin data-class models: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- `ApiResponse<T>` generic wrapper — statically typed `data` field on every response.
- 8 API service classes: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- All API methods are `suspend` functions — fully compatible with Kotlin Coroutines.
- `CleansterError` sealed class: `Unauthorized` (401), `ApiError(statusCode, body)` (4xx/5xx).
- 164 unit tests using MockWebServer (OkHttp) — all passing; no real network access.
- Requires Android API 26+ (Android 8.0 Oreo), Kotlin 1.9+, JVM 11.
