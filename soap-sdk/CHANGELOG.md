# Cleanster SOAP SDK Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-04-22

### Added
- Initial release of the Cleanster SOAP SDK.
- `CleansterSOAPClient` — unified facade exposing all 62 API operations as SOAP 1.1 calls.
- `SOAPTransport` — internal HTTP bridge that translates SOAP envelopes to Cleanster REST calls.
- WSDL and XSD schema (`cleanster.wsdl`, `cleanster-types.xsd`) covering all 62 operations.
- 9 SOAP service classes: `BookingService` (17 ops), `PropertyService` (14 ops),
  `CleanerService` (2 ops), `ChecklistService` (6 ops), `OtherService` (7 ops),
  `UserService` (3 ops), `BlacklistService` (3 ops), `PaymentMethodService` (6 ops),
  `WebhookService` (4 ops).
- Ready-to-use SOAP envelope XML examples in `examples/`.
- Exception hierarchy: `SOAPClientException` with fault code and fault string.
- 118 JUnit 5 + Mockito unit tests across 6 test classes — all passing.
- Maven packaging: `cleanster-soap-sdk` artifact, Java 11+ required.
