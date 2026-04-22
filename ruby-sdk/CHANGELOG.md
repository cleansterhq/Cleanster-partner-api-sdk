# Cleanster Ruby SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-01

### Added
- Initial release of the Cleanster Ruby SDK.
- `Cleanster::Client` with `sandbox` and `production` factory methods.
- `Cleanster::Config` and `Cleanster::Config::Builder` for custom configuration.
- `Cleanster::HttpClient` using Ruby's built-in `Net::HTTP` — zero external HTTP dependencies.
- Full model classes: `Booking`, `User`, `Property`, `Checklist`, `ChecklistItem`, `PaymentMethod`.
- `Cleanster::ApiResponse` generic response wrapper.
- 8 API namespaces: `BookingsApi`, `UsersApi`, `PropertiesApi`, `ChecklistsApi`,
  `OtherApi`, `BlacklistApi`, `PaymentMethodsApi`, `WebhooksApi`.
- Typed exception hierarchy: `CleansterError`, `AuthError`, `ApiError`.
- 90 RSpec unit tests — all passing; no network access required.
- Support for Ruby 2.7+.
