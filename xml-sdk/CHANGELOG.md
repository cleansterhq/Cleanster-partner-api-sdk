# Cleanster XML SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster XML SDK.
- `CleansterXmlClient` — main entry point with `sandbox()` and `production()` factory methods.
- `CleansterXmlConfig` for custom base URL and timeout configuration.
- JAXB 4.0 XML marshalling/unmarshalling for all request and response types.
- OkHttp 4.x HTTP transport with `application/xml` content negotiation.
- 8 API classes: `BookingsXmlApi`, `UsersXmlApi`, `PropertiesXmlApi`, `ChecklistsXmlApi`,
  `OtherXmlApi`, `BlacklistXmlApi`, `PaymentMethodsXmlApi`, `WebhooksXmlApi`.
- Full JAXB-annotated model classes: `Booking`, `User`, `Property`, `Checklist`,
  `ChecklistItem`, `PaymentMethod`, `XmlApiResponse<T>`.
- Exception hierarchy: `CleansterXmlException`, `CleansterXmlAuthException` (401),
  `CleansterXmlApiException` (4xx/5xx).
- 164 JUnit 5 unit tests using MockWebServer — all passing; no real network access.
- Maven packaging: `cleanster-xml-sdk` artifact, Java 17+ required.
