import XCTest
@testable import Cleanster

final class WebhooksTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    func testListWebhooks_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.webhooks.listWebhooks()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testListWebhooks_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.webhooks.listWebhooks()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/webhooks") == true)
    }

    func testCreateWebhook_sendsPOST() async throws {
        mock.succeed(with: ["id": 1, "url": "https://example.com", "event": "booking.completed"])
        _ = try await client.webhooks.createWebhook(url: "https://example.com", event: "booking.completed")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCreateWebhook_correctPath() async throws {
        mock.succeed(with: ["id": 1, "url": "https://example.com", "event": "booking.completed"])
        _ = try await client.webhooks.createWebhook(url: "https://example.com", event: "booking.completed")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/webhooks") == true)
    }

    func testCreateWebhook_encodesUrl() async throws {
        mock.succeed(with: ["id": 1, "url": "https://myapp.com/hook", "event": "booking.created"])
        _ = try await client.webhooks.createWebhook(url: "https://myapp.com/hook", event: "booking.created")
        XCTAssertEqual(mock.capturedBody?["url"] as? String, "https://myapp.com/hook")
    }

    func testCreateWebhook_encodesEvent() async throws {
        mock.succeed(with: ["id": 1, "url": "https://x.com", "event": "booking.cancelled"])
        _ = try await client.webhooks.createWebhook(url: "https://x.com", event: "booking.cancelled")
        XCTAssertEqual(mock.capturedBody?["event"] as? String, "booking.cancelled")
    }

    func testCreateWebhook_decodesId() async throws {
        mock.succeed(with: ["id": 99, "url": "https://x.com", "event": "booking.completed"])
        let resp = try await client.webhooks.createWebhook(url: "https://x.com", event: "booking.completed")
        XCTAssertEqual(resp.data?.id, 99)
    }

    func testUpdateWebhook_sendsPUT() async throws {
        mock.succeed(with: ["id": 1, "url": "https://new.com", "event": "booking.started"])
        _ = try await client.webhooks.updateWebhook(1, url: "https://new.com", event: "booking.started")
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testUpdateWebhook_correctPath() async throws {
        mock.succeed(with: ["id": 1, "url": "https://new.com", "event": "booking.started"])
        _ = try await client.webhooks.updateWebhook(1, url: "https://new.com", event: "booking.started")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/webhooks/1") == true)
    }

    func testDeleteWebhook_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.webhooks.deleteWebhook(1)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testDeleteWebhook_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.webhooks.deleteWebhook(1)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/webhooks/1") == true)
    }
}
