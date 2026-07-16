import XCTest
@testable import Cleanster

final class WebhookUtilsTests: XCTestCase {

    private let secret   = "test-secret-key"
    private let payload  = "{\"event\":\"BOOKING_CREATED\",\"bookingId\":42}"
    private let expected = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364"

    func testComputeSignature_returnsCorrectHex() {
        XCTAssertEqual(expected, WebhookUtils.computeSignature(secret: secret, payload: payload))
    }

    func testVerifySignature_validSignatureReturnsTrue() {
        XCTAssertTrue(WebhookUtils.verifySignature(secret: secret, payload: payload, signature: expected))
    }

    func testVerifySignature_wrongSignatureReturnsFalse() {
        XCTAssertFalse(WebhookUtils.verifySignature(secret: secret, payload: payload, signature: "badsignature"))
    }

    func testVerifySignature_wrongSecretReturnsFalse() {
        let wrongSig = WebhookUtils.computeSignature(secret: "wrong-secret", payload: payload)
        XCTAssertFalse(WebhookUtils.verifySignature(secret: secret, payload: payload, signature: wrongSig))
    }
}
