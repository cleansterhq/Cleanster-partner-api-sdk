import XCTest
@testable import Cleanster

final class PaymentMethodsTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    func testGetSetupIntentDetails_sendsGET() async throws {
        mock.succeed(with: ["clientSecret": "seti_xxx"])
        _ = try await client.paymentMethods.getSetupIntentDetails()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetSetupIntentDetails_correctPath() async throws {
        mock.succeed(with: ["clientSecret": "seti_xxx"])
        _ = try await client.paymentMethods.getSetupIntentDetails()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods/setup-intent-details") == true)
    }

    func testGetPayPalClientToken_sendsGET() async throws {
        mock.succeed(with: ["clientToken": "paypal_xxx"])
        _ = try await client.paymentMethods.getPayPalClientToken()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetPayPalClientToken_correctPath() async throws {
        mock.succeed(with: ["clientToken": "paypal_xxx"])
        _ = try await client.paymentMethods.getPayPalClientToken()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods/paypal-client-token") == true)
    }

    func testAddPaymentMethod_sendsPOST() async throws {
        mock.succeed(with: ["id": 55, "type": "card"])
        _ = try await client.paymentMethods.addPaymentMethod("pm_1OjvDE2eZvKYlo2C")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAddPaymentMethod_correctPath() async throws {
        mock.succeed(with: ["id": 55, "type": "card"])
        _ = try await client.paymentMethods.addPaymentMethod("pm_xxx")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods") == true)
    }

    func testAddPaymentMethod_encodesToken() async throws {
        mock.succeed(with: ["id": 55])
        _ = try await client.paymentMethods.addPaymentMethod("pm_abc123")
        XCTAssertEqual(mock.capturedBody?["paymentMethodId"] as? String, "pm_abc123")
    }

    func testAddPaymentMethod_decodesId() async throws {
        mock.succeed(with: ["id": 55, "type": "card", "brand": "visa", "lastFour": "4242"])
        let resp = try await client.paymentMethods.addPaymentMethod("pm_xxx")
        XCTAssertEqual(resp.data?.id, 55)
    }

    func testAddPaymentMethod_decodesType() async throws {
        mock.succeed(with: ["id": 55, "type": "paypal"])
        let resp = try await client.paymentMethods.addPaymentMethod("nonce_xxx")
        XCTAssertEqual(resp.data?.type, "paypal")
    }

    func testGetPaymentMethods_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.paymentMethods.getPaymentMethods()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetPaymentMethods_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.paymentMethods.getPaymentMethods()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods") == true)
    }

    func testSetDefaultPaymentMethod_sendsPUT() async throws {
        mock.succeedEmpty()
        _ = try await client.paymentMethods.setDefaultPaymentMethod(55)
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testSetDefaultPaymentMethod_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.paymentMethods.setDefaultPaymentMethod(55)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods/55/default") == true)
    }

    func testDeletePaymentMethod_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.paymentMethods.deletePaymentMethod(55)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testDeletePaymentMethod_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.paymentMethods.deletePaymentMethod(55)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/payment-methods/55") == true)
    }
}
