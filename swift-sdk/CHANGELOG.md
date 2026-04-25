# Cleanster Swift SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Swift SDK.
- `CleansterClient` with `sandbox()` and `production()` static factory methods.
- `CleansterConfig` supporting custom base URL and request timeout.
- `NetworkSession` using Swift's native `URLSession` — zero third-party dependencies.
- Full `Codable` model types: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- `ApiResponse<T>` generic wrapper — statically typed `data` field on every response.
- 8 API namespaces: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- All API methods are `async throws` — fully compatible with Swift Structured Concurrency.
- `CleansterError` hierarchy: `CleansterError.unauthorized` (401), `CleansterError.apiError(statusCode:body:)` (4xx/5xx).
- Swift Package Manager support — single `Package.swift`, no Xcode project required.
- Platforms: macOS 13+, iOS 16+, watchOS 9+, tvOS 16+.
- 166 XCTest unit tests — all passing; no network access required (URLProtocol stub).
- Swift 5.9+ required.
