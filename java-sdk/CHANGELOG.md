# Changelog

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster Java SDK
- `CleansterClient` — main entry point with sandbox and production factory methods
- `CleansterConfig` — fluent builder for SDK configuration (timeouts, base URL)
- `BookingApi` — create, list, get, cancel, reschedule bookings; manage cleaner assignment, hours, expenses, chat, feedback, tips, and inspections
- `UserApi` — create user accounts, fetch access tokens, verify JWTs
- `PropertyApi` — full property CRUD, cleaner assignment, iCal integration, checklist assignment
- `ChecklistApi` — create, update, delete, and list cleaning checklists
- `OtherApi` — services, plans, cost estimates, cleaning extras, available cleaners, coupons
- `BlacklistApi` — manage cleaner blacklist
- `PaymentMethodApi` — add, list, delete, and set default payment methods; Stripe setup intent and PayPal token
- `WebhookApi` — create, update, delete, and list webhook endpoints
- Typed exception hierarchy: `CleansterException`, `CleansterAuthException`, `CleansterApiException`
- Comprehensive unit tests using MockWebServer (40+ test cases)
- Javadoc on all public classes and methods
